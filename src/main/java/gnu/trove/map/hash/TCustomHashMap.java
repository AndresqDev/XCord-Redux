package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TCustomHashMap<K, V> extends TCustomObjectHash<K> implements TMap<K, V>, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient V[] _values;

   public TCustomHashMap() {
   }

   public TCustomHashMap(HashingStrategy<? super K> var1) {
      super(var1);
   }

   public TCustomHashMap(HashingStrategy<? super K> var1, int var2) {
      super(var1, var2);
   }

   public TCustomHashMap(HashingStrategy<? super K> var1, int var2, float var3) {
      super(var1, var2, var3);
   }

   public TCustomHashMap(HashingStrategy<? super K> var1, Map<? extends K, ? extends V> var2) {
      this(var1, var2.size());
      this.putAll(var2);
   }

   public TCustomHashMap(HashingStrategy<? super K> var1, TCustomHashMap<? extends K, ? extends V> var2) {
      this(var1, var2.size());
      this.putAll(var2);
   }

   @Override
   public int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = (V[])(new Object[var2]);
      return var2;
   }

   @Override
   public V put(K var1, V var2) {
      int var3 = this.insertKey((K)var1);
      return this.doPut((V)var2, var3);
   }

   @Override
   public V putIfAbsent(K var1, V var2) {
      int var3 = this.insertKey((K)var1);
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
   public boolean equals(Object var1) {
      if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         return var2.size() != this.size() ? false : this.forEachEntry(new TCustomHashMap.EqProcedure<>(var2));
      }
   }

   @Override
   public int hashCode() {
      TCustomHashMap.HashProcedure var1 = new TCustomHashMap.HashProcedure();
      this.forEachEntry(var1);
      return var1.getHashCode();
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TObjectObjectProcedure<K, V>() {
         private boolean first = true;

         @Override
         public boolean execute(K var1x, V var2) {
            if (this.first) {
               this.first = false;
            } else {
               var1.append(", ");
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

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.forEach(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      Object[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED && !var1.execute(var2[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> var1) {
      Object[] var2 = this._set;
      Object[] var3 = this._values;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var2[var4] != FREE && var2[var4] != REMOVED && !var1.execute(var2[var4], var3[var4])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      Object[] var4 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var3[var5] != FREE && var3[var5] != REMOVED && !var1.execute(var3[var5], var4[var5])) {
               this.removeAt(var5);
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
      Object[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;

      while (var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED) {
            var2[var4] = var1.execute(var2[var4]);
         }
      }
   }

   @Override
   protected void rehash(int var1) {
      int var2 = this._set.length;
      int var3 = this.size();
      Object[] var4 = this._set;
      Object[] var5 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = (V[])(new Object[var1]);
      int var6 = var2;

      while (var6-- > 0) {
         Object var7 = var4[var6];
         if (var7 != FREE && var7 != REMOVED) {
            int var8 = this.insertKey((K)var7);
            if (var8 < 0) {
               this.throwObjectContractViolation(this._set[-var8 - 1], var7, this.size(), var3, var4);
            }

            this._values[var8] = (V)var5[var6];
         }
      }
   }

   @Override
   public V get(Object var1) {
      int var2 = this.index(var1);
      return var2 >= 0 && this.strategy.equals((K)this._set[var2], (K)var1) ? this._values[var2] : null;
   }

   @Override
   public void clear() {
      if (this.size() != 0) {
         super.clear();
         Arrays.fill(this._set, 0, this._set.length, FREE);
         Arrays.fill(this._values, 0, this._values.length, null);
      }
   }

   @Override
   public V remove(Object var1) {
      Object var2 = null;
      int var3 = this.index(var1);
      if (var3 >= 0) {
         var2 = this._values[var3];
         this.removeAt(var3);
      }

      return (V)var2;
   }

   @Override
   public void removeAt(int var1) {
      this._values[var1] = null;
      super.removeAt(var1);
   }

   @Override
   public Collection<V> values() {
      return new TCustomHashMap.ValueView();
   }

   @Override
   public Set<K> keySet() {
      return new TCustomHashMap.KeyView();
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      return new TCustomHashMap.EntryView();
   }

   @Override
   public boolean containsValue(Object var1) {
      Object[] var2 = this._set;
      Object[] var3 = this._values;
      if (null == var1) {
         int var5 = var3.length;

         while (var5-- > 0) {
            if (var2[var5] != FREE && var2[var5] != REMOVED && var1 == var3[var5]) {
               return true;
            }
         }
      } else {
         int var4 = var3.length;

         while (var4-- > 0) {
            if (var2[var4] != FREE && var2[var4] != REMOVED && (var1 == var3[var4] || this.strategy.equals((K)var1, (K)var3[var4]))) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      this.ensureCapacity(var1.size());

      for (java.util.Map.Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(1);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while (var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
            var1.writeObject(this._values[var2]);
         }
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      byte var2 = var1.readByte();
      if (var2 != 0) {
         super.readExternal(var1);
      }

      int var3 = var1.readInt();
      this.setUp(var3);

      while (var3-- > 0) {
         Object var4 = var1.readObject();
         Object var5 = var1.readObject();
         this.put((K)var4, (V)var5);
      }
   }

   final class Entry implements java.util.Map.Entry<K, V> {
      private K key;
      private V val;
      private final int index;

      Entry(K var2, V var3, int var4) {
         this.key = (K)var2;
         this.val = (V)var3;
         this.index = var4;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.val;
      }

      @Override
      public V setValue(V var1) {
         if (TCustomHashMap.this._values[this.index] != this.val) {
            throw new ConcurrentModificationException();
         } else {
            Object var2 = this.val;
            TCustomHashMap.this._values[this.index] = (V)var1;
            this.val = (V)var1;
            return (V)var2;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var3 = (java.util.Map.Entry)var1;
            return (this.getKey() == null ? var3.getKey() == null : TCustomHashMap.this.strategy.equals((K)this.getKey(), (K)var3.getKey()))
               && (this.getValue() == null ? var3.getValue() == null : this.getValue().equals(var3.getValue()));
         }
      }

      @Override
      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      @Override
      public String toString() {
         return this.key + "=" + this.val;
      }
   }

   protected class EntryView extends TCustomHashMap<K, V>.MapBackedView<java.util.Map.Entry<K, V>> {
      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator() {
         return new TCustomHashMap.EntryView.EntryIterator(TCustomHashMap.this);
      }

      public boolean removeElement(java.util.Map.Entry<K, V> var1) {
         Object var4 = this.keyForEntry(var1);
         int var3 = TCustomHashMap.this.index(var4);
         if (var3 >= 0) {
            Object var2 = this.valueForEntry(var1);
            if (var2 == TCustomHashMap.this._values[var3] || null != var2 && TCustomHashMap.this.strategy.equals((K)var2, (K)TCustomHashMap.this._values[var3])
               )
             {
               TCustomHashMap.this.removeAt(var3);
               return true;
            }
         }

         return false;
      }

      public boolean containsElement(java.util.Map.Entry<K, V> var1) {
         Object var2 = TCustomHashMap.this.get(this.keyForEntry(var1));
         Object var3 = var1.getValue();
         return var3 == var2 || null != var2 && TCustomHashMap.this.strategy.equals((K)var2, (K)var3);
      }

      protected V valueForEntry(java.util.Map.Entry<K, V> var1) {
         return (V)var1.getValue();
      }

      protected K keyForEntry(java.util.Map.Entry<K, V> var1) {
         return (K)var1.getKey();
      }

      private final class EntryIterator extends TObjectHashIterator {
         EntryIterator(TCustomHashMap<K, V> var2) {
            super(var2);
         }

         public TCustomHashMap<K, V>.Entry objectAtIndex(int var1) {
            return TCustomHashMap.this.new Entry(TCustomHashMap.this._set[var1], TCustomHashMap.this._values[var1], var1);
         }
      }
   }

   private static final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V> {
      private final Map<K, V> _otherMap;

      EqProcedure(Map<K, V> var1) {
         this._otherMap = var1;
      }

      @Override
      public final boolean execute(K var1, V var2) {
         if (var2 == null && !this._otherMap.containsKey(var1)) {
            return false;
         } else {
            Object var3 = this._otherMap.get(var1);
            return var3 == var2 || var3 != null && var3.equals(var2);
         }
      }
   }

   private final class HashProcedure implements TObjectObjectProcedure<K, V> {
      private int h = 0;

      private HashProcedure() {
      }

      public int getHashCode() {
         return this.h;
      }

      @Override
      public final boolean execute(K var1, V var2) {
         this.h = this.h + (HashFunctions.hash(var1) ^ (var2 == null ? 0 : var2.hashCode()));
         return true;
      }
   }

   protected class KeyView extends TCustomHashMap<K, V>.MapBackedView<K> {
      @Override
      public Iterator<K> iterator() {
         return new TObjectHashIterator<>(TCustomHashMap.this);
      }

      @Override
      public boolean removeElement(K var1) {
         return null != TCustomHashMap.this.remove(var1);
      }

      @Override
      public boolean containsElement(K var1) {
         return TCustomHashMap.this.contains(var1);
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
         TCustomHashMap.this.clear();
      }

      @Override
      public boolean add(E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return TCustomHashMap.this.size();
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
         return TCustomHashMap.this.isEmpty();
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

      @Override
      public String toString() {
         Iterator var1 = this.iterator();
         if (!var1.hasNext()) {
            return "{}";
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('{');

            while (true) {
               Object var3 = var1.next();
               var2.append(var3 == this ? "(this Collection)" : var3);
               if (!var1.hasNext()) {
                  return var2.append('}').toString();
               }

               var2.append(", ");
            }
         }
      }
   }

   protected class ValueView extends TCustomHashMap<K, V>.MapBackedView<V> {
      @Override
      public Iterator<V> iterator() {
         return new TObjectHashIterator(TCustomHashMap.this) {
            @Override
            protected V objectAtIndex(int var1) {
               return TCustomHashMap.this._values[var1];
            }
         };
      }

      @Override
      public boolean containsElement(V var1) {
         return TCustomHashMap.this.containsValue(var1);
      }

      @Override
      public boolean removeElement(V var1) {
         Object[] var2 = TCustomHashMap.this._values;
         Object[] var3 = TCustomHashMap.this._set;
         int var4 = var2.length;

         while (var4-- > 0) {
            if (var3[var4] != TObjectHash.FREE && var3[var4] != TObjectHash.REMOVED && var1 == var2[var4]
               || null != var2[var4] && TCustomHashMap.this.strategy.equals((K)var2[var4], (K)var1)) {
               TCustomHashMap.this.removeAt(var4);
               return true;
            }
         }

         return false;
      }
   }
}
