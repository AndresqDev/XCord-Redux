package io.netty.channel.pool;

import io.netty.channel.Channel;

public abstract class AbstractChannelPoolHandler implements ChannelPoolHandler {
   @Override
   public void channelAcquired(Channel var1) throws Exception {
   }

   @Override
   public void channelReleased(Channel var1) throws Exception {
   }
}
