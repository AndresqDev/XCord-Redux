package io.netty.channel;

import io.netty.util.internal.ThrowableUtil;
import java.nio.channels.ClosedChannelException;

final class StacklessClosedChannelException extends ClosedChannelException {
   private static final long serialVersionUID = -2214806025529435136L;

   private StacklessClosedChannelException() {
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }

   static StacklessClosedChannelException newInstance(Class<?> var0, String var1) {
      return ThrowableUtil.unknownStackTrace(new StacklessClosedChannelException(), var0, var1);
   }
}
