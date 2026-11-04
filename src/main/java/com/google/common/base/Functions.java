package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Functions {
   private Functions() {
   }

   public static Function<Object, String> toStringFunction() {
      return Functions.ToStringFunction.INSTANCE;
   }

   public static <E> Function<E, E> identity() {
      return Functions.IdentityFunction.INSTANCE;
   }

   public static <K, V> Function<K, V> forMap(Map<K, V> var0) {
      return new Functions.FunctionForMapNoDefault<>(var0);
   }

   public static <K, V> Function<K, V> forMap(Map<K, ? extends V> var0, @ParametricNullness V var1) {
      return new Functions.ForMapWithDefault<>(var0, (V)var1);
   }

   public static <A, B, C> Function<A, C> compose(Function<B, C> var0, Function<A, ? extends B> var1) {
      return new Functions.FunctionComposition<>(var0, var1);
   }

   public static <T> Function<T, Boolean> forPredicate(Predicate<T> var0) {
      return new Functions.PredicateFunction<>(var0);
   }

   public static <E> Function<Object, E> constant(@ParametricNullness E var0) {
      return new Functions.ConstantFunction<>((E)var0);
   }

   public static <F, T> Function<F, T> forSupplier(Supplier<T> var0) {
      return new Functions.SupplierFunction<>(var0);
   }

   private static class ConstantFunction<E> implements Function<Object, E>, Serializable {
      @ParametricNullness
      private final E value;
      private static final long serialVersionUID = 0L;

      public ConstantFunction(@ParametricNullness E var1) {
         this.value = (E)var1;
      }

      @ParametricNullness
      @Override
      public E apply(Object var1) {
         return this.value;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Functions.ConstantFunction) {
            Functions.ConstantFunction var2 = (Functions.ConstantFunction)var1;
            return Objects.equal(this.value, var2.value);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.value == null ? 0 : this.value.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.value);
         return new StringBuilder(20 + String.valueOf(var1).length()).append("Functions.constant(").append(var1).append(")").toString();
      }
   }

   private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable {
      final Map<K, ? extends V> map;
      @ParametricNullness
      final V defaultValue;
      private static final long serialVersionUID = 0L;

      ForMapWithDefault(Map<K, ? extends V> var1, @ParametricNullness V var2) {
         this.map = Preconditions.checkNotNull(var1);
         this.defaultValue = (V)var2;
      }

      @ParametricNullness
      @Override
      public V apply(@ParametricNullness K var1) {
         Object var2 = this.map.get(var1);
         return var2 == null && !this.map.containsKey(var1) ? this.defaultValue : NullnessCasts.uncheckedCastNullableTToT((V)var2);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Functions.ForMapWithDefault)) {
            return false;
         } else {
            Functions.ForMapWithDefault var2 = (Functions.ForMapWithDefault)var1;
            return this.map.equals(var2.map) && Objects.equal(this.defaultValue, var2.defaultValue);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.map, this.defaultValue);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.map);
         String var2 = String.valueOf(this.defaultValue);
         return new StringBuilder(33 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("Functions.forMap(")
            .append(var1)
            .append(", defaultValue=")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable {
      private final Function<B, C> g;
      private final Function<A, ? extends B> f;
      private static final long serialVersionUID = 0L;

      public FunctionComposition(Function<B, C> var1, Function<A, ? extends B> var2) {
         this.g = Preconditions.checkNotNull(var1);
         this.f = Preconditions.checkNotNull(var2);
      }

      @ParametricNullness
      @Override
      public C apply(@ParametricNullness A var1) {
         return this.g.apply((B)this.f.apply((A)var1));
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Functions.FunctionComposition)) {
            return false;
         } else {
            Functions.FunctionComposition var2 = (Functions.FunctionComposition)var1;
            return this.f.equals(var2.f) && this.g.equals(var2.g);
         }
      }

      @Override
      public int hashCode() {
         return this.f.hashCode() ^ this.g.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.g);
         String var2 = String.valueOf(this.f);
         return new StringBuilder(2 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append("(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {
      final Map<K, V> map;
      private static final long serialVersionUID = 0L;

      FunctionForMapNoDefault(Map<K, V> var1) {
         this.map = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      @Override
      public V apply(@ParametricNullness K var1) {
         Object var2 = this.map.get(var1);
         Preconditions.checkArgument(var2 != null || this.map.containsKey(var1), "Key '%s' not present in map", var1);
         return NullnessCasts.uncheckedCastNullableTToT((V)var2);
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Functions.FunctionForMapNoDefault) {
            Functions.FunctionForMapNoDefault var2 = (Functions.FunctionForMapNoDefault)var1;
            return this.map.equals(var2.map);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.map.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.map);
         return new StringBuilder(18 + String.valueOf(var1).length()).append("Functions.forMap(").append(var1).append(")").toString();
      }
   }

   private static enum IdentityFunction implements Function<Object, Object> {
      INSTANCE;

      @Override
      public Object apply(Object var1) {
         return var1;
      }

      @Override
      public String toString() {
         return "Functions.identity()";
      }
   }

   private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable {
      private final Predicate<T> predicate;
      private static final long serialVersionUID = 0L;

      private PredicateFunction(Predicate<T> var1) {
         this.predicate = Preconditions.checkNotNull(var1);
      }

      public Boolean apply(@ParametricNullness T var1) {
         return this.predicate.apply((T)var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Functions.PredicateFunction) {
            Functions.PredicateFunction var2 = (Functions.PredicateFunction)var1;
            return this.predicate.equals(var2.predicate);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.predicate.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.predicate);
         return new StringBuilder(24 + String.valueOf(var1).length()).append("Functions.forPredicate(").append(var1).append(")").toString();
      }
   }

   private static class SupplierFunction<F, T> implements Function<F, T>, Serializable {
      private final Supplier<T> supplier;
      private static final long serialVersionUID = 0L;

      private SupplierFunction(Supplier<T> var1) {
         this.supplier = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      @Override
      public T apply(@ParametricNullness F var1) {
         return this.supplier.get();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Functions.SupplierFunction) {
            Functions.SupplierFunction var2 = (Functions.SupplierFunction)var1;
            return this.supplier.equals(var2.supplier);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.supplier.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.supplier);
         return new StringBuilder(23 + String.valueOf(var1).length()).append("Functions.forSupplier(").append(var1).append(")").toString();
      }
   }

   private static enum ToStringFunction implements Function<Object, String> {
      INSTANCE;

      public String apply(Object var1) {
         Preconditions.checkNotNull(var1);
         return var1.toString();
      }

      @Override
      public String toString() {
         return "Functions.toStringFunction()";
      }
   }
}
