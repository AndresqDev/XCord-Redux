package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.jni.zlib.JavaZlib;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.PacketRegistry;

public class PacketTransformer<T extends DefinedPacket>
extends PacketRegistry<T> {
    public PacketTransformer(Class<T> clazz, ProtocolConstants.Direction direction, String string) {
        super(clazz, direction, string);
    }

    public ByteBuf transform(ByteBuf byteBuf, Class<T> clazz, ProtocolConstants.Direction direction, int n, int n2) {
        return byteBuf;
    }

    @Override
    protected void register(Class<T> clazz, ProtocolConstants.Direction direction, int n, int n2, byte[] byArray) throws IllegalAccessException, InstantiationException {
        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(byArray.length);
        byteBuf.writeBytes(byArray);
        byteBuf = this.transform(byteBuf, clazz, direction, n, n2);
        ByteBuf byteBuf2 = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        if (BungeeCord.getInstance().getConfig().getCompressionThreshold() > 0 && n >= 47 && n2 > 47) {
            JavaZlib javaZlib = new JavaZlib();
            javaZlib.init(true, 6);
            int n3 = byteBuf.readableBytes();
            if (n3 < BungeeCord.getInstance().getConfig().getCompressionThreshold()) {
                DefinedPacket.writeVarInt(0, byteBuf2);
                byteBuf2.writeBytes(byteBuf);
            } else {
                DefinedPacket.writeVarInt(n3, byteBuf2);
                try {
                    javaZlib.deflate(byteBuf, byteBuf2);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            javaZlib.free();
            byArray = new byte[byteBuf2.readableBytes()];
            byteBuf2.readBytes(byArray);
        } else {
            byArray = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(byArray);
        }
        byteBuf.release();
        byteBuf2.release();
        super.register(clazz, direction, n, n2, byArray);
    }
}

