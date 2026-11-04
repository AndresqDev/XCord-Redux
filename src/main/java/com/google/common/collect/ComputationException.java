package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@Deprecated
@ElementTypesAreNonnullByDefault
@GwtCompatible
public class ComputationException extends RuntimeException {
   private static final long serialVersionUID = 0L;

   public ComputationException(Throwable var1) {
      super(var1);
   }
}
