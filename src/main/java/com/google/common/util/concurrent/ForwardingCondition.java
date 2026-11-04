package com.google.common.util.concurrent;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@ElementTypesAreNonnullByDefault
abstract class ForwardingCondition implements Condition {
   abstract Condition delegate();

   @Override
   public void await() throws InterruptedException {
      this.delegate().await();
   }

   @Override
   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().await(var1, var3);
   }

   @Override
   public void awaitUninterruptibly() {
      this.delegate().awaitUninterruptibly();
   }

   @Override
   public long awaitNanos(long var1) throws InterruptedException {
      return this.delegate().awaitNanos(var1);
   }

   @Override
   public boolean awaitUntil(Date var1) throws InterruptedException {
      return this.delegate().awaitUntil(var1);
   }

   @Override
   public void signal() {
      this.delegate().signal();
   }

   @Override
   public void signalAll() {
      this.delegate().signalAll();
   }
}
