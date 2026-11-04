package xcord.blacklist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class IPSetWriter extends Thread {
    private final File tempFile = new File(UUID.randomUUID().toString());
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger pending = new AtomicInteger();
    private final String tableName;
    private final int intervalMs;
    private final String addPrefix;
    private final String ipsetName;

    public IPSetWriter(int intervalMs, String ipsetName, String tableName) {
        this.intervalMs = intervalMs;
        this.tableName = tableName;
        this.ipsetName = ipsetName;
        this.addPrefix = "add " + ipsetName + " ";
        this.start();
    }

    public void addAddress(String address) {
        this.queue.add(address);
        this.pending.incrementAndGet();
    }

    @Override
    public void run() {
        while (true) {
            long sleepTime = this.intervalMs;

            try {
                if (!this.queue.isEmpty()) {
                    long startedAt = System.currentTimeMillis();
                    this.flushQueue();
                    long elapsed = System.currentTimeMillis() - startedAt;
                    if (elapsed > this.intervalMs - 10L) {
                        sleepTime = 10L;
                    }
                    else {
                        sleepTime = this.intervalMs - elapsed;
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
                return;
            }
        }
    }

    private void flushQueue() throws Exception {
        int pendingCount = this.pending.getAndSet(0);
        if (pendingCount < 8) {
            while (!this.queue.isEmpty()) {
                new ProcessBuilder("sudo", "ipset", "add", this.ipsetName, this.queue.poll()).redirectErrorStream(true).start();
            }
        }
        else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.tempFile), 500000)) {
                writer.write(this.tableName);
                writer.newLine();

                while (!this.queue.isEmpty()) {
                    writer.write(this.addPrefix + this.queue.poll());
                    writer.newLine();
                }
            }

            ProcessBuilder processBuilder = new ProcessBuilder("sudo", "ipset", "restore", "-!").redirectErrorStream(true);
            processBuilder.redirectInput(this.tempFile);
            processBuilder.start().waitFor();
            this.tempFile.delete();
        }
    }
}
