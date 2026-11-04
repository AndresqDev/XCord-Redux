package org.apache.logging.log4j.internal;

public class LogManagerStatus {
   private static boolean initialized = false;

   public static void setInitialized(boolean var0) {
      initialized = var0;
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
