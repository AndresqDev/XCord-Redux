package org.eclipse.sisu.osgi;

import java.util.Collection;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.Weak;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

final class BindingTracker<T> extends ServiceTracker<T, ServiceBinding<T>> {
   private final Collection<BindingSubscriber<T>> subscribers = Weak.elements();
   private final String clazzName;
   private final int maxRank;
   private boolean isOpen;

   BindingTracker(BundleContext var1, int var2, String var3) {
      super(var1, var3, null);
      this.clazzName = var3;
      this.maxRank = var2;
   }

   public void subscribe(BindingSubscriber<T> var1) {
      synchronized (this.subscribers) {
         this.openIfNecessary();

         for (ServiceBinding var3 : this.getTracked().values()) {
            if (var3.isCompatibleWith(var1)) {
               var1.add(var3, var3.rank());
            }
         }

         this.subscribers.add(var1);
      }
   }

   public void unsubscribe(BindingSubscriber<T> var1) {
      synchronized (this.subscribers) {
         if (this.subscribers.remove(var1)) {
            for (ServiceBinding var3 : this.getTracked().values()) {
               var1.remove(var3);
            }
         }

         this.closeIfNecessary();
      }
   }

   public ServiceBinding<T> addingService(ServiceReference<T> var1) {
      ServiceBinding var2;
      try {
         var2 = new ServiceBinding(this.context, this.clazzName, this.maxRank, var1);
      } catch (Exception var7) {
         Logs.warn("Problem subscribing to service: {}", var1, var7);
         return null;
      }

      synchronized (this.subscribers) {
         for (BindingSubscriber var4 : this.subscribers) {
            if (var2.isCompatibleWith(var4)) {
               var4.add(var2, var2.rank());
            }
         }

         this.closeIfNecessary();
         return var2;
      }
   }

   public void removedService(ServiceReference<T> var1, ServiceBinding<T> var2) {
      synchronized (this.subscribers) {
         for (BindingSubscriber var4 : this.subscribers) {
            var4.remove(var2);
         }

         this.closeIfNecessary();
      }

      super.removedService(var1, var2);
   }

   private void openIfNecessary() {
      if (!this.isOpen) {
         this.open(true);
         Logs.trace("Started tracking services: {}", this.filter, null);
         this.isOpen = true;
      }
   }

   private void closeIfNecessary() {
      if (this.isOpen && this.subscribers.isEmpty()) {
         this.isOpen = false;
         Logs.trace("Stopped tracking services: {}", this.filter, null);
         this.close();
      }
   }
}
