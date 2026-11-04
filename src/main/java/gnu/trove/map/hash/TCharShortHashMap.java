package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TCharShortHashMap extends TCharShortHash implements TCharShortMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TCharShortHashMap() {
   }

   public TCharShortHashMap(int var1) {
      super(var1);
   }

   public TCharShortHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TCharShortHashMap(int var1, float var2, char var3, short var4) {
      super(var1, var2, var3, var4);
   }

   public TCharShortHashMap(char[] var1, short[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TCharShortHashMap(TCharShortMap var1) {
      super(var1.size());
      if (var1 instanceof TCharShortHashMap) {
         TCharShortHashMap var2 = (TCharShortHashMap)var1;
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
      char[] var3 = this._set;
      short[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new char[var1];
      this._values = new short[var1];
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
   public short put(char var1, short var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var1, var2, var3);
   }

   @Override
   public short putIfAbsent(char var1, short var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut(var1, var2, var3);
   }

   private short doPut(char var1, short var2, int var3) {
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
   public void putAll(Map<? extends Character, ? extends Short> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Character)var3.getKey(), (Short)var3.getValue());
      }
   }

   @Override
   public void putAll(TCharShortMap var1) {
      this.ensureCapacity(var1.size());
      TCharShortIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public short get(char var1) {
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
   public short remove(char var1) {
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
   public TCharSet keySet() {
      return new TCharShortHashMap.TKeyView();
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
   public TShortCollection valueCollection() {
      return new TCharShortHashMap.TValueView();
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
   public boolean containsKey(char var1) {
      return this.contains(var1);
   }

   @Override
   public TCharShortIterator iterator() {
      return new TCharShortHashMap.TCharShortHashIterator(this);
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
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
   public boolean forEachEntry(TCharShortProcedure var1) {
      byte[] var2 = this._states;
      char[] var3 = this._set;
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
   public boolean retainEntries(TCharShortProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      char[] var4 = this._set;
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
   public boolean increment(char var1) {
      return this.adjustValue(var1, (short)1);
   }

   @Override
   public boolean adjustValue(char var1, short var2) {
      int var3 = this.index(var1);
      if (var3 < 0) {
         return false;
      } else {
         this._values[var3] = (short)(this._values[var3] + var2);
         return true;
      }
   }

   @Override
   public short adjustOrPutValue(char var1, short var2, short var3) {
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
      if (!(var1 instanceof TCharShortMap)) {
         return false;
      } else {
         TCharShortMap var2 = (TCharShortMap)var1;
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
                  char var8 = this._set[var7];
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
      this.forEachEntry(new TCharShortProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(char var1x, short var2) {
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
            var1.writeChar(this._set[var2]);
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
         char var3 = var1.readChar();
         short var4 = var1.readShort();
         this.put(var3, var4);
      }
   }

   class TCharShortHashIterator extends THashPrimitiveIterator implements TCharShortIterator {
      TCharShortHashIterator(TCharShortHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public char key() {
         return TCharShortHashMap.this._set[this._index];
      }

      @Override
      public short value() {
         return TCharShortHashMap.this._values[this._index];
      }

      @Override
      public short setValue(short var1) {
         short var2 = this.value();
         TCharShortHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharShortKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TCharShortKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TCharShortHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TCharShortValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public short next() {
         this.moveToNextIndex();
         return TCharShortHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
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
         return TCharShortHashMap.this.new TCharShortKeyHashIterator(TCharShortHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TCharShortHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TCharShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharShortHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TCharShortHashMap.this.contains(var1);
      }

      @Override
      public char[] toArray() {
         return TCharShortHashMap.this.keys();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TCharShortHashMap.this.keys(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         return TCharShortHashMap.this.no_entry_value != TCharShortHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Character)) {
               return false;
            }

            char var4 = (Character)var3;
            if (!TCharShortHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharShortHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TCharShortHashMap.this.contains(var5)) {
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
         char[] var3 = TCharShortHashMap.this._set;
         byte[] var4 = TCharShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharShortHashMap.this.removeAt(var5);
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
         TCharShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TCharShortHashMap.this.forEachKey(var1);
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
               int var3 = TCharShortHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TCharShortHashMap.this._states[var3] == 1 && !var2.contains(TCharShortHashMap.this._set[var3])) {
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
         int var2 = TCharShortHashMap.this._states.length;

         while (var2-- > 0) {
            if (TCharShortHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TCharShortHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharShortHashMap.this.forEachKey(new TCharProcedure() {
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

   protected class TValueView implements TShortCollection {
      @Override
      public TShortIterator iterator() {
         return TCharShortHashMap.this.new TCharShortValueHashIterator(TCharShortHashMap.this);
      }

      @Override
      public short getNoEntryValue() {
         return TCharShortHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TCharShortHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharShortHashMap.this._size;
      }

      @Override
      public boolean contains(short var1) {
         return TCharShortHashMap.this.containsValue(var1);
      }

      @Override
      public short[] toArray() {
         return TCharShortHashMap.this.values();
      }

      @Override
      public short[] toArray(short[] var1) {
         return TCharShortHashMap.this.values(var1);
      }

      @Override
      public boolean add(short var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(short var1) {
         short[] var2 = TCharShortHashMap.this._values;
         byte[] var3 = TCharShortHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TCharShortHashMap.this.removeAt(var4);
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
            if (!TCharShortHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TShortCollection var1) {
         TShortIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharShortHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(short[] var1) {
         for (short var5 : var1) {
            if (!TCharShortHashMap.this.containsValue(var5)) {
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
         short[] var3 = TCharShortHashMap.this._values;
         byte[] var4 = TCharShortHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharShortHashMap.this.removeAt(var5);
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
         TCharShortHashMap.this.clear();
      }

      @Override
      public boolean forEach(TShortProcedure var1) {
         return TCharShortHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharShortHashMap.this.forEachValue(new TShortProcedure() {
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
