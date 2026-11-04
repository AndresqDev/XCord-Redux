package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongShortMap implements TLongShortMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TLongShortMap m;
   private transient TLongSet keySet = null;
   private transient TShortCollection values = null;

   public TUnmodifiableLongShortMap(TLongShortMap var1) {
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
   public boolean containsValue(short var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public short get(long var1) {
      return this.m.get(var1);
   }

   @Override
   public short put(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TLongShortMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Long, ? extends Short> var1) {
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
   public long getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TLongShortProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TLongShortIterator iterator() {
      return new TLongShortIterator() {
         TLongShortIterator iter = TUnmodifiableLongShortMap.this.m.iterator();

         @Override
         public long key() {
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
   public short putIfAbsent(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TLongShortProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(long var1, short var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short adjustOrPutValue(long var1, short var3, short var4) {
      throw new UnsupportedOperationException();
   }
}
