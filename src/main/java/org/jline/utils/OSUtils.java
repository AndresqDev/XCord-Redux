package org.jline.utils;

import java.io.File;

public class OSUtils {
   public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
   public static final boolean IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
   @Deprecated
   public static final boolean IS_MINGW = IS_WINDOWS && System.getenv("MSYSTEM") != null && System.getenv("MSYSTEM").startsWith("MINGW");
   public static final boolean IS_MSYSTEM = IS_WINDOWS
      && System.getenv("MSYSTEM") != null
      && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
   public static final boolean IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
   public static final boolean IS_OSX = System.getProperty("os.name").toLowerCase().contains("mac");
   public static String TTY_COMMAND;
   public static String STTY_COMMAND;
   public static String STTY_F_OPTION;
   public static String INFOCMP_COMMAND;

   static {
      String var0;
      String var1;
      String var2;
      String var3;
      if (!IS_CYGWIN && !IS_MSYSTEM) {
         var0 = "tty";
         var1 = "stty";
         var3 = "infocmp";
         if (IS_OSX) {
            var2 = "-f";
         } else {
            var2 = "-F";
         }
      } else {
         var0 = "tty.exe";
         var1 = "stty.exe";
         var2 = null;
         var3 = "infocmp.exe";
         String var4 = System.getenv("PATH");
         if (var4 != null) {
            String[] var5 = var4.split(";");

            for (String var9 : var5) {
               if (var0 == null && new File(var9, "tty.exe").exists()) {
                  var0 = new File(var9, "tty.exe").getAbsolutePath();
               }

               if (var1 == null && new File(var9, "stty.exe").exists()) {
                  var1 = new File(var9, "stty.exe").getAbsolutePath();
               }

               if (var3 == null && new File(var9, "infocmp.exe").exists()) {
                  var3 = new File(var9, "infocmp.exe").getAbsolutePath();
               }
            }
         }
      }

      TTY_COMMAND = var0;
      STTY_COMMAND = var1;
      STTY_F_OPTION = var2;
      INFOCMP_COMMAND = var3;
   }
}
