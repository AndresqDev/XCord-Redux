package xcord;

public interface AsyncJob
extends Runnable {
    default public boolean isReady() {
        return true;
    }
}

