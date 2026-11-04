package org.apache.logging.log4j.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TypeUtil {
   private TypeUtil() {
   }

   public static List<Field> getAllDeclaredFields(Class<?> var0) {
      ArrayList var1 = new ArrayList();

      while (var0 != null) {
         Collections.addAll(var1, var0.getDeclaredFields());
         var0 = var0.getSuperclass();
      }

      return var1;
   }

   public static boolean isAssignable(Type var0, Type var1) {
      Objects.requireNonNull(var0, "No left hand side type provided");
      Objects.requireNonNull(var1, "No right hand side type provided");
      if (var0.equals(var1)) {
         return true;
      } else if (Object.class.equals(var0)) {
         return true;
      } else {
         if (var0 instanceof Class) {
            Class var2 = (Class)var0;
            if (var1 instanceof Class) {
               Class var9 = (Class)var1;
               return var2.isAssignableFrom(var9);
            }

            if (var1 instanceof ParameterizedType) {
               Type var3 = ((ParameterizedType)var1).getRawType();
               if (var3 instanceof Class) {
                  return var2.isAssignableFrom((Class<?>)var3);
               }
            }

            if (var2.isArray() && var1 instanceof GenericArrayType) {
               return isAssignable(var2.getComponentType(), ((GenericArrayType)var1).getGenericComponentType());
            }
         }

         if (var0 instanceof ParameterizedType) {
            ParameterizedType var4 = (ParameterizedType)var0;
            if (var1 instanceof Class) {
               Type var6 = var4.getRawType();
               if (var6 instanceof Class) {
                  return ((Class)var6).isAssignableFrom((Class<?>)var1);
               }
            } else if (var1 instanceof ParameterizedType) {
               ParameterizedType var8 = (ParameterizedType)var1;
               return isParameterizedAssignable(var4, var8);
            }
         }

         if (var0 instanceof GenericArrayType) {
            Type var5 = ((GenericArrayType)var0).getGenericComponentType();
            if (var1 instanceof Class) {
               Class var7 = (Class)var1;
               if (var7.isArray()) {
                  return isAssignable(var5, var7.getComponentType());
               }
            } else if (var1 instanceof GenericArrayType) {
               return isAssignable(var5, ((GenericArrayType)var1).getGenericComponentType());
            }
         }

         return var0 instanceof WildcardType ? isWildcardAssignable((WildcardType)var0, var1) : false;
      }
   }

   private static boolean isParameterizedAssignable(ParameterizedType var0, ParameterizedType var1) {
      if (var0.equals(var1)) {
         return true;
      } else {
         Type[] var2 = var0.getActualTypeArguments();
         Type[] var3 = var1.getActualTypeArguments();
         int var4 = var2.length;
         if (var3.length != var4) {
            return false;
         } else {
            for (int var5 = 0; var5 < var4; var5++) {
               Type var6 = var2[var5];
               Type var7 = var3[var5];
               if (!var6.equals(var7) && (!(var6 instanceof WildcardType) || !isWildcardAssignable((WildcardType)var6, var7))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean isWildcardAssignable(WildcardType var0, Type var1) {
      Type[] var2 = getEffectiveUpperBounds(var0);
      Type[] var3 = getEffectiveLowerBounds(var0);
      if (var1 instanceof WildcardType) {
         WildcardType var4 = (WildcardType)var1;
         Type[] var5 = getEffectiveUpperBounds(var4);
         Type[] var6 = getEffectiveLowerBounds(var4);

         for (Type var10 : var2) {
            for (Type var14 : var5) {
               if (!isBoundAssignable(var10, var14)) {
                  return false;
               }
            }

            for (Type var36 : var6) {
               if (!isBoundAssignable(var10, var36)) {
                  return false;
               }
            }
         }

         for (Type var26 : var3) {
            for (Type var37 : var5) {
               if (!isBoundAssignable(var37, var26)) {
                  return false;
               }
            }

            for (Type var38 : var6) {
               if (!isBoundAssignable(var38, var26)) {
                  return false;
               }
            }
         }
      } else {
         for (Type var22 : var2) {
            if (!isBoundAssignable(var22, var1)) {
               return false;
            }
         }

         for (Type var23 : var3) {
            if (!isBoundAssignable(var23, var1)) {
               return false;
            }
         }
      }

      return true;
   }

   private static Type[] getEffectiveUpperBounds(WildcardType var0) {
      Type[] var1 = var0.getUpperBounds();
      return var1.length == 0 ? new Type[]{Object.class} : var1;
   }

   private static Type[] getEffectiveLowerBounds(WildcardType var0) {
      Type[] var1 = var0.getLowerBounds();
      return var1.length == 0 ? new Type[]{null} : var1;
   }

   private static boolean isBoundAssignable(Type var0, Type var1) {
      return var1 == null || var0 != null && isAssignable(var0, var1);
   }
}
