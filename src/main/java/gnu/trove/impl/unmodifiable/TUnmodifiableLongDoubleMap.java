package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongDoubleMap implements TLongDoubleMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TLongDoubleMap m;
   private transient TLongSet keySet = null;
   private transient TDoubleCollection values = null;

   public TUnmodifiableLongDoubleMap(TLongDoubleMap var1) {
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
   public boolean containsValue(double var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public double get(long var1) {
      return this.m.get(var1);
   }

   @Override
   public double put(long var1, double var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TLongDoubleMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Double> var1) {
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
   public long getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public double getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TDoubleProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TLongDoubleProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TLongDoubleIterator iterator() {
      return new TLongDoubleIterator() {
         TLongDoubleIterator iter = TUnmodifiableLongDoubleMap.this.m.iterator();

         @Override
         public long key() {
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
   public double putIfAbsent(long var1, double var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TLongDoubleProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(long var1, double var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double adjustOrPutValue(long var1, double var3, double var5) {
      throw new UnsupportedOperationException();
   }
}
