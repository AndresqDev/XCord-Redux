package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class HashCodeBuilder implements Builder<Integer> {
   private static final int DEFAULT_INITIAL_VALUE = 17;
   private static final int DEFAULT_MULTIPLIER_VALUE = 37;
   private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal<>();
   private final int iConstant;
   private int iTotal = 0;

   static Set<IDKey> getRegistry() {
      return REGISTRY.get();
   }

   static boolean isRegistered(Object var0) {
      Set var1 = getRegistry();
      return var1 != null && var1.contains(new IDKey(var0));
   }

   private static void reflectionAppend(Object var0, Class<?> var1, HashCodeBuilder var2, boolean var3, String[] var4) {
      if (!isRegistered(var0)) {
         try {
            register(var0);
            Field[] var5 = var1.getDeclaredFields();
            AccessibleObject.setAccessible(var5, true);

            for (Field var9 : var5) {
               if (!ArrayUtils.contains(var4, var9.getName())
                  && !var9.getName().contains("$")
                  && (var3 || !Modifier.isTransient(var9.getModifiers()))
                  && !Modifier.isStatic(var9.getModifiers())
                  && !var9.isAnnotationPresent(HashCodeExclude.class)) {
                  try {
                     Object var10 = var9.get(var0);
                     var2.append(var10);
                  } catch (IllegalAccessException var14) {
                     throw new InternalError("Unexpected IllegalAccessException");
                  }
               }
            }
         } finally {
            unregister(var0);
         }
      }
   }

   public static int reflectionHashCode(int var0, int var1, Object var2) {
      return reflectionHashCode(var0, var1, var2, false, null);
   }

   public static int reflectionHashCode(int var0, int var1, Object var2, boolean var3) {
      return reflectionHashCode(var0, var1, var2, var3, null);
   }

   public static <T> int reflectionHashCode(int var0, int var1, T var2, boolean var3, Class<? super T> var4, String... var5) {
      Validate.isTrue(var2 != null, "The object to build a hash code for must not be null");
      HashCodeBuilder var6 = new HashCodeBuilder(var0, var1);
      Class var7 = var2.getClass();
      reflectionAppend(var2, var7, var6, var3, var5);

      while (var7.getSuperclass() != null && var7 != var4) {
         var7 = var7.getSuperclass();
         reflectionAppend(var2, var7, var6, var3, var5);
      }

      return var6.toHashCode();
   }

   public static int reflectionHashCode(Object var0, boolean var1) {
      return reflectionHashCode(17, 37, var0, var1, null);
   }

   public static int reflectionHashCode(Object var0, Collection<String> var1) {
      return reflectionHashCode(var0, ReflectionToStringBuilder.toNoNullStringArray(var1));
   }

   public static int reflectionHashCode(Object var0, String... var1) {
      return reflectionHashCode(17, 37, var0, false, null, var1);
   }

   private static void register(Object var0) {
      Object var1 = getRegistry();
      if (var1 == null) {
         var1 = new HashSet();
         REGISTRY.set((Set<IDKey>)var1);
      }

      var1.add(new IDKey(var0));
   }

   private static void unregister(Object var0) {
      Set var1 = getRegistry();
      if (var1 != null) {
         var1.remove(new IDKey(var0));
         if (var1.isEmpty()) {
            REGISTRY.remove();
         }
      }
   }

   public HashCodeBuilder() {
      this.iConstant = 37;
      this.iTotal = 17;
   }

   public HashCodeBuilder(int var1, int var2) {
      Validate.isTrue(var1 % 2 != 0, "HashCodeBuilder requires an odd initial value");
      Validate.isTrue(var2 % 2 != 0, "HashCodeBuilder requires an odd multiplier");
      this.iConstant = var2;
      this.iTotal = var1;
   }

   public HashCodeBuilder append(boolean var1) {
      this.iTotal = this.iTotal * this.iConstant + (var1 ? 0 : 1);
      return this;
   }

   public HashCodeBuilder append(boolean[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (boolean var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(byte var1) {
      this.iTotal = this.iTotal * this.iConstant + var1;
      return this;
   }

   public HashCodeBuilder append(byte[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (byte var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(char var1) {
      this.iTotal = this.iTotal * this.iConstant + var1;
      return this;
   }

   public HashCodeBuilder append(char[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (char var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(double var1) {
      return this.append(Double.doubleToLongBits(var1));
   }

   public HashCodeBuilder append(double[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (double var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(float var1) {
      this.iTotal = this.iTotal * this.iConstant + Float.floatToIntBits(var1);
      return this;
   }

   public HashCodeBuilder append(float[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (float var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(int var1) {
      this.iTotal = this.iTotal * this.iConstant + var1;
      return this;
   }

   public HashCodeBuilder append(int[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (int var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(long var1) {
      this.iTotal = this.iTotal * this.iConstant + (int)(var1 ^ var1 >> 32);
      return this;
   }

   public HashCodeBuilder append(long[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (long var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(Object var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else if (var1.getClass().isArray()) {
         this.appendArray(var1);
      } else {
         this.iTotal = this.iTotal * this.iConstant + var1.hashCode();
      }

      return this;
   }

   private void appendArray(Object var1) {
      if (var1 instanceof long[]) {
         this.append((long[])var1);
      } else if (var1 instanceof int[]) {
         this.append((int[])var1);
      } else if (var1 instanceof short[]) {
         this.append((short[])var1);
      } else if (var1 instanceof char[]) {
         this.append((char[])var1);
      } else if (var1 instanceof byte[]) {
         this.append((byte[])var1);
      } else if (var1 instanceof double[]) {
         this.append((double[])var1);
      } else if (var1 instanceof float[]) {
         this.append((float[])var1);
      } else if (var1 instanceof boolean[]) {
         this.append((boolean[])var1);
      } else {
         this.append((Object[])var1);
      }
   }

   public HashCodeBuilder append(Object[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (Object var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder append(short var1) {
      this.iTotal = this.iTotal * this.iConstant + var1;
      return this;
   }

   public HashCodeBuilder append(short[] var1) {
      if (var1 == null) {
         this.iTotal = this.iTotal * this.iConstant;
      } else {
         for (short var5 : var1) {
            this.append(var5);
         }
      }

      return this;
   }

   public HashCodeBuilder appendSuper(int var1) {
      this.iTotal = this.iTotal * this.iConstant + var1;
      return this;
   }

   public int toHashCode() {
      return this.iTotal;
   }

   public Integer build() {
      return this.toHashCode();
   }

   @Override
   public int hashCode() {
      return this.toHashCode();
   }
}
