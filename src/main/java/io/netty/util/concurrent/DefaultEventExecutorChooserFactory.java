package io.netty.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultEventExecutorChooserFactory implements EventExecutorChooserFactory {
   public static final DefaultEventExecutorChooserFactory INSTANCE = new DefaultEventExecutorChooserFactory();

   private DefaultEventExecutorChooserFactory() {
   }

   @Override
   public EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] var1) {
      return (EventExecutorChooserFactory.EventExecutorChooser)(isPowerOfTwo(var1.length)
         ? new DefaultEventExecutorChooserFactory.PowerOfTwoEventExecutorChooser(var1)
         : new DefaultEventExecutorChooserFactory.GenericEventExecutorChooser(var1));
   }

   private static boolean isPowerOfTwo(int var0) {
      return (var0 & -var0) == var0;
   }

   private static final class GenericEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {
      private final AtomicLong idx = new AtomicLong();
      private final EventExecutor[] executors;

      GenericEventExecutorChooser(EventExecutor[] var1) {
         this.executors = var1;
      }

      @Override
      public EventExecutor next() {
         return this.executors[(int)Math.abs(this.idx.getAndIncrement() % (long)this.executors.length)];
      }
   }

   private static final class PowerOfTwoEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {
      private final AtomicInteger idx = new AtomicInteger();
      private final EventExecutor[] executors;

      PowerOfTwoEventExecutorChooser(EventExecutor[] var1) {
         this.executors = var1;
      }

      @Override
      public EventExecutor next() {
         return this.executors[this.idx.getAndIncrement() & this.executors.length - 1];
      }
   }
}
