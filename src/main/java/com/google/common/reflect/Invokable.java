package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class Invokable<T, R> implements AnnotatedElement, Member {
   private final AccessibleObject accessibleObject;
   private final Member member;

   <M extends AccessibleObject & Member> Invokable(M var1) {
      Preconditions.checkNotNull(var1);
      this.accessibleObject = var1;
      this.member = (Member)var1;
   }

   public static Invokable<?, Object> from(Method var0) {
      return new Invokable.MethodInvokable(var0);
   }

   public static <T> Invokable<T, T> from(Constructor<T> var0) {
      return new Invokable.ConstructorInvokable<>(var0);
   }

   @Override
   public final boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return this.accessibleObject.isAnnotationPresent(var1);
   }

   @Override
   public final <A extends Annotation> A getAnnotation(Class<A> var1) {
      return this.accessibleObject.getAnnotation(var1);
   }

   @Override
   public final Annotation[] getAnnotations() {
      return this.accessibleObject.getAnnotations();
   }

   @Override
   public final Annotation[] getDeclaredAnnotations() {
      return this.accessibleObject.getDeclaredAnnotations();
   }

   public abstract TypeVariable<?>[] getTypeParameters();

   public final void setAccessible(boolean var1) {
      this.accessibleObject.setAccessible(var1);
   }

   public final boolean trySetAccessible() {
      try {
         this.accessibleObject.setAccessible(true);
         return true;
      } catch (RuntimeException var2) {
         return false;
      }
   }

   public final boolean isAccessible() {
      return this.accessibleObject.isAccessible();
   }

   @Override
   public final String getName() {
      return this.member.getName();
   }

   @Override
   public final int getModifiers() {
      return this.member.getModifiers();
   }

   @Override
   public final boolean isSynthetic() {
      return this.member.isSynthetic();
   }

   public final boolean isPublic() {
      return Modifier.isPublic(this.getModifiers());
   }

   public final boolean isProtected() {
      return Modifier.isProtected(this.getModifiers());
   }

   public final boolean isPackagePrivate() {
      return !this.isPrivate() && !this.isPublic() && !this.isProtected();
   }

   public final boolean isPrivate() {
      return Modifier.isPrivate(this.getModifiers());
   }

   public final boolean isStatic() {
      return Modifier.isStatic(this.getModifiers());
   }

   public final boolean isFinal() {
      return Modifier.isFinal(this.getModifiers());
   }

   public final boolean isAbstract() {
      return Modifier.isAbstract(this.getModifiers());
   }

   public final boolean isNative() {
      return Modifier.isNative(this.getModifiers());
   }

   public final boolean isSynchronized() {
      return Modifier.isSynchronized(this.getModifiers());
   }

   final boolean isVolatile() {
      return Modifier.isVolatile(this.getModifiers());
   }

   final boolean isTransient() {
      return Modifier.isTransient(this.getModifiers());
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Invokable)) {
         return false;
      } else {
         Invokable var2 = (Invokable)var1;
         return this.getOwnerType().equals(var2.getOwnerType()) && this.member.equals(var2.member);
      }
   }

   @Override
   public int hashCode() {
      return this.member.hashCode();
   }

   @Override
   public String toString() {
      return this.member.toString();
   }

   public abstract boolean isOverridable();

   public abstract boolean isVarArgs();

   @CanIgnoreReturnValue
   public final R invoke(T var1, Object... var2) throws InvocationTargetException, IllegalAccessException {
      return (R)this.invokeInternal(var1, Preconditions.checkNotNull(var2));
   }

   public final TypeToken<? extends R> getReturnType() {
      return (TypeToken<? extends R>)TypeToken.of(this.getGenericReturnType());
   }

   public final ImmutableList<Parameter> getParameters() {
      Type[] var1 = this.getGenericParameterTypes();
      Annotation[][] var2 = this.getParameterAnnotations();
      AnnotatedType[] var3 = this.getAnnotatedParameterTypes();
      ImmutableList.Builder var4 = ImmutableList.builder();

      for (int var5 = 0; var5 < var1.length; var5++) {
         var4.add(new Parameter(this, var5, TypeToken.of(var1[var5]), var2[var5], var3[var5]));
      }

      return var4.build();
   }

   public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes() {
      ImmutableList.Builder var1 = ImmutableList.builder();

      for (Type var5 : this.getGenericExceptionTypes()) {
         TypeToken var6 = TypeToken.of(var5);
         var1.add(var6);
      }

      return var1.build();
   }

   public final <R1 extends R> Invokable<T, R1> returning(Class<R1> var1) {
      return this.returning(TypeToken.of(var1));
   }

   public final <R1 extends R> Invokable<T, R1> returning(TypeToken<R1> var1) {
      if (!var1.isSupertypeOf(this.getReturnType())) {
         String var2 = String.valueOf(this.getReturnType());
         String var3 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(35 + String.valueOf(var2).length() + String.valueOf(var3).length())
               .append("Invokable is known to return ")
               .append(var2)
               .append(", not ")
               .append(var3)
               .toString()
         );
      } else {
         return this;
      }
   }

   @Override
   public final Class<? super T> getDeclaringClass() {
      return (Class<? super T>)this.member.getDeclaringClass();
   }

   public TypeToken<T> getOwnerType() {
      return TypeToken.of((Class<T>)this.getDeclaringClass());
   }

   abstract Object invokeInternal(Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException;

   abstract Type[] getGenericParameterTypes();

   abstract AnnotatedType[] getAnnotatedParameterTypes();

   abstract Type[] getGenericExceptionTypes();

   abstract Annotation[][] getParameterAnnotations();

   abstract Type getGenericReturnType();

   public abstract AnnotatedType getAnnotatedReturnType();

   static class ConstructorInvokable<T> extends Invokable<T, T> {
      final Constructor<?> constructor;

      ConstructorInvokable(Constructor<?> var1) {
         super(var1);
         this.constructor = var1;
      }

      @Override
      final Object invokeInternal(Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
         try {
            return this.constructor.newInstance(var2);
         } catch (InstantiationException var5) {
            String var4 = String.valueOf(this.constructor);
            throw new RuntimeException(new StringBuilder(8 + String.valueOf(var4).length()).append(var4).append(" failed.").toString(), var5);
         }
      }

      @Override
      Type getGenericReturnType() {
         Class var1 = this.getDeclaringClass();
         TypeVariable[] var2 = var1.getTypeParameters();
         return (Type)(var2.length > 0 ? Types.newParameterizedType(var1, var2) : var1);
      }

      @Override
      Type[] getGenericParameterTypes() {
         Type[] var1 = this.constructor.getGenericParameterTypes();
         if (var1.length > 0 && this.mayNeedHiddenThis()) {
            Class[] var2 = this.constructor.getParameterTypes();
            if (var1.length == var2.length && var2[0] == this.getDeclaringClass().getEnclosingClass()) {
               return Arrays.copyOfRange(var1, 1, var1.length);
            }
         }

         return var1;
      }

      @Override
      AnnotatedType[] getAnnotatedParameterTypes() {
         return this.constructor.getAnnotatedParameterTypes();
      }

      @Override
      public AnnotatedType getAnnotatedReturnType() {
         return this.constructor.getAnnotatedReturnType();
      }

      @Override
      Type[] getGenericExceptionTypes() {
         return this.constructor.getGenericExceptionTypes();
      }

      @Override
      final Annotation[][] getParameterAnnotations() {
         return this.constructor.getParameterAnnotations();
      }

      @Override
      public final TypeVariable<?>[] getTypeParameters() {
         TypeVariable[] var1 = this.getDeclaringClass().getTypeParameters();
         TypeVariable[] var2 = this.constructor.getTypeParameters();
         TypeVariable[] var3 = new TypeVariable[var1.length + var2.length];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         System.arraycopy(var2, 0, var3, var1.length, var2.length);
         return var3;
      }

      @Override
      public final boolean isOverridable() {
         return false;
      }

      @Override
      public final boolean isVarArgs() {
         return this.constructor.isVarArgs();
      }

      private boolean mayNeedHiddenThis() {
         Class var1 = this.constructor.getDeclaringClass();
         if (var1.getEnclosingConstructor() != null) {
            return true;
         } else {
            Method var2 = var1.getEnclosingMethod();
            return var2 != null ? !Modifier.isStatic(var2.getModifiers()) : var1.getEnclosingClass() != null && !Modifier.isStatic(var1.getModifiers());
         }
      }
   }

   static class MethodInvokable<T> extends Invokable<T, Object> {
      final Method method;

      MethodInvokable(Method var1) {
         super(var1);
         this.method = var1;
      }

      @Override
      final Object invokeInternal(Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
         return this.method.invoke(var1, var2);
      }

      @Override
      Type getGenericReturnType() {
         return this.method.getGenericReturnType();
      }

      @Override
      Type[] getGenericParameterTypes() {
         return this.method.getGenericParameterTypes();
      }

      @Override
      AnnotatedType[] getAnnotatedParameterTypes() {
         return this.method.getAnnotatedParameterTypes();
      }

      @Override
      public AnnotatedType getAnnotatedReturnType() {
         return this.method.getAnnotatedReturnType();
      }

      @Override
      Type[] getGenericExceptionTypes() {
         return this.method.getGenericExceptionTypes();
      }

      @Override
      final Annotation[][] getParameterAnnotations() {
         return this.method.getParameterAnnotations();
      }

      @Override
      public final TypeVariable<?>[] getTypeParameters() {
         return this.method.getTypeParameters();
      }

      @Override
      public final boolean isOverridable() {
         return !this.isFinal() && !this.isPrivate() && !this.isStatic() && !Modifier.isFinal(this.getDeclaringClass().getModifiers());
      }

      @Override
      public final boolean isVarArgs() {
         return this.method.isVarArgs();
      }
   }
}
