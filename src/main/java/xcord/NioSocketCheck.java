package xcord;

import io.netty.channel.socket.nio.NioSocketChannel;
import xcord.SocketCheck;

public class NioSocketCheck<T extends NioSocketChannel>
implements SocketCheck<T> {
    @Override
    public void close(NioSocketChannel nioSocketChannel) {
        if (nioSocketChannel.isActive()) {
            nioSocketChannel.config().setSoLinger(0);
            nioSocketChannel.close();
        }
    }

    @Override
    public boolean check(T t) {
        return false;
    }

    @Override
    public int getPing(T t) {
        return -1;
    }

    @Override
    public boolean isSuspiciousMss(T t) {
        return false;
    }
}

