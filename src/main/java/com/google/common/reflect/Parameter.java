package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;

@ElementTypesAreNonnullByDefault
@Beta
public final class Parameter implements AnnotatedElement {
   private final Invokable<?, ?> declaration;
   private final int position;
   private final TypeToken<?> type;
   private final ImmutableList<Annotation> annotations;
   private final AnnotatedType annotatedType;

   Parameter(Invokable<?, ?> var1, int var2, TypeToken<?> var3, Annotation[] var4, AnnotatedType var5) {
      this.declaration = var1;
      this.position = var2;
      this.type = var3;
      this.annotations = ImmutableList.copyOf(var4);
      this.annotatedType = var5;
   }

   public TypeToken<?> getType() {
      return this.type;
   }

   public Invokable<?, ?> getDeclaringInvokable() {
      return this.declaration;
   }

   @Override
   public boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return this.getAnnotation(var1) != null;
   }

   @Override
   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      Preconditions.checkNotNull(var1);

      for (Annotation var3 : this.annotations) {
         if (var1.isInstance(var3)) {
            return (A)var1.cast(var3);
         }
      }

      return null;
   }

   @Override
   public Annotation[] getAnnotations() {
      return this.getDeclaredAnnotations();
   }

   @Override
   public <A extends Annotation> A[] getAnnotationsByType(Class<A> var1) {
      return (A[])this.getDeclaredAnnotationsByType(var1);
   }

   @Override
   public Annotation[] getDeclaredAnnotations() {
      return this.annotations.toArray(new Annotation[0]);
   }

   @Override
   public <A extends Annotation> A getDeclaredAnnotation(Class<A> var1) {
      Preconditions.checkNotNull(var1);
      return FluentIterable.from(this.annotations).<A>filter(var1).first().orNull();
   }

   @Override
   public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> var1) {
      return (A[])FluentIterable.from(this.annotations).filter(var1).toArray(var1);
   }

   public AnnotatedType getAnnotatedType() {
      return this.annotatedType;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Parameter)) {
         return false;
      } else {
         Parameter var2 = (Parameter)var1;
         return this.position == var2.position && this.declaration.equals(var2.declaration);
      }
   }

   @Override
   public int hashCode() {
      return this.position;
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.type);
      int var2 = this.position;
      return new StringBuilder(15 + String.valueOf(var1).length()).append(var1).append(" arg").append(var2).toString();
   }
}
