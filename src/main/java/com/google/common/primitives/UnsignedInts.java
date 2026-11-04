package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Comparator;

@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class UnsignedInts {
   static final long INT_MASK = 4294967295L;

   private UnsignedInts() {
   }

   static int flip(int var0) {
      return var0 ^ Integer.MIN_VALUE;
   }

   public static int compare(int var0, int var1) {
      return Ints.compare(flip(var0), flip(var1));
   }

   public static long toLong(int var0) {
      return (long)var0 & 4294967295L;
   }

   public static int checkedCast(long var0) {
      Preconditions.checkArgument(var0 >> 32 == 0L, "out of range: %s", var0);
      return (int)var0;
   }

   public static int saturatedCast(long var0) {
      if (var0 <= 0L) {
         return 0;
      } else {
         return var0 >= 4294967296L ? -1 : (int)var0;
      }
   }

   public static int min(int... var0) {
      Preconditions.checkArgument(var0.length > 0);
      int var1 = flip(var0[0]);

      for (int var2 = 1; var2 < var0.length; var2++) {
         int var3 = flip(var0[var2]);
         if (var3 < var1) {
            var1 = var3;
         }
      }

      return flip(var1);
   }

   public static int max(int... var0) {
      Preconditions.checkArgument(var0.length > 0);
      int var1 = flip(var0[0]);

      for (int var2 = 1; var2 < var0.length; var2++) {
         int var3 = flip(var0[var2]);
         if (var3 > var1) {
            var1 = var3;
         }
      }

      return flip(var1);
   }

   public static String join(String var0, int... var1) {
      Preconditions.checkNotNull(var0);
      if (var1.length == 0) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1.length * 5);
         var2.append(toString(var1[0]));

         for (int var3 = 1; var3 < var1.length; var3++) {
            var2.append(var0).append(toString(var1[var3]));
         }

         return var2.toString();
      }
   }

   public static Comparator<int[]> lexicographicalComparator() {
      return UnsignedInts.LexicographicalComparator.INSTANCE;
   }

   public static void sort(int[] var0) {
      Preconditions.checkNotNull(var0);
      sort(var0, 0, var0.length);
   }

   public static void sort(int[] var0, int var1, int var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkPositionIndexes(var1, var2, var0.length);

      for (int var3 = var1; var3 < var2; var3++) {
         var0[var3] = flip(var0[var3]);
      }

      Arrays.sort(var0, var1, var2);

      for (int var4 = var1; var4 < var2; var4++) {
         var0[var4] = flip(var0[var4]);
      }
   }

   public static void sortDescending(int[] var0) {
      Preconditions.checkNotNull(var0);
      sortDescending(var0, 0, var0.length);
   }

   public static void sortDescending(int[] var0, int var1, int var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkPositionIndexes(var1, var2, var0.length);

      for (int var3 = var1; var3 < var2; var3++) {
         var0[var3] ^= Integer.MAX_VALUE;
      }

      Arrays.sort(var0, var1, var2);

      for (int var4 = var1; var4 < var2; var4++) {
         var0[var4] ^= Integer.MAX_VALUE;
      }
   }

   public static int divide(int var0, int var1) {
      return (int)(toLong(var0) / toLong(var1));
   }

   public static int remainder(int var0, int var1) {
      return (int)(toLong(var0) % toLong(var1));
   }

   @CanIgnoreReturnValue
   public static int decode(String var0) {
      ParseRequest var1 = ParseRequest.fromString(var0);

      try {
         return parseUnsignedInt(var1.rawValue, var1.radix);
      } catch (NumberFormatException var4) {
         String var10003 = String.valueOf(var0);
         NumberFormatException var3 = new NumberFormatException(
            var10003.length() != 0 ? "Error parsing value: ".concat(var10003) : new String("Error parsing value: ")
         );
         var3.initCause(var4);
         throw var3;
      }
   }

   @CanIgnoreReturnValue
   public static int parseUnsignedInt(String var0) {
      return parseUnsignedInt(var0, 10);
   }

   @CanIgnoreReturnValue
   public static int parseUnsignedInt(String var0, int var1) {
      Preconditions.checkNotNull(var0);
      long var2 = Long.parseLong(var0, var1);
      if ((var2 & 4294967295L) != var2) {
         throw new NumberFormatException(
            new StringBuilder(69 + String.valueOf(var0).length())
               .append("Input ")
               .append(var0)
               .append(" in base ")
               .append(var1)
               .append(" is not in the range of an unsigned integer")
               .toString()
         );
      } else {
         return (int)var2;
      }
   }

   public static String toString(int var0) {
      return toString(var0, 10);
   }

   public static String toString(int var0, int var1) {
      long var2 = (long)var0 & 4294967295L;
      return Long.toString(var2, var1);
   }

   static enum LexicographicalComparator implements Comparator<int[]> {
      INSTANCE;

      public int compare(int[] var1, int[] var2) {
         int var3 = Math.min(var1.length, var2.length);

         for (int var4 = 0; var4 < var3; var4++) {
            if (var1[var4] != var2[var4]) {
               return UnsignedInts.compare(var1[var4], var2[var4]);
            }
         }

         return var1.length - var2.length;
      }

      @Override
      public String toString() {
         return "UnsignedInts.lexicographicalComparator()";
      }
   }
}
