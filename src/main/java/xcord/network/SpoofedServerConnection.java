package xcord.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Kick;

public class SpoofedServerConnection extends ServerConnection {
    private static final BungeeServerInfo SPOOFED_LOBBY = new BungeeServerInfo("spoofedLobby", null, "spoofed", false);

    public SpoofedServerConnection(BungeeServerInfo serverInfo) {
        super(new ChannelWrapper() {
            @Override
            public void setProtocol(Protocol protocol) {
            }

            @Override
            public void setVersion(int protocolVersion) {
            }

            @Override
            public void addBefore(String baseName, String name, ChannelHandler handler) {
            }

            @Override
            public Channel getHandle() {
                return null;
            }

            @Override
            public void setCompressionThreshold(int threshold) {
            }

            @Override
            public void write(Object message) {
            }

            @Override
            public void writeBuffered(Object message) {
            }

            @Override
            public void markClosed() {
            }

            @Override
            public void close() {
            }

            @Override
            public void close(Object message) {
            }

            @Override
            public void flush() {
            }

            @Override
            public void forceClose() {
            }

            @Override
            public void delayedClose(Kick kick) {
            }
        }, serverInfo);
    }

    @Override
    public ChannelWrapper getCh() {
        return super.getCh();
    }

    public BungeeServerInfo getInfo_BungeeServerInfo() {
        return SPOOFED_LOBBY;
    }

    @Override
    public boolean isObsolete() {
        return super.isObsolete();
    }

    @Override
    public boolean isForgeServer() {
        return super.isForgeServer();
    }

    @Override
    public Queue<ServerConnection.KeepAliveData> getKeepAlives() {
        return super.getKeepAlives();
    }

    @Override
    public void setObsolete(boolean obsolete) {
        super.setObsolete(obsolete);
    }

    @Override
    public void sendData(String channel, byte[] data) {
        super.sendData(channel, data);
    }

    @Override
    public void disconnect(String reason) {
    }

    @Override
    public void disconnect(BaseComponent... reason) {
    }

    @Override
    public void forceDisconnect() {
    }

    @Override
    public void disconnect(BaseComponent reason) {
    }

    @Override
    public InetSocketAddress getAddress() {
        return super.getAddress();
    }

    @Override
    public SocketAddress getSocketAddress() {
        return super.getSocketAddress();
    }

    @Override
    public boolean isConnected() {
        return super.isConnected();
    }

    @Override
    public Connection.Unsafe unsafe() {
        return super.unsafe();
    }
}
