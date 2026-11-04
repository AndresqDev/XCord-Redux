package xcord.util;

public class SilentException extends Exception {
    public SilentException(String message) {
        super(message, null, true, false);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
