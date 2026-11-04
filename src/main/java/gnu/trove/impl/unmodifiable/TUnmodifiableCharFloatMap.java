package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharFloatMap implements TCharFloatMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TCharFloatMap m;
   private transient TCharSet keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableCharFloatMap(TCharFloatMap var1) {
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
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(char var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(char var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TCharFloatMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Float> var1) {
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
   public TFloatCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public float[] values() {
      return this.m.values();
   }

   @Override
   public float[] values(float[] var1) {
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
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TCharFloatProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TCharFloatIterator iterator() {
      return new TCharFloatIterator() {
         TCharFloatIterator iter = TUnmodifiableCharFloatMap.this.m.iterator();

         @Override
         public char key() {
            return this.iter.key();
         }

         @Override
         public float value() {
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
         public float setValue(float var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public float putIfAbsent(char var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TCharFloatProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(char var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(char var1, float var2, float var3) {
      throw new UnsupportedOperationException();
   }
}
