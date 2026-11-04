package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Collections;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Absent<T> extends Optional<T> {
   static final Absent<Object> INSTANCE = new Absent<>();
   private static final long serialVersionUID = 0L;

   static <T> Optional<T> withType() {
      return (Optional<T>)INSTANCE;
   }

   private Absent() {
   }

   @Override
   public boolean isPresent() {
      return false;
   }

   @Override
   public T get() {
      throw new IllegalStateException("Optional.get() cannot be called on an absent value");
   }

   @Override
   public T or(T var1) {
      return Preconditions.checkNotNull((T)var1, "use Optional.orNull() instead of Optional.or(null)");
   }

   @Override
   public Optional<T> or(Optional<? extends T> var1) {
      return Preconditions.checkNotNull(var1);
   }

   @Override
   public T or(Supplier<? extends T> var1) {
      return Preconditions.checkNotNull((T)var1.get(), "use Optional.orNull() instead of a Supplier that returns null");
   }

   @Override
   public T orNull() {
      return null;
   }

   @Override
   public Set<T> asSet() {
      return Collections.emptySet();
   }

   @Override
   public <V> Optional<V> transform(Function<? super T, V> var1) {
      Preconditions.checkNotNull(var1);
      return Optional.absent();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this;
   }

   @Override
   public int hashCode() {
      return 2040732332;
   }

   @Override
   public String toString() {
      return "Optional.absent()";
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
