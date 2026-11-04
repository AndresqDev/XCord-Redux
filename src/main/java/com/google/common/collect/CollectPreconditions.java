package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectPreconditions {
   static void checkEntryNotNull(Object var0, Object var1) {
      if (var0 == null) {
         String var3 = String.valueOf(var1);
         throw new NullPointerException(new StringBuilder(24 + String.valueOf(var3).length()).append("null key in entry: null=").append(var3).toString());
      } else if (var1 == null) {
         String var2 = String.valueOf(var0);
         throw new NullPointerException(
            new StringBuilder(26 + String.valueOf(var2).length()).append("null value in entry: ").append(var2).append("=null").toString()
         );
      }
   }

   @CanIgnoreReturnValue
   static int checkNonnegative(int var0, String var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException(
            new StringBuilder(40 + String.valueOf(var1).length()).append(var1).append(" cannot be negative but was: ").append(var0).toString()
         );
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   static long checkNonnegative(long var0, String var2) {
      if (var0 < 0L) {
         throw new IllegalArgumentException(
            new StringBuilder(49 + String.valueOf(var2).length()).append(var2).append(" cannot be negative but was: ").append(var0).toString()
         );
      } else {
         return var0;
      }
   }

   static void checkPositive(int var0, String var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException(
            new StringBuilder(38 + String.valueOf(var1).length()).append(var1).append(" must be positive but was: ").append(var0).toString()
         );
      }
   }

   static void checkRemove(boolean var0) {
      Preconditions.checkState(var0, "no calls to next() since the last call to remove()");
   }
}
