package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.InlineMeValidationDisabled;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Streams {
   public static <T> Stream<T> stream(Iterable<T> var0) {
      return var0 instanceof Collection ? ((Collection)var0).stream() : StreamSupport.stream(var0.spliterator(), false);
   }

   @Deprecated
   @Beta
   @InlineMe(
      replacement = "collection.stream()"
   )
   public static <T> Stream<T> stream(Collection<T> var0) {
      return var0.stream();
   }

   @Beta
   public static <T> Stream<T> stream(Iterator<T> var0) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(var0, 0), false);
   }

   @Beta
   public static <T> Stream<T> stream(Optional<T> var0) {
      return var0.isPresent() ? Stream.of((T)var0.get()) : Stream.empty();
   }

   @Beta
   @InlineMe(
      replacement = "optional.stream()"
   )
   @InlineMeValidationDisabled("Java 9+ API only")
   public static <T> Stream<T> stream(java.util.Optional<T> var0) {
      return var0.isPresent() ? Stream.of((T)var0.get()) : Stream.empty();
   }

   @Beta
   @InlineMe(
      replacement = "optional.stream()"
   )
   @InlineMeValidationDisabled("Java 9+ API only")
   public static IntStream stream(OptionalInt var0) {
      return var0.isPresent() ? IntStream.of(var0.getAsInt()) : IntStream.empty();
   }

   @Beta
   @InlineMe(
      replacement = "optional.stream()"
   )
   @InlineMeValidationDisabled("Java 9+ API only")
   public static LongStream stream(OptionalLong var0) {
      return var0.isPresent() ? LongStream.of(var0.getAsLong()) : LongStream.empty();
   }

   @Beta
   @InlineMe(
      replacement = "optional.stream()"
   )
   @InlineMeValidationDisabled("Java 9+ API only")
   public static DoubleStream stream(OptionalDouble var0) {
      return var0.isPresent() ? DoubleStream.of(var0.getAsDouble()) : DoubleStream.empty();
   }

   private static void closeAll(BaseStream<?, ?>[] var0) {
      for (BaseStream var4 : var0) {
         var4.close();
      }
   }

   @SafeVarargs
   public static <T> Stream<T> concat(Stream<? extends T>... var0) {
      boolean var1 = false;
      int var2 = 336;
      long var3 = 0L;
      ImmutableList.Builder var5 = new ImmutableList.Builder(var0.length);

      for (Stream var9 : var0) {
         var1 |= var9.isParallel();
         Spliterator var10 = var9.spliterator();
         var5.add(var10);
         var2 &= var10.characteristics();
         var3 = LongMath.saturatedAdd(var3, var10.estimateSize());
      }

      return StreamSupport.stream(CollectSpliterators.flatMap(var5.build().spliterator(), var0x -> (Spliterator<T>)var0x, var2, var3), var1)
         .onClose(() -> closeAll(var0));
   }

   public static IntStream concat(IntStream... var0) {
      boolean var1 = false;
      int var2 = 336;
      long var3 = 0L;
      ImmutableList.Builder var5 = new ImmutableList.Builder(var0.length);

      for (IntStream var9 : var0) {
         var1 |= var9.isParallel();
         OfInt var10 = var9.spliterator();
         var5.add(var10);
         var2 &= var10.characteristics();
         var3 = LongMath.saturatedAdd(var3, var10.estimateSize());
      }

      return StreamSupport.intStream(CollectSpliterators.flatMapToInt(var5.build().spliterator(), var0x -> var0x, var2, var3), var1)
         .onClose(() -> closeAll(var0));
   }

   public static LongStream concat(LongStream... var0) {
      boolean var1 = false;
      int var2 = 336;
      long var3 = 0L;
      ImmutableList.Builder var5 = new ImmutableList.Builder(var0.length);

      for (LongStream var9 : var0) {
         var1 |= var9.isParallel();
         OfLong var10 = var9.spliterator();
         var5.add(var10);
         var2 &= var10.characteristics();
         var3 = LongMath.saturatedAdd(var3, var10.estimateSize());
      }

      return StreamSupport.longStream(CollectSpliterators.flatMapToLong(var5.build().spliterator(), var0x -> var0x, var2, var3), var1)
         .onClose(() -> closeAll(var0));
   }

   public static DoubleStream concat(DoubleStream... var0) {
      boolean var1 = false;
      int var2 = 336;
      long var3 = 0L;
      ImmutableList.Builder var5 = new ImmutableList.Builder(var0.length);

      for (DoubleStream var9 : var0) {
         var1 |= var9.isParallel();
         OfDouble var10 = var9.spliterator();
         var5.add(var10);
         var2 &= var10.characteristics();
         var3 = LongMath.saturatedAdd(var3, var10.estimateSize());
      }

      return StreamSupport.doubleStream(CollectSpliterators.flatMapToDouble(var5.build().spliterator(), var0x -> var0x, var2, var3), var1)
         .onClose(() -> closeAll(var0));
   }

   @Beta
   public static <A, B, R> Stream<R> zip(Stream<A> var0, Stream<B> var1, final BiFunction<? super A, ? super B, R> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      boolean var3 = var0.isParallel() || var1.isParallel();
      Spliterator var4 = var0.spliterator();
      Spliterator var5 = var1.spliterator();
      int var6 = var4.characteristics() & var5.characteristics() & 80;
      final Iterator var7 = Spliterators.iterator(var4);
      final Iterator var8 = Spliterators.iterator(var5);
      return StreamSupport.stream(new AbstractSpliterator<R>(Math.min(var4.estimateSize(), var5.estimateSize()), var6) {
         @Override
         public boolean tryAdvance(Consumer<? super R> var1) {
            if (var7.hasNext() && var8.hasNext()) {
               var1.accept(var2.apply(var7.next(), var8.next()));
               return true;
            } else {
               return false;
            }
         }
      }, var3).onClose(var0::close).onClose(var1::close);
   }

   @Beta
   public static <A, B> void forEachPair(Stream<A> var0, Stream<B> var1, BiConsumer<? super A, ? super B> var2) {
      Preconditions.checkNotNull(var2);
      if (!var0.isParallel() && !var1.isParallel()) {
         Iterator var3 = var0.iterator();
         Iterator var4 = var1.iterator();

         while (var3.hasNext() && var4.hasNext()) {
            var2.accept(var3.next(), var4.next());
         }
      } else {
         zip(var0, var1, Streams.TemporaryPair::new).forEach(var1x -> var2.accept(var1x.a, var1x.b));
      }
   }

   @Beta
   public static <T, R> Stream<R> mapWithIndex(Stream<T> var0, final Streams.FunctionWithIndex<? super T, ? extends R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      Spliterator var3 = var0.spliterator();

      class 1Splitr extends Streams.MapWithIndexSpliterator<Spliterator<T>, R, 1Splitr> implements Consumer<T> {
         T holder;

         _Splitr(Spliterator<T> var1x, long var2) {
            super(var1x, var2);
         }

         @Override
         public void accept(@ParametricNullness T var1x) {
            this.holder = (T)var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super R> var1x) {
            if (this.fromSpliterator.tryAdvance(this)) {
               boolean var2;
               try {
                  var1x.accept(var1.apply(NullnessCasts.uncheckedCastNullableTToT(this.holder), this.index++));
                  var2 = true;
               } finally {
                  this.holder = null;
               }

               return var2;
            } else {
               return false;
            }
         }

         1Splitr createSplit(Spliterator<T> var1x, long var2) {
            return new 1Splitr(var1x, var2);
         }
      }

      if (!var3.hasCharacteristics(16384)) {
         final Iterator var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            @Override
            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.next(), this.index++));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2).onClose(var0::close);
      } else {
         return StreamSupport.stream(new 1Splitr(var3, 0L), var2).onClose(var0::close);
      }
   }

   @Beta
   public static <R> Stream<R> mapWithIndex(IntStream var0, final Streams.IntFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfInt var3 = var0.spliterator();

      class 2Splitr extends Streams.MapWithIndexSpliterator<OfInt, R, 2Splitr> implements Spliterator<R>, IntConsumer {
         int holder;

         _Splitr(OfInt var1x, long var2) {
            super(var1x, var2);
         }

         @Override
         public void accept(int var1x) {
            this.holder = var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super R> var1x) {
            if (this.fromSpliterator.tryAdvance((IntConsumer)this)) {
               var1x.accept(var1.apply(this.holder, this.index++));
               return true;
            } else {
               return false;
            }
         }

         2Splitr createSplit(OfInt var1x, long var2) {
            return new 2Splitr(var1x, var2);
         }
      }

      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfInt var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            @Override
            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextInt(), this.index++));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2).onClose(var0::close);
      } else {
         return StreamSupport.stream(new 2Splitr(var3, 0L), var2).onClose(var0::close);
      }
   }

   @Beta
   public static <R> Stream<R> mapWithIndex(LongStream var0, final Streams.LongFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfLong var3 = var0.spliterator();

      class 3Splitr extends Streams.MapWithIndexSpliterator<OfLong, R, 3Splitr> implements Spliterator<R>, LongConsumer {
         long holder;

         _Splitr(OfLong var1x, long var2) {
            super(var1x, var2);
         }

         @Override
         public void accept(long var1x) {
            this.holder = var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super R> var1x) {
            if (this.fromSpliterator.tryAdvance((LongConsumer)this)) {
               var1x.accept(var1.apply(this.holder, this.index++));
               return true;
            } else {
               return false;
            }
         }

         3Splitr createSplit(OfLong var1x, long var2) {
            return new 3Splitr(var1x, var2);
         }
      }

      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfLong var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            @Override
            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextLong(), this.index++));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2).onClose(var0::close);
      } else {
         return StreamSupport.stream(new 3Splitr(var3, 0L), var2).onClose(var0::close);
      }
   }

   @Beta
   public static <R> Stream<R> mapWithIndex(DoubleStream var0, final Streams.DoubleFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfDouble var3 = var0.spliterator();

      class 4Splitr extends Streams.MapWithIndexSpliterator<OfDouble, R, 4Splitr> implements Spliterator<R>, DoubleConsumer {
         double holder;

         _Splitr(OfDouble var1x, long var2) {
            super(var1x, var2);
         }

         @Override
         public void accept(double var1x) {
            this.holder = var1x;
         }

         @Override
         public boolean tryAdvance(Consumer<? super R> var1x) {
            if (this.fromSpliterator.tryAdvance((DoubleConsumer)this)) {
               var1x.accept(var1.apply(this.holder, this.index++));
               return true;
            } else {
               return false;
            }
         }

         4Splitr createSplit(OfDouble var1x, long var2) {
            return new 4Splitr(var1x, var2);
         }
      }

      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfDouble var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            @Override
            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextDouble(), this.index++));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2).onClose(var0::close);
      } else {
         return StreamSupport.stream(new 4Splitr(var3, 0L), var2).onClose(var0::close);
      }
   }

   @Beta
   public static <T> java.util.Optional<T> findLast(Stream<T> var0) {
      class 1OptionalState {
         boolean set = false;
         T value = (T)null;

         void set(T var1) {
            this.set = true;
            this.value = (T)var1;
         }

         T get() {
            return Objects.requireNonNull(this.value);
         }
      }

      1OptionalState var1 = new 1OptionalState();
      ArrayDeque var2 = new ArrayDeque();
      var2.addLast(var0.spliterator());

      while (!var2.isEmpty()) {
         Spliterator var3 = (Spliterator)var2.removeLast();
         if (var3.getExactSizeIfKnown() != 0L) {
            if (var3.hasCharacteristics(16384)) {
               while (true) {
                  Spliterator var5 = var3.trySplit();
                  if (var5 == null || var5.getExactSizeIfKnown() == 0L) {
                     break;
                  }

                  if (var3.getExactSizeIfKnown() == 0L) {
                     var3 = var5;
                     break;
                  }
               }

               var3.forEachRemaining(var1::set);
               return java.util.Optional.of((T)var1.get());
            }

            Spliterator var4 = var3.trySplit();
            if (var4 != null && var4.getExactSizeIfKnown() != 0L) {
               var2.addLast(var4);
               var2.addLast(var3);
            } else {
               var3.forEachRemaining(var1::set);
               if (var1.set) {
                  return java.util.Optional.of((T)var1.get());
               }
            }
         }
      }

      return java.util.Optional.empty();
   }

   @Beta
   public static OptionalInt findLast(IntStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.map(OptionalInt::of).orElseGet(OptionalInt::empty);
   }

   @Beta
   public static OptionalLong findLast(LongStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.map(OptionalLong::of).orElseGet(OptionalLong::empty);
   }

   @Beta
   public static OptionalDouble findLast(DoubleStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
   }

   private Streams() {
   }

   @Beta
   public interface DoubleFunctionWithIndex<R> {
      @ParametricNullness
      R apply(double var1, long var3);
   }

   @Beta
   public interface FunctionWithIndex<T, R> {
      @ParametricNullness
      R apply(@ParametricNullness T var1, long var2);
   }

   @Beta
   public interface IntFunctionWithIndex<R> {
      @ParametricNullness
      R apply(int var1, long var2);
   }

   @Beta
   public interface LongFunctionWithIndex<R> {
      @ParametricNullness
      R apply(long var1, long var3);
   }

   private abstract static class MapWithIndexSpliterator<F extends Spliterator<?>, R, S extends Streams.MapWithIndexSpliterator<F, R, S>>
      implements Spliterator<R> {
      final F fromSpliterator;
      long index;

      MapWithIndexSpliterator(F var1, long var2) {
         this.fromSpliterator = (F)var1;
         this.index = var2;
      }

      abstract S createSplit(F var1, long var2);

      public S trySplit() {
         Spliterator var1 = this.fromSpliterator.trySplit();
         if (var1 == null) {
            return null;
         } else {
            Streams.MapWithIndexSpliterator var3 = this.createSplit((F)var1, this.index);
            this.index = this.index + var1.getExactSizeIfKnown();
            return (S)var3;
         }
      }

      @Override
      public long estimateSize() {
         return this.fromSpliterator.estimateSize();
      }

      @Override
      public int characteristics() {
         return this.fromSpliterator.characteristics() & 16464;
      }
   }

   private static class TemporaryPair<A, B> {
      @ParametricNullness
      final A a;
      @ParametricNullness
      final B b;

      TemporaryPair(@ParametricNullness A var1, @ParametricNullness B var2) {
         this.a = (A)var1;
         this.b = (B)var2;
      }
   }
}
