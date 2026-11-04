package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

public class SimpleOptionNameMap<V> implements OptionNameMap<V> {
   private final Map<String, V> map = new HashMap<>();

   @Override
   public boolean contains(String var1) {
      return this.map.containsKey(var1);
   }

   @Override
   public V get(String var1) {
      return this.map.get(var1);
   }

   @Override
   public void put(String var1, V var2) {
      this.map.put(var1, (V)var2);
   }

   @Override
   public void putAll(Iterable<String> var1, V var2) {
      for (String var4 : var1) {
         this.map.put(var4, (V)var2);
      }
   }

   @Override
   public void remove(String var1) {
      this.map.remove(var1);
   }

   @Override
   public Map<String, V> toJavaUtilMap() {
      return new HashMap<>(this.map);
   }
}
