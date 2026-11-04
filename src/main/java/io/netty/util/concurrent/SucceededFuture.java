package io.netty.util.concurrent;

public final class SucceededFuture<V> extends CompleteFuture<V> {
   private final V result;

   public SucceededFuture(EventExecutor var1, V var2) {
      super(var1);
      this.result = (V)var2;
   }

   @Override
   public Throwable cause() {
      return null;
   }

   @Override
   public boolean isSuccess() {
      return true;
   }

   @Override
   public V getNow() {
      return this.result;
   }
}
