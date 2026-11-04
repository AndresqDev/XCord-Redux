package com.google.gson.internal;

public final class JavaVersion {
   private static final int majorJavaVersion = determineMajorJavaVersion();

   private static int determineMajorJavaVersion() {
      String var0 = System.getProperty("java.version");
      return getMajorJavaVersion(var0);
   }

   static int getMajorJavaVersion(String var0) {
      int var1 = parseDotted(var0);
      if (var1 == -1) {
         var1 = extractBeginningInt(var0);
      }

      return var1 == -1 ? 6 : var1;
   }

   private static int parseDotted(String var0) {
      try {
         String[] var1 = var0.split("[._]");
         int var2 = Integer.parseInt(var1[0]);
         return var2 == 1 && var1.length > 1 ? Integer.parseInt(var1[1]) : var2;
      } catch (NumberFormatException var3) {
         return -1;
      }
   }

   private static int extractBeginningInt(String var0) {
      try {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (!Character.isDigit(var3)) {
               break;
            }

            var1.append(var3);
         }

         return Integer.parseInt(var1.toString());
      } catch (NumberFormatException var4) {
         return -1;
      }
   }

   public static int getMajorJavaVersion() {
      return majorJavaVersion;
   }

   public static boolean isJava9OrLater() {
      return majorJavaVersion >= 9;
   }

   private JavaVersion() {
   }
}
