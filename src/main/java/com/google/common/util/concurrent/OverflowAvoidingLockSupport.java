package com.google.common.util.concurrent;

import java.util.concurrent.locks.LockSupport;

@ElementTypesAreNonnullByDefault
final class OverflowAvoidingLockSupport {
   static final long MAX_NANOSECONDS_THRESHOLD = 2147483647999999999L;

   private OverflowAvoidingLockSupport() {
   }

   static void parkNanos(Object var0, long var1) {
      LockSupport.parkNanos(var0, Math.min(var1, 2147483647999999999L));
   }
}
