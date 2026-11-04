package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.j2objc.annotations.Weak;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredMultimapValues<K, V> extends AbstractCollection<V> {
   @Weak
   private final FilteredMultimap<K, V> multimap;

   FilteredMultimapValues(FilteredMultimap<K, V> var1) {
      this.multimap = Preconditions.checkNotNull(var1);
   }

   @Override
   public Iterator<V> iterator() {
      return Maps.valueIterator(this.multimap.entries().iterator());
   }

   @Override
   public boolean contains(Object var1) {
      return this.multimap.containsValue(var1);
   }

   @Override
   public int size() {
      return this.multimap.size();
   }

   @Override
   public boolean remove(Object var1) {
      Predicate var2 = this.multimap.entryPredicate();
      Iterator var3 = this.multimap.unfiltered().entries().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var2.apply(var4) && Objects.equal(var4.getValue(), var1)) {
            var3.remove();
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      return Iterables.removeIf(
         this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.in(var1)))
      );
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      return Iterables.removeIf(
         this.multimap.unfiltered().entries(),
         Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(var1))))
      );
   }

   @Override
   public void clear() {
      this.multimap.clear();
   }
}
