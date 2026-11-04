package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TDoubleShortHashMap extends TDoubleShortHash implements TDoubleShortMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TDoubleShortHashMap() {
   }

   public TDoubleShortHashMap(int var1) {
      super(var1);
   }

   public TDoubleShortHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TDoubleShortHashMap(int var1, float var2, double var3, short var5) {
      super(var1, var2, var3, var5);
   }

   public TDoubleShortHashMap(double[] var1, short[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TDoubleShortHashMap(TDoubleShortMap var1) {
      super(var1.size());
      if (var1 instanceof TDoubleShortHashMap) {
         TDoubleShortHashMap var2 = (TDoubleShortHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0.0) {
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
      double[] var3 = this._set;
      short[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new double[var1];
      this._values = new short[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            double var7 = var3[var6];
            int var9 = this.insertKey(var7);
            this._values[var9] = var4[var6];
         }
      }
   }

   @Override
   public short put(double var1, short var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var3, var4);
   }

   @Override
   public short putIfAbsent(double var1, short var3) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var3, var4);
   }

   private short doPut(double var1, short var3, int var4) {
      short var5 = this.no_entry_value;
      boolean var6 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var6 = false;
      }

      this._values[var4] = var3;
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Short> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Double)var3.getKey(), (Short)var3.getValue());
      }
   }

   @Override
   public void putAll(TDoubleShortMap var1) {
      this.ensureCapacity(var1.size());
      TDoubleShortIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public short get(double var1) {
      int var3 = this.index(var1);
      return var3 < 0 ? this.no_entry_value : this._values[var3];
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
   public short remove(double var1) {
      short var3 = this.no_entry_value;
      int var4 = this.index(var1);
      if (var4 >= 0) {
         var3 = this._values[var4];
         this.removeAt(var4);
      }

      return var3;
   }

   @Override
   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   @Override
   public TDoubleSet keySet() {
      return new TDoubleShortHashMap.TKeyView();
   }

   @Override
   public double[] keys() {
      double[] var1 = new double[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         double[] var2 = this._set;
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
   public double[] keys(double[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new double[var2];
         }

         double[] var3 = this._set;
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
      return new TDoubleShortHashMap.TValueView();
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
   public boolean containsKey(double var1) {
      return this.contains(var1);
   }

   @Override
   public TDoubleShortIterator iterator() {
      return new TDoubleShortHashMap.TDoubleShortHashIterator(this);
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
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
   public boolean forEachEntry(TDoubleShortProcedure var1) {
      byte[] var2 = this._states;
      double[] var3 = this._set;
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
   public boolean retainEntries(TDoubleShortProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      double[] var4 = this._set;
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
   public boolean increment(double var1) {
      return this.adjustValue(var1, (short)1);
   }

   @Override
   public boolean adjustValue(double var1, short var3) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = (short)(this._values[var4] + var3);
         return true;
      }
   }

   @Override
   public short adjustOrPutValue(double var1, short var3, short var4) {
      int var5 = this.insertKey(var1);
      boolean var6;
      short var7;
      if (var5 < 0) {
         var5 = -var5 - 1;
         var7 = this._values[var5] = (short)(this._values[var5] + var3);
         var6 = false;
      } else {
         var7 = this._values[var5] = var4;
         var6 = true;
      }

      byte var8 = this._states[var5];
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var7;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TDoubleShortMap)) {
         return false;
      } else {
         TDoubleShortMap var2 = (TDoubleShortMap)var1;
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
                  double var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  short var10 = var2.get(var8);
                  short var11 = var3[var7];
                  if (var11 != var10 && (var11 != var5 || var10 != var6)) {
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
      this.forEachEntry(new TDoubleShortProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(double var1x, short var3) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append(var1x);
            var1.append("=");
            var1.append((int)var3);
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
            var1.writeDouble(this._set[var2]);
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
         double var3 = var1.readDouble();
         short var5 = var1.readShort();
         this.put(var3, var5);
      }
   }

   class TDoubleShortHashIterator extends THashPrimitiveIterator implements TDoubleShortIterator {
      TDoubleShortHashIterator(TDoubleShortHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public double key() {
         return TDoubleShortHashMap.this._set[this._index];
      }

      @Override
      public short value() {
         return TDoubleShortHashMap.this._values[this._index];
      }

      @Override
      public short setValue(short var1) {
         short var2 = this.value();
         TDoubleShortHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TDoubleShortKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TDoubleShortKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public double next() {
         this.moveToNextIndex();
         return TDoubleShortHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TDoubleShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TDoubleShortValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public short next() {
         this.moveToNextIndex();
         return TDoubleShortHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TKeyView implements TDoubleSet {
      @Override
      public TDoubleIterator iterator() {
         return TDoubleShortHashMap.this.new TDoubleShortKeyHashIterator(TDoubleShortHashMap.this);
      }

      @Override
      public double getNoEntryValue() {
         return TDoubleShortHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TDoubleShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TDoubleShortHashMap.this._size;
      }

      @Override
      public boolean contains(double var1) {
         return TDoubleShortHashMap.this.contains(var1);
      }

      @Override
      public double[] toArray() {
         return TDoubleShortHashMap.this.keys();
      }

      @Override
      public double[] toArray(double[] var1) {
         return TDoubleShortHashMap.this.keys(var1);
      }

      @Override
      public boolean add(double var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double var1) {
         return TDoubleShortHashMap.this.no_entry_value != TDoubleShortHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Double)) {
               return false;
            }

            double var4 = (Double)var3;
            if (!TDoubleShortHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TDoubleCollection var1) {
         TDoubleIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TDoubleShortHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(double[] var1) {
         for (double var5 : var1) {
            if (!TDoubleShortHashMap.this.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TDoubleCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(double[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TDoubleIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TDoubleCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TDoubleIterator var3 = this.iterator();

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
      public boolean retainAll(double[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         double[] var3 = TDoubleShortHashMap.this._set;
         byte[] var4 = TDoubleShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TDoubleShortHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Double) {
               double var5 = (Double)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TDoubleCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TDoubleIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               double var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(double[] var1) {
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
         TDoubleShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TDoubleProcedure var1) {
         return TDoubleShortHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TDoubleSet)) {
            return false;
         } else {
            TDoubleSet var2 = (TDoubleSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TDoubleShortHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TDoubleShortHashMap.this._states[var3] == 1 && !var2.contains(TDoubleShortHashMap.this._set[var3])) {
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
         int var2 = TDoubleShortHashMap.this._states.length;

         while (var2-- > 0) {
            if (TDoubleShortHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TDoubleShortHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TDoubleShortHashMap.this.forEachKey(new TDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double var1x) {
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
         return TDoubleShortHashMap.this.new TDoubleShortValueHashIterator(TDoubleShortHashMap.this);
      }

      @Override
      public short getNoEntryValue() {
         return TDoubleShortHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TDoubleShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TDoubleShortHashMap.this._size;
      }

      @Override
      public boolean contains(short var1) {
         return TDoubleShortHashMap.this.containsValue(var1);
      }

      @Override
      public short[] toArray() {
         return TDoubleShortHashMap.this.values();
      }

      @Override
      public short[] toArray(short[] var1) {
         return TDoubleShortHashMap.this.values(var1);
      }

      @Override
      public boolean add(short var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short var1) {
         short[] var2 = TDoubleShortHashMap.this._values;
         byte[] var3 = TDoubleShortHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TDoubleShortHashMap.this.removeAt(var4);
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
            if (!TDoubleShortHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TShortCollection var1) {
         TShortIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TDoubleShortHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(short[] var1) {
         for (short var5 : var1) {
            if (!TDoubleShortHashMap.this.containsValue(var5)) {
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
         short[] var3 = TDoubleShortHashMap.this._values;
         byte[] var4 = TDoubleShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TDoubleShortHashMap.this.removeAt(var5);
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
         TDoubleShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TShortProcedure var1) {
         return TDoubleShortHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TDoubleShortHashMap.this.forEachValue(new TShortProcedure() {
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
