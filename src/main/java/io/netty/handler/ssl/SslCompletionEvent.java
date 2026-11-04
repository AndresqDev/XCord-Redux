package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;

public abstract class SslCompletionEvent {
   private final Throwable cause;

   SslCompletionEvent() {
      this.cause = null;
   }

   SslCompletionEvent(Throwable var1) {
      this.cause = ObjectUtil.checkNotNull(var1, "cause");
   }

   public final boolean isSuccess() {
      return this.cause == null;
   }

   public final Throwable cause() {
      return this.cause;
   }

   @Override
   public String toString() {
      Throwable var1 = this.cause();
      return var1 == null ? this.getClass().getSimpleName() + "(SUCCESS)" : this.getClass().getSimpleName() + '(' + var1 + ')';
   }
}
