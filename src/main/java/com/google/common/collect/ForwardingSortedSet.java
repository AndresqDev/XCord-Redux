package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E> {
   protected ForwardingSortedSet() {
   }

   protected abstract SortedSet<E> delegate();

   @Override
   public Comparator<? super E> comparator() {
      return this.delegate().comparator();
   }

   @ParametricNullness
   @Override
   public E first() {
      return this.delegate().first();
   }

   @Override
   public SortedSet<E> headSet(@ParametricNullness E var1) {
      return this.delegate().headSet((E)var1);
   }

   @ParametricNullness
   @Override
   public E last() {
      return this.delegate().last();
   }

   @Override
   public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
      return this.delegate().subSet((E)var1, (E)var2);
   }

   @Override
   public SortedSet<E> tailSet(@ParametricNullness E var1) {
      return this.delegate().tailSet((E)var1);
   }

   @Beta
   @Override
   protected boolean standardContains(Object var1) {
      try {
         Object var3 = this.tailSet((E)var1).first();
         return ForwardingSortedMap.unsafeCompare(this.comparator(), var3, var1) == 0;
      } catch (NoSuchElementException | NullPointerException | ClassCastException var4) {
         return false;
      }
   }

   @Beta
   @Override
   protected boolean standardRemove(Object var1) {
      try {
         Iterator var3 = this.tailSet((E)var1).iterator();
         if (var3.hasNext()) {
            Object var4 = var3.next();
            if (ForwardingSortedMap.unsafeCompare(this.comparator(), var4, var1) == 0) {
               var3.remove();
               return true;
            }
         }

         return false;
      } catch (NullPointerException | ClassCastException var5) {
         return false;
      }
   }

   @Beta
   protected SortedSet<E> standardSubSet(@ParametricNullness E var1, @ParametricNullness E var2) {
      return this.tailSet((E)var1).headSet((E)var2);
   }
}
