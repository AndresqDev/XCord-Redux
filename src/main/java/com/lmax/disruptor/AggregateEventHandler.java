package com.lmax.disruptor;

public final class AggregateEventHandler<T> implements EventHandler<T>, LifecycleAware {
   private final EventHandler<T>[] eventHandlers;

   @SafeVarargs
   public AggregateEventHandler(EventHandler<T>... var1) {
      this.eventHandlers = var1;
   }

   @Override
   public void onEvent(T var1, long var2, boolean var4) throws Exception {
      for (EventHandler var8 : this.eventHandlers) {
         var8.onEvent(var1, var2, var4);
      }
   }

   @Override
   public void onStart() {
      for (EventHandler var4 : this.eventHandlers) {
         if (var4 instanceof LifecycleAware) {
            ((LifecycleAware)var4).onStart();
         }
      }
   }

   @Override
   public void onShutdown() {
      for (EventHandler var4 : this.eventHandlers) {
         if (var4 instanceof LifecycleAware) {
            ((LifecycleAware)var4).onShutdown();
         }
      }
   }
}
