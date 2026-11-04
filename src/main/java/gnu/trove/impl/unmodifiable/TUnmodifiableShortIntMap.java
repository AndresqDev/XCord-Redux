package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortIntMap implements TShortIntMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TShortIntMap m;
   private transient TShortSet keySet = null;
   private transient TIntCollection values = null;

   public TUnmodifiableShortIntMap(TShortIntMap var1) {
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
   public boolean containsKey(short var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(int var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public int get(short var1) {
      return this.m.get(var1);
   }

   @Override
   public int put(short var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int remove(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TShortIntMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Integer> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TShortSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public short[] keys() {
      return this.m.keys();
   }

   @Override
   public short[] keys(short[] var1) {
      return this.m.keys(var1);
   }

   @Override
   public TIntCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public int[] values() {
      return this.m.values();
   }

   @Override
   public int[] values(int[] var1) {
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
   public short getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public int getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TShortProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TIntProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TShortIntProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TShortIntIterator iterator() {
      return new TShortIntIterator() {
         TShortIntIterator iter = TUnmodifiableShortIntMap.this.m.iterator();

         @Override
         public short key() {
            return this.iter.key();
         }

         @Override
         public int value() {
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
         public int setValue(int var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public int putIfAbsent(short var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TIntFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TShortIntProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(short var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int adjustOrPutValue(short var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }
}
