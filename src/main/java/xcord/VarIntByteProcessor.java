package xcord;

import io.netty.util.ByteProcessor;

class VarIntByteProcessor
implements ByteProcessor {
    private int value;
    private int bytesRead;
    private State state = State.INCOMPLETE;

    VarIntByteProcessor() {
    }

    @Override
    public boolean process(byte by) {
        if (by == 0 && this.bytesRead == 0) {
            this.state = State.ZERO;
            return true;
        }
        if (this.state == State.ZERO) {
            return false;
        }
        this.value |= (by & 0x7F) << this.bytesRead++ * 7;
        if (this.bytesRead > 3) {
            this.state = State.TOO_LONG;
            return false;
        }
        if ((by & 0x80) != 128) {
            this.state = State.VALUE;
            return false;
        }
        return true;
    }

    public int getValue() {
        return this.value;
    }

    public int getBytesRead() {
        return this.bytesRead;
    }

    public State getState() {
        return this.state;
    }

    public static enum State {
        VALUE,
        INCOMPLETE,
        TOO_LONG,
        ZERO;

    }
}

