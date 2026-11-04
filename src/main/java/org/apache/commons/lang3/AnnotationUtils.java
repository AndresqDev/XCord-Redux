package org.apache.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnnotationUtils {
   private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
      private static final long serialVersionUID = 1L;

      {
         this.setDefaultFullDetail(true);
         this.setArrayContentDetail(true);
         this.setUseClassName(true);
         this.setUseShortClassName(true);
         this.setUseIdentityHashCode(false);
         this.setContentStart("(");
         this.setContentEnd(")");
         this.setFieldSeparator(", ");
         this.setArrayStart("[");
         this.setArrayEnd("]");
      }

      @Override
      protected String getShortClassName(Class<?> var1) {
         Class var2 = null;

         for (Class var4 : ClassUtils.getAllInterfaces(var1)) {
            if (Annotation.class.isAssignableFrom(var4)) {
               var2 = var4;
               break;
            }
         }

         return new StringBuilder(var2 == null ? "" : var2.getName()).insert(0, '@').toString();
      }

      @Override
      protected void appendDetail(StringBuffer var1, String var2, Object var3) {
         if (var3 instanceof Annotation) {
            var3 = AnnotationUtils.toString((Annotation)var3);
         }

         super.appendDetail(var1, var2, var3);
      }
   };

   public static boolean equals(Annotation var0, Annotation var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         Class var2 = var0.annotationType();
         Class var3 = var1.annotationType();
         Validate.notNull(var2, "Annotation %s with null annotationType()", var0);
         Validate.notNull(var3, "Annotation %s with null annotationType()", var1);
         if (!var2.equals(var3)) {
            return false;
         } else {
            try {
               for (Method var7 : var2.getDeclaredMethods()) {
                  if (var7.getParameterTypes().length == 0 && isValidAnnotationMemberType(var7.getReturnType())) {
                     Object var8 = var7.invoke(var0);
                     Object var9 = var7.invoke(var1);
                     if (!memberEquals(var7.getReturnType(), var8, var9)) {
                        return false;
                     }
                  }
               }

               return true;
            } catch (InvocationTargetException | IllegalAccessException var10) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static int hashCode(Annotation var0) {
      int var1 = 0;
      Class var2 = var0.annotationType();

      for (Method var6 : var2.getDeclaredMethods()) {
         try {
            Object var7 = var6.invoke(var0);
            if (var7 == null) {
               throw new IllegalStateException(String.format("Annotation method %s returned null", var6));
            }

            var1 += hashMember(var6.getName(), var7);
         } catch (RuntimeException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }
      }

      return var1;
   }

   public static String toString(Annotation var0) {
      ToStringBuilder var1 = new ToStringBuilder(var0, TO_STRING_STYLE);

      for (Method var5 : var0.annotationType().getDeclaredMethods()) {
         if (var5.getParameterTypes().length <= 0) {
            try {
               var1.append(var5.getName(), var5.invoke(var0));
            } catch (RuntimeException var7) {
               throw var7;
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      return var1.build();
   }

   public static boolean isValidAnnotationMemberType(Class<?> var0) {
      if (var0 == null) {
         return false;
      } else {
         if (var0.isArray()) {
            var0 = var0.getComponentType();
         }

         return var0.isPrimitive() || var0.isEnum() || var0.isAnnotation() || String.class.equals(var0) || Class.class.equals(var0);
      }
   }

   private static int hashMember(String var0, Object var1) {
      int var2 = var0.hashCode() * 127;
      if (var1.getClass().isArray()) {
         return var2 ^ arrayMemberHash(var1.getClass().getComponentType(), var1);
      } else {
         return var1 instanceof Annotation ? var2 ^ hashCode((Annotation)var1) : var2 ^ var1.hashCode();
      }
   }

   private static boolean memberEquals(Class<?> var0, Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 == null || var2 == null) {
         return false;
      } else if (var0.isArray()) {
         return arrayMemberEquals(var0.getComponentType(), var1, var2);
      } else {
         return var0.isAnnotation() ? equals((Annotation)var1, (Annotation)var2) : var1.equals(var2);
      }
   }

   private static boolean arrayMemberEquals(Class<?> var0, Object var1, Object var2) {
      if (var0.isAnnotation()) {
         return annotationArrayMemberEquals((Annotation[])var1, (Annotation[])var2);
      } else if (var0.equals(byte.class)) {
         return Arrays.equals((byte[])var1, (byte[])var2);
      } else if (var0.equals(short.class)) {
         return Arrays.equals((short[])var1, (short[])var2);
      } else if (var0.equals(int.class)) {
         return Arrays.equals((int[])var1, (int[])var2);
      } else if (var0.equals(char.class)) {
         return Arrays.equals((char[])var1, (char[])var2);
      } else if (var0.equals(long.class)) {
         return Arrays.equals((long[])var1, (long[])var2);
      } else if (var0.equals(float.class)) {
         return Arrays.equals((float[])var1, (float[])var2);
      } else if (var0.equals(double.class)) {
         return Arrays.equals((double[])var1, (double[])var2);
      } else {
         return var0.equals(boolean.class) ? Arrays.equals((boolean[])var1, (boolean[])var2) : Arrays.equals((Object[])var1, (Object[])var2);
      }
   }

   private static boolean annotationArrayMemberEquals(Annotation[] var0, Annotation[] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!equals(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      }
   }

   private static int arrayMemberHash(Class<?> var0, Object var1) {
      if (var0.equals(byte.class)) {
         return Arrays.hashCode((byte[])var1);
      } else if (var0.equals(short.class)) {
         return Arrays.hashCode((short[])var1);
      } else if (var0.equals(int.class)) {
         return Arrays.hashCode((int[])var1);
      } else if (var0.equals(char.class)) {
         return Arrays.hashCode((char[])var1);
      } else if (var0.equals(long.class)) {
         return Arrays.hashCode((long[])var1);
      } else if (var0.equals(float.class)) {
         return Arrays.hashCode((float[])var1);
      } else if (var0.equals(double.class)) {
         return Arrays.hashCode((double[])var1);
      } else {
         return var0.equals(boolean.class) ? Arrays.hashCode((boolean[])var1) : Arrays.hashCode((Object[])var1);
      }
   }
}
