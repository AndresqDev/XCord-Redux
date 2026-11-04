package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.sisu.BeanEntry;

final class BeanCache<Q extends Annotation, T> extends AtomicReference<Object> {
   private static final long serialVersionUID = 1L;
   private Map<Binding<T>, BeanEntry<Q, T>> readCache;
   private volatile boolean mutated;

   public BeanEntry<Q, T> create(Q var1, Binding<T> var2, int var3) {
      LazyBeanEntry var4;
      Object var5;
      Object var6;
      do {
         var5 = this.get();
         if (var5 == null) {
            var6 = var4 = new LazyBeanEntry<>(var1, var2, var3);
         } else {
            if (!(var5 instanceof LazyBeanEntry)) {
               synchronized (this) {
                  Map var8 = (Map)var5;
                  if ((var4 = (LazyBeanEntry)var8.get(var2)) == null) {
                     var8.put(var2, var4 = new LazyBeanEntry<>(var1, var2, var3));
                     this.mutated = true;
                  }

                  return var4;
               }
            }

            LazyBeanEntry var7 = (LazyBeanEntry)var5;
            if (var2 == var7.binding) {
               return var7;
            }

            var6 = createMap(var7, var4 = new LazyBeanEntry<>(var1, var2, var3));
         }
      } while (!this.compareAndSet(var5, var6));

      if (var6 instanceof IdentityHashMap) {
         this.mutated = true;
      }

      return var4;
   }

   public Map<Binding<T>, BeanEntry<Q, T>> flush() {
      if (this.mutated) {
         synchronized (this) {
            if (this.mutated) {
               this.readCache = (Map<Binding<T>, BeanEntry<Q, T>>)((IdentityHashMap)this.get()).clone();
               this.mutated = false;
            }
         }
      }

      return this.readCache;
   }

   public Iterable<Binding<T>> bindings() {
      Object var1 = this.get();
      if (var1 == null) {
         return Collections.EMPTY_SET;
      } else if (var1 instanceof LazyBeanEntry) {
         return Collections.singleton(((LazyBeanEntry)var1).binding);
      } else {
         synchronized (this) {
            return new ArrayList<>(((Map)var1).keySet());
         }
      }
   }

   public BeanEntry<Q, T> remove(Binding<T> var1) {
      while (true) {
         Object var3 = this.get();
         if (var3 == null) {
            return null;
         }

         if (var3 instanceof LazyBeanEntry) {
            LazyBeanEntry var7 = (LazyBeanEntry)var3;
            if (var1 != var7.binding) {
               return null;
            }

            Object var4 = null;
            if (!this.compareAndSet(var3, var4)) {
               continue;
            }

            return var7;
         }

         synchronized (this) {
            LazyBeanEntry var2 = (LazyBeanEntry)((Map)var3).remove(var1);
            if (var2 != null) {
               this.mutated = true;
            }

            return var2;
         }
      }
   }

   private static Map createMap(LazyBeanEntry var0, LazyBeanEntry var1) {
      IdentityHashMap var2 = new IdentityHashMap(10);
      var2.put(var0.binding, var0);
      var2.put(var1.binding, var1);
      return var2;
   }
}
