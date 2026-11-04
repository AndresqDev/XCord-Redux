package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongLongMap implements TLongLongMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TLongLongMap m;
   private transient TLongSet keySet = null;
   private transient TLongCollection values = null;

   public TUnmodifiableLongLongMap(TLongLongMap var1) {
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
   public boolean containsValue(long var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public long get(long var1) {
      return this.m.get(var1);
   }

   @Override
   public long put(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TLongLongMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Long> var1) {
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
   public long getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public long getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TLongLongProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TLongLongIterator iterator() {
      return new TLongLongIterator() {
         TLongLongIterator iter = TUnmodifiableLongLongMap.this.m.iterator();

         @Override
         public long key() {
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
   public long putIfAbsent(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TLongLongProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long adjustOrPutValue(long var1, long var3, long var5) {
      throw new UnsupportedOperationException();
   }
}
