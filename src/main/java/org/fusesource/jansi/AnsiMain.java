package org.fusesource.jansi;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.jansi.internal.CLibrary;

public class AnsiMain {
   public static void main(String... var0) throws IOException {
      System.out
         .println(
            "Jansi "
               + getJansiVersion()
               + " (Jansi native "
               + getPomPropertiesVersion("org.fusesource.jansi/jansi-native")
               + ", HawtJNI runtime "
               + getPomPropertiesVersion("org.fusesource.hawtjni/hawtjni-runtime")
               + ")"
         );
      System.out.println();
      System.out.println("library.jansi.path= " + System.getProperty("library.jansi.path", ""));
      System.out.println("library.jansi.version= " + System.getProperty("library.jansi.version", ""));
      Library var1 = new Library("jansi", CLibrary.class);
      var1.load();
      System.out.println("Jansi native library loaded from " + var1.getNativeLibraryPath());
      if (var1.getNativeLibrarySourceUrl() != null) {
         System.out.println("   which was auto-extracted from " + var1.getNativeLibrarySourceUrl());
      }

      System.out.println();
      System.out
         .println(
            "os.name= " + System.getProperty("os.name") + ", os.version= " + System.getProperty("os.version") + ", os.arch= " + System.getProperty("os.arch")
         );
      System.out.println("file.encoding= " + System.getProperty("file.encoding"));
      System.out
         .println(
            "java.version= "
               + System.getProperty("java.version")
               + ", java.vendor= "
               + System.getProperty("java.vendor")
               + ", java.home= "
               + System.getProperty("java.home")
         );
      System.out.println();
      System.out.println("jansi.passthrough= " + Boolean.getBoolean("jansi.passthrough"));
      System.out.println("jansi.strip= " + Boolean.getBoolean("jansi.strip"));
      System.out.println("jansi.force= " + Boolean.getBoolean("jansi.force"));
      System.out.println(Ansi.DISABLE + "= " + Boolean.getBoolean(Ansi.DISABLE));
      System.out.println();
      System.out.println("IS_WINDOWS: " + AnsiConsole.IS_WINDOWS);
      if (AnsiConsole.IS_WINDOWS) {
         System.out.println("IS_CYGWIN: " + AnsiConsole.IS_CYGWIN);
         System.out.println("IS_MINGW_XTERM: " + AnsiConsole.IS_MINGW_XTERM);
      }

      System.out.println();
      diagnoseTty(false);
      diagnoseTty(true);
      AnsiConsole.systemInstall();
      System.out.println();
      System.out.println("Resulting Jansi modes for stout/stderr streams:");
      System.out.println("  - System.out: " + AnsiConsole.JANSI_STDOUT_TYPE);
      System.out.println("  - System.err: " + AnsiConsole.JANSI_STDERR_TYPE);
      System.out.println("modes description:");
      int var2 = 1;

      for (AnsiConsole.JansiOutputType var6 : AnsiConsole.JansiOutputType.values()) {
         System.out.println(var2++ + ". " + var6 + ": " + var6.getDescription());
      }

      try {
         System.out.println();
         testAnsi(false);
         testAnsi(true);
         if (var0.length == 0) {
            printJansiLogoDemo();
            return;
         }

         System.out.println();
         if (var0.length == 1) {
            File var11 = new File(var0[0]);
            if (var11.exists()) {
               System.out.println(Ansi.ansi().bold().a("\"" + var0[0] + "\" content:").reset());
               writeFileContent(var11);
               return;
            }
         }

         System.out.println(Ansi.ansi().bold().a("original args:").reset());
         int var12 = 1;

         for (String var7 : var0) {
            AnsiConsole.system_out.print(var12++ + ": ");
            AnsiConsole.system_out.println(var7);
         }

         System.out.println(Ansi.ansi().bold().a("Jansi filtered args:").reset());
         var12 = 1;

         for (String var20 : var0) {
            System.out.print(var12++ + ": ");
            System.out.println(var20);
         }
      } finally {
         AnsiConsole.systemUninstall();
      }
   }

   private static String getJansiVersion() {
      Package var0 = AnsiMain.class.getPackage();
      return var0 == null ? null : var0.getImplementationVersion();
   }

   private static void diagnoseTty(boolean var0) {
      int var1 = var0 ? CLibrary.STDERR_FILENO : CLibrary.STDOUT_FILENO;
      int var2 = CLibrary.isatty(var1);
      System.out
         .println(
            "isatty(STD"
               + (var0 ? "ERR" : "OUT")
               + "_FILENO): "
               + var2
               + ", System."
               + (var0 ? "err" : "out")
               + " "
               + (var2 == 0 ? "is *NOT*" : "is")
               + " a terminal"
         );
   }

   private static void testAnsi(boolean var0) {
      PrintStream var1 = var0 ? System.err : System.out;
      var1.print("test on System." + (var0 ? "err" : "out") + ":");

      for (Ansi.Color var5 : Ansi.Color.values()) {
         var1.print(" " + Ansi.ansi().fg(var5) + var5 + Ansi.ansi().reset());
      }

      var1.println();
      var1.print("            bright:");

      for (Ansi.Color var12 : Ansi.Color.values()) {
         var1.print(" " + Ansi.ansi().fgBright(var12) + var12 + Ansi.ansi().reset());
      }

      var1.println();
      var1.print("              bold:");

      for (Ansi.Color var13 : Ansi.Color.values()) {
         var1.print(" " + Ansi.ansi().bold().fg(var13) + var13 + Ansi.ansi().reset());
      }

      var1.println();
   }

   private static String getPomPropertiesVersion(String var0) throws IOException {
      InputStream var1 = AnsiMain.class.getResourceAsStream("/META-INF/maven/" + var0 + "/pom.properties");
      if (var1 == null) {
         return null;
      } else {
         String var3;
         try {
            Properties var2 = new Properties();
            var2.load(var1);
            var3 = var2.getProperty("version");
         } finally {
            closeQuietly(var1);
         }

         return var3;
      }
   }

   private static void printJansiLogoDemo() throws IOException {
      InputStreamReader var0 = new InputStreamReader(AnsiMain.class.getResourceAsStream("jansi.txt"), "UTF-8");

      try {
         char[] var1 = new char[1024];
         int var2 = 0;

         while ((var2 = var0.read(var1)) >= 0) {
            for (int var3 = 0; var3 < var2; var3++) {
               System.out.print(var1[var3]);
            }
         }
      } finally {
         closeQuietly(var0);
      }
   }

   private static void writeFileContent(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);

      try {
         byte[] var2 = new byte[1024];
         int var3 = 0;

         while ((var3 = var1.read(var2)) >= 0) {
            System.out.write(var2, 0, var3);
         }
      } finally {
         closeQuietly(var1);
      }
   }

   private static void closeQuietly(Closeable var0) {
      try {
         var0.close();
      } catch (IOException var2) {
         var2.printStackTrace(System.err);
      }
   }
}
