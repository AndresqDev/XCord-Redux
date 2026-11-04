package io.netty.util.internal.logging;

public final class FormattingTuple {
   private final String message;
   private final Throwable throwable;

   FormattingTuple(String var1, Throwable var2) {
      this.message = var1;
      this.throwable = var2;
   }

   public String getMessage() {
      return this.message;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }
}
