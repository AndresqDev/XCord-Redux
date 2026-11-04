package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleLongMap implements TDoubleLongMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TDoubleLongMap m;
   private transient TDoubleSet keySet = null;
   private transient TLongCollection values = null;

   public TUnmodifiableDoubleLongMap(TDoubleLongMap var1) {
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
   public boolean containsValue(long var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public long get(double var1) {
      return this.m.get(var1);
   }

   @Override
   public long put(double var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TDoubleLongMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Long> var1) {
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
   public TLongCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public long[] values() {
      return this.m.values();
   }

   @Override
   public long[] values(long[] var1) {
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
   public long getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TDoubleLongProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TDoubleLongIterator iterator() {
      return new TDoubleLongIterator() {
         TDoubleLongIterator iter = TUnmodifiableDoubleLongMap.this.m.iterator();

         @Override
         public double key() {
            return this.iter.key();
         }

         @Override
         public long value() {
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
         public long setValue(long var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public long putIfAbsent(double var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TDoubleLongProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(double var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long adjustOrPutValue(double var1, long var3, long var5) {
      throw new UnsupportedOperationException();
   }
}
