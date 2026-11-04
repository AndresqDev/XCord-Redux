package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Collections;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Present<T> extends Optional<T> {
   private final T reference;
   private static final long serialVersionUID = 0L;

   Present(T var1) {
      this.reference = (T)var1;
   }

   @Override
   public boolean isPresent() {
      return true;
   }

   @Override
   public T get() {
      return this.reference;
   }

   @Override
   public T or(T var1) {
      Preconditions.checkNotNull(var1, "use Optional.orNull() instead of Optional.or(null)");
      return this.reference;
   }

   @Override
   public Optional<T> or(Optional<? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return this;
   }

   @Override
   public T or(Supplier<? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return this.reference;
   }

   @Override
   public T orNull() {
      return this.reference;
   }

   @Override
   public Set<T> asSet() {
      return Collections.singleton(this.reference);
   }

   @Override
   public <V> Optional<V> transform(Function<? super T, V> var1) {
      return (Optional<V>)(new Present<>(
         Preconditions.checkNotNull(var1.apply(this.reference), "the Function passed to Optional.transform() must not return null.")
      ));
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof Present) {
         Present var2 = (Present)var1;
         return this.reference.equals(var2.reference);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 1502476572 + this.reference.hashCode();
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.reference);
      return new StringBuilder(13 + String.valueOf(var1).length()).append("Optional.of(").append(var1).append(")").toString();
   }
}
