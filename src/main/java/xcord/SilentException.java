package xcord;

public class SilentException
extends Exception {
    public SilentException(String string) {
        super(string, null, true, false);
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

