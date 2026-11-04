package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor extends AbstractEventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ImmediateEventExecutor.class);
   public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
   private static final FastThreadLocal<Queue<Runnable>> DELAYED_RUNNABLES = new FastThreadLocal<Queue<Runnable>>() {
      protected Queue<Runnable> initialValue() throws Exception {
         return new ArrayDeque<>();
      }
   };
   private static final FastThreadLocal<Boolean> RUNNING = new FastThreadLocal<Boolean>() {
      protected Boolean initialValue() throws Exception {
         return false;
      }
   };
   private final Future<?> terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());

   private ImmediateEventExecutor() {
   }

   @Override
   public boolean inEventLoop() {
      return true;
   }

   @Override
   public boolean inEventLoop(Thread var1) {
      return true;
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
   }

   @Override
   public boolean isShuttingDown() {
      return false;
   }

   @Override
   public boolean isShutdown() {
      return false;
   }

   @Override
   public boolean isTerminated() {
      return false;
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) {
      return false;
   }

   @Override
   public void execute(Runnable var1) {
      ObjectUtil.checkNotNull(var1, "command");
      if (!RUNNING.get()) {
         RUNNING.set(true);

         try {
            var1.run();
         } catch (Throwable var15) {
            logger.info("Throwable caught while executing Runnable {}", var1, var15);
         } finally {
            Queue var6 = DELAYED_RUNNABLES.get();

            Runnable var7;
            while ((var7 = (Runnable)var6.poll()) != null) {
               try {
                  var7.run();
               } catch (Throwable var14) {
                  logger.info("Throwable caught while executing Runnable {}", var7, var14);
               }
            }

            RUNNING.set(false);
         }
      } else {
         DELAYED_RUNNABLES.get().add(var1);
      }
   }

   @Override
   public <V> Promise<V> newPromise() {
      return new ImmediateEventExecutor.ImmediatePromise<>(this);
   }

   @Override
   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new ImmediateEventExecutor.ImmediateProgressivePromise<>(this);
   }

   static class ImmediateProgressivePromise<V> extends DefaultProgressivePromise<V> {
      ImmediateProgressivePromise(EventExecutor var1) {
         super(var1);
      }

      @Override
      protected void checkDeadLock() {
      }
   }

   static class ImmediatePromise<V> extends DefaultPromise<V> {
      ImmediatePromise(EventExecutor var1) {
         super(var1);
      }

      @Override
      protected void checkDeadLock() {
      }
   }
}
