package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class PairwiseEquivalence<E, T extends E> extends Equivalence<Iterable<T>> implements Serializable {
   final Equivalence<E> elementEquivalence;
   private static final long serialVersionUID = 1L;

   PairwiseEquivalence(Equivalence<E> var1) {
      this.elementEquivalence = Preconditions.checkNotNull(var1);
   }

   protected boolean doEquivalent(Iterable<T> var1, Iterable<T> var2) {
      Iterator var3 = var1.iterator();
      Iterator var4 = var2.iterator();

      while (var3.hasNext() && var4.hasNext()) {
         if (!this.elementEquivalence.equivalent((E)var3.next(), (E)var4.next())) {
            return false;
         }
      }

      return !var3.hasNext() && !var4.hasNext();
   }

   protected int doHash(Iterable<T> var1) {
      int var2 = 78721;

      for (Object var4 : var1) {
         var2 = var2 * 24943 + this.elementEquivalence.hash((E)var4);
      }

      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof PairwiseEquivalence) {
         PairwiseEquivalence var2 = (PairwiseEquivalence)var1;
         return this.elementEquivalence.equals(var2.elementEquivalence);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.elementEquivalence.hashCode() ^ 1185147655;
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.elementEquivalence);
      return new StringBuilder(11 + String.valueOf(var1).length()).append(var1).append(".pairwise()").toString();
   }
}
