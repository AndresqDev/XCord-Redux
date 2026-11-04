package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleByteMap implements TDoubleByteMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TDoubleByteMap m;
   private transient TDoubleSet keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableDoubleByteMap(TDoubleByteMap var1) {
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
   public boolean containsKey(double var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(double var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(double var1, byte var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TDoubleByteMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Byte> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TDoubleSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public double[] keys() {
      return this.m.keys();
   }

   @Override
   public double[] keys(double[] var1) {
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
   public double getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TDoubleByteProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TDoubleByteIterator iterator() {
      return new TDoubleByteIterator() {
         TDoubleByteIterator iter = TUnmodifiableDoubleByteMap.this.m.iterator();

         @Override
         public double key() {
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
   public byte putIfAbsent(double var1, byte var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TDoubleByteProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(double var1, byte var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(double var1, byte var3, byte var4) {
      throw new UnsupportedOperationException();
   }
}
