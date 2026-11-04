package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

final class FailedChannelFuture extends CompleteChannelFuture {
   private final Throwable cause;

   FailedChannelFuture(Channel var1, EventExecutor var2, Throwable var3) {
      super(var1, var2);
      this.cause = ObjectUtil.checkNotNull(var3, "cause");
   }

   @Override
   public Throwable cause() {
      return this.cause;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   @Override
   public ChannelFuture sync() {
      PlatformDependent.throwException(this.cause);
      return this;
   }

   @Override
   public ChannelFuture syncUninterruptibly() {
      PlatformDependent.throwException(this.cause);
      return this;
   }
}
