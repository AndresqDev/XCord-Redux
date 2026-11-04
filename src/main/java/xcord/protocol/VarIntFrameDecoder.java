package xcord.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;
import xcord.XCord;

public class VarIntFrameDecoder extends ByteToMessageDecoder {
    private static final MutedCorruptedFrameException FRAME_BROKEN_LENGTH = new MutedCorruptedFrameException("Frame broken length");
    private static final MutedCorruptedFrameException FRAME_TOO_BIG = new MutedCorruptedFrameException("Frame too big");
    private static final MutedCorruptedFrameException POTENTIAL_FRAME_EXPLOIT = new MutedCorruptedFrameException("Potential Frame exploit");
    private int invalidFrames = 0;

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> out) {
        if (!context.channel().isActive()) {
            buffer.clear();
            return;
        }
        VarIntByteProcessor processor = new VarIntByteProcessor();
        int index = buffer.forEachByte(processor);
        if (index == -1) {
            if (processor.getState() == VarIntByteProcessor.State.ZERO) {
                buffer.clear();
            }
            if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                buffer.clear();
                throw POTENTIAL_FRAME_EXPLOIT;
            }
            return;
        }
        if (processor.getState() == VarIntByteProcessor.State.VALUE) {
            int length = processor.getValue();
            int lengthBytes = processor.getBytesRead();
            if (length < 0) {
                buffer.clear();
                throw FRAME_BROKEN_LENGTH;
            }
            if (length == 0) {
                if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                    buffer.clear();
                    throw POTENTIAL_FRAME_EXPLOIT;
                }
                buffer.readerIndex(index + 1);
            } else {
                int frameLength = lengthBytes + length;
                if (buffer.isReadable(frameLength)) {
                    out.add(buffer.retainedSlice(index + 1, length));
                    buffer.skipBytes(frameLength);
                    this.invalidFrames = 0;
                }
            }
        } else if (processor.getState() == VarIntByteProcessor.State.TOO_LONG) {
            buffer.clear();
            throw FRAME_TOO_BIG;
        } else if (processor.getState() == VarIntByteProcessor.State.ZERO) {
            buffer.readerIndex(index);
            if (++this.invalidFrames > XCord.getConfig().getAntiDdosImprobableBufferMaxViolations()) {
                buffer.clear();
                throw POTENTIAL_FRAME_EXPLOIT;
            }
        }
    }
}
