package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteFloatMap implements TByteFloatMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TByteFloatMap m;
   private transient TByteSet keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableByteFloatMap(TByteFloatMap var1) {
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
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(byte var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(byte var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TByteFloatMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Float> var1) {
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
   public TFloatCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public float[] values() {
      return this.m.values();
   }

   @Override
   public float[] values(float[] var1) {
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
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TByteFloatProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TByteFloatIterator iterator() {
      return new TByteFloatIterator() {
         TByteFloatIterator iter = TUnmodifiableByteFloatMap.this.m.iterator();

         @Override
         public byte key() {
            return this.iter.key();
         }

         @Override
         public float value() {
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
         public float setValue(float var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public float putIfAbsent(byte var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TByteFloatProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(byte var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(byte var1, float var2, float var3) {
      throw new UnsupportedOperationException();
   }
}
