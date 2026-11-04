package org.eclipse.aether;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class DefaultSessionData implements SessionData {
   private final ConcurrentMap<Object, Object> data = new ConcurrentHashMap<>();

   @Override
   public void set(Object var1, Object var2) {
      Objects.requireNonNull(var1, "key cannot be null");
      if (var2 != null) {
         this.data.put(var1, var2);
      } else {
         this.data.remove(var1);
      }
   }

   @Override
   public boolean set(Object var1, Object var2, Object var3) {
      Objects.requireNonNull(var1, "key cannot be null");
      if (var3 != null) {
         return var2 == null ? this.data.putIfAbsent(var1, var3) == null : this.data.replace(var1, var2, var3);
      } else {
         return var2 == null ? !this.data.containsKey(var1) : this.data.remove(var1, var2);
      }
   }

   @Override
   public Object get(Object var1) {
      Objects.requireNonNull(var1, "key cannot be null");
      return this.data.get(var1);
   }
}
