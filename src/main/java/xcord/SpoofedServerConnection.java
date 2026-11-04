package xcord;

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

public class SpoofedServerConnection
extends ServerConnection {
    private static final BungeeServerInfo SPOOFED_LOBBY = new BungeeServerInfo("spoofedLobby", null, "spoofed", false);

    public SpoofedServerConnection(BungeeServerInfo bungeeServerInfo) {
        super(new ChannelWrapper(){

            @Override
            public void setProtocol(Protocol protocol) {
            }

            @Override
            public void setVersion(int n) {
            }

            @Override
            public void addBefore(String string, String string2, ChannelHandler channelHandler) {
            }

            @Override
            public Channel getHandle() {
                return null;
            }

            @Override
            public void setCompressionThreshold(int n) {
            }

            @Override
            public void write(Object object) {
            }

            @Override
            public void writeBuffered(Object object) {
            }

            @Override
            public void markClosed() {
            }

            @Override
            public void close() {
            }

            @Override
            public void close(Object object) {
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
        }, bungeeServerInfo);
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
    public void setObsolete(boolean bl) {
        super.setObsolete(bl);
    }

    @Override
    public void sendData(String string, byte[] byArray) {
        super.sendData(string, byArray);
    }

    @Override
    public void disconnect(String string) {
    }

    @Override
    public void disconnect(BaseComponent ... baseComponentArray) {
    }

    @Override
    public void forceDisconnect() {
    }

    @Override
    public void disconnect(BaseComponent baseComponent) {
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

