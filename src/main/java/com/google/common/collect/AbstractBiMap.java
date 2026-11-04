package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class AbstractBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {
   private transient Map<K, V> delegate;
   @RetainedWith
   transient AbstractBiMap<V, K> inverse;
   private transient Set<K> keySet;
   private transient Set<V> valueSet;
   private transient Set<Entry<K, V>> entrySet;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   AbstractBiMap(Map<K, V> var1, Map<V, K> var2) {
      this.setDelegates(var1, var2);
   }

   private AbstractBiMap(Map<K, V> var1, AbstractBiMap<V, K> var2) {
      this.delegate = var1;
      this.inverse = var2;
   }

   @Override
   protected Map<K, V> delegate() {
      return this.delegate;
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   K checkKey(@ParametricNullness K var1) {
      return (K)var1;
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   V checkValue(@ParametricNullness V var1) {
      return (V)var1;
   }

   void setDelegates(Map<K, V> var1, Map<V, K> var2) {
      Preconditions.checkState(this.delegate == null);
      Preconditions.checkState(this.inverse == null);
      Preconditions.checkArgument(var1.isEmpty());
      Preconditions.checkArgument(var2.isEmpty());
      Preconditions.checkArgument(var1 != var2);
      this.delegate = var1;
      this.inverse = this.makeInverse(var2);
   }

   AbstractBiMap<V, K> makeInverse(Map<V, K> var1) {
      return new AbstractBiMap.Inverse<>(var1, this);
   }

   void setInverse(AbstractBiMap<V, K> var1) {
      this.inverse = var1;
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.inverse.containsKey(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public V put(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.putInBothMaps((K)var1, (V)var2, false);
   }

   @CanIgnoreReturnValue
   @Override
   public V forcePut(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.putInBothMaps((K)var1, (V)var2, true);
   }

   private V putInBothMaps(@ParametricNullness K var1, @ParametricNullness V var2, boolean var3) {
      this.checkKey((K)var1);
      this.checkValue((V)var2);
      boolean var4 = this.containsKey(var1);
      if (var4 && Objects.equal(var2, this.get(var1))) {
         return (V)var2;
      } else {
         if (var3) {
            this.inverse().remove(var2);
         } else {
            Preconditions.checkArgument(!this.containsValue(var2), "value already present: %s", var2);
         }

         Object var5 = this.delegate.put((K)var1, (V)var2);
         this.updateInverseMap((K)var1, var4, (V)var5, (V)var2);
         return (V)var5;
      }
   }

   private void updateInverseMap(@ParametricNullness K var1, boolean var2, V var3, @ParametricNullness V var4) {
      if (var2) {
         this.removeFromInverseMap(NullnessCasts.uncheckedCastNullableTToT((V)var3));
      }

      this.inverse.delegate.put((V)var4, (K)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1) {
      return this.containsKey(var1) ? this.removeFromBothMaps(var1) : null;
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   private V removeFromBothMaps(Object var1) {
      Object var2 = NullnessCasts.uncheckedCastNullableTToT(this.delegate.remove(var1));
      this.removeFromInverseMap((V)var2);
      return (V)var2;
   }

   private void removeFromInverseMap(@ParametricNullness V var1) {
      this.inverse.delegate.remove(var1);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      this.delegate.replaceAll(var1);
      this.inverse.delegate.clear();
      Entry var2 = null;
      Iterator var3 = this.delegate.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Object var5 = var4.getKey();
         Object var6 = var4.getValue();
         Object var7 = this.inverse.delegate.putIfAbsent((V)var6, (K)var5);
         if (var7 != null) {
            var2 = var4;
            var3.remove();
         }
      }

      if (var2 != null) {
         String var8 = String.valueOf(var2.getValue());
         throw new IllegalArgumentException(new StringBuilder(23 + String.valueOf(var8).length()).append("value already present: ").append(var8).toString());
      }
   }

   @Override
   public void clear() {
      this.delegate.clear();
      this.inverse.delegate.clear();
   }

   @Override
   public BiMap<V, K> inverse() {
      return this.inverse;
   }

   @Override
   public Set<K> keySet() {
      Set var1 = this.keySet;
      return var1 == null ? (this.keySet = new AbstractBiMap.KeySet()) : var1;
   }

   @Override
   public Set<V> values() {
      Set var1 = this.valueSet;
      return var1 == null ? (this.valueSet = new AbstractBiMap.ValueSet()) : var1;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 == null ? (this.entrySet = new AbstractBiMap.EntrySet()) : var1;
   }

   Iterator<Entry<K, V>> entrySetIterator() {
      final Iterator var1 = this.delegate.entrySet().iterator();
      return new Iterator<Entry<K, V>>() {
         Entry<K, V> entry;

         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         public Entry<K, V> next() {
            this.entry = (Entry<K, V>)var1.next();
            return AbstractBiMap.this.new BiMapEntry(this.entry);
         }

         @Override
         public void remove() {
            if (this.entry == null) {
               throw new IllegalStateException("no calls to next() since the last call to remove()");
            } else {
               Object var1x = this.entry.getValue();
               var1.remove();
               AbstractBiMap.this.removeFromInverseMap(var1x);
               this.entry = null;
            }
         }
      };
   }

   class BiMapEntry extends ForwardingMapEntry<K, V> {
      private final Entry<K, V> delegate;

      BiMapEntry(Entry<K, V> var2) {
         this.delegate = var2;
      }

      @Override
      protected Entry<K, V> delegate() {
         return this.delegate;
      }

      @Override
      public V setValue(V var1) {
         AbstractBiMap.this.checkValue((V)var1);
         Preconditions.checkState(AbstractBiMap.this.entrySet().contains(this), "entry no longer in map");
         if (Objects.equal(var1, this.getValue())) {
            return (V)var1;
         } else {
            Preconditions.checkArgument(!AbstractBiMap.this.containsValue(var1), "value already present: %s", var1);
            Object var2 = this.delegate.setValue((V)var1);
            Preconditions.checkState(Objects.equal(var1, AbstractBiMap.this.get(this.getKey())), "entry no longer in map");
            AbstractBiMap.this.updateInverseMap((K)this.getKey(), true, (V)var2, (V)var1);
            return (V)var2;
         }
      }
   }

   private class EntrySet extends ForwardingSet<Entry<K, V>> {
      final Set<Entry<K, V>> esDelegate = AbstractBiMap.this.delegate.entrySet();

      private EntrySet() {
      }

      @Override
      protected Set<Entry<K, V>> delegate() {
         return this.esDelegate;
      }

      @Override
      public void clear() {
         AbstractBiMap.this.clear();
      }

      @Override
      public boolean remove(Object var1) {
         if (this.esDelegate.contains(var1) && var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            AbstractBiMap.this.inverse.delegate.remove(var2.getValue());
            this.esDelegate.remove(var2);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return AbstractBiMap.this.entrySetIterator();
      }

      @Override
      public Object[] toArray() {
         return this.standardToArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.standardToArray((T[])var1);
      }

      @Override
      public boolean contains(Object var1) {
         return Maps.containsEntryImpl(this.delegate(), var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return this.standardContainsAll(var1);
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.standardRemoveAll(var1);
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.standardRetainAll(var1);
      }
   }

   static class Inverse<K, V> extends AbstractBiMap<K, V> {
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      Inverse(Map<K, V> var1, AbstractBiMap<V, K> var2) {
         super(var1, var2);
      }

      @ParametricNullness
      @Override
      K checkKey(@ParametricNullness K var1) {
         return this.inverse.checkValue((K)var1);
      }

      @ParametricNullness
      @Override
      V checkValue(@ParametricNullness V var1) {
         return this.inverse.checkKey((V)var1);
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeObject(this.inverse());
      }

      @GwtIncompatible
      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.setInverse((AbstractBiMap<V, K>)var1.readObject());
      }

      @GwtIncompatible
      Object readResolve() {
         return this.inverse().inverse();
      }
   }

   private class KeySet extends ForwardingSet<K> {
      private KeySet() {
      }

      @Override
      protected Set<K> delegate() {
         return AbstractBiMap.this.delegate.keySet();
      }

      @Override
      public void clear() {
         AbstractBiMap.this.clear();
      }

      @Override
      public boolean remove(Object var1) {
         if (!this.contains(var1)) {
            return false;
         } else {
            AbstractBiMap.this.removeFromBothMaps(var1);
            return true;
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.standardRemoveAll(var1);
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.standardRetainAll(var1);
      }

      @Override
      public Iterator<K> iterator() {
         return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
      }
   }

   private class ValueSet extends ForwardingSet<V> {
      final Set<V> valuesDelegate = AbstractBiMap.this.inverse.keySet();

      private ValueSet() {
      }

      @Override
      protected Set<V> delegate() {
         return this.valuesDelegate;
      }

      @Override
      public Iterator<V> iterator() {
         return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
      }

      @Override
      public Object[] toArray() {
         return this.standardToArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.standardToArray((T[])var1);
      }

      @Override
      public String toString() {
         return this.standardToString();
      }
   }
}
