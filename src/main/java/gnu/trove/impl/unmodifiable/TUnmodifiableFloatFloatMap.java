package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatFloatMap implements TFloatFloatMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TFloatFloatMap m;
   private transient TFloatSet keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableFloatFloatMap(TFloatFloatMap var1) {
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
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(float var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(float var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TFloatFloatMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Float> var1) {
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
   public float getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TFloatFloatProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TFloatFloatIterator iterator() {
      return new TFloatFloatIterator() {
         TFloatFloatIterator iter = TUnmodifiableFloatFloatMap.this.m.iterator();

         @Override
         public float key() {
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
   public float putIfAbsent(float var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TFloatFloatProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(float var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(float var1, float var2, float var3) {
      throw new UnsupportedOperationException();
   }
}
