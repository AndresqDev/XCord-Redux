package xcord.protocol;

import io.netty.util.ByteProcessor;

class VarIntByteProcessor implements ByteProcessor {
    private int value;
    private int bytesRead;
    private State state = State.INCOMPLETE;

    VarIntByteProcessor() {
    }

    @Override
    public boolean process(byte currentByte) {
        if (currentByte == 0 && this.bytesRead == 0) {
            this.state = State.ZERO;
            return true;
        }
        if (this.state == State.ZERO) {
            return false;
        }
        this.value |= (currentByte & 0x7F) << (this.bytesRead * 7);
        this.bytesRead++;
        if (this.bytesRead > 3) {
            this.state = State.TOO_LONG;
            return false;
        }
        if ((currentByte & 0x80) != 0) {
            return true;
        }
        this.state = State.VALUE;
        return false;
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

    public enum State {
        VALUE,
        INCOMPLETE,
        TOO_LONG,
        ZERO
    }
}
