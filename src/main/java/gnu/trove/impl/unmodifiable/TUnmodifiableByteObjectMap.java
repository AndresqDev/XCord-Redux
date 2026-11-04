package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableByteObjectMap<V> implements TByteObjectMap<V>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TByteObjectMap<V> m;
   private transient TByteSet keySet = null;
   private transient Collection<V> values = null;

   public TUnmodifiableByteObjectMap(TByteObjectMap<V> var1) {
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
   public boolean containsKey(byte var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public V get(byte var1) {
      return this.m.get(var1);
   }

   @Override
   public V put(byte var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TByteObjectMap<? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TByteSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public byte[] keys() {
      return this.m.keys();
   }

   @Override
   public byte[] keys(byte[] var1) {
      return this.m.keys(var1);
   }

   @Override
   public Collection<V> valueCollection() {
      if (this.values == null) {
         this.values = Collections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public Object[] values() {
      return this.m.values();
   }

   @Override
   public V[] values(V[] var1) {
      return this.m.values((V[])var1);
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
   public byte getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TByteObjectProcedure<? super V> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TByteObjectIterator<V> iterator() {
      return new TByteObjectIterator<V>() {
         TByteObjectIterator<V> iter = TUnmodifiableByteObjectMap.this.m.iterator();

         @Override
         public byte key() {
            return this.iter.key();
         }

         @Override
         public V value() {
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
         public V setValue(V var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public V putIfAbsent(byte var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TByteObjectProcedure<? super V> var1) {
      throw new UnsupportedOperationException();
   }
}
