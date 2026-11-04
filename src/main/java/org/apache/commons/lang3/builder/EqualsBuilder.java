package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

public class EqualsBuilder implements Builder<Boolean> {
   private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal<>();
   private boolean isEquals = true;
   private boolean testTransients = false;
   private boolean testRecursive = false;
   private List<Class<?>> bypassReflectionClasses;
   private Class<?> reflectUpToClass = null;
   private String[] excludeFields = null;

   static Set<Pair<IDKey, IDKey>> getRegistry() {
      return REGISTRY.get();
   }

   static Pair<IDKey, IDKey> getRegisterPair(Object var0, Object var1) {
      IDKey var2 = new IDKey(var0);
      IDKey var3 = new IDKey(var1);
      return Pair.of(var2, var3);
   }

   static boolean isRegistered(Object var0, Object var1) {
      Set var2 = getRegistry();
      Pair var3 = getRegisterPair(var0, var1);
      Pair var4 = Pair.of(var3.getRight(), var3.getLeft());
      return var2 != null && (var2.contains(var3) || var2.contains(var4));
   }

   private static void register(Object var0, Object var1) {
      Object var2 = getRegistry();
      if (var2 == null) {
         var2 = new HashSet();
         REGISTRY.set((Set<Pair<IDKey, IDKey>>)var2);
      }

      Pair var3 = getRegisterPair(var0, var1);
      var2.add(var3);
   }

   private static void unregister(Object var0, Object var1) {
      Set var2 = getRegistry();
      if (var2 != null) {
         Pair var3 = getRegisterPair(var0, var1);
         var2.remove(var3);
         if (var2.isEmpty()) {
            REGISTRY.remove();
         }
      }
   }

   public EqualsBuilder() {
      this.bypassReflectionClasses = new ArrayList<>();
      this.bypassReflectionClasses.add(String.class);
   }

   public EqualsBuilder setTestTransients(boolean var1) {
      this.testTransients = var1;
      return this;
   }

   public EqualsBuilder setTestRecursive(boolean var1) {
      this.testRecursive = var1;
      return this;
   }

   public EqualsBuilder setBypassReflectionClasses(List<Class<?>> var1) {
      this.bypassReflectionClasses = var1;
      return this;
   }

   public EqualsBuilder setReflectUpToClass(Class<?> var1) {
      this.reflectUpToClass = var1;
      return this;
   }

   public EqualsBuilder setExcludeFields(String... var1) {
      this.excludeFields = var1;
      return this;
   }

   public static boolean reflectionEquals(Object var0, Object var1, Collection<String> var2) {
      return reflectionEquals(var0, var1, ReflectionToStringBuilder.toNoNullStringArray(var2));
   }

   public static boolean reflectionEquals(Object var0, Object var1, String... var2) {
      return reflectionEquals(var0, var1, false, null, var2);
   }

   public static boolean reflectionEquals(Object var0, Object var1, boolean var2) {
      return reflectionEquals(var0, var1, var2, null);
   }

   public static boolean reflectionEquals(Object var0, Object var1, boolean var2, Class<?> var3, String... var4) {
      return reflectionEquals(var0, var1, var2, var3, false, var4);
   }

   public static boolean reflectionEquals(Object var0, Object var1, boolean var2, Class<?> var3, boolean var4, String... var5) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null
            ? new EqualsBuilder()
               .setExcludeFields(var5)
               .setReflectUpToClass(var3)
               .setTestTransients(var2)
               .setTestRecursive(var4)
               .reflectionAppend(var0, var1)
               .isEquals()
            : false;
      }
   }

   public EqualsBuilder reflectionAppend(Object var1, Object var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         Class var3 = var1.getClass();
         Class var4 = var2.getClass();
         Class var5;
         if (var3.isInstance(var2)) {
            var5 = var3;
            if (!var4.isInstance(var1)) {
               var5 = var4;
            }
         } else {
            if (!var4.isInstance(var1)) {
               this.isEquals = false;
               return this;
            }

            var5 = var4;
            if (!var3.isInstance(var2)) {
               var5 = var3;
            }
         }

         try {
            if (var5.isArray()) {
               this.append(var1, var2);
            } else if (this.bypassReflectionClasses == null || !this.bypassReflectionClasses.contains(var3) && !this.bypassReflectionClasses.contains(var4)) {
               this.reflectionAppend(var1, var2, var5);

               while (var5.getSuperclass() != null && var5 != this.reflectUpToClass) {
                  var5 = var5.getSuperclass();
                  this.reflectionAppend(var1, var2, var5);
               }
            } else {
               this.isEquals = var1.equals(var2);
            }

            return this;
         } catch (IllegalArgumentException var7) {
            this.isEquals = false;
            return this;
         }
      } else {
         this.isEquals = false;
         return this;
      }
   }

   private void reflectionAppend(Object var1, Object var2, Class<?> var3) {
      if (!isRegistered(var1, var2)) {
         try {
            register(var1, var2);
            Field[] var4 = var3.getDeclaredFields();
            AccessibleObject.setAccessible(var4, true);

            for (int var5 = 0; var5 < var4.length && this.isEquals; var5++) {
               Field var6 = var4[var5];
               if (!ArrayUtils.contains(this.excludeFields, var6.getName())
                  && !var6.getName().contains("$")
                  && (this.testTransients || !Modifier.isTransient(var6.getModifiers()))
                  && !Modifier.isStatic(var6.getModifiers())
                  && !var6.isAnnotationPresent(EqualsExclude.class)) {
                  try {
                     this.append(var6.get(var1), var6.get(var2));
                  } catch (IllegalAccessException var11) {
                     throw new InternalError("Unexpected IllegalAccessException");
                  }
               }
            }
         } finally {
            unregister(var1, var2);
         }
      }
   }

   public EqualsBuilder appendSuper(boolean var1) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1;
         return this;
      }
   }

   public EqualsBuilder append(Object var1, Object var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         Class var3 = var1.getClass();
         if (var3.isArray()) {
            this.appendArray(var1, var2);
         } else if (this.testRecursive && !ClassUtils.isPrimitiveOrWrapper(var3)) {
            this.reflectionAppend(var1, var2);
         } else {
            this.isEquals = var1.equals(var2);
         }

         return this;
      } else {
         this.setEquals(false);
         return this;
      }
   }

   private void appendArray(Object var1, Object var2) {
      if (var1.getClass() != var2.getClass()) {
         this.setEquals(false);
      } else if (var1 instanceof long[]) {
         this.append((long[])var1, (long[])var2);
      } else if (var1 instanceof int[]) {
         this.append((int[])var1, (int[])var2);
      } else if (var1 instanceof short[]) {
         this.append((short[])var1, (short[])var2);
      } else if (var1 instanceof char[]) {
         this.append((char[])var1, (char[])var2);
      } else if (var1 instanceof byte[]) {
         this.append((byte[])var1, (byte[])var2);
      } else if (var1 instanceof double[]) {
         this.append((double[])var1, (double[])var2);
      } else if (var1 instanceof float[]) {
         this.append((float[])var1, (float[])var2);
      } else if (var1 instanceof boolean[]) {
         this.append((boolean[])var1, (boolean[])var2);
      } else {
         this.append((Object[])var1, (Object[])var2);
      }
   }

   public EqualsBuilder append(long var1, long var3) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var3;
         return this;
      }
   }

   public EqualsBuilder append(int var1, int var2) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var2;
         return this;
      }
   }

   public EqualsBuilder append(short var1, short var2) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var2;
         return this;
      }
   }

   public EqualsBuilder append(char var1, char var2) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var2;
         return this;
      }
   }

   public EqualsBuilder append(byte var1, byte var2) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var2;
         return this;
      }
   }

   public EqualsBuilder append(double var1, double var3) {
      return !this.isEquals ? this : this.append(Double.doubleToLongBits(var1), Double.doubleToLongBits(var3));
   }

   public EqualsBuilder append(float var1, float var2) {
      return !this.isEquals ? this : this.append(Float.floatToIntBits(var1), Float.floatToIntBits(var2));
   }

   public EqualsBuilder append(boolean var1, boolean var2) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = var1 == var2;
         return this;
      }
   }

   public EqualsBuilder append(Object[] var1, Object[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(long[] var1, long[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(int[] var1, int[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(short[] var1, short[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(char[] var1, char[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(byte[] var1, byte[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(double[] var1, double[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(float[] var1, float[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(boolean[] var1, boolean[] var2) {
      if (!this.isEquals) {
         return this;
      } else if (var1 == var2) {
         return this;
      } else if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            this.setEquals(false);
            return this;
         } else {
            for (int var3 = 0; var3 < var1.length && this.isEquals; var3++) {
               this.append(var1[var3], var2[var3]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public boolean isEquals() {
      return this.isEquals;
   }

   public Boolean build() {
      return this.isEquals();
   }

   protected void setEquals(boolean var1) {
      this.isEquals = var1;
   }

   public void reset() {
      this.isEquals = true;
   }
}
