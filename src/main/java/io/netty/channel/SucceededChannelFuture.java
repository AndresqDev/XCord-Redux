package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

final class SucceededChannelFuture extends CompleteChannelFuture {
   SucceededChannelFuture(Channel var1, EventExecutor var2) {
      super(var1, var2);
   }

   @Override
   public Throwable cause() {
      return null;
   }

   @Override
   public boolean isSuccess() {
      return true;
   }
}
