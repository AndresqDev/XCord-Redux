package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V> {
   protected ForwardingMap() {
   }

   protected abstract Map<K, V> delegate();

   @Override
   public int size() {
      return this.delegate().size();
   }

   @Override
   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1) {
      return this.delegate().remove(var1);
   }

   @Override
   public void clear() {
      this.delegate().clear();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.delegate().containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.delegate().containsValue(var1);
   }

   @Override
   public V get(Object var1) {
      return this.delegate().get(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public V put(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.delegate().put((K)var1, (V)var2);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      this.delegate().putAll(var1);
   }

   @Override
   public Set<K> keySet() {
      return this.delegate().keySet();
   }

   @Override
   public Collection<V> values() {
      return this.delegate().values();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.delegate().entrySet();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected void standardPutAll(Map<? extends K, ? extends V> var1) {
      Maps.putAllImpl(this, var1);
   }

   @Beta
   protected V standardRemove(Object var1) {
      Iterator var2 = this.entrySet().iterator();

      while (var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (Objects.equal(var3.getKey(), var1)) {
            Object var4 = var3.getValue();
            var2.remove();
            return (V)var4;
         }
      }

      return null;
   }

   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected boolean standardContainsKey(Object var1) {
      return Maps.containsKeyImpl(this, var1);
   }

   protected boolean standardContainsValue(Object var1) {
      return Maps.containsValueImpl(this, var1);
   }

   protected boolean standardIsEmpty() {
      return !this.entrySet().iterator().hasNext();
   }

   protected boolean standardEquals(Object var1) {
      return Maps.equalsImpl(this, var1);
   }

   protected int standardHashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   protected String standardToString() {
      return Maps.toStringImpl(this);
   }

   @Beta
   protected abstract class StandardEntrySet extends Maps.EntrySet<K, V> {
      public StandardEntrySet() {
      }

      @Override
      Map<K, V> map() {
         return ForwardingMap.this;
      }
   }

   @Beta
   protected class StandardKeySet extends Maps.KeySet<K, V> {
      public StandardKeySet(ForwardingMap var1) {
         super(var1);
      }
   }

   @Beta
   protected class StandardValues extends Maps.Values<K, V> {
      public StandardValues(ForwardingMap var1) {
         super(var1);
      }
   }
}
