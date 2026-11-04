package io.netty.channel.unix;

import io.netty.channel.socket.DuplexChannel;

public interface DomainSocketChannel extends DuplexChannel, UnixChannel {
   DomainSocketAddress remoteAddress();

   DomainSocketAddress localAddress();

   DomainSocketChannelConfig config();
}
