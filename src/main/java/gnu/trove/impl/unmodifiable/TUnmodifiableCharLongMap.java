package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharLongMap implements TCharLongMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TCharLongMap m;
   private transient TCharSet keySet = null;
   private transient TLongCollection values = null;

   public TUnmodifiableCharLongMap(TCharLongMap var1) {
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
   public boolean containsKey(char var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(long var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public long get(char var1) {
      return this.m.get(var1);
   }

   @Override
   public long put(char var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TCharLongMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Long> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TCharSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public char[] keys() {
      return this.m.keys();
   }

   @Override
   public char[] keys(char[] var1) {
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
   public char getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public long getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TCharLongProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TCharLongIterator iterator() {
      return new TCharLongIterator() {
         TCharLongIterator iter = TUnmodifiableCharLongMap.this.m.iterator();

         @Override
         public char key() {
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
   public long putIfAbsent(char var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TCharLongProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(char var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long adjustOrPutValue(char var1, long var2, long var4) {
      throw new UnsupportedOperationException();
   }
}
