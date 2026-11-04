package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>> {
   private final T route;
   private final Set<E> leased;
   private final LinkedList<E> available;
   private final LinkedList<Future<E>> pending;

   RouteSpecificPool(T var1) {
      this.route = (T)var1;
      this.leased = new HashSet<>();
      this.available = new LinkedList<>();
      this.pending = new LinkedList<>();
   }

   protected abstract E createEntry(C var1);

   public final T getRoute() {
      return this.route;
   }

   public int getLeasedCount() {
      return this.leased.size();
   }

   public int getPendingCount() {
      return this.pending.size();
   }

   public int getAvailableCount() {
      return this.available.size();
   }

   public int getAllocatedCount() {
      return this.available.size() + this.leased.size();
   }

   public E getFree(Object var1) {
      if (!this.available.isEmpty()) {
         if (var1 != null) {
            Iterator var2 = this.available.iterator();

            while (var2.hasNext()) {
               PoolEntry var3 = (PoolEntry)var2.next();
               if (var1.equals(var3.getState())) {
                  var2.remove();
                  this.leased.add((E)var3);
                  return (E)var3;
               }
            }
         }

         Iterator var4 = this.available.iterator();

         while (var4.hasNext()) {
            PoolEntry var5 = (PoolEntry)var4.next();
            if (var5.getState() == null) {
               var4.remove();
               this.leased.add((E)var5);
               return (E)var5;
            }
         }
      }

      return null;
   }

   public E getLastUsed() {
      return this.available.isEmpty() ? null : this.available.getLast();
   }

   public boolean remove(E var1) {
      Args.notNull(var1, "Pool entry");
      return this.available.remove(var1) || this.leased.remove(var1);
   }

   public void free(E var1, boolean var2) {
      Args.notNull(var1, "Pool entry");
      boolean var3 = this.leased.remove(var1);
      Asserts.check(var3, "Entry %s has not been leased from this pool", var1);
      if (var2) {
         this.available.addFirst((E)var1);
      }
   }

   public E add(C var1) {
      PoolEntry var2 = this.createEntry((C)var1);
      this.leased.add((E)var2);
      return (E)var2;
   }

   public void queue(Future<E> var1) {
      if (var1 != null) {
         this.pending.add(var1);
      }
   }

   public Future<E> nextPending() {
      return this.pending.poll();
   }

   public void unqueue(Future<E> var1) {
      if (var1 != null) {
         this.pending.remove(var1);
      }
   }

   public void shutdown() {
      for (Future var2 : this.pending) {
         var2.cancel(true);
      }

      this.pending.clear();

      for (PoolEntry var5 : this.available) {
         var5.close();
      }

      this.available.clear();

      for (PoolEntry var6 : this.leased) {
         var6.close();
      }

      this.leased.clear();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[route: ");
      var1.append(this.route);
      var1.append("][leased: ");
      var1.append(this.leased.size());
      var1.append("][available: ");
      var1.append(this.available.size());
      var1.append("][pending: ");
      var1.append(this.pending.size());
      var1.append("]");
      return var1.toString();
   }
}
