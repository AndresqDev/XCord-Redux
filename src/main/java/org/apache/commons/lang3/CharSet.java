package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CharSet implements Serializable {
   private static final long serialVersionUID = 5947847346149275958L;
   public static final CharSet EMPTY = new CharSet((String)null);
   public static final CharSet ASCII_ALPHA = new CharSet("a-zA-Z");
   public static final CharSet ASCII_ALPHA_LOWER = new CharSet("a-z");
   public static final CharSet ASCII_ALPHA_UPPER = new CharSet("A-Z");
   public static final CharSet ASCII_NUMERIC = new CharSet("0-9");
   protected static final Map<String, CharSet> COMMON = Collections.synchronizedMap(new HashMap<>());
   private final Set<CharRange> set = Collections.synchronizedSet(new HashSet<>());

   public static CharSet getInstance(String... var0) {
      if (var0 == null) {
         return null;
      } else {
         if (var0.length == 1) {
            CharSet var1 = COMMON.get(var0[0]);
            if (var1 != null) {
               return var1;
            }
         }

         return new CharSet(var0);
      }
   }

   protected CharSet(String... var1) {
      for (String var5 : var1) {
         this.add(var5);
      }
   }

   protected void add(String var1) {
      if (var1 != null) {
         int var2 = var1.length();
         int var3 = 0;

         while (var3 < var2) {
            int var4 = var2 - var3;
            if (var4 >= 4 && var1.charAt(var3) == '^' && var1.charAt(var3 + 2) == '-') {
               this.set.add(CharRange.isNotIn(var1.charAt(var3 + 1), var1.charAt(var3 + 3)));
               var3 += 4;
            } else if (var4 >= 3 && var1.charAt(var3 + 1) == '-') {
               this.set.add(CharRange.isIn(var1.charAt(var3), var1.charAt(var3 + 2)));
               var3 += 3;
            } else if (var4 >= 2 && var1.charAt(var3) == '^') {
               this.set.add(CharRange.isNot(var1.charAt(var3 + 1)));
               var3 += 2;
            } else {
               this.set.add(CharRange.is(var1.charAt(var3)));
               var3++;
            }
         }
      }
   }

   CharRange[] getCharRanges() {
      return this.set.toArray(new CharRange[this.set.size()]);
   }

   public boolean contains(char var1) {
      for (CharRange var3 : this.set) {
         if (var3.contains(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CharSet)) {
         return false;
      } else {
         CharSet var2 = (CharSet)var1;
         return this.set.equals(var2.set);
      }
   }

   @Override
   public int hashCode() {
      return 89 + this.set.hashCode();
   }

   @Override
   public String toString() {
      return this.set.toString();
   }

   static {
      COMMON.put(null, EMPTY);
      COMMON.put("", EMPTY);
      COMMON.put("a-zA-Z", ASCII_ALPHA);
      COMMON.put("A-Za-z", ASCII_ALPHA);
      COMMON.put("a-z", ASCII_ALPHA_LOWER);
      COMMON.put("A-Z", ASCII_ALPHA_UPPER);
      COMMON.put("0-9", ASCII_NUMERIC);
   }
}
