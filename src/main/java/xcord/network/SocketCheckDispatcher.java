package xcord.network;

import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SocketCheckDispatcher<T extends Channel> implements SocketCheck<T> {
    private final EpollSocketCheck<EpollSocketChannel> epollChecker = new EpollSocketCheck<>();
    private final NioSocketCheck<NioSocketChannel> nioChecker = new NioSocketCheck<>();

    @Override
    public void close(T channel) {
        if (channel instanceof EpollSocketChannel) {
            this.epollChecker.close((EpollSocketChannel) channel);
        } else if (channel instanceof NioSocketChannel) {
            this.nioChecker.close((NioSocketChannel) channel);
        } else {
            channel.close();
        }
    }

    @Override
    public boolean check(T channel) {
        if (channel instanceof EpollSocketChannel) {
            return this.epollChecker.check((EpollSocketChannel) channel);
        }
        if (channel instanceof NioSocketChannel) {
            return this.nioChecker.check((NioSocketChannel) channel);
        }
        return false;
    }

    @Override
    public int getPing(T channel) {
        if (channel instanceof EpollSocketChannel) {
            return this.epollChecker.getPing((EpollSocketChannel) channel);
        }
        return -1;
    }

    @Override
    public boolean isSuspiciousMss(T channel) {
        if (channel instanceof EpollSocketChannel) {
            return this.epollChecker.isSuspiciousMss((EpollSocketChannel) channel);
        }
        return false;
    }
}
