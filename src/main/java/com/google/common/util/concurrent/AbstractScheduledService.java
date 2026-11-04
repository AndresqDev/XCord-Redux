package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractScheduledService implements Service {
   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
   private final AbstractService delegate = new AbstractScheduledService.ServiceDelegate();

   protected AbstractScheduledService() {
   }

   protected abstract void runOneIteration() throws Exception;

   protected void startUp() throws Exception {
   }

   protected void shutDown() throws Exception {
   }

   protected abstract AbstractScheduledService.Scheduler scheduler();

   protected ScheduledExecutorService executor() {
      class 1ThreadFactoryImpl implements ThreadFactory {
         @Override
         public Thread newThread(Runnable var1) {
            return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), var1);
         }
      }

      final ScheduledExecutorService var1 = Executors.newSingleThreadScheduledExecutor(new 1ThreadFactoryImpl());
      this.addListener(new Service.Listener(this) {
         @Override
         public void terminated(Service.State var1x) {
            var1.shutdown();
         }

         @Override
         public void failed(Service.State var1x, Throwable var2) {
            var1.shutdown();
         }
      }, MoreExecutors.directExecutor());
      return var1;
   }

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public String toString() {
      String var1 = this.serviceName();
      String var2 = String.valueOf(this.state());
      return new StringBuilder(3 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(" [").append(var2).append("]").toString();
   }

   @Override
   public final boolean isRunning() {
      return this.delegate.isRunning();
   }

   @Override
   public final Service.State state() {
      return this.delegate.state();
   }

   @Override
   public final void addListener(Service.Listener var1, Executor var2) {
      this.delegate.addListener(var1, var2);
   }

   @Override
   public final Throwable failureCause() {
      return this.delegate.failureCause();
   }

   @CanIgnoreReturnValue
   @Override
   public final Service startAsync() {
      this.delegate.startAsync();
      return this;
   }

   @CanIgnoreReturnValue
   @Override
   public final Service stopAsync() {
      this.delegate.stopAsync();
      return this;
   }

   @Override
   public final void awaitRunning() {
      this.delegate.awaitRunning();
   }

   @Override
   public final void awaitRunning(Duration var1) throws TimeoutException {
      Service.super.awaitRunning(var1);
   }

   @Override
   public final void awaitRunning(long var1, TimeUnit var3) throws TimeoutException {
      this.delegate.awaitRunning(var1, var3);
   }

   @Override
   public final void awaitTerminated() {
      this.delegate.awaitTerminated();
   }

   @Override
   public final void awaitTerminated(Duration var1) throws TimeoutException {
      Service.super.awaitTerminated(var1);
   }

   @Override
   public final void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException {
      this.delegate.awaitTerminated(var1, var3);
   }

   interface Cancellable {
      void cancel(boolean var1);

      boolean isCancelled();
   }

   public abstract static class CustomScheduler extends AbstractScheduledService.Scheduler {
      @Override
      final AbstractScheduledService.Cancellable schedule(AbstractService var1, ScheduledExecutorService var2, Runnable var3) {
         return new AbstractScheduledService.CustomScheduler.ReschedulableCallable(var1, var2, var3).reschedule();
      }

      protected abstract AbstractScheduledService.CustomScheduler.Schedule getNextSchedule() throws Exception;

      private final class ReschedulableCallable implements Callable<Void> {
         private final Runnable wrappedRunnable;
         private final ScheduledExecutorService executor;
         private final AbstractService service;
         private final ReentrantLock lock = new ReentrantLock();
         @GuardedBy("lock")
         private AbstractScheduledService.CustomScheduler.SupplantableFuture cancellationDelegate;

         ReschedulableCallable(AbstractService var2, ScheduledExecutorService var3, Runnable var4) {
            this.wrappedRunnable = var4;
            this.executor = var3;
            this.service = var2;
         }

         public Void call() throws Exception {
            this.wrappedRunnable.run();
            this.reschedule();
            return null;
         }

         @CanIgnoreReturnValue
         public AbstractScheduledService.Cancellable reschedule() {
            AbstractScheduledService.CustomScheduler.Schedule var1;
            try {
               var1 = CustomScheduler.this.getNextSchedule();
            } catch (Throwable var11) {
               this.service.notifyFailed(var11);
               return new AbstractScheduledService.FutureAsCancellable(Futures.immediateCancelledFuture());
            }

            Throwable var2 = null;
            this.lock.lock();

            Object var3;
            try {
               var3 = this.initializeOrUpdateCancellationDelegate(var1);
            } catch (Throwable var9) {
               var2 = var9;
               var3 = new AbstractScheduledService.FutureAsCancellable(Futures.immediateCancelledFuture());
            } finally {
               this.lock.unlock();
            }

            if (var2 != null) {
               this.service.notifyFailed(var2);
            }

            return (AbstractScheduledService.Cancellable)var3;
         }

         @GuardedBy("lock")
         private AbstractScheduledService.Cancellable initializeOrUpdateCancellationDelegate(AbstractScheduledService.CustomScheduler.Schedule var1) {
            if (this.cancellationDelegate == null) {
               return this.cancellationDelegate = new AbstractScheduledService.CustomScheduler.SupplantableFuture(this.lock, this.submitToExecutor(var1));
            } else {
               if (!this.cancellationDelegate.currentFuture.isCancelled()) {
                  this.cancellationDelegate.currentFuture = this.submitToExecutor(var1);
               }

               return this.cancellationDelegate;
            }
         }

         private ScheduledFuture<Void> submitToExecutor(AbstractScheduledService.CustomScheduler.Schedule var1) {
            return this.executor.schedule(this, var1.delay, var1.unit);
         }
      }

      protected static final class Schedule {
         private final long delay;
         private final TimeUnit unit;

         public Schedule(long var1, TimeUnit var3) {
            this.delay = var1;
            this.unit = Preconditions.checkNotNull(var3);
         }
      }

      private static final class SupplantableFuture implements AbstractScheduledService.Cancellable {
         private final ReentrantLock lock;
         @GuardedBy("lock")
         private Future<Void> currentFuture;

         SupplantableFuture(ReentrantLock var1, Future<Void> var2) {
            this.lock = var1;
            this.currentFuture = var2;
         }

         @Override
         public void cancel(boolean var1) {
            this.lock.lock();

            try {
               this.currentFuture.cancel(var1);
            } finally {
               this.lock.unlock();
            }
         }

         @Override
         public boolean isCancelled() {
            this.lock.lock();

            boolean var1;
            try {
               var1 = this.currentFuture.isCancelled();
            } finally {
               this.lock.unlock();
            }

            return var1;
         }
      }
   }

   private static final class FutureAsCancellable implements AbstractScheduledService.Cancellable {
      private final Future<?> delegate;

      FutureAsCancellable(Future<?> var1) {
         this.delegate = var1;
      }

      @Override
      public void cancel(boolean var1) {
         this.delegate.cancel(var1);
      }

      @Override
      public boolean isCancelled() {
         return this.delegate.isCancelled();
      }
   }

   public abstract static class Scheduler {
      public static AbstractScheduledService.Scheduler newFixedDelaySchedule(Duration var0, Duration var1) {
         return newFixedDelaySchedule(Internal.toNanosSaturated(var0), Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
      }

      public static AbstractScheduledService.Scheduler newFixedDelaySchedule(final long var0, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var4);
         Preconditions.checkArgument(var2 > 0L, "delay must be > 0, found %s", var2);
         return new AbstractScheduledService.Scheduler() {
            @Override
            public AbstractScheduledService.Cancellable schedule(AbstractService var1, ScheduledExecutorService var2x, Runnable var3) {
               return new AbstractScheduledService.FutureAsCancellable(var2x.scheduleWithFixedDelay(var3, var0, var2, var4));
            }
         };
      }

      public static AbstractScheduledService.Scheduler newFixedRateSchedule(Duration var0, Duration var1) {
         return newFixedRateSchedule(Internal.toNanosSaturated(var0), Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
      }

      public static AbstractScheduledService.Scheduler newFixedRateSchedule(final long var0, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var4);
         Preconditions.checkArgument(var2 > 0L, "period must be > 0, found %s", var2);
         return new AbstractScheduledService.Scheduler() {
            @Override
            public AbstractScheduledService.Cancellable schedule(AbstractService var1, ScheduledExecutorService var2x, Runnable var3) {
               return new AbstractScheduledService.FutureAsCancellable(var2x.scheduleAtFixedRate(var3, var0, var2, var4));
            }
         };
      }

      abstract AbstractScheduledService.Cancellable schedule(AbstractService var1, ScheduledExecutorService var2, Runnable var3);

      private Scheduler() {
      }
   }

   private final class ServiceDelegate extends AbstractService {
      private volatile AbstractScheduledService.Cancellable runningTask;
      private volatile ScheduledExecutorService executorService;
      private final ReentrantLock lock = new ReentrantLock();
      private final Runnable task = new AbstractScheduledService.ServiceDelegate.Task();

      private ServiceDelegate() {
      }

      @Override
      protected final void doStart() {
         this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier<String>() {
            public String get() {
               String var1 = AbstractScheduledService.this.serviceName();
               String var2 = String.valueOf(ServiceDelegate.this.state());
               return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(" ").append(var2).toString();
            }
         });
         this.executorService
            .execute(
               new Runnable() {
                  @Override
                  public void run() {
                     ServiceDelegate.this.lock.lock();

                     try {
                        AbstractScheduledService.this.startUp();
                        ServiceDelegate.this.runningTask = AbstractScheduledService.this.scheduler()
                           .schedule(AbstractScheduledService.this.delegate, ServiceDelegate.this.executorService, ServiceDelegate.this.task);
                        ServiceDelegate.this.notifyStarted();
                     } catch (Throwable var5) {
                        ServiceDelegate.this.notifyFailed(var5);
                        if (ServiceDelegate.this.runningTask != null) {
                           ServiceDelegate.this.runningTask.cancel(false);
                        }
                     } finally {
                        ServiceDelegate.this.lock.unlock();
                     }
                  }
               }
            );
      }

      @Override
      protected final void doStop() {
         Objects.requireNonNull(this.runningTask);
         Objects.requireNonNull(this.executorService);
         this.runningTask.cancel(false);
         this.executorService.execute(new Runnable() {
            @Override
            public void run() {
               try {
                  ServiceDelegate.this.lock.lock();

                  label42: {
                     try {
                        if (ServiceDelegate.this.state() == Service.State.STOPPING) {
                           AbstractScheduledService.this.shutDown();
                           break label42;
                        }
                     } finally {
                        ServiceDelegate.this.lock.unlock();
                     }

                     return;
                  }

                  ServiceDelegate.this.notifyStopped();
               } catch (Throwable var5) {
                  ServiceDelegate.this.notifyFailed(var5);
               }
            }
         });
      }

      @Override
      public String toString() {
         return AbstractScheduledService.this.toString();
      }

      class Task implements Runnable {
         @Override
         public void run() {
            ServiceDelegate.this.lock.lock();

            try {
               if (!Objects.requireNonNull(ServiceDelegate.this.runningTask).isCancelled()) {
                  AbstractScheduledService.this.runOneIteration();
                  return;
               }
            } catch (Throwable var8) {
               try {
                  AbstractScheduledService.this.shutDown();
               } catch (Exception var7) {
                  AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", (Throwable)var7);
               }

               ServiceDelegate.this.notifyFailed(var8);
               Objects.requireNonNull(ServiceDelegate.this.runningTask).cancel(false);
               return;
            } finally {
               ServiceDelegate.this.lock.unlock();
            }
         }
      }
   }
}
