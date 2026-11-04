package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Atomics {
   private Atomics() {
   }

   public static <V> AtomicReference<V> newReference() {
      return new AtomicReference<>();
   }

   public static <V> AtomicReference<V> newReference(@ParametricNullness V var0) {
      return new AtomicReference<>((V)var0);
   }

   public static <E> AtomicReferenceArray<E> newReferenceArray(int var0) {
      return new AtomicReferenceArray<>(var0);
   }

   public static <E> AtomicReferenceArray<E> newReferenceArray(E[] var0) {
      return new AtomicReferenceArray<>((E[])var0);
   }
}
