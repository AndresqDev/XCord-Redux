package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectCharMap<K> implements TObjectCharMap<K>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TObjectCharMap<K> m;
   private transient Set<K> keySet = null;
   private transient TCharCollection values = null;

   public TUnmodifiableObjectCharMap(TObjectCharMap<K> var1) {
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
   public boolean containsValue(char var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public char get(Object var1) {
      return this.m.get(var1);
   }

   @Override
   public char put(K var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TObjectCharMap<? extends K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends Character> var1) {
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
   public char getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TCharProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TObjectCharProcedure<? super K> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TObjectCharIterator<K> iterator() {
      return new TObjectCharIterator<K>() {
         TObjectCharIterator<K> iter = TUnmodifiableObjectCharMap.this.m.iterator();

         @Override
         public K key() {
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
   public char putIfAbsent(K var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TCharFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TObjectCharProcedure<? super K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(K var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(K var1, char var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char adjustOrPutValue(K var1, char var2, char var3) {
      throw new UnsupportedOperationException();
   }
}
