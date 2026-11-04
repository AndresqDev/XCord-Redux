package org.eclipse.aether;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultRepositoryCache implements RepositoryCache {
   private final Map<Object, Object> cache = new ConcurrentHashMap<>(256);

   @Override
   public Object get(RepositorySystemSession var1, Object var2) {
      return this.cache.get(var2);
   }

   @Override
   public void put(RepositorySystemSession var1, Object var2, Object var3) {
      if (var3 != null) {
         this.cache.put(var2, var3);
      } else {
         this.cache.remove(var2);
      }
   }
}
