package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingQueue;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E> implements BlockingQueue<E> {
   protected ForwardingBlockingQueue() {
   }

   protected abstract BlockingQueue<E> delegate();

   @Override
   public int drainTo(Collection<? super E> var1, int var2) {
      return this.delegate().drainTo(var1, var2);
   }

   @Override
   public int drainTo(Collection<? super E> var1) {
      return this.delegate().drainTo(var1);
   }

   @Override
   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().offer((E)var1, var2, var4);
   }

   @Override
   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().poll(var1, var3);
   }

   @Override
   public void put(E var1) throws InterruptedException {
      this.delegate().put((E)var1);
   }

   @Override
   public int remainingCapacity() {
      return this.delegate().remainingCapacity();
   }

   @Override
   public E take() throws InterruptedException {
      return this.delegate().take();
   }
}
