package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

final class MildConcurrentKeys<K, V> extends MildKeys<K, V> implements ConcurrentMap<K, V> {
   private final ConcurrentMap<Reference<K>, V> concurrentMap;

   MildConcurrentKeys(ConcurrentMap<Reference<K>, V> var1, boolean var2) {
      super(var1, var2);
      this.concurrentMap = var1;
   }

   @Override
   public V putIfAbsent(K var1, V var2) {
      this.compact();
      return this.concurrentMap.putIfAbsent(this.mildKey((K)var1), (V)var2);
   }

   @Override
   public V replace(K var1, V var2) {
      this.compact();
      return this.concurrentMap.replace(this.mildKey((K)var1), (V)var2);
   }

   @Override
   public boolean replace(K var1, V var2, V var3) {
      this.compact();
      return this.concurrentMap.replace(this.mildKey((K)var1), (V)var2, (V)var3);
   }

   @Override
   public boolean remove(Object var1, Object var2) {
      this.compact();
      return this.concurrentMap.remove(tempKey(var1), var2);
   }
}
