package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {
   private static final Object NO_INIT = new Object();
   private volatile T object = (T)NO_INIT;

   @Override
   public T get() throws ConcurrentException {
      Object var1 = this.object;
      if (var1 == NO_INIT) {
         synchronized (this) {
            var1 = this.object;
            if (var1 == NO_INIT) {
               this.object = (T)(var1 = this.initialize());
            }
         }
      }

      return (T)var1;
   }

   protected abstract T initialize() throws ConcurrentException;
}
