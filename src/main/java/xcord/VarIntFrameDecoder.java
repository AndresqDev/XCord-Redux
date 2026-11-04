package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;
import xcord.VarIntByteProcessor;
import xcord.XCord;

public class VarIntFrameDecoder
extends ByteToMessageDecoder {
    private static final MutedCorruptedFrameException a_MutedCorruptedFrameException = new MutedCorruptedFrameException("Frame broken length");
    private static final MutedCorruptedFrameException b = new MutedCorruptedFrameException("Frame too big");
    private static final MutedCorruptedFrameException c = new MutedCorruptedFrameException("Potential Frame exploit");
    private int invalidFrames = 0;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (!channelHandlerContext.channel().isActive()) {
            byteBuf.clear();
            return;
        }
        VarIntByteProcessor varIntByteProcessor = new VarIntByteProcessor();
        int n = byteBuf.forEachByte(varIntByteProcessor);
        if (n == -1) {
            if (varIntByteProcessor.getState() == VarIntByteProcessor.State.ZERO) {
                byteBuf.clear();
            }
            if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                byteBuf.clear();
                throw c;
            }
            return;
        }
        if (varIntByteProcessor.getState() == VarIntByteProcessor.State.VALUE) {
            int n2 = varIntByteProcessor.getValue();
            int n3 = varIntByteProcessor.getBytesRead();
            if (n2 < 0) {
                byteBuf.clear();
                throw a_MutedCorruptedFrameException;
            }
            if (n2 == 0) {
                if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                    byteBuf.clear();
                    throw c;
                }
                byteBuf.readerIndex(n + 1);
            } else {
                int n4 = n3 + n2;
                if (byteBuf.isReadable(n4)) {
                    list.add(byteBuf.retainedSlice(n + 1, n2));
                    byteBuf.skipBytes(n4);
                    this.invalidFrames = 0;
                }
            }
        } else {
            if (varIntByteProcessor.getState() == VarIntByteProcessor.State.TOO_LONG) {
                byteBuf.clear();
                throw b;
            }
            if (varIntByteProcessor.getState() == VarIntByteProcessor.State.ZERO) {
                byteBuf.readerIndex(n);
                if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                    byteBuf.clear();
                    throw c;
                }
            }
        }
    }
}

