package xcord.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import xcord.protocol.VarInts;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final int threshold;
    private final BungeeZlib zlib;

    public CompressionEncoder(int threshold, BungeeZlib zlib) {
        this.threshold = threshold;
        this.zlib = zlib;
    }

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws Exception {
        int length = input.readableBytes();
        if (length < this.threshold) {
            VarInts.writeVarInt(output, 0);
            output.writeBytes(input);
            return;
        }
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
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        this.zlib.free();
    }
}
