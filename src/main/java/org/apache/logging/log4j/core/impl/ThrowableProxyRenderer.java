package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.core.pattern.TextRenderer;

class ThrowableProxyRenderer {
   private static final String TAB = "\t";
   private static final String CAUSED_BY_LABEL = "Caused by: ";
   private static final String SUPPRESSED_LABEL = "Suppressed: ";
   private static final String WRAPPED_BY_LABEL = "Wrapped by: ";

   private ThrowableProxyRenderer() {
   }

   static void formatWrapper(StringBuilder var0, ThrowableProxy var1, List<String> var2, TextRenderer var3, String var4, String var5) {
      Throwable var6 = var1.getCauseProxy() != null ? var1.getCauseProxy().getThrowable() : null;
      if (var6 != null) {
         formatWrapper(var0, var1.getCauseProxy(), var2, var3, var4, var5);
         var0.append("Wrapped by: ");
         renderSuffix(var4, var0, var3);
      }

      renderOn(var1, var0, var3);
      renderSuffix(var4, var0, var3);
      var3.render(var5, var0, "Text");
      formatElements(var0, "", var1.getCommonElementCount(), var1.getThrowable().getStackTrace(), var1.getExtendedStackTrace(), var2, var3, var4, var5);
   }

   private static void formatCause(StringBuilder var0, String var1, ThrowableProxy var2, List<String> var3, TextRenderer var4, String var5, String var6) {
      formatThrowableProxy(var0, var1, "Caused by: ", var2, var3, var4, var5, var6);
   }

   private static void formatThrowableProxy(
      StringBuilder var0, String var1, String var2, ThrowableProxy var3, List<String> var4, TextRenderer var5, String var6, String var7
   ) {
      if (var3 != null) {
         var5.render(var1, var0, "Prefix");
         var5.render(var2, var0, "CauseLabel");
         renderOn(var3, var0, var5);
         renderSuffix(var6, var0, var5);
         var5.render(var7, var0, "Text");
         formatElements(var0, var1, var3.getCommonElementCount(), var3.getStackTrace(), var3.getExtendedStackTrace(), var4, var5, var6, var7);
         formatSuppressed(var0, var1 + "\t", var3.getSuppressedProxies(), var4, var5, var6, var7);
         formatCause(var0, var1, var3.getCauseProxy(), var4, var5, var6, var7);
      }
   }

   private static void formatSuppressed(StringBuilder var0, String var1, ThrowableProxy[] var2, List<String> var3, TextRenderer var4, String var5, String var6) {
      if (var2 != null) {
         for (ThrowableProxy var10 : var2) {
            formatThrowableProxy(var0, var1, "Suppressed: ", var10, var3, var4, var5, var6);
         }
      }
   }

   private static void formatElements(
      StringBuilder var0,
      String var1,
      int var2,
      StackTraceElement[] var3,
      ExtendedStackTraceElement[] var4,
      List<String> var5,
      TextRenderer var6,
      String var7,
      String var8
   ) {
      if (var5 != null && !var5.isEmpty()) {
         int var13 = 0;

         for (int var14 = 0; var14 < var4.length; var14++) {
            if (!ignoreElement(var3[var14], var5)) {
               if (var13 > 0) {
                  appendSuppressedCount(var0, var1, var13, var6, var7, var8);
                  var13 = 0;
               }

               formatEntry(var4[var14], var0, var1, var6, var7, var8);
            } else {
               var13++;
            }
         }

         if (var13 > 0) {
            appendSuppressedCount(var0, var1, var13, var6, var7, var8);
         }
      } else {
         for (ExtendedStackTraceElement var12 : var4) {
            formatEntry(var12, var0, var1, var6, var7, var8);
         }
      }

      if (var2 != 0) {
         var6.render(var1, var0, "Prefix");
         var6.render("\t... ", var0, "More");
         var6.render(Integer.toString(var2), var0, "More");
         var6.render(" more", var0, "More");
         renderSuffix(var7, var0, var6);
         var6.render(var8, var0, "Text");
      }
   }

   private static void renderSuffix(String var0, StringBuilder var1, TextRenderer var2) {
      if (!var0.isEmpty()) {
         var2.render(" ", var1, "Suffix");
         var2.render(var0, var1, "Suffix");
      }
   }

   private static void appendSuppressedCount(StringBuilder var0, String var1, int var2, TextRenderer var3, String var4, String var5) {
      var3.render(var1, var0, "Prefix");
      if (var2 == 1) {
         var3.render("\t... ", var0, "Suppressed");
      } else {
         var3.render("\t... suppressed ", var0, "Suppressed");
         var3.render(Integer.toString(var2), var0, "Suppressed");
         var3.render(" lines", var0, "Suppressed");
      }

      renderSuffix(var4, var0, var3);
      var3.render(var5, var0, "Text");
   }

   private static void formatEntry(ExtendedStackTraceElement var0, StringBuilder var1, String var2, TextRenderer var3, String var4, String var5) {
      var3.render(var2, var1, "Prefix");
      var3.render("\tat ", var1, "At");
      var0.renderOn(var1, var3);
      renderSuffix(var4, var1, var3);
      var3.render(var5, var1, "Text");
   }

   private static boolean ignoreElement(StackTraceElement var0, List<String> var1) {
      if (var1 != null) {
         String var2 = var0.getClassName();

         for (String var4 : var1) {
            if (var2.startsWith(var4)) {
               return true;
            }
         }
      }

      return false;
   }

   static void formatExtendedStackTraceTo(ThrowableProxy var0, StringBuilder var1, List<String> var2, TextRenderer var3, String var4, String var5) {
      var3.render(var0.getName(), var1, "Name");
      var3.render(": ", var1, "NameMessageSeparator");
      var3.render(var0.getMessage(), var1, "Message");
      renderSuffix(var4, var1, var3);
      var3.render(var5, var1, "Text");
      StackTraceElement[] var6 = var0.getThrowable() != null ? var0.getThrowable().getStackTrace() : null;
      formatElements(var1, "", 0, var6, var0.getExtendedStackTrace(), var2, var3, var4, var5);
      formatSuppressed(var1, "\t", var0.getSuppressedProxies(), var2, var3, var4, var5);
      formatCause(var1, "", var0.getCauseProxy(), var2, var3, var4, var5);
   }

   static void formatCauseStackTrace(ThrowableProxy var0, StringBuilder var1, List<String> var2, TextRenderer var3, String var4, String var5) {
      ThrowableProxy var6 = var0.getCauseProxy();
      if (var6 != null) {
         formatWrapper(var1, var6, var2, var3, var4, var5);
         var1.append("Wrapped by: ");
         renderSuffix(var4, var1, var3);
      }

      renderOn(var0, var1, var3);
      renderSuffix(var4, var1, var3);
      var3.render(var5, var1, "Text");
      formatElements(var1, "", 0, var0.getStackTrace(), var0.getExtendedStackTrace(), var2, var3, var4, var5);
   }

   private static void renderOn(ThrowableProxy var0, StringBuilder var1, TextRenderer var2) {
      String var3 = var0.getMessage();
      var2.render(var0.getName(), var1, "Name");
      if (var3 != null) {
         var2.render(": ", var1, "NameMessageSeparator");
         var2.render(var3, var1, "Message");
      }
   }
}
