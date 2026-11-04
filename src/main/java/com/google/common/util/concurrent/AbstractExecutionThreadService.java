package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractExecutionThreadService implements Service {
   private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
   private final Service delegate = new AbstractService() {
      @Override
      protected final void doStart() {
         Executor var1 = MoreExecutors.renamingDecorator(AbstractExecutionThreadService.this.executor(), new Supplier<String>() {
            public String get() {
               return AbstractExecutionThreadService.this.serviceName();
            }
         });
         var1.execute(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     AbstractExecutionThreadService.this.startUp();
                     notifyStarted();
                     if (isRunning()) {
                        try {
                           AbstractExecutionThreadService.this.run();
                        } catch (Throwable var4) {
                           try {
                              AbstractExecutionThreadService.this.shutDown();
                           } catch (Exception var3) {
                              AbstractExecutionThreadService.logger
                                 .log(Level.WARNING, "Error while attempting to shut down the service after failure.", (Throwable)var3);
                           }

                           notifyFailed(var4);
                           return;
                        }
                     }

                     AbstractExecutionThreadService.this.shutDown();
                     notifyStopped();
                  } catch (Throwable var5) {
                     notifyFailed(var5);
                  }
               }
            }
         );
      }

      @Override
      protected void doStop() {
         AbstractExecutionThreadService.this.triggerShutdown();
      }

      @Override
      public String toString() {
         return AbstractExecutionThreadService.this.toString();
      }
   };

   protected AbstractExecutionThreadService() {
   }

   protected void startUp() throws Exception {
   }

   protected abstract void run() throws Exception;

   protected void shutDown() throws Exception {
   }

   @Beta
   protected void triggerShutdown() {
   }

   protected Executor executor() {
      return new Executor() {
         @Override
         public void execute(Runnable var1) {
            MoreExecutors.newThread(AbstractExecutionThreadService.this.serviceName(), var1).start();
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
}
