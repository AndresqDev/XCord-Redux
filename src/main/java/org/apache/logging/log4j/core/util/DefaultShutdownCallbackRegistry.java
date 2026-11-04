package org.apache.logging.log4j.core.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.status.StatusLogger;

public class DefaultShutdownCallbackRegistry implements Runnable, LifeCycle2, ShutdownCallbackRegistry {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private final AtomicReference<LifeCycle.State> state = new AtomicReference<>(LifeCycle.State.INITIALIZED);
   private final ThreadFactory threadFactory;
   private final Collection<Reference<Cancellable>> hooks = new CopyOnWriteArrayList<>();
   private Reference<Thread> shutdownHookRef;

   public DefaultShutdownCallbackRegistry() {
      this(Executors.defaultThreadFactory());
   }

   protected DefaultShutdownCallbackRegistry(ThreadFactory var1) {
      this.threadFactory = var1;
   }

   @Override
   public void run() {
      if (this.state.compareAndSet(LifeCycle.State.STARTED, LifeCycle.State.STOPPING)) {
         for (Reference var2 : this.hooks) {
            Cancellable var3 = (Cancellable)var2.get();
            if (var3 != null) {
               try {
                  var3.run();
               } catch (Throwable var7) {
                  Throwable var4 = var7;

                  try {
                     LOGGER.error(SHUTDOWN_HOOK_MARKER, "Caught exception executing shutdown hook {}", var3, var4);
                  } catch (Throwable var6) {
                     System.err.println("Caught exception " + var6.getClass() + " logging exception " + var7.getClass());
                     var7.printStackTrace();
                  }
               }
            }
         }

         this.state.set(LifeCycle.State.STOPPED);
      }
   }

   @Override
   public Cancellable addShutdownCallback(Runnable var1) {
      if (this.isStarted()) {
         DefaultShutdownCallbackRegistry.RegisteredCancellable var2 = new DefaultShutdownCallbackRegistry.RegisteredCancellable(var1, this.hooks);
         this.hooks.add(new SoftReference<>(var2));
         return var2;
      } else {
         throw new IllegalStateException("Cannot add new shutdown hook as this is not started. Current state: " + this.state.get().name());
      }
   }

   @Override
   public void initialize() {
   }

   @Override
   public void start() {
      if (this.state.compareAndSet(LifeCycle.State.INITIALIZED, LifeCycle.State.STARTING)) {
         try {
            this.addShutdownHook(this.threadFactory.newThread(this));
            this.state.set(LifeCycle.State.STARTED);
         } catch (IllegalStateException var2) {
            this.state.set(LifeCycle.State.STOPPED);
            throw var2;
         } catch (Exception var3) {
            LOGGER.catching(var3);
            this.state.set(LifeCycle.State.STOPPED);
         }
      }
   }

   private void addShutdownHook(Thread var1) {
      this.shutdownHookRef = new WeakReference<>(var1);
      Runtime.getRuntime().addShutdownHook(var1);
   }

   @Override
   public void stop() {
      this.stop(0L, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      if (this.state.compareAndSet(LifeCycle.State.STARTED, LifeCycle.State.STOPPING)) {
         try {
            this.removeShutdownHook();
         } finally {
            this.state.set(LifeCycle.State.STOPPED);
         }
      }

      return true;
   }

   private void removeShutdownHook() {
      Thread var1 = this.shutdownHookRef.get();
      if (var1 != null) {
         Runtime.getRuntime().removeShutdownHook(var1);
         this.shutdownHookRef.enqueue();
      }
   }

   @Override
   public LifeCycle.State getState() {
      return this.state.get();
   }

   @Override
   public boolean isStarted() {
      return this.state.get() == LifeCycle.State.STARTED;
   }

   @Override
   public boolean isStopped() {
      return this.state.get() == LifeCycle.State.STOPPED;
   }

   private static class RegisteredCancellable implements Cancellable {
      private Runnable callback;
      private Collection<Reference<Cancellable>> registered;

      RegisteredCancellable(Runnable var1, Collection<Reference<Cancellable>> var2) {
         this.callback = var1;
         this.registered = var2;
      }

      @Override
      public void cancel() {
         this.callback = null;
         Collection var1 = this.registered;
         if (var1 != null) {
            this.registered = null;
            var1.removeIf(var1x -> {
               Cancellable var2 = (Cancellable)var1x.get();
               return var2 == null || var2 == this;
            });
         }
      }

      @Override
      public void run() {
         Runnable var1 = this.callback;
         if (var1 != null) {
            var1.run();
            this.callback = null;
         }
      }

      @Override
      public String toString() {
         return String.valueOf(this.callback);
      }
   }
}
