package org.fusesource.jansi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.fusesource.jansi.internal.CLibrary;

public class AnsiConsole {
   public static final PrintStream system_out = System.out;
   public static final PrintStream out = wrapSystemOut(system_out);
   public static final PrintStream system_err = System.err;
   public static final PrintStream err = wrapSystemErr(system_err);
   static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
   static final boolean IS_CYGWIN = IS_WINDOWS
      && System.getenv("PWD") != null
      && System.getenv("PWD").startsWith("/")
      && !"cygwin".equals(System.getenv("TERM"));
   static final boolean IS_MINGW_XTERM = IS_WINDOWS
      && System.getenv("MSYSTEM") != null
      && System.getenv("MSYSTEM").startsWith("MINGW")
      && "xterm".equals(System.getenv("TERM"));
   private static AnsiConsole.JansiOutputType jansiOutputType;
   static final AnsiConsole.JansiOutputType JANSI_STDOUT_TYPE = jansiOutputType;
   static final AnsiConsole.JansiOutputType JANSI_STDERR_TYPE = jansiOutputType;
   private static int installed;

   private AnsiConsole() {
   }

   @Deprecated
   public static OutputStream wrapOutputStream(OutputStream var0) {
      try {
         return wrapOutputStream(var0, CLibrary.STDOUT_FILENO);
      } catch (Throwable var2) {
         return wrapOutputStream(var0, 1);
      }
   }

   public static PrintStream wrapSystemOut(PrintStream var0) {
      try {
         return wrapPrintStream(var0, CLibrary.STDOUT_FILENO);
      } catch (Throwable var2) {
         return wrapPrintStream(var0, 1);
      }
   }

   @Deprecated
   public static OutputStream wrapErrorOutputStream(OutputStream var0) {
      try {
         return wrapOutputStream(var0, CLibrary.STDERR_FILENO);
      } catch (Throwable var2) {
         return wrapOutputStream(var0, 2);
      }
   }

   public static PrintStream wrapSystemErr(PrintStream var0) {
      try {
         return wrapPrintStream(var0, CLibrary.STDERR_FILENO);
      } catch (Throwable var2) {
         return wrapPrintStream(var0, 2);
      }
   }

   @Deprecated
   public static OutputStream wrapOutputStream(OutputStream var0, int var1) {
      if (Boolean.getBoolean("jansi.passthrough")) {
         jansiOutputType = AnsiConsole.JansiOutputType.PASSTHROUGH;
         return var0;
      } else if (Boolean.getBoolean("jansi.strip")) {
         jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
         return new AnsiOutputStream(var0);
      } else if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW_XTERM) {
         try {
            jansiOutputType = AnsiConsole.JansiOutputType.WINDOWS;
            return new WindowsAnsiOutputStream(var0, var1 == CLibrary.STDOUT_FILENO);
         } catch (Throwable var3) {
            jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
            return new AnsiOutputStream(var0);
         }
      } else {
         try {
            boolean var2 = Boolean.getBoolean("jansi.force");
            if (!var2 && CLibrary.isatty(var1) == 0) {
               jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
               return new AnsiOutputStream(var0);
            }
         } catch (Throwable var4) {
         }

         jansiOutputType = AnsiConsole.JansiOutputType.RESET_ANSI_AT_CLOSE;
         return new FilterOutputStream(var0) {
            @Override
            public void close() throws IOException {
               this.write(AnsiOutputStream.RESET_CODE);
               this.flush();
               super.close();
            }
         };
      }
   }

   public static PrintStream wrapPrintStream(PrintStream var0, int var1) {
      if (Boolean.getBoolean("jansi.passthrough")) {
         jansiOutputType = AnsiConsole.JansiOutputType.PASSTHROUGH;
         return var0;
      } else if (Boolean.getBoolean("jansi.strip")) {
         jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
         return new AnsiPrintStream(var0);
      } else if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW_XTERM) {
         try {
            jansiOutputType = AnsiConsole.JansiOutputType.WINDOWS;
            return new WindowsAnsiPrintStream(var0, var1 == CLibrary.STDOUT_FILENO);
         } catch (Throwable var3) {
            jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
            return new AnsiPrintStream(var0);
         }
      } else {
         try {
            boolean var2 = Boolean.getBoolean("jansi.force");
            if (!var2 && CLibrary.isatty(var1) == 0) {
               jansiOutputType = AnsiConsole.JansiOutputType.STRIP_ANSI;
               return new AnsiPrintStream(var0);
            }
         } catch (Throwable var4) {
         }

         jansiOutputType = AnsiConsole.JansiOutputType.RESET_ANSI_AT_CLOSE;
         return new FilterPrintStream(var0) {
            @Override
            public void close() {
               this.ps.print("\u001b[0m");
               this.ps.flush();
               super.close();
            }
         };
      }
   }

   public static PrintStream out() {
      return out;
   }

   public static PrintStream err() {
      return err;
   }

   public static synchronized void systemInstall() {
      installed++;
      if (installed == 1) {
         System.setOut(out);
         System.setErr(err);
      }
   }

   public static synchronized void systemUninstall() {
      installed--;
      if (installed == 0) {
         System.setOut(system_out);
         System.setErr(system_err);
      }
   }

   static enum JansiOutputType {
      PASSTHROUGH("just pass through, ANSI escape codes are supposed to be supported by terminal"),
      RESET_ANSI_AT_CLOSE("like pass through but reset ANSI attributes when closing the stream"),
      STRIP_ANSI("strip ANSI escape codes, for example when output is not a terminal"),
      WINDOWS(
         "detect ANSI escape codes and transform Jansi-supported ones into a Windows API to get desired effect (since ANSI escape codes are not natively supported by Windows terminals like cmd.exe or PowerShell)"
      );

      private final String description;

      private JansiOutputType(String var3) {
         this.description = var3;
      }

      String getDescription() {
         return this.description;
      }
   }
}
