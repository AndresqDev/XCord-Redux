package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Suppliers {
   private Suppliers() {
   }

   public static <F, T> Supplier<T> compose(Function<? super F, T> var0, Supplier<F> var1) {
      return new Suppliers.SupplierComposition<>(var0, var1);
   }

   public static <T> Supplier<T> memoize(Supplier<T> var0) {
      if (!(var0 instanceof Suppliers.NonSerializableMemoizingSupplier) && !(var0 instanceof Suppliers.MemoizingSupplier)) {
         return (Supplier<T>)(var0 instanceof Serializable ? new Suppliers.MemoizingSupplier<>(var0) : new Suppliers.NonSerializableMemoizingSupplier<>(var0));
      } else {
         return var0;
      }
   }

   public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> var0, long var1, TimeUnit var3) {
      return new Suppliers.ExpiringMemoizingSupplier<>(var0, var1, var3);
   }

   public static <T> Supplier<T> ofInstance(@ParametricNullness T var0) {
      return new Suppliers.SupplierOfInstance<>((T)var0);
   }

   public static <T> Supplier<T> synchronizedSupplier(Supplier<T> var0) {
      return new Suppliers.ThreadSafeSupplier<>(var0);
   }

   public static <T> Function<Supplier<T>, T> supplierFunction() {
      return Suppliers.SupplierFunctionImpl.INSTANCE;
   }

   @VisibleForTesting
   static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      final long durationNanos;
      transient volatile T value;
      transient volatile long expirationNanos;
      private static final long serialVersionUID = 0L;

      ExpiringMemoizingSupplier(Supplier<T> var1, long var2, TimeUnit var4) {
         this.delegate = Preconditions.checkNotNull(var1);
         this.durationNanos = var4.toNanos(var2);
         Preconditions.checkArgument(var2 > 0L, "duration (%s %s) must be > 0", var2, var4);
      }

      @ParametricNullness
      @Override
      public T get() {
         long var1 = this.expirationNanos;
         long var3 = Platform.systemNanoTime();
         if (var1 == 0L || var3 - var1 >= 0L) {
            synchronized (this) {
               if (var1 == this.expirationNanos) {
                  Object var6 = this.delegate.get();
                  this.value = (T)var6;
                  var1 = var3 + this.durationNanos;
                  this.expirationNanos = var1 == 0L ? 1L : var1;
                  return (T)var6;
               }
            }
         }

         return NullnessCasts.uncheckedCastNullableTToT(this.value);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.delegate);
         long var2 = this.durationNanos;
         return new StringBuilder(62 + String.valueOf(var1).length())
            .append("Suppliers.memoizeWithExpiration(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(", NANOS)")
            .toString();
      }
   }

   @VisibleForTesting
   static class MemoizingSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      transient volatile boolean initialized;
      transient T value;
      private static final long serialVersionUID = 0L;

      MemoizingSupplier(Supplier<T> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      @Override
      public T get() {
         if (!this.initialized) {
            synchronized (this) {
               if (!this.initialized) {
                  Object var2 = this.delegate.get();
                  this.value = (T)var2;
                  this.initialized = true;
                  return (T)var2;
               }
            }
         }

         return NullnessCasts.uncheckedCastNullableTToT(this.value);
      }

      @Override
      public String toString() {
         Object var10000;
         if (this.initialized) {
            String var1 = String.valueOf(this.value);
            var10000 = new StringBuilder(25 + String.valueOf(var1).length()).append("<supplier that returned ").append(var1).append(">").toString();
         } else {
            var10000 = this.delegate;
         }

         String var2 = String.valueOf(var10000);
         return new StringBuilder(19 + String.valueOf(var2).length()).append("Suppliers.memoize(").append(var2).append(")").toString();
      }
   }

   @VisibleForTesting
   static class NonSerializableMemoizingSupplier<T> implements Supplier<T> {
      volatile Supplier<T> delegate;
      volatile boolean initialized;
      T value;

      NonSerializableMemoizingSupplier(Supplier<T> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      @Override
      public T get() {
         if (!this.initialized) {
            synchronized (this) {
               if (!this.initialized) {
                  Object var2 = java.util.Objects.requireNonNull(this.delegate).get();
                  this.value = (T)var2;
                  this.initialized = true;
                  this.delegate = null;
                  return (T)var2;
               }
            }
         }

         return NullnessCasts.uncheckedCastNullableTToT(this.value);
      }

      @Override
      public String toString() {
         Supplier var1 = this.delegate;
         Object var10000;
         if (var1 == null) {
            String var2 = String.valueOf(this.value);
            var10000 = new StringBuilder(25 + String.valueOf(var2).length()).append("<supplier that returned ").append(var2).append(">").toString();
         } else {
            var10000 = var1;
         }

         String var3 = String.valueOf(var10000);
         return new StringBuilder(19 + String.valueOf(var3).length()).append("Suppliers.memoize(").append(var3).append(")").toString();
      }
   }

   private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
      final Function<? super F, T> function;
      final Supplier<F> supplier;
      private static final long serialVersionUID = 0L;

      SupplierComposition(Function<? super F, T> var1, Supplier<F> var2) {
         this.function = Preconditions.checkNotNull(var1);
         this.supplier = Preconditions.checkNotNull(var2);
      }

      @ParametricNullness
      @Override
      public T get() {
         return this.function.apply(this.supplier.get());
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Suppliers.SupplierComposition)) {
            return false;
         } else {
            Suppliers.SupplierComposition var2 = (Suppliers.SupplierComposition)var1;
            return this.function.equals(var2.function) && this.supplier.equals(var2.supplier);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.function, this.supplier);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.function);
         String var2 = String.valueOf(this.supplier);
         return new StringBuilder(21 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("Suppliers.compose(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private interface SupplierFunction<T> extends Function<Supplier<T>, T> {
   }

   private static enum SupplierFunctionImpl implements Suppliers.SupplierFunction<Object> {
      INSTANCE;

      public Object apply(Supplier<Object> var1) {
         return var1.get();
      }

      @Override
      public String toString() {
         return "Suppliers.supplierFunction()";
      }
   }

   private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
      @ParametricNullness
      final T instance;
      private static final long serialVersionUID = 0L;

      SupplierOfInstance(@ParametricNullness T var1) {
         this.instance = (T)var1;
      }

      @ParametricNullness
      @Override
      public T get() {
         return this.instance;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Suppliers.SupplierOfInstance) {
            Suppliers.SupplierOfInstance var2 = (Suppliers.SupplierOfInstance)var1;
            return Objects.equal(this.instance, var2.instance);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.instance);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.instance);
         return new StringBuilder(22 + String.valueOf(var1).length()).append("Suppliers.ofInstance(").append(var1).append(")").toString();
      }
   }

   private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      private static final long serialVersionUID = 0L;

      ThreadSafeSupplier(Supplier<T> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      @Override
      public T get() {
         synchronized (this.delegate) {
            return this.delegate.get();
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.delegate);
         return new StringBuilder(32 + String.valueOf(var1).length()).append("Suppliers.synchronizedSupplier(").append(var1).append(")").toString();
      }
   }
}
