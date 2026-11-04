package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class Synchronized {
   private Synchronized() {
   }

   private static <E> Collection<E> collection(Collection<E> var0, Object var1) {
      return new Synchronized.SynchronizedCollection<>(var0, var1);
   }

   @VisibleForTesting
   static <E> Set<E> set(Set<E> var0, Object var1) {
      return new Synchronized.SynchronizedSet<>(var0, var1);
   }

   private static <E> SortedSet<E> sortedSet(SortedSet<E> var0, Object var1) {
      return new Synchronized.SynchronizedSortedSet<>(var0, var1);
   }

   private static <E> List<E> list(List<E> var0, Object var1) {
      return (List<E>)(var0 instanceof RandomAccess
         ? new Synchronized.SynchronizedRandomAccessList<>(var0, var1)
         : new Synchronized.SynchronizedList<>(var0, var1));
   }

   static <E> Multiset<E> multiset(Multiset<E> var0, Object var1) {
      return (Multiset<E>)(!(var0 instanceof Synchronized.SynchronizedMultiset) && !(var0 instanceof ImmutableMultiset)
         ? new Synchronized.SynchronizedMultiset<>(var0, var1)
         : var0);
   }

   static <K, V> Multimap<K, V> multimap(Multimap<K, V> var0, Object var1) {
      return (Multimap<K, V>)(!(var0 instanceof Synchronized.SynchronizedMultimap) && !(var0 instanceof BaseImmutableMultimap)
         ? new Synchronized.SynchronizedMultimap<>(var0, var1)
         : var0);
   }

   static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> var0, Object var1) {
      return (ListMultimap<K, V>)(!(var0 instanceof Synchronized.SynchronizedListMultimap) && !(var0 instanceof BaseImmutableMultimap)
         ? new Synchronized.SynchronizedListMultimap<>(var0, var1)
         : var0);
   }

   static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> var0, Object var1) {
      return (SetMultimap<K, V>)(!(var0 instanceof Synchronized.SynchronizedSetMultimap) && !(var0 instanceof BaseImmutableMultimap)
         ? new Synchronized.SynchronizedSetMultimap<>(var0, var1)
         : var0);
   }

   static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> var0, Object var1) {
      return (SortedSetMultimap<K, V>)(var0 instanceof Synchronized.SynchronizedSortedSetMultimap
         ? var0
         : new Synchronized.SynchronizedSortedSetMultimap<>(var0, var1));
   }

   private static <E> Collection<E> typePreservingCollection(Collection<E> var0, Object var1) {
      if (var0 instanceof SortedSet) {
         return sortedSet((SortedSet<E>)var0, var1);
      } else if (var0 instanceof Set) {
         return set((Set<E>)var0, var1);
      } else {
         return (Collection<E>)(var0 instanceof List ? list((List<E>)var0, var1) : collection(var0, var1));
      }
   }

   private static <E> Set<E> typePreservingSet(Set<E> var0, Object var1) {
      return (Set<E>)(var0 instanceof SortedSet ? sortedSet((SortedSet<E>)var0, var1) : set(var0, var1));
   }

   @VisibleForTesting
   static <K, V> Map<K, V> map(Map<K, V> var0, Object var1) {
      return new Synchronized.SynchronizedMap<>(var0, var1);
   }

   static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> var0, Object var1) {
      return new Synchronized.SynchronizedSortedMap<>(var0, var1);
   }

   static <K, V> BiMap<K, V> biMap(BiMap<K, V> var0, Object var1) {
      return (BiMap<K, V>)(!(var0 instanceof Synchronized.SynchronizedBiMap) && !(var0 instanceof ImmutableBiMap)
         ? new Synchronized.SynchronizedBiMap<>(var0, var1, null)
         : var0);
   }

   @GwtIncompatible
   static <E> NavigableSet<E> navigableSet(NavigableSet<E> var0, Object var1) {
      return new Synchronized.SynchronizedNavigableSet<>(var0, var1);
   }

   @GwtIncompatible
   static <E> NavigableSet<E> navigableSet(NavigableSet<E> var0) {
      return navigableSet(var0, null);
   }

   @GwtIncompatible
   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> var0) {
      return navigableMap(var0, null);
   }

   @GwtIncompatible
   static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> var0, Object var1) {
      return new Synchronized.SynchronizedNavigableMap<>(var0, var1);
   }

   @GwtIncompatible
   private static <K, V> Entry<K, V> nullableSynchronizedEntry(Entry<K, V> var0, Object var1) {
      return var0 == null ? null : new Synchronized.SynchronizedEntry<>(var0, var1);
   }

   static <E> Queue<E> queue(Queue<E> var0, Object var1) {
      return (Queue<E>)(var0 instanceof Synchronized.SynchronizedQueue ? var0 : new Synchronized.SynchronizedQueue<>(var0, var1));
   }

   static <E> Deque<E> deque(Deque<E> var0, Object var1) {
      return new Synchronized.SynchronizedDeque<>(var0, var1);
   }

   static <R, C, V> Table<R, C, V> table(Table<R, C, V> var0, Object var1) {
      return new Synchronized.SynchronizedTable<>(var0, var1);
   }

   private static class SynchronizedAsMap<K, V> extends Synchronized.SynchronizedMap<K, Collection<V>> {
      transient Set<Entry<K, Collection<V>>> asMapEntrySet;
      transient Collection<Collection<V>> asMapValues;
      private static final long serialVersionUID = 0L;

      SynchronizedAsMap(Map<K, Collection<V>> var1, Object var2) {
         super(var1, var2);
      }

      public Collection<V> get(Object var1) {
         synchronized (this.mutex) {
            Collection var3 = (Collection)super.get(var1);
            return var3 == null ? null : Synchronized.typePreservingCollection(var3, this.mutex);
         }
      }

      @Override
      public Set<Entry<K, Collection<V>>> entrySet() {
         synchronized (this.mutex) {
            if (this.asMapEntrySet == null) {
               this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries<>(this.delegate().entrySet(), this.mutex);
            }

            return this.asMapEntrySet;
         }
      }

      @Override
      public Collection<Collection<V>> values() {
         synchronized (this.mutex) {
            if (this.asMapValues == null) {
               this.asMapValues = new Synchronized.SynchronizedAsMapValues<>(this.delegate().values(), this.mutex);
            }

            return this.asMapValues;
         }
      }

      @Override
      public boolean containsValue(Object var1) {
         return this.values().contains(var1);
      }
   }

   private static class SynchronizedAsMapEntries<K, V> extends Synchronized.SynchronizedSet<Entry<K, Collection<V>>> {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapEntries(Set<Entry<K, Collection<V>>> var1, Object var2) {
         super(var1, var2);
      }

      @Override
      public Iterator<Entry<K, Collection<V>>> iterator() {
         return new TransformedIterator<Entry<K, Collection<V>>, Entry<K, Collection<V>>>(super.iterator()) {
            Entry<K, Collection<V>> transform(final Entry<K, Collection<V>> var1) {
               return new ForwardingMapEntry<K, Collection<V>>() {
                  @Override
                  protected Entry<K, Collection<V>> delegate() {
                     return var1;
                  }

                  public Collection<V> getValue() {
                     return Synchronized.typePreservingCollection((Collection<V>)var1.getValue(), SynchronizedAsMapEntries.this.mutex);
                  }
               };
            }
         };
      }

      @Override
      public Object[] toArray() {
         synchronized (this.mutex) {
            return ObjectArrays.toArrayImpl(this.delegate());
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         synchronized (this.mutex) {
            return (T[])ObjectArrays.toArrayImpl(this.delegate(), var1);
         }
      }

      @Override
      public boolean contains(Object var1) {
         synchronized (this.mutex) {
            return Maps.containsEntryImpl(this.delegate(), var1);
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return Collections2.containsAllImpl(this.delegate(), var1);
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return Sets.equalsImpl(this.delegate(), var1);
            }
         }
      }

      @Override
      public boolean remove(Object var1) {
         synchronized (this.mutex) {
            return Maps.removeEntryImpl(this.delegate(), var1);
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return Iterators.removeAll(this.delegate().iterator(), var1);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return Iterators.retainAll(this.delegate().iterator(), var1);
         }
      }
   }

   private static class SynchronizedAsMapValues<V> extends Synchronized.SynchronizedCollection<Collection<V>> {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapValues(Collection<Collection<V>> var1, Object var2) {
         super(var1, var2);
      }

      @Override
      public Iterator<Collection<V>> iterator() {
         return new TransformedIterator<Collection<V>, Collection<V>>(super.iterator()) {
            Collection<V> transform(Collection<V> var1) {
               return Synchronized.typePreservingCollection(var1, SynchronizedAsMapValues.this.mutex);
            }
         };
      }
   }

   @VisibleForTesting
   static class SynchronizedBiMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements BiMap<K, V>, Serializable {
      private transient Set<V> valueSet;
      @RetainedWith
      private transient BiMap<V, K> inverse;
      private static final long serialVersionUID = 0L;

      private SynchronizedBiMap(BiMap<K, V> var1, Object var2, BiMap<V, K> var3) {
         super(var1, var2);
         this.inverse = var3;
      }

      BiMap<K, V> delegate() {
         return (BiMap<K, V>)super.delegate();
      }

      @Override
      public Set<V> values() {
         synchronized (this.mutex) {
            if (this.valueSet == null) {
               this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
            }

            return this.valueSet;
         }
      }

      @Override
      public V forcePut(K var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().forcePut((K)var1, (V)var2);
         }
      }

      @Override
      public BiMap<V, K> inverse() {
         synchronized (this.mutex) {
            if (this.inverse == null) {
               this.inverse = new Synchronized.SynchronizedBiMap<>(this.delegate().inverse(), this.mutex, this);
            }

            return this.inverse;
         }
      }
   }

   @VisibleForTesting
   static class SynchronizedCollection<E> extends Synchronized.SynchronizedObject implements Collection<E> {
      private static final long serialVersionUID = 0L;

      private SynchronizedCollection(Collection<E> var1, Object var2) {
         super(var1, var2);
      }

      Collection<E> delegate() {
         return (Collection<E>)super.delegate();
      }

      @Override
      public boolean add(E var1) {
         synchronized (this.mutex) {
            return this.delegate().add((E)var1);
         }
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         synchronized (this.mutex) {
            return this.delegate().addAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.delegate().clear();
         }
      }

      @Override
      public boolean contains(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().contains(var1);
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.delegate().containsAll(var1);
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      @Override
      public Iterator<E> iterator() {
         return this.delegate().iterator();
      }

      @Override
      public Spliterator<E> spliterator() {
         synchronized (this.mutex) {
            return this.delegate().spliterator();
         }
      }

      @Override
      public Stream<E> stream() {
         synchronized (this.mutex) {
            return this.delegate().stream();
         }
      }

      @Override
      public Stream<E> parallelStream() {
         synchronized (this.mutex) {
            return this.delegate().parallelStream();
         }
      }

      @Override
      public void forEach(Consumer<? super E> var1) {
         synchronized (this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      @Override
      public boolean remove(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         synchronized (this.mutex) {
            return this.delegate().retainAll(var1);
         }
      }

      @Override
      public boolean removeIf(Predicate<? super E> var1) {
         synchronized (this.mutex) {
            return this.delegate().removeIf(var1);
         }
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.delegate().size();
         }
      }

      @Override
      public Object[] toArray() {
         synchronized (this.mutex) {
            return this.delegate().toArray();
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         synchronized (this.mutex) {
            return (T[])this.delegate().toArray(var1);
         }
      }
   }

   private static final class SynchronizedDeque<E> extends Synchronized.SynchronizedQueue<E> implements Deque<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedDeque(Deque<E> var1, Object var2) {
         super(var1, var2);
      }

      Deque<E> delegate() {
         return (Deque<E>)super.delegate();
      }

      @Override
      public void addFirst(E var1) {
         synchronized (this.mutex) {
            this.delegate().addFirst((E)var1);
         }
      }

      @Override
      public void addLast(E var1) {
         synchronized (this.mutex) {
            this.delegate().addLast((E)var1);
         }
      }

      @Override
      public boolean offerFirst(E var1) {
         synchronized (this.mutex) {
            return this.delegate().offerFirst((E)var1);
         }
      }

      @Override
      public boolean offerLast(E var1) {
         synchronized (this.mutex) {
            return this.delegate().offerLast((E)var1);
         }
      }

      @Override
      public E removeFirst() {
         synchronized (this.mutex) {
            return this.delegate().removeFirst();
         }
      }

      @Override
      public E removeLast() {
         synchronized (this.mutex) {
            return this.delegate().removeLast();
         }
      }

      @Override
      public E pollFirst() {
         synchronized (this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      @Override
      public E pollLast() {
         synchronized (this.mutex) {
            return this.delegate().pollLast();
         }
      }

      @Override
      public E getFirst() {
         synchronized (this.mutex) {
            return this.delegate().getFirst();
         }
      }

      @Override
      public E getLast() {
         synchronized (this.mutex) {
            return this.delegate().getLast();
         }
      }

      @Override
      public E peekFirst() {
         synchronized (this.mutex) {
            return this.delegate().peekFirst();
         }
      }

      @Override
      public E peekLast() {
         synchronized (this.mutex) {
            return this.delegate().peekLast();
         }
      }

      @Override
      public boolean removeFirstOccurrence(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeFirstOccurrence(var1);
         }
      }

      @Override
      public boolean removeLastOccurrence(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeLastOccurrence(var1);
         }
      }

      @Override
      public void push(E var1) {
         synchronized (this.mutex) {
            this.delegate().push((E)var1);
         }
      }

      @Override
      public E pop() {
         synchronized (this.mutex) {
            return this.delegate().pop();
         }
      }

      @Override
      public Iterator<E> descendingIterator() {
         synchronized (this.mutex) {
            return this.delegate().descendingIterator();
         }
      }
   }

   @GwtIncompatible
   private static class SynchronizedEntry<K, V> extends Synchronized.SynchronizedObject implements Entry<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedEntry(Entry<K, V> var1, Object var2) {
         super(var1, var2);
      }

      Entry<K, V> delegate() {
         return (Entry<K, V>)super.delegate();
      }

      @Override
      public boolean equals(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().equals(var1);
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }

      @Override
      public K getKey() {
         synchronized (this.mutex) {
            return this.delegate().getKey();
         }
      }

      @Override
      public V getValue() {
         synchronized (this.mutex) {
            return this.delegate().getValue();
         }
      }

      @Override
      public V setValue(V var1) {
         synchronized (this.mutex) {
            return this.delegate().setValue((V)var1);
         }
      }
   }

   private static class SynchronizedList<E> extends Synchronized.SynchronizedCollection<E> implements List<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedList(List<E> var1, Object var2) {
         super(var1, var2);
      }

      List<E> delegate() {
         return (List<E>)super.delegate();
      }

      @Override
      public void add(int var1, E var2) {
         synchronized (this.mutex) {
            this.delegate().add(var1, (E)var2);
         }
      }

      @Override
      public boolean addAll(int var1, Collection<? extends E> var2) {
         synchronized (this.mutex) {
            return this.delegate().addAll(var1, var2);
         }
      }

      @Override
      public E get(int var1) {
         synchronized (this.mutex) {
            return this.delegate().get(var1);
         }
      }

      @Override
      public int indexOf(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().indexOf(var1);
         }
      }

      @Override
      public int lastIndexOf(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().lastIndexOf(var1);
         }
      }

      @Override
      public ListIterator<E> listIterator() {
         return this.delegate().listIterator();
      }

      @Override
      public ListIterator<E> listIterator(int var1) {
         return this.delegate().listIterator(var1);
      }

      @Override
      public E remove(int var1) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      @Override
      public E set(int var1, E var2) {
         synchronized (this.mutex) {
            return this.delegate().set(var1, (E)var2);
         }
      }

      @Override
      public void replaceAll(UnaryOperator<E> var1) {
         synchronized (this.mutex) {
            this.delegate().replaceAll(var1);
         }
      }

      @Override
      public void sort(Comparator<? super E> var1) {
         synchronized (this.mutex) {
            this.delegate().sort(var1);
         }
      }

      @Override
      public List<E> subList(int var1, int var2) {
         synchronized (this.mutex) {
            return Synchronized.list(this.delegate().subList(var1, var2), this.mutex);
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedListMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements ListMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedListMultimap(ListMultimap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      ListMultimap<K, V> delegate() {
         return (ListMultimap<K, V>)super.delegate();
      }

      @Override
      public List<V> get(K var1) {
         synchronized (this.mutex) {
            return Synchronized.list(this.delegate().get((K)var1), this.mutex);
         }
      }

      @Override
      public List<V> removeAll(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      @Override
      public List<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().replaceValues((K)var1, var2);
         }
      }
   }

   private static class SynchronizedMap<K, V> extends Synchronized.SynchronizedObject implements Map<K, V> {
      transient Set<K> keySet;
      transient Collection<V> values;
      transient Set<Entry<K, V>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMap(Map<K, V> var1, Object var2) {
         super(var1, var2);
      }

      Map<K, V> delegate() {
         return (Map<K, V>)super.delegate();
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.delegate().clear();
         }
      }

      @Override
      public boolean containsKey(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsKey(var1);
         }
      }

      @Override
      public boolean containsValue(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsValue(var1);
         }
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         synchronized (this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized (this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      @Override
      public V get(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().get(var1);
         }
      }

      @Override
      public V getOrDefault(Object var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().getOrDefault(var1, (V)var2);
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      @Override
      public Set<K> keySet() {
         synchronized (this.mutex) {
            if (this.keySet == null) {
               this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      @Override
      public V put(K var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().put((K)var1, (V)var2);
         }
      }

      @Override
      public V putIfAbsent(K var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().putIfAbsent((K)var1, (V)var2);
         }
      }

      @Override
      public boolean replace(K var1, V var2, V var3) {
         synchronized (this.mutex) {
            return this.delegate().replace((K)var1, (V)var2, (V)var3);
         }
      }

      @Override
      public V replace(K var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().replace((K)var1, (V)var2);
         }
      }

      @Override
      public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().computeIfAbsent((K)var1, var2);
         }
      }

      @Override
      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().computeIfPresent((K)var1, var2);
         }
      }

      @Override
      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().compute((K)var1, var2);
         }
      }

      @Override
      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized (this.mutex) {
            return this.delegate().merge((K)var1, (V)var2, var3);
         }
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         synchronized (this.mutex) {
            this.delegate().putAll(var1);
         }
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         synchronized (this.mutex) {
            this.delegate().replaceAll(var1);
         }
      }

      @Override
      public V remove(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1);
         }
      }

      @Override
      public boolean remove(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.delegate().size();
         }
      }

      @Override
      public Collection<V> values() {
         synchronized (this.mutex) {
            if (this.values == null) {
               this.values = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.values;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedMultimap<K, V> extends Synchronized.SynchronizedObject implements Multimap<K, V> {
      transient Set<K> keySet;
      transient Collection<V> valuesCollection;
      transient Collection<Entry<K, V>> entries;
      transient Map<K, Collection<V>> asMap;
      transient Multiset<K> keys;
      private static final long serialVersionUID = 0L;

      Multimap<K, V> delegate() {
         return (Multimap<K, V>)super.delegate();
      }

      SynchronizedMultimap(Multimap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.delegate().size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      @Override
      public boolean containsKey(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsKey(var1);
         }
      }

      @Override
      public boolean containsValue(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsValue(var1);
         }
      }

      @Override
      public boolean containsEntry(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().containsEntry(var1, var2);
         }
      }

      @Override
      public Collection<V> get(K var1) {
         synchronized (this.mutex) {
            return Synchronized.typePreservingCollection(this.delegate().get((K)var1), this.mutex);
         }
      }

      @Override
      public boolean put(K var1, V var2) {
         synchronized (this.mutex) {
            return this.delegate().put((K)var1, (V)var2);
         }
      }

      @Override
      public boolean putAll(K var1, Iterable<? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().putAll((K)var1, var2);
         }
      }

      @Override
      public boolean putAll(Multimap<? extends K, ? extends V> var1) {
         synchronized (this.mutex) {
            return this.delegate().putAll(var1);
         }
      }

      @Override
      public Collection<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().replaceValues((K)var1, var2);
         }
      }

      @Override
      public boolean remove(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      @Override
      public Collection<V> removeAll(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.delegate().clear();
         }
      }

      @Override
      public Set<K> keySet() {
         synchronized (this.mutex) {
            if (this.keySet == null) {
               this.keySet = Synchronized.typePreservingSet(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      @Override
      public Collection<V> values() {
         synchronized (this.mutex) {
            if (this.valuesCollection == null) {
               this.valuesCollection = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.valuesCollection;
         }
      }

      @Override
      public Collection<Entry<K, V>> entries() {
         synchronized (this.mutex) {
            if (this.entries == null) {
               this.entries = Synchronized.typePreservingCollection(this.delegate().entries(), this.mutex);
            }

            return this.entries;
         }
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized (this.mutex) {
            this.delegate().forEach(var1);
         }
      }

      @Override
      public Map<K, Collection<V>> asMap() {
         synchronized (this.mutex) {
            if (this.asMap == null) {
               this.asMap = new Synchronized.SynchronizedAsMap<>(this.delegate().asMap(), this.mutex);
            }

            return this.asMap;
         }
      }

      @Override
      public Multiset<K> keys() {
         synchronized (this.mutex) {
            if (this.keys == null) {
               this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
            }

            return this.keys;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedMultiset<E> extends Synchronized.SynchronizedCollection<E> implements Multiset<E> {
      transient Set<E> elementSet;
      transient Set<Multiset.Entry<E>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMultiset(Multiset<E> var1, Object var2) {
         super(var1, var2);
      }

      Multiset<E> delegate() {
         return (Multiset<E>)super.delegate();
      }

      @Override
      public int count(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().count(var1);
         }
      }

      @Override
      public int add(E var1, int var2) {
         synchronized (this.mutex) {
            return this.delegate().add((E)var1, var2);
         }
      }

      @Override
      public int remove(Object var1, int var2) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      @Override
      public int setCount(E var1, int var2) {
         synchronized (this.mutex) {
            return this.delegate().setCount((E)var1, var2);
         }
      }

      @Override
      public boolean setCount(E var1, int var2, int var3) {
         synchronized (this.mutex) {
            return this.delegate().setCount((E)var1, var2, var3);
         }
      }

      @Override
      public Set<E> elementSet() {
         synchronized (this.mutex) {
            if (this.elementSet == null) {
               this.elementSet = Synchronized.typePreservingSet(this.delegate().elementSet(), this.mutex);
            }

            return this.elementSet;
         }
      }

      @Override
      public Set<Multiset.Entry<E>> entrySet() {
         synchronized (this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.typePreservingSet(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   @GwtIncompatible
   @VisibleForTesting
   static class SynchronizedNavigableMap<K, V> extends Synchronized.SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
      transient NavigableSet<K> descendingKeySet;
      transient NavigableMap<K, V> descendingMap;
      transient NavigableSet<K> navigableKeySet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableMap(NavigableMap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      NavigableMap<K, V> delegate() {
         return (NavigableMap<K, V>)super.delegate();
      }

      @Override
      public Entry<K, V> ceilingEntry(K var1) {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().ceilingEntry((K)var1), this.mutex);
         }
      }

      @Override
      public K ceilingKey(K var1) {
         synchronized (this.mutex) {
            return this.delegate().ceilingKey((K)var1);
         }
      }

      @Override
      public NavigableSet<K> descendingKeySet() {
         synchronized (this.mutex) {
            return this.descendingKeySet == null
               ? (this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex))
               : this.descendingKeySet;
         }
      }

      @Override
      public NavigableMap<K, V> descendingMap() {
         synchronized (this.mutex) {
            return this.descendingMap == null
               ? (this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex))
               : this.descendingMap;
         }
      }

      @Override
      public Entry<K, V> firstEntry() {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().firstEntry(), this.mutex);
         }
      }

      @Override
      public Entry<K, V> floorEntry(K var1) {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().floorEntry((K)var1), this.mutex);
         }
      }

      @Override
      public K floorKey(K var1) {
         synchronized (this.mutex) {
            return this.delegate().floorKey((K)var1);
         }
      }

      @Override
      public NavigableMap<K, V> headMap(K var1, boolean var2) {
         synchronized (this.mutex) {
            return Synchronized.navigableMap(this.delegate().headMap((K)var1, var2), this.mutex);
         }
      }

      @Override
      public SortedMap<K, V> headMap(K var1) {
         return this.headMap((K)var1, false);
      }

      @Override
      public Entry<K, V> higherEntry(K var1) {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().higherEntry((K)var1), this.mutex);
         }
      }

      @Override
      public K higherKey(K var1) {
         synchronized (this.mutex) {
            return this.delegate().higherKey((K)var1);
         }
      }

      @Override
      public Entry<K, V> lastEntry() {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lastEntry(), this.mutex);
         }
      }

      @Override
      public Entry<K, V> lowerEntry(K var1) {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lowerEntry((K)var1), this.mutex);
         }
      }

      @Override
      public K lowerKey(K var1) {
         synchronized (this.mutex) {
            return this.delegate().lowerKey((K)var1);
         }
      }

      @Override
      public Set<K> keySet() {
         return this.navigableKeySet();
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         synchronized (this.mutex) {
            return this.navigableKeySet == null
               ? (this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex))
               : this.navigableKeySet;
         }
      }

      @Override
      public Entry<K, V> pollFirstEntry() {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollFirstEntry(), this.mutex);
         }
      }

      @Override
      public Entry<K, V> pollLastEntry() {
         synchronized (this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollLastEntry(), this.mutex);
         }
      }

      @Override
      public NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
         synchronized (this.mutex) {
            return Synchronized.navigableMap(this.delegate().subMap((K)var1, var2, (K)var3, var4), this.mutex);
         }
      }

      @Override
      public SortedMap<K, V> subMap(K var1, K var2) {
         return this.subMap((K)var1, true, (K)var2, false);
      }

      @Override
      public NavigableMap<K, V> tailMap(K var1, boolean var2) {
         synchronized (this.mutex) {
            return Synchronized.navigableMap(this.delegate().tailMap((K)var1, var2), this.mutex);
         }
      }

      @Override
      public SortedMap<K, V> tailMap(K var1) {
         return this.tailMap((K)var1, true);
      }
   }

   @GwtIncompatible
   @VisibleForTesting
   static class SynchronizedNavigableSet<E> extends Synchronized.SynchronizedSortedSet<E> implements NavigableSet<E> {
      transient NavigableSet<E> descendingSet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableSet(NavigableSet<E> var1, Object var2) {
         super(var1, var2);
      }

      NavigableSet<E> delegate() {
         return (NavigableSet<E>)super.delegate();
      }

      @Override
      public E ceiling(E var1) {
         synchronized (this.mutex) {
            return this.delegate().ceiling((E)var1);
         }
      }

      @Override
      public Iterator<E> descendingIterator() {
         return this.delegate().descendingIterator();
      }

      @Override
      public NavigableSet<E> descendingSet() {
         synchronized (this.mutex) {
            if (this.descendingSet == null) {
               NavigableSet var2 = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);
               this.descendingSet = var2;
               return var2;
            } else {
               return this.descendingSet;
            }
         }
      }

      @Override
      public E floor(E var1) {
         synchronized (this.mutex) {
            return this.delegate().floor((E)var1);
         }
      }

      @Override
      public NavigableSet<E> headSet(E var1, boolean var2) {
         synchronized (this.mutex) {
            return Synchronized.navigableSet(this.delegate().headSet((E)var1, var2), this.mutex);
         }
      }

      @Override
      public SortedSet<E> headSet(E var1) {
         return this.headSet((E)var1, false);
      }

      @Override
      public E higher(E var1) {
         synchronized (this.mutex) {
            return this.delegate().higher((E)var1);
         }
      }

      @Override
      public E lower(E var1) {
         synchronized (this.mutex) {
            return this.delegate().lower((E)var1);
         }
      }

      @Override
      public E pollFirst() {
         synchronized (this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      @Override
      public E pollLast() {
         synchronized (this.mutex) {
            return this.delegate().pollLast();
         }
      }

      @Override
      public NavigableSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
         synchronized (this.mutex) {
            return Synchronized.navigableSet(this.delegate().subSet((E)var1, var2, (E)var3, var4), this.mutex);
         }
      }

      @Override
      public SortedSet<E> subSet(E var1, E var2) {
         return this.subSet((E)var1, true, (E)var2, false);
      }

      @Override
      public NavigableSet<E> tailSet(E var1, boolean var2) {
         synchronized (this.mutex) {
            return Synchronized.navigableSet(this.delegate().tailSet((E)var1, var2), this.mutex);
         }
      }

      @Override
      public SortedSet<E> tailSet(E var1) {
         return this.tailSet((E)var1, true);
      }
   }

   static class SynchronizedObject implements Serializable {
      final Object delegate;
      final Object mutex;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      SynchronizedObject(Object var1, Object var2) {
         this.delegate = Preconditions.checkNotNull(var1);
         this.mutex = var2 == null ? this : var2;
      }

      Object delegate() {
         return this.delegate;
      }

      @Override
      public String toString() {
         synchronized (this.mutex) {
            return this.delegate.toString();
         }
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized (this.mutex) {
            var1.defaultWriteObject();
         }
      }
   }

   private static class SynchronizedQueue<E> extends Synchronized.SynchronizedCollection<E> implements Queue<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedQueue(Queue<E> var1, Object var2) {
         super(var1, var2);
      }

      Queue<E> delegate() {
         return (Queue<E>)super.delegate();
      }

      @Override
      public E element() {
         synchronized (this.mutex) {
            return this.delegate().element();
         }
      }

      @Override
      public boolean offer(E var1) {
         synchronized (this.mutex) {
            return this.delegate().offer((E)var1);
         }
      }

      @Override
      public E peek() {
         synchronized (this.mutex) {
            return this.delegate().peek();
         }
      }

      @Override
      public E poll() {
         synchronized (this.mutex) {
            return this.delegate().poll();
         }
      }

      @Override
      public E remove() {
         synchronized (this.mutex) {
            return this.delegate().remove();
         }
      }
   }

   private static class SynchronizedRandomAccessList<E> extends Synchronized.SynchronizedList<E> implements RandomAccess {
      private static final long serialVersionUID = 0L;

      SynchronizedRandomAccessList(List<E> var1, Object var2) {
         super(var1, var2);
      }
   }

   static class SynchronizedSet<E> extends Synchronized.SynchronizedCollection<E> implements Set<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedSet(Set<E> var1, Object var2) {
         super(var1, var2);
      }

      Set<E> delegate() {
         return (Set<E>)super.delegate();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedSetMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V> implements SetMultimap<K, V> {
      transient Set<Entry<K, V>> entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedSetMultimap(SetMultimap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      SetMultimap<K, V> delegate() {
         return (SetMultimap<K, V>)super.delegate();
      }

      @Override
      public Set<V> get(K var1) {
         synchronized (this.mutex) {
            return Synchronized.set(this.delegate().get((K)var1), this.mutex);
         }
      }

      @Override
      public Set<V> removeAll(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      @Override
      public Set<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().replaceValues((K)var1, var2);
         }
      }

      @Override
      public Set<Entry<K, V>> entries() {
         synchronized (this.mutex) {
            if (this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
            }

            return this.entrySet;
         }
      }
   }

   static class SynchronizedSortedMap<K, V> extends Synchronized.SynchronizedMap<K, V> implements SortedMap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedMap(SortedMap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      SortedMap<K, V> delegate() {
         return (SortedMap<K, V>)super.delegate();
      }

      @Override
      public Comparator<? super K> comparator() {
         synchronized (this.mutex) {
            return this.delegate().comparator();
         }
      }

      @Override
      public K firstKey() {
         synchronized (this.mutex) {
            return this.delegate().firstKey();
         }
      }

      @Override
      public SortedMap<K, V> headMap(K var1) {
         synchronized (this.mutex) {
            return Synchronized.sortedMap(this.delegate().headMap((K)var1), this.mutex);
         }
      }

      @Override
      public K lastKey() {
         synchronized (this.mutex) {
            return this.delegate().lastKey();
         }
      }

      @Override
      public SortedMap<K, V> subMap(K var1, K var2) {
         synchronized (this.mutex) {
            return Synchronized.sortedMap(this.delegate().subMap((K)var1, (K)var2), this.mutex);
         }
      }

      @Override
      public SortedMap<K, V> tailMap(K var1) {
         synchronized (this.mutex) {
            return Synchronized.sortedMap(this.delegate().tailMap((K)var1), this.mutex);
         }
      }
   }

   static class SynchronizedSortedSet<E> extends Synchronized.SynchronizedSet<E> implements SortedSet<E> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSet(SortedSet<E> var1, Object var2) {
         super(var1, var2);
      }

      SortedSet<E> delegate() {
         return (SortedSet<E>)super.delegate();
      }

      @Override
      public Comparator<? super E> comparator() {
         synchronized (this.mutex) {
            return this.delegate().comparator();
         }
      }

      @Override
      public SortedSet<E> subSet(E var1, E var2) {
         synchronized (this.mutex) {
            return Synchronized.sortedSet(this.delegate().subSet((E)var1, (E)var2), this.mutex);
         }
      }

      @Override
      public SortedSet<E> headSet(E var1) {
         synchronized (this.mutex) {
            return Synchronized.sortedSet(this.delegate().headSet((E)var1), this.mutex);
         }
      }

      @Override
      public SortedSet<E> tailSet(E var1) {
         synchronized (this.mutex) {
            return Synchronized.sortedSet(this.delegate().tailSet((E)var1), this.mutex);
         }
      }

      @Override
      public E first() {
         synchronized (this.mutex) {
            return this.delegate().first();
         }
      }

      @Override
      public E last() {
         synchronized (this.mutex) {
            return this.delegate().last();
         }
      }
   }

   private static class SynchronizedSortedSetMultimap<K, V> extends Synchronized.SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> var1, Object var2) {
         super(var1, var2);
      }

      SortedSetMultimap<K, V> delegate() {
         return (SortedSetMultimap<K, V>)super.delegate();
      }

      @Override
      public SortedSet<V> get(K var1) {
         synchronized (this.mutex) {
            return Synchronized.sortedSet(this.delegate().get((K)var1), this.mutex);
         }
      }

      @Override
      public SortedSet<V> removeAll(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().removeAll(var1);
         }
      }

      @Override
      public SortedSet<V> replaceValues(K var1, Iterable<? extends V> var2) {
         synchronized (this.mutex) {
            return this.delegate().replaceValues((K)var1, var2);
         }
      }

      @Override
      public Comparator<? super V> valueComparator() {
         synchronized (this.mutex) {
            return this.delegate().valueComparator();
         }
      }
   }

   private static final class SynchronizedTable<R, C, V> extends Synchronized.SynchronizedObject implements Table<R, C, V> {
      SynchronizedTable(Table<R, C, V> var1, Object var2) {
         super(var1, var2);
      }

      Table<R, C, V> delegate() {
         return (Table<R, C, V>)super.delegate();
      }

      @Override
      public boolean contains(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().contains(var1, var2);
         }
      }

      @Override
      public boolean containsRow(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsRow(var1);
         }
      }

      @Override
      public boolean containsColumn(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsColumn(var1);
         }
      }

      @Override
      public boolean containsValue(Object var1) {
         synchronized (this.mutex) {
            return this.delegate().containsValue(var1);
         }
      }

      @Override
      public V get(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().get(var1, var2);
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      @Override
      public int size() {
         synchronized (this.mutex) {
            return this.delegate().size();
         }
      }

      @Override
      public void clear() {
         synchronized (this.mutex) {
            this.delegate().clear();
         }
      }

      @Override
      public V put(R var1, C var2, V var3) {
         synchronized (this.mutex) {
            return this.delegate().put((R)var1, (C)var2, (V)var3);
         }
      }

      @Override
      public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
         synchronized (this.mutex) {
            this.delegate().putAll(var1);
         }
      }

      @Override
      public V remove(Object var1, Object var2) {
         synchronized (this.mutex) {
            return this.delegate().remove(var1, var2);
         }
      }

      @Override
      public Map<C, V> row(R var1) {
         synchronized (this.mutex) {
            return Synchronized.map(this.delegate().row((R)var1), this.mutex);
         }
      }

      @Override
      public Map<R, V> column(C var1) {
         synchronized (this.mutex) {
            return Synchronized.map(this.delegate().column((C)var1), this.mutex);
         }
      }

      @Override
      public Set<Table.Cell<R, C, V>> cellSet() {
         synchronized (this.mutex) {
            return Synchronized.set(this.delegate().cellSet(), this.mutex);
         }
      }

      @Override
      public Set<R> rowKeySet() {
         synchronized (this.mutex) {
            return Synchronized.set(this.delegate().rowKeySet(), this.mutex);
         }
      }

      @Override
      public Set<C> columnKeySet() {
         synchronized (this.mutex) {
            return Synchronized.set(this.delegate().columnKeySet(), this.mutex);
         }
      }

      @Override
      public Collection<V> values() {
         synchronized (this.mutex) {
            return Synchronized.collection(this.delegate().values(), this.mutex);
         }
      }

      @Override
      public Map<R, Map<C, V>> rowMap() {
         synchronized (this.mutex) {
            return Synchronized.map(Maps.transformValues(this.delegate().rowMap(), new com.google.common.base.Function<Map<C, V>, Map<C, V>>() {
               public Map<C, V> apply(Map<C, V> var1) {
                  return Synchronized.map(var1, SynchronizedTable.this.mutex);
               }
            }), this.mutex);
         }
      }

      @Override
      public Map<C, Map<R, V>> columnMap() {
         synchronized (this.mutex) {
            return Synchronized.map(Maps.transformValues(this.delegate().columnMap(), new com.google.common.base.Function<Map<R, V>, Map<R, V>>() {
               public Map<R, V> apply(Map<R, V> var1) {
                  return Synchronized.map(var1, SynchronizedTable.this.mutex);
               }
            }), this.mutex);
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.mutex) {
            return this.delegate().hashCode();
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            synchronized (this.mutex) {
               return this.delegate().equals(var1);
            }
         }
      }
   }
}
