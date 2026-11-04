package org.eclipse.aether.internal.impl.collect;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

class ObjectPool<T> {
   private final Map<Object, Reference<T>> objects = new WeakHashMap<>(256);

   public synchronized T intern(T var1) {
      Reference var2 = this.objects.get(var1);
      if (var2 != null) {
         Object var3 = var2.get();
         if (var3 != null) {
            return (T)var3;
         }
      }

      this.objects.put(var1, new WeakReference<>((T)var1));
      return (T)var1;
   }
}
