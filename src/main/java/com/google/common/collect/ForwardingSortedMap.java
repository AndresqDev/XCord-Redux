package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V> {
   protected ForwardingSortedMap() {
   }

   protected abstract SortedMap<K, V> delegate();

   @Override
   public Comparator<? super K> comparator() {
      return this.delegate().comparator();
   }

   @ParametricNullness
   @Override
   public K firstKey() {
      return this.delegate().firstKey();
   }

   @Override
   public SortedMap<K, V> headMap(@ParametricNullness K var1) {
      return this.delegate().headMap((K)var1);
   }

   @ParametricNullness
   @Override
   public K lastKey() {
      return this.delegate().lastKey();
   }

   @Override
   public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
      return this.delegate().subMap((K)var1, (K)var2);
   }

   @Override
   public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
      return this.delegate().tailMap((K)var1);
   }

   static int unsafeCompare(Comparator<?> var0, Object var1, Object var2) {
      return var0 == null ? ((Comparable)var1).compareTo(var2) : var0.compare(var1, var2);
   }

   @Beta
   @Override
   protected boolean standardContainsKey(Object var1) {
      try {
         Object var3 = this.tailMap((K)var1).firstKey();
         return unsafeCompare(this.comparator(), var3, var1) == 0;
      } catch (NoSuchElementException | NullPointerException | ClassCastException var4) {
         return false;
      }
   }

   @Beta
   protected SortedMap<K, V> standardSubMap(K var1, K var2) {
      Preconditions.checkArgument(unsafeCompare(this.comparator(), var1, var2) <= 0, "fromKey must be <= toKey");
      return this.tailMap((K)var1).headMap((K)var2);
   }

   @Beta
   protected class StandardKeySet extends Maps.SortedKeySet<K, V> {
      public StandardKeySet(ForwardingSortedMap var1) {
         super(var1);
      }
   }
}
