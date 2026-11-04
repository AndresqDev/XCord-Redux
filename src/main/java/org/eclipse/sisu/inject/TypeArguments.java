package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import xcord.Qualifier;

public final class TypeArguments {
   private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
   private static final TypeLiteral<?>[] NO_TYPE_LITERALS = new TypeLiteral[0];

   private TypeArguments() {
   }

   public static TypeLiteral<?>[] get(TypeLiteral<?> var0) {
      Type var1 = var0.getType();
      if (!(var1 instanceof ParameterizedType)) {
         return var1 instanceof GenericArrayType ? new TypeLiteral[]{expand(((GenericArrayType)var1).getGenericComponentType())} : NO_TYPE_LITERALS;
      } else {
         Type[] var2 = ((ParameterizedType)var1).getActualTypeArguments();
         TypeLiteral[] var3 = new TypeLiteral[var2.length];

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3[var4] = expand(var2[var4]);
         }

         return var3;
      }
   }

   public static TypeLiteral<?> get(TypeLiteral<?> var0, int var1) {
      Type var2 = var0.getType();
      if (var2 instanceof ParameterizedType) {
         return expand(((ParameterizedType)var2).getActualTypeArguments()[var1]);
      } else if (var2 instanceof GenericArrayType) {
         if (var1 == 0) {
            return expand(((GenericArrayType)var2).getGenericComponentType());
         } else {
            throw new ArrayIndexOutOfBoundsException(var1);
         }
      } else {
         return OBJECT_TYPE_LITERAL;
      }
   }

   public static boolean isAssignableFrom(TypeLiteral<?> var0, TypeLiteral<?> var1) {
      Class var2 = var0.getRawType();
      if (!var2.isAssignableFrom(var1.getRawType())) {
         return false;
      } else {
         Type var3 = var0.getType();
         if (var2 == var3) {
            return true;
         } else {
            if (var3 instanceof ParameterizedType) {
               Type var4 = var1.getSupertype(var2).getType();
               if (var4 instanceof ParameterizedType) {
                  Type[] var5 = ((ParameterizedType)var3).getActualTypeArguments();
                  Type[] var6 = ((ParameterizedType)var4).getActualTypeArguments();
                  return isAssignableFrom(var5, var6);
               }
            } else if (var3 instanceof GenericArrayType) {
               Type var7 = var1.getSupertype(var2).getType();
               if (var7 instanceof GenericArrayType) {
                  Type var8 = ((GenericArrayType)var3).getGenericComponentType();
                  Type var9 = ((GenericArrayType)var7).getGenericComponentType();
                  return isAssignableFrom(new Type[]{var8}, new Type[]{var9});
               }
            }

            return false;
         }
      }
   }

   public static boolean isConcrete(TypeLiteral<?> var0) {
      return isConcrete(var0.getRawType());
   }

   public static boolean isConcrete(Class<?> var0) {
      return !var0.isInterface() && !Modifier.isAbstract(var0.getModifiers());
   }

   public static boolean isImplicit(TypeLiteral<?> var0) {
      return isImplicit(var0.getRawType());
   }

   public static boolean isImplicit(Class<?> var0) {
      return isConcrete(var0) || var0.isAnnotationPresent(ImplementedBy.class) || var0.isAnnotationPresent(ProvidedBy.class);
   }

   public static <T> Key<T> implicitKey(Class<T> var0) {
      return Key.get(var0, TypeArguments.Implicit.class);
   }

   private static TypeLiteral<?> expand(Type var0) {
      if (var0 instanceof WildcardType) {
         return TypeLiteral.get(((WildcardType)var0).getUpperBounds()[0]);
      } else {
         return var0 instanceof TypeVariable ? TypeLiteral.get(((TypeVariable)var0).getBounds()[0]) : TypeLiteral.get(var0);
      }
   }

   private static boolean isAssignableFrom(Type[] var0, Type[] var1) {
      int var2 = 0;

      for (int var3 = Math.min(var0.length, var1.length); var2 < var3; var2++) {
         Type var4 = var0[var2];
         Type var5 = var1[var2];
         if (!(var5 instanceof TypeVariable) || !isAssignableFrom(expand(var5), expand(var4))) {
            if (!(var4 instanceof WildcardType) && !(var4 instanceof TypeVariable)) {
               if (!var4.equals(var5)) {
                  return false;
               }
            } else if (!isAssignableFrom(expand(var4), expand(var5))) {
               return false;
            }
         }
      }

      return true;
   }

   @Qualifier
   @Retention(RetentionPolicy.RUNTIME)
   private @interface Implicit {
   }
}
