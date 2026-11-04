package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteIntMap implements TByteIntMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TByteIntMap m;
   private transient TByteSet keySet = null;
   private transient TIntCollection values = null;

   public TUnmodifiableByteIntMap(TByteIntMap var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
      }
   }

   @Override
   public int size() {
      return this.m.size();
   }

   @Override
   public boolean isEmpty() {
      return this.m.isEmpty();
   }

   @Override
   public boolean containsKey(byte var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(int var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public int get(byte var1) {
      return this.m.get(var1);
   }

   @Override
   public int put(byte var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TByteIntMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Integer> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TByteSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public byte[] keys() {
      return this.m.keys();
   }

   @Override
   public byte[] keys(byte[] var1) {
      return this.m.keys(var1);
   }

   @Override
   public TIntCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public int[] values() {
      return this.m.values();
   }

   @Override
   public int[] values(int[] var1) {
      return this.m.values(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.m.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.m.hashCode();
   }

   @Override
   public String toString() {
      return this.m.toString();
   }

   @Override
   public byte getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public int getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TIntProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TByteIntProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TByteIntIterator iterator() {
      return new TByteIntIterator() {
         TByteIntIterator iter = TUnmodifiableByteIntMap.this.m.iterator();

         @Override
         public byte key() {
            return this.iter.key();
         }

         @Override
         public int value() {
            return this.iter.value();
         }

         @Override
         public void advance() {
            this.iter.advance();
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         @Override
         public int setValue(int var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public int putIfAbsent(byte var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TIntFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TByteIntProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(byte var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int adjustOrPutValue(byte var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }
}
