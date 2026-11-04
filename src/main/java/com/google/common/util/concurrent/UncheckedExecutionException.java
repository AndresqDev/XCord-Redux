package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class UncheckedExecutionException extends RuntimeException {
   private static final long serialVersionUID = 0L;

   protected UncheckedExecutionException() {
   }

   protected UncheckedExecutionException(String var1) {
      super(var1);
   }

   public UncheckedExecutionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public UncheckedExecutionException(Throwable var1) {
      super(var1);
   }
}
