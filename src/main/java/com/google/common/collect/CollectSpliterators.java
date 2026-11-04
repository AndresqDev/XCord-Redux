package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectSpliterators {
   private CollectSpliterators() {
   }

   static <T> Spliterator<T> indexed(int var0, int var1, IntFunction<T> var2) {
      return indexed(var0, var1, var2, null);
   }

   static <T> Spliterator<T> indexed(int var0, final int var1, final IntFunction<T> var2, final Comparator<? super T> var3) {
      if (var3 != null) {
         Preconditions.checkArgument((var1 & 4) != 0);
      }

      class 1WithCharacteristics implements Spliterator<T> {
         private final OfInt delegate;

         _WithCharacteristics(OfInt var1x) {
            this.delegate = var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super T> var1x) {
            return this.delegate.tryAdvance(var2xx -> var1x.accept(var2.apply(var2xx)));
         }

         @Override
         public void forEachRemaining(Consumer<? super T> var1x) {
            this.delegate.forEachRemaining(var2xx -> var1x.accept(var2.apply(var2xx)));
         }

         @Override
         public Spliterator<T> trySplit() {
            OfInt var1x = this.delegate.trySplit();
            return var1x == null ? null : new 1WithCharacteristics(var1x);
         }

         @Override
         public long estimateSize() {
            return this.delegate.estimateSize();
         }

         @Override
         public int characteristics() {
            return 16464 | var1;
         }

         @Override
         public Comparator<? super T> getComparator() {
            if (this.hasCharacteristics(4)) {
               return var3;
            } else {
               throw new IllegalStateException();
            }
         }
      }

      return new 1WithCharacteristics(IntStream.range(0, var0).spliterator());
   }

   static <InElementT, OutElementT> Spliterator<OutElementT> map(
      final Spliterator<InElementT> var0, final Function<? super InElementT, ? extends OutElementT> var1
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Spliterator<OutElementT>() {
         @Override
         public boolean tryAdvance(Consumer<? super OutElementT> var1x) {
            return var0.tryAdvance(var2 -> var1x.accept(var1.apply(var2)));
         }

         @Override
         public void forEachRemaining(Consumer<? super OutElementT> var1x) {
            var0.forEachRemaining(var2 -> var1x.accept(var1.apply(var2)));
         }

         @Override
         public Spliterator<OutElementT> trySplit() {
            Spliterator var1x = var0.trySplit();
            return var1x != null ? CollectSpliterators.map(var1x, var1) : null;
         }

         @Override
         public long estimateSize() {
            return var0.estimateSize();
         }

         @Override
         public int characteristics() {
            return var0.characteristics() & -262;
         }
      };
   }

   static <T> Spliterator<T> filter(final Spliterator<T> var0, final Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      class 1Splitr implements Spliterator<T>, Consumer<T> {
         T holder = (T)null;

         @Override
         public void accept(@ParametricNullness T var1x) {
            this.holder = (T)var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super T> var1x) {
            while (var0.tryAdvance(this)) {
               boolean var3;
               try {
                  Object var2 = NullnessCasts.uncheckedCastNullableTToT(this.holder);
                  if (!var1.test(var2)) {
                     continue;
                  }

                  var1x.accept(var2);
                  var3 = true;
               } finally {
                  this.holder = null;
               }

               return var3;
            }

            return false;
         }

         @Override
         public Spliterator<T> trySplit() {
            Spliterator var1x = var0.trySplit();
            return var1x == null ? null : CollectSpliterators.filter(var1x, var1);
         }

         @Override
         public long estimateSize() {
            return var0.estimateSize() / 2L;
         }

         @Override
         public Comparator<? super T> getComparator() {
            return var0.getComparator();
         }

         @Override
         public int characteristics() {
            return var0.characteristics() & 277;
         }
      }

      return new 1Splitr();
   }

   static <InElementT, OutElementT> Spliterator<OutElementT> flatMap(
      Spliterator<InElementT> var0, Function<? super InElementT, Spliterator<OutElementT>> var1, int var2, long var3
   ) {
      Preconditions.checkArgument((var2 & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
      Preconditions.checkArgument((var2 & 4) == 0, "flatMap does not support SORTED characteristic");
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new CollectSpliterators.FlatMapSpliteratorOfObject<>(null, var0, var1, var2, var3);
   }

   static <InElementT> OfInt flatMapToInt(Spliterator<InElementT> var0, Function<? super InElementT, OfInt> var1, int var2, long var3) {
      Preconditions.checkArgument((var2 & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
      Preconditions.checkArgument((var2 & 4) == 0, "flatMap does not support SORTED characteristic");
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new CollectSpliterators.FlatMapSpliteratorOfInt(null, var0, var1, var2, var3);
   }

   static <InElementT> OfLong flatMapToLong(Spliterator<InElementT> var0, Function<? super InElementT, OfLong> var1, int var2, long var3) {
      Preconditions.checkArgument((var2 & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
      Preconditions.checkArgument((var2 & 4) == 0, "flatMap does not support SORTED characteristic");
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new CollectSpliterators.FlatMapSpliteratorOfLong(null, var0, var1, var2, var3);
   }

   static <InElementT> OfDouble flatMapToDouble(Spliterator<InElementT> var0, Function<? super InElementT, OfDouble> var1, int var2, long var3) {
      Preconditions.checkArgument((var2 & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
      Preconditions.checkArgument((var2 & 4) == 0, "flatMap does not support SORTED characteristic");
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new CollectSpliterators.FlatMapSpliteratorOfDouble(null, var0, var1, var2, var3);
   }

   abstract static class FlatMapSpliterator<InElementT, OutElementT, OutSpliteratorT extends Spliterator<OutElementT>> implements Spliterator<OutElementT> {
      @Weak
      OutSpliteratorT prefix;
      final Spliterator<InElementT> from;
      final Function<? super InElementT, OutSpliteratorT> function;
      final CollectSpliterators.FlatMapSpliterator.Factory<InElementT, OutSpliteratorT> factory;
      int characteristics;
      long estimatedSize;

      FlatMapSpliterator(
         OutSpliteratorT var1,
         Spliterator<InElementT> var2,
         Function<? super InElementT, OutSpliteratorT> var3,
         CollectSpliterators.FlatMapSpliterator.Factory<InElementT, OutSpliteratorT> var4,
         int var5,
         long var6
      ) {
         this.prefix = (OutSpliteratorT)var1;
         this.from = var2;
         this.function = var3;
         this.factory = var4;
         this.characteristics = var5;
         this.estimatedSize = var6;
      }

      @Override
      public final boolean tryAdvance(Consumer<? super OutElementT> var1) {
         while (this.prefix == null || !this.prefix.tryAdvance(var1)) {
            this.prefix = null;
            if (!this.from.tryAdvance(var1x -> this.prefix = this.function.apply(var1x))) {
               return false;
            }
         }

         if (this.estimatedSize != Long.MAX_VALUE) {
            this.estimatedSize--;
         }

         return true;
      }

      @Override
      public final void forEachRemaining(Consumer<? super OutElementT> var1) {
         if (this.prefix != null) {
            this.prefix.forEachRemaining(var1);
            this.prefix = null;
         }

         this.from.forEachRemaining(var2 -> {
            Spliterator var3 = this.function.apply(var2);
            if (var3 != null) {
               var3.forEachRemaining(var1);
            }
         });
         this.estimatedSize = 0L;
      }

      @Override
      public final OutSpliteratorT trySplit() {
         Spliterator var1 = this.from.trySplit();
         if (var1 != null) {
            int var6 = this.characteristics & -65;
            long var3 = this.estimateSize();
            if (var3 < Long.MAX_VALUE) {
               var3 /= 2L;
               this.estimatedSize -= var3;
               this.characteristics = var6;
            }

            Spliterator var5 = this.factory.newFlatMapSpliterator(this.prefix, var1, this.function, var6, var3);
            this.prefix = null;
            return (OutSpliteratorT)var5;
         } else if (this.prefix != null) {
            Spliterator var2 = this.prefix;
            this.prefix = null;
            return (OutSpliteratorT)var2;
         } else {
            return null;
         }
      }

      @Override
      public final long estimateSize() {
         if (this.prefix != null) {
            this.estimatedSize = Math.max(this.estimatedSize, this.prefix.estimateSize());
         }

         return Math.max(this.estimatedSize, 0L);
      }

      @Override
      public final int characteristics() {
         return this.characteristics;
      }

      @FunctionalInterface
      interface Factory<InElementT, OutSpliteratorT extends Spliterator<?>> {
         OutSpliteratorT newFlatMapSpliterator(
            OutSpliteratorT var1, Spliterator<InElementT> var2, Function<? super InElementT, OutSpliteratorT> var3, int var4, long var5
         );
      }
   }

   static final class FlatMapSpliteratorOfDouble<InElementT>
      extends CollectSpliterators.FlatMapSpliteratorOfPrimitive<InElementT, Double, DoubleConsumer, OfDouble>
      implements OfDouble {
      FlatMapSpliteratorOfDouble(OfDouble var1, Spliterator<InElementT> var2, Function<? super InElementT, OfDouble> var3, int var4, long var5) {
         super(var1, var2, var3, CollectSpliterators.FlatMapSpliteratorOfDouble::new, var4, var5);
      }
   }

   static final class FlatMapSpliteratorOfInt<InElementT>
      extends CollectSpliterators.FlatMapSpliteratorOfPrimitive<InElementT, Integer, IntConsumer, OfInt>
      implements OfInt {
      FlatMapSpliteratorOfInt(OfInt var1, Spliterator<InElementT> var2, Function<? super InElementT, OfInt> var3, int var4, long var5) {
         super(var1, var2, var3, CollectSpliterators.FlatMapSpliteratorOfInt::new, var4, var5);
      }
   }

   static final class FlatMapSpliteratorOfLong<InElementT>
      extends CollectSpliterators.FlatMapSpliteratorOfPrimitive<InElementT, Long, LongConsumer, OfLong>
      implements OfLong {
      FlatMapSpliteratorOfLong(OfLong var1, Spliterator<InElementT> var2, Function<? super InElementT, OfLong> var3, int var4, long var5) {
         super(var1, var2, var3, CollectSpliterators.FlatMapSpliteratorOfLong::new, var4, var5);
      }
   }

   static final class FlatMapSpliteratorOfObject<InElementT, OutElementT>
      extends CollectSpliterators.FlatMapSpliterator<InElementT, OutElementT, Spliterator<OutElementT>> {
      FlatMapSpliteratorOfObject(
         Spliterator<OutElementT> var1, Spliterator<InElementT> var2, Function<? super InElementT, Spliterator<OutElementT>> var3, int var4, long var5
      ) {
         super(var1, var2, var3, CollectSpliterators.FlatMapSpliteratorOfObject::new, var4, var5);
      }
   }

   abstract static class FlatMapSpliteratorOfPrimitive<InElementT, OutElementT, OutConsumerT, OutSpliteratorT extends OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT>>
      extends CollectSpliterators.FlatMapSpliterator<InElementT, OutElementT, OutSpliteratorT>
      implements OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT> {
      FlatMapSpliteratorOfPrimitive(
         OutSpliteratorT var1,
         Spliterator<InElementT> var2,
         Function<? super InElementT, OutSpliteratorT> var3,
         CollectSpliterators.FlatMapSpliterator.Factory<InElementT, OutSpliteratorT> var4,
         int var5,
         long var6
      ) {
         super((OutSpliteratorT)var1, var2, var3, var4, var5, var6);
      }

      @Override
      public final boolean tryAdvance(OutConsumerT var1) {
         while (this.prefix == null || !this.prefix.tryAdvance((OutConsumerT)var1)) {
            this.prefix = null;
            if (!this.from.tryAdvance(var1x -> this.prefix = this.function.apply(var1x))) {
               return false;
            }
         }

         if (this.estimatedSize != Long.MAX_VALUE) {
            this.estimatedSize--;
         }

         return true;
      }

      @Override
      public final void forEachRemaining(OutConsumerT var1) {
         if (this.prefix != null) {
            this.prefix.forEachRemaining((OutConsumerT)var1);
            this.prefix = null;
         }

         this.from.forEachRemaining(var2 -> {
            OfPrimitive var3 = this.function.apply(var2);
            if (var3 != null) {
               var3.forEachRemaining(var1);
            }
         });
         this.estimatedSize = 0L;
      }
   }
}
