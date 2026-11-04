package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
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

public class TByteByteHashMap extends TByteByteHash implements TByteByteMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient byte[] _values;

   public TByteByteHashMap() {
   }

   public TByteByteHashMap(int var1) {
      super(var1);
   }

   public TByteByteHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TByteByteHashMap(int var1, float var2, byte var3, byte var4) {
      super(var1, var2, var3, var4);
   }

   public TByteByteHashMap(byte[] var1, byte[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TByteByteHashMap(TByteByteMap var1) {
      super(var1.size());
      if (var1 instanceof TByteByteHashMap) {
         TByteByteHashMap var2 = (TByteByteHashMap)var1;
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
      this._values = new byte[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      byte[] var3 = this._set;
      byte[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new byte[var1];
      this._values = new byte[var1];
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
   public byte put(byte var1, byte var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   @Override
   public byte putIfAbsent(byte var1, byte var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private byte doPut(byte var1, byte var2, int var3) {
      byte var4 = this.no_entry_value;
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
   public void putAll(Map<? extends Byte, ? extends Byte> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Byte)var3.getKey(), (Byte)var3.getValue());
      }
   }

   @Override
   public void putAll(TByteByteMap var1) {
      this.ensureCapacity(var1.size());
      TByteByteIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public byte get(byte var1) {
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
   public byte remove(byte var1) {
      byte var2 = this.no_entry_value;
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
   public TByteSet keySet() {
      return new TByteByteHashMap.TKeyView();
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
   public TByteCollection valueCollection() {
      return new TByteByteHashMap.TValueView();
   }

   @Override
   public byte[] values() {
      byte[] var1 = new byte[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         byte[] var2 = this._values;
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
   public byte[] values(byte[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new byte[var2];
         }

         byte[] var3 = this._values;
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
   public boolean containsValue(byte var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && var1 == var3[var4]) {
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
   public TByteByteIterator iterator() {
      return new TByteByteHashMap.TByteByteHashIterator(this);
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TByteByteProcedure var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._set;
      byte[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TByteFunction var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TByteByteProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      byte[] var4 = this._set;
      byte[] var5 = this._values;
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
      return this.adjustValue(var1, (byte)1);
   }

   @Override
   public boolean adjustValue(byte var1, byte var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (byte)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public byte adjustOrPutValue(byte var1, byte var2, byte var3) {
      int var4 = this.insertKey(var1);
      boolean var5;
      byte var6;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var6 = this._values[var4] = (byte)(this._values[var4] + var2);
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
      if (!(var1 instanceof TByteByteMap)) {
         return false;
      } else {
         TByteByteMap var2 = (TByteByteMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            byte[] var3 = this._values;
            byte[] var4 = this._states;
            byte var5 = this.getNoEntryValue();
            byte var6 = var2.getNoEntryValue();
            int var7 = var3.length;

            while (var7-- > 0) {
               if (var4[var7] == 1) {
                  byte var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  byte var9 = var2.get(var8);
                  byte var10 = var3[var7];
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
      this.forEachEntry(new TByteByteProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(byte var1x, byte var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append((int)var1x);
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
            var1.writeByte(this._set[var2]);
            var1.writeByte(this._values[var2]);
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
         byte var4 = var1.readByte();
         this.put(var3, var4);
      }
   }

   class TByteByteHashIterator extends THashPrimitiveIterator implements TByteByteIterator {
      TByteByteHashIterator(TByteByteHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public byte key() {
         return TByteByteHashMap.this._set[this._index];
      }

      @Override
      public byte value() {
         return TByteByteHashMap.this._values[this._index];
      }

      @Override
      public byte setValue(byte var1) {
         byte var2 = this.value();
         TByteByteHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteByteHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TByteByteKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {
      TByteByteKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public byte next() {
         this.moveToNextIndex();
         return TByteByteHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteByteHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TByteByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
      TByteByteValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public byte next() {
         this.moveToNextIndex();
         return TByteByteHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteByteHashMap.this.removeAt(this._index);
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
         return TByteByteHashMap.this.new TByteByteKeyHashIterator(TByteByteHashMap.this);
      }

      @Override
      public byte getNoEntryValue() {
         return TByteByteHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TByteByteHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TByteByteHashMap.this._size;
      }

      @Override
      public boolean contains(byte var1) {
         return TByteByteHashMap.this.contains(var1);
      }

      @Override
      public byte[] toArray() {
         return TByteByteHashMap.this.keys();
      }

      @Override
      public byte[] toArray(byte[] var1) {
         return TByteByteHashMap.this.keys(var1);
      }

      @Override
      public boolean add(byte var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte var1) {
         return TByteByteHashMap.this.no_entry_value != TByteByteHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Byte)) {
               return false;
            }

            byte var4 = (Byte)var3;
            if (!TByteByteHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TByteCollection var1) {
         TByteIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TByteByteHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(byte[] var1) {
         for (byte var5 : var1) {
            if (!TByteByteHashMap.this.contains(var5)) {
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
         byte[] var3 = TByteByteHashMap.this._set;
         byte[] var4 = TByteByteHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TByteByteHashMap.this.removeAt(var5);
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
         TByteByteHashMap.this.clear();
      }

      @Override
      public boolean forEach(TByteProcedure var1) {
         return TByteByteHashMap.this.forEachKey(var1);
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
               int var3 = TByteByteHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TByteByteHashMap.this._states[var3] == 1 && !var2.contains(TByteByteHashMap.this._set[var3])) {
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
         int var2 = TByteByteHashMap.this._states.length;

         while (var2-- > 0) {
            if (TByteByteHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TByteByteHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TByteByteHashMap.this.forEachKey(new TByteProcedure() {
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

   protected class TValueView implements TByteCollection {
      @Override
      public TByteIterator iterator() {
         return TByteByteHashMap.this.new TByteByteValueHashIterator(TByteByteHashMap.this);
      }

      @Override
      public byte getNoEntryValue() {
         return TByteByteHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TByteByteHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TByteByteHashMap.this._size;
      }

      @Override
      public boolean contains(byte var1) {
         return TByteByteHashMap.this.containsValue(var1);
      }

      @Override
      public byte[] toArray() {
         return TByteByteHashMap.this.values();
      }

      @Override
      public byte[] toArray(byte[] var1) {
         return TByteByteHashMap.this.values(var1);
      }

      @Override
      public boolean add(byte var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte var1) {
         byte[] var2 = TByteByteHashMap.this._values;
         byte[] var3 = TByteByteHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TByteByteHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Byte)) {
               return false;
            }

            byte var4 = (Byte)var3;
            if (!TByteByteHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TByteCollection var1) {
         TByteIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TByteByteHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(byte[] var1) {
         for (byte var5 : var1) {
            if (!TByteByteHashMap.this.containsValue(var5)) {
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
         byte[] var3 = TByteByteHashMap.this._values;
         byte[] var4 = TByteByteHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TByteByteHashMap.this.removeAt(var5);
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
         TByteByteHashMap.this.clear();
      }

      @Override
      public boolean forEach(TByteProcedure var1) {
         return TByteByteHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TByteByteHashMap.this.forEachValue(new TByteProcedure() {
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
}
