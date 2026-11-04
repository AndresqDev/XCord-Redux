package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatCharMap implements TFloatCharMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TFloatCharMap m;
   private transient TFloatSet keySet = null;
   private transient TCharCollection values = null;

   public TUnmodifiableFloatCharMap(TFloatCharMap var1) {
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
   public boolean containsKey(float var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(char var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public char get(float var1) {
      return this.m.get(var1);
   }

   @Override
   public char put(float var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TFloatCharMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Character> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TFloatSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public float[] keys() {
      return this.m.keys();
   }

   @Override
   public float[] keys(float[] var1) {
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
   public float getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public char getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TCharProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TFloatCharProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TFloatCharIterator iterator() {
      return new TFloatCharIterator() {
         TFloatCharIterator iter = TUnmodifiableFloatCharMap.this.m.iterator();

         @Override
         public float key() {
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
   public char putIfAbsent(float var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TCharFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TFloatCharProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(float var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char adjustOrPutValue(float var1, char var2, char var3) {
      throw new UnsupportedOperationException();
   }
}
