package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatByteMap implements TFloatByteMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TFloatByteMap m;
   private transient TFloatSet keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableFloatByteMap(TFloatByteMap var1) {
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
   public boolean containsKey(float var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(float var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(float var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TFloatByteMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Byte> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TFloatSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public float[] keys() {
      return this.m.keys();
   }

   @Override
   public float[] keys(float[] var1) {
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
   public float getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TFloatByteProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TFloatByteIterator iterator() {
      return new TFloatByteIterator() {
         TFloatByteIterator iter = TUnmodifiableFloatByteMap.this.m.iterator();

         @Override
         public float key() {
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
   public byte putIfAbsent(float var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TFloatByteProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(float var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(float var1, byte var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
