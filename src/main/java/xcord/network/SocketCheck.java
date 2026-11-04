package xcord.network;

import io.netty.channel.Channel;

public interface SocketCheck<T extends Channel> {
    public void close(T channel);

    public boolean check(T channel);

    public int getPing(T channel);

    public boolean isSuspiciousMss(T channel);
}
