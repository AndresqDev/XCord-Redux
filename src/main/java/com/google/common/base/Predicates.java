package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Predicates {
   private Predicates() {
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> alwaysTrue() {
      return Predicates.ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> alwaysFalse() {
      return Predicates.ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> isNull() {
      return Predicates.ObjectPredicate.IS_NULL.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> notNull() {
      return Predicates.ObjectPredicate.NOT_NULL.withNarrowedType();
   }

   public static <T> Predicate<T> not(Predicate<T> var0) {
      return new Predicates.NotPredicate<>(var0);
   }

   public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> var0) {
      return new Predicates.AndPredicate<>(defensiveCopy(var0));
   }

   @SafeVarargs
   public static <T> Predicate<T> and(Predicate<? super T>... var0) {
      return new Predicates.AndPredicate<>(defensiveCopy(var0));
   }

   public static <T> Predicate<T> and(Predicate<? super T> var0, Predicate<? super T> var1) {
      return new Predicates.AndPredicate<>(asList(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1)));
   }

   public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> var0) {
      return new Predicates.OrPredicate<>(defensiveCopy(var0));
   }

   @SafeVarargs
   public static <T> Predicate<T> or(Predicate<? super T>... var0) {
      return new Predicates.OrPredicate<>(defensiveCopy(var0));
   }

   public static <T> Predicate<T> or(Predicate<? super T> var0, Predicate<? super T> var1) {
      return new Predicates.OrPredicate<>(asList(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1)));
   }

   public static <T> Predicate<T> equalTo(@ParametricNullness T var0) {
      return var0 == null ? isNull() : new Predicates.IsEqualToPredicate(var0).withNarrowedType();
   }

   @GwtIncompatible
   public static <T> Predicate<T> instanceOf(Class<?> var0) {
      return new Predicates.InstanceOfPredicate<>(var0);
   }

   @GwtIncompatible
   @Beta
   public static Predicate<Class<?>> subtypeOf(Class<?> var0) {
      return new Predicates.SubtypeOfPredicate(var0);
   }

   public static <T> Predicate<T> in(Collection<? extends T> var0) {
      return new Predicates.InPredicate<>(var0);
   }

   public static <A, B> Predicate<A> compose(Predicate<B> var0, Function<A, ? extends B> var1) {
      return new Predicates.CompositionPredicate<>(var0, var1);
   }

   @GwtIncompatible
   public static Predicate<CharSequence> containsPattern(String var0) {
      return new Predicates.ContainsPatternFromStringPredicate(var0);
   }

   @GwtIncompatible("java.util.regex.Pattern")
   public static Predicate<CharSequence> contains(Pattern var0) {
      return new Predicates.ContainsPatternPredicate(new JdkPattern(var0));
   }

   private static String toStringHelper(String var0, Iterable<?> var1) {
      StringBuilder var2 = new StringBuilder("Predicates.").append(var0).append('(');
      boolean var3 = true;

      for (Object var5 : var1) {
         if (!var3) {
            var2.append(',');
         }

         var2.append(var5);
         var3 = false;
      }

      return var2.append(')').toString();
   }

   private static <T> List<Predicate<? super T>> asList(Predicate<? super T> var0, Predicate<? super T> var1) {
      return Arrays.asList(var0, var1);
   }

   private static <T> List<T> defensiveCopy(T... var0) {
      return defensiveCopy(Arrays.asList((T[])var0));
   }

   static <T> List<T> defensiveCopy(Iterable<T> var0) {
      ArrayList var1 = new ArrayList();

      for (Object var3 : var0) {
         var1.add(Preconditions.checkNotNull(var3));
      }

      return var1;
   }

   private static class AndPredicate<T> implements Predicate<T>, Serializable {
      private final List<? extends Predicate<? super T>> components;
      private static final long serialVersionUID = 0L;

      private AndPredicate(List<? extends Predicate<? super T>> var1) {
         this.components = var1;
      }

      @Override
      public boolean apply(@ParametricNullness T var1) {
         for (int var2 = 0; var2 < this.components.size(); var2++) {
            if (!this.components.get(var2).apply((T)var1)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public int hashCode() {
         return this.components.hashCode() + 306654252;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.AndPredicate) {
            Predicates.AndPredicate var2 = (Predicates.AndPredicate)var1;
            return this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return Predicates.toStringHelper("and", this.components);
      }
   }

   private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable {
      final Predicate<B> p;
      final Function<A, ? extends B> f;
      private static final long serialVersionUID = 0L;

      private CompositionPredicate(Predicate<B> var1, Function<A, ? extends B> var2) {
         this.p = Preconditions.checkNotNull(var1);
         this.f = Preconditions.checkNotNull(var2);
      }

      @Override
      public boolean apply(@ParametricNullness A var1) {
         return this.p.apply((B)this.f.apply((A)var1));
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Predicates.CompositionPredicate)) {
            return false;
         } else {
            Predicates.CompositionPredicate var2 = (Predicates.CompositionPredicate)var1;
            return this.f.equals(var2.f) && this.p.equals(var2.p);
         }
      }

      @Override
      public int hashCode() {
         return this.f.hashCode() ^ this.p.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.p);
         String var2 = String.valueOf(this.f);
         return new StringBuilder(2 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append("(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   @GwtIncompatible
   private static class ContainsPatternFromStringPredicate extends Predicates.ContainsPatternPredicate {
      private static final long serialVersionUID = 0L;

      ContainsPatternFromStringPredicate(String var1) {
         super(Platform.compilePattern(var1));
      }

      @Override
      public String toString() {
         String var1 = this.pattern.pattern();
         return new StringBuilder(28 + String.valueOf(var1).length()).append("Predicates.containsPattern(").append(var1).append(")").toString();
      }
   }

   @GwtIncompatible
   private static class ContainsPatternPredicate implements Predicate<CharSequence>, Serializable {
      final CommonPattern pattern;
      private static final long serialVersionUID = 0L;

      ContainsPatternPredicate(CommonPattern var1) {
         this.pattern = Preconditions.checkNotNull(var1);
      }

      public boolean apply(CharSequence var1) {
         return this.pattern.matcher(var1).find();
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.pattern.pattern(), this.pattern.flags());
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Predicates.ContainsPatternPredicate)) {
            return false;
         } else {
            Predicates.ContainsPatternPredicate var2 = (Predicates.ContainsPatternPredicate)var1;
            return Objects.equal(this.pattern.pattern(), var2.pattern.pattern()) && this.pattern.flags() == var2.pattern.flags();
         }
      }

      @Override
      public String toString() {
         String var1 = MoreObjects.toStringHelper(this.pattern).add("pattern", this.pattern.pattern()).add("pattern.flags", this.pattern.flags()).toString();
         return new StringBuilder(21 + String.valueOf(var1).length()).append("Predicates.contains(").append(var1).append(")").toString();
      }
   }

   private static class InPredicate<T> implements Predicate<T>, Serializable {
      private final Collection<?> target;
      private static final long serialVersionUID = 0L;

      private InPredicate(Collection<?> var1) {
         this.target = Preconditions.checkNotNull(var1);
      }

      @Override
      public boolean apply(@ParametricNullness T var1) {
         try {
            return this.target.contains(var1);
         } catch (ClassCastException | NullPointerException var3) {
            return false;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.InPredicate) {
            Predicates.InPredicate var2 = (Predicates.InPredicate)var1;
            return this.target.equals(var2.target);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.target.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.target);
         return new StringBuilder(15 + String.valueOf(var1).length()).append("Predicates.in(").append(var1).append(")").toString();
      }
   }

   @GwtIncompatible
   private static class InstanceOfPredicate<T> implements Predicate<T>, Serializable {
      private final Class<?> clazz;
      private static final long serialVersionUID = 0L;

      private InstanceOfPredicate(Class<?> var1) {
         this.clazz = Preconditions.checkNotNull(var1);
      }

      @Override
      public boolean apply(@ParametricNullness T var1) {
         return this.clazz.isInstance(var1);
      }

      @Override
      public int hashCode() {
         return this.clazz.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.InstanceOfPredicate) {
            Predicates.InstanceOfPredicate var2 = (Predicates.InstanceOfPredicate)var1;
            return this.clazz == var2.clazz;
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = this.clazz.getName();
         return new StringBuilder(23 + String.valueOf(var1).length()).append("Predicates.instanceOf(").append(var1).append(")").toString();
      }
   }

   private static class IsEqualToPredicate implements Predicate<Object>, Serializable {
      private final Object target;
      private static final long serialVersionUID = 0L;

      private IsEqualToPredicate(Object var1) {
         this.target = var1;
      }

      @Override
      public boolean apply(Object var1) {
         return this.target.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.target.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.IsEqualToPredicate) {
            Predicates.IsEqualToPredicate var2 = (Predicates.IsEqualToPredicate)var1;
            return this.target.equals(var2.target);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.target);
         return new StringBuilder(20 + String.valueOf(var1).length()).append("Predicates.equalTo(").append(var1).append(")").toString();
      }

      <T> Predicate<T> withNarrowedType() {
         return this;
      }
   }

   private static class NotPredicate<T> implements Predicate<T>, Serializable {
      final Predicate<T> predicate;
      private static final long serialVersionUID = 0L;

      NotPredicate(Predicate<T> var1) {
         this.predicate = Preconditions.checkNotNull(var1);
      }

      @Override
      public boolean apply(@ParametricNullness T var1) {
         return !this.predicate.apply((T)var1);
      }

      @Override
      public int hashCode() {
         return ~this.predicate.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.NotPredicate) {
            Predicates.NotPredicate var2 = (Predicates.NotPredicate)var1;
            return this.predicate.equals(var2.predicate);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.predicate);
         return new StringBuilder(16 + String.valueOf(var1).length()).append("Predicates.not(").append(var1).append(")").toString();
      }
   }

   static enum ObjectPredicate implements Predicate<Object> {
      ALWAYS_TRUE {
         @Override
         public boolean apply(Object var1) {
            return true;
         }

         @Override
         public String toString() {
            return "Predicates.alwaysTrue()";
         }
      },
      ALWAYS_FALSE {
         @Override
         public boolean apply(Object var1) {
            return false;
         }

         @Override
         public String toString() {
            return "Predicates.alwaysFalse()";
         }
      },
      IS_NULL {
         @Override
         public boolean apply(Object var1) {
            return var1 == null;
         }

         @Override
         public String toString() {
            return "Predicates.isNull()";
         }
      },
      NOT_NULL {
         @Override
         public boolean apply(Object var1) {
            return var1 != null;
         }

         @Override
         public String toString() {
            return "Predicates.notNull()";
         }
      };

      private ObjectPredicate() {
      }

      <T> Predicate<T> withNarrowedType() {
         return this;
      }
   }

   private static class OrPredicate<T> implements Predicate<T>, Serializable {
      private final List<? extends Predicate<? super T>> components;
      private static final long serialVersionUID = 0L;

      private OrPredicate(List<? extends Predicate<? super T>> var1) {
         this.components = var1;
      }

      @Override
      public boolean apply(@ParametricNullness T var1) {
         for (int var2 = 0; var2 < this.components.size(); var2++) {
            if (this.components.get(var2).apply((T)var1)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int hashCode() {
         return this.components.hashCode() + 87855567;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.OrPredicate) {
            Predicates.OrPredicate var2 = (Predicates.OrPredicate)var1;
            return this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return Predicates.toStringHelper("or", this.components);
      }
   }

   @GwtIncompatible
   private static class SubtypeOfPredicate implements Predicate<Class<?>>, Serializable {
      private final Class<?> clazz;
      private static final long serialVersionUID = 0L;

      private SubtypeOfPredicate(Class<?> var1) {
         this.clazz = Preconditions.checkNotNull(var1);
      }

      public boolean apply(Class<?> var1) {
         return this.clazz.isAssignableFrom(var1);
      }

      @Override
      public int hashCode() {
         return this.clazz.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Predicates.SubtypeOfPredicate) {
            Predicates.SubtypeOfPredicate var2 = (Predicates.SubtypeOfPredicate)var1;
            return this.clazz == var2.clazz;
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = this.clazz.getName();
         return new StringBuilder(22 + String.valueOf(var1).length()).append("Predicates.subtypeOf(").append(var1).append(")").toString();
      }
   }
}
