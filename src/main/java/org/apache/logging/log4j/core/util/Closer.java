package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.status.StatusLogger;

public final class Closer {
   private Closer() {
   }

   public static boolean close(AutoCloseable var0) throws Exception {
      if (var0 != null) {
         StatusLogger.getLogger().debug("Closing {} {}", var0.getClass().getSimpleName(), var0);
         var0.close();
         return true;
      } else {
         return false;
      }
   }

   public static boolean closeSilently(AutoCloseable var0) {
      try {
         return close(var0);
      } catch (Exception var2) {
         return false;
      }
   }
}
