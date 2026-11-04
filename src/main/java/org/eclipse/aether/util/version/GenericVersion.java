package org.eclipse.aether.util.version;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.aether.version.Version;

final class GenericVersion implements Version {
   private final String version;
   private final GenericVersion.Item[] items;
   private final int hash;

   GenericVersion(String var1) {
      this.version = var1;
      this.items = parse(var1);
      this.hash = Arrays.hashCode((Object[])this.items);
   }

   private static GenericVersion.Item[] parse(String var0) {
      ArrayList var1 = new ArrayList();
      GenericVersion.Tokenizer var2 = new GenericVersion.Tokenizer(var0);

      while (var2.next()) {
         GenericVersion.Item var3 = var2.toItem();
         var1.add(var3);
      }

      trimPadding(var1);
      return var1.toArray(new GenericVersion.Item[var1.size()]);
   }

   private static void trimPadding(List<GenericVersion.Item> var0) {
      Boolean var1 = null;
      int var2 = var0.size() - 1;

      for (int var3 = var2; var3 > 0; var3--) {
         GenericVersion.Item var4 = (GenericVersion.Item)var0.get(var3);
         if (!Boolean.valueOf(var4.isNumber()).equals(var1)) {
            var2 = var3;
            var1 = var4.isNumber();
         }

         if (var2 == var3 && (var3 == var0.size() - 1 || ((GenericVersion.Item)var0.get(var3 - 1)).isNumber() == var4.isNumber()) && var4.compareTo(null) == 0) {
            var0.remove(var3);
            var2--;
         }
      }
   }

   public int compareTo(Version var1) {
      GenericVersion.Item[] var2 = this.items;
      GenericVersion.Item[] var3 = ((GenericVersion)var1).items;
      boolean var4 = true;

      for (int var5 = 0; var5 < var2.length || var5 < var3.length; var5++) {
         if (var5 >= var2.length) {
            return -comparePadding(var3, var5, null);
         }

         if (var5 >= var3.length) {
            return comparePadding(var2, var5, null);
         }

         GenericVersion.Item var6 = var2[var5];
         GenericVersion.Item var7 = var3[var5];
         if (var6.isNumber() != var7.isNumber()) {
            if (var4 == var6.isNumber()) {
               return comparePadding(var2, var5, var4);
            }

            return -comparePadding(var3, var5, var4);
         }

         int var8 = var6.compareTo(var7);
         if (var8 != 0) {
            return var8;
         }

         var4 = var6.isNumber();
      }

      return 0;
   }

   private static int comparePadding(GenericVersion.Item[] var0, int var1, Boolean var2) {
      int var3 = 0;

      for (int var4 = var1; var4 < var0.length; var4++) {
         GenericVersion.Item var5 = var0[var4];
         if (var2 != null && var2 != var5.isNumber()) {
            break;
         }

         var3 = var5.compareTo(null);
         if (var3 != 0) {
            break;
         }
      }

      return var3;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof GenericVersion && this.compareTo((Version)((GenericVersion)var1)) == 0;
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   @Override
   public String toString() {
      return this.version;
   }

   static final class Item {
      static final int KIND_MAX = 8;
      static final int KIND_BIGINT = 5;
      static final int KIND_INT = 4;
      static final int KIND_STRING = 3;
      static final int KIND_QUALIFIER = 2;
      static final int KIND_MIN = 0;
      static final GenericVersion.Item MAX = new GenericVersion.Item(8, "max");
      static final GenericVersion.Item MIN = new GenericVersion.Item(0, "min");
      private final int kind;
      private final Object value;

      Item(int var1, Object var2) {
         this.kind = var1;
         this.value = var2;
      }

      public boolean isNumber() {
         return (this.kind & 2) == 0;
      }

      public int compareTo(GenericVersion.Item var1) {
         int var2;
         if (var1 == null) {
            switch (this.kind) {
               case 0:
                  var2 = -1;
                  break;
               case 1:
               case 6:
               case 7:
               default:
                  throw new IllegalStateException("unknown version item kind " + this.kind);
               case 2:
               case 4:
                  var2 = (Integer)this.value;
                  break;
               case 3:
               case 5:
               case 8:
                  var2 = 1;
            }
         } else {
            var2 = this.kind - var1.kind;
            if (var2 == 0) {
               switch (this.kind) {
                  case 0:
                  case 8:
                     break;
                  case 1:
                  case 6:
                  case 7:
                  default:
                     throw new IllegalStateException("unknown version item kind " + this.kind);
                  case 2:
                  case 4:
                     var2 = ((Integer)this.value).compareTo((Integer)var1.value);
                     break;
                  case 3:
                     var2 = ((String)this.value).compareToIgnoreCase((String)var1.value);
                     break;
                  case 5:
                     var2 = ((BigInteger)this.value).compareTo((BigInteger)var1.value);
               }
            }
         }

         return var2;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof GenericVersion.Item && this.compareTo((GenericVersion.Item)var1) == 0;
      }

      @Override
      public int hashCode() {
         return this.value.hashCode() + this.kind * 31;
      }

      @Override
      public String toString() {
         return String.valueOf(this.value);
      }
   }

   static final class Tokenizer {
      private static final Integer QUALIFIER_ALPHA = -5;
      private static final Integer QUALIFIER_BETA = -4;
      private static final Integer QUALIFIER_MILESTONE = -3;
      private static final Map<String, Integer> QUALIFIERS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      private final String version;
      private int index;
      private String token;
      private boolean number;
      private boolean terminatedByNumber;

      Tokenizer(String var1) {
         this.version = var1.length() > 0 ? var1 : "0";
      }

      public boolean next() {
         int var1 = this.version.length();
         if (this.index >= var1) {
            return false;
         } else {
            int var2 = -2;
            int var3 = this.index;
            int var4 = var1;

            for (this.terminatedByNumber = false; this.index < var1; this.index++) {
               char var5 = this.version.charAt(this.index);
               if (var5 == '.' || var5 == '-' || var5 == '_') {
                  var4 = this.index++;
                  break;
               }

               int var6 = Character.digit(var5, 10);
               if (var6 >= 0) {
                  if (var2 == -1) {
                     var4 = this.index;
                     this.terminatedByNumber = true;
                     break;
                  }

                  if (var2 == 0) {
                     var3++;
                  }

                  var2 = var2 <= 0 && var6 <= 0 ? 0 : 1;
               } else {
                  if (var2 >= 0) {
                     var4 = this.index;
                     break;
                  }

                  var2 = -1;
               }
            }

            if (var4 - var3 > 0) {
               this.token = this.version.substring(var3, var4);
               this.number = var2 >= 0;
            } else {
               this.token = "0";
               this.number = true;
            }

            return true;
         }
      }

      @Override
      public String toString() {
         return String.valueOf(this.token);
      }

      public GenericVersion.Item toItem() {
         if (this.number) {
            try {
               return this.token.length() < 10
                  ? new GenericVersion.Item(4, Integer.parseInt(this.token))
                  : new GenericVersion.Item(5, new BigInteger(this.token));
            } catch (NumberFormatException var2) {
               throw new IllegalStateException(var2);
            }
         } else {
            if (this.index >= this.version.length()) {
               if ("min".equalsIgnoreCase(this.token)) {
                  return GenericVersion.Item.MIN;
               }

               if ("max".equalsIgnoreCase(this.token)) {
                  return GenericVersion.Item.MAX;
               }
            }

            if (this.terminatedByNumber && this.token.length() == 1) {
               switch (this.token.charAt(0)) {
                  case 'A':
                  case 'a':
                     return new GenericVersion.Item(2, QUALIFIER_ALPHA);
                  case 'B':
                  case 'b':
                     return new GenericVersion.Item(2, QUALIFIER_BETA);
                  case 'M':
                  case 'm':
                     return new GenericVersion.Item(2, QUALIFIER_MILESTONE);
               }
            }

            Integer var1 = QUALIFIERS.get(this.token);
            return var1 != null ? new GenericVersion.Item(2, var1) : new GenericVersion.Item(3, this.token.toLowerCase(Locale.ENGLISH));
         }
      }

      static {
         QUALIFIERS.put("alpha", QUALIFIER_ALPHA);
         QUALIFIERS.put("beta", QUALIFIER_BETA);
         QUALIFIERS.put("milestone", QUALIFIER_MILESTONE);
         QUALIFIERS.put("cr", -2);
         QUALIFIERS.put("rc", -2);
         QUALIFIERS.put("snapshot", -1);
         QUALIFIERS.put("ga", 0);
         QUALIFIERS.put("final", 0);
         QUALIFIERS.put("release", 0);
         QUALIFIERS.put("", 0);
         QUALIFIERS.put("sp", 1);
      }
   }
}
