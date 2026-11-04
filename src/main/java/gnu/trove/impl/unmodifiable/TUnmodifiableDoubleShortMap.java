package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleShortMap implements TDoubleShortMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TDoubleShortMap m;
   private transient TDoubleSet keySet = null;
   private transient TShortCollection values = null;

   public TUnmodifiableDoubleShortMap(TDoubleShortMap var1) {
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
   public boolean containsValue(short var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public short get(double var1) {
      return this.m.get(var1);
   }

   @Override
   public short put(double var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TDoubleShortMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Short> var1) {
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
   public double getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TDoubleShortProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TDoubleShortIterator iterator() {
      return new TDoubleShortIterator() {
         TDoubleShortIterator iter = TUnmodifiableDoubleShortMap.this.m.iterator();

         @Override
         public double key() {
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
   public short putIfAbsent(double var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TDoubleShortProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(double var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short adjustOrPutValue(double var1, short var3, short var4) {
      throw new UnsupportedOperationException();
   }
}
