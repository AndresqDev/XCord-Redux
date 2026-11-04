package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public class LinkedListMultimap<K, V> extends AbstractMultimap<K, V> implements ListMultimap<K, V>, Serializable {
   private transient LinkedListMultimap.Node<K, V> head;
   private transient LinkedListMultimap.Node<K, V> tail;
   private transient Map<K, LinkedListMultimap.KeyList<K, V>> keyToKeyList;
   private transient int size;
   private transient int modCount;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K, V> LinkedListMultimap<K, V> create() {
      return new LinkedListMultimap<>();
   }

   public static <K, V> LinkedListMultimap<K, V> create(int var0) {
      return new LinkedListMultimap<>(var0);
   }

   public static <K, V> LinkedListMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      return new LinkedListMultimap<>(var0);
   }

   LinkedListMultimap() {
      this(12);
   }

   private LinkedListMultimap(int var1) {
      this.keyToKeyList = Platform.newHashMapWithExpectedSize(var1);
   }

   private LinkedListMultimap(Multimap<? extends K, ? extends V> var1) {
      this(var1.keySet().size());
      this.putAll(var1);
   }

   @CanIgnoreReturnValue
   private LinkedListMultimap.Node<K, V> addNode(@ParametricNullness K var1, @ParametricNullness V var2, LinkedListMultimap.Node<K, V> var3) {
      LinkedListMultimap.Node var4 = new LinkedListMultimap.Node<>(var1, var2);
      if (this.head == null) {
         this.head = this.tail = var4;
         this.keyToKeyList.put((K)var1, new LinkedListMultimap.KeyList<>(var4));
         this.modCount++;
      } else if (var3 == null) {
         Objects.requireNonNull(this.tail).next = var4;
         var4.previous = this.tail;
         this.tail = var4;
         LinkedListMultimap.KeyList var5 = this.keyToKeyList.get(var1);
         if (var5 == null) {
            this.keyToKeyList.put((K)var1, new LinkedListMultimap.KeyList<>(var4));
            this.modCount++;
         } else {
            var5.count++;
            LinkedListMultimap.Node var6 = var5.tail;
            var6.nextSibling = var4;
            var4.previousSibling = var6;
            var5.tail = var4;
         }
      } else {
         LinkedListMultimap.KeyList var7 = Objects.requireNonNull(this.keyToKeyList.get(var1));
         var7.count++;
         var4.previous = var3.previous;
         var4.previousSibling = var3.previousSibling;
         var4.next = var3;
         var4.nextSibling = var3;
         if (var3.previousSibling == null) {
            var7.head = var4;
         } else {
            var3.previousSibling.nextSibling = var4;
         }

         if (var3.previous == null) {
            this.head = var4;
         } else {
            var3.previous.next = var4;
         }

         var3.previous = var4;
         var3.previousSibling = var4;
      }

      this.size++;
      return var4;
   }

   private void removeNode(LinkedListMultimap.Node<K, V> var1) {
      if (var1.previous != null) {
         var1.previous.next = var1.next;
      } else {
         this.head = var1.next;
      }

      if (var1.next != null) {
         var1.next.previous = var1.previous;
      } else {
         this.tail = var1.previous;
      }

      if (var1.previousSibling == null && var1.nextSibling == null) {
         LinkedListMultimap.KeyList var3 = Objects.requireNonNull(this.keyToKeyList.remove(var1.key));
         var3.count = 0;
         this.modCount++;
      } else {
         LinkedListMultimap.KeyList var2 = Objects.requireNonNull(this.keyToKeyList.get(var1.key));
         var2.count--;
         if (var1.previousSibling == null) {
            var2.head = Objects.requireNonNull(var1.nextSibling);
         } else {
            var1.previousSibling.nextSibling = var1.nextSibling;
         }

         if (var1.nextSibling == null) {
            var2.tail = Objects.requireNonNull(var1.previousSibling);
         } else {
            var1.nextSibling.previousSibling = var1.previousSibling;
         }
      }

      this.size--;
   }

   private void removeAllNodes(@ParametricNullness K var1) {
      Iterators.clear(new LinkedListMultimap.ValueForKeyIterator(var1));
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.head == null;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.keyToKeyList.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
      this.addNode((K)var1, (V)var2, null);
      return true;
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      List var3 = this.getCopy((K)var1);
      LinkedListMultimap.ValueForKeyIterator var4 = new LinkedListMultimap.ValueForKeyIterator(var1);
      Iterator var5 = var2.iterator();

      while (var4.hasNext() && var5.hasNext()) {
         var4.next();
         var4.set(var5.next());
      }

      while (var4.hasNext()) {
         var4.next();
         var4.remove();
      }

      while (var5.hasNext()) {
         var4.add(var5.next());
      }

      return var3;
   }

   private List<V> getCopy(@ParametricNullness K var1) {
      return Collections.unmodifiableList(Lists.newArrayList(new LinkedListMultimap.ValueForKeyIterator(var1)));
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> removeAll(@Nullable Object var1) {
      List var3 = this.getCopy((K)var1);
      this.removeAllNodes((K)var1);
      return var3;
   }

   @Override
   public void clear() {
      this.head = null;
      this.tail = null;
      this.keyToKeyList.clear();
      this.size = 0;
      this.modCount++;
   }

   @Override
   public List<V> get(@ParametricNullness final K var1) {
      return new AbstractSequentialList<V>() {
         @Override
         public int size() {
            LinkedListMultimap.KeyList var1x = LinkedListMultimap.this.keyToKeyList.get(var1);
            return var1x == null ? 0 : var1x.count;
         }

         @Override
         public ListIterator<V> listIterator(int var1x) {
            return LinkedListMultimap.this.new ValueForKeyIterator(var1, var1x);
         }
      };
   }

   @Override
   Set<K> createKeySet() {
      class 1KeySetImpl extends Sets.ImprovedAbstractSet<K> {
         @Override
         public int size() {
            return LinkedListMultimap.this.keyToKeyList.size();
         }

         @Override
         public Iterator<K> iterator() {
            return LinkedListMultimap.this.new DistinctKeyIterator();
         }

         @Override
         public boolean contains(Object var1) {
            return LinkedListMultimap.this.containsKey(var1);
         }

         @Override
         public boolean remove(Object var1) {
            return !LinkedListMultimap.this.removeAll(var1).isEmpty();
         }
      }

      return new 1KeySetImpl();
   }

   @Override
   Multiset<K> createKeys() {
      return new Multimaps.Keys<>(this);
   }

   public List<V> values() {
      return (List<V>)super.values();
   }

   List<V> createValues() {
      class 1ValuesImpl extends AbstractSequentialList<V> {
         @Override
         public int size() {
            return LinkedListMultimap.this.size;
         }

         @Override
         public ListIterator<V> listIterator(int var1) {
            final LinkedListMultimap.NodeIterator var2 = LinkedListMultimap.this.new NodeIterator(var1);
            return new TransformedListIterator<Entry<K, V>, V>(this, var2) {
               @ParametricNullness
               V transform(Entry<K, V> var1) {
                  return (V)var1.getValue();
               }

               @Override
               public void set(@ParametricNullness V var1) {
                  var2.setValue((V)var1);
               }
            };
         }
      }

      return new 1ValuesImpl();
   }

   public List<Entry<K, V>> entries() {
      return (List<Entry<K, V>>)super.entries();
   }

   List<Entry<K, V>> createEntries() {
      class 1EntriesImpl extends AbstractSequentialList<Entry<K, V>> {
         @Override
         public int size() {
            return LinkedListMultimap.this.size;
         }

         @Override
         public ListIterator<Entry<K, V>> listIterator(int var1) {
            return LinkedListMultimap.this.new NodeIterator(var1);
         }

         @Override
         public void forEach(Consumer<? super Entry<K, V>> var1) {
            Preconditions.checkNotNull(var1);

            for (LinkedListMultimap.Node var2 = LinkedListMultimap.this.head; var2 != null; var2 = var2.next) {
               var1.accept(var2);
            }
         }
      }

      return new 1EntriesImpl();
   }

   @Override
   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   @Override
   Map<K, Collection<V>> createAsMap() {
      return new Multimaps.AsMap<>(this);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size());

      for (Entry var3 : this.entries()) {
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keyToKeyList = Maps.newLinkedHashMap();
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Object var4 = var1.readObject();
         Object var5 = var1.readObject();
         this.put((K)var4, (V)var5);
      }
   }

   private class DistinctKeyIterator implements Iterator<K> {
      final Set<K> seenKeys = Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
      LinkedListMultimap.Node<K, V> next = LinkedListMultimap.this.head;
      LinkedListMultimap.Node<K, V> current;
      int expectedModCount = LinkedListMultimap.this.modCount;

      private DistinctKeyIterator() {
      }

      private void checkForConcurrentModification() {
         if (LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      @Override
      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      @ParametricNullness
      @Override
      public K next() {
         this.checkForConcurrentModification();
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.current = this.next;
            this.seenKeys.add(this.current.key);

            do {
               this.next = this.next.next;
            } while (this.next != null && !this.seenKeys.add(this.next.key));

            return this.current.key;
         }
      }

      @Override
      public void remove() {
         this.checkForConcurrentModification();
         Preconditions.checkState(this.current != null, "no calls to next() since the last call to remove()");
         LinkedListMultimap.this.removeAllNodes(this.current.key);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }
   }

   private static class KeyList<K, V> {
      LinkedListMultimap.Node<K, V> head;
      LinkedListMultimap.Node<K, V> tail;
      int count;

      KeyList(LinkedListMultimap.Node<K, V> var1) {
         this.head = var1;
         this.tail = var1;
         var1.previousSibling = null;
         var1.nextSibling = null;
         this.count = 1;
      }
   }

   private static final class Node<K, V> extends AbstractMapEntry<K, V> {
      @ParametricNullness
      final K key;
      @ParametricNullness
      V value;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> previous;
      LinkedListMultimap.Node<K, V> nextSibling;
      LinkedListMultimap.Node<K, V> previousSibling;

      Node(@ParametricNullness K var1, @ParametricNullness V var2) {
         this.key = (K)var1;
         this.value = (V)var2;
      }

      @ParametricNullness
      @Override
      public K getKey() {
         return this.key;
      }

      @ParametricNullness
      @Override
      public V getValue() {
         return this.value;
      }

      @ParametricNullness
      @Override
      public V setValue(@ParametricNullness V var1) {
         Object var2 = this.value;
         this.value = (V)var1;
         return (V)var2;
      }
   }

   private class NodeIterator implements ListIterator<Entry<K, V>> {
      int nextIndex;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> current;
      LinkedListMultimap.Node<K, V> previous;
      int expectedModCount = LinkedListMultimap.this.modCount;

      NodeIterator(int var2) {
         int var3 = LinkedListMultimap.this.size();
         Preconditions.checkPositionIndex(var2, var3);
         if (var2 >= var3 / 2) {
            this.previous = LinkedListMultimap.this.tail;
            this.nextIndex = var3;

            while (var2++ < var3) {
               this.previous();
            }
         } else {
            this.next = LinkedListMultimap.this.head;

            while (var2-- > 0) {
               this.next();
            }
         }

         this.current = null;
      }

      private void checkForConcurrentModification() {
         if (LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      @Override
      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      @CanIgnoreReturnValue
      public LinkedListMultimap.Node<K, V> next() {
         this.checkForConcurrentModification();
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.previous = this.current = this.next;
            this.next = this.next.next;
            this.nextIndex++;
            return this.current;
         }
      }

      @Override
      public void remove() {
         this.checkForConcurrentModification();
         Preconditions.checkState(this.current != null, "no calls to next() since the last call to remove()");
         if (this.current != this.next) {
            this.previous = this.current.previous;
            this.nextIndex--;
         } else {
            this.next = this.current.next;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      @Override
      public boolean hasPrevious() {
         this.checkForConcurrentModification();
         return this.previous != null;
      }

      @CanIgnoreReturnValue
      public LinkedListMultimap.Node<K, V> previous() {
         this.checkForConcurrentModification();
         if (this.previous == null) {
            throw new NoSuchElementException();
         } else {
            this.next = this.current = this.previous;
            this.previous = this.previous.previous;
            this.nextIndex--;
            return this.current;
         }
      }

      @Override
      public int nextIndex() {
         return this.nextIndex;
      }

      @Override
      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void set(Entry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Entry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      void setValue(@ParametricNullness V var1) {
         Preconditions.checkState(this.current != null);
         this.current.value = (V)var1;
      }
   }

   private class ValueForKeyIterator implements ListIterator<V> {
      @ParametricNullness
      final K key;
      int nextIndex;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> current;
      LinkedListMultimap.Node<K, V> previous;

      ValueForKeyIterator(@ParametricNullness K var2) {
         this.key = (K)var2;
         LinkedListMultimap.KeyList var3 = LinkedListMultimap.this.keyToKeyList.get(var2);
         this.next = var3 == null ? null : var3.head;
      }

      public ValueForKeyIterator(@ParametricNullness K var2, int var3) {
         LinkedListMultimap.KeyList var4 = LinkedListMultimap.this.keyToKeyList.get(var2);
         int var5 = var4 == null ? 0 : var4.count;
         Preconditions.checkPositionIndex(var3, var5);
         if (var3 >= var5 / 2) {
            this.previous = var4 == null ? null : var4.tail;
            this.nextIndex = var5;

            while (var3++ < var5) {
               this.previous();
            }
         } else {
            this.next = var4 == null ? null : var4.head;

            while (var3-- > 0) {
               this.next();
            }
         }

         this.key = (K)var2;
         this.current = null;
      }

      @Override
      public boolean hasNext() {
         return this.next != null;
      }

      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public V next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.previous = this.current = this.next;
            this.next = this.next.nextSibling;
            this.nextIndex++;
            return this.current.value;
         }
      }

      @Override
      public boolean hasPrevious() {
         return this.previous != null;
      }

      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public V previous() {
         if (this.previous == null) {
            throw new NoSuchElementException();
         } else {
            this.next = this.current = this.previous;
            this.previous = this.previous.previousSibling;
            this.nextIndex--;
            return this.current.value;
         }
      }

      @Override
      public int nextIndex() {
         return this.nextIndex;
      }

      @Override
      public int previousIndex() {
         return this.nextIndex - 1;
      }

      @Override
      public void remove() {
         Preconditions.checkState(this.current != null, "no calls to next() since the last call to remove()");
         if (this.current != this.next) {
            this.previous = this.current.previousSibling;
            this.nextIndex--;
         } else {
            this.next = this.current.nextSibling;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
      }

      @Override
      public void set(@ParametricNullness V var1) {
         Preconditions.checkState(this.current != null);
         this.current.value = (V)var1;
      }

      @Override
      public void add(@ParametricNullness V var1) {
         this.previous = LinkedListMultimap.this.addNode(this.key, (V)var1, this.next);
         this.nextIndex++;
         this.current = null;
      }
   }
}
