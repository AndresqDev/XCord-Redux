package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteByteMap implements TByteByteMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TByteByteMap m;
   private transient TByteSet keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableByteByteMap(TByteByteMap var1) {
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
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(byte var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(byte var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TByteByteMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Byte> var1) {
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
   public TByteCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public byte[] values() {
      return this.m.values();
   }

   @Override
   public byte[] values(byte[] var1) {
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
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TByteByteProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TByteByteIterator iterator() {
      return new TByteByteIterator() {
         TByteByteIterator iter = TUnmodifiableByteByteMap.this.m.iterator();

         @Override
         public byte key() {
            return this.iter.key();
         }

         @Override
         public byte value() {
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
         public byte setValue(byte var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public byte putIfAbsent(byte var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TByteByteProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(byte var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(byte var1, byte var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
