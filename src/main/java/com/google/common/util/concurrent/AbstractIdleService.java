package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractIdleService implements Service {
   private final Supplier<String> threadNameSupplier = new AbstractIdleService.ThreadNameSupplier();
   private final Service delegate = new AbstractIdleService.DelegateService();

   protected AbstractIdleService() {
   }

   protected abstract void startUp() throws Exception;

   protected abstract void shutDown() throws Exception;

   protected Executor executor() {
      return new Executor() {
         @Override
         public void execute(Runnable var1) {
            MoreExecutors.newThread(AbstractIdleService.this.threadNameSupplier.get(), var1).start();
         }
      };
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

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }

   private final class DelegateService extends AbstractService {
      private DelegateService() {
      }

      @Override
      protected final void doStart() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            @Override
            public void run() {
               try {
                  AbstractIdleService.this.startUp();
                  DelegateService.this.notifyStarted();
               } catch (Throwable var2) {
                  DelegateService.this.notifyFailed(var2);
               }
            }
         });
      }

      @Override
      protected final void doStop() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            @Override
            public void run() {
               try {
                  AbstractIdleService.this.shutDown();
                  DelegateService.this.notifyStopped();
               } catch (Throwable var2) {
                  DelegateService.this.notifyFailed(var2);
               }
            }
         });
      }

      @Override
      public String toString() {
         return AbstractIdleService.this.toString();
      }
   }

   private final class ThreadNameSupplier implements Supplier<String> {
      private ThreadNameSupplier() {
      }

      public String get() {
         String var1 = AbstractIdleService.this.serviceName();
         String var2 = String.valueOf(AbstractIdleService.this.state());
         return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(" ").append(var2).toString();
      }
   }
}
