package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleCharMap implements TDoubleCharMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TDoubleCharMap m;
   private transient TDoubleSet keySet = null;
   private transient TCharCollection values = null;

   public TUnmodifiableDoubleCharMap(TDoubleCharMap var1) {
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
   public boolean containsValue(char var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public char get(double var1) {
      return this.m.get(var1);
   }

   @Override
   public char put(double var1, char var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TDoubleCharMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Character> var1) {
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
   public TCharCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public char[] values() {
      return this.m.values();
   }

   @Override
   public char[] values(char[] var1) {
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
   public char getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TCharProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TDoubleCharProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TDoubleCharIterator iterator() {
      return new TDoubleCharIterator() {
         TDoubleCharIterator iter = TUnmodifiableDoubleCharMap.this.m.iterator();

         @Override
         public double key() {
            return this.iter.key();
         }

         @Override
         public char value() {
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
         public char setValue(char var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public char putIfAbsent(double var1, char var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TCharFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TDoubleCharProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(double var1, char var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char adjustOrPutValue(double var1, char var3, char var4) {
      throw new UnsupportedOperationException();
   }
}
