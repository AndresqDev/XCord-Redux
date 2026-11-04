package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TByteObjectHashMap<V> extends TByteHash implements TByteObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TByteObjectProcedure<V> PUT_ALL_PROC = new TByteObjectProcedure<V>() {
      @Override
      public boolean execute(byte var1, V var2) {
         TByteObjectHashMap.this.put(var1, var2);
         return true;
      }
   };
   protected transient V[] _values;
   protected byte no_entry_key;

   public TByteObjectHashMap() {
   }

   public TByteObjectHashMap(int var1) {
      super(var1);
      this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
   }

   public TByteObjectHashMap(int var1, float var2) {
      super(var1, var2);
      this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
   }

   public TByteObjectHashMap(int var1, float var2, byte var3) {
      super(var1, var2);
      this.no_entry_key = var3;
   }

   public TByteObjectHashMap(TByteObjectMap<? extends V> var1) {
      this(var1.size(), 0.5F, var1.getNoEntryKey());
      this.putAll(var1);
   }

   @Override
   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = (V[])(new Object[var2]);
      return var2;
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      byte[] var3 = this._set;
      Object[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new byte[var1];
      this._values = (V[])(new Object[var1]);
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            byte var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = (V)var4[var6];
         }
      }
   }

   @Override
   public byte getNoEntryKey() {
      return this.no_entry_key;
   }

   @Override
   public boolean containsKey(byte var1) {
      return this.contains(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      byte[] var2 = this._states;
      Object[] var3 = this._values;
      if (null == var1) {
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var2[var5] == 1 && null == var3[var5]) {
               return true;
            }
         }
      } else {
         int var4 = var3.length;

         while (var4-- > 0) {
            if (var2[var4] == 1 && (var1 == var3[var4] || var1.equals(var3[var4]))) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public V get(byte var1) {
      int var2 = this.index(var1);
      return var2 < 0 ? null : this._values[var2];
   }

   @Override
   public V put(byte var1, V var2) {
      int var3 = this.insertKey(var1);
      return this.doPut((V)var2, var3);
   }

   @Override
   public V putIfAbsent(byte var1, V var2) {
      int var3 = this.insertKey(var1);
      return var3 < 0 ? this._values[-var3 - 1] : this.doPut((V)var2, var3);
   }

   private V doPut(V var1, int var2) {
      Object var3 = null;
      boolean var4 = true;
      if (var2 < 0) {
         var2 = -var2 - 1;
         var3 = this._values[var2];
         var4 = false;
      }

      this._values[var2] = (V)var1;
      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return (V)var3;
   }

   @Override
   public V remove(byte var1) {
      Object var2 = null;
      int var3 = this.index(var1);
      if (var3 >= 0) {
         var2 = this._values[var3];
         this.removeAt(var3);
      }

      return (V)var2;
   }

   @Override
   protected void removeAt(int var1) {
      this._values[var1] = null;
      super.removeAt(var1);
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends V> var1) {
      for (Entry var4 : var1.entrySet()) {
         this.put((Byte)var4.getKey(), (V)var4.getValue());
      }
   }

   @Override
   public void putAll(TByteObjectMap<? extends V> var1) {
      var1.forEachEntry(this.PUT_ALL_PROC);
   }

   @Override
   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, null);
   }

   @Override
   public TByteSet keySet() {
      return new TByteObjectHashMap.KeyView();
   }

   @Override
   public byte[] keys() {
      byte[] var1 = new byte[this.size()];
      byte[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   @Override
   public byte[] keys(byte[] var1) {
      if (var1.length < this._size) {
         var1 = new byte[this._size];
      }

      byte[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   @Override
   public Collection<V> valueCollection() {
      return new TByteObjectHashMap.ValueView();
   }

   @Override
   public Object[] values() {
      Object[] var1 = new Object[this.size()];
      Object[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   @Override
   public V[] values(V[] var1) {
      if (var1.length < this._size) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), this._size);
      }

      Object[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return (V[])var1;
   }

   @Override
   public TByteObjectIterator<V> iterator() {
      return new TByteObjectHashMap.TByteObjectHashIterator<>(this);
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      byte[] var2 = this._states;
      Object[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1 && !var1.execute(var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TByteObjectProcedure<? super V> var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._set;
      Object[] var4 = this._values;
      int var5 = var3.length;

      while (var5-- > 0) {
         if (var2[var5] == 1 && !var1.execute(var3[var5], var4[var5])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean retainEntries(TByteObjectProcedure<? super V> var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      byte[] var4 = this._set;
      Object[] var5 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var6 = var4.length;

         while (var6-- > 0) {
            if (var3[var6] == 1 && !var1.execute(var4[var6], var5[var6])) {
               this.removeAt(var6);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      byte[] var2 = this._states;
      Object[] var3 = this._values;
      int var4 = var3.length;

      while (var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof TByteObjectMap)) {
         return false;
      } else {
         TByteObjectMap var2 = (TByteObjectMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               TByteObjectIterator var3 = this.iterator();

               while (var3.hasNext()) {
                  var3.advance();
                  byte var4 = var3.key();
                  Object var5 = var3.value();
                  if (var5 == null) {
                     if (var2.get(var4) != null || !var2.containsKey(var4)) {
                        return false;
                     }
                  } else if (!var5.equals(var2.get(var4))) {
                     return false;
                  }
               }
            } catch (ClassCastException var6) {
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      Object[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var3[var4] == 1) {
            var1 += HashFunctions.hash(this._set[var4]) ^ (var2[var4] == null ? 0 : var2[var4].hashCode());
         }
      }

      return var1;
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeByte(this.no_entry_key);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeByte(this._set[var2]);
            var1.writeObject(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      this.no_entry_key = var1.readByte();
      int var2 = var1.readInt();
      this.setUp(var2);

      while (var2-- > 0) {
         byte var3 = var1.readByte();
         Object var4 = var1.readObject();
         this.put(var3, (V)var4);
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TByteObjectProcedure<V>() {
         private boolean first = true;

         @Override
         public boolean execute(byte var1x, Object var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(",");
            }

            var1.append((int)var1x);
            var1.append("=");
            var1.append(var2);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   class KeyView implements TByteSet {
      @Override
      public byte getNoEntryValue() {
         return TByteObjectHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TByteObjectHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return TByteObjectHashMap.this._size == 0;
      }

      @Override
      public boolean contains(byte var1) {
         return TByteObjectHashMap.this.containsKey(var1);
      }

      @Override
      public TByteIterator iterator() {
         return new TByteObjectHashMap.KeyView.TByteHashIterator(TByteObjectHashMap.this);
      }

      @Override
      public byte[] toArray() {
         return TByteObjectHashMap.this.keys();
      }

      @Override
      public byte[] toArray(byte[] var1) {
         return TByteObjectHashMap.this.keys(var1);
      }

      @Override
      public boolean add(byte var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(byte var1) {
         return null != TByteObjectHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!TByteObjectHashMap.this.containsKey((Byte)var3)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TByteCollection var1) {
         if (var1 == this) {
            return true;
         } else {
            TByteIterator var2 = var1.iterator();

            while (var2.hasNext()) {
               if (!TByteObjectHashMap.this.containsKey(var2.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public boolean containsAll(byte[] var1) {
         for (byte var5 : var1) {
            if (!TByteObjectHashMap.this.containsKey(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(byte[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TByteIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TByteCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TByteIterator var3 = this.iterator();

            while (var3.hasNext()) {
               if (!var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean retainAll(byte[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         byte[] var3 = TByteObjectHashMap.this._set;
         byte[] var4 = TByteObjectHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TByteObjectHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Byte) {
               byte var5 = (Byte)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TByteCollection var1) {
         if (var1 == this) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TByteIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               byte var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(byte[] var1) {
         boolean var2 = false;
         int var3 = var1.length;

         while (var3-- > 0) {
            if (this.remove(var1[var3])) {
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public void clear() {
         TByteObjectHashMap.this.clear();
      }

      @Override
      public boolean forEach(TByteProcedure var1) {
         return TByteObjectHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TByteSet)) {
            return false;
         } else {
            TByteSet var2 = (TByteSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TByteObjectHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TByteObjectHashMap.this._states[var3] == 1 && !var2.contains(TByteObjectHashMap.this._set[var3])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      @Override
      public int hashCode() {
         int var1 = 0;
         int var2 = TByteObjectHashMap.this._states.length;

         while (var2-- > 0) {
            if (TByteObjectHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TByteObjectHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("{");
         boolean var2 = true;
         int var3 = TByteObjectHashMap.this._states.length;

         while (var3-- > 0) {
            if (TByteObjectHashMap.this._states[var3] == 1) {
               if (var2) {
                  var2 = false;
               } else {
                  var1.append(",");
               }

               var1.append(TByteObjectHashMap.this._set[var3]);
            }
         }

         return var1.toString();
      }

      class TByteHashIterator extends THashPrimitiveIterator implements TByteIterator {
         private final TByteHash _hash;

         public TByteHashIterator(TByteHash var2) {
            super(var2);
            this._hash = var2;
         }

         @Override
         public byte next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
         }
      }
   }

   private abstract class MapBackedView<E> extends AbstractSet<E> implements Iterable<E>, Set<E> {
      private MapBackedView() {
      }

      @Override
      public abstract Iterator<E> iterator();

      public abstract boolean removeElement(E var1);

      public abstract boolean containsElement(E var1);

      @Override
      public boolean contains(Object var1) {
         return this.containsElement((E)var1);
      }

      @Override
      public boolean remove(Object var1) {
         return this.removeElement((E)var1);
      }

      @Override
      public void clear() {
         TByteObjectHashMap.this.clear();
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return TByteObjectHashMap.this.size();
      }

      @Override
      public Object[] toArray() {
         Object[] var1 = new Object[this.size()];
         Iterator var2 = this.iterator();

         for (int var3 = 0; var2.hasNext(); var3++) {
            var1[var3] = var2.next();
         }

         return var1;
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         int var2 = this.size();
         if (var1.length < var2) {
            var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
         }

         Iterator var3 = this.iterator();
         Object[] var4 = var1;

         for (int var5 = 0; var5 < var2; var5++) {
            var4[var5] = var3.next();
         }

         if (var1.length > var2) {
            var1[var2] = null;
         }

         return (T[])var1;
      }

      @Override
      public boolean isEmpty() {
         return TByteObjectHashMap.this.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   class TByteObjectHashIterator<V> extends THashPrimitiveIterator implements TByteObjectIterator<V> {
      private final TByteObjectHashMap<V> _map;

      public TByteObjectHashIterator(TByteObjectHashMap<V> var2) {
         super(var2);
         this._map = var2;
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public byte key() {
         return this._map._set[this._index];
      }

      @Override
      public V value() {
         return this._map._values[this._index];
      }

      @Override
      public V setValue(V var1) {
         Object var2 = this.value();
         this._map._values[this._index] = (V)var1;
         return (V)var2;
      }
   }

   protected class ValueView extends TByteObjectHashMap<V>.MapBackedView<V> {
      @Override
      public Iterator<V> iterator() {
         return new TByteObjectHashMap<V>.ValueView.TByteObjectValueHashIterator(TByteObjectHashMap.this) {
            @Override
            protected V objectAtIndex(int var1) {
               return TByteObjectHashMap.this._values[var1];
            }
         };
      }

      @Override
      public boolean containsElement(V var1) {
         return TByteObjectHashMap.this.containsValue(var1);
      }

      @Override
      public boolean removeElement(V var1) {
         Object[] var2 = TByteObjectHashMap.this._values;
         byte[] var3 = TByteObjectHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] == 1 && (var1 == var2[var4] || null != var2[var4] && var2[var4].equals(var1))) {
               TByteObjectHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      class TByteObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TByteObjectHashMap _map;

         public TByteObjectValueHashIterator(TByteObjectHashMap var2) {
            super(var2);
            this._map = var2;
         }

         protected V objectAtIndex(int var1) {
            byte[] var2 = TByteObjectHashMap.this._states;
            Object var3 = this._map._values[var1];
            return (V)(var2[var1] != 1 ? null : var3);
         }

         @Override
         public V next() {
            this.moveToNextIndex();
            return this._map._values[this._index];
         }
      }
   }
}
