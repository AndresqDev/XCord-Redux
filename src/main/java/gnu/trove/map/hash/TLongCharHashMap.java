package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongCharHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;

public class TLongCharHashMap extends TLongCharHash implements TLongCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TLongCharHashMap() {
   }

   public TLongCharHashMap(int var1) {
      super(var1);
   }

   public TLongCharHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TLongCharHashMap(int var1, float var2, long var3, char var5) {
      super(var1, var2, var3, var5);
   }

   public TLongCharHashMap(long[] var1, char[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TLongCharHashMap(TLongCharMap var1) {
      super(var1.size());
      if (var1 instanceof TLongCharHashMap) {
         TLongCharHashMap var2 = (TLongCharHashMap)var1;
         this._loadFactor = Math.abs(var2._loadFactor);
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0L) {
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
      long[] var3 = this._set;
      char[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new long[var1];
      this._values = new char[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            long var7 = var3[var6];
            int var9 = this.insertKey(var7);
            this._values[var9] = var4[var6];
         }
      }
   }

   @Override
   public char put(long var1, char var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var3, var4);
   }

   @Override
   public char putIfAbsent(long var1, char var3) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var3, var4);
   }

   private char doPut(long var1, char var3, int var4) {
      char var5 = this.no_entry_value;
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
   public void putAll(Map<? extends Long, ? extends Character> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Long)var3.getKey(), (Character)var3.getValue());
      }
   }

   @Override
   public void putAll(TLongCharMap var1) {
      this.ensureCapacity(var1.size());
      TLongCharIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public char get(long var1) {
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
   public char remove(long var1) {
      char var3 = this.no_entry_value;
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
   public TLongSet keySet() {
      return new TLongCharHashMap.TKeyView();
   }

   @Override
   public long[] keys() {
      long[] var1 = new long[this.size()];
      if (var1.length == 0) {
         return var1;
      } else {
         long[] var2 = this._set;
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
   public long[] keys(long[] var1) {
      int var2 = this.size();
      if (var2 == 0) {
         return var1;
      } else {
         if (var1.length < var2) {
            var1 = new long[var2];
         }

         long[] var3 = this._set;
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
      return new TLongCharHashMap.TValueView();
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
   public boolean containsKey(long var1) {
      return this.contains(var1);
   }

   @Override
   public TLongCharIterator iterator() {
      return new TLongCharHashMap.TLongCharHashIterator(this);
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
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
   public boolean forEachEntry(TLongCharProcedure var1) {
      byte[] var2 = this._states;
      long[] var3 = this._set;
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
   public boolean retainEntries(TLongCharProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      long[] var4 = this._set;
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
   public boolean increment(long var1) {
      return this.adjustValue(var1, '\u0001');
   }

   @Override
   public boolean adjustValue(long var1, char var3) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = (char)(this._values[var4] + var3);
         return true;
      }
   }

   @Override
   public char adjustOrPutValue(long var1, char var3, char var4) {
      int var5 = this.insertKey(var1);
      boolean var6;
      char var7;
      if (var5 < 0) {
         var5 = -var5 - 1;
         var7 = this._values[var5] = (char)(this._values[var5] + var3);
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
      if (!(var1 instanceof TLongCharMap)) {
         return false;
      } else {
         TLongCharMap var2 = (TLongCharMap)var1;
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
                  long var8 = this._set[var7];
                  if (!var2.containsKey(var8)) {
                     return false;
                  }

                  char var10 = var2.get(var8);
                  char var11 = var3[var7];
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
      this.forEachEntry(new TLongCharProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(long var1x, char var3) {
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
            var1.writeLong(this._set[var2]);
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
         long var3 = var1.readLong();
         char var5 = var1.readChar();
         this.put(var3, var5);
      }
   }

   protected class TKeyView implements TLongSet {
      @Override
      public TLongIterator iterator() {
         return TLongCharHashMap.this.new TLongCharKeyHashIterator(TLongCharHashMap.this);
      }

      @Override
      public long getNoEntryValue() {
         return TLongCharHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TLongCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TLongCharHashMap.this._size;
      }

      @Override
      public boolean contains(long var1) {
         return TLongCharHashMap.this.contains(var1);
      }

      @Override
      public long[] toArray() {
         return TLongCharHashMap.this.keys();
      }

      @Override
      public long[] toArray(long[] var1) {
         return TLongCharHashMap.this.keys(var1);
      }

      @Override
      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long var1) {
         return TLongCharHashMap.this.no_entry_value != TLongCharHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Long)) {
               return false;
            }

            long var4 = (Long)var3;
            if (!TLongCharHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TLongCollection var1) {
         TLongIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TLongCharHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(long[] var1) {
         for (long var5 : var1) {
            if (!TLongCharHashMap.this.contains(var5)) {
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
         long[] var3 = TLongCharHashMap.this._set;
         byte[] var4 = TLongCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TLongCharHashMap.this.removeAt(var5);
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
         TLongCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TLongProcedure var1) {
         return TLongCharHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TLongSet)) {
            return false;
         } else {
            TLongSet var2 = (TLongSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TLongCharHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TLongCharHashMap.this._states[var3] == 1 && !var2.contains(TLongCharHashMap.this._set[var3])) {
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
         int var2 = TLongCharHashMap.this._states.length;

         while (var2-- > 0) {
            if (TLongCharHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TLongCharHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TLongCharHashMap.this.forEachKey(new TLongProcedure() {
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

   class TLongCharHashIterator extends THashPrimitiveIterator implements TLongCharIterator {
      TLongCharHashIterator(TLongCharHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public long key() {
         return TLongCharHashMap.this._set[this._index];
      }

      @Override
      public char value() {
         return TLongCharHashMap.this._values[this._index];
      }

      @Override
      public char setValue(char var1) {
         char var2 = this.value();
         TLongCharHashMap.this._values[this._index] = var1;
         return var2;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TLongCharKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TLongCharKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public long next() {
         this.moveToNextIndex();
         return TLongCharHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TLongCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TLongCharValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TLongCharHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   protected class TValueView implements TCharCollection {
      @Override
      public TCharIterator iterator() {
         return TLongCharHashMap.this.new TLongCharValueHashIterator(TLongCharHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TLongCharHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TLongCharHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TLongCharHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TLongCharHashMap.this.containsValue(var1);
      }

      @Override
      public char[] toArray() {
         return TLongCharHashMap.this.values();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TLongCharHashMap.this.values(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         char[] var2 = TLongCharHashMap.this._values;
         byte[] var3 = TLongCharHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != 0 && var3[var4] != 2 && var1 == var2[var4]) {
               TLongCharHashMap.this.removeAt(var4);
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
            if (!TLongCharHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TLongCharHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TLongCharHashMap.this.containsValue(var5)) {
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
         char[] var3 = TLongCharHashMap.this._values;
         byte[] var4 = TLongCharHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TLongCharHashMap.this.removeAt(var5);
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
         TLongCharHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TLongCharHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TLongCharHashMap.this.forEachValue(new TCharProcedure() {
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
