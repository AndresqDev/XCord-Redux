package xcord.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;
import xcord.protocol.VarInts;

public class CompressionDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final int threshold;
    private final BungeeZlib zlib;

    public CompressionDecoder(int threshold, BungeeZlib zlib) {
        this.threshold = threshold;
        this.zlib = zlib;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> out) throws Exception {
        int uncompressedLength = VarInts.readVarInt(input);
        if (uncompressedLength == 0) {
            out.add(input.retainedSlice());
            return;
        }
        if (uncompressedLength > 2097152) {
            throw new MutedCorruptedFrameException("Packet exceeds 16MB in size: " + uncompressedLength);
        }
        if (uncompressedLength < this.threshold) {
            throw new MutedCorruptedFrameException("Packet smaller then threshold " + uncompressedLength + " / " + this.threshold);
        }
        boolean directMemory = this.zlib.requiresDirectMemory();
        boolean releaseInput = false;
        ByteBuf source = input;
        if (directMemory && !source.hasMemoryAddress()) {
            ByteBuf directBuffer = context.alloc().directBuffer(source.readableBytes());
            directBuffer.writeBytes(source);
            source = directBuffer;
            releaseInput = true;
        }
        ByteBuf decompressed = directMemory
                ? context.alloc().directBuffer(uncompressedLength)
                : context.alloc().heapBuffer(uncompressedLength);
        try {
            this.zlib.inflate(source, decompressed, uncompressedLength);
            out.add(decompressed);
        } catch (Exception exception) {
            decompressed.release();
            throw exception;
        } finally {
            if (releaseInput) {
                source.release();
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        this.zlib.free();
    }
}
