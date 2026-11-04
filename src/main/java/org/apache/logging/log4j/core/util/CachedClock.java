package org.apache.logging.log4j.core.util;

import java.util.concurrent.locks.LockSupport;

public final class CachedClock implements Clock {
   private static final int UPDATE_THRESHOLD = 1000;
   private static volatile CachedClock instance;
   private static final Object INSTANCE_LOCK = new Object();
   private volatile long millis = System.currentTimeMillis();
   private short count = 0;

   private CachedClock() {
      Log4jThread var1 = new Log4jThread(() -> {
         while (true) {
            long var1x = System.currentTimeMillis();
            this.millis = var1x;
            LockSupport.parkNanos(1000000L);
         }
      }, "CachedClock Updater Thread");
      var1.setDaemon(true);
      var1.start();
   }

   public static CachedClock instance() {
      CachedClock var0 = instance;
      if (var0 == null) {
         synchronized (INSTANCE_LOCK) {
            var0 = instance;
            if (var0 == null) {
               instance = var0 = new CachedClock();
            }
         }
      }

      return var0;
   }

   @Override
   public long currentTimeMillis() {
      if (++this.count > 1000) {
         this.millis = System.currentTimeMillis();
         this.count = 0;
      }

      return this.millis;
   }
}
