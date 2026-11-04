package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionUtils {
   public static <K, V> Map<K, V> mergeMaps(Map<K, V> var0, Map<K, V> var1) {
      if (var0 == null && var1 == null) {
         return null;
      } else if (var0 != null && var1 == null) {
         return var0;
      } else if (var0 == null) {
         return var1;
      } else {
         HashMap var2 = new HashMap();
         Set var3 = var0.keySet();
         Set var4 = var1.keySet();
         Collection var5 = subtract(var4, intersection(var3, var4));
         var2.putAll(var0);

         for (Object var7 : var5) {
            var2.put(var7, var1.get(var7));
         }

         return var2;
      }
   }

   public static <K, V> Map<K, V> mergeMaps(Map<K, V>[] var0) {
      Map var1;
      if (var0.length == 0) {
         var1 = null;
      } else if (var0.length == 1) {
         var1 = var0[0];
      } else {
         var1 = mergeMaps(var0[0], var0[1]);

         for (int var2 = 2; var2 < var0.length; var2++) {
            var1 = mergeMaps(var1, var0[var2]);
         }
      }

      return var1;
   }

   public static <E> Collection<E> intersection(Collection<E> var0, Collection<E> var1) {
      ArrayList var2 = new ArrayList();
      Map var3 = getCardinalityMap(var0);
      Map var4 = getCardinalityMap(var1);
      HashSet var5 = new HashSet(var0);
      var5.addAll(var1);

      for (Object var7 : var5) {
         int var8 = 0;

         for (int var9 = Math.min(getFreq(var7, var3), getFreq(var7, var4)); var8 < var9; var8++) {
            var2.add(var7);
         }
      }

      return var2;
   }

   public static <T> Collection<T> subtract(Collection<T> var0, Collection<T> var1) {
      ArrayList var2 = new ArrayList(var0);

      for (Object var4 : var1) {
         var2.remove(var4);
      }

      return var2;
   }

   public static <E> Map<E, Integer> getCardinalityMap(Collection<E> var0) {
      HashMap var1 = new HashMap();

      for (Object var3 : var0) {
         Integer var4 = (Integer)var1.get(var3);
         if (null == var4) {
            var1.put(var3, 1);
         } else {
            var1.put(var3, var4 + 1);
         }
      }

      return var1;
   }

   public static <E> List<E> iteratorToList(Iterator<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("it cannot be null.");
      } else {
         ArrayList var1 = new ArrayList();

         while (var0.hasNext()) {
            var1.add(var0.next());
         }

         return var1;
      }
   }

   private static <E> int getFreq(E var0, Map<E, Integer> var1) {
      try {
         Integer var2 = (Integer)var1.get(var0);
         if (var2 != null) {
            return var2;
         }
      } catch (NullPointerException var3) {
      } catch (NoSuchElementException var4) {
      }

      return 0;
   }
}
