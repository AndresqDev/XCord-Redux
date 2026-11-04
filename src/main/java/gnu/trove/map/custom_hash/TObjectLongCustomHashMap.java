package gnu.trove.map.custom_hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
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

public class TObjectLongCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectLongMap<K>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TObjectLongProcedure<K> PUT_ALL_PROC = new TObjectLongProcedure<K>() {
      @Override
      public boolean execute(K var1, long var2) {
         TObjectLongCustomHashMap.this.put(var1, var2);
         return true;
      }
   };
   protected transient long[] _values;
   protected long no_entry_value;

   public TObjectLongCustomHashMap() {
   }

   public TObjectLongCustomHashMap(HashingStrategy<? super K> var1) {
      super(var1);
      this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
   }

   public TObjectLongCustomHashMap(HashingStrategy<? super K> var1, int var2) {
      super(var1, var2);
      this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
   }

   public TObjectLongCustomHashMap(HashingStrategy<? super K> var1, int var2, float var3) {
      super(var1, var2, var3);
      this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
   }

   public TObjectLongCustomHashMap(HashingStrategy<? super K> var1, int var2, float var3, long var4) {
      super(var1, var2, var3);
      this.no_entry_value = var4;
      if (this.no_entry_value != 0L) {
         Arrays.fill(this._values, this.no_entry_value);
      }
   }

   public TObjectLongCustomHashMap(HashingStrategy<? super K> var1, TObjectLongMap<? extends K> var2) {
      this(var1, var2.size(), 0.5F, var2.getNoEntryValue());
      if (var2 instanceof TObjectLongCustomHashMap) {
         TObjectLongCustomHashMap var3 = (TObjectLongCustomHashMap)var2;
         this._loadFactor = Math.abs(var3._loadFactor);
         this.no_entry_value = var3.no_entry_value;
         this.strategy = var3.strategy;
         if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.putAll(var2);
   }

   @Override
   public int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new long[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      Object[] var3 = this._set;
      long[] var4 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = new long[var1];
      Arrays.fill(this._values, this.no_entry_value);
      int var5 = var2;

      while (var5-- > 0) {
         Object var6 = var3[var5];
         if (var6 != FREE && var6 != REMOVED) {
            int var7 = this.insertKey((K)var6);
            if (var7 < 0) {
               this.throwObjectContractViolation(this._set[-var7 - 1], var6);
            }

            this._values[var7] = var4[var5];
         }
      }
   }

   @Override
   public long getNoEntryValue() {
      return this.no_entry_value;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   @Override
   public boolean containsValue(long var1) {
      Object[] var3 = this._set;
      long[] var4 = this._values;
      int var5 = var4.length;

      while (var5-- > 0) {
         if (var3[var5] != FREE && var3[var5] != REMOVED && var1 == var4[var5]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public long get(Object var1) {
      int var2 = this.index(var1);
      return var2 < 0 ? this.no_entry_value : this._values[var2];
   }

   @Override
   public long put(K var1, long var2) {
      int var4 = this.insertKey((K)var1);
      return this.doPut(var2, var4);
   }

   @Override
   public long putIfAbsent(K var1, long var2) {
      int var4 = this.insertKey((K)var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var2, var4);
   }

   private long doPut(long var1, int var3) {
      long var4 = this.no_entry_value;
      boolean var6 = true;
      if (var3 < 0) {
         var3 = -var3 - 1;
         var4 = this._values[var3];
         var6 = false;
      }

      this._values[var3] = var1;
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var4;
   }

   @Override
   public long remove(Object var1) {
      long var2 = this.no_entry_value;
      int var4 = this.index(var1);
      if (var4 >= 0) {
         var2 = this._values[var4];
         this.removeAt(var4);
      }

      return var2;
   }

   @Override
   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   @Override
   public void putAll(Map<? extends K, ? extends Long> var1) {
      for (Entry var4 : var1.entrySet()) {
         this.put((K)var4.getKey(), (Long)var4.getValue());
      }
   }

   @Override
   public void putAll(TObjectLongMap<? extends K> var1) {
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
      return new TObjectLongCustomHashMap.KeyView();
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
   public TLongCollection valueCollection() {
      return new TObjectLongCustomHashMap.TLongValueCollection();
   }

   @Override
   public long[] values() {
      long[] var1 = new long[this.size()];
      long[] var2 = this._values;
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
   public long[] values(long[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new long[var2];
      }

      long[] var3 = this._values;
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
   public TObjectLongIterator<K> iterator() {
      return new TObjectLongCustomHashMap.TObjectLongHashIterator<>(this);
   }

   @Override
   public boolean increment(K var1) {
      return this.adjustValue((K)var1, 1L);
   }

   @Override
   public boolean adjustValue(K var1, long var2) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = this._values[var4] + var2;
         return true;
      }
   }

   @Override
   public long adjustOrPutValue(K var1, long var2, long var4) {
      int var6 = this.insertKey((K)var1);
      boolean var7;
      long var8;
      if (var6 < 0) {
         var6 = -var6 - 1;
         var8 = this._values[var6] = this._values[var6] + var2;
         var7 = false;
      } else {
         var8 = this._values[var6] = var4;
         var7 = true;
      }

      if (var7) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var8;
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      Object[] var2 = this._set;
      long[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TObjectLongProcedure<? super K> var1) {
      Object[] var2 = this._set;
      long[] var3 = this._values;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && !var1.execute(var2[var4], var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean retainEntries(TObjectLongProcedure<? super K> var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      long[] var4 = this._values;
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
   public void transformValues(TLongFunction var1) {
      Object[] var2 = this._set;
      long[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] != null && var2[var4] != REMOVED) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TObjectLongMap)) {
         return false;
      } else {
         TObjectLongMap var2 = (TObjectLongMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               TObjectLongIterator var3 = this.iterator();

               while (var3.hasNext()) {
                  var3.advance();
                  Object var4 = var3.key();
                  long var5 = var3.value();
                  if (var5 == this.no_entry_value) {
                     if (var2.get(var4) != var2.getNoEntryValue() || !var2.containsKey(var4)) {
                        return false;
                     }
                  } else if (var5 != var2.get(var4)) {
                     return false;
                  }
               }
            } catch (ClassCastException var7) {
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._set;
      long[] var3 = this._values;
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
      var1.writeObject(this.strategy);
      var1.writeLong(this.no_entry_value);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while (var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
            var1.writeLong(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      this.strategy = (HashingStrategy<? super K>)var1.readObject();
      this.no_entry_value = var1.readLong();
      int var2 = var1.readInt();
      this.setUp(var2);

      while (var2-- > 0) {
         Object var3 = var1.readObject();
         long var4 = var1.readLong();
         this.put((K)var3, var4);
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TObjectLongProcedure<K>() {
         private boolean first = true;

         @Override
         public boolean execute(K var1x, long var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(",");
            }

            var1.append(var1x).append("=").append(var2);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   protected class KeyView extends TObjectLongCustomHashMap<K>.MapBackedView<K> {
      @Override
      public Iterator<K> iterator() {
         return new TObjectHashIterator<>(TObjectLongCustomHashMap.this);
      }

      @Override
      public boolean removeElement(K var1) {
         return TObjectLongCustomHashMap.this.no_entry_value != TObjectLongCustomHashMap.this.remove(var1);
      }

      @Override
      public boolean containsElement(K var1) {
         return TObjectLongCustomHashMap.this.contains(var1);
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
         TObjectLongCustomHashMap.this.clear();
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return TObjectLongCustomHashMap.this.size();
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
         return TObjectLongCustomHashMap.this.isEmpty();
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

   class TLongValueCollection implements TLongCollection {
      @Override
      public TLongIterator iterator() {
         return new TObjectLongCustomHashMap.TLongValueCollection.TObjectLongValueHashIterator();
      }

      @Override
      public long getNoEntryValue() {
         return TObjectLongCustomHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TObjectLongCustomHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TObjectLongCustomHashMap.this._size;
      }

      @Override
      public boolean contains(long var1) {
         return TObjectLongCustomHashMap.this.containsValue(var1);
      }

      @Override
      public long[] toArray() {
         return TObjectLongCustomHashMap.this.values();
      }

      @Override
      public long[] toArray(long[] var1) {
         return TObjectLongCustomHashMap.this.values(var1);
      }

      @Override
      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long var1) {
         long[] var3 = TObjectLongCustomHashMap.this._values;
         Object[] var4 = TObjectLongCustomHashMap.this._set;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] != TObjectHash.FREE && var4[var5] != TObjectHash.REMOVED && var1 == var3[var5]) {
               TObjectLongCustomHashMap.this.removeAt(var5);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Long)) {
               return false;
            }

            long var4 = (Long)var3;
            if (!TObjectLongCustomHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TLongCollection var1) {
         TLongIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TObjectLongCustomHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(long[] var1) {
         for (long var5 : var1) {
            if (!TObjectLongCustomHashMap.this.containsValue(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TLongCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(long[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TLongIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TLongCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TLongIterator var3 = this.iterator();

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
      public boolean retainAll(long[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         long[] var3 = TObjectLongCustomHashMap.this._values;
         Object[] var4 = TObjectLongCustomHashMap.this._set;
         int var5 = var4.length;

         while (var5-- > 0) {
            if (var4[var5] != TObjectHash.FREE && var4[var5] != TObjectHash.REMOVED && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TObjectLongCustomHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Long) {
               long var5 = (Long)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TLongCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TLongIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               long var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(long[] var1) {
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
         TObjectLongCustomHashMap.this.clear();
      }

      @Override
      public boolean forEach(TLongProcedure var1) {
         return TObjectLongCustomHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TObjectLongCustomHashMap.this.forEachValue(new TLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(long var1x) {
               if (this.first) {
                  this.first = false;
               } else {
                  var1.append(", ");
               }

               var1.append(var1x);
               return true;
            }
         });
         var1.append("}");
         return var1.toString();
      }

      class TObjectLongValueHashIterator implements TLongIterator {
         protected THash _hash = TObjectLongCustomHashMap.this;
         protected int _expectedSize = this._hash.size();
         protected int _index = this._hash.capacity();

         @Override
         public boolean hasNext() {
            return this.nextIndex() >= 0;
         }

         @Override
         public long next() {
            this.moveToNextIndex();
            return TObjectLongCustomHashMap.this._values[this._index];
         }

         @Override
         public void remove() {
            if (this._expectedSize != this._hash.size()) {
               throw new ConcurrentModificationException();
            } else {
               try {
                  this._hash.tempDisableAutoCompaction();
                  TObjectLongCustomHashMap.this.removeAt(this._index);
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
               Object[] var1 = TObjectLongCustomHashMap.this._set;
               int var2 = this._index;

               while (var2-- > 0 && (var1[var2] == TCustomObjectHash.FREE || var1[var2] == TCustomObjectHash.REMOVED)) {
               }

               return var2;
            }
         }
      }
   }

   class TObjectLongHashIterator<K> extends TObjectHashIterator<K> implements TObjectLongIterator<K> {
      private final TObjectLongCustomHashMap<K> _map;

      public TObjectLongHashIterator(TObjectLongCustomHashMap<K> var2) {
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
      public long value() {
         return this._map._values[this._index];
      }

      @Override
      public long setValue(long var1) {
         long var3 = this.value();
         this._map._values[this._index] = var1;
         return var3;
      }
   }
}
