package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntLongMap implements TIntLongMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TIntLongMap m;
   private transient TIntSet keySet = null;
   private transient TLongCollection values = null;

   public TUnmodifiableIntLongMap(TIntLongMap var1) {
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
   public boolean containsValue(long var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public long get(int var1) {
      return this.m.get(var1);
   }

   @Override
   public long put(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long remove(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TIntLongMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Long> var1) {
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
   public int getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public long getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TIntLongProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TIntLongIterator iterator() {
      return new TIntLongIterator() {
         TIntLongIterator iter = TUnmodifiableIntLongMap.this.m.iterator();

         @Override
         public int key() {
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
   public long putIfAbsent(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TIntLongProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long adjustOrPutValue(int var1, long var2, long var4) {
      throw new UnsupportedOperationException();
   }
}
