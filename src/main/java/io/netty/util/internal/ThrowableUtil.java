package io.netty.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public final class ThrowableUtil {
   private ThrowableUtil() {
   }

   public static <T extends Throwable> T unknownStackTrace(T var0, Class<?> var1, String var2) {
      var0.setStackTrace(new StackTraceElement[]{new StackTraceElement(var1.getName(), var2, null, -1)});
      return (T)var0;
   }

   public static String stackTraceToString(Throwable var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      PrintStream var2 = new PrintStream(var1);
      var0.printStackTrace(var2);
      var2.flush();

      String var3;
      try {
         var3 = new String(var1.toByteArray());
      } finally {
         try {
            var1.close();
         } catch (IOException var10) {
         }
      }

      return var3;
   }

   public static boolean haveSuppressed() {
      return PlatformDependent.javaVersion() >= 7;
   }

   @SuppressJava6Requirement(
      reason = "Throwable addSuppressed is only available for >= 7. Has check for < 7."
   )
   public static void addSuppressed(Throwable var0, Throwable var1) {
      if (haveSuppressed()) {
         var0.addSuppressed(var1);
      }
   }

   public static void addSuppressedAndClear(Throwable var0, List<Throwable> var1) {
      addSuppressed(var0, var1);
      var1.clear();
   }

   public static void addSuppressed(Throwable var0, List<Throwable> var1) {
      for (Throwable var3 : var1) {
         addSuppressed(var0, var3);
      }
   }
}
