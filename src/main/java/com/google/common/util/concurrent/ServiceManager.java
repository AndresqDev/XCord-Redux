package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class ServiceManager implements ServiceManagerBridge {
   private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());
   private static final ListenerCallQueue.Event<ServiceManager.Listener> HEALTHY_EVENT = new ListenerCallQueue.Event<ServiceManager.Listener>() {
      public void call(ServiceManager.Listener var1) {
         var1.healthy();
      }

      @Override
      public String toString() {
         return "healthy()";
      }
   };
   private static final ListenerCallQueue.Event<ServiceManager.Listener> STOPPED_EVENT = new ListenerCallQueue.Event<ServiceManager.Listener>() {
      public void call(ServiceManager.Listener var1) {
         var1.stopped();
      }

      @Override
      public String toString() {
         return "stopped()";
      }
   };
   private final ServiceManager.ServiceManagerState state;
   private final ImmutableList<Service> services;

   public ServiceManager(Iterable<? extends Service> var1) {
      ImmutableList var2 = ImmutableList.copyOf(var1);
      if (var2.isEmpty()) {
         logger.log(
            Level.WARNING,
            "ServiceManager configured with no services.  Is your application configured properly?",
            (Throwable)(new ServiceManager.EmptyServiceManagerWarning())
         );
         var2 = ImmutableList.of(new ServiceManager.NoOpService());
      }

      this.state = new ServiceManager.ServiceManagerState(var2);
      this.services = var2;
      WeakReference var3 = new WeakReference<>(this.state);

      for (Service var5 : var2) {
         var5.addListener(new ServiceManager.ServiceListener(var5, var3), MoreExecutors.directExecutor());
         Preconditions.checkArgument(var5.state() == Service.State.NEW, "Can only manage NEW services, %s", var5);
      }

      this.state.markReady();
   }

   public void addListener(ServiceManager.Listener var1, Executor var2) {
      this.state.addListener(var1, var2);
   }

   @CanIgnoreReturnValue
   public ServiceManager startAsync() {
      for (Service var2 : this.services) {
         Service.State var3 = var2.state();
         Preconditions.checkState(var3 == Service.State.NEW, "Service %s is %s, cannot start it.", var2, var3);
      }

      for (Service var7 : this.services) {
         try {
            this.state.tryStartTiming(var7);
            var7.startAsync();
         } catch (IllegalStateException var5) {
            Logger var10000 = logger;
            Level var10001 = Level.WARNING;
            String var4 = String.valueOf(var7);
            var10000.log(
               var10001, new StringBuilder(24 + String.valueOf(var4).length()).append("Unable to start Service ").append(var4).toString(), (Throwable)var5
            );
         }
      }

      return this;
   }

   public void awaitHealthy() {
      this.state.awaitHealthy();
   }

   public void awaitHealthy(Duration var1) throws TimeoutException {
      this.awaitHealthy(Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   public void awaitHealthy(long var1, TimeUnit var3) throws TimeoutException {
      this.state.awaitHealthy(var1, var3);
   }

   @CanIgnoreReturnValue
   public ServiceManager stopAsync() {
      for (Service var2 : this.services) {
         var2.stopAsync();
      }

      return this;
   }

   public void awaitStopped() {
      this.state.awaitStopped();
   }

   public void awaitStopped(Duration var1) throws TimeoutException {
      this.awaitStopped(Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   public void awaitStopped(long var1, TimeUnit var3) throws TimeoutException {
      this.state.awaitStopped(var1, var3);
   }

   public boolean isHealthy() {
      for (Service var2 : this.services) {
         if (!var2.isRunning()) {
            return false;
         }
      }

      return true;
   }

   public ImmutableSetMultimap<Service.State, Service> servicesByState() {
      return this.state.servicesByState();
   }

   public ImmutableMap<Service, Long> startupTimes() {
      return this.state.startupTimes();
   }

   public ImmutableMap<Service, Duration> startupDurations() {
      return ImmutableMap.copyOf(Maps.transformValues(this.startupTimes(), Duration::ofMillis));
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(ServiceManager.class)
         .add("services", Collections2.filter(this.services, Predicates.not(Predicates.instanceOf(ServiceManager.NoOpService.class))))
         .toString();
   }

   private static final class EmptyServiceManagerWarning extends Throwable {
      private EmptyServiceManagerWarning() {
      }
   }

   private static final class FailedService extends Throwable {
      FailedService(Service var1) {
         super(var1.toString(), var1.failureCause(), false, false);
      }
   }

   public abstract static class Listener {
      public void healthy() {
      }

      public void stopped() {
      }

      public void failure(Service var1) {
      }
   }

   private static final class NoOpService extends AbstractService {
      private NoOpService() {
      }

      @Override
      protected void doStart() {
         this.notifyStarted();
      }

      @Override
      protected void doStop() {
         this.notifyStopped();
      }
   }

   private static final class ServiceListener extends Service.Listener {
      final Service service;
      final WeakReference<ServiceManager.ServiceManagerState> state;

      ServiceListener(Service var1, WeakReference<ServiceManager.ServiceManagerState> var2) {
         this.service = var1;
         this.state = var2;
      }

      @Override
      public void starting() {
         ServiceManager.ServiceManagerState var1 = this.state.get();
         if (var1 != null) {
            var1.transitionService(this.service, Service.State.NEW, Service.State.STARTING);
            if (!(this.service instanceof ServiceManager.NoOpService)) {
               ServiceManager.logger.log(Level.FINE, "Starting {0}.", this.service);
            }
         }
      }

      @Override
      public void running() {
         ServiceManager.ServiceManagerState var1 = this.state.get();
         if (var1 != null) {
            var1.transitionService(this.service, Service.State.STARTING, Service.State.RUNNING);
         }
      }

      @Override
      public void stopping(Service.State var1) {
         ServiceManager.ServiceManagerState var2 = this.state.get();
         if (var2 != null) {
            var2.transitionService(this.service, var1, Service.State.STOPPING);
         }
      }

      @Override
      public void terminated(Service.State var1) {
         ServiceManager.ServiceManagerState var2 = this.state.get();
         if (var2 != null) {
            if (!(this.service instanceof ServiceManager.NoOpService)) {
               ServiceManager.logger.log(Level.FINE, "Service {0} has terminated. Previous state was: {1}", new Object[]{this.service, var1});
            }

            var2.transitionService(this.service, var1, Service.State.TERMINATED);
         }
      }

      @Override
      public void failed(Service.State var1, Throwable var2) {
         ServiceManager.ServiceManagerState var3 = this.state.get();
         if (var3 != null) {
            boolean var4 = !(this.service instanceof ServiceManager.NoOpService);
            var4 &= var1 != Service.State.STARTING;
            if (var4) {
               Logger var10000 = ServiceManager.logger;
               Level var10001 = Level.SEVERE;
               String var5 = String.valueOf(this.service);
               String var6 = String.valueOf(var1);
               var10000.log(
                  var10001,
                  new StringBuilder(34 + String.valueOf(var5).length() + String.valueOf(var6).length())
                     .append("Service ")
                     .append(var5)
                     .append(" has failed in the ")
                     .append(var6)
                     .append(" state.")
                     .toString(),
                  var2
               );
            }

            var3.transitionService(this.service, var1, Service.State.FAILED);
         }
      }
   }

   private static final class ServiceManagerState {
      final Monitor monitor = new Monitor();
      @GuardedBy("monitor")
      final SetMultimap<Service.State, Service> servicesByState = MultimapBuilder.enumKeys(Service.State.class).linkedHashSetValues().build();
      @GuardedBy("monitor")
      final Multiset<Service.State> states = this.servicesByState.keys();
      @GuardedBy("monitor")
      final Map<Service, Stopwatch> startupTimers = Maps.newIdentityHashMap();
      @GuardedBy("monitor")
      boolean ready;
      @GuardedBy("monitor")
      boolean transitioned;
      final int numberOfServices;
      final Monitor.Guard awaitHealthGuard = new ServiceManager.ServiceManagerState.AwaitHealthGuard();
      final Monitor.Guard stoppedGuard = new ServiceManager.ServiceManagerState.StoppedGuard();
      final ListenerCallQueue<ServiceManager.Listener> listeners = new ListenerCallQueue<>();

      ServiceManagerState(ImmutableCollection<Service> var1) {
         this.numberOfServices = var1.size();
         this.servicesByState.putAll(Service.State.NEW, var1);
      }

      void tryStartTiming(Service var1) {
         this.monitor.enter();

         try {
            Stopwatch var2 = this.startupTimers.get(var1);
            if (var2 == null) {
               this.startupTimers.put(var1, Stopwatch.createStarted());
            }
         } finally {
            this.monitor.leave();
         }
      }

      void markReady() {
         this.monitor.enter();

         try {
            if (this.transitioned) {
               ArrayList var1 = Lists.newArrayList();

               for (Service var3 : this.servicesByState().values()) {
                  if (var3.state() != Service.State.NEW) {
                     var1.add(var3);
                  }
               }

               String var7 = String.valueOf(var1);
               throw new IllegalArgumentException(
                  new StringBuilder(89 + String.valueOf(var7).length())
                     .append("Services started transitioning asynchronously before the ServiceManager was constructed: ")
                     .append(var7)
                     .toString()
               );
            }

            this.ready = true;
         } finally {
            this.monitor.leave();
         }
      }

      void addListener(ServiceManager.Listener var1, Executor var2) {
         this.listeners.addListener(var1, var2);
      }

      void awaitHealthy() {
         this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);

         try {
            this.checkHealthy();
         } finally {
            this.monitor.leave();
         }
      }

      void awaitHealthy(long var1, TimeUnit var3) throws TimeoutException {
         this.monitor.enter();

         try {
            if (!this.monitor.waitForUninterruptibly(this.awaitHealthGuard, var1, var3)) {
               String var4 = String.valueOf(
                  Multimaps.filterKeys(this.servicesByState, Predicates.in(ImmutableSet.of(Service.State.NEW, Service.State.STARTING)))
               );
               throw new TimeoutException(
                  new StringBuilder(93 + String.valueOf(var4).length())
                     .append("Timeout waiting for the services to become healthy. The following services have not started: ")
                     .append(var4)
                     .toString()
               );
            }

            this.checkHealthy();
         } finally {
            this.monitor.leave();
         }
      }

      void awaitStopped() {
         this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
         this.monitor.leave();
      }

      void awaitStopped(long var1, TimeUnit var3) throws TimeoutException {
         this.monitor.enter();

         try {
            if (!this.monitor.waitForUninterruptibly(this.stoppedGuard, var1, var3)) {
               String var4 = String.valueOf(
                  Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.in(EnumSet.of(Service.State.TERMINATED, Service.State.FAILED))))
               );
               throw new TimeoutException(
                  new StringBuilder(83 + String.valueOf(var4).length())
                     .append("Timeout waiting for the services to stop. The following services have not stopped: ")
                     .append(var4)
                     .toString()
               );
            }
         } finally {
            this.monitor.leave();
         }
      }

      ImmutableSetMultimap<Service.State, Service> servicesByState() {
         ImmutableSetMultimap.Builder var1 = ImmutableSetMultimap.builder();
         this.monitor.enter();

         try {
            for (Entry var3 : this.servicesByState.entries()) {
               if (!(var3.getValue() instanceof ServiceManager.NoOpService)) {
                  var1.put(var3);
               }
            }
         } finally {
            this.monitor.leave();
         }

         return var1.build();
      }

      ImmutableMap<Service, Long> startupTimes() {
         this.monitor.enter();

         ArrayList var1;
         try {
            var1 = Lists.newArrayListWithCapacity(this.startupTimers.size());

            for (Entry var3 : this.startupTimers.entrySet()) {
               Service var4 = (Service)var3.getKey();
               Stopwatch var5 = (Stopwatch)var3.getValue();
               if (!var5.isRunning() && !(var4 instanceof ServiceManager.NoOpService)) {
                  var1.add(Maps.immutableEntry(var4, var5.elapsed(TimeUnit.MILLISECONDS)));
               }
            }
         } finally {
            this.monitor.leave();
         }

         Collections.sort(var1, Ordering.<Long>natural().onResultOf(new Function<Entry<Service, Long>, Long>(this) {
            public Long apply(Entry<Service, Long> var1) {
               return (Long)var1.getValue();
            }
         }));
         return ImmutableMap.copyOf(var1);
      }

      void transitionService(Service var1, Service.State var2, Service.State var3) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkArgument(var2 != var3);
         this.monitor.enter();

         try {
            this.transitioned = true;
            if (!this.ready) {
               return;
            }

            Preconditions.checkState(this.servicesByState.remove(var2, var1), "Service %s not at the expected location in the state map %s", var1, var2);
            Preconditions.checkState(this.servicesByState.put(var3, var1), "Service %s in the state map unexpectedly at %s", var1, var3);
            Stopwatch var4 = this.startupTimers.get(var1);
            if (var4 == null) {
               var4 = Stopwatch.createStarted();
               this.startupTimers.put(var1, var4);
            }

            if (var3.compareTo(Service.State.RUNNING) >= 0 && var4.isRunning()) {
               var4.stop();
               if (!(var1 instanceof ServiceManager.NoOpService)) {
                  ServiceManager.logger.log(Level.FINE, "Started {0} in {1}.", new Object[]{var1, var4});
               }
            }

            if (var3 == Service.State.FAILED) {
               this.enqueueFailedEvent(var1);
            }

            if (this.states.count(Service.State.RUNNING) == this.numberOfServices) {
               this.enqueueHealthyEvent();
            } else if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) == this.numberOfServices) {
               this.enqueueStoppedEvent();
            }
         } finally {
            this.monitor.leave();
            this.dispatchListenerEvents();
         }
      }

      void enqueueStoppedEvent() {
         this.listeners.enqueue(ServiceManager.STOPPED_EVENT);
      }

      void enqueueHealthyEvent() {
         this.listeners.enqueue(ServiceManager.HEALTHY_EVENT);
      }

      void enqueueFailedEvent(final Service var1) {
         this.listeners.enqueue(new ListenerCallQueue.Event<ServiceManager.Listener>(this) {
            public void call(ServiceManager.Listener var1x) {
               var1x.failure(var1);
            }

            @Override
            public String toString() {
               String var1x = String.valueOf(var1);
               return new StringBuilder(18 + String.valueOf(var1x).length()).append("failed({service=").append(var1x).append("})").toString();
            }
         });
      }

      void dispatchListenerEvents() {
         Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
         this.listeners.dispatch();
      }

      @GuardedBy("monitor")
      void checkHealthy() {
         if (this.states.count(Service.State.RUNNING) != this.numberOfServices) {
            String var2 = String.valueOf(Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.equalTo(Service.State.RUNNING))));
            IllegalStateException var1 = new IllegalStateException(
               new StringBuilder(79 + String.valueOf(var2).length())
                  .append("Expected to be healthy after starting. The following services are not running: ")
                  .append(var2)
                  .toString()
            );

            for (Service var3 : this.servicesByState.get(Service.State.FAILED)) {
               var1.addSuppressed(new ServiceManager.FailedService(var3));
            }

            throw var1;
         }
      }

      final class AwaitHealthGuard extends Monitor.Guard {
         AwaitHealthGuard() {
            super(ServiceManagerState.this.monitor);
         }

         @GuardedBy("ServiceManagerState.this.monitor")
         @Override
         public boolean isSatisfied() {
            return ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManagerState.this.numberOfServices
               || ServiceManagerState.this.states.contains(Service.State.STOPPING)
               || ServiceManagerState.this.states.contains(Service.State.TERMINATED)
               || ServiceManagerState.this.states.contains(Service.State.FAILED);
         }
      }

      final class StoppedGuard extends Monitor.Guard {
         StoppedGuard() {
            super(ServiceManagerState.this.monitor);
         }

         @GuardedBy("ServiceManagerState.this.monitor")
         @Override
         public boolean isSatisfied() {
            return ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManagerState.this.states.count(Service.State.FAILED)
               == ServiceManagerState.this.numberOfServices;
         }
      }
   }
}
