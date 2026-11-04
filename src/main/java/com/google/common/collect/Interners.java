package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Interners {
   private Interners() {
   }

   public static Interners.InternerBuilder newBuilder() {
      return new Interners.InternerBuilder();
   }

   public static <E> Interner<E> newStrongInterner() {
      return newBuilder().strong().build();
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public static <E> Interner<E> newWeakInterner() {
      return newBuilder().weak().build();
   }

   public static <E> Function<E, E> asFunction(Interner<E> var0) {
      return new Interners.InternerFunction<>(Preconditions.checkNotNull(var0));
   }

   public static class InternerBuilder {
      private final MapMaker mapMaker = new MapMaker();
      private boolean strong = true;

      private InternerBuilder() {
      }

      public Interners.InternerBuilder strong() {
         this.strong = true;
         return this;
      }

      @GwtIncompatible("java.lang.ref.WeakReference")
      public Interners.InternerBuilder weak() {
         this.strong = false;
         return this;
      }

      public Interners.InternerBuilder concurrencyLevel(int var1) {
         this.mapMaker.concurrencyLevel(var1);
         return this;
      }

      public <E> Interner<E> build() {
         if (!this.strong) {
            this.mapMaker.weakKeys();
         }

         return new Interners.InternerImpl<>(this.mapMaker);
      }
   }

   private static class InternerFunction<E> implements Function<E, E> {
      private final Interner<E> interner;

      public InternerFunction(Interner<E> var1) {
         this.interner = var1;
      }

      @Override
      public E apply(E var1) {
         return this.interner.intern((E)var1);
      }

      @Override
      public int hashCode() {
         return this.interner.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Interners.InternerFunction) {
            Interners.InternerFunction var2 = (Interners.InternerFunction)var1;
            return this.interner.equals(var2.interner);
         } else {
            return false;
         }
      }
   }

   @VisibleForTesting
   static final class InternerImpl<E> implements Interner<E> {
      @VisibleForTesting
      final MapMakerInternalMap<E, MapMaker.Dummy, ?, ?> map;

      private InternerImpl(MapMaker var1) {
         this.map = MapMakerInternalMap.createWithDummyValues(var1.keyEquivalence(Equivalence.equals()));
      }

      @Override
      public E intern(E var1) {
         MapMaker.Dummy var5;
         do {
            MapMakerInternalMap.InternalEntry var2 = this.map.getEntry(var1);
            if (var2 != null) {
               var5 = (MapMaker.Dummy)var2.getKey();
               if (var5 != null) {
                  return (E)var5;
               }
            }

            var5 = this.map.putIfAbsent((E)var1, MapMaker.Dummy.VALUE);
         } while (var5 != null);

         return (E)var1;
      }
   }
}
