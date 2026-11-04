package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class JdkBackedImmutableMultiset<E> extends ImmutableMultiset<E> {
   private final Map<E, Integer> delegateMap;
   private final ImmutableList<Multiset.Entry<E>> entries;
   private final long size;
   private transient ImmutableSet<E> elementSet;

   static <E> ImmutableMultiset<E> create(Collection<? extends Multiset.Entry<? extends E>> var0) {
      Multiset.Entry[] var1 = var0.toArray(new Multiset.Entry[0]);
      HashMap var2 = Maps.newHashMapWithExpectedSize(var1.length);
      long var3 = 0L;

      for (int var5 = 0; var5 < var1.length; var5++) {
         Multiset.Entry var6 = var1[var5];
         int var7 = var6.getCount();
         var3 += (long)var7;
         Object var8 = Preconditions.checkNotNull(var6.getElement());
         var2.put(var8, var7);
         if (!(var6 instanceof Multisets.ImmutableEntry)) {
            var1[var5] = Multisets.immutableEntry(var8, var7);
         }
      }

      return new JdkBackedImmutableMultiset<>(var2, ImmutableList.asImmutableList(var1), var3);
   }

   private JdkBackedImmutableMultiset(Map<E, Integer> var1, ImmutableList<Multiset.Entry<E>> var2, long var3) {
      this.delegateMap = var1;
      this.entries = var2;
      this.size = var3;
   }

   @Override
   public int count(Object var1) {
      return this.delegateMap.getOrDefault(var1, 0);
   }

   @Override
   public ImmutableSet<E> elementSet() {
      ImmutableSet var1 = this.elementSet;
      return var1 == null ? (this.elementSet = new ImmutableMultiset.ElementSet<>(this.entries, this)) : var1;
   }

   @Override
   Multiset.Entry<E> getEntry(int var1) {
      return this.entries.get(var1);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public int size() {
      return Ints.saturatedCast(this.size);
   }
}
