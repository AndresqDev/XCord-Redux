package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
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

public class TCharLongHashMap extends TCharLongHash implements TCharLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TCharLongHashMap() {
   }

   public TCharLongHashMap(int var1) {
      super(var1);
   }

   public TCharLongHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TCharLongHashMap(int var1, float var2, char var3, long var4) {
      super(var1, var2, var3, var4);
   }

   public TCharLongHashMap(char[] var1, long[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.put(var1[var4], var2[var4]);
      }
   }

   public TCharLongHashMap(TCharLongMap var1) {
      super(var1.size());
      if (var1 instanceof TCharLongHashMap) {
         TCharLongHashMap var2 = (TCharLongHashMap)var1;
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
      char[] var3 = this._set;
      long[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new char[var1];
      this._values = new long[var1];
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
   public long put(char var1, long var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var2, var4);
   }

   @Override
   public long putIfAbsent(char var1, long var2) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var2, var4);
   }

   private long doPut(char var1, long var2, int var4) {
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
   public void putAll(Map<? extends Character, ? extends Long> var1) {
      this.ensureCapacity(var1.size());

      for (Entry var3 : var1.entrySet()) {
         this.put((Character)var3.getKey(), (Long)var3.getValue());
      }
   }

   @Override
   public void putAll(TCharLongMap var1) {
      this.ensureCapacity(var1.size());
      TCharLongIterator var2 = var1.iterator();

      while (var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }
   }

   @Override
   public long get(char var1) {
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
   public long remove(char var1) {
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
   public TCharSet keySet() {
      return new TCharLongHashMap.TKeyView();
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
   public TLongCollection valueCollection() {
      return new TCharLongHashMap.TValueView();
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
   public boolean containsKey(char var1) {
      return this.contains(var1);
   }

   @Override
   public TCharLongIterator iterator() {
      return new TCharLongHashMap.TCharLongHashIterator(this);
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
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
   public boolean forEachEntry(TCharLongProcedure var1) {
      byte[] var2 = this._states;
      char[] var3 = this._set;
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
   public boolean retainEntries(TCharLongProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      char[] var4 = this._set;
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
   public boolean increment(char var1) {
      return this.adjustValue(var1, 1L);
   }

   @Override
   public boolean adjustValue(char var1, long var2) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         this._values[var4] = this._values[var4] + var2;
         return true;
      }
   }

   @Override
   public long adjustOrPutValue(char var1, long var2, long var4) {
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
      if (!(var1 instanceof TCharLongMap)) {
         return false;
      } else {
         TCharLongMap var2 = (TCharLongMap)var1;
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
                  char var10 = this._set[var9];
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
      this.forEachEntry(new TCharLongProcedure() {
         private boolean first = true;

         @Override
         public boolean execute(char var1x, long var2) {
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
         char var3 = var1.readChar();
         long var4 = var1.readLong();
         this.put(var3, var4);
      }
   }

   class TCharLongHashIterator extends THashPrimitiveIterator implements TCharLongIterator {
      TCharLongHashIterator(TCharLongHashMap var2) {
         super(var2);
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public char key() {
         return TCharLongHashMap.this._set[this._index];
      }

      @Override
      public long value() {
         return TCharLongHashMap.this._values[this._index];
      }

      @Override
      public long setValue(long var1) {
         long var3 = this.value();
         TCharLongHashMap.this._values[this._index] = var1;
         return var3;
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharLongKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TCharLongKeyHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public char next() {
         this.moveToNextIndex();
         return TCharLongHashMap.this._set[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            this._expectedSize--;
         }
      }
   }

   class TCharLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TCharLongValueHashIterator(TPrimitiveHash var2) {
         super(var2);
      }

      @Override
      public long next() {
         this.moveToNextIndex();
         return TCharLongHashMap.this._values[this._index];
      }

      @Override
      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharLongHashMap.this.removeAt(this._index);
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
         return TCharLongHashMap.this.new TCharLongKeyHashIterator(TCharLongHashMap.this);
      }

      @Override
      public char getNoEntryValue() {
         return TCharLongHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TCharLongHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharLongHashMap.this._size;
      }

      @Override
      public boolean contains(char var1) {
         return TCharLongHashMap.this.contains(var1);
      }

      @Override
      public char[] toArray() {
         return TCharLongHashMap.this.keys();
      }

      @Override
      public char[] toArray(char[] var1) {
         return TCharLongHashMap.this.keys(var1);
      }

      @Override
      public boolean add(char var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(char var1) {
         return TCharLongHashMap.this.no_entry_value != TCharLongHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!(var3 instanceof Character)) {
               return false;
            }

            char var4 = (Character)var3;
            if (!TCharLongHashMap.this.containsKey(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TCharCollection var1) {
         TCharIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharLongHashMap.this.containsKey(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(char[] var1) {
         for (char var5 : var1) {
            if (!TCharLongHashMap.this.contains(var5)) {
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
         char[] var3 = TCharLongHashMap.this._set;
         byte[] var4 = TCharLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharLongHashMap.this.removeAt(var5);
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
         TCharLongHashMap.this.clear();
      }

      @Override
      public boolean forEach(TCharProcedure var1) {
         return TCharLongHashMap.this.forEachKey(var1);
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
               int var3 = TCharLongHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TCharLongHashMap.this._states[var3] == 1 && !var2.contains(TCharLongHashMap.this._set[var3])) {
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
         int var2 = TCharLongHashMap.this._states.length;

         while (var2-- > 0) {
            if (TCharLongHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TCharLongHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharLongHashMap.this.forEachKey(new TCharProcedure() {
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

   protected class TValueView implements TLongCollection {
      @Override
      public TLongIterator iterator() {
         return TCharLongHashMap.this.new TCharLongValueHashIterator(TCharLongHashMap.this);
      }

      @Override
      public long getNoEntryValue() {
         return TCharLongHashMap.this.no_entry_value;
      }

      @Override
      public int size() {
         return TCharLongHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return 0 == TCharLongHashMap.this._size;
      }

      @Override
      public boolean contains(long var1) {
         return TCharLongHashMap.this.containsValue(var1);
      }

      @Override
      public long[] toArray() {
         return TCharLongHashMap.this.values();
      }

      @Override
      public long[] toArray(long[] var1) {
         return TCharLongHashMap.this.values(var1);
      }

      @Override
      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(long var1) {
         long[] var3 = TCharLongHashMap.this._values;
         byte[] var4 = TCharLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] != 0 && var4[var5] != 2 && var1 == var3[var5]) {
               TCharLongHashMap.this.removeAt(var5);
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
            if (!TCharLongHashMap.this.containsValue(var4)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TLongCollection var1) {
         TLongIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            if (!TCharLongHashMap.this.containsValue(var2.next())) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(long[] var1) {
         for (long var5 : var1) {
            if (!TCharLongHashMap.this.containsValue(var5)) {
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
         long[] var3 = TCharLongHashMap.this._values;
         byte[] var4 = TCharLongHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TCharLongHashMap.this.removeAt(var5);
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
         TCharLongHashMap.this.clear();
      }

      @Override
      public boolean forEach(TLongProcedure var1) {
         return TCharLongHashMap.this.forEachValue(var1);
      }

      @Override
      public String toString() {
         final StringBuilder var1 = new StringBuilder("{");
         TCharLongHashMap.this.forEachValue(new TLongProcedure() {
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
