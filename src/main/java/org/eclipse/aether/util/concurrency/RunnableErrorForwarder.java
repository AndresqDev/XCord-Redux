package org.eclipse.aether.util.concurrency;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public final class RunnableErrorForwarder {
   private final Thread thread = Thread.currentThread();
   private final AtomicInteger counter = new AtomicInteger();
   private final AtomicReference<Throwable> error = new AtomicReference<>();

   public Runnable wrap(final Runnable var1) {
      Objects.requireNonNull(var1, "runnable cannot be null");
      this.counter.incrementAndGet();
      return new Runnable() {
         @Override
         public void run() {
            try {
               var1.run();
            } catch (Error | RuntimeException var5) {
               RunnableErrorForwarder.this.error.compareAndSet(null, var5);
               throw var5;
            } finally {
               RunnableErrorForwarder.this.counter.decrementAndGet();
               LockSupport.unpark(RunnableErrorForwarder.this.thread);
            }
         }
      };
   }

   public void await() {
      this.awaitTerminationOfAllRunnables();
      Throwable var1 = this.error.get();
      if (var1 != null) {
         if (var1 instanceof RuntimeException) {
            throw (RuntimeException)var1;
         } else if (var1 instanceof ThreadDeath) {
            throw new IllegalStateException(var1);
         } else if (var1 instanceof Error) {
            throw (Error)var1;
         } else {
            throw new IllegalStateException(var1);
         }
      }
   }

   private void awaitTerminationOfAllRunnables() {
      if (!this.thread.equals(Thread.currentThread())) {
         throw new IllegalStateException("wrong caller thread, expected " + this.thread + " and not " + Thread.currentThread());
      } else {
         boolean var1 = false;

         while (this.counter.get() > 0) {
            LockSupport.park();
            if (Thread.interrupted()) {
               var1 = true;
            }
         }

         if (var1) {
            Thread.currentThread().interrupt();
         }
      }
   }
}
