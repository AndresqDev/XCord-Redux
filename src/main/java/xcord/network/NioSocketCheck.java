package xcord.network;

import io.netty.channel.socket.nio.NioSocketChannel;

public class NioSocketCheck<T extends NioSocketChannel> implements SocketCheck<T> {
    @Override
    public void close(NioSocketChannel channel) {
        if (channel.isActive()) {
            channel.config().setSoLinger(0);
            channel.close();
        }
    }

    @Override
    public boolean check(T channel) {
        return false;
    }

    @Override
    public int getPing(T channel) {
        return -1;
    }

    @Override
    public boolean isSuspiciousMss(T channel) {
        return false;
    }
}
