package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicSafeInitializer<T> implements ConcurrentInitializer<T> {
   private final AtomicReference<AtomicSafeInitializer<T>> factory = new AtomicReference<>();
   private final AtomicReference<T> reference = new AtomicReference<>();

   @Override
   public final T get() throws ConcurrentException {
      Object var1;
      while ((var1 = this.reference.get()) == null) {
         if (this.factory.compareAndSet(null, this)) {
            this.reference.set(this.initialize());
         }
      }

      return (T)var1;
   }

   protected abstract T initialize() throws ConcurrentException;
}
