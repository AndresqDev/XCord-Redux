package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.DoNotMock;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

@DoNotMock("Use Optional.of(value) or Optional.absent()")
@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
public abstract class Optional<T> implements Serializable {
   private static final long serialVersionUID = 0L;

   public static <T> Optional<T> absent() {
      return Absent.withType();
   }

   public static <T> Optional<T> of(T var0) {
      return new Present<>(Preconditions.checkNotNull((T)var0));
   }

   public static <T> Optional<T> fromNullable(T var0) {
      return (Optional<T>)(var0 == null ? absent() : new Present<>((T)var0));
   }

   public static <T> Optional<T> fromJavaUtil(java.util.Optional<T> var0) {
      return var0 == null ? null : fromNullable((T)var0.orElse(null));
   }

   public static <T> java.util.Optional<T> toJavaUtil(Optional<T> var0) {
      return var0 == null ? null : var0.toJavaUtil();
   }

   public java.util.Optional<T> toJavaUtil() {
      return java.util.Optional.ofNullable(this.orNull());
   }

   Optional() {
   }

   public abstract boolean isPresent();

   public abstract T get();

   public abstract T or(T var1);

   public abstract Optional<T> or(Optional<? extends T> var1);

   @Beta
   public abstract T or(Supplier<? extends T> var1);

   public abstract T orNull();

   public abstract Set<T> asSet();

   public abstract <V> Optional<V> transform(Function<? super T, V> var1);

   @Override
   public abstract boolean equals(Object var1);

   @Override
   public abstract int hashCode();

   @Override
   public abstract String toString();

   @Beta
   public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> var0) {
      Preconditions.checkNotNull(var0);
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
               private final Iterator<? extends Optional<? extends T>> iterator = Preconditions.checkNotNull(var0.iterator());

               @Override
               protected T computeNext() {
                  while (this.iterator.hasNext()) {
                     Optional var1 = this.iterator.next();
                     if (var1.isPresent()) {
                        return (T)var1.get();
                     }
                  }

                  return (T)this.endOfData();
               }
            };
         }
      };
   }
}
