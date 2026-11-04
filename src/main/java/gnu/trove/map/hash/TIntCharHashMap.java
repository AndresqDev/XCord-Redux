package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntCharHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
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

public class TIntCharHashMap extends TIntCharHash implements TIntCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TIntCharHashMap() {
   }

   public TIntCharHashMap(int var1) {
      super(var1);
   }

   public TIntCharHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TIntCharHashMap(int var1, float var2, int var3, char var4) {
      super(var1, var2, var3, var4);
   }

   public TIntCharHashMap(int[] var1, char[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TIntCharHashMap(TIntCharMap var1) {
      super(var1.size());
      if (var1 instanceof TIntCharHashMap) {
         TIntCharHashMap var2 = (TIntCharHashMap)var1;
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
      this._values = new char[var2];
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      int[] var3 = this._set;
      char[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new int[var1];
      this._values = new char[var1];
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
   public char put(int var1, char var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   @Override
   public char putIfAbsent(int var1, char var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private char doPut(int var1, char var2, int var3) {
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
   public void putAll(Map<? extends Integer, ? extends Character> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Integer)var3.getKey(), (Character)var3.getValue());
      }
   }

   @Override
   public void putAll(TIntCharMap var1) {
      this.ensureCapacity(var1.size());
      TIntCharIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public char get(int var1) {
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
   public char remove(int var1) {
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
   public TIntSet keySet() {
      return new TIntCharHashMap.TKeyView();
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
   public TCharCollection valueCollection() {
      return new TIntCharHashMap.TValueView();
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
   public boolean containsKey(int var1) {
      return this.contains(var1);
   }

   @Override
   public TIntCharIterator iterator() {
      return new TIntCharHashMap.TIntCharHashIterator(this);
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
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
   public boolean forEachEntry(TIntCharProcedure var1) {
      byte[] var2 = this._states;
      int[] var3 = this._set;
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
   public boolean retainEntries(TIntCharProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      int[] var4 = this._set;
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
   public boolean increment(int var1) {
      return this.adjustValue(var1, '\u0001');
   }

   @Override
   public boolean adjustValue(int var1, char var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (char)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public char adjustOrPutValue(int var1, char var2, char var3) {
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
      if (!(var1 instanceof TIntCharMap)) {
         return false;
      } else {
         TIntCharMap var2 = (TIntCharMap)var1;
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
                  int var8 = this._set[var7];
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
      this.forEachEntry(new TIntCharProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(int var1x, char var2) {
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
            var1.writeInt(this._set[var2]);
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
         int var3 = var1.readInt();
         char var4 = var1.readChar();
         this.put(var3, var4);
      }
   }

   class TIntCharHashIterator extends THashPrimitiveIterator implements TIntCharIterator {
      TIntCharHashIterator(TIntCharHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public int key() {
         return TIntCharHashMap.this._set[this._index];
      }

      @Override
      public char value() {
         return TIntCharHashMap.this._values[this._index];
      }

      @Override
      public char setValue(char var1) {
         char var2 = this.value();
         TIntCharHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TIntCharKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {
      TIntCharKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public int next() {
         this.moveToNextIndex();
         return TIntCharHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TIntCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TIntCharValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TIntCharHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntCharHashMap.this.removeAt(this._index);
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
         return TIntCharHashMap.this.new TIntCharKeyHashIterator(TIntCharHashMap.this);
      }

      @Override
      public int getNoEntryValue() {
         return TIntCharHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TIntCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TIntCharHashMap.this._size;
      }

      @Override
      public boolean contains(int var1) {
         return TIntCharHashMap.this.contains(var1);
      }

      @Override
      public int[] toArray() {
         return TIntCharHashMap.this.keys();
      }

      @Override
      public int[] toArray(int[] var1) {
         return TIntCharHashMap.this.keys(var1);
      }

      @Override
      public boolean add(int var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int var1) {
         return TIntCharHashMap.this.no_entry_value != TIntCharHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Integer)) {
               return false;
            }

            int var4 = (Integer)var3;
            if (!TIntCharHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TIntCollection var1) {
         TIntIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TIntCharHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(int[] var1) {
         for (int var5 : var1) {
            if (!TIntCharHashMap.this.contains(var5)) {
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
         int[] var3 = TIntCharHashMap.this._set;
         byte[] var4 = TIntCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TIntCharHashMap.this.removeAt(var5);
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
         TIntCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TIntProcedure var1) {
         return TIntCharHashMap.this.forEachKey(var1);
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
               int var3 = TIntCharHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TIntCharHashMap.this._states[var3] == 1 && !var2.contains(TIntCharHashMap.this._set[var3])) {
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
         int var2 = TIntCharHashMap.this._states.length;

         while (var2-- > 0) {
            if (TIntCharHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TIntCharHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TIntCharHashMap.this.forEachKey(new TIntProcedure() {
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

   protected class TValueView implements TCharCollection {
      @Override
      public TCharIterator iterator() {
         return TIntCharHashMap.this.new TIntCharValueHashIterator(TIntCharHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TIntCharHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TIntCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TIntCharHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TIntCharHashMap.this.containsValue(var1);
      }

      @Override
      public char[] toArray() {
         return TIntCharHashMap.this.values();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TIntCharHashMap.this.values(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         char[] var2 = TIntCharHashMap.this._values;
         byte[] var3 = TIntCharHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TIntCharHashMap.this.removeAt(var4);
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
            if (!TIntCharHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TIntCharHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TIntCharHashMap.this.containsValue(var5)) {
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
         char[] var3 = TIntCharHashMap.this._values;
         byte[] var4 = TIntCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TIntCharHashMap.this.removeAt(var5);
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
         TIntCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TIntCharHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TIntCharHashMap.this.forEachValue(new TCharProcedure() {
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
