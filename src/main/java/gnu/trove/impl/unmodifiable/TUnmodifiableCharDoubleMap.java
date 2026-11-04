package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharDoubleMap implements TCharDoubleMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TCharDoubleMap m;
   private transient TCharSet keySet = null;
   private transient TDoubleCollection values = null;

   public TUnmodifiableCharDoubleMap(TCharDoubleMap var1) {
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
   public boolean containsValue(double var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public double get(char var1) {
      return this.m.get(var1);
   }

   @Override
   public double put(char var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TCharDoubleMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Double> var1) {
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
   public char getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public double getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TDoubleProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TCharDoubleProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TCharDoubleIterator iterator() {
      return new TCharDoubleIterator() {
         TCharDoubleIterator iter = TUnmodifiableCharDoubleMap.this.m.iterator();

         @Override
         public char key() {
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
   public double putIfAbsent(char var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TCharDoubleProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(char var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double adjustOrPutValue(char var1, double var2, double var4) {
      throw new UnsupportedOperationException();
   }
}
