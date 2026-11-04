package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class TypeToken<T> extends TypeCapture<T> implements Serializable {
   private final Type runtimeType;
   private transient TypeResolver invariantTypeResolver;
   private transient TypeResolver covariantTypeResolver;
   private static final long serialVersionUID = 3637540370352322684L;

   protected TypeToken() {
      this.runtimeType = this.capture();
      Preconditions.checkState(
         !(this.runtimeType instanceof TypeVariable),
         "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.",
         this.runtimeType
      );
   }

   protected TypeToken(Class<?> var1) {
      Type var2 = super.capture();
      if (var2 instanceof Class) {
         this.runtimeType = var2;
      } else {
         this.runtimeType = TypeResolver.covariantly(var1).resolveType(var2);
      }
   }

   private TypeToken(Type var1) {
      this.runtimeType = Preconditions.checkNotNull(var1);
   }

   public static <T> TypeToken<T> of(Class<T> var0) {
      return new TypeToken.SimpleTypeToken<>(var0);
   }

   public static TypeToken<?> of(Type var0) {
      return new TypeToken.SimpleTypeToken(var0);
   }

   public final Class<? super T> getRawType() {
      return this.getRawTypes().iterator().next();
   }

   public final Type getType() {
      return this.runtimeType;
   }

   public final <X> TypeToken<T> where(TypeParameter<X> var1, TypeToken<X> var2) {
      TypeResolver var3 = new TypeResolver().where(ImmutableMap.of(new TypeResolver.TypeVariableKey(var1.typeVariable), var2.runtimeType));
      return new TypeToken.SimpleTypeToken<>(var3.resolveType(this.runtimeType));
   }

   public final <X> TypeToken<T> where(TypeParameter<X> var1, Class<X> var2) {
      return this.where(var1, of(var2));
   }

   public final TypeToken<?> resolveType(Type var1) {
      Preconditions.checkNotNull(var1);
      return of(this.getInvariantTypeResolver().resolveType(var1));
   }

   private TypeToken<?> resolveSupertype(Type var1) {
      TypeToken var2 = of(this.getCovariantTypeResolver().resolveType(var1));
      var2.covariantTypeResolver = this.covariantTypeResolver;
      var2.invariantTypeResolver = this.invariantTypeResolver;
      return var2;
   }

   final TypeToken<? super T> getGenericSuperclass() {
      if (this.runtimeType instanceof TypeVariable) {
         return this.boundAsSuperclass(((TypeVariable)this.runtimeType).getBounds()[0]);
      } else if (this.runtimeType instanceof WildcardType) {
         return this.boundAsSuperclass(((WildcardType)this.runtimeType).getUpperBounds()[0]);
      } else {
         Type var1 = this.getRawType().getGenericSuperclass();
         return (TypeToken<? super T>)(var1 == null ? null : this.resolveSupertype(var1));
      }
   }

   private TypeToken<? super T> boundAsSuperclass(Type var1) {
      TypeToken var2 = of(var1);
      return var2.getRawType().isInterface() ? null : var2;
   }

   final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
      if (this.runtimeType instanceof TypeVariable) {
         return this.boundsAsInterfaces(((TypeVariable)this.runtimeType).getBounds());
      } else if (this.runtimeType instanceof WildcardType) {
         return this.boundsAsInterfaces(((WildcardType)this.runtimeType).getUpperBounds());
      } else {
         ImmutableList.Builder var1 = ImmutableList.builder();

         for (Type var5 : this.getRawType().getGenericInterfaces()) {
            TypeToken var6 = this.resolveSupertype(var5);
            var1.add(var6);
         }

         return var1.build();
      }
   }

   private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] var1) {
      ImmutableList.Builder var2 = ImmutableList.builder();

      for (Type var6 : var1) {
         TypeToken var7 = of(var6);
         if (var7.getRawType().isInterface()) {
            var2.add(var7);
         }
      }

      return var2.build();
   }

   public final TypeToken<T>.TypeSet getTypes() {
      return new TypeToken.TypeSet();
   }

   public final TypeToken<? super T> getSupertype(Class<? super T> var1) {
      Preconditions.checkArgument(this.someRawTypeIsSubclassOf(var1), "%s is not a super class of %s", var1, this);
      if (this.runtimeType instanceof TypeVariable) {
         return this.getSupertypeFromUpperBounds(var1, ((TypeVariable)this.runtimeType).getBounds());
      } else if (this.runtimeType instanceof WildcardType) {
         return this.getSupertypeFromUpperBounds(var1, ((WildcardType)this.runtimeType).getUpperBounds());
      } else {
         return var1.isArray() ? this.getArraySupertype(var1) : this.resolveSupertype(toGenericType(var1).runtimeType);
      }
   }

   public final TypeToken<? extends T> getSubtype(Class<?> var1) {
      Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", this);
      if (this.runtimeType instanceof WildcardType) {
         return this.getSubtypeFromLowerBounds(var1, ((WildcardType)this.runtimeType).getLowerBounds());
      } else if (this.isArray()) {
         return this.getArraySubtype(var1);
      } else {
         Preconditions.checkArgument(this.getRawType().isAssignableFrom(var1), "%s isn't a subclass of %s", var1, this);
         Type var2 = this.resolveTypeArgsForSubclass(var1);
         TypeToken var3 = of(var2);
         Preconditions.checkArgument(var3.isSubtypeOf(this), "%s does not appear to be a subtype of %s", var3, this);
         return var3;
      }
   }

   public final boolean isSupertypeOf(TypeToken<?> var1) {
      return var1.isSubtypeOf(this.getType());
   }

   public final boolean isSupertypeOf(Type var1) {
      return of(var1).isSubtypeOf(this.getType());
   }

   public final boolean isSubtypeOf(TypeToken<?> var1) {
      return this.isSubtypeOf(var1.getType());
   }

   public final boolean isSubtypeOf(Type var1) {
      Preconditions.checkNotNull(var1);
      if (var1 instanceof WildcardType) {
         return any(((WildcardType)var1).getLowerBounds()).isSupertypeOf(this.runtimeType);
      } else if (this.runtimeType instanceof WildcardType) {
         return any(((WildcardType)this.runtimeType).getUpperBounds()).isSubtypeOf(var1);
      } else if (!(this.runtimeType instanceof TypeVariable)) {
         if (this.runtimeType instanceof GenericArrayType) {
            return of(var1).isSupertypeOfArray((GenericArrayType)this.runtimeType);
         } else if (var1 instanceof Class) {
            return this.someRawTypeIsSubclassOf((Class<?>)var1);
         } else if (var1 instanceof ParameterizedType) {
            return this.isSubtypeOfParameterizedType((ParameterizedType)var1);
         } else {
            return var1 instanceof GenericArrayType ? this.isSubtypeOfArrayType((GenericArrayType)var1) : false;
         }
      } else {
         return this.runtimeType.equals(var1) || any(((TypeVariable)this.runtimeType).getBounds()).isSubtypeOf(var1);
      }
   }

   public final boolean isArray() {
      return this.getComponentType() != null;
   }

   public final boolean isPrimitive() {
      return this.runtimeType instanceof Class && ((Class)this.runtimeType).isPrimitive();
   }

   public final TypeToken<T> wrap() {
      if (this.isPrimitive()) {
         Class var1 = (Class)this.runtimeType;
         return of(Primitives.wrap(var1));
      } else {
         return this;
      }
   }

   private boolean isWrapper() {
      return Primitives.allWrapperTypes().contains(this.runtimeType);
   }

   public final TypeToken<T> unwrap() {
      if (this.isWrapper()) {
         Class var1 = (Class)this.runtimeType;
         return of(Primitives.unwrap(var1));
      } else {
         return this;
      }
   }

   public final TypeToken<?> getComponentType() {
      Type var1 = Types.getComponentType(this.runtimeType);
      return var1 == null ? null : of(var1);
   }

   public final Invokable<T, Object> method(Method var1) {
      Preconditions.checkArgument(this.someRawTypeIsSubclassOf(var1.getDeclaringClass()), "%s not declared by %s", var1, this);
      return new Invokable.MethodInvokable<T>(var1) {
         @Override
         Type getGenericReturnType() {
            return TypeToken.this.getCovariantTypeResolver().resolveType(super.getGenericReturnType());
         }

         @Override
         Type[] getGenericParameterTypes() {
            return TypeToken.this.getInvariantTypeResolver().resolveTypesInPlace(super.getGenericParameterTypes());
         }

         @Override
         Type[] getGenericExceptionTypes() {
            return TypeToken.this.getCovariantTypeResolver().resolveTypesInPlace(super.getGenericExceptionTypes());
         }

         @Override
         public TypeToken<T> getOwnerType() {
            return TypeToken.this;
         }

         @Override
         public String toString() {
            String var1 = String.valueOf(this.getOwnerType());
            String var2 = super.toString();
            return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(".").append(var2).toString();
         }
      };
   }

   public final Invokable<T, T> constructor(Constructor<?> var1) {
      Preconditions.checkArgument(var1.getDeclaringClass() == this.getRawType(), "%s not declared by %s", var1, this.getRawType());
      return new Invokable.ConstructorInvokable<T>(var1) {
         @Override
         Type getGenericReturnType() {
            return TypeToken.this.getCovariantTypeResolver().resolveType(super.getGenericReturnType());
         }

         @Override
         Type[] getGenericParameterTypes() {
            return TypeToken.this.getInvariantTypeResolver().resolveTypesInPlace(super.getGenericParameterTypes());
         }

         @Override
         Type[] getGenericExceptionTypes() {
            return TypeToken.this.getCovariantTypeResolver().resolveTypesInPlace(super.getGenericExceptionTypes());
         }

         @Override
         public TypeToken<T> getOwnerType() {
            return TypeToken.this;
         }

         @Override
         public String toString() {
            String var1 = String.valueOf(this.getOwnerType());
            String var2 = Joiner.on(", ").join(this.getGenericParameterTypes());
            return new StringBuilder(2 + String.valueOf(var1).length() + String.valueOf(var2).length())
               .append(var1)
               .append("(")
               .append(var2)
               .append(")")
               .toString();
         }
      };
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof TypeToken) {
         TypeToken var2 = (TypeToken)var1;
         return this.runtimeType.equals(var2.runtimeType);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.runtimeType.hashCode();
   }

   @Override
   public String toString() {
      return Types.toString(this.runtimeType);
   }

   protected Object writeReplace() {
      return of(new TypeResolver().resolveType(this.runtimeType));
   }

   @CanIgnoreReturnValue
   final TypeToken<T> rejectTypeVariables() {
      (new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> var1) {
               String var2 = String.valueOf(TypeToken.this.runtimeType);
               throw new IllegalArgumentException(
                  new StringBuilder(58 + String.valueOf(var2).length())
                     .append(var2)
                     .append("contains a type variable and is not safe for the operation")
                     .toString()
               );
            }

            @Override
            void visitWildcardType(WildcardType var1) {
               this.visit(var1.getLowerBounds());
               this.visit(var1.getUpperBounds());
            }

            @Override
            void visitParameterizedType(ParameterizedType var1) {
               this.visit(var1.getActualTypeArguments());
               this.visit(new Type[]{var1.getOwnerType()});
            }

            @Override
            void visitGenericArrayType(GenericArrayType var1) {
               this.visit(new Type[]{var1.getGenericComponentType()});
            }
         })
         .visit(new Type[]{this.runtimeType});
      return this;
   }

   private boolean someRawTypeIsSubclassOf(Class<?> var1) {
      for (Class var3 : this.getRawTypes()) {
         if (var1.isAssignableFrom(var3)) {
            return true;
         }
      }

      return false;
   }

   private boolean isSubtypeOfParameterizedType(ParameterizedType var1) {
      Class var2 = of(var1).getRawType();
      if (!this.someRawTypeIsSubclassOf(var2)) {
         return false;
      } else {
         TypeVariable[] var3 = var2.getTypeParameters();
         Type[] var4 = var1.getActualTypeArguments();

         for (int var5 = 0; var5 < var3.length; var5++) {
            Type var6 = this.getCovariantTypeResolver().resolveType(var3[var5]);
            if (!of(var6).is(var4[var5], var3[var5])) {
               return false;
            }
         }

         return Modifier.isStatic(((Class)var1.getRawType()).getModifiers()) || var1.getOwnerType() == null || this.isOwnedBySubtypeOf(var1.getOwnerType());
      }
   }

   private boolean isSubtypeOfArrayType(GenericArrayType var1) {
      if (this.runtimeType instanceof Class) {
         Class var3 = (Class)this.runtimeType;
         return !var3.isArray() ? false : of(var3.getComponentType()).isSubtypeOf(var1.getGenericComponentType());
      } else if (this.runtimeType instanceof GenericArrayType) {
         GenericArrayType var2 = (GenericArrayType)this.runtimeType;
         return of(var2.getGenericComponentType()).isSubtypeOf(var1.getGenericComponentType());
      } else {
         return false;
      }
   }

   private boolean isSupertypeOfArray(GenericArrayType var1) {
      if (this.runtimeType instanceof Class) {
         Class var2 = (Class)this.runtimeType;
         return !var2.isArray() ? var2.isAssignableFrom(Object[].class) : of(var1.getGenericComponentType()).isSubtypeOf(var2.getComponentType());
      } else {
         return this.runtimeType instanceof GenericArrayType
            ? of(var1.getGenericComponentType()).isSubtypeOf(((GenericArrayType)this.runtimeType).getGenericComponentType())
            : false;
      }
   }

   private boolean is(Type var1, TypeVariable<?> var2) {
      if (this.runtimeType.equals(var1)) {
         return true;
      } else if (!(var1 instanceof WildcardType)) {
         return canonicalizeWildcardsInType(this.runtimeType).equals(canonicalizeWildcardsInType(var1));
      } else {
         WildcardType var3 = canonicalizeWildcardType(var2, (WildcardType)var1);
         return every(var3.getUpperBounds()).isSupertypeOf(this.runtimeType) && every(var3.getLowerBounds()).isSubtypeOf(this.runtimeType);
      }
   }

   private static Type canonicalizeTypeArg(TypeVariable<?> var0, Type var1) {
      return (Type)(var1 instanceof WildcardType ? canonicalizeWildcardType(var0, (WildcardType)var1) : canonicalizeWildcardsInType(var1));
   }

   private static Type canonicalizeWildcardsInType(Type var0) {
      if (var0 instanceof ParameterizedType) {
         return canonicalizeWildcardsInParameterizedType((ParameterizedType)var0);
      } else {
         return var0 instanceof GenericArrayType ? Types.newArrayType(canonicalizeWildcardsInType(((GenericArrayType)var0).getGenericComponentType())) : var0;
      }
   }

   private static WildcardType canonicalizeWildcardType(TypeVariable<?> var0, WildcardType var1) {
      Type[] var2 = var0.getBounds();
      ArrayList var3 = new ArrayList();

      for (Type var7 : var1.getUpperBounds()) {
         if (!any(var2).isSubtypeOf(var7)) {
            var3.add(canonicalizeWildcardsInType(var7));
         }
      }

      return new Types.WildcardTypeImpl(var1.getLowerBounds(), var3.toArray(new Type[0]));
   }

   private static ParameterizedType canonicalizeWildcardsInParameterizedType(ParameterizedType var0) {
      Class var1 = (Class)var0.getRawType();
      TypeVariable[] var2 = var1.getTypeParameters();
      Type[] var3 = var0.getActualTypeArguments();

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = canonicalizeTypeArg(var2[var4], var3[var4]);
      }

      return Types.newParameterizedTypeWithOwner(var0.getOwnerType(), var1, var3);
   }

   private static TypeToken.Bounds every(Type[] var0) {
      return new TypeToken.Bounds(var0, false);
   }

   private static TypeToken.Bounds any(Type[] var0) {
      return new TypeToken.Bounds(var0, true);
   }

   private ImmutableSet<Class<? super T>> getRawTypes() {
      final ImmutableSet.Builder var1 = ImmutableSet.builder();
      (new TypeVisitor(this) {
         @Override
         void visitTypeVariable(TypeVariable<?> var1x) {
            this.visit(var1x.getBounds());
         }

         @Override
         void visitWildcardType(WildcardType var1x) {
            this.visit(var1x.getUpperBounds());
         }

         @Override
         void visitParameterizedType(ParameterizedType var1x) {
            var1.add((Class)var1x.getRawType());
         }

         @Override
         void visitClass(Class<?> var1x) {
            var1.add(var1x);
         }

         @Override
         void visitGenericArrayType(GenericArrayType var1x) {
            var1.add(Types.getArrayClass(TypeToken.of(var1x.getGenericComponentType()).getRawType()));
         }
      }).visit(new Type[]{this.runtimeType});
      return var1.build();
   }

   private boolean isOwnedBySubtypeOf(Type var1) {
      for (TypeToken var3 : this.getTypes()) {
         Type var4 = var3.getOwnerTypeIfPresent();
         if (var4 != null && of(var4).isSubtypeOf(var1)) {
            return true;
         }
      }

      return false;
   }

   private Type getOwnerTypeIfPresent() {
      if (this.runtimeType instanceof ParameterizedType) {
         return ((ParameterizedType)this.runtimeType).getOwnerType();
      } else {
         return this.runtimeType instanceof Class ? ((Class)this.runtimeType).getEnclosingClass() : null;
      }
   }

   @VisibleForTesting
   static <T> TypeToken<? extends T> toGenericType(Class<T> var0) {
      if (var0.isArray()) {
         Type var4 = Types.newArrayType(toGenericType(var0.getComponentType()).runtimeType);
         return (TypeToken<? extends T>)of(var4);
      } else {
         TypeVariable[] var1 = var0.getTypeParameters();
         Type var2 = var0.isMemberClass() && !Modifier.isStatic(var0.getModifiers()) ? toGenericType(var0.getEnclosingClass()).runtimeType : null;
         return var1.length <= 0 && (var2 == null || var2 == var0.getEnclosingClass()) ? of(var0) : of(Types.newParameterizedTypeWithOwner(var2, var0, var1));
      }
   }

   private TypeResolver getCovariantTypeResolver() {
      TypeResolver var1 = this.covariantTypeResolver;
      if (var1 == null) {
         var1 = this.covariantTypeResolver = TypeResolver.covariantly(this.runtimeType);
      }

      return var1;
   }

   private TypeResolver getInvariantTypeResolver() {
      TypeResolver var1 = this.invariantTypeResolver;
      if (var1 == null) {
         var1 = this.invariantTypeResolver = TypeResolver.invariantly(this.runtimeType);
      }

      return var1;
   }

   private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> var1, Type[] var2) {
      for (Type var6 : var2) {
         TypeToken var7 = of(var6);
         if (var7.isSubtypeOf(var1)) {
            return var7.getSupertype(var1);
         }
      }

      String var9 = String.valueOf(var1);
      String var10 = String.valueOf(this);
      throw new IllegalArgumentException(
         new StringBuilder(23 + String.valueOf(var9).length() + String.valueOf(var10).length())
            .append(var9)
            .append(" isn't a super type of ")
            .append(var10)
            .toString()
      );
   }

   private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> var1, Type[] var2) {
      if (var2.length > 0) {
         TypeToken var5 = of(var2[0]);
         return var5.getSubtype(var1);
      } else {
         String var3 = String.valueOf(var1);
         String var4 = String.valueOf(this);
         throw new IllegalArgumentException(
            new StringBuilder(21 + String.valueOf(var3).length() + String.valueOf(var4).length())
               .append(var3)
               .append(" isn't a subclass of ")
               .append(var4)
               .toString()
         );
      }
   }

   private TypeToken<? super T> getArraySupertype(Class<? super T> var1) {
      TypeToken var2 = this.getComponentType();
      if (var2 == null) {
         String var5 = String.valueOf(var1);
         String var4 = String.valueOf(this);
         throw new IllegalArgumentException(
            new StringBuilder(23 + String.valueOf(var5).length() + String.valueOf(var4).length())
               .append(var5)
               .append(" isn't a super type of ")
               .append(var4)
               .toString()
         );
      } else {
         TypeToken var3 = var2.getSupertype(Objects.requireNonNull((Class<? super T>)var1.getComponentType()));
         return (TypeToken<? super T>)of(newArrayClassOrGenericArrayType(var3.runtimeType));
      }
   }

   private TypeToken<? extends T> getArraySubtype(Class<?> var1) {
      Class var2 = var1.getComponentType();
      if (var2 == null) {
         String var5 = String.valueOf(var1);
         String var4 = String.valueOf(this);
         throw new IllegalArgumentException(
            new StringBuilder(36 + String.valueOf(var5).length() + String.valueOf(var4).length())
               .append(var5)
               .append(" does not appear to be a subtype of ")
               .append(var4)
               .toString()
         );
      } else {
         TypeToken var3 = Objects.requireNonNull(this.getComponentType()).getSubtype(var2);
         return (TypeToken<? extends T>)of(newArrayClassOrGenericArrayType(var3.runtimeType));
      }
   }

   private Type resolveTypeArgsForSubclass(Class<?> var1) {
      if (!(this.runtimeType instanceof Class) || var1.getTypeParameters().length != 0 && this.getRawType().getTypeParameters().length == 0) {
         TypeToken var2 = toGenericType(var1);
         Type var3 = var2.getSupertype(this.getRawType()).runtimeType;
         return new TypeResolver().where(var3, this.runtimeType).resolveType(var2.runtimeType);
      } else {
         return var1;
      }
   }

   private static Type newArrayClassOrGenericArrayType(Type var0) {
      return Types.JavaVersion.JAVA7.newArrayType(var0);
   }

   private static class Bounds {
      private final Type[] bounds;
      private final boolean target;

      Bounds(Type[] var1, boolean var2) {
         this.bounds = var1;
         this.target = var2;
      }

      boolean isSubtypeOf(Type var1) {
         for (Type var5 : this.bounds) {
            if (TypeToken.of(var5).isSubtypeOf(var1) == this.target) {
               return this.target;
            }
         }

         return !this.target;
      }

      boolean isSupertypeOf(Type var1) {
         TypeToken var2 = TypeToken.of(var1);

         for (Type var6 : this.bounds) {
            if (var2.isSubtypeOf(var6) == this.target) {
               return this.target;
            }
         }

         return !this.target;
      }
   }

   private final class ClassSet extends TypeToken<T>.TypeSet {
      private transient ImmutableSet<TypeToken<? super T>> classes;
      private static final long serialVersionUID = 0L;

      private ClassSet() {
      }

      @Override
      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.classes;
         if (var1 == null) {
            ImmutableList var2 = TypeToken.TypeCollector.FOR_GENERIC_TYPE.classesOnly().collectTypes(TypeToken.this);
            return this.classes = FluentIterable.from(var2).filter(TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
         } else {
            return var1;
         }
      }

      @Override
      public TypeToken<T>.TypeSet classes() {
         return this;
      }

      @Override
      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.classesOnly().collectTypes(TypeToken.this.getRawTypes());
         return ImmutableSet.copyOf(var1);
      }

      @Override
      public TypeToken<T>.TypeSet interfaces() {
         throw new UnsupportedOperationException("classes().interfaces() not supported.");
      }

      private Object readResolve() {
         return TypeToken.this.getTypes().classes();
      }
   }

   private final class InterfaceSet extends TypeToken<T>.TypeSet {
      private final transient TypeToken<T>.TypeSet allTypes;
      private transient ImmutableSet<TypeToken<? super T>> interfaces;
      private static final long serialVersionUID = 0L;

      InterfaceSet(TypeToken<T>.TypeSet var2) {
         this.allTypes = var2;
      }

      @Override
      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.interfaces;
         return var1 == null ? (this.interfaces = FluentIterable.from(this.allTypes).filter(TypeToken.TypeFilter.INTERFACE_ONLY).toSet()) : var1;
      }

      @Override
      public TypeToken<T>.TypeSet interfaces() {
         return this;
      }

      @Override
      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes(TypeToken.this.getRawTypes());
         return FluentIterable.from(var1).filter(new Predicate<Class<?>>(this) {
            public boolean apply(Class<?> var1) {
               return var1.isInterface();
            }
         }).toSet();
      }

      @Override
      public TypeToken<T>.TypeSet classes() {
         throw new UnsupportedOperationException("interfaces().classes() not supported.");
      }

      private Object readResolve() {
         return TypeToken.this.getTypes().interfaces();
      }
   }

   private static final class SimpleTypeToken<T> extends TypeToken<T> {
      private static final long serialVersionUID = 0L;

      SimpleTypeToken(Type var1) {
         super(var1);
      }
   }

   private abstract static class TypeCollector<K> {
      static final TypeToken.TypeCollector<TypeToken<?>> FOR_GENERIC_TYPE = new TypeToken.TypeCollector<TypeToken<?>>() {
         Class<?> getRawType(TypeToken<?> var1) {
            return var1.getRawType();
         }

         Iterable<? extends TypeToken<?>> getInterfaces(TypeToken<?> var1) {
            return var1.getGenericInterfaces();
         }

         TypeToken<?> getSuperclass(TypeToken<?> var1) {
            return var1.getGenericSuperclass();
         }
      };
      static final TypeToken.TypeCollector<Class<?>> FOR_RAW_TYPE = new TypeToken.TypeCollector<Class<?>>() {
         Class<?> getRawType(Class<?> var1) {
            return var1;
         }

         Iterable<? extends Class<?>> getInterfaces(Class<?> var1) {
            return Arrays.asList(var1.getInterfaces());
         }

         Class<?> getSuperclass(Class<?> var1) {
            return var1.getSuperclass();
         }
      };

      private TypeCollector() {
      }

      final TypeToken.TypeCollector<K> classesOnly() {
         return new TypeToken.TypeCollector.ForwardingTypeCollector<K>(this, this) {
            @Override
            Iterable<? extends K> getInterfaces(K var1) {
               return ImmutableSet.of();
            }

            @Override
            ImmutableList<K> collectTypes(Iterable<? extends K> var1) {
               ImmutableList.Builder var2 = ImmutableList.builder();

               for (Object var4 : var1) {
                  if (!this.getRawType((K)var4).isInterface()) {
                     var2.add(var4);
                  }
               }

               return super.collectTypes(var2.build());
            }
         };
      }

      final ImmutableList<K> collectTypes(K var1) {
         return this.collectTypes(ImmutableList.of((K)var1));
      }

      ImmutableList<K> collectTypes(Iterable<? extends K> var1) {
         HashMap var2 = Maps.newHashMap();

         for (Object var4 : var1) {
            this.collectTypes((K)var4, var2);
         }

         return sortKeysByValue(var2, Ordering.natural().reverse());
      }

      @CanIgnoreReturnValue
      private int collectTypes(K var1, Map<? super K, Integer> var2) {
         Integer var3 = (Integer)var2.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            int var4 = this.getRawType((K)var1).isInterface() ? 1 : 0;

            for (Object var6 : this.getInterfaces((K)var1)) {
               var4 = Math.max(var4, this.collectTypes((K)var6, var2));
            }

            Object var7 = this.getSuperclass((K)var1);
            if (var7 != null) {
               var4 = Math.max(var4, this.collectTypes((K)var7, var2));
            }

            var2.put(var1, var4 + 1);
            return var4 + 1;
         }
      }

      private static <K, V> ImmutableList<K> sortKeysByValue(final Map<K, V> var0, final Comparator<? super V> var1) {
         Ordering var2 = new Ordering<K>() {
            @Override
            public int compare(K var1x, K var2) {
               return var1.compare(Objects.requireNonNull(var0.get(var1x)), Objects.requireNonNull(var0.get(var2)));
            }
         };
         return var2.immutableSortedCopy(var0.keySet());
      }

      abstract Class<?> getRawType(K var1);

      abstract Iterable<? extends K> getInterfaces(K var1);

      abstract K getSuperclass(K var1);

      private static class ForwardingTypeCollector<K> extends TypeToken.TypeCollector<K> {
         private final TypeToken.TypeCollector<K> delegate;

         ForwardingTypeCollector(TypeToken.TypeCollector<K> var1) {
            this.delegate = var1;
         }

         @Override
         Class<?> getRawType(K var1) {
            return this.delegate.getRawType((K)var1);
         }

         @Override
         Iterable<? extends K> getInterfaces(K var1) {
            return this.delegate.getInterfaces((K)var1);
         }

         @Override
         K getSuperclass(K var1) {
            return this.delegate.getSuperclass((K)var1);
         }
      }
   }

   private static enum TypeFilter implements Predicate<TypeToken<?>> {
      IGNORE_TYPE_VARIABLE_OR_WILDCARD {
         public boolean apply(TypeToken<?> var1) {
            return !(var1.runtimeType instanceof TypeVariable) && !(var1.runtimeType instanceof WildcardType);
         }
      },
      INTERFACE_ONLY {
         public boolean apply(TypeToken<?> var1) {
            return var1.getRawType().isInterface();
         }
      };

      private TypeFilter() {
      }
   }

   public class TypeSet extends ForwardingSet<TypeToken<? super T>> implements Serializable {
      private transient ImmutableSet<TypeToken<? super T>> types;
      private static final long serialVersionUID = 0L;

      TypeSet() {
      }

      public TypeToken<T>.TypeSet interfaces() {
         return TypeToken.this.new InterfaceSet(this);
      }

      public TypeToken<T>.TypeSet classes() {
         return TypeToken.this.new ClassSet();
      }

      @Override
      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.types;
         if (var1 == null) {
            ImmutableList var2 = TypeToken.TypeCollector.FOR_GENERIC_TYPE.collectTypes(TypeToken.this);
            return this.types = FluentIterable.from(var2).filter(TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
         } else {
            return var1;
         }
      }

      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes(TypeToken.this.getRawTypes());
         return ImmutableSet.copyOf(var1);
      }
   }
}
