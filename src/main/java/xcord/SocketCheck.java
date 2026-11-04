package xcord;

import io.netty.channel.Channel;

public interface SocketCheck<T extends Channel> {
    public void close(T var1);

    public boolean check(T var1);

    public int getPing(T var1);

    public boolean isSuspiciousMss(T var1);
}

