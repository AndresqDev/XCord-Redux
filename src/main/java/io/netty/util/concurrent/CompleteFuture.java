package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public abstract class CompleteFuture<V> extends AbstractFuture<V> {
   private final EventExecutor executor;

   protected CompleteFuture(EventExecutor var1) {
      this.executor = var1;
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   @Override
   public Future<V> addListener(GenericFutureListener<? extends Future<? super V>> var1) {
      DefaultPromise.notifyListener(this.executor(), this, ObjectUtil.checkNotNull(var1, "listener"));
      return this;
   }

   @Override
   public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      for (GenericFutureListener var5 : ObjectUtil.checkNotNull(var1, "listeners")) {
         if (var5 == null) {
            break;
         }

         DefaultPromise.notifyListener(this.executor(), this, var5);
      }

      return this;
   }

   @Override
   public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1) {
      return this;
   }

   @Override
   public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      return this;
   }

   @Override
   public Future<V> await() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this;
      }
   }

   @Override
   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return true;
      }
   }

   @Override
   public Future<V> sync() throws InterruptedException {
      return this;
   }

   @Override
   public Future<V> syncUninterruptibly() {
      return this;
   }

   @Override
   public boolean await(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return true;
      }
   }

   @Override
   public Future<V> awaitUninterruptibly() {
      return this;
   }

   @Override
   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      return true;
   }

   @Override
   public boolean awaitUninterruptibly(long var1) {
      return true;
   }

   @Override
   public boolean isDone() {
      return true;
   }

   @Override
   public boolean isCancellable() {
      return false;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public boolean cancel(boolean var1) {
      return false;
   }
}
