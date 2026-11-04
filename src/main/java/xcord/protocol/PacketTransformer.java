package xcord.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.jni.zlib.JavaZlib;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PacketTransformer<T extends DefinedPacket> extends PacketRegistry<T> {
    public PacketTransformer(Class<T> packetClass, ProtocolConstants.Direction direction, String resourceName) {
        super(packetClass, direction, resourceName);
    }

    public ByteBuf transform(ByteBuf buffer, Class<T> packetClass, ProtocolConstants.Direction direction, int protocolVersion, int maxVersion) {
        return buffer;
    }

    @Override
    protected void register(Class<T> packetClass, ProtocolConstants.Direction direction, int protocolVersion, int maxVersion, byte[] data) throws IllegalAccessException, InstantiationException {
        ByteBuf transformed = UnpooledByteBufAllocator.DEFAULT.heapBuffer(data.length);
        transformed.writeBytes(data);
        transformed = this.transform(transformed, packetClass, direction, protocolVersion, maxVersion);
        ByteBuf compressed = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        if (BungeeCord.getInstance().getConfig().getCompressionThreshold() > 0 && protocolVersion >= 47 && maxVersion > 47) {
            JavaZlib zlib = new JavaZlib();
            zlib.init(true, 6);
            int length = transformed.readableBytes();
            if (length < BungeeCord.getInstance().getConfig().getCompressionThreshold()) {
                DefinedPacket.writeVarInt(0, compressed);
                compressed.writeBytes(transformed);
            } else {
                DefinedPacket.writeVarInt(length, compressed);
                try {
                    zlib.deflate(transformed, compressed);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            zlib.free();
            data = new byte[compressed.readableBytes()];
            compressed.readBytes(data);
        } else {
            data = new byte[transformed.readableBytes()];
            transformed.readBytes(data);
        }
        transformed.release();
        compressed.release();
        super.register(packetClass, direction, protocolVersion, maxVersion, data);
    }
}
