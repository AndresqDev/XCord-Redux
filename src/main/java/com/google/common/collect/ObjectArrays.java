package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class ObjectArrays {
   private ObjectArrays() {
   }

   @GwtIncompatible
   public static <T> T[] newArray(Class<T> var0, int var1) {
      return (T[])((Object[])Array.newInstance(var0, var1));
   }

   public static <T> T[] newArray(T[] var0, int var1) {
      return (T[])Platform.newArray(var0, var1);
   }

   @GwtIncompatible
   public static <T> T[] concat(T[] var0, T[] var1, Class<T> var2) {
      Object[] var3 = newArray(var2, var0.length + var1.length);
      System.arraycopy(var0, 0, var3, 0, var0.length);
      System.arraycopy(var1, 0, var3, var0.length, var1.length);
      return (T[])var3;
   }

   public static <T> T[] concat(@ParametricNullness T var0, T[] var1) {
      Object[] var2 = newArray(var1, var1.length + 1);
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return (T[])var2;
   }

   public static <T> T[] concat(T[] var0, @ParametricNullness T var1) {
      Object[] var2 = Arrays.copyOf(var0, var0.length + 1);
      var2[var0.length] = var1;
      return (T[])var2;
   }

   static <T> T[] toArrayImpl(Collection<?> var0, T[] var1) {
      int var2 = var0.size();
      if (var1.length < var2) {
         var1 = newArray(var1, var2);
      }

      fillArray(var0, var1);
      if (var1.length > var2) {
         var1[var2] = null;
      }

      return (T[])var1;
   }

   static <T> T[] toArrayImpl(Object[] var0, int var1, int var2, T[] var3) {
      Preconditions.checkPositionIndexes(var1, var1 + var2, var0.length);
      if (var3.length < var2) {
         var3 = newArray(var3, var2);
      } else if (var3.length > var2) {
         var3[var2] = null;
      }

      System.arraycopy(var0, var1, var3, 0, var2);
      return (T[])var3;
   }

   static Object[] toArrayImpl(Collection<?> var0) {
      return fillArray(var0, new Object[var0.size()]);
   }

   static Object[] copyAsObjectArray(Object[] var0, int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var1 + var2, var0.length);
      if (var2 == 0) {
         return new Object[0];
      } else {
         Object[] var3 = new Object[var2];
         System.arraycopy(var0, var1, var3, 0, var2);
         return var3;
      }
   }

   @CanIgnoreReturnValue
   private static Object[] fillArray(Iterable<?> var0, Object[] var1) {
      int var2 = 0;

      for (Object var4 : var0) {
         var1[var2++] = var4;
      }

      return var1;
   }

   static void swap(Object[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   @CanIgnoreReturnValue
   static Object[] checkElementsNotNull(Object... var0) {
      return checkElementsNotNull(var0, var0.length);
   }

   @CanIgnoreReturnValue
   static Object[] checkElementsNotNull(Object[] var0, int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         checkElementNotNull(var0[var2], var2);
      }

      return var0;
   }

   @CanIgnoreReturnValue
   static Object checkElementNotNull(Object var0, int var1) {
      if (var0 == null) {
         throw new NullPointerException(new StringBuilder(20).append("at index ").append(var1).toString());
      } else {
         return var0;
      }
   }
}
