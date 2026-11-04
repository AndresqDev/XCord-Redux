package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class FastMap<K, V> implements Serializable, Cloneable, Map<K, V> {
   private transient FastMap.EntryImpl[] _entries;
   private transient int _capacity;
   private transient int _mask;
   private transient FastMap.EntryImpl _poolFirst;
   private transient FastMap.EntryImpl _mapFirst;
   private transient FastMap.EntryImpl _mapLast;
   private transient int _size;
   private transient FastMap<K, V>.Values _values;
   private transient FastMap<K, V>.EntrySet _entrySet;
   private transient FastMap<K, V>.KeySet _keySet;

   public FastMap() {
      this.initialize(256);
   }

   public FastMap(Map var1) {
      int var2 = var1 instanceof FastMap ? ((FastMap)var1).capacity() : var1.size();
      this.initialize(var2);
      this.putAll(var1);
   }

   public FastMap(int var1) {
      this.initialize(var1);
   }

   @Override
   public int size() {
      return this._size;
   }

   public int capacity() {
      return this._capacity;
   }

   @Override
   public boolean isEmpty() {
      return this._size == 0;
   }

   @Override
   public boolean containsKey(Object var1) {
      for (FastMap.EntryImpl var2 = this._entries[keyHash(var1) & this._mask]; var2 != null; var2 = var2._next) {
         if (var1.equals(var2._key)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsValue(Object var1) {
      for (FastMap.EntryImpl var2 = this._mapFirst; var2 != null; var2 = var2._after) {
         if (var1.equals(var2._value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public V get(Object var1) {
      for (FastMap.EntryImpl var2 = this._entries[keyHash(var1) & this._mask]; var2 != null; var2 = var2._next) {
         if (var1.equals(var2._key)) {
            return var2._value;
         }
      }

      return null;
   }

   public Entry getEntry(Object var1) {
      for (FastMap.EntryImpl var2 = this._entries[keyHash(var1) & this._mask]; var2 != null; var2 = var2._next) {
         if (var1.equals(var2._key)) {
            return var2;
         }
      }

      return null;
   }

   @Override
   public Object put(Object var1, Object var2) {
      for (FastMap.EntryImpl var3 = this._entries[keyHash(var1) & this._mask]; var3 != null; var3 = var3._next) {
         if (var1.equals(var3._key)) {
            Object var4 = var3._value;
            var3._value = (V)var2;
            return var4;
         }
      }

      this.addEntry(var1, var2);
      return null;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.addEntry(var3.getKey(), var3.getValue());
      }
   }

   @Override
   public V remove(Object var1) {
      for (FastMap.EntryImpl var2 = this._entries[keyHash(var1) & this._mask]; var2 != null; var2 = var2._next) {
         if (var1.equals(var2._key)) {
            Object var3 = var2._value;
            this.removeEntry(var2);
            return (V)var3;
         }
      }

      return null;
   }

   @Override
   public void clear() {
      for (FastMap.EntryImpl var1 = this._mapFirst; var1 != null; var1 = var1._after) {
         var1._key = null;
         var1._value = null;
         var1._before = null;
         var1._next = null;
         if (var1._previous == null) {
            this._entries[var1._index] = null;
         } else {
            var1._previous = null;
         }
      }

      if (this._mapLast != null) {
         this._mapLast._after = this._poolFirst;
         this._poolFirst = this._mapFirst;
         this._mapFirst = null;
         this._mapLast = null;
         this._size = 0;
         this.sizeChanged();
      }
   }

   public void setCapacity(int var1) {
      if (var1 > this._capacity) {
         for (int var2 = this._capacity; var2 < var1; var2++) {
            FastMap.EntryImpl var3 = new FastMap.EntryImpl();
            var3._after = this._poolFirst;
            this._poolFirst = var3;
         }
      } else if (var1 < this._capacity) {
         for (int var6 = var1; var6 < this._capacity && this._poolFirst != null; var6++) {
            FastMap.EntryImpl var8 = this._poolFirst;
            this._poolFirst = var8._after;
            var8._after = null;
         }
      }

      byte var7 = 16;

      while (var7 < var1) {
         var7 <<= 1;
      }

      if (this._entries.length != var7) {
         this._entries = new FastMap.EntryImpl[var7];
         this._mask = var7 - 1;

         for (FastMap.EntryImpl var9 = this._mapFirst; var9 != null; var9 = var9._after) {
            int var4 = keyHash(var9._key) & this._mask;
            var9._index = var4;
            var9._previous = null;
            FastMap.EntryImpl var5 = this._entries[var4];
            var9._next = var5;
            if (var5 != null) {
               var5._previous = var9;
            }

            this._entries[var4] = var9;
         }
      }

      this._capacity = var1;
   }

   @Override
   public Object clone() {
      try {
         FastMap var1 = (FastMap)super.clone();
         var1.initialize(this._capacity);
         var1.putAll(this);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof Map) {
         Map var2 = (Map)var1;
         if (this.size() == var2.size()) {
            for (FastMap.EntryImpl var3 = this._mapFirst; var3 != null; var3 = var3._after) {
               if (!var2.entrySet().contains(var3)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (FastMap.EntryImpl var2 = this._mapFirst; var2 != null; var2 = var2._after) {
         var1 += var2.hashCode();
      }

      return var1;
   }

   @Override
   public String toString() {
      return this.entrySet().toString();
   }

   @Override
   public Collection values() {
      return this._values;
   }

   @Override
   public Set entrySet() {
      return this._entrySet;
   }

   @Override
   public Set keySet() {
      return this._keySet;
   }

   protected void sizeChanged() {
      if (this.size() > this.capacity()) {
         this.setCapacity(this.capacity() * 2);
      }
   }

   private static int keyHash(Object var0) {
      int var1 = var0.hashCode();
      var1 += ~(var1 << 9);
      var1 ^= var1 >>> 14;
      var1 += var1 << 4;
      return var1 ^ var1 >>> 10;
   }

   private void addEntry(Object var1, Object var2) {
      FastMap.EntryImpl var3 = this._poolFirst;
      if (var3 != null) {
         this._poolFirst = var3._after;
         var3._after = null;
      } else {
         var3 = new FastMap.EntryImpl();
      }

      var3._key = (K)var1;
      var3._value = (V)var2;
      int var4 = keyHash(var1) & this._mask;
      var3._index = var4;
      FastMap.EntryImpl var5 = this._entries[var4];
      var3._next = var5;
      if (var5 != null) {
         var5._previous = var3;
      }

      this._entries[var4] = var3;
      if (this._mapLast != null) {
         var3._before = this._mapLast;
         this._mapLast._after = var3;
      } else {
         this._mapFirst = var3;
      }

      this._mapLast = var3;
      this._size++;
      this.sizeChanged();
   }

   private void removeEntry(FastMap.EntryImpl var1) {
      FastMap.EntryImpl var2 = var1._previous;
      FastMap.EntryImpl var3 = var1._next;
      if (var2 != null) {
         var2._next = var3;
         var1._previous = null;
      } else {
         this._entries[var1._index] = var3;
      }

      if (var3 != null) {
         var3._previous = var2;
         var1._next = null;
      }

      FastMap.EntryImpl var4 = var1._before;
      FastMap.EntryImpl var5 = var1._after;
      if (var4 != null) {
         var4._after = var5;
         var1._before = null;
      } else {
         this._mapFirst = var5;
      }

      if (var5 != null) {
         var5._before = var4;
      } else {
         this._mapLast = var4;
      }

      var1._key = null;
      var1._value = null;
      var1._after = this._poolFirst;
      this._poolFirst = var1;
      this._size--;
      this.sizeChanged();
   }

   private void initialize(int var1) {
      byte var2 = 16;

      while (var2 < var1) {
         var2 <<= 1;
      }

      this._entries = new FastMap.EntryImpl[var2];
      this._mask = var2 - 1;
      this._capacity = var1;
      this._size = 0;
      this._values = new FastMap.Values();
      this._entrySet = new FastMap.EntrySet();
      this._keySet = new FastMap.KeySet();
      this._poolFirst = null;
      this._mapFirst = null;
      this._mapLast = null;

      for (int var3 = 0; var3 < var1; var3++) {
         FastMap.EntryImpl var4 = new FastMap.EntryImpl();
         var4._after = this._poolFirst;
         this._poolFirst = var4;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = var1.readInt();
      this.initialize(var2);
      int var3 = var1.readInt();

      for (int var4 = 0; var4 < var3; var4++) {
         Object var5 = var1.readObject();
         Object var6 = var1.readObject();
         this.addEntry(var5, var6);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeInt(this._capacity);
      var1.writeInt(this._size);
      int var2 = 0;

      for (FastMap.EntryImpl var3 = this._mapFirst; var3 != null; var3 = var3._after) {
         var1.writeObject(var3._key);
         var1.writeObject(var3._value);
         var2++;
      }

      if (var2 != this._size) {
         throw new IOException("FastMap Corrupted");
      }
   }

   private static final class EntryImpl<K, V> implements Entry<K, V> {
      private K _key;
      private V _value;
      private int _index;
      private FastMap.EntryImpl _previous;
      private FastMap.EntryImpl _next;
      private FastMap.EntryImpl _before;
      private FastMap.EntryImpl _after;

      private EntryImpl() {
      }

      @Override
      public K getKey() {
         return this._key;
      }

      @Override
      public V getValue() {
         return this._value;
      }

      @Override
      public V setValue(V var1) {
         Object var2 = this._value;
         this._value = (V)var1;
         return (V)var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return this._key.equals(var2.getKey()) && (this._value != null ? this._value.equals(var2.getValue()) : var2.getValue() == null);
         }
      }

      @Override
      public int hashCode() {
         return this._key.hashCode() ^ (this._value != null ? this._value.hashCode() : 0);
      }

      @Override
      public String toString() {
         return this._key + "=" + this._value;
      }
   }

   private class EntrySet extends AbstractSet {
      private EntrySet() {
      }

      @Override
      public Iterator iterator() {
         return new Iterator() {
            FastMap.EntryImpl after = FastMap.this._mapFirst;
            FastMap.EntryImpl before;

            @Override
            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            @Override
            public boolean hasNext() {
               return this.after != null;
            }

            @Override
            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before;
            }
         };
      }

      @Override
      public int size() {
         return FastMap.this._size;
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            Entry var3 = FastMap.this.getEntry(var2.getKey());
            return var2.equals(var3);
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            FastMap.EntryImpl var3 = (FastMap.EntryImpl)FastMap.this.getEntry(var2.getKey());
            if (var3 != null && var2.getValue().equals(var3._value)) {
               FastMap.this.removeEntry(var3);
               return true;
            }
         }

         return false;
      }
   }

   private class KeySet extends AbstractSet {
      private KeySet() {
      }

      @Override
      public Iterator iterator() {
         return new Iterator() {
            FastMap.EntryImpl after = FastMap.this._mapFirst;
            FastMap.EntryImpl before;

            @Override
            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            @Override
            public boolean hasNext() {
               return this.after != null;
            }

            @Override
            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before._key;
            }
         };
      }

      @Override
      public int size() {
         return FastMap.this._size;
      }

      @Override
      public boolean contains(Object var1) {
         return FastMap.this.containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         return FastMap.this.remove(var1) != null;
      }

      @Override
      public void clear() {
         FastMap.this.clear();
      }
   }

   private class Values extends AbstractCollection {
      private Values() {
      }

      @Override
      public Iterator iterator() {
         return new Iterator() {
            FastMap.EntryImpl after = FastMap.this._mapFirst;
            FastMap.EntryImpl before;

            @Override
            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            @Override
            public boolean hasNext() {
               return this.after != null;
            }

            @Override
            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before._value;
            }
         };
      }

      @Override
      public int size() {
         return FastMap.this._size;
      }

      @Override
      public boolean contains(Object var1) {
         return FastMap.this.containsValue(var1);
      }

      @Override
      public void clear() {
         FastMap.this.clear();
      }
   }
}
