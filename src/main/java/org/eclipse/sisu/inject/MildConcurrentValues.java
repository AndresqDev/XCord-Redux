package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

final class MildConcurrentValues<K, V> extends MildValues<K, V> implements ConcurrentMap<K, V> {
   private final ConcurrentMap<K, Reference<V>> concurrentMap;

   MildConcurrentValues(ConcurrentMap<K, Reference<V>> var1, boolean var2) {
      super(var1, var2);
      this.concurrentMap = var1;
   }

   @Override
   public V putIfAbsent(K var1, V var2) {
      this.compact();
      Reference var3 = this.mildValue((K)var1, (V)var2);

      Reference var4;
      while ((var4 = this.concurrentMap.putIfAbsent((K)var1, var3)) != null) {
         Object var5 = var4.get();
         if (var5 != null) {
            return (V)var5;
         }

         this.concurrentMap.remove(var1, var4);
      }

      return null;
   }

   @Override
   public V replace(K var1, V var2) {
      this.compact();
      Reference var3 = this.concurrentMap.replace((K)var1, this.mildValue((K)var1, (V)var2));
      return (V)(var3 != null ? var3.get() : null);
   }

   @Override
   public boolean replace(K var1, V var2, V var3) {
      this.compact();
      return this.concurrentMap.replace((K)var1, tempValue((V)var2), this.mildValue((K)var1, (V)var3));
   }

   @Override
   public boolean remove(Object var1, Object var2) {
      this.compact();
      return this.concurrentMap.remove(var1, tempValue(var2));
   }

   @Override
   void compact() {
      Reference var1;
      while ((var1 = this.queue.poll()) != null) {
         this.concurrentMap.remove(((MildValues.InverseMapping)var1).key(), var1);
      }
   }
}
