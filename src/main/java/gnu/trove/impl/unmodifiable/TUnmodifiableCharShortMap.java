package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharShortMap implements TCharShortMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TCharShortMap m;
   private transient TCharSet keySet = null;
   private transient TShortCollection values = null;

   public TUnmodifiableCharShortMap(TCharShortMap var1) {
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
   public boolean containsValue(short var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public short get(char var1) {
      return this.m.get(var1);
   }

   @Override
   public short put(char var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TCharShortMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Short> var1) {
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
   public char getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TCharShortProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TCharShortIterator iterator() {
      return new TCharShortIterator() {
         TCharShortIterator iter = TUnmodifiableCharShortMap.this.m.iterator();

         @Override
         public char key() {
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
   public short putIfAbsent(char var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TCharShortProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(char var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short adjustOrPutValue(char var1, short var2, short var3) {
      throw new UnsupportedOperationException();
   }
}
