package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class ExecutionError extends Error {
   private static final long serialVersionUID = 0L;

   protected ExecutionError() {
   }

   protected ExecutionError(String var1) {
      super(var1);
   }

   public ExecutionError(String var1, Error var2) {
      super(var1, var2);
   }

   public ExecutionError(Error var1) {
      super(var1);
   }
}
