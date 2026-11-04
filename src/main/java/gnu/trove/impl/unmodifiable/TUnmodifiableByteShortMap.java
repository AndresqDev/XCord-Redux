package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteShortMap implements TByteShortMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TByteShortMap m;
   private transient TByteSet keySet = null;
   private transient TShortCollection values = null;

   public TUnmodifiableByteShortMap(TByteShortMap var1) {
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
   public boolean containsValue(short var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public short get(byte var1) {
      return this.m.get(var1);
   }

   @Override
   public short put(byte var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TByteShortMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Short> var1) {
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
   public TShortCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public short[] values() {
      return this.m.values();
   }

   @Override
   public short[] values(short[] var1) {
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
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TByteShortProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TByteShortIterator iterator() {
      return new TByteShortIterator() {
         TByteShortIterator iter = TUnmodifiableByteShortMap.this.m.iterator();

         @Override
         public byte key() {
            return this.iter.key();
         }

         @Override
         public short value() {
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
         public short setValue(short var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public short putIfAbsent(byte var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TByteShortProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(byte var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short adjustOrPutValue(byte var1, short var2, short var3) {
      throw new UnsupportedOperationException();
   }
}
