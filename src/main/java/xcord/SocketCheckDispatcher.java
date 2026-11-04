package xcord;

import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import xcord.EpollSocketCheck;
import xcord.NioSocketCheck;
import xcord.SocketCheck;

public class SocketCheckDispatcher<T extends Channel>
implements SocketCheck<T> {
    private final EpollSocketCheck<EpollSocketChannel> epollChecker = new EpollSocketCheck();
    private final NioSocketCheck<NioSocketChannel> nioChecker = new NioSocketCheck();

    @Override
    public void close(T t) {
        if (t instanceof EpollSocketChannel) {
            this.epollChecker.close((EpollSocketChannel)t);
        } else if (t instanceof NioSocketChannel) {
            this.nioChecker.close((NioSocketChannel)t);
        } else {
            t.close();
        }
    }

    @Override
    public boolean check(T t) {
        if (t instanceof EpollSocketChannel) {
            return this.epollChecker.check((EpollSocketChannel)t);
        }
        if (t instanceof NioSocketChannel) {
            return this.nioChecker.check((NioSocketChannel)t);
        }
        return false;
    }

    @Override
    public int getPing(T t) {
        if (t instanceof EpollSocketChannel) {
            return this.epollChecker.getPing((EpollSocketChannel)t);
        }
        return -1;
    }

    @Override
    public boolean isSuspiciousMss(T t) {
        if (t instanceof EpollSocketChannel) {
            return this.epollChecker.isSuspiciousMss((EpollSocketChannel)t);
        }
        return false;
    }
}

