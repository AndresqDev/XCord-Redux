package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractService implements Service {
   private static final ListenerCallQueue.Event<Service.Listener> STARTING_EVENT = new ListenerCallQueue.Event<Service.Listener>() {
      public void call(Service.Listener var1) {
         var1.starting();
      }

      @Override
      public String toString() {
         return "starting()";
      }
   };
   private static final ListenerCallQueue.Event<Service.Listener> RUNNING_EVENT = new ListenerCallQueue.Event<Service.Listener>() {
      public void call(Service.Listener var1) {
         var1.running();
      }

      @Override
      public String toString() {
         return "running()";
      }
   };
   private static final ListenerCallQueue.Event<Service.Listener> STOPPING_FROM_STARTING_EVENT = stoppingEvent(Service.State.STARTING);
   private static final ListenerCallQueue.Event<Service.Listener> STOPPING_FROM_RUNNING_EVENT = stoppingEvent(Service.State.RUNNING);
   private static final ListenerCallQueue.Event<Service.Listener> TERMINATED_FROM_NEW_EVENT = terminatedEvent(Service.State.NEW);
   private static final ListenerCallQueue.Event<Service.Listener> TERMINATED_FROM_STARTING_EVENT = terminatedEvent(Service.State.STARTING);
   private static final ListenerCallQueue.Event<Service.Listener> TERMINATED_FROM_RUNNING_EVENT = terminatedEvent(Service.State.RUNNING);
   private static final ListenerCallQueue.Event<Service.Listener> TERMINATED_FROM_STOPPING_EVENT = terminatedEvent(Service.State.STOPPING);
   private final Monitor monitor = new Monitor();
   private final Monitor.Guard isStartable = new AbstractService.IsStartableGuard();
   private final Monitor.Guard isStoppable = new AbstractService.IsStoppableGuard();
   private final Monitor.Guard hasReachedRunning = new AbstractService.HasReachedRunningGuard();
   private final Monitor.Guard isStopped = new AbstractService.IsStoppedGuard();
   private final ListenerCallQueue<Service.Listener> listeners = new ListenerCallQueue<>();
   private volatile AbstractService.StateSnapshot snapshot = new AbstractService.StateSnapshot(Service.State.NEW);

   private static ListenerCallQueue.Event<Service.Listener> terminatedEvent(final Service.State var0) {
      return new ListenerCallQueue.Event<Service.Listener>() {
         public void call(Service.Listener var1) {
            var1.terminated(var0);
         }

         @Override
         public String toString() {
            String var1 = String.valueOf(var0);
            return new StringBuilder(21 + String.valueOf(var1).length()).append("terminated({from = ").append(var1).append("})").toString();
         }
      };
   }

   private static ListenerCallQueue.Event<Service.Listener> stoppingEvent(final Service.State var0) {
      return new ListenerCallQueue.Event<Service.Listener>() {
         public void call(Service.Listener var1) {
            var1.stopping(var0);
         }

         @Override
         public String toString() {
            String var1 = String.valueOf(var0);
            return new StringBuilder(19 + String.valueOf(var1).length()).append("stopping({from = ").append(var1).append("})").toString();
         }
      };
   }

   protected AbstractService() {
   }

   @ForOverride
   protected abstract void doStart();

   @ForOverride
   protected abstract void doStop();

   @Beta
   @ForOverride
   protected void doCancelStart() {
   }

   @CanIgnoreReturnValue
   @Override
   public final Service startAsync() {
      if (this.monitor.enterIf(this.isStartable)) {
         try {
            this.snapshot = new AbstractService.StateSnapshot(Service.State.STARTING);
            this.enqueueStartingEvent();
            this.doStart();
         } catch (Throwable var5) {
            this.notifyFailed(var5);
         } finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
         }

         return this;
      } else {
         String var1 = String.valueOf(this);
         throw new IllegalStateException(
            new StringBuilder(33 + String.valueOf(var1).length()).append("Service ").append(var1).append(" has already been started").toString()
         );
      }
   }

   @CanIgnoreReturnValue
   @Override
   public final Service stopAsync() {
      if (this.monitor.enterIf(this.isStoppable)) {
         try {
            Service.State var1 = this.state();
            switch (var1) {
               case NEW:
                  this.snapshot = new AbstractService.StateSnapshot(Service.State.TERMINATED);
                  this.enqueueTerminatedEvent(Service.State.NEW);
                  break;
               case STARTING:
                  this.snapshot = new AbstractService.StateSnapshot(Service.State.STARTING, true, null);
                  this.enqueueStoppingEvent(Service.State.STARTING);
                  this.doCancelStart();
                  break;
               case RUNNING:
                  this.snapshot = new AbstractService.StateSnapshot(Service.State.STOPPING);
                  this.enqueueStoppingEvent(Service.State.RUNNING);
                  this.doStop();
                  break;
               case STOPPING:
               case TERMINATED:
               case FAILED:
                  String var2 = String.valueOf(var1);
                  throw new AssertionError(
                     new StringBuilder(45 + String.valueOf(var2).length()).append("isStoppable is incorrectly implemented, saw: ").append(var2).toString()
                  );
            }
         } catch (Throwable var6) {
            this.notifyFailed(var6);
         } finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
         }
      }

      return this;
   }

   @Override
   public final void awaitRunning() {
      this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);

      try {
         this.checkCurrentState(Service.State.RUNNING);
      } finally {
         this.monitor.leave();
      }
   }

   @Override
   public final void awaitRunning(Duration var1) throws TimeoutException {
      Service.super.awaitRunning(var1);
   }

   @Override
   public final void awaitRunning(long var1, TimeUnit var3) throws TimeoutException {
      if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, var1, var3)) {
         try {
            this.checkCurrentState(Service.State.RUNNING);
         } finally {
            this.monitor.leave();
         }
      } else {
         String var4 = String.valueOf(this);
         throw new TimeoutException(
            new StringBuilder(50 + String.valueOf(var4).length())
               .append("Timed out waiting for ")
               .append(var4)
               .append(" to reach the RUNNING state.")
               .toString()
         );
      }
   }

   @Override
   public final void awaitTerminated() {
      this.monitor.enterWhenUninterruptibly(this.isStopped);

      try {
         this.checkCurrentState(Service.State.TERMINATED);
      } finally {
         this.monitor.leave();
      }
   }

   @Override
   public final void awaitTerminated(Duration var1) throws TimeoutException {
      Service.super.awaitTerminated(var1);
   }

   @Override
   public final void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException {
      if (this.monitor.enterWhenUninterruptibly(this.isStopped, var1, var3)) {
         try {
            this.checkCurrentState(Service.State.TERMINATED);
         } finally {
            this.monitor.leave();
         }
      } else {
         String var4 = String.valueOf(this);
         String var5 = String.valueOf(this.state());
         throw new TimeoutException(
            new StringBuilder(65 + String.valueOf(var4).length() + String.valueOf(var5).length())
               .append("Timed out waiting for ")
               .append(var4)
               .append(" to reach a terminal state. Current state: ")
               .append(var5)
               .toString()
         );
      }
   }

   @GuardedBy("monitor")
   private void checkCurrentState(Service.State var1) {
      Service.State var2 = this.state();
      if (var2 != var1) {
         if (var2 == Service.State.FAILED) {
            String var6 = String.valueOf(this);
            String var7 = String.valueOf(var1);
            throw new IllegalStateException(
               new StringBuilder(56 + String.valueOf(var6).length() + String.valueOf(var7).length())
                  .append("Expected the service ")
                  .append(var6)
                  .append(" to be ")
                  .append(var7)
                  .append(", but the service has FAILED")
                  .toString(),
               this.failureCause()
            );
         } else {
            String var3 = String.valueOf(this);
            String var4 = String.valueOf(var1);
            String var5 = String.valueOf(var2);
            throw new IllegalStateException(
               new StringBuilder(38 + String.valueOf(var3).length() + String.valueOf(var4).length() + String.valueOf(var5).length())
                  .append("Expected the service ")
                  .append(var3)
                  .append(" to be ")
                  .append(var4)
                  .append(", but was ")
                  .append(var5)
                  .toString()
            );
         }
      }
   }

   protected final void notifyStarted() {
      this.monitor.enter();

      try {
         if (this.snapshot.state != Service.State.STARTING) {
            String var2 = String.valueOf(this.snapshot.state);
            IllegalStateException var1 = new IllegalStateException(
               new StringBuilder(43 + String.valueOf(var2).length()).append("Cannot notifyStarted() when the service is ").append(var2).toString()
            );
            this.notifyFailed(var1);
            throw var1;
         }

         if (this.snapshot.shutdownWhenStartupFinishes) {
            this.snapshot = new AbstractService.StateSnapshot(Service.State.STOPPING);
            this.doStop();
         } else {
            this.snapshot = new AbstractService.StateSnapshot(Service.State.RUNNING);
            this.enqueueRunningEvent();
         }
      } finally {
         this.monitor.leave();
         this.dispatchListenerEvents();
      }
   }

   protected final void notifyStopped() {
      this.monitor.enter();

      try {
         Service.State var1 = this.state();
         switch (var1) {
            case NEW:
            case TERMINATED:
            case FAILED:
               String var2 = String.valueOf(var1);
               throw new IllegalStateException(
                  new StringBuilder(43 + String.valueOf(var2).length()).append("Cannot notifyStopped() when the service is ").append(var2).toString()
               );
            case STARTING:
            case RUNNING:
            case STOPPING:
               this.snapshot = new AbstractService.StateSnapshot(Service.State.TERMINATED);
               this.enqueueTerminatedEvent(var1);
         }
      } finally {
         this.monitor.leave();
         this.dispatchListenerEvents();
      }
   }

   protected final void notifyFailed(Throwable var1) {
      Preconditions.checkNotNull(var1);
      this.monitor.enter();

      try {
         Service.State var2 = this.state();
         switch (var2) {
            case NEW:
            case TERMINATED:
               String var3 = String.valueOf(var2);
               throw new IllegalStateException(
                  new StringBuilder(22 + String.valueOf(var3).length()).append("Failed while in state:").append(var3).toString(), var1
               );
            case STARTING:
            case RUNNING:
            case STOPPING:
               this.snapshot = new AbstractService.StateSnapshot(Service.State.FAILED, false, var1);
               this.enqueueFailedEvent(var2, var1);
            case FAILED:
         }
      } finally {
         this.monitor.leave();
         this.dispatchListenerEvents();
      }
   }

   @Override
   public final boolean isRunning() {
      return this.state() == Service.State.RUNNING;
   }

   @Override
   public final Service.State state() {
      return this.snapshot.externalState();
   }

   @Override
   public final Throwable failureCause() {
      return this.snapshot.failureCause();
   }

   @Override
   public final void addListener(Service.Listener var1, Executor var2) {
      this.listeners.addListener(var1, var2);
   }

   @Override
   public String toString() {
      String var1 = this.getClass().getSimpleName();
      String var2 = String.valueOf(this.state());
      return new StringBuilder(3 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(" [").append(var2).append("]").toString();
   }

   private void dispatchListenerEvents() {
      if (!this.monitor.isOccupiedByCurrentThread()) {
         this.listeners.dispatch();
      }
   }

   private void enqueueStartingEvent() {
      this.listeners.enqueue(STARTING_EVENT);
   }

   private void enqueueRunningEvent() {
      this.listeners.enqueue(RUNNING_EVENT);
   }

   private void enqueueStoppingEvent(Service.State var1) {
      if (var1 == Service.State.STARTING) {
         this.listeners.enqueue(STOPPING_FROM_STARTING_EVENT);
      } else {
         if (var1 != Service.State.RUNNING) {
            throw new AssertionError();
         }

         this.listeners.enqueue(STOPPING_FROM_RUNNING_EVENT);
      }
   }

   private void enqueueTerminatedEvent(Service.State var1) {
      switch (var1) {
         case NEW:
            this.listeners.enqueue(TERMINATED_FROM_NEW_EVENT);
            break;
         case STARTING:
            this.listeners.enqueue(TERMINATED_FROM_STARTING_EVENT);
            break;
         case RUNNING:
            this.listeners.enqueue(TERMINATED_FROM_RUNNING_EVENT);
            break;
         case STOPPING:
            this.listeners.enqueue(TERMINATED_FROM_STOPPING_EVENT);
            break;
         case TERMINATED:
         case FAILED:
            throw new AssertionError();
      }
   }

   private void enqueueFailedEvent(final Service.State var1, final Throwable var2) {
      this.listeners
         .enqueue(
            new ListenerCallQueue.Event<Service.Listener>(this) {
               public void call(Service.Listener var1x) {
                  var1x.failed(var1, var2);
               }

               @Override
               public String toString() {
                  String var1x = String.valueOf(var1);
                  String var2x = String.valueOf(var2);
                  return new StringBuilder(27 + String.valueOf(var1x).length() + String.valueOf(var2x).length())
                     .append("failed({from = ")
                     .append(var1x)
                     .append(", cause = ")
                     .append(var2x)
                     .append("})")
                     .toString();
               }
            }
         );
   }

   private final class HasReachedRunningGuard extends Monitor.Guard {
      HasReachedRunningGuard() {
         super(AbstractService.this.monitor);
      }

      @Override
      public boolean isSatisfied() {
         return AbstractService.this.state().compareTo(Service.State.RUNNING) >= 0;
      }
   }

   private final class IsStartableGuard extends Monitor.Guard {
      IsStartableGuard() {
         super(AbstractService.this.monitor);
      }

      @Override
      public boolean isSatisfied() {
         return AbstractService.this.state() == Service.State.NEW;
      }
   }

   private final class IsStoppableGuard extends Monitor.Guard {
      IsStoppableGuard() {
         super(AbstractService.this.monitor);
      }

      @Override
      public boolean isSatisfied() {
         return AbstractService.this.state().compareTo(Service.State.RUNNING) <= 0;
      }
   }

   private final class IsStoppedGuard extends Monitor.Guard {
      IsStoppedGuard() {
         super(AbstractService.this.monitor);
      }

      @Override
      public boolean isSatisfied() {
         return AbstractService.this.state().compareTo(Service.State.TERMINATED) >= 0;
      }
   }

   private static final class StateSnapshot {
      final Service.State state;
      final boolean shutdownWhenStartupFinishes;
      final Throwable failure;

      StateSnapshot(Service.State var1) {
         this(var1, false, null);
      }

      StateSnapshot(Service.State var1, boolean var2, Throwable var3) {
         Preconditions.checkArgument(
            !var2 || var1 == Service.State.STARTING, "shutdownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", var1
         );
         Preconditions.checkArgument(
            var3 != null == (var1 == Service.State.FAILED),
            "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.",
            var1,
            var3
         );
         this.state = var1;
         this.shutdownWhenStartupFinishes = var2;
         this.failure = var3;
      }

      Service.State externalState() {
         return this.shutdownWhenStartupFinishes && this.state == Service.State.STARTING ? Service.State.STOPPING : this.state;
      }

      Throwable failureCause() {
         Preconditions.checkState(this.state == Service.State.FAILED, "failureCause() is only valid if the service has failed, service is %s", this.state);
         return Objects.requireNonNull(this.failure);
      }
   }
}
