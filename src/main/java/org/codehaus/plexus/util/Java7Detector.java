package org.codehaus.plexus.util;

class Java7Detector {
   private static final boolean isJava7;

   public static boolean isJava7() {
      return isJava7;
   }

   static {
      boolean var0 = true;

      try {
         Class.forName("java.nio.file.Files");
      } catch (Exception var2) {
         var0 = false;
      }

      isJava7 = var0;
   }
}
