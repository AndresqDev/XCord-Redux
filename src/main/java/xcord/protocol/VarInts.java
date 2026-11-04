package xcord.protocol;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;

public final class VarInts {
    private static final MutedCorruptedFrameException BAD_VARINT = new MutedCorruptedFrameException("Bad VarInt decoded");
    private static final boolean packetDecodeLogging = Boolean.getBoolean("waterfall.packet-decode-logging");
    private static final int[] VARINT_SIZES = new int[33];

    private VarInts() {
    }

    public static int readVarInt(ByteBuf buffer) {
        int value = readVarIntRaw(buffer);
        if (value == Integer.MIN_VALUE) {
            throw packetDecodeLogging ? new MutedCorruptedFrameException("Bad VarInt decoded") : BAD_VARINT;
        }
        return value;
    }

    public static int readVarIntRaw(ByteBuf buffer) {
        int value = 0;
        int maxBytes = Math.min(5, buffer.readableBytes());
        for (int index = 0; index < maxBytes; index++) {
            byte currentByte = buffer.readByte();
            value |= (currentByte & 127) << (index * 7);
            if ((currentByte & 128) != 128) {
                return value;
            }
        }
        return Integer.MIN_VALUE;
    }

    public static int varIntSize(int value) {
        return VARINT_SIZES[Integer.numberOfLeadingZeros(value)];
    }

    public static void writeVarInt(ByteBuf buffer, int value) {
        if ((value & -128) == 0) {
            buffer.writeByte(value);
        } else if ((value & -16384) == 0) {
            int encoded = ((value & 127) | 128) << 8 | (value >>> 7);
            buffer.writeShort(encoded);
        } else {
            writeVarIntFull(buffer, value);
        }
    }

    private static void writeVarIntFull(ByteBuf buffer, int value) {
        if ((value & -128) == 0) {
            buffer.writeByte(value);
        } else if ((value & -16384) == 0) {
            int encoded = ((value & 127) | 128) << 8 | (value >>> 7);
            buffer.writeShort(encoded);
        } else if ((value & -2097152) == 0) {
            int encoded = ((value & 127) | 128) << 16
                    | (((value >>> 7) & 127) | 128) << 8
                    | (value >>> 14);
            buffer.writeMedium(encoded);
        } else if ((value & -268435456) == 0) {
            int encoded = ((value & 127) | 128) << 24
                    | (((value >>> 7) & 127) | 128) << 16
                    | (((value >>> 14) & 127) | 128) << 8
                    | (value >>> 21);
            buffer.writeInt(encoded);
        } else {
            int encoded = ((value & 127) | 128) << 24
                    | (((value >>> 7) & 127) | 128) << 16
                    | (((value >>> 14) & 127) | 128) << 8
                    | ((value >>> 21) & 127)
                    | 128;
            buffer.writeInt(encoded);
            buffer.writeByte(value >>> 28);
        }
    }

    public static void writeVarInt3(ByteBuf buffer, int value) {
        int encoded = ((value & 127) | 128) << 16
                | (((value >>> 7) & 127) | 128) << 8
                | (value >>> 14);
        buffer.writeMedium(encoded);
    }

    static {
        for (int index = 0; index <= 32; index++) {
            VARINT_SIZES[index] = (int) Math.ceil((31.0 - (double) (index - 1)) / 7.0);
        }
        VARINT_SIZES[32] = 1;
    }
}
