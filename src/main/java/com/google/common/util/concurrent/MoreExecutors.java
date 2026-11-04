package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class MoreExecutors {
   private MoreExecutors() {
   }

   @Beta
   @GwtIncompatible
   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor var0, Duration var1) {
      return getExitingExecutorService(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   @Beta
   @GwtIncompatible
   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor var0, long var1, TimeUnit var3) {
      return new MoreExecutors.Application().getExitingExecutorService(var0, var1, var3);
   }

   @Beta
   @GwtIncompatible
   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor var0) {
      return new MoreExecutors.Application().getExitingExecutorService(var0);
   }

   @Beta
   @GwtIncompatible
   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var0, Duration var1) {
      return getExitingScheduledExecutorService(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   @Beta
   @GwtIncompatible
   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var0, long var1, TimeUnit var3) {
      return new MoreExecutors.Application().getExitingScheduledExecutorService(var0, var1, var3);
   }

   @Beta
   @GwtIncompatible
   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var0) {
      return new MoreExecutors.Application().getExitingScheduledExecutorService(var0);
   }

   @Beta
   @GwtIncompatible
   public static void addDelayedShutdownHook(ExecutorService var0, Duration var1) {
      addDelayedShutdownHook(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   @Beta
   @GwtIncompatible
   public static void addDelayedShutdownHook(ExecutorService var0, long var1, TimeUnit var3) {
      new MoreExecutors.Application().addDelayedShutdownHook(var0, var1, var3);
   }

   @GwtIncompatible
   private static void useDaemonThreadFactory(ThreadPoolExecutor var0) {
      var0.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(var0.getThreadFactory()).build());
   }

   @GwtIncompatible
   public static ListeningExecutorService newDirectExecutorService() {
      return new MoreExecutors.DirectExecutorService();
   }

   public static Executor directExecutor() {
      return DirectExecutor.INSTANCE;
   }

   @Beta
   @GwtIncompatible
   public static Executor newSequentialExecutor(Executor var0) {
      return new SequentialExecutor(var0);
   }

   @GwtIncompatible
   public static ListeningExecutorService listeningDecorator(ExecutorService var0) {
      return (ListeningExecutorService)(var0 instanceof ListeningExecutorService
         ? (ListeningExecutorService)var0
         : (
            var0 instanceof ScheduledExecutorService
               ? new MoreExecutors.ScheduledListeningDecorator((ScheduledExecutorService)var0)
               : new MoreExecutors.ListeningDecorator(var0)
         ));
   }

   @GwtIncompatible
   public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService var0) {
      return (ListeningScheduledExecutorService)(var0 instanceof ListeningScheduledExecutorService
         ? (ListeningScheduledExecutorService)var0
         : new MoreExecutors.ScheduledListeningDecorator(var0));
   }

   @ParametricNullness
   @GwtIncompatible
   static <T> T invokeAnyImpl(ListeningExecutorService var0, Collection<? extends Callable<T>> var1, boolean var2, Duration var3) throws InterruptedException, ExecutionException, TimeoutException {
      return invokeAnyImpl(var0, var1, var2, Internal.toNanosSaturated(var3), TimeUnit.NANOSECONDS);
   }

   @ParametricNullness
   @GwtIncompatible
   static <T> T invokeAnyImpl(ListeningExecutorService var0, Collection<? extends Callable<T>> var1, boolean var2, long var3, TimeUnit var5) throws InterruptedException, ExecutionException, TimeoutException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var5);
      int var6 = var1.size();
      Preconditions.checkArgument(var6 > 0);
      ArrayList var7 = Lists.newArrayListWithCapacity(var6);
      LinkedBlockingQueue var8 = Queues.newLinkedBlockingQueue();
      long var9 = var5.toNanos(var3);

      try {
         ExecutionException var11 = null;
         long var12 = var2 ? System.nanoTime() : 0L;
         Iterator var14 = var1.iterator();
         var7.add(submitAndAddQueueListener(var0, (Callable<T>)var14.next(), var8));
         var6--;
         int var15 = 1;

         while (true) {
            Future var16 = (Future)var8.poll();
            if (var16 == null) {
               if (var6 > 0) {
                  var6--;
                  var7.add(submitAndAddQueueListener(var0, (Callable<T>)var14.next(), var8));
                  var15++;
               } else {
                  if (var15 == 0) {
                     if (var11 == null) {
                        var11 = new ExecutionException(null);
                     }

                     throw var11;
                  }

                  if (var2) {
                     var16 = (Future)var8.poll(var9, TimeUnit.NANOSECONDS);
                     if (var16 == null) {
                        throw new TimeoutException();
                     }

                     long var17 = System.nanoTime();
                     var9 -= var17 - var12;
                     var12 = var17;
                  } else {
                     var16 = (Future)var8.take();
                  }
               }
            }

            if (var16 != null) {
               var15--;

               try {
                  return (T)var16.get();
               } catch (ExecutionException var26) {
                  var11 = var26;
               } catch (RuntimeException var27) {
                  var11 = new ExecutionException(var27);
               }
            }
         }
      } finally {
         for (Future var22 : var7) {
            var22.cancel(true);
         }
      }
   }

   @GwtIncompatible
   private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService var0, Callable<T> var1, final BlockingQueue<Future<T>> var2) {
      final ListenableFuture var3 = var0.submit(var1);
      var3.addListener(new Runnable() {
         @Override
         public void run() {
            var2.add(var3);
         }
      }, directExecutor());
      return var3;
   }

   @Beta
   @GwtIncompatible
   public static ThreadFactory platformThreadFactory() {
      if (!isAppEngineWithApiClasses()) {
         return Executors.defaultThreadFactory();
      } else {
         try {
            return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory").invoke(null);
         } catch (IllegalAccessException var1) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var1);
         } catch (ClassNotFoundException var2) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var2);
         } catch (NoSuchMethodException var3) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var3);
         } catch (InvocationTargetException var4) {
            throw Throwables.propagate(var4.getCause());
         }
      }
   }

   @GwtIncompatible
   private static boolean isAppEngineWithApiClasses() {
      if (System.getProperty("com.google.appengine.runtime.environment") == null) {
         return false;
      } else {
         try {
            Class.forName("com.google.appengine.api.utils.SystemProperty");
         } catch (ClassNotFoundException var5) {
            return false;
         }

         try {
            return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment").invoke(null) != null;
         } catch (ClassNotFoundException var1) {
            return false;
         } catch (InvocationTargetException var2) {
            return false;
         } catch (IllegalAccessException var3) {
            return false;
         } catch (NoSuchMethodException var4) {
            return false;
         }
      }
   }

   @GwtIncompatible
   static Thread newThread(String var0, Runnable var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Thread var2 = platformThreadFactory().newThread(var1);

      try {
         var2.setName(var0);
      } catch (SecurityException var4) {
      }

      return var2;
   }

   @GwtIncompatible
   static Executor renamingDecorator(final Executor var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Executor() {
         @Override
         public void execute(Runnable var1x) {
            var0.execute(Callables.threadRenaming(var1x, var1));
         }
      };
   }

   @GwtIncompatible
   static ExecutorService renamingDecorator(ExecutorService var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new WrappingExecutorService(var0) {
         @Override
         protected <T> Callable<T> wrapTask(Callable<T> var1x) {
            return Callables.threadRenaming(var1x, var1);
         }

         @Override
         protected Runnable wrapTask(Runnable var1x) {
            return Callables.threadRenaming(var1x, var1);
         }
      };
   }

   @GwtIncompatible
   static ScheduledExecutorService renamingDecorator(ScheduledExecutorService var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new WrappingScheduledExecutorService(var0) {
         @Override
         protected <T> Callable<T> wrapTask(Callable<T> var1x) {
            return Callables.threadRenaming(var1x, var1);
         }

         @Override
         protected Runnable wrapTask(Runnable var1x) {
            return Callables.threadRenaming(var1x, var1);
         }
      };
   }

   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static boolean shutdownAndAwaitTermination(ExecutorService var0, Duration var1) {
      return shutdownAndAwaitTermination(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static boolean shutdownAndAwaitTermination(ExecutorService var0, long var1, TimeUnit var3) {
      long var4 = var3.toNanos(var1) / 2L;
      var0.shutdown();

      try {
         if (!var0.awaitTermination(var4, TimeUnit.NANOSECONDS)) {
            var0.shutdownNow();
            var0.awaitTermination(var4, TimeUnit.NANOSECONDS);
         }
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
         var0.shutdownNow();
      }

      return var0.isTerminated();
   }

   static Executor rejectionPropagatingExecutor(final Executor var0, final AbstractFuture<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return var0 == directExecutor() ? var0 : new Executor() {
         @Override
         public void execute(Runnable var1x) {
            try {
               var0.execute(var1x);
            } catch (RejectedExecutionException var3) {
               var1.setException(var3);
            }
         }
      };
   }

   @GwtIncompatible
   @VisibleForTesting
   static class Application {
      final ExecutorService getExitingExecutorService(ThreadPoolExecutor var1, long var2, TimeUnit var4) {
         MoreExecutors.useDaemonThreadFactory(var1);
         ExecutorService var5 = Executors.unconfigurableExecutorService(var1);
         this.addDelayedShutdownHook(var1, var2, var4);
         return var5;
      }

      final ExecutorService getExitingExecutorService(ThreadPoolExecutor var1) {
         return this.getExitingExecutorService(var1, 120L, TimeUnit.SECONDS);
      }

      final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var1, long var2, TimeUnit var4) {
         MoreExecutors.useDaemonThreadFactory(var1);
         ScheduledExecutorService var5 = Executors.unconfigurableScheduledExecutorService(var1);
         this.addDelayedShutdownHook(var1, var2, var4);
         return var5;
      }

      final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var1) {
         return this.getExitingScheduledExecutorService(var1, 120L, TimeUnit.SECONDS);
      }

      final void addDelayedShutdownHook(final ExecutorService var1, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var4);
         String var5 = String.valueOf(var1);
         this.addShutdownHook(
            MoreExecutors.newThread(
               new StringBuilder(24 + String.valueOf(var5).length()).append("DelayedShutdownHook-for-").append(var5).toString(), new Runnable(this) {
                  @Override
                  public void run() {
                     try {
                        var1.shutdown();
                        var1.awaitTermination(var2, var4);
                     } catch (InterruptedException var2x) {
                     }
                  }
               }
            )
         );
      }

      @VisibleForTesting
      void addShutdownHook(Thread var1) {
         Runtime.getRuntime().addShutdownHook(var1);
      }
   }

   @GwtIncompatible
   private static final class DirectExecutorService extends AbstractListeningExecutorService {
      private final Object lock = new Object();
      @GuardedBy("lock")
      private int runningTasks = 0;
      @GuardedBy("lock")
      private boolean shutdown = false;

      private DirectExecutorService() {
      }

      @Override
      public void execute(Runnable var1) {
         this.startTask();

         try {
            var1.run();
         } finally {
            this.endTask();
         }
      }

      @Override
      public boolean isShutdown() {
         synchronized (this.lock) {
            return this.shutdown;
         }
      }

      @Override
      public void shutdown() {
         synchronized (this.lock) {
            this.shutdown = true;
            if (this.runningTasks == 0) {
               this.lock.notifyAll();
            }
         }
      }

      @Override
      public List<Runnable> shutdownNow() {
         this.shutdown();
         return Collections.emptyList();
      }

      @Override
      public boolean isTerminated() {
         synchronized (this.lock) {
            return this.shutdown && this.runningTasks == 0;
         }
      }

      @Override
      public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         long var4 = var3.toNanos(var1);
         synchronized (this.lock) {
            while (!this.shutdown || this.runningTasks != 0) {
               if (var4 <= 0L) {
                  return false;
               }

               long var7 = System.nanoTime();
               TimeUnit.NANOSECONDS.timedWait(this.lock, var4);
               var4 -= System.nanoTime() - var7;
            }

            return true;
         }
      }

      private void startTask() {
         synchronized (this.lock) {
            if (this.shutdown) {
               throw new RejectedExecutionException("Executor already shutdown");
            } else {
               this.runningTasks++;
            }
         }
      }

      private void endTask() {
         synchronized (this.lock) {
            int var2 = --this.runningTasks;
            if (var2 == 0) {
               this.lock.notifyAll();
            }
         }
      }
   }

   @GwtIncompatible
   private static class ListeningDecorator extends AbstractListeningExecutorService {
      private final ExecutorService delegate;

      ListeningDecorator(ExecutorService var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      public final boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         return this.delegate.awaitTermination(var1, var3);
      }

      @Override
      public final boolean isShutdown() {
         return this.delegate.isShutdown();
      }

      @Override
      public final boolean isTerminated() {
         return this.delegate.isTerminated();
      }

      @Override
      public final void shutdown() {
         this.delegate.shutdown();
      }

      @Override
      public final List<Runnable> shutdownNow() {
         return this.delegate.shutdownNow();
      }

      @Override
      public final void execute(Runnable var1) {
         this.delegate.execute(var1);
      }

      @Override
      public final String toString() {
         String var1 = super.toString();
         String var2 = String.valueOf(this.delegate);
         return new StringBuilder(2 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append("[")
            .append(var2)
            .append("]")
            .toString();
      }
   }

   @GwtIncompatible
   private static final class ScheduledListeningDecorator extends MoreExecutors.ListeningDecorator implements ListeningScheduledExecutorService {
      final ScheduledExecutorService delegate;

      ScheduledListeningDecorator(ScheduledExecutorService var1) {
         super(var1);
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      public ListenableScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
         TrustedListenableFutureTask var5 = TrustedListenableFutureTask.create(var1, null);
         ScheduledFuture var6 = this.delegate.schedule(var5, var2, var4);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask(var5, var6);
      }

      @Override
      public <V> ListenableScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
         TrustedListenableFutureTask var5 = TrustedListenableFutureTask.create(var1);
         ScheduledFuture var6 = this.delegate.schedule(var5, var2, var4);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(var5, var6);
      }

      @Override
      public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
         MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask var7 = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(
            var1
         );
         ScheduledFuture var8 = this.delegate.scheduleAtFixedRate(var7, var2, var4, var6);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(var7, var8);
      }

      @Override
      public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
         MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask var7 = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(
            var1
         );
         ScheduledFuture var8 = this.delegate.scheduleWithFixedDelay(var7, var2, var4, var6);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask<>(var7, var8);
      }

      private static final class ListenableScheduledTask<V>
         extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
         implements ListenableScheduledFuture<V> {
         private final ScheduledFuture<?> scheduledDelegate;

         public ListenableScheduledTask(ListenableFuture<V> var1, ScheduledFuture<?> var2) {
            super(var1);
            this.scheduledDelegate = var2;
         }

         @Override
         public boolean cancel(boolean var1) {
            boolean var2 = super.cancel(var1);
            if (var2) {
               this.scheduledDelegate.cancel(var1);
            }

            return var2;
         }

         @Override
         public long getDelay(TimeUnit var1) {
            return this.scheduledDelegate.getDelay(var1);
         }

         public int compareTo(Delayed var1) {
            return this.scheduledDelegate.compareTo(var1);
         }
      }

      @GwtIncompatible
      private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture.TrustedFuture<Void> implements Runnable {
         private final Runnable delegate;

         public NeverSuccessfulListenableFutureTask(Runnable var1) {
            this.delegate = Preconditions.checkNotNull(var1);
         }

         @Override
         public void run() {
            try {
               this.delegate.run();
            } catch (Throwable var2) {
               this.setException(var2);
               throw Throwables.propagate(var2);
            }
         }

         @Override
         protected String pendingToString() {
            String var1 = String.valueOf(this.delegate);
            return new StringBuilder(7 + String.valueOf(var1).length()).append("task=[").append(var1).append("]").toString();
         }
      }
   }
}
