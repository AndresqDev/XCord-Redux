package org.apache.maven.artifact.versioning;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ComparableVersion implements Comparable<ComparableVersion> {
   private static final int MAX_INTITEM_LENGTH = 9;
   private static final int MAX_LONGITEM_LENGTH = 18;
   private String value;
   private String canonical;
   private ComparableVersion.ListItem items;

   public ComparableVersion(String var1) {
      this.parseVersion(var1);
   }

   public final void parseVersion(String var1) {
      this.value = var1;
      this.items = new ComparableVersion.ListItem();
      var1 = var1.toLowerCase(Locale.ENGLISH);
      ComparableVersion.ListItem var2 = this.items;
      ArrayDeque var3 = new ArrayDeque();
      var3.push(var2);
      boolean var4 = false;
      int var5 = 0;

      for (int var6 = 0; var6 < var1.length(); var6++) {
         char var7 = var1.charAt(var6);
         if (var7 == '.') {
            if (var6 == var5) {
               var2.add(ComparableVersion.IntItem.ZERO);
            } else {
               var2.add(parseItem(var4, var1.substring(var5, var6)));
            }

            var5 = var6 + 1;
         } else if (var7 == '-') {
            if (var6 == var5) {
               var2.add(ComparableVersion.IntItem.ZERO);
            } else {
               var2.add(parseItem(var4, var1.substring(var5, var6)));
            }

            var5 = var6 + 1;
            var2.add(var2 = new ComparableVersion.ListItem());
            var3.push(var2);
         } else if (Character.isDigit(var7)) {
            if (!var4 && var6 > var5) {
               var2.add(new ComparableVersion.StringItem(var1.substring(var5, var6), true));
               var5 = var6;
               var2.add(var2 = new ComparableVersion.ListItem());
               var3.push(var2);
            }

            var4 = true;
         } else {
            if (var4 && var6 > var5) {
               var2.add(parseItem(true, var1.substring(var5, var6)));
               var5 = var6;
               var2.add(var2 = new ComparableVersion.ListItem());
               var3.push(var2);
            }

            var4 = false;
         }
      }

      if (var1.length() > var5) {
         var2.add(parseItem(var4, var1.substring(var5)));
      }

      while (!var3.isEmpty()) {
         var2 = (ComparableVersion.ListItem)var3.pop();
         var2.normalize();
      }
   }

   private static ComparableVersion.Item parseItem(boolean var0, String var1) {
      if (var0) {
         var1 = stripLeadingZeroes(var1);
         if (var1.length() <= 9) {
            return new ComparableVersion.IntItem(var1);
         } else {
            return (ComparableVersion.Item)(var1.length() <= 18 ? new ComparableVersion.LongItem(var1) : new ComparableVersion.BigIntegerItem(var1));
         }
      } else {
         return new ComparableVersion.StringItem(var1, false);
      }
   }

   private static String stripLeadingZeroes(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            char var2 = var0.charAt(var1);
            if (var2 != '0') {
               return var0.substring(var1);
            }
         }

         return var0;
      } else {
         return "0";
      }
   }

   public int compareTo(ComparableVersion var1) {
      return this.items.compareTo(var1.items);
   }

   @Override
   public String toString() {
      return this.value;
   }

   public String getCanonical() {
      if (this.canonical == null) {
         this.canonical = this.items.toString();
      }

      return this.canonical;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof ComparableVersion && this.items.equals(((ComparableVersion)var1).items);
   }

   @Override
   public int hashCode() {
      return this.items.hashCode();
   }

   public static void main(String... var0) {
      System.out.println("Display parameters as parsed by Maven (in canonical form and as a list of tokens) and comparison result:");
      if (var0.length != 0) {
         ComparableVersion var1 = null;
         int var2 = 1;

         for (String var6 : var0) {
            ComparableVersion var7 = new ComparableVersion(var6);
            if (var1 != null) {
               int var8 = var1.compareTo(var7);
               System.out.println("   " + var1.toString() + ' ' + (var8 == 0 ? "==" : (var8 < 0 ? "<" : ">")) + ' ' + var6);
            }

            System.out.println(var2++ + ". " + var6 + " -> " + var7.getCanonical() + "; tokens: " + var7.items.toListString());
            var1 = var7;
         }
      }
   }

   private static class BigIntegerItem implements ComparableVersion.Item {
      private final BigInteger value;

      BigIntegerItem(String var1) {
         this.value = new BigInteger(var1);
      }

      @Override
      public int getType() {
         return 0;
      }

      @Override
      public boolean isNull() {
         return BigInteger.ZERO.equals(this.value);
      }

      @Override
      public int compareTo(ComparableVersion.Item var1) {
         if (var1 == null) {
            return BigInteger.ZERO.equals(this.value) ? 0 : 1;
         } else {
            switch (var1.getType()) {
               case 0:
                  return this.value.compareTo(((ComparableVersion.BigIntegerItem)var1).value);
               case 1:
                  return 1;
               case 2:
                  return 1;
               case 3:
               case 4:
                  return 1;
               default:
                  throw new IllegalStateException("invalid item: " + var1.getClass());
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ComparableVersion.BigIntegerItem var2 = (ComparableVersion.BigIntegerItem)var1;
            return this.value.equals(var2.value);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.value.hashCode();
      }

      @Override
      public String toString() {
         return this.value.toString();
      }
   }

   private static class IntItem implements ComparableVersion.Item {
      private final int value;
      public static final ComparableVersion.IntItem ZERO = new ComparableVersion.IntItem();

      private IntItem() {
         this.value = 0;
      }

      IntItem(String var1) {
         this.value = Integer.parseInt(var1);
      }

      @Override
      public int getType() {
         return 3;
      }

      @Override
      public boolean isNull() {
         return this.value == 0;
      }

      @Override
      public int compareTo(ComparableVersion.Item var1) {
         if (var1 == null) {
            return this.value == 0 ? 0 : 1;
         } else {
            switch (var1.getType()) {
               case 0:
               case 4:
                  return -1;
               case 1:
                  return 1;
               case 2:
                  return 1;
               case 3:
                  int var2 = ((ComparableVersion.IntItem)var1).value;
                  return this.value < var2 ? -1 : (this.value == var2 ? 0 : 1);
               default:
                  throw new IllegalStateException("invalid item: " + var1.getClass());
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ComparableVersion.IntItem var2 = (ComparableVersion.IntItem)var1;
            return this.value == var2.value;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.value;
      }

      @Override
      public String toString() {
         return Integer.toString(this.value);
      }
   }

   private interface Item {
      int INT_ITEM = 3;
      int LONG_ITEM = 4;
      int BIGINTEGER_ITEM = 0;
      int STRING_ITEM = 1;
      int LIST_ITEM = 2;

      int compareTo(ComparableVersion.Item var1);

      int getType();

      boolean isNull();
   }

   private static class ListItem extends ArrayList<ComparableVersion.Item> implements ComparableVersion.Item {
      private ListItem() {
      }

      @Override
      public int getType() {
         return 2;
      }

      @Override
      public boolean isNull() {
         return this.size() == 0;
      }

      void normalize() {
         for (int var1 = this.size() - 1; var1 >= 0; var1--) {
            ComparableVersion.Item var2 = this.get(var1);
            if (var2.isNull()) {
               this.remove(var1);
            } else if (!(var2 instanceof ComparableVersion.ListItem)) {
               break;
            }
         }
      }

      @Override
      public int compareTo(ComparableVersion.Item var1) {
         if (var1 == null) {
            if (this.size() == 0) {
               return 0;
            } else {
               for (ComparableVersion.Item var8 : this) {
                  int var9 = var8.compareTo(null);
                  if (var9 != 0) {
                     return var9;
                  }
               }

               return 0;
            }
         } else {
            switch (var1.getType()) {
               case 0:
               case 3:
               case 4:
                  return -1;
               case 1:
                  return 1;
               case 2:
                  Iterator var2 = this.iterator();
                  Iterator var3 = ((ComparableVersion.ListItem)var1).iterator();

                  while (var2.hasNext() || var3.hasNext()) {
                     ComparableVersion.Item var4 = var2.hasNext() ? (ComparableVersion.Item)var2.next() : null;
                     ComparableVersion.Item var5 = var3.hasNext() ? (ComparableVersion.Item)var3.next() : null;
                     int var6 = var4 == null ? (var5 == null ? 0 : -1 * var5.compareTo(var4)) : var4.compareTo(var5);
                     if (var6 != 0) {
                        return var6;
                     }
                  }

                  return 0;
               default:
                  throw new IllegalStateException("invalid item: " + var1.getClass());
            }
         }
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();

         for (ComparableVersion.Item var3 : this) {
            if (var1.length() > 0) {
               var1.append((char)(var3 instanceof ComparableVersion.ListItem ? '-' : '.'));
            }

            var1.append(var3);
         }

         return var1.toString();
      }

      private String toListString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("[");

         for (ComparableVersion.Item var3 : this) {
            if (var1.length() > 1) {
               var1.append(", ");
            }

            if (var3 instanceof ComparableVersion.ListItem) {
               var1.append(((ComparableVersion.ListItem)var3).toListString());
            } else {
               var1.append(var3);
            }
         }

         var1.append("]");
         return var1.toString();
      }
   }

   private static class LongItem implements ComparableVersion.Item {
      private final long value;

      LongItem(String var1) {
         this.value = Long.parseLong(var1);
      }

      @Override
      public int getType() {
         return 4;
      }

      @Override
      public boolean isNull() {
         return this.value == 0L;
      }

      @Override
      public int compareTo(ComparableVersion.Item var1) {
         if (var1 == null) {
            return this.value == 0L ? 0 : 1;
         } else {
            switch (var1.getType()) {
               case 0:
                  return -1;
               case 1:
                  return 1;
               case 2:
                  return 1;
               case 3:
                  return 1;
               case 4:
                  long var2 = ((ComparableVersion.LongItem)var1).value;
                  return this.value < var2 ? -1 : (this.value == var2 ? 0 : 1);
               default:
                  throw new IllegalStateException("invalid item: " + var1.getClass());
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ComparableVersion.LongItem var2 = (ComparableVersion.LongItem)var1;
            return this.value == var2.value;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return (int)(this.value ^ this.value >>> 32);
      }

      @Override
      public String toString() {
         return Long.toString(this.value);
      }
   }

   private static class StringItem implements ComparableVersion.Item {
      private static final List<String> QUALIFIERS = Arrays.asList("alpha", "beta", "milestone", "rc", "snapshot", "", "sp");
      private static final Properties ALIASES = new Properties();
      private static final String RELEASE_VERSION_INDEX = String.valueOf(QUALIFIERS.indexOf(""));
      private final String value;

      StringItem(String var1, boolean var2) {
         if (var2 && var1.length() == 1) {
            switch (var1.charAt(0)) {
               case 'a':
                  var1 = "alpha";
                  break;
               case 'b':
                  var1 = "beta";
                  break;
               case 'm':
                  var1 = "milestone";
            }
         }

         this.value = ALIASES.getProperty(var1, var1);
      }

      @Override
      public int getType() {
         return 1;
      }

      @Override
      public boolean isNull() {
         return comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX) == 0;
      }

      public static String comparableQualifier(String var0) {
         int var1 = QUALIFIERS.indexOf(var0);
         return var1 == -1 ? QUALIFIERS.size() + "-" + var0 : String.valueOf(var1);
      }

      @Override
      public int compareTo(ComparableVersion.Item var1) {
         if (var1 == null) {
            return comparableQualifier(this.value).compareTo(RELEASE_VERSION_INDEX);
         } else {
            switch (var1.getType()) {
               case 0:
               case 3:
               case 4:
                  return -1;
               case 1:
                  return comparableQualifier(this.value).compareTo(comparableQualifier(((ComparableVersion.StringItem)var1).value));
               case 2:
                  return -1;
               default:
                  throw new IllegalStateException("invalid item: " + var1.getClass());
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ComparableVersion.StringItem var2 = (ComparableVersion.StringItem)var1;
            return this.value.equals(var2.value);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.value.hashCode();
      }

      @Override
      public String toString() {
         return this.value;
      }

      static {
         ALIASES.put("ga", "");
         ALIASES.put("final", "");
         ALIASES.put("release", "");
         ALIASES.put("cr", "rc");
      }
   }
}
