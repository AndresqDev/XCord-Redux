package io.netty.handler.ssl;

public final class SniCompletionEvent extends SslCompletionEvent {
   private final String hostname;

   public SniCompletionEvent(String var1) {
      this.hostname = var1;
   }

   public SniCompletionEvent(String var1, Throwable var2) {
      super(var2);
      this.hostname = var1;
   }

   public SniCompletionEvent(Throwable var1) {
      this(null, var1);
   }

   public String hostname() {
      return this.hostname;
   }

   @Override
   public String toString() {
      Throwable var1 = this.cause();
      return var1 == null ? this.getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")" : this.getClass().getSimpleName() + '(' + var1 + ')';
   }
}
