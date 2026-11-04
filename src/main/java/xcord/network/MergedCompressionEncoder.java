package xcord.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import xcord.protocol.VarInts;

public class MergedCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private int threshold;
    private final BungeeZlib zlib;

    public MergedCompressionEncoder(int threshold, BungeeZlib zlib) {
        this.threshold = threshold;
        this.zlib = zlib;
    }

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws Exception {
        int length = input.readableBytes();
        if (length < this.threshold) {
            VarInts.writeVarInt(output, length + 1);
            VarInts.writeVarInt(output, 0);
            output.writeBytes(input);
            return;
        }
        VarInts.writeVarInt3(output, 0);
        VarInts.writeVarInt(output, length);
        boolean releaseInput = false;
        if (this.zlib.requiresDirectMemory() && !input.hasMemoryAddress()) {
            ByteBuf directBuffer = context.alloc().directBuffer(input.readableBytes());
            directBuffer.writeBytes(input);
            input = directBuffer;
            releaseInput = true;
        }
        try {
            this.zlib.deflate(input, output);
        } finally {
            if (releaseInput) {
                input.release();
            }
        }
        int compressedLength = output.readableBytes();
        int writerIndex = output.writerIndex();
        output.writerIndex(0);
        VarInts.writeVarInt3(output, compressedLength - 3);
        output.writerIndex(writerIndex);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        this.zlib.free();
    }
}
