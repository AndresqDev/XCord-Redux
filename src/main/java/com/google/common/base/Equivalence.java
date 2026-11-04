package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.ForOverride;
import java.io.Serializable;
import java.util.function.BiPredicate;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Equivalence<T> implements BiPredicate<T, T> {
   protected Equivalence() {
   }

   public final boolean equivalent(T var1, T var2) {
      if (var1 == var2) {
         return true;
      } else {
         return var1 != null && var2 != null ? this.doEquivalent((T)var1, (T)var2) : false;
      }
   }

   @Deprecated
   @Override
   public final boolean test(T var1, T var2) {
      return this.equivalent((T)var1, (T)var2);
   }

   @ForOverride
   protected abstract boolean doEquivalent(T var1, T var2);

   public final int hash(T var1) {
      return var1 == null ? 0 : this.doHash((T)var1);
   }

   @ForOverride
   protected abstract int doHash(T var1);

   public final <F> Equivalence<F> onResultOf(Function<? super F, ? extends T> var1) {
      return new FunctionalEquivalence<>(var1, this);
   }

   public final <S extends T> Equivalence.Wrapper<S> wrap(@ParametricNullness S var1) {
      return new Equivalence.Wrapper<>(this, var1);
   }

   @GwtCompatible(
      serializable = true
   )
   public final <S extends T> Equivalence<Iterable<S>> pairwise() {
      return new PairwiseEquivalence<>(this);
   }

   public final Predicate<T> equivalentTo(T var1) {
      return new Equivalence.EquivalentToPredicate<>(this, (T)var1);
   }

   public static Equivalence<Object> equals() {
      return Equivalence.Equals.INSTANCE;
   }

   public static Equivalence<Object> identity() {
      return Equivalence.Identity.INSTANCE;
   }

   static final class Equals extends Equivalence<Object> implements Serializable {
      static final Equivalence.Equals INSTANCE = new Equivalence.Equals();
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean doEquivalent(Object var1, Object var2) {
         return var1.equals(var2);
      }

      @Override
      protected int doHash(Object var1) {
         return var1.hashCode();
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class EquivalentToPredicate<T> implements Predicate<T>, Serializable {
      private final Equivalence<T> equivalence;
      private final T target;
      private static final long serialVersionUID = 0L;

      EquivalentToPredicate(Equivalence<T> var1, T var2) {
         this.equivalence = Preconditions.checkNotNull(var1);
         this.target = (T)var2;
      }

      @Override
      public boolean apply(T var1) {
         return this.equivalence.equivalent((T)var1, this.target);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Equivalence.EquivalentToPredicate)) {
            return false;
         } else {
            Equivalence.EquivalentToPredicate var2 = (Equivalence.EquivalentToPredicate)var1;
            return this.equivalence.equals(var2.equivalence) && Objects.equal(this.target, var2.target);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.equivalence, this.target);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.equivalence);
         String var2 = String.valueOf(this.target);
         return new StringBuilder(15 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".equivalentTo(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   static final class Identity extends Equivalence<Object> implements Serializable {
      static final Equivalence.Identity INSTANCE = new Equivalence.Identity();
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean doEquivalent(Object var1, Object var2) {
         return false;
      }

      @Override
      protected int doHash(Object var1) {
         return System.identityHashCode(var1);
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   public static final class Wrapper<T> implements Serializable {
      private final Equivalence<? super T> equivalence;
      @ParametricNullness
      private final T reference;
      private static final long serialVersionUID = 0L;

      private Wrapper(Equivalence<? super T> var1, @ParametricNullness T var2) {
         this.equivalence = Preconditions.checkNotNull(var1);
         this.reference = (T)var2;
      }

      @ParametricNullness
      public T get() {
         return this.reference;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            if (var1 instanceof Equivalence.Wrapper) {
               Equivalence.Wrapper var2 = (Equivalence.Wrapper)var1;
               if (this.equivalence.equals(var2.equivalence)) {
                  Equivalence var3 = this.equivalence;
                  return var3.equivalent(this.reference, var2.reference);
               }
            }

            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.equivalence.hash(this.reference);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.equivalence);
         String var2 = String.valueOf(this.reference);
         return new StringBuilder(7 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".wrap(")
            .append(var2)
            .append(")")
            .toString();
      }
   }
}
