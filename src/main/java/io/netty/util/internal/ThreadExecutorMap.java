package io.netty.util.internal;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadExecutorMap {
   private static final FastThreadLocal<EventExecutor> mappings = new FastThreadLocal<>();

   private ThreadExecutorMap() {
   }

   public static EventExecutor currentExecutor() {
      return mappings.get();
   }

   private static void setCurrentEventExecutor(EventExecutor var0) {
      mappings.set(var0);
   }

   public static Executor apply(final Executor var0, final EventExecutor var1) {
      ObjectUtil.checkNotNull(var0, "executor");
      ObjectUtil.checkNotNull(var1, "eventExecutor");
      return new Executor() {
         @Override
         public void execute(Runnable var1x) {
            var0.execute(ThreadExecutorMap.apply(var1x, var1));
         }
      };
   }

   public static Runnable apply(final Runnable var0, final EventExecutor var1) {
      ObjectUtil.checkNotNull(var0, "command");
      ObjectUtil.checkNotNull(var1, "eventExecutor");
      return new Runnable() {
         @Override
         public void run() {
            ThreadExecutorMap.setCurrentEventExecutor(var1);

            try {
               var0.run();
            } finally {
               ThreadExecutorMap.setCurrentEventExecutor(null);
            }
         }
      };
   }

   public static ThreadFactory apply(final ThreadFactory var0, final EventExecutor var1) {
      ObjectUtil.checkNotNull(var0, "command");
      ObjectUtil.checkNotNull(var1, "eventExecutor");
      return new ThreadFactory() {
         @Override
         public Thread newThread(Runnable var1x) {
            return var0.newThread(ThreadExecutorMap.apply(var1x, var1));
         }
      };
   }
}
