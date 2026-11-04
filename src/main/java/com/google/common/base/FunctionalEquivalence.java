package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
final class FunctionalEquivalence<F, T> extends Equivalence<F> implements Serializable {
   private static final long serialVersionUID = 0L;
   private final Function<? super F, ? extends T> function;
   private final Equivalence<T> resultEquivalence;

   FunctionalEquivalence(Function<? super F, ? extends T> var1, Equivalence<T> var2) {
      this.function = Preconditions.checkNotNull(var1);
      this.resultEquivalence = Preconditions.checkNotNull(var2);
   }

   @Override
   protected boolean doEquivalent(F var1, F var2) {
      return this.resultEquivalence.equivalent((T)this.function.apply((F)var1), (T)this.function.apply((F)var2));
   }

   @Override
   protected int doHash(F var1) {
      return this.resultEquivalence.hash((T)this.function.apply((F)var1));
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FunctionalEquivalence)) {
         return false;
      } else {
         FunctionalEquivalence var2 = (FunctionalEquivalence)var1;
         return this.function.equals(var2.function) && this.resultEquivalence.equals(var2.resultEquivalence);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.function, this.resultEquivalence);
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.resultEquivalence);
      String var2 = String.valueOf(this.function);
      return new StringBuilder(13 + String.valueOf(var1).length() + String.valueOf(var2).length())
         .append(var1)
         .append(".onResultOf(")
         .append(var2)
         .append(")")
         .toString();
   }
}
