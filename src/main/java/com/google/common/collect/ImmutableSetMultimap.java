package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V> implements SetMultimap<K, V> {
   private final transient ImmutableSet<V> emptySet;
   @LazyInit
   @RetainedWith
   private transient ImmutableSetMultimap<V, K> inverse;
   @LazyInit
   @RetainedWith
   private transient ImmutableSet<Entry<K, V>> entries;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1
   ) {
      return CollectCollectors.toImmutableSetMultimap(var0, var1);
   }

   public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1
   ) {
      return CollectCollectors.flatteningToImmutableSetMultimap(var0, var1);
   }

   public static <K, V> ImmutableSetMultimap<K, V> of() {
      return EmptyImmutableSetMultimap.INSTANCE;
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1) {
      ImmutableSetMultimap.Builder var2 = builder();
      var2.put(var0, var1);
      return var2.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3) {
      ImmutableSetMultimap.Builder var4 = builder();
      var4.put(var0, var1);
      var4.put(var2, var3);
      return var4.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      ImmutableSetMultimap.Builder var6 = builder();
      var6.put(var0, var1);
      var6.put(var2, var3);
      var6.put(var4, var5);
      return var6.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      ImmutableSetMultimap.Builder var8 = builder();
      var8.put(var0, var1);
      var8.put(var2, var3);
      var8.put(var4, var5);
      var8.put(var6, var7);
      return var8.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      ImmutableSetMultimap.Builder var10 = builder();
      var10.put(var0, var1);
      var10.put(var2, var3);
      var10.put(var4, var5);
      var10.put(var6, var7);
      var10.put(var8, var9);
      return var10.build();
   }

   public static <K, V> ImmutableSetMultimap.Builder<K, V> builder() {
      return new ImmutableSetMultimap.Builder<>();
   }

   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0) {
      return copyOf(var0, null);
   }

   private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0, Comparator<? super V> var1) {
      Preconditions.checkNotNull(var0);
      if (var0.isEmpty() && var1 == null) {
         return of();
      } else {
         if (var0 instanceof ImmutableSetMultimap) {
            ImmutableSetMultimap var2 = (ImmutableSetMultimap)var0;
            if (!var2.isPartialView()) {
               return var2;
            }
         }

         return fromMapEntries(var0.asMap().entrySet(), var1);
      }
   }

   @Beta
   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      return new ImmutableSetMultimap.Builder<K, V>().putAll(var0).build();
   }

   static <K, V> ImmutableSetMultimap<K, V> fromMapEntries(
      Collection<? extends Entry<? extends K, ? extends Collection<? extends V>>> var0, Comparator<? super V> var1
   ) {
      if (var0.isEmpty()) {
         return of();
      } else {
         ImmutableMap.Builder var2 = new ImmutableMap.Builder(var0.size());
         int var3 = 0;

         for (Entry var5 : var0) {
            Object var6 = var5.getKey();
            Collection var7 = (Collection)var5.getValue();
            ImmutableSet var8 = valueSet(var1, var7);
            if (!var8.isEmpty()) {
               var2.put(var6, var8);
               var3 += var8.size();
            }
         }

         return new ImmutableSetMultimap<>(var2.build(), var3, var1);
      }
   }

   ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> var1, int var2, Comparator<? super V> var3) {
      super(var1, var2);
      this.emptySet = emptySet(var3);
   }

   public ImmutableSet<V> get(K var1) {
      ImmutableSet var2 = (ImmutableSet)this.map.get(var1);
      return MoreObjects.firstNonNull(var2, this.emptySet);
   }

   public ImmutableSetMultimap<V, K> inverse() {
      ImmutableSetMultimap var1 = this.inverse;
      return var1 == null ? (this.inverse = this.invert()) : var1;
   }

   private ImmutableSetMultimap<V, K> invert() {
      ImmutableSetMultimap.Builder var1 = builder();

      for (Entry var3 : this.entries()) {
         var1.put(var3.getValue(), var3.getKey());
      }

      ImmutableSetMultimap var4 = var1.build();
      var4.inverse = (ImmutableSetMultimap<V, K>)this;
      return var4;
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public final ImmutableSet<V> removeAll(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public final ImmutableSet<V> replaceValues(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   public ImmutableSet<Entry<K, V>> entries() {
      ImmutableSet var1 = this.entries;
      return var1 == null ? (this.entries = new ImmutableSetMultimap.EntrySet<>(this)) : var1;
   }

   private static <V> ImmutableSet<V> valueSet(Comparator<? super V> var0, Collection<? extends V> var1) {
      return (ImmutableSet<V>)(var0 == null ? ImmutableSet.copyOf(var1) : ImmutableSortedSet.copyOf(var0, var1));
   }

   private static <V> ImmutableSet<V> emptySet(Comparator<? super V> var0) {
      return (ImmutableSet<V>)(var0 == null ? ImmutableSet.of() : ImmutableSortedSet.emptySet(var0));
   }

   private static <V> ImmutableSet.Builder<V> valuesBuilder(Comparator<? super V> var0) {
      return (ImmutableSet.Builder<V>)(var0 == null ? new ImmutableSet.Builder<>() : new ImmutableSortedSet.Builder<>(var0));
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.valueComparator());
      Serialization.writeMultimap(this, var1);
   }

   Comparator<? super V> valueComparator() {
      return this.emptySet instanceof ImmutableSortedSet ? ((ImmutableSortedSet)this.emptySet).comparator() : null;
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      int var3 = var1.readInt();
      if (var3 < 0) {
         throw new InvalidObjectException(new StringBuilder(29).append("Invalid key count ").append(var3).toString());
      } else {
         ImmutableMap.Builder var4 = ImmutableMap.builder();
         int var5 = 0;

         for (int var6 = 0; var6 < var3; var6++) {
            Object var7 = var1.readObject();
            int var8 = var1.readInt();
            if (var8 <= 0) {
               throw new InvalidObjectException(new StringBuilder(31).append("Invalid value count ").append(var8).toString());
            }

            ImmutableSet.Builder var9 = valuesBuilder(var2);

            for (int var10 = 0; var10 < var8; var10++) {
               var9.add(var1.readObject());
            }

            ImmutableSet var14 = var9.build();
            if (var14.size() != var8) {
               String var11 = String.valueOf(var7);
               throw new InvalidObjectException(
                  new StringBuilder(40 + String.valueOf(var11).length()).append("Duplicate key-value pairs exist for key ").append(var11).toString()
               );
            }

            var4.put(var7, var14);
            var5 += var8;
         }

         ImmutableMap var13;
         try {
            var13 = var4.build();
         } catch (IllegalArgumentException var12) {
            throw (InvalidObjectException)new InvalidObjectException(var12.getMessage()).initCause(var12);
         }

         ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, var13);
         ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, var5);
         ImmutableSetMultimap.SetFieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(var2));
      }
   }

   public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {
      @Override
      Collection<V> newMutableValueCollection() {
         return Platform.preservesInsertionOrderOnAddsSet();
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> put(K var1, V var2) {
         super.put((K)var1, (V)var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableSetMultimap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(K var1, Iterable<? extends V> var2) {
         super.putAll((K)var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(K var1, V... var2) {
         return this.putAll((K)var1, Arrays.asList((V[])var2));
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> var1) {
         for (Entry var3 : var1.asMap().entrySet()) {
            this.putAll((K)var3.getKey(), (Iterable<? extends V>)var3.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      ImmutableSetMultimap.Builder<K, V> combine(ImmutableMultimap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> var1) {
         super.orderKeysBy(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> var1) {
         super.orderValuesBy(var1);
         return this;
      }

      public ImmutableSetMultimap<K, V> build() {
         Object var1 = this.builderMap.entrySet();
         if (this.keyComparator != null) {
            var1 = Ordering.from(this.keyComparator).onKeys().immutableSortedCopy((Iterable)var1);
         }

         return ImmutableSetMultimap.fromMapEntries((Collection<? extends Entry<? extends K, ? extends Collection<? extends V>>>)var1, this.valueComparator);
      }
   }

   private static final class EntrySet<K, V> extends ImmutableSet<Entry<K, V>> {
      @Weak
      private final transient ImmutableSetMultimap<K, V> multimap;

      EntrySet(ImmutableSetMultimap<K, V> var1) {
         this.multimap = var1;
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.multimap.containsEntry(var2.getKey(), var2.getValue());
         } else {
            return false;
         }
      }

      @Override
      public int size() {
         return this.multimap.size();
      }

      @Override
      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return this.multimap.entryIterator();
      }

      @Override
      boolean isPartialView() {
         return false;
      }
   }

   @GwtIncompatible
   private static final class SetFieldSettersHolder {
      static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");
   }
}
