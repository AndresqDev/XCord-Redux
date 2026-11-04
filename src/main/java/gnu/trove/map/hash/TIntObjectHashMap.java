package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
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

public class TIntObjectHashMap<V> extends TIntHash implements TIntObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TIntObjectProcedure<V> PUT_ALL_PROC = new TIntObjectProcedure<V>() {
      @Override
      public boolean execute(int var1, V var2) {
         TIntObjectHashMap.this.put(var1, var2);
         return true;
      }
   };
   protected transient V[] _values;
   protected int no_entry_key;

   public TIntObjectHashMap() {
   }

   public TIntObjectHashMap(int var1) {
      super(var1);
      this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
   }

   public TIntObjectHashMap(int var1, float var2) {
      super(var1, var2);
      this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
   }

   public TIntObjectHashMap(int var1, float var2, int var3) {
      super(var1, var2);
      this.no_entry_key = var3;
   }

   public TIntObjectHashMap(TIntObjectMap<? extends V> var1) {
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
      int[] var3 = this._set;
      Object[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new int[var1];
      this._values = (V[])(new Object[var1]);
      this._states = new byte[var1];
      int var6 = var2;

      while (var6-- > 0) {
         if (var5[var6] == 1) {
            int var7 = var3[var6];
            int var8 = this.insertKey(var7);
            this._values[var8] = (V)var4[var6];
         }
      }
   }

   @Override
   public int getNoEntryKey() {
      return this.no_entry_key;
   }

   @Override
   public boolean containsKey(int var1) {
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
   public V get(int var1) {
      int var2 = this.index(var1);
      return var2 < 0 ? null : this._values[var2];
   }

   @Override
   public V put(int var1, V var2) {
      int var3 = this.insertKey(var1);
      return this.doPut((V)var2, var3);
   }

   @Override
   public V putIfAbsent(int var1, V var2) {
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
   public V remove(int var1) {
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
   public void putAll(Map<? extends Integer, ? extends V> var1) {
      for (Entry var4 : var1.entrySet()) {
         this.put((Integer)var4.getKey(), (V)var4.getValue());
      }
   }

   @Override
   public void putAll(TIntObjectMap<? extends V> var1) {
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
   public TIntSet keySet() {
      return new TIntObjectHashMap.KeyView();
   }

   @Override
   public int[] keys() {
      int[] var1 = new int[this.size()];
      int[] var2 = this._set;
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
   public int[] keys(int[] var1) {
      if (var1.length < this._size) {
         var1 = new int[this._size];
      }

      int[] var2 = this._set;
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
      return new TIntObjectHashMap.ValueView();
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
   public TIntObjectIterator<V> iterator() {
      return new TIntObjectHashMap.TIntObjectHashIterator<>(this);
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
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
   public boolean forEachEntry(TIntObjectProcedure<? super V> var1) {
      byte[] var2 = this._states;
      int[] var3 = this._set;
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
   public boolean retainEntries(TIntObjectProcedure<? super V> var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      int[] var4 = this._set;
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
      if (!(var1 instanceof TIntObjectMap)) {
         return false;
      } else {
         TIntObjectMap var2 = (TIntObjectMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            try {
               TIntObjectIterator var3 = this.iterator();

               while (var3.hasNext()) {
                  var3.advance();
                  int var4 = var3.key();
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
      var1.writeInt(this.no_entry_key);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while (var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeInt(this._set[var2]);
            var1.writeObject(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      this.no_entry_key = var1.readInt();
      int var2 = var1.readInt();
      this.setUp(var2);

      while (var2-- > 0) {
         int var3 = var1.readInt();
         Object var4 = var1.readObject();
         this.put(var3, (V)var4);
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TIntObjectProcedure<V>() {
         private boolean first = true;

         @Override
         public boolean execute(int var1x, Object var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(",");
            }

            var1.append(var1x);
            var1.append("=");
            var1.append(var2);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   class KeyView implements TIntSet {
      @Override
      public int getNoEntryValue() {
         return TIntObjectHashMap.this.no_entry_key;
      }

      @Override
      public int size() {
         return TIntObjectHashMap.this._size;
      }

      @Override
      public boolean isEmpty() {
         return TIntObjectHashMap.this._size == 0;
      }

      @Override
      public boolean contains(int var1) {
         return TIntObjectHashMap.this.containsKey(var1);
      }

      @Override
      public TIntIterator iterator() {
         return new TIntObjectHashMap.KeyView.TIntHashIterator(TIntObjectHashMap.this);
      }

      @Override
      public int[] toArray() {
         return TIntObjectHashMap.this.keys();
      }

      @Override
      public int[] toArray(int[] var1) {
         return TIntObjectHashMap.this.keys(var1);
      }

      @Override
      public boolean add(int var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(int var1) {
         return null != TIntObjectHashMap.this.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         for (Object var3 : var1) {
            if (!TIntObjectHashMap.this.containsKey((Integer)var3)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean containsAll(TIntCollection var1) {
         if (var1 == this) {
            return true;
         } else {
            TIntIterator var2 = var1.iterator();

            while (var2.hasNext()) {
               if (!TIntObjectHashMap.this.containsKey(var2.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public boolean containsAll(int[] var1) {
         for (int var5 : var1) {
            if (!TIntObjectHashMap.this.containsKey(var5)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean addAll(Collection<? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(TIntCollection var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         TIntIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(TIntCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TIntIterator var3 = this.iterator();

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
      public boolean retainAll(int[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         int[] var3 = TIntObjectHashMap.this._set;
         byte[] var4 = TIntObjectHashMap.this._states;
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               TIntObjectHashMap.this.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         boolean var2 = false;

         for (Object var4 : var1) {
            if (var4 instanceof Integer) {
               int var5 = (Integer)var4;
               if (this.remove(var5)) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      @Override
      public boolean removeAll(TIntCollection var1) {
         if (var1 == this) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TIntIterator var3 = var1.iterator();

            while (var3.hasNext()) {
               int var4 = var3.next();
               if (this.remove(var4)) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(int[] var1) {
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
         TIntObjectHashMap.this.clear();
      }

      @Override
      public boolean forEach(TIntProcedure var1) {
         return TIntObjectHashMap.this.forEachKey(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof TIntSet)) {
            return false;
         } else {
            TIntSet var2 = (TIntSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = TIntObjectHashMap.this._states.length;

               while (var3-- > 0) {
                  if (TIntObjectHashMap.this._states[var3] == 1 && !var2.contains(TIntObjectHashMap.this._set[var3])) {
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
         int var2 = TIntObjectHashMap.this._states.length;

         while (var2-- > 0) {
            if (TIntObjectHashMap.this._states[var2] == 1) {
               var1 += HashFunctions.hash(TIntObjectHashMap.this._set[var2]);
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("{");
         boolean var2 = true;
         int var3 = TIntObjectHashMap.this._states.length;

         while (var3-- > 0) {
            if (TIntObjectHashMap.this._states[var3] == 1) {
               if (var2) {
                  var2 = false;
               } else {
                  var1.append(",");
               }

               var1.append(TIntObjectHashMap.this._set[var3]);
            }
         }

         return var1.toString();
      }

      class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {
         private final TIntHash _hash;

         public TIntHashIterator(TIntHash var2) {
            super(var2);
            this._hash = var2;
         }

         @Override
         public int next() {
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
         TIntObjectHashMap.this.clear();
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return TIntObjectHashMap.this.size();
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
         return TIntObjectHashMap.this.isEmpty();
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

   class TIntObjectHashIterator<V> extends THashPrimitiveIterator implements TIntObjectIterator<V> {
      private final TIntObjectHashMap<V> _map;

      public TIntObjectHashIterator(TIntObjectHashMap<V> var2) {
         super(var2);
         this._map = var2;
      }

      @Override
      public void advance() {
         this.moveToNextIndex();
      }

      @Override
      public int key() {
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

   protected class ValueView extends TIntObjectHashMap<V>.MapBackedView<V> {
      @Override
      public Iterator<V> iterator() {
         return new TIntObjectHashMap<V>.ValueView.TIntObjectValueHashIterator(TIntObjectHashMap.this) {
            @Override
            protected V objectAtIndex(int var1) {
               return TIntObjectHashMap.this._values[var1];
            }
         };
      }

      @Override
      public boolean containsElement(V var1) {
         return TIntObjectHashMap.this.containsValue(var1);
      }

      @Override
      public boolean removeElement(V var1) {
         Object[] var2 = TIntObjectHashMap.this._values;
         byte[] var3 = TIntObjectHashMap.this._states;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] == 1 && (var1 == var2[var4] || null != var2[var4] && var2[var4].equals(var1))) {
               TIntObjectHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }

      class TIntObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TIntObjectHashMap _map;

         public TIntObjectValueHashIterator(TIntObjectHashMap var2) {
            super(var2);
            this._map = var2;
         }

         protected V objectAtIndex(int var1) {
            byte[] var2 = TIntObjectHashMap.this._states;
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
