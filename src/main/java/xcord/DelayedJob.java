package xcord;

import xcord.AsyncJob;

public class DelayedJob
implements AsyncJob {
    private long executeAt;
    private Runnable task;

    public DelayedJob(long l, Runnable runnable) {
        this.executeAt = l;
        this.task = runnable;
    }

    @Override
    public void run() {
        try {
            this.task.run();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isReady() {
        return System.currentTimeMillis() > this.executeAt;
    }
}

