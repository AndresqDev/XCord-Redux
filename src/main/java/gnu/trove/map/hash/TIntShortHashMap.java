package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntShortHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TIntShortHashMap extends TIntShortHash implements TIntShortMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TIntShortHashMap() {
   }

   public TIntShortHashMap(int var1) {
      super(var1);
   }

   public TIntShortHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TIntShortHashMap(int var1, float var2, int var3, short var4) {
      super(var1, var2, var3, var4);
   }

   public TIntShortHashMap(int[] var1, short[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TIntShortHashMap(TIntShortMap var1) {
      super(var1.size());
      if (var1 instanceof TIntShortHashMap) {
         TIntShortHashMap var2 = (TIntShortHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.putAll(var1);
   }

   @Override
   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new short[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      int[] var3 = this._set;
      short[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new int[var1];
      this._values = new short[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            int var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = var4[var6];
         }
      }
   }

   @Override
   public short put(int var1, short var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   @Override
   public short putIfAbsent(int var1, short var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private short doPut(int var1, short var2, int var3) {
      short var4 = this.no_entry_value;
      boolean var5 = true;
      if (var3 < 0) {
         var3 = -var3 - 1;
         var4 = this._values[var3];
         var5 = false;
      }

      this._values[var3] = var2;
      if (var5) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var4;
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Short> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Integer)var3.getKey(), (Short)var3.getValue());
      }
   }

   @Override
   public void putAll(TIntShortMap var1) {
      this.ensureCapacity(var1.size());
      TIntShortIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public short get(int var1) {
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
   public short remove(int var1) {
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
   public TIntSet keySet() {
      return new TIntShortHashMap.TKeyView();
   }

   @Override
   public int[] keys() {
      int[] var1 = new int[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         int[] var2 = this._set;
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
   public int[] keys(int[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new int[var2];
         }

         int[] var3 = this._set;
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
   public TShortCollection valueCollection() {
      return new TIntShortHashMap.TValueView();
   }

   @Override
   public short[] values() {
      short[] var1 = new short[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         short[] var2 = this._values;
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
   public short[] values(short[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new short[var2];
         }

         short[] var3 = this._values;
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
   public boolean containsValue(short var1) {
      byte[] var2 = this._states;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && var1 == var3[var4]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsKey(int var1) {
      return this.contains(var1);
   }

   @Override
   public TIntShortIterator iterator() {
      return new TIntShortHashMap.TIntShortHashIterator(this);
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      byte[] var2 = this._states;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TIntShortProcedure var1) {
      byte[] var2 = this._states;
      int[] var3 = this._set;
      short[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TShortFunction var1) {
      byte[] var2 = this._states;
      short[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TIntShortProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      int[] var4 = this._set;
      short[] var5 = this._values;
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
   public boolean increment(int var1) {
      return this.adjustValue(var1, (short)1);
   }

   @Override
   public boolean adjustValue(int var1, short var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (short)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public short adjustOrPutValue(int var1, short var2, short var3) {
      int var4 = this.insertKey(var1);
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

      byte var7 = this._states[var4];
      if (var5) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var6;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TIntShortMap)) {
         return false;
      } else {
         TIntShortMap var2 = (TIntShortMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            short[] var3 = this._values;
            byte[] var4 = this._states;
            short var5 = this.getNoEntryValue();
            short var6 = var2.getNoEntryValue();
            int var7 = var3.length;

            while (var7-- > 0) {
               if (var4[var7] == 1) {
                  int var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  short var9 = var2.get(var8);
                  short var10 = var3[var7];
                  if (var10 != var9 && (var10 != var5 || var9 != var6)) {
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
      this.forEachEntry(new TIntShortProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(int var1x, short var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append(var1x);
            var1.append("=");
            var1.append((int)var2);
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
            var1.writeInt(this._set[var2]);
            var1.writeShort(this._values[var2]);
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
         int var3 = var1.readInt();
         short var4 = var1.readShort();
         this.put(var3, var4);
      }
   }

   class TIntShortHashIterator extends THashPrimitiveIterator implements TIntShortIterator {
      TIntShortHashIterator(TIntShortHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public int key() {
         return TIntShortHashMap.this._set[this._index];
      }

      @Override
      public short value() {
         return TIntShortHashMap.this._values[this._index];
      }

      @Override
      public short setValue(short var1) {
         short var2 = this.value();
         TIntShortHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TIntShortKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {
      TIntShortKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public int next() {
         this.moveToNextIndex();
         return TIntShortHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TIntShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TIntShortValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public short next() {
         this.moveToNextIndex();
         return TIntShortHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TKeyView implements TIntSet {
      @Override
      public TIntIterator iterator() {
         return TIntShortHashMap.this.new TIntShortKeyHashIterator(TIntShortHashMap.this);
      }

      @Override
      public int getNoEntryValue() {
         return TIntShortHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TIntShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TIntShortHashMap.this._size;
      }

      @Override
      public boolean contains(int var1) {
         return TIntShortHashMap.this.contains(var1);
      }

      @Override
      public int[] toArray() {
         return TIntShortHashMap.this.keys();
      }

      @Override
      public int[] toArray(int[] var1) {
         return TIntShortHashMap.this.keys(var1);
      }

      @Override
      public boolean add(int var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int var1) {
         return TIntShortHashMap.this.no_entry_value != TIntShortHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Integer)) {
               return false;
            }

            int var4 = (Integer)var3;
            if (!TIntShortHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TIntCollection var1) {
         TIntIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TIntShortHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(int[] var1) {
         for (int var5 : var1) {
            if (!TIntShortHashMap.this.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TIntCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TIntIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TIntCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TIntIterator var3 = this.iterator();

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
      public boolean retainAll(int[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         int[] var3 = TIntShortHashMap.this._set;
         byte[] var4 = TIntShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TIntShortHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Integer) {
               int var5 = (Integer)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TIntCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TIntIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               int var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(int[] var1) {
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
         TIntShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TIntProcedure var1) {
         return TIntShortHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TIntSet)) {
            return false;
         } else {
            TIntSet var2 = (TIntSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TIntShortHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TIntShortHashMap.this._states[var3] == 1 && !var2.contains(TIntShortHashMap.this._set[var3])) {
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
         int var2 = TIntShortHashMap.this._states.length;

         while (var2-- > 0) {
            if (TIntShortHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TIntShortHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TIntShortHashMap.this.forEachKey(new TIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int var1x) {
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

   protected class TValueView implements TShortCollection {
      @Override
      public TShortIterator iterator() {
         return TIntShortHashMap.this.new TIntShortValueHashIterator(TIntShortHashMap.this);
      }

      @Override
      public short getNoEntryValue() {
         return TIntShortHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TIntShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TIntShortHashMap.this._size;
      }

      @Override
      public boolean contains(short var1) {
         return TIntShortHashMap.this.containsValue(var1);
      }

      @Override
      public short[] toArray() {
         return TIntShortHashMap.this.values();
      }

      @Override
      public short[] toArray(short[] var1) {
         return TIntShortHashMap.this.values(var1);
      }

      @Override
      public boolean add(short var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short var1) {
         short[] var2 = TIntShortHashMap.this._values;
         byte[] var3 = TIntShortHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TIntShortHashMap.this.removeAt(var4);
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
            if (!TIntShortHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TShortCollection var1) {
         TShortIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TIntShortHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(short[] var1) {
         for (short var5 : var1) {
            if (!TIntShortHashMap.this.containsValue(var5)) {
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
         short[] var3 = TIntShortHashMap.this._values;
         byte[] var4 = TIntShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TIntShortHashMap.this.removeAt(var5);
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
         TIntShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TShortProcedure var1) {
         return TIntShortHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TIntShortHashMap.this.forEachValue(new TShortProcedure() {
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
   }
}
