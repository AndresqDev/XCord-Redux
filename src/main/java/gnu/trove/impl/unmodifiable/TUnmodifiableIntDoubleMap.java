package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntDoubleMap implements TIntDoubleMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TIntDoubleMap m;
   private transient TIntSet keySet = null;
   private transient TDoubleCollection values = null;

   public TUnmodifiableIntDoubleMap(TIntDoubleMap var1) {
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
   public boolean containsKey(int var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(double var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public double get(int var1) {
      return this.m.get(var1);
   }

   @Override
   public double put(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double remove(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TIntDoubleMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Double> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TIntSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public int[] keys() {
      return this.m.keys();
   }

   @Override
   public int[] keys(int[] var1) {
      return this.m.keys(var1);
   }

   @Override
   public TDoubleCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public double[] values() {
      return this.m.values();
   }

   @Override
   public double[] values(double[] var1) {
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
   public int getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public double getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TDoubleProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TIntDoubleProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TIntDoubleIterator iterator() {
      return new TIntDoubleIterator() {
         TIntDoubleIterator iter = TUnmodifiableIntDoubleMap.this.m.iterator();

         @Override
         public int key() {
            return this.iter.key();
         }

         @Override
         public double value() {
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
         public double setValue(double var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public double putIfAbsent(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TIntDoubleProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double adjustOrPutValue(int var1, double var2, double var4) {
      throw new UnsupportedOperationException();
   }
}
