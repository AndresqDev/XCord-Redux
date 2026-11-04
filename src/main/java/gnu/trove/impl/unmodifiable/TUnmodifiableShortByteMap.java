package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortByteMap implements TShortByteMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TShortByteMap m;
   private transient TShortSet keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableShortByteMap(TShortByteMap var1) {
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
   public boolean containsKey(short var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(short var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(short var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TShortByteMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Byte> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TShortSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public short[] keys() {
      return this.m.keys();
   }

   @Override
   public short[] keys(short[] var1) {
      return this.m.keys(var1);
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
   public short getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TShortProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TShortByteProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TShortByteIterator iterator() {
      return new TShortByteIterator() {
         TShortByteIterator iter = TUnmodifiableShortByteMap.this.m.iterator();

         @Override
         public short key() {
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
   public byte putIfAbsent(short var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TShortByteProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(short var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(short var1, byte var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
