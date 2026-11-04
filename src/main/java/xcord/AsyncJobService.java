package xcord;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.md_5.bungee.BungeeCord;
import xcord.AsyncJob;
import xcord.XCord;

public class AsyncJobService
extends Thread {
    private final Queue<AsyncJob> queue = new ConcurrentLinkedQueue<AsyncJob>();
    private final ExecutorService executor = Executors.newFixedThreadPool(XCord.getConfig().getLoginThreadPoolSize());

    public AsyncJobService() {
        super("async-consumer-service");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            long l = System.currentTimeMillis();
            try {
                Iterator iterator = this.queue.iterator();
                while (iterator.hasNext()) {
                    AsyncJob asyncJob = (AsyncJob)iterator.next();
                    if (asyncJob == null || !asyncJob.isReady()) continue;
                    iterator.remove();
                    this.executor.submit(asyncJob);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            long l2 = System.currentTimeMillis() - l;
            int n = XCord.getConfig().getLoginTickLengthMs();
            if (l2 < (long)n) {
                try {
                    Thread.sleep((long)n - l2);
                }
                catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                continue;
            }
            if (l2 <= (long)(n * 6)) continue;
            BungeeCord.getInstance().getLogger().warning("[!] AsyncJobConsumerService is currently overloaded: " + this.queue.size() + " Time (MS): " + l2);
        }
    }

    public void submit(AsyncJob asyncJob) {
        this.queue.add(asyncJob);
    }
}

