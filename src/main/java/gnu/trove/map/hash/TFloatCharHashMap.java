package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TFloatCharHashMap extends TFloatCharHash implements TFloatCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TFloatCharHashMap() {
   }

   public TFloatCharHashMap(int var1) {
      super(var1);
   }

   public TFloatCharHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TFloatCharHashMap(int var1, float var2, float var3, char var4) {
      super(var1, var2, var3, var4);
   }

   public TFloatCharHashMap(float[] var1, char[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TFloatCharHashMap(TFloatCharMap var1) {
      super(var1.size());
      if (var1 instanceof TFloatCharHashMap) {
         TFloatCharHashMap var2 = (TFloatCharHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0.0F) {
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
      this._values = new char[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      float[] var3 = this._set;
      char[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new float[var1];
      this._values = new char[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            float var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = var4[var6];
         }
      }
   }

   @Override
   public char put(float var1, char var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   @Override
   public char putIfAbsent(float var1, char var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private char doPut(float var1, char var2, int var3) {
      char var4 = this.no_entry_value;
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
   public void putAll(Map<? extends Float, ? extends Character> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Float)var3.getKey(), (Character)var3.getValue());
      }
   }

   @Override
   public void putAll(TFloatCharMap var1) {
      this.ensureCapacity(var1.size());
      TFloatCharIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public char get(float var1) {
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
   public char remove(float var1) {
      char var2 = this.no_entry_value;
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
   public TFloatSet keySet() {
      return new TFloatCharHashMap.TKeyView();
   }

   @Override
   public float[] keys() {
      float[] var1 = new float[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         float[] var2 = this._set;
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
   public float[] keys(float[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new float[var2];
         }

         float[] var3 = this._set;
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
   public TCharCollection valueCollection() {
      return new TFloatCharHashMap.TValueView();
   }

   @Override
   public char[] values() {
      char[] var1 = new char[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         char[] var2 = this._values;
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
   public char[] values(char[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new char[var2];
         }

         char[] var3 = this._values;
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
   public boolean containsValue(char var1) {
      byte[] var2 = this._states;
      char[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && var1 == var3[var4]) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsKey(float var1) {
      return this.contains(var1);
   }

   @Override
   public TFloatCharIterator iterator() {
      return new TFloatCharHashMap.TFloatCharHashIterator(this);
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TCharProcedure var1) {
      byte[] var2 = this._states;
      char[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TFloatCharProcedure var1) {
      byte[] var2 = this._states;
      float[] var3 = this._set;
      char[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void transformValues(TCharFunction var1) {
      byte[] var2 = this._states;
      char[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean retainEntries(TFloatCharProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      float[] var4 = this._set;
      char[] var5 = this._values;
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
   public boolean increment(float var1) {
      return this.adjustValue(var1, '\u0001');
   }

   @Override
   public boolean adjustValue(float var1, char var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (char)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public char adjustOrPutValue(float var1, char var2, char var3) {
      int var4 = this.insertKey(var1);
      boolean var5;
      char var6;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var6 = this._values[var4] = (char)(this._values[var4] + var2);
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
      if (!(var1 instanceof TFloatCharMap)) {
         return false;
      } else {
         TFloatCharMap var2 = (TFloatCharMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            char[] var3 = this._values;
            byte[] var4 = this._states;
            char var5 = this.getNoEntryValue();
            char var6 = var2.getNoEntryValue();
            int var7 = var3.length;

            while (var7-- > 0) {
               if (var4[var7] == 1) {
                  float var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  char var9 = var2.get(var8);
                  char var10 = var3[var7];
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
      this.forEachEntry(new TFloatCharProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(float var1x, char var2) {
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
            var1.writeFloat(this._set[var2]);
            var1.writeChar(this._values[var2]);
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
         float var3 = var1.readFloat();
         char var4 = var1.readChar();
         this.put(var3, var4);
      }
   }

   class TFloatCharHashIterator extends THashPrimitiveIterator implements TFloatCharIterator {
      TFloatCharHashIterator(TFloatCharHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public float key() {
         return TFloatCharHashMap.this._set[this._index];
      }

      @Override
      public char value() {
         return TFloatCharHashMap.this._values[this._index];
      }

      @Override
      public char setValue(char var1) {
         char var2 = this.value();
         TFloatCharHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TFloatCharKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
      TFloatCharKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public float next() {
         this.moveToNextIndex();
         return TFloatCharHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TFloatCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TFloatCharValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TFloatCharHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TKeyView implements TFloatSet {
      @Override
      public TFloatIterator iterator() {
         return TFloatCharHashMap.this.new TFloatCharKeyHashIterator(TFloatCharHashMap.this);
      }

      @Override
      public float getNoEntryValue() {
         return TFloatCharHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TFloatCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TFloatCharHashMap.this._size;
      }

      @Override
      public boolean contains(float var1) {
         return TFloatCharHashMap.this.contains(var1);
      }

      @Override
      public float[] toArray() {
         return TFloatCharHashMap.this.keys();
      }

      @Override
      public float[] toArray(float[] var1) {
         return TFloatCharHashMap.this.keys(var1);
      }

      @Override
      public boolean add(float var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(float var1) {
         return TFloatCharHashMap.this.no_entry_value != TFloatCharHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Float)) {
               return false;
            }

            float var4 = (Float)var3;
            if (!TFloatCharHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TFloatCollection var1) {
         TFloatIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TFloatCharHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(float[] var1) {
         for (float var5 : var1) {
            if (!TFloatCharHashMap.this.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TFloatCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(float[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TFloatIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TFloatCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TFloatIterator var3 = this.iterator();

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
      public boolean retainAll(float[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         float[] var3 = TFloatCharHashMap.this._set;
         byte[] var4 = TFloatCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TFloatCharHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Float) {
               float var5 = (Float)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TFloatCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TFloatIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               float var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(float[] var1) {
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
         TFloatCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TFloatProcedure var1) {
         return TFloatCharHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TFloatSet)) {
            return false;
         } else {
            TFloatSet var2 = (TFloatSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TFloatCharHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TFloatCharHashMap.this._states[var3] == 1 && !var2.contains(TFloatCharHashMap.this._set[var3])) {
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
         int var2 = TFloatCharHashMap.this._states.length;

         while (var2-- > 0) {
            if (TFloatCharHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TFloatCharHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TFloatCharHashMap.this.forEachKey(new TFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(float var1x) {
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

   protected class TValueView implements TCharCollection {
      @Override
      public TCharIterator iterator() {
         return TFloatCharHashMap.this.new TFloatCharValueHashIterator(TFloatCharHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TFloatCharHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TFloatCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TFloatCharHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TFloatCharHashMap.this.containsValue(var1);
      }

      @Override
      public char[] toArray() {
         return TFloatCharHashMap.this.values();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TFloatCharHashMap.this.values(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         char[] var2 = TFloatCharHashMap.this._values;
         byte[] var3 = TFloatCharHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TFloatCharHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Character)) {
               return false;
            }

            char var4 = (Character)var3;
            if (!TFloatCharHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TFloatCharHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TFloatCharHashMap.this.containsValue(var5)) {
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
         char[] var3 = TFloatCharHashMap.this._values;
         byte[] var4 = TFloatCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TFloatCharHashMap.this.removeAt(var5);
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
         TFloatCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TFloatCharHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TFloatCharHashMap.this.forEachValue(new TCharProcedure() {
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
}
