package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

@ElementTypesAreNonnullByDefault
@Beta
public final class TypeResolver {
   private final TypeResolver.TypeTable typeTable;

   public TypeResolver() {
      this.typeTable = new TypeResolver.TypeTable();
   }

   private TypeResolver(TypeResolver.TypeTable var1) {
      this.typeTable = var1;
   }

   static TypeResolver covariantly(Type var0) {
      return new TypeResolver().where(TypeResolver.TypeMappingIntrospector.getTypeMappings(var0));
   }

   static TypeResolver invariantly(Type var0) {
      Type var1 = TypeResolver.WildcardCapturer.INSTANCE.capture(var0);
      return new TypeResolver().where(TypeResolver.TypeMappingIntrospector.getTypeMappings(var1));
   }

   public TypeResolver where(Type var1, Type var2) {
      HashMap var3 = Maps.newHashMap();
      populateTypeMappings(var3, Preconditions.checkNotNull(var1), Preconditions.checkNotNull(var2));
      return this.where(var3);
   }

   TypeResolver where(Map<TypeResolver.TypeVariableKey, ? extends Type> var1) {
      return new TypeResolver(this.typeTable.where(var1));
   }

   private static void populateTypeMappings(final Map<TypeResolver.TypeVariableKey, Type> var0, Type var1, final Type var2) {
      if (!var1.equals(var2)) {
         (new TypeVisitor() {
               @Override
               void visitTypeVariable(TypeVariable<?> var1) {
                  var0.put(new TypeResolver.TypeVariableKey(var1), var2);
               }

               @Override
               void visitWildcardType(WildcardType var1) {
                  if (var2 instanceof WildcardType) {
                     WildcardType var2x = (WildcardType)var2;
                     Type[] var3 = var1.getUpperBounds();
                     Type[] var4 = var2x.getUpperBounds();
                     Type[] var5 = var1.getLowerBounds();
                     Type[] var6 = var2x.getLowerBounds();
                     Preconditions.checkArgument(var3.length == var4.length && var5.length == var6.length, "Incompatible type: %s vs. %s", var1, var2);

                     for (int var7 = 0; var7 < var3.length; var7++) {
                        TypeResolver.populateTypeMappings(var0, var3[var7], var4[var7]);
                     }

                     for (int var8 = 0; var8 < var5.length; var8++) {
                        TypeResolver.populateTypeMappings(var0, var5[var8], var6[var8]);
                     }
                  }
               }

               @Override
               void visitParameterizedType(ParameterizedType var1) {
                  if (!(var2 instanceof WildcardType)) {
                     ParameterizedType var2x = TypeResolver.expectArgument(ParameterizedType.class, var2);
                     if (var1.getOwnerType() != null && var2x.getOwnerType() != null) {
                        TypeResolver.populateTypeMappings(var0, var1.getOwnerType(), var2x.getOwnerType());
                     }

                     Preconditions.checkArgument(var1.getRawType().equals(var2x.getRawType()), "Inconsistent raw type: %s vs. %s", var1, var2);
                     Type[] var3 = var1.getActualTypeArguments();
                     Type[] var4 = var2x.getActualTypeArguments();
                     Preconditions.checkArgument(var3.length == var4.length, "%s not compatible with %s", var1, var2x);

                     for (int var5 = 0; var5 < var3.length; var5++) {
                        TypeResolver.populateTypeMappings(var0, var3[var5], var4[var5]);
                     }
                  }
               }

               @Override
               void visitGenericArrayType(GenericArrayType var1) {
                  if (!(var2 instanceof WildcardType)) {
                     Type var2x = Types.getComponentType(var2);
                     Preconditions.checkArgument(var2x != null, "%s is not an array type.", var2);
                     TypeResolver.populateTypeMappings(var0, var1.getGenericComponentType(), var2x);
                  }
               }

               @Override
               void visitClass(Class<?> var1) {
                  if (!(var2 instanceof WildcardType)) {
                     String var2x = String.valueOf(var1);
                     String var3 = String.valueOf(var2);
                     throw new IllegalArgumentException(
                        new StringBuilder(25 + String.valueOf(var2x).length() + String.valueOf(var3).length())
                           .append("No type mapping from ")
                           .append(var2x)
                           .append(" to ")
                           .append(var3)
                           .toString()
                     );
                  }
               }
            })
            .visit(new Type[]{var1});
      }
   }

   public Type resolveType(Type var1) {
      Preconditions.checkNotNull(var1);
      if (var1 instanceof TypeVariable) {
         return this.typeTable.resolve((TypeVariable<?>)var1);
      } else if (var1 instanceof ParameterizedType) {
         return this.resolveParameterizedType((ParameterizedType)var1);
      } else if (var1 instanceof GenericArrayType) {
         return this.resolveGenericArrayType((GenericArrayType)var1);
      } else {
         return (Type)(var1 instanceof WildcardType ? this.resolveWildcardType((WildcardType)var1) : var1);
      }
   }

   Type[] resolveTypesInPlace(Type[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.resolveType(var1[var2]);
      }

      return var1;
   }

   private Type[] resolveTypes(Type[] var1) {
      Type[] var2 = new Type[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = this.resolveType(var1[var3]);
      }

      return var2;
   }

   private WildcardType resolveWildcardType(WildcardType var1) {
      Type[] var2 = var1.getLowerBounds();
      Type[] var3 = var1.getUpperBounds();
      return new Types.WildcardTypeImpl(this.resolveTypes(var2), this.resolveTypes(var3));
   }

   private Type resolveGenericArrayType(GenericArrayType var1) {
      Type var2 = var1.getGenericComponentType();
      Type var3 = this.resolveType(var2);
      return Types.newArrayType(var3);
   }

   private ParameterizedType resolveParameterizedType(ParameterizedType var1) {
      Type var2 = var1.getOwnerType();
      Type var3 = var2 == null ? null : this.resolveType(var2);
      Type var4 = this.resolveType(var1.getRawType());
      Type[] var5 = var1.getActualTypeArguments();
      Type[] var6 = this.resolveTypes(var5);
      return Types.newParameterizedTypeWithOwner(var3, (Class<?>)var4, var6);
   }

   private static <T> T expectArgument(Class<T> var0, Object var1) {
      try {
         return (T)var0.cast(var1);
      } catch (ClassCastException var5) {
         String var3 = String.valueOf(var1);
         String var4 = var0.getSimpleName();
         throw new IllegalArgumentException(
            new StringBuilder(10 + String.valueOf(var3).length() + String.valueOf(var4).length()).append(var3).append(" is not a ").append(var4).toString()
         );
      }
   }

   private static final class TypeMappingIntrospector extends TypeVisitor {
      private final Map<TypeResolver.TypeVariableKey, Type> mappings = Maps.newHashMap();

      static ImmutableMap<TypeResolver.TypeVariableKey, Type> getTypeMappings(Type var0) {
         Preconditions.checkNotNull(var0);
         TypeResolver.TypeMappingIntrospector var1 = new TypeResolver.TypeMappingIntrospector();
         var1.visit(new Type[]{var0});
         return ImmutableMap.copyOf(var1.mappings);
      }

      @Override
      void visitClass(Class<?> var1) {
         this.visit(new Type[]{var1.getGenericSuperclass()});
         this.visit(var1.getGenericInterfaces());
      }

      @Override
      void visitParameterizedType(ParameterizedType var1) {
         Class var2 = (Class)var1.getRawType();
         TypeVariable[] var3 = var2.getTypeParameters();
         Type[] var4 = var1.getActualTypeArguments();
         Preconditions.checkState(var3.length == var4.length);

         for (int var5 = 0; var5 < var3.length; var5++) {
            this.map(new TypeResolver.TypeVariableKey(var3[var5]), var4[var5]);
         }

         this.visit(new Type[]{var2});
         this.visit(new Type[]{var1.getOwnerType()});
      }

      @Override
      void visitTypeVariable(TypeVariable<?> var1) {
         this.visit(var1.getBounds());
      }

      @Override
      void visitWildcardType(WildcardType var1) {
         this.visit(var1.getUpperBounds());
      }

      private void map(TypeResolver.TypeVariableKey var1, Type var2) {
         if (!this.mappings.containsKey(var1)) {
            for (Type var3 = var2; var3 != null; var3 = this.mappings.get(TypeResolver.TypeVariableKey.forLookup(var3))) {
               if (var1.equalsType(var3)) {
                  Type var4 = var2;

                  while (var4 != null) {
                     var4 = this.mappings.remove(TypeResolver.TypeVariableKey.forLookup(var4));
                  }

                  return;
               }
            }

            this.mappings.put(var1, var2);
         }
      }
   }

   private static class TypeTable {
      private final ImmutableMap<TypeResolver.TypeVariableKey, Type> map;

      TypeTable() {
         this.map = ImmutableMap.of();
      }

      private TypeTable(ImmutableMap<TypeResolver.TypeVariableKey, Type> var1) {
         this.map = var1;
      }

      final TypeResolver.TypeTable where(Map<TypeResolver.TypeVariableKey, ? extends Type> var1) {
         ImmutableMap.Builder var2 = ImmutableMap.builder();
         var2.putAll(this.map);

         for (Entry var4 : var1.entrySet()) {
            TypeResolver.TypeVariableKey var5 = (TypeResolver.TypeVariableKey)var4.getKey();
            Type var6 = (Type)var4.getValue();
            Preconditions.checkArgument(!var5.equalsType(var6), "Type variable %s bound to itself", var5);
            var2.put(var5, var6);
         }

         return new TypeResolver.TypeTable(var2.build());
      }

      final Type resolve(final TypeVariable<?> var1) {
         TypeResolver.TypeTable var3 = new TypeResolver.TypeTable(this) {
            @Override
            public Type resolveInternal(TypeVariable<?> var1x, TypeResolver.TypeTable var2) {
               return (Type)(var1x.getGenericDeclaration().equals(var1.getGenericDeclaration()) ? var1x : TypeTable.this.resolveInternal(var1x, var2));
            }
         };
         return this.resolveInternal(var1, var3);
      }

      Type resolveInternal(TypeVariable<?> var1, TypeResolver.TypeTable var2) {
         Type var3 = this.map.get(new TypeResolver.TypeVariableKey(var1));
         if (var3 == null) {
            Type[] var4 = var1.getBounds();
            if (var4.length == 0) {
               return var1;
            } else {
               Type[] var5 = new TypeResolver(var2).resolveTypes(var4);
               return Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY && Arrays.equals((Object[])var4, (Object[])var5)
                  ? var1
                  : Types.newArtificialTypeVariable(var1.getGenericDeclaration(), var1.getName(), var5);
            }
         } else {
            return new TypeResolver(var2).resolveType(var3);
         }
      }
   }

   static final class TypeVariableKey {
      private final TypeVariable<?> var;

      TypeVariableKey(TypeVariable<?> var1) {
         this.var = Preconditions.checkNotNull(var1);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.var.getGenericDeclaration(), this.var.getName());
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof TypeResolver.TypeVariableKey) {
            TypeResolver.TypeVariableKey var2 = (TypeResolver.TypeVariableKey)var1;
            return this.equalsTypeVariable(var2.var);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return this.var.toString();
      }

      static TypeResolver.TypeVariableKey forLookup(Type var0) {
         return var0 instanceof TypeVariable ? new TypeResolver.TypeVariableKey((TypeVariable<?>)var0) : null;
      }

      boolean equalsType(Type var1) {
         return var1 instanceof TypeVariable ? this.equalsTypeVariable((TypeVariable<?>)var1) : false;
      }

      private boolean equalsTypeVariable(TypeVariable<?> var1) {
         return this.var.getGenericDeclaration().equals(var1.getGenericDeclaration()) && this.var.getName().equals(var1.getName());
      }
   }

   private static class WildcardCapturer {
      static final TypeResolver.WildcardCapturer INSTANCE = new TypeResolver.WildcardCapturer();
      private final AtomicInteger id;

      private WildcardCapturer() {
         this(new AtomicInteger());
      }

      private WildcardCapturer(AtomicInteger var1) {
         this.id = var1;
      }

      final Type capture(Type var1) {
         Preconditions.checkNotNull(var1);
         if (var1 instanceof Class) {
            return var1;
         } else if (var1 instanceof TypeVariable) {
            return var1;
         } else if (var1 instanceof GenericArrayType) {
            GenericArrayType var8 = (GenericArrayType)var1;
            return Types.newArrayType(this.notForTypeVariable().capture(var8.getGenericComponentType()));
         } else if (!(var1 instanceof ParameterizedType)) {
            if (var1 instanceof WildcardType) {
               WildcardType var7 = (WildcardType)var1;
               Type[] var9 = var7.getLowerBounds();
               return (Type)(var9.length == 0 ? this.captureAsTypeVariable(var7.getUpperBounds()) : var1);
            } else {
               throw new AssertionError("must have been one of the known types");
            }
         } else {
            ParameterizedType var2 = (ParameterizedType)var1;
            Class var3 = (Class)var2.getRawType();
            TypeVariable[] var4 = var3.getTypeParameters();
            Type[] var5 = var2.getActualTypeArguments();

            for (int var6 = 0; var6 < var5.length; var6++) {
               var5[var6] = this.forTypeVariable(var4[var6]).capture(var5[var6]);
            }

            return Types.newParameterizedTypeWithOwner(this.notForTypeVariable().captureNullable(var2.getOwnerType()), var3, var5);
         }
      }

      TypeVariable<?> captureAsTypeVariable(Type[] var1) {
         int var3 = this.id.incrementAndGet();
         String var4 = Joiner.on('&').join(var1);
         String var2 = new StringBuilder(33 + String.valueOf(var4).length()).append("capture#").append(var3).append("-of ? extends ").append(var4).toString();
         return Types.newArtificialTypeVariable(TypeResolver.WildcardCapturer.class, var2, var1);
      }

      private TypeResolver.WildcardCapturer forTypeVariable(final TypeVariable<?> var1) {
         return new TypeResolver.WildcardCapturer(this, this.id) {
            @Override
            TypeVariable<?> captureAsTypeVariable(Type[] var1x) {
               LinkedHashSet var2 = new LinkedHashSet<>(Arrays.asList(var1x));
               var2.addAll(Arrays.asList(var1.getBounds()));
               if (var2.size() > 1) {
                  var2.remove(Object.class);
               }

               return super.captureAsTypeVariable(var2.toArray(new Type[0]));
            }
         };
      }

      private TypeResolver.WildcardCapturer notForTypeVariable() {
         return new TypeResolver.WildcardCapturer(this.id);
      }

      private Type captureNullable(Type var1) {
         return var1 == null ? null : this.capture(var1);
      }
   }
}
