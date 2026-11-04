package org.eclipse.sisu.inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;
import xcord.Inject;

@Singleton
public final class DefaultBeanLocator implements MutableBeanLocator {
   private final RankedSequence<BindingPublisher> publishers = new RankedSequence<>();
   private final ConcurrentMap<Long, RankedBindings> cachedBindings = Weak.concurrentValues(256, 8);
   private final Map<WatchedBeans, Object> cachedWatchers = Weak.values();
   private final ImplicitBindings implicitBindings = new ImplicitBindings(this.publishers);
   private final Long[] typeIdHolder = new Long[1];

   @Override
   public Iterable<BeanEntry> locate(Key var1) {
      TypeLiteral var2 = var1.getTypeLiteral();
      RankedBindings var3 = this.fetchBindings(var2, null);
      if (var3 == null) {
         synchronized (this.cachedBindings) {
            var3 = this.fetchBindings(var2, this.typeIdHolder);
            if (var3 == null) {
               var3 = new RankedBindings(var2, this.publishers);
               this.cachedBindings.put(this.typeIdHolder[0], var3);
            }
         }
      }

      boolean var6 = var1.getAnnotationType() == null && TypeArguments.isImplicit(var2);
      return new LocatedBeans(var1, var3, var6 ? this.implicitBindings : null);
   }

   @Override
   public synchronized void watch(Key var1, Mediator var2, Object var3) {
      WatchedBeans var4 = new WatchedBeans<>(var1, var2, var3);

      for (BindingPublisher var5 : this.publishers()) {
         var5.subscribe(var4);
      }

      this.cachedWatchers.put(var4, var3);
   }

   @Override
   public synchronized boolean add(BindingPublisher var1) {
      if (this.publishers.contains(var1)) {
         return false;
      } else {
         Logs.trace("Add publisher: {}", var1, null);
         synchronized (this.cachedBindings) {
            int var3 = var1.maxBindingRank();
            this.publishers.insert(var1, var3);

            for (RankedBindings var4 : this.cachedBindings.values()) {
               var4.add(var1, var3);
            }
         }

         for (WatchedBeans var7 : new ArrayList<>(this.cachedWatchers.keySet())) {
            var1.subscribe(var7);
         }

         return true;
      }
   }

   @Override
   public synchronized boolean remove(BindingPublisher var1) {
      BindingPublisher var2;
      synchronized (this.cachedBindings) {
         var2 = this.publishers.remove(var1);
         if (var2 == null) {
            return false;
         }

         Logs.trace("Remove publisher: {}", var2, null);

         for (RankedBindings var4 : this.cachedBindings.values()) {
            var4.remove(var2);
         }
      }

      for (WatchedBeans var7 : this.cachedWatchers.keySet()) {
         var2.unsubscribe(var7);
      }

      ((MildConcurrentValues)this.cachedBindings).compact();
      return true;
   }

   @Override
   public Iterable<BindingPublisher> publishers() {
      return this.publishers.snapshot();
   }

   @Override
   public synchronized void clear() {
      for (BindingPublisher var1 : this.publishers()) {
         this.remove(var1);
      }
   }

   @Override
   public void add(Injector var1, int var2) {
      this.add(new InjectorBindings(var1, new DefaultRankingFunction(var2)));
   }

   @Override
   public void remove(Injector var1) {
      this.remove(new InjectorBindings(var1, null));
   }

   private RankedBindings fetchBindings(TypeLiteral var1, Long[] var2) {
      int var3 = System.identityHashCode(var1.getRawType().getClassLoader());
      long var4 = (long)var1.hashCode() << 32 | 4294967295L & (long)var3;

      RankedBindings var6;
      while ((var6 = this.cachedBindings.get(Long.valueOf(var4))) != null && !var1.equals(var6.type())) {
         var4++;
      }

      if (var2 != null) {
         var2[0] = var4;
      }

      return var6;
   }

   @Inject
   void autoPublish(Injector var1) {
      staticAutoPublish(this, var1);
   }

   @Inject
   static void staticAutoPublish(MutableBeanLocator var0, Injector var1) {
      var0.add(new InjectorBindings(var1));
   }
}
