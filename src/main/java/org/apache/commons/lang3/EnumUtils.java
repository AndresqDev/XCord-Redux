package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils {
   private static final String NULL_ELEMENTS_NOT_PERMITTED = "null elements not permitted";
   private static final String CANNOT_STORE_S_S_VALUES_IN_S_BITS = "Cannot store %s %s values in %s bits";
   private static final String S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE = "%s does not seem to be an Enum type";
   private static final String ENUM_CLASS_MUST_BE_DEFINED = "EnumClass must be defined.";

   public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Enum var5 : (Enum[])var0.getEnumConstants()) {
         var1.put(var5.name(), var5);
      }

      return var1;
   }

   public static <E extends Enum<E>> List<E> getEnumList(Class<E> var0) {
      return new ArrayList<>(Arrays.asList((E[])var0.getEnumConstants()));
   }

   public static <E extends Enum<E>> boolean isValidEnum(Class<E> var0, String var1) {
      return getEnum(var0, var1) != null;
   }

   public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(Class<E> var0, String var1) {
      return getEnumIgnoreCase(var0, var1) != null;
   }

   public static <E extends Enum<E>> E getEnum(Class<E> var0, String var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            return Enum.valueOf(var0, var1);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }
   }

   public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> var0, String var1) {
      if (var1 != null && var0.isEnum()) {
         for (Enum var5 : (Enum[])var0.getEnumConstants()) {
            if (var5.name().equalsIgnoreCase(var1)) {
               return (E)var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static <E extends Enum<E>> long generateBitVector(Class<E> var0, Iterable<? extends E> var1) {
      checkBitVectorable(var0);
      Validate.notNull(var1);
      long var2 = 0L;

      for (Enum var5 : var1) {
         Validate.isTrue(var5 != null, "null elements not permitted");
         var2 |= 1L << var5.ordinal();
      }

      return var2;
   }

   public static <E extends Enum<E>> long[] generateBitVectors(Class<E> var0, Iterable<? extends E> var1) {
      asEnum(var0);
      Validate.notNull(var1);
      EnumSet var2 = EnumSet.noneOf(var0);

      for (Enum var4 : var1) {
         Validate.isTrue(var4 != null, "null elements not permitted");
         var2.add((E)var4);
      }

      long[] var6 = new long[(((Enum[])var0.getEnumConstants()).length - 1) / 64 + 1];

      for (Enum var5 : var2) {
         var6[var5.ordinal() / 64] |= 1L << var5.ordinal() % 64;
      }

      ArrayUtils.reverse(var6);
      return var6;
   }

   @SafeVarargs
   public static <E extends Enum<E>> long generateBitVector(Class<E> var0, E... var1) {
      Validate.noNullElements(var1);
      return generateBitVector(var0, Arrays.asList((E[])var1));
   }

   @SafeVarargs
   public static <E extends Enum<E>> long[] generateBitVectors(Class<E> var0, E... var1) {
      asEnum(var0);
      Validate.noNullElements(var1);
      EnumSet var2 = EnumSet.noneOf(var0);
      Collections.addAll(var2, (E[])var1);
      long[] var3 = new long[(((Enum[])var0.getEnumConstants()).length - 1) / 64 + 1];

      for (Enum var5 : var2) {
         var3[var5.ordinal() / 64] |= 1L << var5.ordinal() % 64;
      }

      ArrayUtils.reverse(var3);
      return var3;
   }

   public static <E extends Enum<E>> EnumSet<E> processBitVector(Class<E> var0, long var1) {
      checkBitVectorable(var0).getEnumConstants();
      return processBitVectors(var0, var1);
   }

   public static <E extends Enum<E>> EnumSet<E> processBitVectors(Class<E> var0, long... var1) {
      EnumSet var2 = EnumSet.noneOf(asEnum(var0));
      long[] var3 = ArrayUtils.clone(Validate.notNull(var1));
      ArrayUtils.reverse(var3);

      for (Enum var7 : (Enum[])var0.getEnumConstants()) {
         int var8 = var7.ordinal() / 64;
         if (var8 < var3.length && (var3[var8] & 1L << var7.ordinal() % 64) != 0L) {
            var2.add((E)var7);
         }
      }

      return var2;
   }

   private static <E extends Enum<E>> Class<E> checkBitVectorable(Class<E> var0) {
      Enum[] var1 = (Enum[])asEnum(var0).getEnumConstants();
      Validate.isTrue(var1.length <= 64, "Cannot store %s %s values in %s bits", var1.length, var0.getSimpleName(), 64);
      return var0;
   }

   private static <E extends Enum<E>> Class<E> asEnum(Class<E> var0) {
      Validate.notNull(var0, "EnumClass must be defined.");
      Validate.isTrue(var0.isEnum(), "%s does not seem to be an Enum type", var0);
      return var0;
   }
}
