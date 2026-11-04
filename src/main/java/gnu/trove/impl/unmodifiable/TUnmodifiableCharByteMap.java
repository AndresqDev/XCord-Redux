package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharByteMap implements TCharByteMap, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TCharByteMap m;
   private transient TCharSet keySet = null;
   private transient TByteCollection values = null;

   public TUnmodifiableCharByteMap(TCharByteMap var1) {
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
   public boolean containsValue(byte var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public byte get(char var1) {
      return this.m.get(var1);
   }

   @Override
   public byte put(char var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TCharByteMap var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Byte> var1) {
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
   public char getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TCharProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TByteProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TCharByteProcedure var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TCharByteIterator iterator() {
      return new TCharByteIterator() {
         TCharByteIterator iter = TUnmodifiableCharByteMap.this.m.iterator();

         @Override
         public char key() {
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
   public byte putIfAbsent(char var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TCharByteProcedure var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(char var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte adjustOrPutValue(char var1, byte var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
