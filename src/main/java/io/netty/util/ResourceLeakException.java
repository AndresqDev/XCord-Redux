package io.netty.util;

import java.util.Arrays;

@Deprecated
public class ResourceLeakException extends RuntimeException {
   private static final long serialVersionUID = 7186453858343358280L;
   private final StackTraceElement[] cachedStackTrace = this.getStackTrace();

   public ResourceLeakException() {
   }

   public ResourceLeakException(String var1) {
      super(var1);
   }

   public ResourceLeakException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ResourceLeakException(Throwable var1) {
      super(var1);
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (StackTraceElement var5 : this.cachedStackTrace) {
         var1 = var1 * 31 + var5.hashCode();
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ResourceLeakException)) {
         return false;
      } else {
         return var1 == this ? true : Arrays.equals((Object[])this.cachedStackTrace, (Object[])((ResourceLeakException)var1).cachedStackTrace);
      }
   }
}
