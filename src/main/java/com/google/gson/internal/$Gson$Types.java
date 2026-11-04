package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class $Gson$Types {
   static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

   private $Gson$Types() {
      throw new UnsupportedOperationException();
   }

   public static ParameterizedType newParameterizedTypeWithOwner(Type var0, Type var1, Type... var2) {
      return new $Gson$Types.ParameterizedTypeImpl(var0, var1, var2);
   }

   public static GenericArrayType arrayOf(Type var0) {
      return new $Gson$Types.GenericArrayTypeImpl(var0);
   }

   public static WildcardType subtypeOf(Type var0) {
      Type[] var1;
      if (var0 instanceof WildcardType) {
         var1 = ((WildcardType)var0).getUpperBounds();
      } else {
         var1 = new Type[]{var0};
      }

      return new $Gson$Types.WildcardTypeImpl(var1, EMPTY_TYPE_ARRAY);
   }

   public static WildcardType supertypeOf(Type var0) {
      Type[] var1;
      if (var0 instanceof WildcardType) {
         var1 = ((WildcardType)var0).getLowerBounds();
      } else {
         var1 = new Type[]{var0};
      }

      return new $Gson$Types.WildcardTypeImpl(new Type[]{Object.class}, var1);
   }

   public static Type canonicalize(Type var0) {
      if (var0 instanceof Class) {
         Class var4 = (Class)var0;
         return (Type)(var4.isArray() ? new $Gson$Types.GenericArrayTypeImpl(canonicalize(var4.getComponentType())) : var4);
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var3 = (ParameterizedType)var0;
         return new $Gson$Types.ParameterizedTypeImpl(var3.getOwnerType(), var3.getRawType(), var3.getActualTypeArguments());
      } else if (var0 instanceof GenericArrayType) {
         GenericArrayType var2 = (GenericArrayType)var0;
         return new $Gson$Types.GenericArrayTypeImpl(var2.getGenericComponentType());
      } else if (var0 instanceof WildcardType) {
         WildcardType var1 = (WildcardType)var0;
         return new $Gson$Types.WildcardTypeImpl(var1.getUpperBounds(), var1.getLowerBounds());
      } else {
         return var0;
      }
   }

   public static Class<?> getRawType(Type var0) {
      if (var0 instanceof Class) {
         return (Class<?>)var0;
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var4 = (ParameterizedType)var0;
         Type var2 = var4.getRawType();
         $Gson$Preconditions.checkArgument(var2 instanceof Class);
         return (Class<?>)var2;
      } else if (var0 instanceof GenericArrayType) {
         Type var3 = ((GenericArrayType)var0).getGenericComponentType();
         return Array.newInstance(getRawType(var3), 0).getClass();
      } else if (var0 instanceof TypeVariable) {
         return Object.class;
      } else if (var0 instanceof WildcardType) {
         return getRawType(((WildcardType)var0).getUpperBounds()[0]);
      } else {
         String var1 = var0 == null ? "null" : var0.getClass().getName();
         throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + var0 + "> is of type " + var1);
      }
   }

   static boolean equal(Object var0, Object var1) {
      return var0 == var1 || var0 != null && var0.equals(var1);
   }

   public static boolean equals(Type var0, Type var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 instanceof Class) {
         return var0.equals(var1);
      } else if (var0 instanceof ParameterizedType) {
         if (!(var1 instanceof ParameterizedType)) {
            return false;
         } else {
            ParameterizedType var6 = (ParameterizedType)var0;
            ParameterizedType var9 = (ParameterizedType)var1;
            return equal(var6.getOwnerType(), var9.getOwnerType())
               && var6.getRawType().equals(var9.getRawType())
               && Arrays.equals((Object[])var6.getActualTypeArguments(), (Object[])var9.getActualTypeArguments());
         }
      } else if (var0 instanceof GenericArrayType) {
         if (!(var1 instanceof GenericArrayType)) {
            return false;
         } else {
            GenericArrayType var5 = (GenericArrayType)var0;
            GenericArrayType var8 = (GenericArrayType)var1;
            return equals(var5.getGenericComponentType(), var8.getGenericComponentType());
         }
      } else if (var0 instanceof WildcardType) {
         if (!(var1 instanceof WildcardType)) {
            return false;
         } else {
            WildcardType var4 = (WildcardType)var0;
            WildcardType var7 = (WildcardType)var1;
            return Arrays.equals((Object[])var4.getUpperBounds(), (Object[])var7.getUpperBounds())
               && Arrays.equals((Object[])var4.getLowerBounds(), (Object[])var7.getLowerBounds());
         }
      } else if (var0 instanceof TypeVariable) {
         if (!(var1 instanceof TypeVariable)) {
            return false;
         } else {
            TypeVariable var2 = (TypeVariable)var0;
            TypeVariable var3 = (TypeVariable)var1;
            return var2.getGenericDeclaration() == var3.getGenericDeclaration() && var2.getName().equals(var3.getName());
         }
      } else {
         return false;
      }
   }

   static int hashCodeOrZero(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }

   public static String typeToString(Type var0) {
      return var0 instanceof Class ? ((Class)var0).getName() : var0.toString();
   }

   static Type getGenericSupertype(Type var0, Class<?> var1, Class<?> var2) {
      if (var2 == var1) {
         return var0;
      } else {
         if (var2.isInterface()) {
            Class[] var3 = var1.getInterfaces();
            int var4 = 0;

            for (int var5 = var3.length; var4 < var5; var4++) {
               if (var3[var4] == var2) {
                  return var1.getGenericInterfaces()[var4];
               }

               if (var2.isAssignableFrom(var3[var4])) {
                  return getGenericSupertype(var1.getGenericInterfaces()[var4], var3[var4], var2);
               }
            }
         }

         if (!var1.isInterface()) {
            while (var1 != Object.class) {
               Class var6 = var1.getSuperclass();
               if (var6 == var2) {
                  return var1.getGenericSuperclass();
               }

               if (var2.isAssignableFrom(var6)) {
                  return getGenericSupertype(var1.getGenericSuperclass(), var6, var2);
               }

               var1 = var6;
            }
         }

         return var2;
      }
   }

   static Type getSupertype(Type var0, Class<?> var1, Class<?> var2) {
      if (var0 instanceof WildcardType) {
         var0 = ((WildcardType)var0).getUpperBounds()[0];
      }

      $Gson$Preconditions.checkArgument(var2.isAssignableFrom(var1));
      return resolve(var0, var1, getGenericSupertype(var0, var1, var2));
   }

   public static Type getArrayComponentType(Type var0) {
      return (Type)(var0 instanceof GenericArrayType ? ((GenericArrayType)var0).getGenericComponentType() : ((Class)var0).getComponentType());
   }

   public static Type getCollectionElementType(Type var0, Class<?> var1) {
      Type var2 = getSupertype(var0, var1, Collection.class);
      if (var2 instanceof WildcardType) {
         var2 = ((WildcardType)var2).getUpperBounds()[0];
      }

      return (Type)(var2 instanceof ParameterizedType ? ((ParameterizedType)var2).getActualTypeArguments()[0] : Object.class);
   }

   public static Type[] getMapKeyAndValueTypes(Type var0, Class<?> var1) {
      if (var0 == Properties.class) {
         return new Type[]{String.class, String.class};
      } else {
         Type var2 = getSupertype(var0, var1, Map.class);
         if (var2 instanceof ParameterizedType) {
            ParameterizedType var3 = (ParameterizedType)var2;
            return var3.getActualTypeArguments();
         } else {
            return new Type[]{Object.class, Object.class};
         }
      }
   }

   public static Type resolve(Type var0, Class<?> var1, Type var2) {
      return resolve(var0, var1, var2, new HashMap<>());
   }

   private static Type resolve(Type var0, Class<?> var1, Type var2, Map<TypeVariable<?>, Type> var3) {
      TypeVariable var4 = null;

      while (true) {
         if (var2 instanceof TypeVariable) {
            TypeVariable var5 = (TypeVariable)var2;
            Type var6 = (Type)var3.get(var5);
            if (var6 != null) {
               return (Type)(var6 == void.class ? var2 : var6);
            }

            var3.put(var5, void.class);
            if (var4 == null) {
               var4 = var5;
            }

            var2 = resolveTypeVariable(var0, var1, var5);
            if (var2 != var5) {
               continue;
            }
         } else if (var2 instanceof Class && ((Class)var2).isArray()) {
            Class var16 = (Class)var2;
            Class var20 = var16.getComponentType();
            Type var23 = resolve(var0, var1, var20, var3);
            var2 = equal(var20, var23) ? var16 : arrayOf(var23);
         } else if (var2 instanceof GenericArrayType) {
            GenericArrayType var13 = (GenericArrayType)var2;
            Type var17 = var13.getGenericComponentType();
            Type var7 = resolve(var0, var1, var17, var3);
            var2 = equal(var17, var7) ? var13 : arrayOf(var7);
         } else if (var2 instanceof ParameterizedType) {
            ParameterizedType var14 = (ParameterizedType)var2;
            Type var18 = var14.getOwnerType();
            Type var21 = resolve(var0, var1, var18, var3);
            boolean var8 = !equal(var21, var18);
            Type[] var9 = var14.getActualTypeArguments();
            int var10 = 0;

            for (int var11 = var9.length; var10 < var11; var10++) {
               Type var12 = resolve(var0, var1, var9[var10], var3);
               if (!equal(var12, var9[var10])) {
                  if (!var8) {
                     var9 = (Type[])var9.clone();
                     var8 = true;
                  }

                  var9[var10] = var12;
               }
            }

            var2 = var8 ? newParameterizedTypeWithOwner(var21, var14.getRawType(), var9) : var14;
         } else {
            label83:
            if (var2 instanceof WildcardType) {
               WildcardType var15 = (WildcardType)var2;
               Type[] var19 = var15.getLowerBounds();
               Type[] var22 = var15.getUpperBounds();
               if (var19.length == 1) {
                  Type var24 = resolve(var0, var1, var19[0], var3);
                  if (var24 != var19[0]) {
                     var2 = supertypeOf(var24);
                     break label83;
                  }
               } else if (var22.length == 1) {
                  Type var25 = resolve(var0, var1, var22[0], var3);
                  if (var25 != var22[0]) {
                     var2 = subtypeOf(var25);
                     break label83;
                  }
               }

               var2 = var15;
            }
         }

         if (var4 != null) {
            var3.put(var4, var2);
         }

         return (Type)var2;
      }
   }

   static Type resolveTypeVariable(Type var0, Class<?> var1, TypeVariable<?> var2) {
      Class var3 = declaringClassOf(var2);
      if (var3 == null) {
         return var2;
      } else {
         Type var4 = getGenericSupertype(var0, var1, var3);
         if (var4 instanceof ParameterizedType) {
            int var5 = indexOf(var3.getTypeParameters(), var2);
            return ((ParameterizedType)var4).getActualTypeArguments()[var5];
         } else {
            return var2;
         }
      }
   }

   private static int indexOf(Object[] var0, Object var1) {
      int var2 = 0;

      for (int var3 = var0.length; var2 < var3; var2++) {
         if (var1.equals(var0[var2])) {
            return var2;
         }
      }

      throw new NoSuchElementException();
   }

   private static Class<?> declaringClassOf(TypeVariable<?> var0) {
      GenericDeclaration var1 = var0.getGenericDeclaration();
      return var1 instanceof Class ? (Class)var1 : null;
   }

   static void checkNotPrimitive(Type var0) {
      $Gson$Preconditions.checkArgument(!(var0 instanceof Class) || !((Class)var0).isPrimitive());
   }

   private static final class GenericArrayTypeImpl implements Serializable, GenericArrayType {
      private final Type componentType;
      private static final long serialVersionUID = 0L;

      public GenericArrayTypeImpl(Type var1) {
         this.componentType = $Gson$Types.canonicalize(var1);
      }

      @Override
      public Type getGenericComponentType() {
         return this.componentType;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof GenericArrayType && $Gson$Types.equals(this, (GenericArrayType)var1);
      }

      @Override
      public int hashCode() {
         return this.componentType.hashCode();
      }

      @Override
      public String toString() {
         return $Gson$Types.typeToString(this.componentType) + "[]";
      }
   }

   private static final class ParameterizedTypeImpl implements Serializable, ParameterizedType {
      private final Type ownerType;
      private final Type rawType;
      private final Type[] typeArguments;
      private static final long serialVersionUID = 0L;

      public ParameterizedTypeImpl(Type var1, Type var2, Type... var3) {
         if (var2 instanceof Class) {
            Class var4 = (Class)var2;
            boolean var5 = Modifier.isStatic(var4.getModifiers()) || var4.getEnclosingClass() == null;
            $Gson$Preconditions.checkArgument(var1 != null || var5);
         }

         this.ownerType = var1 == null ? null : $Gson$Types.canonicalize(var1);
         this.rawType = $Gson$Types.canonicalize(var2);
         this.typeArguments = (Type[])var3.clone();
         int var6 = 0;

         for (int var7 = this.typeArguments.length; var6 < var7; var6++) {
            $Gson$Preconditions.checkNotNull(this.typeArguments[var6]);
            $Gson$Types.checkNotPrimitive(this.typeArguments[var6]);
            this.typeArguments[var6] = $Gson$Types.canonicalize(this.typeArguments[var6]);
         }
      }

      @Override
      public Type[] getActualTypeArguments() {
         return (Type[])this.typeArguments.clone();
      }

      @Override
      public Type getRawType() {
         return this.rawType;
      }

      @Override
      public Type getOwnerType() {
         return this.ownerType;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof ParameterizedType && $Gson$Types.equals(this, (ParameterizedType)var1);
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType.hashCode() ^ $Gson$Types.hashCodeOrZero(this.ownerType);
      }

      @Override
      public String toString() {
         int var1 = this.typeArguments.length;
         if (var1 == 0) {
            return $Gson$Types.typeToString(this.rawType);
         } else {
            StringBuilder var2 = new StringBuilder(30 * (var1 + 1));
            var2.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));

            for (int var3 = 1; var3 < var1; var3++) {
               var2.append(", ").append($Gson$Types.typeToString(this.typeArguments[var3]));
            }

            return var2.append(">").toString();
         }
      }
   }

   private static final class WildcardTypeImpl implements Serializable, WildcardType {
      private final Type upperBound;
      private final Type lowerBound;
      private static final long serialVersionUID = 0L;

      public WildcardTypeImpl(Type[] var1, Type[] var2) {
         $Gson$Preconditions.checkArgument(var2.length <= 1);
         $Gson$Preconditions.checkArgument(var1.length == 1);
         if (var2.length == 1) {
            $Gson$Preconditions.checkNotNull(var2[0]);
            $Gson$Types.checkNotPrimitive(var2[0]);
            $Gson$Preconditions.checkArgument(var1[0] == Object.class);
            this.lowerBound = $Gson$Types.canonicalize(var2[0]);
            this.upperBound = Object.class;
         } else {
            $Gson$Preconditions.checkNotNull(var1[0]);
            $Gson$Types.checkNotPrimitive(var1[0]);
            this.lowerBound = null;
            this.upperBound = $Gson$Types.canonicalize(var1[0]);
         }
      }

      @Override
      public Type[] getUpperBounds() {
         return new Type[]{this.upperBound};
      }

      @Override
      public Type[] getLowerBounds() {
         return this.lowerBound != null ? new Type[]{this.lowerBound} : $Gson$Types.EMPTY_TYPE_ARRAY;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof WildcardType && $Gson$Types.equals(this, (WildcardType)var1);
      }

      @Override
      public int hashCode() {
         return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
      }

      @Override
      public String toString() {
         if (this.lowerBound != null) {
            return "? super " + $Gson$Types.typeToString(this.lowerBound);
         } else {
            return this.upperBound == Object.class ? "?" : "? extends " + $Gson$Types.typeToString(this.upperBound);
         }
      }
   }
}
