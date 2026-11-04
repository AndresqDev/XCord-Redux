package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TByteLongHashMap extends TByteLongHash implements TByteLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TByteLongHashMap() {
   }

   public TByteLongHashMap(int var1) {
      super(var1);
   }

   public TByteLongHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TByteLongHashMap(int var1, float var2, byte var3, long var4) {
      super(var1, var2, var3, var4);
   }

   public TByteLongHashMap(byte[] var1, long[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TByteLongHashMap(TByteLongMap var1) {
      super(var1.size());
      if (var1 instanceof TByteLongHashMap) {
         TByteLongHashMap var2 = (TByteLongHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.putAll(var1);
   }

   @Override
   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new long[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      byte[] var3 = this._set;
      long[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new byte[var1];
      this._values = new long[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            byte var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = var4[var6];
         }
      }
   }

   @Override
   public long put(byte var1, long var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var2, var4);
   }

   @Override
   public long putIfAbsent(byte var1, long var2) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var2, var4);
   }

   private long doPut(byte var1, long var2, int var4) {
      long var5 = this.no_entry_value;
      boolean var7 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var7 = false;
      }

      this._values[var4] = var2;
      if (var7) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Long> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Byte)var3.getKey(), (Long)var3.getValue());
      }
   }

   @Override
   public void putAll(TByteLongMap var1) {
      this.ensureCapacity(var1.size());
      TByteLongIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public long get(byte var1) {
      int var2 = this.index(var1);
      return var2 < 0 ? this.no_entry_value : this._values[var2];
   }

   @Override
   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
   }

   @Override
   public boolean isEmpty() {
      return 0 == this._size;
   }

   @Override
   public long remove(byte var1) {
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
   public TByteSet keySet() {
      return new TByteLongHashMap.TKeyView();
   }

   @Override
   public byte[] keys() {
      byte[] var1 = new byte[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         byte[] var2 = this._set;
         byte[] var3 = this._states;
         int var4 = var2.length;
         int var5 = 0;

         while (var4-- > 0) {
            if (var3[var4] == 1) {
               var1[var5++] = var2[var4];
            }
         }

         return var1;
      }
   }

   @Override
   public byte[] keys(byte[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new byte[var2];
         }

         byte[] var3 = this._set;
         byte[] var4 = this._states;
         int var5 = var3.length;
         int var6 = 0;

         while (var5-- > 0) {
            if (var4[var5] == 1) {
               var1[var6++] = var3[var5];
            }
         }

         return var1;
      }
   }

   @Override
   public TLongCollection valueCollection() {
      return new TByteLongHashMap.TValueView();
   }

   @Override
   public long[] values() {
      long[] var1 = new long[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         long[] var2 = this._values;
         byte[] var3 = this._states;
         int var4 = var2.length;
         int var5 = 0;

         while (var4-- > 0) {
            if (var3[var4] == 1) {
               var1[var5++] = var2[var4];
            }
         }

         return var1;
      }
   }

   @Override
   public long[] values(long[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new long[var2];
         }

         long[] var3 = this._values;
         byte[] var4 = this._states;
         int var5 = var3.length;
         int var6 = 0;

         while (var5-- > 0) {
            if (var4[var5] == 1) {
               var1[var6++] = var3[var5];
            }
         }

         return var1;
      }
   }

   @Override
   public boolean containsValue(long var1) {
      byte[] var3 = this._states;
      long[] var4 = this._values;
      int var5 = var4.length;

      while (var5-- > 0) {
         if (var3[var5] == 1 && var1 == var4[var5]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsKey(byte var1) {
      return this.contains(var1);
   }

   @Override
   public TByteLongIterator iterator() {
      return new TByteLongHashMap.TByteLongHashIterator(this);
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      byte[] var2 = this._states;
      long[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TByteLongProcedure var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._set;
      long[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TLongFunction var1) {
      byte[] var2 = this._states;
      long[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TByteLongProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      byte[] var4 = this._set;
      long[] var5 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var6 = var4.length;

         while (var6-- > 0) {
            if (var3[var6] == 1 && !var1.execute(var4[var6], var5[var6])) {
               this.removeAt(var6);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   @Override
   public boolean increment(byte var1) {
      return this.adjustValue(var1, 1L);
   }

   @Override
   public boolean adjustValue(byte var1, long var2) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = this._values[var4] + var2;
         return true;
      }
   }

   @Override
   public long adjustOrPutValue(byte var1, long var2, long var4) {
      int var6 = this.insertKey(var1);
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

      byte var10 = this._states[var6];
      if (var7) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var8;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TByteLongMap)) {
         return false;
      } else {
         TByteLongMap var2 = (TByteLongMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            long[] var3 = this._values;
            byte[] var4 = this._states;
            long var5 = this.getNoEntryValue();
            long var7 = var2.getNoEntryValue();
            int var9 = var3.length;

            while (var9-- > 0) {
               if (var4[var9] == 1) {
                  byte var10 = this._set[var9];
                  if (!var2.containsKey(var10)) {
                     return false;
                  }

                  long var11 = var2.get(var10);
                  long var13 = var3[var9];
                  if (var13 != var11 && (var13 != var5 || var11 != var7)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      byte[] var2 = this._states;
      int var3 = this._values.length;

      while (var3-- > 0) {
         if (var2[var3] == 1) {
            var1 += HashFunctions.hash(this._set[var3]) ^ HashFunctions.hash(this._values[var3]);
         }
      }

      return var1;
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TByteLongProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(byte var1x, long var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append((int)var1x);
            var1.append("=");
            var1.append(var2);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeByte(this._set[var2]);
            var1.writeLong(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      int var2 = var1.readInt();
      this.setUp(var2);

      while (var2-- > 0) {
         byte var3 = var1.readByte();
         long var4 = var1.readLong();
         this.put(var3, var4);
      }
   }

   class TByteLongHashIterator extends THashPrimitiveIterator implements TByteLongIterator {
      TByteLongHashIterator(TByteLongHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public byte key() {
         return TByteLongHashMap.this._set[this._index];
      }

      @Override
      public long value() {
         return TByteLongHashMap.this._values[this._index];
      }

      @Override
      public long setValue(long var1) {
         long var3 = this.value();
         TByteLongHashMap.this._values[this._index] = var1;
         return var3;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TByteLongKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {
      TByteLongKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public byte next() {
         this.moveToNextIndex();
         return TByteLongHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TByteLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TByteLongValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public long next() {
         this.moveToNextIndex();
         return TByteLongHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TKeyView implements TByteSet {
      @Override
      public TByteIterator iterator() {
         return TByteLongHashMap.this.new TByteLongKeyHashIterator(TByteLongHashMap.this);
      }

      @Override
      public byte getNoEntryValue() {
         return TByteLongHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TByteLongHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TByteLongHashMap.this._size;
      }

      @Override
      public boolean contains(byte var1) {
         return TByteLongHashMap.this.contains(var1);
      }

      @Override
      public byte[] toArray() {
         return TByteLongHashMap.this.keys();
      }

      @Override
      public byte[] toArray(byte[] var1) {
         return TByteLongHashMap.this.keys(var1);
      }

      @Override
      public boolean add(byte var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte var1) {
         return TByteLongHashMap.this.no_entry_value != TByteLongHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Byte)) {
               return false;
            }

            byte var4 = (Byte)var3;
            if (!TByteLongHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TByteCollection var1) {
         TByteIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TByteLongHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(byte[] var1) {
         for (byte var5 : var1) {
            if (!TByteLongHashMap.this.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(byte[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TByteIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TByteCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TByteIterator var3 = this.iterator();

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
      public boolean retainAll(byte[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         byte[] var3 = TByteLongHashMap.this._set;
         byte[] var4 = TByteLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TByteLongHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Byte) {
               byte var5 = (Byte)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TByteCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TByteIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               byte var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(byte[] var1) {
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
         TByteLongHashMap.this.clear();
      }

      @Override
      public boolean forEach(TByteProcedure var1) {
         return TByteLongHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TByteSet)) {
            return false;
         } else {
            TByteSet var2 = (TByteSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TByteLongHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TByteLongHashMap.this._states[var3] == 1 && !var2.contains(TByteLongHashMap.this._set[var3])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      @Override
      public int hashCode() {
         int var1 = 0;
         int var2 = TByteLongHashMap.this._states.length;

         while (var2-- > 0) {
            if (TByteLongHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TByteLongHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TByteLongHashMap.this.forEachKey(new TByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte var1x) {
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
   }

   protected class TValueView implements TLongCollection {
      @Override
      public TLongIterator iterator() {
         return TByteLongHashMap.this.new TByteLongValueHashIterator(TByteLongHashMap.this);
      }

      @Override
      public long getNoEntryValue() {
         return TByteLongHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TByteLongHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TByteLongHashMap.this._size;
      }

      @Override
      public boolean contains(long var1) {
         return TByteLongHashMap.this.containsValue(var1);
      }

      @Override
      public long[] toArray() {
         return TByteLongHashMap.this.values();
      }

      @Override
      public long[] toArray(long[] var1) {
         return TByteLongHashMap.this.values(var1);
      }

      @Override
      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long var1) {
         long[] var3 = TByteLongHashMap.this._values;
         byte[] var4 = TByteLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] != 0 && var4[var5] != 2 && var1 == var3[var5]) {
               TByteLongHashMap.this.removeAt(var5);
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
            if (!TByteLongHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TLongCollection var1) {
         TLongIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TByteLongHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(long[] var1) {
         for (long var5 : var1) {
            if (!TByteLongHashMap.this.containsValue(var5)) {
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
         long[] var3 = TByteLongHashMap.this._values;
         byte[] var4 = TByteLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TByteLongHashMap.this.removeAt(var5);
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
         TByteLongHashMap.this.clear();
      }

      @Override
      public boolean forEach(TLongProcedure var1) {
         return TByteLongHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TByteLongHashMap.this.forEachValue(new TLongProcedure() {
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
   }
}
