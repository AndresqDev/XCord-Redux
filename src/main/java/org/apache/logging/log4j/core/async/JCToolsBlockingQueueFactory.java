package org.apache.logging.log4j.core.async;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.jctools.queues.MpscArrayQueue;

@Plugin(
   name = "JCToolsBlockingQueue",
   category = "Core",
   elementType = "BlockingQueueFactory"
)
public class JCToolsBlockingQueueFactory<E> implements BlockingQueueFactory<E> {
   private final JCToolsBlockingQueueFactory.WaitStrategy waitStrategy;

   private JCToolsBlockingQueueFactory(JCToolsBlockingQueueFactory.WaitStrategy var1) {
      this.waitStrategy = var1;
   }

   @Override
   public BlockingQueue<E> create(int var1) {
      return new JCToolsBlockingQueueFactory.MpscBlockingQueue<>(var1, this.waitStrategy);
   }

   @PluginFactory
   public static <E> JCToolsBlockingQueueFactory<E> createFactory(
      @PluginAttribute(value = "WaitStrategy",defaultString = "PARK") JCToolsBlockingQueueFactory.WaitStrategy var0
   ) {
      return new JCToolsBlockingQueueFactory<>(var0);
   }

   private interface Idle {
      int idle(int var1);
   }

   private static final class MpscBlockingQueue<E> extends MpscArrayQueue<E> implements BlockingQueue<E> {
      private final JCToolsBlockingQueueFactory.WaitStrategy waitStrategy;

      MpscBlockingQueue(int var1, JCToolsBlockingQueueFactory.WaitStrategy var2) {
         super(var1);
         this.waitStrategy = var2;
      }

      @Override
      public int drainTo(Collection<? super E> var1) {
         return this.drainTo(var1, this.capacity());
      }

      @Override
      public int drainTo(Collection<? super E> var1, int var2) {
         return this.drain(var1x -> var1.add(var1x), var2);
      }

      @Override
      public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
         int var5 = 0;
         long var6 = System.nanoTime() + var4.toNanos(var2);

         while (!this.offer((E)var1)) {
            if (System.nanoTime() - var6 > 0L) {
               return false;
            }

            var5 = this.waitStrategy.idle(var5);
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
         }

         return true;
      }

      @Override
      public E poll(long var1, TimeUnit var3) throws InterruptedException {
         int var4 = 0;
         long var5 = System.nanoTime() + var3.toNanos(var1);

         do {
            Object var7 = this.poll();
            if (var7 != null) {
               return (E)var7;
            }

            if (System.nanoTime() - var5 > 0L) {
               return null;
            }

            var4 = this.waitStrategy.idle(var4);
         } while (!Thread.interrupted());

         throw new InterruptedException();
      }

      @Override
      public void put(E var1) throws InterruptedException {
         int var2 = 0;

         while (!this.offer((E)var1)) {
            var2 = this.waitStrategy.idle(var2);
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
         }
      }

      @Override
      public boolean offer(E var1) {
         return this.offerIfBelowThreshold(var1, this.capacity() - 32);
      }

      @Override
      public int remainingCapacity() {
         return this.capacity() - this.size();
      }

      @Override
      public E take() throws InterruptedException {
         int var1 = 100;

         do {
            Object var2 = this.relaxedPoll();
            if (var2 != null) {
               return (E)var2;
            }

            var1 = this.waitStrategy.idle(var1);
         } while (!Thread.interrupted());

         throw new InterruptedException();
      }
   }

   public static enum WaitStrategy {
      SPIN(var0 -> var0 + 1),
      YIELD(var0 -> {
         Thread.yield();
         return var0 + 1;
      }),
      PARK(var0 -> {
         LockSupport.parkNanos(1L);
         return var0 + 1;
      }),
      PROGRESSIVE(var0 -> {
         if (var0 > 200) {
            LockSupport.parkNanos(1L);
         } else if (var0 > 100) {
            Thread.yield();
         }

         return var0 + 1;
      });

      private final JCToolsBlockingQueueFactory.Idle idle;

      private int idle(int var1) {
         return this.idle.idle(var1);
      }

      private WaitStrategy(JCToolsBlockingQueueFactory.Idle var3) {
         this.idle = var3;
      }
   }
}
