package xcord.service;

public class DelayedJob implements AsyncJob {
    private final long executeAt;
    private final Runnable task;

    public DelayedJob(long executeAt, Runnable task) {
        this.executeAt = executeAt;
        this.task = task;
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
