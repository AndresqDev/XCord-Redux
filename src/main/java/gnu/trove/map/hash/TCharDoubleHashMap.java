package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TCharDoubleHashMap extends TCharDoubleHash implements TCharDoubleMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient double[] _values;

   public TCharDoubleHashMap() {
   }

   public TCharDoubleHashMap(int var1) {
      super(var1);
   }

   public TCharDoubleHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TCharDoubleHashMap(int var1, float var2, char var3, double var4) {
      super(var1, var2, var3, var4);
   }

   public TCharDoubleHashMap(char[] var1, double[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TCharDoubleHashMap(TCharDoubleMap var1) {
      super(var1.size());
      if (var1 instanceof TCharDoubleHashMap) {
         TCharDoubleHashMap var2 = (TCharDoubleHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0.0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil(10.0 / (double)this._loadFactor)));
      }

      this.putAll(var1);
   }

   @Override
   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new double[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      char[] var3 = this._set;
      double[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new char[var1];
      this._values = new double[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            char var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = var4[var6];
         }
      }
   }

   @Override
   public double put(char var1, double var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var2, var4);
   }

   @Override
   public double putIfAbsent(char var1, double var2) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var2, var4);
   }

   private double doPut(char var1, double var2, int var4) {
      double var5 = this.no_entry_value;
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
   public void putAll(Map<? extends Character, ? extends Double> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Character)var3.getKey(), (Double)var3.getValue());
      }
   }

   @Override
   public void putAll(TCharDoubleMap var1) {
      this.ensureCapacity(var1.size());
      TCharDoubleIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public double get(char var1) {
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
   public double remove(char var1) {
      double var2 = this.no_entry_value;
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
   public TCharSet keySet() {
      return new TCharDoubleHashMap.TKeyView();
   }

   @Override
   public char[] keys() {
      char[] var1 = new char[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         char[] var2 = this._set;
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
   public char[] keys(char[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new char[var2];
         }

         char[] var3 = this._set;
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
   public TDoubleCollection valueCollection() {
      return new TCharDoubleHashMap.TValueView();
   }

   @Override
   public double[] values() {
      double[] var1 = new double[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         double[] var2 = this._values;
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
   public double[] values(double[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new double[var2];
         }

         double[] var3 = this._values;
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
   public boolean containsValue(double var1) {
      byte[] var3 = this._states;
      double[] var4 = this._values;
      int var5 = var4.length;

      while (var5-- > 0) {
         if (var3[var5] == 1 && var1 == var4[var5]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsKey(char var1) {
      return this.contains(var1);
   }

   @Override
   public TCharDoubleIterator iterator() {
      return new TCharDoubleHashMap.TCharDoubleHashIterator(this);
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TDoubleProcedure var1) {
      byte[] var2 = this._states;
      double[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TCharDoubleProcedure var1) {
      byte[] var2 = this._states;
      char[] var3 = this._set;
      double[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
      byte[] var2 = this._states;
      double[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TCharDoubleProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      char[] var4 = this._set;
      double[] var5 = this._values;
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
   public boolean increment(char var1) {
      return this.adjustValue(var1, 1.0);
   }

   @Override
   public boolean adjustValue(char var1, double var2) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = this._values[var4] + var2;
         return true;
      }
   }

   @Override
   public double adjustOrPutValue(char var1, double var2, double var4) {
      int var6 = this.insertKey(var1);
      boolean var7;
      double var8;
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
      if (!(var1 instanceof TCharDoubleMap)) {
         return false;
      } else {
         TCharDoubleMap var2 = (TCharDoubleMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            double[] var3 = this._values;
            byte[] var4 = this._states;
            double var5 = this.getNoEntryValue();
            double var7 = var2.getNoEntryValue();
            int var9 = var3.length;

            while (var9-- > 0) {
               if (var4[var9] == 1) {
                  char var10 = this._set[var9];
                  if (!var2.containsKey(var10)) {
                     return false;
                  }

                  double var11 = var2.get(var10);
                  double var13 = var3[var9];
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
      this.forEachEntry(new TCharDoubleProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(char var1x, double var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
            }

            var1.append(var1x);
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
            var1.writeChar(this._set[var2]);
            var1.writeDouble(this._values[var2]);
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
         char var3 = var1.readChar();
         double var4 = var1.readDouble();
         this.put(var3, var4);
      }
   }

   class TCharDoubleHashIterator extends THashPrimitiveIterator implements TCharDoubleIterator {
      TCharDoubleHashIterator(TCharDoubleHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public char key() {
         return TCharDoubleHashMap.this._set[this._index];
      }

      @Override
      public double value() {
         return TCharDoubleHashMap.this._values[this._index];
      }

      @Override
      public double setValue(double var1) {
         double var3 = this.value();
         TCharDoubleHashMap.this._values[this._index] = var1;
         return var3;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharDoubleKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TCharDoubleKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TCharDoubleHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TCharDoubleValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public double next() {
         this.moveToNextIndex();
         return TCharDoubleHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TKeyView implements TCharSet {
      @Override
      public TCharIterator iterator() {
         return TCharDoubleHashMap.this.new TCharDoubleKeyHashIterator(TCharDoubleHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TCharDoubleHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TCharDoubleHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharDoubleHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TCharDoubleHashMap.this.contains(var1);
      }

      @Override
      public char[] toArray() {
         return TCharDoubleHashMap.this.keys();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TCharDoubleHashMap.this.keys(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         return TCharDoubleHashMap.this.no_entry_value != TCharDoubleHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Character)) {
               return false;
            }

            char var4 = (Character)var3;
            if (!TCharDoubleHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharDoubleHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TCharDoubleHashMap.this.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TCharCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(char[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TCharIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TCharCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TCharIterator var3 = this.iterator();

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
      public boolean retainAll(char[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         char[] var3 = TCharDoubleHashMap.this._set;
         byte[] var4 = TCharDoubleHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharDoubleHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Character) {
               char var5 = (Character)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TCharCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TCharIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               char var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(char[] var1) {
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
         TCharDoubleHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TCharDoubleHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TCharSet)) {
            return false;
         } else {
            TCharSet var2 = (TCharSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TCharDoubleHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TCharDoubleHashMap.this._states[var3] == 1 && !var2.contains(TCharDoubleHashMap.this._set[var3])) {
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
         int var2 = TCharDoubleHashMap.this._states.length;

         while (var2-- > 0) {
            if (TCharDoubleHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TCharDoubleHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharDoubleHashMap.this.forEachKey(new TCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char var1x) {
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

   protected class TValueView implements TDoubleCollection {
      @Override
      public TDoubleIterator iterator() {
         return TCharDoubleHashMap.this.new TCharDoubleValueHashIterator(TCharDoubleHashMap.this);
      }

      @Override
      public double getNoEntryValue() {
         return TCharDoubleHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TCharDoubleHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharDoubleHashMap.this._size;
      }

      @Override
      public boolean contains(double var1) {
         return TCharDoubleHashMap.this.containsValue(var1);
      }

      @Override
      public double[] toArray() {
         return TCharDoubleHashMap.this.values();
      }

      @Override
      public double[] toArray(double[] var1) {
         return TCharDoubleHashMap.this.values(var1);
      }

      @Override
      public boolean add(double var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(double var1) {
         double[] var3 = TCharDoubleHashMap.this._values;
         byte[] var4 = TCharDoubleHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] != 0 && var4[var5] != 2 && var1 == var3[var5]) {
               TCharDoubleHashMap.this.removeAt(var5);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Double)) {
               return false;
            }

            double var4 = (Double)var3;
            if (!TCharDoubleHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TDoubleCollection var1) {
         TDoubleIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharDoubleHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(double[] var1) {
         for (double var5 : var1) {
            if (!TCharDoubleHashMap.this.containsValue(var5)) {
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
         double[] var3 = TCharDoubleHashMap.this._values;
         byte[] var4 = TCharDoubleHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharDoubleHashMap.this.removeAt(var5);
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
         TCharDoubleHashMap.this.clear();
      }

      @Override
      public boolean forEach(TDoubleProcedure var1) {
         return TCharDoubleHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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
}
