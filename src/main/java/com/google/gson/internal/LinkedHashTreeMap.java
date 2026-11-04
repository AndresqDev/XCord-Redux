package com.google.gson.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class LinkedHashTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
   private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
      public int compare(Comparable var1, Comparable var2) {
         return var1.compareTo(var2);
      }
   };
   Comparator<? super K> comparator;
   LinkedHashTreeMap.Node<K, V>[] table;
   final LinkedHashTreeMap.Node<K, V> header;
   int size = 0;
   int modCount = 0;
   int threshold;
   private LinkedHashTreeMap<K, V>.EntrySet entrySet;
   private LinkedHashTreeMap<K, V>.KeySet keySet;

   public LinkedHashTreeMap() {
      this(NATURAL_ORDER);
   }

   public LinkedHashTreeMap(Comparator<? super K> var1) {
      this.comparator = var1 != null ? var1 : NATURAL_ORDER;
      this.header = new LinkedHashTreeMap.Node<>();
      this.table = new LinkedHashTreeMap.Node[16];
      this.threshold = this.table.length / 2 + this.table.length / 4;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public V get(Object var1) {
      LinkedHashTreeMap.Node var2 = this.findByObject(var1);
      return var2 != null ? var2.value : null;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.findByObject(var1) != null;
   }

   @Override
   public V put(K var1, V var2) {
      if (var1 == null) {
         throw new NullPointerException("key == null");
      } else {
         LinkedHashTreeMap.Node var3 = this.find((K)var1, true);
         Object var4 = var3.value;
         var3.value = (V)var2;
         return (V)var4;
      }
   }

   @Override
   public void clear() {
      Arrays.fill(this.table, null);
      this.size = 0;
      this.modCount++;
      LinkedHashTreeMap.Node var1 = this.header;
      LinkedHashTreeMap.Node var2 = var1.next;

      while (var2 != var1) {
         LinkedHashTreeMap.Node var3 = var2.next;
         var2.next = var2.prev = null;
         var2 = var3;
      }

      var1.next = var1.prev = var1;
   }

   @Override
   public V remove(Object var1) {
      LinkedHashTreeMap.Node var2 = this.removeInternalByKey(var1);
      return var2 != null ? var2.value : null;
   }

   LinkedHashTreeMap.Node<K, V> find(K var1, boolean var2) {
      Comparator var3 = this.comparator;
      LinkedHashTreeMap.Node[] var4 = this.table;
      int var5 = secondaryHash(var1.hashCode());
      int var6 = var5 & var4.length - 1;
      LinkedHashTreeMap.Node var7 = var4[var6];
      int var8 = 0;
      if (var7 != null) {
         Comparable var9 = var3 == NATURAL_ORDER ? (Comparable)var1 : null;

         while (true) {
            var8 = var9 != null ? var9.compareTo(var7.key) : var3.compare(var1, var7.key);
            if (var8 == 0) {
               return var7;
            }

            LinkedHashTreeMap.Node var10 = var8 < 0 ? var7.left : var7.right;
            if (var10 == null) {
               break;
            }

            var7 = var10;
         }
      }

      if (!var2) {
         return null;
      } else {
         LinkedHashTreeMap.Node var11 = this.header;
         LinkedHashTreeMap.Node var12;
         if (var7 == null) {
            if (var3 == NATURAL_ORDER && !(var1 instanceof Comparable)) {
               throw new ClassCastException(var1.getClass().getName() + " is not Comparable");
            }

            var12 = new LinkedHashTreeMap.Node<>(var7, var1, var5, var11, var11.prev);
            var4[var6] = var12;
         } else {
            var12 = new LinkedHashTreeMap.Node<>(var7, var1, var5, var11, var11.prev);
            if (var8 < 0) {
               var7.left = var12;
            } else {
               var7.right = var12;
            }

            this.rebalance(var7, true);
         }

         if (this.size++ > this.threshold) {
            this.doubleCapacity();
         }

         this.modCount++;
         return var12;
      }
   }

   LinkedHashTreeMap.Node<K, V> findByObject(Object var1) {
      try {
         return var1 != null ? this.find((K)var1, false) : null;
      } catch (ClassCastException var3) {
         return null;
      }
   }

   LinkedHashTreeMap.Node<K, V> findByEntry(Entry<?, ?> var1) {
      LinkedHashTreeMap.Node var2 = this.findByObject(var1.getKey());
      boolean var3 = var2 != null && this.equal(var2.value, var1.getValue());
      return var3 ? var2 : null;
   }

   private boolean equal(Object var1, Object var2) {
      return var1 == var2 || var1 != null && var1.equals(var2);
   }

   private static int secondaryHash(int var0) {
      var0 ^= var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   void removeInternal(LinkedHashTreeMap.Node<K, V> var1, boolean var2) {
      if (var2) {
         var1.prev.next = var1.next;
         var1.next.prev = var1.prev;
         var1.next = var1.prev = null;
      }

      LinkedHashTreeMap.Node var3 = var1.left;
      LinkedHashTreeMap.Node var4 = var1.right;
      LinkedHashTreeMap.Node var5 = var1.parent;
      if (var3 != null && var4 != null) {
         LinkedHashTreeMap.Node var6 = var3.height > var4.height ? var3.last() : var4.first();
         this.removeInternal(var6, false);
         int var7 = 0;
         var3 = var1.left;
         if (var3 != null) {
            var7 = var3.height;
            var6.left = var3;
            var3.parent = var6;
            var1.left = null;
         }

         int var8 = 0;
         var4 = var1.right;
         if (var4 != null) {
            var8 = var4.height;
            var6.right = var4;
            var4.parent = var6;
            var1.right = null;
         }

         var6.height = Math.max(var7, var8) + 1;
         this.replaceInParent(var1, var6);
      } else {
         if (var3 != null) {
            this.replaceInParent(var1, var3);
            var1.left = null;
         } else if (var4 != null) {
            this.replaceInParent(var1, var4);
            var1.right = null;
         } else {
            this.replaceInParent(var1, null);
         }

         this.rebalance(var5, false);
         this.size--;
         this.modCount++;
      }
   }

   LinkedHashTreeMap.Node<K, V> removeInternalByKey(Object var1) {
      LinkedHashTreeMap.Node var2 = this.findByObject(var1);
      if (var2 != null) {
         this.removeInternal(var2, true);
      }

      return var2;
   }

   private void replaceInParent(LinkedHashTreeMap.Node<K, V> var1, LinkedHashTreeMap.Node<K, V> var2) {
      LinkedHashTreeMap.Node var3 = var1.parent;
      var1.parent = null;
      if (var2 != null) {
         var2.parent = var3;
      }

      if (var3 != null) {
         if (var3.left == var1) {
            var3.left = var2;
         } else {
            assert var3.right == var1;

            var3.right = var2;
         }
      } else {
         int var4 = var1.hash & this.table.length - 1;
         this.table[var4] = var2;
      }
   }

   private void rebalance(LinkedHashTreeMap.Node<K, V> var1, boolean var2) {
      for (LinkedHashTreeMap.Node var3 = var1; var3 != null; var3 = var3.parent) {
         LinkedHashTreeMap.Node var4 = var3.left;
         LinkedHashTreeMap.Node var5 = var3.right;
         int var6 = var4 != null ? var4.height : 0;
         int var7 = var5 != null ? var5.height : 0;
         int var8 = var6 - var7;
         if (var8 == -2) {
            LinkedHashTreeMap.Node var9 = var5.left;
            LinkedHashTreeMap.Node var10 = var5.right;
            int var11 = var10 != null ? var10.height : 0;
            int var12 = var9 != null ? var9.height : 0;
            int var13 = var12 - var11;
            if (var13 != -1 && (var13 != 0 || var2)) {
               assert var13 == 1;

               this.rotateRight(var5);
               this.rotateLeft(var3);
            } else {
               this.rotateLeft(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 2) {
            LinkedHashTreeMap.Node var14 = var4.left;
            LinkedHashTreeMap.Node var15 = var4.right;
            int var16 = var15 != null ? var15.height : 0;
            int var17 = var14 != null ? var14.height : 0;
            int var18 = var17 - var16;
            if (var18 != 1 && (var18 != 0 || var2)) {
               assert var18 == -1;

               this.rotateLeft(var4);
               this.rotateRight(var3);
            } else {
               this.rotateRight(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 0) {
            var3.height = var6 + 1;
            if (var2) {
               break;
            }
         } else {
            assert var8 == -1 || var8 == 1;

            var3.height = Math.max(var6, var7) + 1;
            if (!var2) {
               break;
            }
         }
      }
   }

   private void rotateLeft(LinkedHashTreeMap.Node<K, V> var1) {
      LinkedHashTreeMap.Node var2 = var1.left;
      LinkedHashTreeMap.Node var3 = var1.right;
      LinkedHashTreeMap.Node var4 = var3.left;
      LinkedHashTreeMap.Node var5 = var3.right;
      var1.right = var4;
      if (var4 != null) {
         var4.parent = var1;
      }

      this.replaceInParent(var1, var3);
      var3.left = var1;
      var1.parent = var3;
      var1.height = Math.max(var2 != null ? var2.height : 0, var4 != null ? var4.height : 0) + 1;
      var3.height = Math.max(var1.height, var5 != null ? var5.height : 0) + 1;
   }

   private void rotateRight(LinkedHashTreeMap.Node<K, V> var1) {
      LinkedHashTreeMap.Node var2 = var1.left;
      LinkedHashTreeMap.Node var3 = var1.right;
      LinkedHashTreeMap.Node var4 = var2.left;
      LinkedHashTreeMap.Node var5 = var2.right;
      var1.left = var5;
      if (var5 != null) {
         var5.parent = var1;
      }

      this.replaceInParent(var1, var2);
      var2.right = var1;
      var1.parent = var2;
      var1.height = Math.max(var3 != null ? var3.height : 0, var5 != null ? var5.height : 0) + 1;
      var2.height = Math.max(var1.height, var4 != null ? var4.height : 0) + 1;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      LinkedHashTreeMap.EntrySet var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new LinkedHashTreeMap.EntrySet());
   }

   @Override
   public Set<K> keySet() {
      LinkedHashTreeMap.KeySet var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new LinkedHashTreeMap.KeySet());
   }

   private void doubleCapacity() {
      this.table = doubleCapacity(this.table);
      this.threshold = this.table.length / 2 + this.table.length / 4;
   }

   static <K, V> LinkedHashTreeMap.Node<K, V>[] doubleCapacity(LinkedHashTreeMap.Node<K, V>[] var0) {
      int var1 = var0.length;
      LinkedHashTreeMap.Node[] var2 = new LinkedHashTreeMap.Node[var1 * 2];
      LinkedHashTreeMap.AvlIterator var3 = new LinkedHashTreeMap.AvlIterator();
      LinkedHashTreeMap.AvlBuilder var4 = new LinkedHashTreeMap.AvlBuilder();
      LinkedHashTreeMap.AvlBuilder var5 = new LinkedHashTreeMap.AvlBuilder();

      for (int var6 = 0; var6 < var1; var6++) {
         LinkedHashTreeMap.Node var7 = var0[var6];
         if (var7 != null) {
            var3.reset(var7);
            int var8 = 0;
            int var9 = 0;

            LinkedHashTreeMap.Node var10;
            while ((var10 = var3.next()) != null) {
               if ((var10.hash & var1) == 0) {
                  var8++;
               } else {
                  var9++;
               }
            }

            var4.reset(var8);
            var5.reset(var9);
            var3.reset(var7);

            while ((var10 = var3.next()) != null) {
               if ((var10.hash & var1) == 0) {
                  var4.add(var10);
               } else {
                  var5.add(var10);
               }
            }

            var2[var6] = var8 > 0 ? var4.root() : null;
            var2[var6 + var1] = var9 > 0 ? var5.root() : null;
         }
      }

      return var2;
   }

   private Object writeReplace() throws ObjectStreamException {
      return new LinkedHashMap<>(this);
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      throw new InvalidObjectException("Deserialization is unsupported");
   }

   static final class AvlBuilder<K, V> {
      private LinkedHashTreeMap.Node<K, V> stack;
      private int leavesToSkip;
      private int leavesSkipped;
      private int size;

      void reset(int var1) {
         int var2 = Integer.highestOneBit(var1) * 2 - 1;
         this.leavesToSkip = var2 - var1;
         this.size = 0;
         this.leavesSkipped = 0;
         this.stack = null;
      }

      void add(LinkedHashTreeMap.Node<K, V> var1) {
         var1.left = var1.parent = var1.right = null;
         var1.height = 1;
         if (this.leavesToSkip > 0 && (this.size & 1) == 0) {
            this.size++;
            this.leavesToSkip--;
            this.leavesSkipped++;
         }

         var1.parent = this.stack;
         this.stack = var1;
         this.size++;
         if (this.leavesToSkip > 0 && (this.size & 1) == 0) {
            this.size++;
            this.leavesToSkip--;
            this.leavesSkipped++;
         }

         for (byte var2 = 4; (this.size & var2 - 1) == var2 - 1; var2 *= 2) {
            if (this.leavesSkipped == 0) {
               LinkedHashTreeMap.Node var3 = this.stack;
               LinkedHashTreeMap.Node var4 = var3.parent;
               LinkedHashTreeMap.Node var5 = var4.parent;
               var4.parent = var5.parent;
               this.stack = var4;
               var4.left = var5;
               var4.right = var3;
               var4.height = var3.height + 1;
               var5.parent = var4;
               var3.parent = var4;
            } else if (this.leavesSkipped == 1) {
               LinkedHashTreeMap.Node var6 = this.stack;
               LinkedHashTreeMap.Node var7 = var6.parent;
               this.stack = var7;
               var7.right = var6;
               var7.height = var6.height + 1;
               var6.parent = var7;
               this.leavesSkipped = 0;
            } else if (this.leavesSkipped == 2) {
               this.leavesSkipped = 0;
            }
         }
      }

      LinkedHashTreeMap.Node<K, V> root() {
         LinkedHashTreeMap.Node var1 = this.stack;
         if (var1.parent != null) {
            throw new IllegalStateException();
         } else {
            return var1;
         }
      }
   }

   static class AvlIterator<K, V> {
      private LinkedHashTreeMap.Node<K, V> stackTop;

      void reset(LinkedHashTreeMap.Node<K, V> var1) {
         LinkedHashTreeMap.Node var2 = null;

         for (LinkedHashTreeMap.Node var3 = var1; var3 != null; var3 = var3.left) {
            var3.parent = var2;
            var2 = var3;
         }

         this.stackTop = var2;
      }

      public LinkedHashTreeMap.Node<K, V> next() {
         LinkedHashTreeMap.Node var1 = this.stackTop;
         if (var1 == null) {
            return null;
         } else {
            LinkedHashTreeMap.Node var4 = var1.parent;
            var1.parent = null;

            for (LinkedHashTreeMap.Node var3 = var1.right; var3 != null; var3 = var3.left) {
               var3.parent = var4;
               var4 = var3;
            }

            this.stackTop = var4;
            return var1;
         }
      }
   }

   final class EntrySet extends AbstractSet<Entry<K, V>> {
      @Override
      public int size() {
         return LinkedHashTreeMap.this.size;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return new LinkedHashTreeMap<K, V>.LinkedTreeMapIterator<Entry<K, V>>() {
            public Entry<K, V> next() {
               return this.nextNode();
            }
         };
      }

      @Override
      public boolean contains(Object var1) {
         return var1 instanceof Entry && LinkedHashTreeMap.this.findByEntry((Entry<?, ?>)var1) != null;
      }

      @Override
      public boolean remove(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            LinkedHashTreeMap.Node var2 = LinkedHashTreeMap.this.findByEntry((Entry<?, ?>)var1);
            if (var2 == null) {
               return false;
            } else {
               LinkedHashTreeMap.this.removeInternal(var2, true);
               return true;
            }
         }
      }

      @Override
      public void clear() {
         LinkedHashTreeMap.this.clear();
      }
   }

   final class KeySet extends AbstractSet<K> {
      @Override
      public int size() {
         return LinkedHashTreeMap.this.size;
      }

      @Override
      public Iterator<K> iterator() {
         return new LinkedHashTreeMap<K, V>.LinkedTreeMapIterator<K>() {
            @Override
            public K next() {
               return this.nextNode().key;
            }
         };
      }

      @Override
      public boolean contains(Object var1) {
         return LinkedHashTreeMap.this.containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         return LinkedHashTreeMap.this.removeInternalByKey(var1) != null;
      }

      @Override
      public void clear() {
         LinkedHashTreeMap.this.clear();
      }
   }

   private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
      LinkedHashTreeMap.Node<K, V> next;
      LinkedHashTreeMap.Node<K, V> lastReturned;
      int expectedModCount;

      LinkedTreeMapIterator() {
         this.next = LinkedHashTreeMap.this.header.next;
         this.lastReturned = null;
         this.expectedModCount = LinkedHashTreeMap.this.modCount;
      }

      @Override
      public final boolean hasNext() {
         return this.next != LinkedHashTreeMap.this.header;
      }

      final LinkedHashTreeMap.Node<K, V> nextNode() {
         LinkedHashTreeMap.Node var1 = this.next;
         if (var1 == LinkedHashTreeMap.this.header) {
            throw new NoSuchElementException();
         } else if (LinkedHashTreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.next = var1.next;
            return this.lastReturned = var1;
         }
      }

      @Override
      public final void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else {
            LinkedHashTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedHashTreeMap.this.modCount;
         }
      }
   }

   static final class Node<K, V> implements Entry<K, V> {
      LinkedHashTreeMap.Node<K, V> parent;
      LinkedHashTreeMap.Node<K, V> left;
      LinkedHashTreeMap.Node<K, V> right;
      LinkedHashTreeMap.Node<K, V> next;
      LinkedHashTreeMap.Node<K, V> prev;
      final K key;
      final int hash;
      V value;
      int height;

      Node() {
         this.key = null;
         this.hash = -1;
         this.next = this.prev = this;
      }

      Node(LinkedHashTreeMap.Node<K, V> var1, K var2, int var3, LinkedHashTreeMap.Node<K, V> var4, LinkedHashTreeMap.Node<K, V> var5) {
         this.parent = var1;
         this.key = (K)var2;
         this.hash = var3;
         this.height = 1;
         this.next = var4;
         this.prev = var5;
         var5.next = this;
         var4.prev = this;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V var1) {
         Object var2 = this.value;
         this.value = (V)var1;
         return (V)var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return (this.key == null ? var2.getKey() == null : this.key.equals(var2.getKey()))
               && (this.value == null ? var2.getValue() == null : this.value.equals(var2.getValue()));
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "=" + this.value;
      }

      public LinkedHashTreeMap.Node<K, V> first() {
         LinkedHashTreeMap.Node var1 = this;

         for (LinkedHashTreeMap.Node var2 = this.left; var2 != null; var2 = var2.left) {
            var1 = var2;
         }

         return var1;
      }

      public LinkedHashTreeMap.Node<K, V> last() {
         LinkedHashTreeMap.Node var1 = this;

         for (LinkedHashTreeMap.Node var2 = this.right; var2 != null; var2 = var2.right) {
            var1 = var2;
         }

         return var1;
      }
   }
}
