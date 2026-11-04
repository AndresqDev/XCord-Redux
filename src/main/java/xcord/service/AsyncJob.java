package xcord.service;

public interface AsyncJob extends Runnable {
    default boolean isReady() {
        return true;
    }
}
