package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatShortMap implements TFloatShortMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TFloatShortMap m;
   private transient TFloatSet keySet = null;
   private transient TShortCollection values = null;

   public TUnmodifiableFloatShortMap(TFloatShortMap var1) {
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
   public boolean containsValue(short var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public short get(float var1) {
      return this.m.get(var1);
   }

   @Override
   public short put(float var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TFloatShortMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Short> var1) {
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
   public float getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TFloatShortProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TFloatShortIterator iterator() {
      return new TFloatShortIterator() {
         TFloatShortIterator iter = TUnmodifiableFloatShortMap.this.m.iterator();

         @Override
         public float key() {
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
   public short putIfAbsent(float var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TFloatShortProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(float var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short adjustOrPutValue(float var1, short var2, short var3) {
      throw new UnsupportedOperationException();
   }
}
