package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable {
   final Function<F, ? extends T> function;
   final Ordering<T> ordering;
   private static final long serialVersionUID = 0L;

   ByFunctionOrdering(Function<F, ? extends T> var1, Ordering<T> var2) {
      this.function = Preconditions.checkNotNull(var1);
      this.ordering = Preconditions.checkNotNull(var2);
   }

   @Override
   public int compare(@ParametricNullness F var1, @ParametricNullness F var2) {
      return this.ordering.compare((T)this.function.apply((F)var1), (T)this.function.apply((F)var2));
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ByFunctionOrdering)) {
         return false;
      } else {
         ByFunctionOrdering var2 = (ByFunctionOrdering)var1;
         return this.function.equals(var2.function) && this.ordering.equals(var2.ordering);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.function, this.ordering);
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.ordering);
      String var2 = String.valueOf(this.function);
      return new StringBuilder(13 + String.valueOf(var1).length() + String.valueOf(var2).length())
         .append(var1)
         .append(".onResultOf(")
         .append(var2)
         .append(")")
         .toString();
   }
}
