package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongFloatMap implements TLongFloatMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TLongFloatMap m;
   private transient TLongSet keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableLongFloatMap(TLongFloatMap var1) {
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
   public boolean containsKey(long var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(long var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(long var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TLongFloatMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Float> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TLongSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public long[] keys() {
      return this.m.keys();
   }

   @Override
   public long[] keys(long[] var1) {
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
   public long getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TLongFloatProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TLongFloatIterator iterator() {
      return new TLongFloatIterator() {
         TLongFloatIterator iter = TUnmodifiableLongFloatMap.this.m.iterator();

         @Override
         public long key() {
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
   public float putIfAbsent(long var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TLongFloatProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(long var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(long var1, float var3, float var4) {
      throw new UnsupportedOperationException();
   }
}
