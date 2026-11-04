package xcord.service;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class AsyncJobService extends Thread {
    private final Queue<AsyncJob> queue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(XCord.getConfig().getLoginThreadPoolSize());

    public AsyncJobService() {
        super("async-consumer-service");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            long startTime = System.currentTimeMillis();
            try {
                Iterator<AsyncJob> iterator = this.queue.iterator();
                while (iterator.hasNext()) {
                    AsyncJob asyncJob = iterator.next();
                    if (asyncJob == null || !asyncJob.isReady()) {
                        continue;
                    }
                    iterator.remove();
                    this.executor.submit(asyncJob);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            int tickLength = XCord.getConfig().getLoginTickLengthMs();
            if (elapsedTime < tickLength) {
                try {
                    Thread.sleep(tickLength - elapsedTime);
                }
                catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                continue;
            }
            if (elapsedTime <= (long) (tickLength * 6)) {
                continue;
            }
            BungeeCord.getInstance().getLogger().warning("[!] AsyncJobConsumerService is currently overloaded: " + this.queue.size() + " Time (MS): " + elapsedTime);
        }
    }

    public void submit(AsyncJob asyncJob) {
        this.queue.add(asyncJob);
    }
}
