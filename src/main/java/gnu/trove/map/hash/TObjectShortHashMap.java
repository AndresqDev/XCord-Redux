package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class TObjectShortHashMap<K> extends TObjectHash<K> implements TObjectShortMap<K>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TObjectShortProcedure<K> PUT_ALL_PROC = new TObjectShortProcedure<K>() {
      @Override
      public boolean execute(K var1, short var2) {
         TObjectShortHashMap.this.put(var1, var2);
         return true;
      }
   };
   protected transient short[] _values;
   protected short no_entry_value;

   public TObjectShortHashMap() {
      this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
   }

   public TObjectShortHashMap(int var1) {
      super(var1);
      this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
   }

   public TObjectShortHashMap(int var1, float var2) {
      super(var1, var2);
      this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
   }

   public TObjectShortHashMap(int var1, float var2, short var3) {
      super(var1, var2);
      this.no_entry_value = var3;
      if (this.no_entry_value != 0) {
         Arrays.fill(this._values, this.no_entry_value);
      }
   }

   public TObjectShortHashMap(TObjectShortMap<? extends K> var1) {
      this(var1.size(), 0.5F, var1.getNoEntryValue());
      if (var1 instanceof TObjectShortHashMap) {
         TObjectShortHashMap var2 = (TObjectShortHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.putAll(var1);
   }

   @Override
   public int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new short[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      Object[] var3 = this._set;
      short[] var4 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = new short[var1];
      Arrays.fill(this._values, this.no_entry_value);
      int var5 = var2;

      while (var5-- > 0) {
         if (var3[var5] != FREE && var3[var5] != REMOVED) {
            Object var6 = var3[var5];
            int var7 = this.insertKey((K)var6);
            if (var7 < 0) {
               this.throwObjectContractViolation(this._set[-var7 - 1], var6);
            }

            this._set[var7] = var6;
            this._values[var7] = var4[var5];
         }
      }
   }

   @Override
   public short getNoEntryValue() {
      return this.no_entry_value;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   @Override
   public boolean containsValue(short var1) {
      Object[] var2 = this._set;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && var1 == var3[var4]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public short get(Object var1) {
      int var2 = this.index(var1);
      return var2 < 0 ? this.no_entry_value : this._values[var2];
   }

   @Override
   public short put(K var1, short var2) {
      int var3 = this.insertKey((K)var1);
      return this.doPut(var2, var3);
   }

   @Override
   public short putIfAbsent(K var1, short var2) {
      int var3 = this.insertKey((K)var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var2, var3);
   }

   private short doPut(short var1, int var2) {
      short var3 = this.no_entry_value;
      boolean var4 = true;
      if (var2 < 0) {
         var2 = -var2 - 1;
         var3 = this._values[var2];
         var4 = false;
      }

      this._values[var2] = var1;
      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var3;
   }

   @Override
   public short remove(Object var1) {
      short var2 = this.no_entry_value;
      int var3 = this.index(var1);
      if (var3 >= 0) {
         var2 = this._values[var3];
         this.removeAt(var3);
      }

      return var2;
   }

   @Override
   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   @Override
   public void putAll(Map<? extends K, ? extends Short> var1) {
      for (Entry var4 : var1.entrySet()) {
         this.put((K)var4.getKey(), (Short)var4.getValue());
      }
   }

   @Override
   public void putAll(TObjectShortMap<? extends K> var1) {
      var1.forEachEntry(this.PUT_ALL_PROC);
   }

   @Override
   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, FREE);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
   }

   @Override
   public Set<K> keySet() {
      return new TObjectShortHashMap.KeyView();
   }

   @Override
   public Object[] keys() {
      Object[] var1 = new Object[this.size()];
      Object[] var2 = this._set;
      int var3 = var2.length;
      int var4 = 0;

      while (var3-- > 0) {
         if (var2[var3] != FREE && var2[var3] != REMOVED) {
            var1[var4++] = var2[var3];
         }
      }

      return var1;
   }

   @Override
   public K[] keys(K[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
      }

      Object[] var3 = this._set;
      int var4 = var3.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED) {
            var1[var5++] = var3[var4];
         }
      }

      return (K[])var1;
   }

   @Override
   public TShortCollection valueCollection() {
      return new TObjectShortHashMap.TShortValueCollection();
   }

   @Override
   public short[] values() {
      short[] var1 = new short[this.size()];
      short[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   @Override
   public short[] values(short[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new short[var2];
      }

      short[] var3 = this._values;
      Object[] var4 = this._set;
      int var5 = var3.length;
      int var6 = 0;

      while (var5-- > 0) {
         if (var4[var5] != FREE && var4[var5] != REMOVED) {
            var1[var6++] = var3[var5];
         }
      }

      if (var1.length > var2) {
         var1[var2] = this.no_entry_value;
      }

      return var1;
   }

   @Override
   public TObjectShortIterator<K> iterator() {
      return new TObjectShortHashMap.TObjectShortHashIterator<>(this);
   }

   @Override
   public boolean increment(K var1) {
      return this.adjustValue((K)var1, (short)1);
   }

   @Override
   public boolean adjustValue(K var1, short var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (short)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public short adjustOrPutValue(K var1, short var2, short var3) {
      int var4 = this.insertKey((K)var1);
      boolean var5;
      short var6;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var6 = this._values[var4] = (short)(this._values[var4] + var2);
         var5 = false;
      } else {
         var6 = this._values[var4] = var3;
         var5 = true;
      }

      if (var5) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var6;
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      Object[] var2 = this._set;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TObjectShortProcedure<? super K> var1) {
      Object[] var2 = this._set;
      short[] var3 = this._values;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && !var1.execute(var2[var4], var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean retainEntries(TObjectShortProcedure<? super K> var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      short[] var4 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var3[var5] != FREE && var3[var5] != REMOVED && !var1.execute(var3[var5], var4[var5])) {
               this.removeAt(var5);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   @Override
   public void transformValues(TShortFunction var1) {
      Object[] var2 = this._set;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != null && var2[var4] != REMOVED) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TObjectShortMap)) {
         return false;
      } else {
         TObjectShortMap var2 = (TObjectShortMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               TObjectShortIterator var3 = this.iterator();

               while (var3.hasNext()) {
                  var3.advance();
                  Object var4 = var3.key();
                  short var5 = var3.value();
                  if (var5 == this.no_entry_value) {
                     if (var2.get(var4) != var2.getNoEntryValue() || !var2.containsKey(var4)) {
                        return false;
                     }
                  } else if (var5 != var2.get(var4)) {
                     return false;
                  }
               }
            } catch (ClassCastException var6) {
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._set;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED) {
            var1 += HashFunctions.hash(var3[var4]) ^ (var2[var4] == null ? 0 : var2[var4].hashCode());
         }
      }

      return var1;
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeShort(this.no_entry_value);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while (var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
            var1.writeShort(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      this.no_entry_value = var1.readShort();
      int var2 = var1.readInt();
      this.setUp(var2);

      while (var2-- > 0) {
         Object var3 = var1.readObject();
         short var4 = var1.readShort();
         this.put((K)var3, var4);
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TObjectShortProcedure<K>() {
         private boolean first = true;

         @Override
         public boolean execute(K var1x, short var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(",");
            }

            var1.append(var1x).append("=").append((int)var2);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   protected class KeyView extends TObjectShortHashMap<K>.MapBackedView<K> {
      @Override
      public Iterator<K> iterator() {
         return new TObjectHashIterator<>(TObjectShortHashMap.this);
      }

      @Override
      public boolean removeElement(K var1) {
         return TObjectShortHashMap.this.no_entry_value != TObjectShortHashMap.this.remove(var1);
      }

      @Override
      public boolean containsElement(K var1) {
         return TObjectShortHashMap.this.contains(var1);
      }
   }

   private abstract class MapBackedView<E> extends AbstractSet<E> implements Iterable<E>, Set<E> {
      private MapBackedView() {
      }

      public abstract boolean removeElement(E var1);

      public abstract boolean containsElement(E var1);

      @Override
      public boolean contains(Object var1) {
         return this.containsElement((E)var1);
      }

      @Override
      public boolean remove(Object var1) {
         return this.removeElement((E)var1);
      }

      @Override
      public void clear() {
         TObjectShortHashMap.this.clear();
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return TObjectShortHashMap.this.size();
      }

      @Override
      public Object[] toArray() {
         Object[] var1 = new Object[this.size()];
         Iterator var2 = this.iterator();

         for (int var3 = 0; var2.hasNext(); var3++) {
            var1[var3] = var2.next();
         }

         return var1;
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         int var2 = this.size();
         if (var1.length < var2) {
            var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
         }

         Iterator var3 = this.iterator();
         Object[] var4 = var1;

         for (int var5 = 0; var5 < var2; var5++) {
            var4[var5] = var3.next();
         }

         if (var1.length > var2) {
            var1[var2] = null;
         }

         return (T[])var1;
      }

      @Override
      public boolean isEmpty() {
         return TObjectShortHashMap.this.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   class TObjectShortHashIterator<K> extends TObjectHashIterator<K> implements TObjectShortIterator<K> {
      private final TObjectShortHashMap<K> _map;

      public TObjectShortHashIterator(TObjectShortHashMap<K> var2) {
         super(var2);
         this._map = var2;
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public K key() {
         return (K)this._map._set[this._index];
      }

      @Override
      public short value() {
         return this._map._values[this._index];
      }

      @Override
      public short setValue(short var1) {
         short var2 = this.value();
         this._map._values[this._index] = var1;
         return var2;
      }
   }

   class TShortValueCollection implements TShortCollection {
      @Override
      public TShortIterator iterator() {
         return new TObjectShortHashMap.TShortValueCollection.TObjectShortValueHashIterator();
      }

      @Override
      public short getNoEntryValue() {
         return TObjectShortHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TObjectShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TObjectShortHashMap.this._size;
      }

      @Override
      public boolean contains(short var1) {
         return TObjectShortHashMap.this.containsValue(var1);
      }

      @Override
      public short[] toArray() {
         return TObjectShortHashMap.this.values();
      }

      @Override
      public short[] toArray(short[] var1) {
         return TObjectShortHashMap.this.values(var1);
      }

      @Override
      public boolean add(short var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short var1) {
         short[] var2 = TObjectShortHashMap.this._values;
         Object[] var3 = TObjectShortHashMap.this._set;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != TObjectHash.FREE && var3[var4] != TObjectHash.REMOVED && var1 == var2[var4]) {
               TObjectShortHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Short)) {
               return false;
            }

            short var4 = (Short)var3;
            if (!TObjectShortHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TShortCollection var1) {
         TShortIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TObjectShortHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(short[] var1) {
         for (short var5 : var1) {
            if (!TObjectShortHashMap.this.containsValue(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(short[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TShortIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TShortCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TShortIterator var3 = this.iterator();

            while (var3.hasNext()) {
               if (!var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean retainAll(short[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         short[] var3 = TObjectShortHashMap.this._values;
         Object[] var4 = TObjectShortHashMap.this._set;
         int var5 = var4.length;

         while (var5-- > 0) {
            if (var4[var5] != TObjectHash.FREE && var4[var5] != TObjectHash.REMOVED && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TObjectShortHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Short) {
               short var5 = (Short)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TShortCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TShortIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               short var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(short[] var1) {
         boolean var2 = false;
         int var3 = var1.length;

         while (var3-- > 0) {
            if (this.remove(var1[var3])) {
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public void clear() {
         TObjectShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TShortProcedure var1) {
         return TObjectShortHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TObjectShortHashMap.this.forEachValue(new TShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short var1x) {
               if (this.first) {
                  this.first = false;
               } else {
                  var1.append(", ");
               }

               var1.append((int)var1x);
               return true;
            }
         });
         var1.append("}");
         return var1.toString();
      }

      class TObjectShortValueHashIterator implements TShortIterator {
         protected THash _hash = TObjectShortHashMap.this;
         protected int _expectedSize = this._hash.size();
         protected int _index = this._hash.capacity();

         @Override
         public boolean hasNext() {
            return this.nextIndex() >= 0;
         }

         @Override
         public short next() {
            this.moveToNextIndex();
            return TObjectShortHashMap.this._values[this._index];
         }

         @Override
         public void remove() {
            if (this._expectedSize != this._hash.size()) {
               throw new ConcurrentModificationException();
            } else {
               try {
                  this._hash.tempDisableAutoCompaction();
                  TObjectShortHashMap.this.removeAt(this._index);
               } finally {
                  this._hash.reenableAutoCompaction(false);
               }

               this._expectedSize--;
            }
         }

         protected final void moveToNextIndex() {
            if ((this._index = this.nextIndex()) < 0) {
               throw new NoSuchElementException();
            }
         }

         protected final int nextIndex() {
            if (this._expectedSize != this._hash.size()) {
               throw new ConcurrentModificationException();
            } else {
               Object[] var1 = TObjectShortHashMap.this._set;
               int var2 = this._index;

               while (var2-- > 0 && (var1[var2] == TObjectHash.FREE || var1[var2] == TObjectHash.REMOVED)) {
               }

               return var2;
            }
         }
      }
   }
}
