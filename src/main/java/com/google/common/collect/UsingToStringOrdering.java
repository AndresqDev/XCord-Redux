package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class UsingToStringOrdering extends Ordering<Object> implements Serializable {
   static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
   private static final long serialVersionUID = 0L;

   @Override
   public int compare(Object var1, Object var2) {
      return var1.toString().compareTo(var2.toString());
   }

   private Object readResolve() {
      return INSTANCE;
   }

   @Override
   public String toString() {
      return "Ordering.usingToString()";
   }

   private UsingToStringOrdering() {
   }
}
