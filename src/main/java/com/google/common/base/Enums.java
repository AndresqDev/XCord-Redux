package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Enums {
   @GwtIncompatible
   private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> enumConstantCache = new WeakHashMap<>();

   private Enums() {
   }

   @GwtIncompatible
   public static Field getField(Enum<?> var0) {
      Class var1 = var0.getDeclaringClass();

      try {
         return var1.getDeclaredField(var0.name());
      } catch (NoSuchFieldException var3) {
         throw new AssertionError(var3);
      }
   }

   public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> var0, String var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Platform.getEnumIfPresent(var0, var1);
   }

   @GwtIncompatible
   private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> populateCache(Class<T> var0) {
      HashMap var1 = new HashMap();

      for (Enum var3 : EnumSet.allOf(var0)) {
         var1.put(var3.name(), new WeakReference<>(var3));
      }

      enumConstantCache.put(var0, var1);
      return var1;
   }

   @GwtIncompatible
   static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(Class<T> var0) {
      synchronized (enumConstantCache) {
         Map var2 = enumConstantCache.get(var0);
         if (var2 == null) {
            var2 = populateCache(var0);
         }

         return var2;
      }
   }

   public static <T extends Enum<T>> Converter<String, T> stringConverter(Class<T> var0) {
      return new Enums.StringConverter<>(var0);
   }

   private static final class StringConverter<T extends Enum<T>> extends Converter<String, T> implements Serializable {
      private final Class<T> enumClass;
      private static final long serialVersionUID = 0L;

      StringConverter(Class<T> var1) {
         this.enumClass = Preconditions.checkNotNull(var1);
      }

      protected T doForward(String var1) {
         return Enum.valueOf(this.enumClass, var1);
      }

      protected String doBackward(T var1) {
         return var1.name();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Enums.StringConverter) {
            Enums.StringConverter var2 = (Enums.StringConverter)var1;
            return this.enumClass.equals(var2.enumClass);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.enumClass.hashCode();
      }

      @Override
      public String toString() {
         String var1 = this.enumClass.getName();
         return new StringBuilder(29 + String.valueOf(var1).length()).append("Enums.stringConverter(").append(var1).append(".class)").toString();
      }
   }
}
