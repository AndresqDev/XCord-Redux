package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleIntHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
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

public class TDoubleIntHashMap extends TDoubleIntHash implements TDoubleIntMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient int[] _values;

   public TDoubleIntHashMap() {
   }

   public TDoubleIntHashMap(int var1) {
      super(var1);
   }

   public TDoubleIntHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TDoubleIntHashMap(int var1, float var2, double var3, int var5) {
      super(var1, var2, var3, var5);
   }

   public TDoubleIntHashMap(double[] var1, int[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TDoubleIntHashMap(TDoubleIntMap var1) {
      super(var1.size());
      if (var1 instanceof TDoubleIntHashMap) {
         TDoubleIntHashMap var2 = (TDoubleIntHashMap)var1;
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
      this._values = new int[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      double[] var3 = this._set;
      int[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new double[var1];
      this._values = new int[var1];
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
   public int put(double var1, int var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var3, var4);
   }

   @Override
   public int putIfAbsent(double var1, int var3) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var3, var4);
   }

   private int doPut(double var1, int var3, int var4) {
      int var5 = this.no_entry_value;
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
   public void putAll(Map<? extends Double, ? extends Integer> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Double)var3.getKey(), (Integer)var3.getValue());
      }
   }

   @Override
   public void putAll(TDoubleIntMap var1) {
      this.ensureCapacity(var1.size());
      TDoubleIntIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public int get(double var1) {
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
   public int remove(double var1) {
      int var3 = this.no_entry_value;
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
      return new TDoubleIntHashMap.TKeyView();
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
   public TIntCollection valueCollection() {
      return new TDoubleIntHashMap.TValueView();
   }

   @Override
   public int[] values() {
      int[] var1 = new int[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         int[] var2 = this._values;
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
   public int[] values(int[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new int[var2];
         }

         int[] var3 = this._values;
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
   public boolean containsValue(int var1) {
      byte[] var2 = this._states;
      int[] var3 = this._values;
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
   public TDoubleIntIterator iterator() {
      return new TDoubleIntHashMap.TDoubleIntHashIterator(this);
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TIntProcedure var1) {
      byte[] var2 = this._states;
      int[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TDoubleIntProcedure var1) {
      byte[] var2 = this._states;
      double[] var3 = this._set;
      int[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TIntFunction var1) {
      byte[] var2 = this._states;
      int[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TDoubleIntProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      double[] var4 = this._set;
      int[] var5 = this._values;
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
      return this.adjustValue(var1, 1);
   }

   @Override
   public boolean adjustValue(double var1, int var3) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = this._values[var4] + var3;
         return true;
      }
   }

   @Override
   public int adjustOrPutValue(double var1, int var3, int var4) {
      int var5 = this.insertKey(var1);
      boolean var6;
      int var7;
      if (var5 < 0) {
         var5 = -var5 - 1;
         var7 = this._values[var5] = this._values[var5] + var3;
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
      if (!(var1 instanceof TDoubleIntMap)) {
         return false;
      } else {
         TDoubleIntMap var2 = (TDoubleIntMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            int[] var3 = this._values;
            byte[] var4 = this._states;
            int var5 = this.getNoEntryValue();
            int var6 = var2.getNoEntryValue();
            int var7 = var3.length;

            while (var7-- > 0) {
               if (var4[var7] == 1) {
                  double var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  int var10 = var2.get(var8);
                  int var11 = var3[var7];
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
      this.forEachEntry(new TDoubleIntProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(double var1x, int var3) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append(var1x);
            var1.append("=");
            var1.append(var3);
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
            var1.writeInt(this._values[var2]);
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
         int var5 = var1.readInt();
         this.put(var3, var5);
      }
   }

   class TDoubleIntHashIterator extends THashPrimitiveIterator implements TDoubleIntIterator {
      TDoubleIntHashIterator(TDoubleIntHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public double key() {
         return TDoubleIntHashMap.this._set[this._index];
      }

      @Override
      public int value() {
         return TDoubleIntHashMap.this._values[this._index];
      }

      @Override
      public int setValue(int var1) {
         int var2 = this.value();
         TDoubleIntHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleIntHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TDoubleIntKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TDoubleIntKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public double next() {
         this.moveToNextIndex();
         return TDoubleIntHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleIntHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TDoubleIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {
      TDoubleIntValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public int next() {
         this.moveToNextIndex();
         return TDoubleIntHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleIntHashMap.this.removeAt(this._index);
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
         return TDoubleIntHashMap.this.new TDoubleIntKeyHashIterator(TDoubleIntHashMap.this);
      }

      @Override
      public double getNoEntryValue() {
         return TDoubleIntHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TDoubleIntHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TDoubleIntHashMap.this._size;
      }

      @Override
      public boolean contains(double var1) {
         return TDoubleIntHashMap.this.contains(var1);
      }

      @Override
      public double[] toArray() {
         return TDoubleIntHashMap.this.keys();
      }

      @Override
      public double[] toArray(double[] var1) {
         return TDoubleIntHashMap.this.keys(var1);
      }

      @Override
      public boolean add(double var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double var1) {
         return TDoubleIntHashMap.this.no_entry_value != TDoubleIntHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Double)) {
               return false;
            }

            double var4 = (Double)var3;
            if (!TDoubleIntHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TDoubleCollection var1) {
         TDoubleIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TDoubleIntHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(double[] var1) {
         for (double var5 : var1) {
            if (!TDoubleIntHashMap.this.contains(var5)) {
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
         double[] var3 = TDoubleIntHashMap.this._set;
         byte[] var4 = TDoubleIntHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TDoubleIntHashMap.this.removeAt(var5);
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
         TDoubleIntHashMap.this.clear();
      }

      @Override
      public boolean forEach(TDoubleProcedure var1) {
         return TDoubleIntHashMap.this.forEachKey(var1);
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
               int var3 = TDoubleIntHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TDoubleIntHashMap.this._states[var3] == 1 && !var2.contains(TDoubleIntHashMap.this._set[var3])) {
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
         int var2 = TDoubleIntHashMap.this._states.length;

         while (var2-- > 0) {
            if (TDoubleIntHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TDoubleIntHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TDoubleIntHashMap.this.forEachKey(new TDoubleProcedure() {
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

   protected class TValueView implements TIntCollection {
      @Override
      public TIntIterator iterator() {
         return TDoubleIntHashMap.this.new TDoubleIntValueHashIterator(TDoubleIntHashMap.this);
      }

      @Override
      public int getNoEntryValue() {
         return TDoubleIntHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TDoubleIntHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TDoubleIntHashMap.this._size;
      }

      @Override
      public boolean contains(int var1) {
         return TDoubleIntHashMap.this.containsValue(var1);
      }

      @Override
      public int[] toArray() {
         return TDoubleIntHashMap.this.values();
      }

      @Override
      public int[] toArray(int[] var1) {
         return TDoubleIntHashMap.this.values(var1);
      }

      @Override
      public boolean add(int var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int var1) {
         int[] var2 = TDoubleIntHashMap.this._values;
         byte[] var3 = TDoubleIntHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TDoubleIntHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Integer)) {
               return false;
            }

            int var4 = (Integer)var3;
            if (!TDoubleIntHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TIntCollection var1) {
         TIntIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TDoubleIntHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(int[] var1) {
         for (int var5 : var1) {
            if (!TDoubleIntHashMap.this.containsValue(var5)) {
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
         int[] var3 = TDoubleIntHashMap.this._values;
         byte[] var4 = TDoubleIntHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TDoubleIntHashMap.this.removeAt(var5);
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
         TDoubleIntHashMap.this.clear();
      }

      @Override
      public boolean forEach(TIntProcedure var1) {
         return TDoubleIntHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TDoubleIntHashMap.this.forEachValue(new TIntProcedure() {
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
}
