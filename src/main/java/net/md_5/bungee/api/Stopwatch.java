package net.md_5.bungee.api;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Stopwatch<K> {
   private Map<K, Long> startTimeMap = new ConcurrentHashMap<>();
   private Map<K, Long> endTimeMap = new ConcurrentHashMap<>();

   public void start(K var1) {
      this.startTimeMap.put((K)var1, System.nanoTime());
   }

   public void stop(K var1) {
      this.endTimeMap.put((K)var1, System.nanoTime());
   }

   public SortedSet<Entry<K, Double>> getDeltas() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.startTimeMap.entrySet()) {
         Long var4 = this.endTimeMap.get(var3.getKey());
         if (var4 != null) {
            var1.put(var3.getKey(), (double)(var4 - (Long)var3.getValue()) / 1000000.0);
         }
      }

      TreeSet var5 = new TreeSet<>((var0, var1x) -> Double.compare((Double)var1x.getValue(), (Double)var0.getValue()));
      var5.addAll(var1.entrySet());
      return var5;
   }
}
