package xcord.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public class PacketRegistry<T extends DefinedPacket> {
    private final IntObjectMap<T> packets = new IntObjectHashMap<>(ProtocolConstants.SUPPORTED_VERSIONS.size() * 4);

    public PacketRegistry(Class<T> packetClass, Direction direction, String resourceName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(packetClass.getClassLoader().getResourceAsStream(resourceName))))) {
            String line;
            while ((line = reader.readLine()) != null && line.length() > 2) {
                String[] parts = line.split(",");
                int protocolVersion = Integer.parseInt(parts[0]);
                int maxVersion = Integer.parseInt(parts[1]);
                byte[] data = new byte[parts.length - 2];
                for (int index = 2; index < parts.length; index++) {
                    data[index - 2] = Byte.parseByte(parts[index]);
                }
                this.register(packetClass, direction, protocolVersion, maxVersion, data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void put(int protocolVersion, T packet) {
        this.packets.put(protocolVersion, packet);
    }

    protected void register(Class<T> packetClass, Direction direction, int protocolVersion, int maxVersion, byte[] data) throws IllegalAccessException, InstantiationException {
        ByteBuf buffer = Unpooled.copiedBuffer(data);
        T packet = packetClass.newInstance();
        try {
            packet.read(buffer, direction, protocolVersion);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            buffer.release();
        }
        this.put(protocolVersion, packet);
        for (int version : ProtocolConstants.SUPPORTED_VERSION_IDS) {
            if (version < protocolVersion) {
                continue;
            }
            if (version >= maxVersion) {
                break;
            }
            this.put(version, packet);
        }
    }

    public T get(int protocolVersion) {
        return this.packets.get(protocolVersion);
    }
}
