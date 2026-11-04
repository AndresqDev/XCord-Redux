package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleFloatMap implements TDoubleFloatMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TDoubleFloatMap m;
   private transient TDoubleSet keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableDoubleFloatMap(TDoubleFloatMap var1) {
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
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(double var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(double var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TDoubleFloatMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Float> var1) {
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
   public double getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TDoubleFloatProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TDoubleFloatIterator iterator() {
      return new TDoubleFloatIterator() {
         TDoubleFloatIterator iter = TUnmodifiableDoubleFloatMap.this.m.iterator();

         @Override
         public double key() {
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
   public float putIfAbsent(double var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TDoubleFloatProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(double var1, float var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(double var1, float var3, float var4) {
      throw new UnsupportedOperationException();
   }
}
