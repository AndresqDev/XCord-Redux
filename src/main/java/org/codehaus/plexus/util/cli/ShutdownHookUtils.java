package org.codehaus.plexus.util.cli;

import java.security.AccessControlException;

class ShutdownHookUtils {
   public static void addShutDownHook(Thread var0) {
      try {
         Runtime.getRuntime().addShutdownHook(var0);
      } catch (IllegalStateException var2) {
      } catch (AccessControlException var3) {
      }
   }

   public static void removeShutdownHook(Thread var0) {
      try {
         Runtime.getRuntime().removeShutdownHook(var0);
      } catch (IllegalStateException var2) {
      } catch (AccessControlException var3) {
      }
   }
}
