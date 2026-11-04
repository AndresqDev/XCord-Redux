package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.DoNotMock;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public abstract class ImmutableMultimap<K, V> extends BaseImmutableMultimap<K, V> implements Serializable {
   final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
   final transient int size;
   private static final long serialVersionUID = 0L;

   public static <K, V> ImmutableMultimap<K, V> of() {
      return ImmutableListMultimap.of();
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1) {
      return ImmutableListMultimap.of((K)var0, (V)var1);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3) {
      return ImmutableListMultimap.of((K)var0, (V)var1, (K)var2, (V)var3);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return ImmutableListMultimap.of((K)var0, (V)var1, (K)var2, (V)var3, (K)var4, (V)var5);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return ImmutableListMultimap.of((K)var0, (V)var1, (K)var2, (V)var3, (K)var4, (V)var5, (K)var6, (V)var7);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return ImmutableListMultimap.of((K)var0, (V)var1, (K)var2, (V)var3, (K)var4, (V)var5, (K)var6, (V)var7, (K)var8, (V)var9);
   }

   public static <K, V> ImmutableMultimap.Builder<K, V> builder() {
      return new ImmutableMultimap.Builder<>();
   }

   public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0) {
      if (var0 instanceof ImmutableMultimap) {
         ImmutableMultimap var1 = (ImmutableMultimap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      }

      return ImmutableListMultimap.copyOf(var0);
   }

   @Beta
   public static <K, V> ImmutableMultimap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      return ImmutableListMultimap.copyOf(var0);
   }

   ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> var1, int var2) {
      this.map = var1;
      this.size = var2;
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public ImmutableCollection<V> removeAll(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public ImmutableCollection<V> replaceValues(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableCollection<V> get(K var1);

   public abstract ImmutableMultimap<V, K> inverse();

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean putAll(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean putAll(Multimap<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   boolean isPartialView() {
      return this.map.isPartialView();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.map.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 != null && super.containsValue(var1);
   }

   @Override
   public int size() {
      return this.size;
   }

   public ImmutableSet<K> keySet() {
      return this.map.keySet();
   }

   @Override
   Set<K> createKeySet() {
      throw new AssertionError("unreachable");
   }

   public ImmutableMap<K, Collection<V>> asMap() {
      return (ImmutableMap<K, Collection<V>>)this.map;
   }

   @Override
   Map<K, Collection<V>> createAsMap() {
      throw new AssertionError("should never be called");
   }

   public ImmutableCollection<Entry<K, V>> entries() {
      return (ImmutableCollection<Entry<K, V>>)super.entries();
   }

   ImmutableCollection<Entry<K, V>> createEntries() {
      return new ImmutableMultimap.EntryCollection<>(this);
   }

   UnmodifiableIterator<Entry<K, V>> entryIterator() {
      return new UnmodifiableIterator<Entry<K, V>>() {
         final Iterator<? extends Entry<K, ? extends ImmutableCollection<V>>> asMapItr = ImmutableMultimap.this.map.entrySet().iterator();
         K currentKey = (K)null;
         Iterator<V> valueItr = Iterators.emptyIterator();

         @Override
         public boolean hasNext() {
            return this.valueItr.hasNext() || this.asMapItr.hasNext();
         }

         public Entry<K, V> next() {
            if (!this.valueItr.hasNext()) {
               Entry var1 = this.asMapItr.next();
               this.currentKey = (K)var1.getKey();
               this.valueItr = ((ImmutableCollection)var1.getValue()).iterator();
            }

            return Maps.immutableEntry(Objects.requireNonNull(this.currentKey), this.valueItr.next());
         }
      };
   }

   @Override
   Spliterator<Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.flatMap(this.asMap().entrySet().spliterator(), var0 -> {
         Object var1 = var0.getKey();
         Collection var2 = var0.getValue();
         return CollectSpliterators.map(var2.spliterator(), var1x -> Maps.immutableEntry((K)var1, (V)var1x));
      }, 64 | (this instanceof SetMultimap ? 1 : 0), (long)this.size());
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.asMap().forEach((var1x, var2) -> var2.forEach(var2x -> var1.accept(var1x, var2x)));
   }

   public ImmutableMultiset<K> keys() {
      return (ImmutableMultiset<K>)super.keys();
   }

   ImmutableMultiset<K> createKeys() {
      return new ImmutableMultimap.Keys();
   }

   public ImmutableCollection<V> values() {
      return (ImmutableCollection<V>)super.values();
   }

   ImmutableCollection<V> createValues() {
      return new ImmutableMultimap.Values<>(this);
   }

   UnmodifiableIterator<V> valueIterator() {
      return new UnmodifiableIterator<V>() {
         Iterator<? extends ImmutableCollection<V>> valueCollectionItr = ImmutableMultimap.this.map.values().iterator();
         Iterator<V> valueItr = Iterators.emptyIterator();

         @Override
         public boolean hasNext() {
            return this.valueItr.hasNext() || this.valueCollectionItr.hasNext();
         }

         @Override
         public V next() {
            if (!this.valueItr.hasNext()) {
               this.valueItr = this.valueCollectionItr.next().iterator();
            }

            return this.valueItr.next();
         }
      };
   }

   @DoNotMock
   public static class Builder<K, V> {
      final Map<K, Collection<V>> builderMap = Platform.preservesInsertionOrderOnPutsMap();
      Comparator<? super K> keyComparator;
      Comparator<? super V> valueComparator;

      Collection<V> newMutableValueCollection() {
         return new ArrayList<>();
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> put(K var1, V var2) {
         CollectPreconditions.checkEntryNotNull(var1, var2);
         Collection var3 = this.builderMap.get(var1);
         if (var3 == null) {
            this.builderMap.put((K)var1, var3 = this.newMutableValueCollection());
         }

         var3.add(var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         return this.put((K)var1.getKey(), (V)var1.getValue());
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMultimap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         for (Entry var3 : var1) {
            this.put(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(K var1, Iterable<? extends V> var2) {
         if (var1 == null) {
            String var10003 = String.valueOf(Iterables.toString(var2));
            throw new NullPointerException(var10003.length() != 0 ? "null key in entry: null=".concat(var10003) : new String("null key in entry: null="));
         } else {
            Collection var3 = this.builderMap.get(var1);
            if (var3 != null) {
               for (Object var8 : var2) {
                  CollectPreconditions.checkEntryNotNull(var1, var8);
                  var3.add(var8);
               }

               return this;
            } else {
               Iterator var4 = var2.iterator();
               if (!var4.hasNext()) {
                  return this;
               } else {
                  var3 = this.newMutableValueCollection();

                  while (var4.hasNext()) {
                     Object var5 = var4.next();
                     CollectPreconditions.checkEntryNotNull(var1, var5);
                     var3.add(var5);
                  }

                  this.builderMap.put((K)var1, var3);
                  return this;
               }
            }
         }
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(K var1, V... var2) {
         return this.putAll((K)var1, Arrays.asList((V[])var2));
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> var1) {
         for (Entry var3 : var1.asMap().entrySet()) {
            this.putAll((K)var3.getKey(), (Iterable<? extends V>)var3.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> var1) {
         this.keyComparator = Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> var1) {
         this.valueComparator = Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableMultimap.Builder<K, V> combine(ImmutableMultimap.Builder<K, V> var1) {
         for (Entry var3 : var1.builderMap.entrySet()) {
            this.putAll((K)var3.getKey(), (Iterable<? extends V>)var3.getValue());
         }

         return this;
      }

      public ImmutableMultimap<K, V> build() {
         Object var1 = this.builderMap.entrySet();
         if (this.keyComparator != null) {
            var1 = Ordering.from(this.keyComparator).onKeys().immutableSortedCopy((Iterable)var1);
         }

         return ImmutableListMultimap.fromMapEntries((Collection<? extends Entry<? extends K, ? extends Collection<? extends V>>>)var1, this.valueComparator);
      }
   }

   private static class EntryCollection<K, V> extends ImmutableCollection<Entry<K, V>> {
      @Weak
      final ImmutableMultimap<K, V> multimap;
      private static final long serialVersionUID = 0L;

      EntryCollection(ImmutableMultimap<K, V> var1) {
         this.multimap = var1;
      }

      @Override
      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return this.multimap.entryIterator();
      }

      @Override
      boolean isPartialView() {
         return this.multimap.isPartialView();
      }

      @Override
      public int size() {
         return this.multimap.size();
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
   }

   @GwtIncompatible
   static class FieldSettersHolder {
      static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
      static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
   }

   class Keys extends ImmutableMultiset<K> {
      @Override
      public boolean contains(Object var1) {
         return ImmutableMultimap.this.containsKey(var1);
      }

      @Override
      public int count(Object var1) {
         Collection var2 = ImmutableMultimap.this.map.get(var1);
         return var2 == null ? 0 : var2.size();
      }

      @Override
      public ImmutableSet<K> elementSet() {
         return ImmutableMultimap.this.keySet();
      }

      @Override
      public int size() {
         return ImmutableMultimap.this.size();
      }

      @Override
      Multiset.Entry<K> getEntry(int var1) {
         Map.Entry var2 = ImmutableMultimap.this.map.entrySet().asList().get(var1);
         return Multisets.immutableEntry((K)var2.getKey(), ((Collection)var2.getValue()).size());
      }

      @Override
      boolean isPartialView() {
         return true;
      }

      @GwtIncompatible
      @Override
      Object writeReplace() {
         return new ImmutableMultimap.KeysSerializedForm(ImmutableMultimap.this);
      }
   }

   @GwtIncompatible
   private static final class KeysSerializedForm implements Serializable {
      final ImmutableMultimap<?, ?> multimap;

      KeysSerializedForm(ImmutableMultimap<?, ?> var1) {
         this.multimap = var1;
      }

      Object readResolve() {
         return this.multimap.keys();
      }
   }

   private static final class Values<K, V> extends ImmutableCollection<V> {
      @Weak
      private final transient ImmutableMultimap<K, V> multimap;
      private static final long serialVersionUID = 0L;

      Values(ImmutableMultimap<K, V> var1) {
         this.multimap = var1;
      }

      @Override
      public boolean contains(Object var1) {
         return this.multimap.containsValue(var1);
      }

      @Override
      public UnmodifiableIterator<V> iterator() {
         return this.multimap.valueIterator();
      }

      @GwtIncompatible
      @Override
      int copyIntoArray(Object[] var1, int var2) {
         for (ImmutableCollection var4 : this.multimap.map.values()) {
            var2 = var4.copyIntoArray(var1, var2);
         }

         return var2;
      }

      @Override
      public int size() {
         return this.multimap.size();
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }
}
