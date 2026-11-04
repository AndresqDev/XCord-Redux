package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectLongMap<K> implements TObjectLongMap<K>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TObjectLongMap<K> m;
   private transient Set<K> keySet = null;
   private transient TLongCollection values = null;

   public TUnmodifiableObjectLongMap(TObjectLongMap<K> var1) {
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
   public boolean containsValue(long var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public long get(Object var1) {
      return this.m.get(var1);
   }

   @Override
   public long put(K var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TObjectLongMap<? extends K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends Long> var1) {
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
   public long getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TLongProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TObjectLongProcedure<? super K> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TObjectLongIterator<K> iterator() {
      return new TObjectLongIterator<K>() {
         TObjectLongIterator<K> iter = TUnmodifiableObjectLongMap.this.m.iterator();

         @Override
         public K key() {
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
   public long putIfAbsent(K var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TObjectLongProcedure<? super K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(K var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(K var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long adjustOrPutValue(K var1, long var2, long var4) {
      throw new UnsupportedOperationException();
   }
}
