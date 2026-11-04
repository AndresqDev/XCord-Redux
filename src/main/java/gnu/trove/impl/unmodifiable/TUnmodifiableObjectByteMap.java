package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectByteMap<K> implements TObjectByteMap<K>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TObjectByteMap<K> m;
   private transient Set<K> keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableObjectByteMap(TObjectByteMap<K> var1) {
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
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(Object var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(K var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TObjectByteMap<? extends K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends Byte> var1) {
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
   public TByteCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public byte[] values() {
      return this.m.values();
   }

   @Override
   public byte[] values(byte[] var1) {
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
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TObjectByteProcedure<? super K> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TObjectByteIterator<K> iterator() {
      return new TObjectByteIterator<K>() {
         TObjectByteIterator<K> iter = TUnmodifiableObjectByteMap.this.m.iterator();

         @Override
         public K key() {
            return this.iter.key();
         }

         @Override
         public byte value() {
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
         public byte setValue(byte var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public byte putIfAbsent(K var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TObjectByteProcedure<? super K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(K var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(K var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(K var1, byte var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
