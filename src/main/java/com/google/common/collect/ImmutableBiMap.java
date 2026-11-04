package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableBiMap<K, V> extends ImmutableBiMapFauxverideShim<K, V> implements BiMap<K, V> {
   public static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      return CollectCollectors.toImmutableBiMap(var0, var1);
   }

   public static <K, V> ImmutableBiMap<K, V> of() {
      return (ImmutableBiMap<K, V>)RegularImmutableBiMap.EMPTY;
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1) {
      return new SingletonImmutableBiMap<>((K)var0, (V)var1);
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11) {
      return RegularImmutableBiMap.fromEntries(
         entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11)
      );
   }

   public static <K, V> ImmutableBiMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13
   ) {
      return RegularImmutableBiMap.fromEntries(
         entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11), entryOf(var12, var13)
      );
   }

   public static <K, V> ImmutableBiMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15
   ) {
      return RegularImmutableBiMap.fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15)
      );
   }

   public static <K, V> ImmutableBiMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15, K var16, V var17
   ) {
      return RegularImmutableBiMap.fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15),
         entryOf(var16, var17)
      );
   }

   public static <K, V> ImmutableBiMap<K, V> of(
      K var0,
      V var1,
      K var2,
      V var3,
      K var4,
      V var5,
      K var6,
      V var7,
      K var8,
      V var9,
      K var10,
      V var11,
      K var12,
      V var13,
      K var14,
      V var15,
      K var16,
      V var17,
      K var18,
      V var19
   ) {
      return RegularImmutableBiMap.fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15),
         entryOf(var16, var17),
         entryOf(var18, var19)
      );
   }

   @SafeVarargs
   public static <K, V> ImmutableBiMap<K, V> ofEntries(Entry<? extends K, ? extends V>... var0) {
      return RegularImmutableBiMap.fromEntries(var0);
   }

   public static <K, V> ImmutableBiMap.Builder<K, V> builder() {
      return new ImmutableBiMap.Builder<>();
   }

   @Beta
   public static <K, V> ImmutableBiMap.Builder<K, V> builderWithExpectedSize(int var0) {
      CollectPreconditions.checkNonnegative(var0, "expectedSize");
      return new ImmutableBiMap.Builder<>(var0);
   }

   public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      if (var0 instanceof ImmutableBiMap) {
         ImmutableBiMap var1 = (ImmutableBiMap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      }

      return copyOf(var0.entrySet());
   }

   @Beta
   public static <K, V> ImmutableBiMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Entry[] var1 = Iterables.toArray(var0, EMPTY_ENTRY_ARRAY);
      switch (var1.length) {
         case 0:
            return of();
         case 1:
            Entry var2 = var1[0];
            return of((K)var2.getKey(), (V)var2.getValue());
         default:
            return RegularImmutableBiMap.fromEntries(var1);
      }
   }

   ImmutableBiMap() {
   }

   public abstract ImmutableBiMap<V, K> inverse();

   public ImmutableSet<V> values() {
      return this.inverse().keySet();
   }

   final ImmutableSet<V> createValues() {
      throw new AssertionError("should never be called");
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V forcePut(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   Object writeReplace() {
      return new ImmutableBiMap.SerializedForm<>(this);
   }

   public static final class Builder<K, V> extends ImmutableMap.Builder<K, V> {
      public Builder() {
      }

      Builder(int var1) {
         super(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> put(K var1, V var2) {
         super.put((K)var1, (V)var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableBiMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableBiMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         super.orderEntriesByValue(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableBiMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableBiMap<K, V> build() {
         return this.buildOrThrow();
      }

      public ImmutableBiMap<K, V> buildOrThrow() {
         switch (this.size) {
            case 0:
               return ImmutableBiMap.of();
            case 1:
               Entry var1 = Objects.requireNonNull(this.entries[0]);
               return ImmutableBiMap.of((K)var1.getKey(), (V)var1.getValue());
            default:
               if (this.valueComparator != null) {
                  if (this.entriesUsed) {
                     this.entries = Arrays.copyOf(this.entries, this.size);
                  }

                  Arrays.sort(this.entries, 0, this.size, Ordering.from(this.valueComparator).onResultOf(Maps.valueFunction()));
               }

               this.entriesUsed = true;
               return RegularImmutableBiMap.fromEntryArray(this.size, this.entries);
         }
      }

      @VisibleForTesting
      ImmutableBiMap<K, V> buildJdkBacked() {
         Preconditions.checkState(this.valueComparator == null, "buildJdkBacked is for tests only, doesn't support orderEntriesByValue");
         switch (this.size) {
            case 0:
               return ImmutableBiMap.of();
            case 1:
               Entry var1 = Objects.requireNonNull(this.entries[0]);
               return ImmutableBiMap.of((K)var1.getKey(), (V)var1.getValue());
            default:
               this.entriesUsed = true;
               return RegularImmutableBiMap.fromEntryArray(this.size, this.entries);
         }
      }
   }

   private static class SerializedForm<K, V> extends ImmutableMap.SerializedForm<K, V> {
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableBiMap<K, V> var1) {
         super(var1);
      }

      ImmutableBiMap.Builder<K, V> makeBuilder(int var1) {
         return new ImmutableBiMap.Builder<>(var1);
      }
   }
}
