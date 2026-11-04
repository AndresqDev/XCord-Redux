package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectIntMap<K> implements TObjectIntMap<K>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TObjectIntMap<K> m;
   private transient Set<K> keySet = null;
   private transient TIntCollection values = null;

   public TUnmodifiableObjectIntMap(TObjectIntMap<K> var1) {
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
   public boolean containsKey(Object var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(int var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public int get(Object var1) {
      return this.m.get(var1);
   }

   @Override
   public int put(K var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TObjectIntMap<? extends K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends Integer> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = Collections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public Object[] keys() {
      return this.m.keys();
   }

   @Override
   public K[] keys(K[] var1) {
      return this.m.keys((K[])var1);
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
   public int getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TIntProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TObjectIntProcedure<? super K> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TObjectIntIterator<K> iterator() {
      return new TObjectIntIterator<K>() {
         TObjectIntIterator<K> iter = TUnmodifiableObjectIntMap.this.m.iterator();

         @Override
         public K key() {
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
   public int putIfAbsent(K var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TIntFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TObjectIntProcedure<? super K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(K var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(K var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int adjustOrPutValue(K var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }
}
