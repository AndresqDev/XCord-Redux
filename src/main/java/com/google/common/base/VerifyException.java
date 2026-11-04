package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public class VerifyException extends RuntimeException {
   public VerifyException() {
   }

   public VerifyException(String var1) {
      super(var1);
   }

   public VerifyException(Throwable var1) {
      super(var1);
   }

   public VerifyException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
