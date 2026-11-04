package org.apache.logging.log4j.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.core.pattern.JAnsiTextRenderer;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class ThrowableFormatOptions {
   private static final int DEFAULT_LINES = Integer.MAX_VALUE;
   protected static final ThrowableFormatOptions DEFAULT = new ThrowableFormatOptions();
   private static final String FULL = "full";
   private static final String NONE = "none";
   private static final String SHORT = "short";
   private final TextRenderer textRenderer;
   private final int lines;
   private final String separator;
   private final String suffix;
   private final List<String> ignorePackages;
   public static final String CLASS_NAME = "short.className";
   public static final String METHOD_NAME = "short.methodName";
   public static final String LINE_NUMBER = "short.lineNumber";
   public static final String FILE_NAME = "short.fileName";
   public static final String MESSAGE = "short.message";
   public static final String LOCALIZED_MESSAGE = "short.localizedMessage";

   protected ThrowableFormatOptions(int var1, String var2, List<String> var3, TextRenderer var4, String var5) {
      this.lines = var1;
      this.separator = var2 == null ? Strings.LINE_SEPARATOR : var2;
      this.ignorePackages = var3;
      this.textRenderer = (TextRenderer)(var4 == null ? PlainTextRenderer.getInstance() : var4);
      this.suffix = var5;
   }

   protected ThrowableFormatOptions(List<String> var1) {
      this(Integer.MAX_VALUE, null, var1, null, null);
   }

   protected ThrowableFormatOptions() {
      this(Integer.MAX_VALUE, null, null, null, null);
   }

   public int getLines() {
      return this.lines;
   }

   public String getSeparator() {
      return this.separator;
   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   public List<String> getIgnorePackages() {
      return this.ignorePackages;
   }

   public boolean allLines() {
      return this.lines == Integer.MAX_VALUE;
   }

   public boolean anyLines() {
      return this.lines > 0;
   }

   public int minLines(int var1) {
      return this.lines > var1 ? var1 : this.lines;
   }

   public boolean hasPackages() {
      return this.ignorePackages != null && !this.ignorePackages.isEmpty();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('{').append(this.allLines() ? "full" : (this.lines == 2 ? "short" : (this.anyLines() ? String.valueOf(this.lines) : "none"))).append('}');
      var1.append("{separator(").append(this.separator).append(")}");
      if (this.hasPackages()) {
         var1.append("{filters(");

         for (String var3 : this.ignorePackages) {
            var1.append(var3).append(',');
         }

         var1.deleteCharAt(var1.length() - 1);
         var1.append(")}");
      }

      return var1.toString();
   }

   public static ThrowableFormatOptions newInstance(String[] var0) {
      if (var0 != null && var0.length != 0) {
         if (var0.length == 1 && Strings.isNotEmpty(var0[0])) {
            String[] var1 = var0[0].split(Patterns.COMMA_SEPARATOR, 2);
            String var2 = var1[0].trim();

            try (Scanner var3 = new Scanner(var2)) {
               if (var1.length > 1 && (var2.equalsIgnoreCase("full") || var2.equalsIgnoreCase("short") || var2.equalsIgnoreCase("none") || var3.hasNextInt())) {
                  var0 = new String[]{var2, var1[1].trim()};
               }
            }
         }

         int var24 = DEFAULT.lines;
         String var25 = DEFAULT.separator;
         Object var26 = DEFAULT.ignorePackages;
         Object var27 = DEFAULT.textRenderer;
         String var5 = DEFAULT.getSuffix();

         for (String var9 : var0) {
            if (var9 != null) {
               String var10 = var9.trim();
               if (!var10.isEmpty()) {
                  if (var10.startsWith("separator(") && var10.endsWith(")")) {
                     var25 = var10.substring("separator(".length(), var10.length() - 1);
                  } else if (var10.startsWith("filters(") && var10.endsWith(")")) {
                     String var28 = var10.substring("filters(".length(), var10.length() - 1);
                     if (var28.length() > 0) {
                        String[] var12 = var28.split(Patterns.COMMA_SEPARATOR);
                        if (var12.length > 0) {
                           var26 = new ArrayList(var12.length);

                           for (String var16 : var12) {
                              var16 = var16.trim();
                              if (var16.length() > 0) {
                                 var26.add(var16);
                              }
                           }
                        }
                     }
                  } else if (var10.equalsIgnoreCase("none")) {
                     var24 = 0;
                  } else if (!var10.equalsIgnoreCase("short")
                     && !var10.equalsIgnoreCase("short.className")
                     && !var10.equalsIgnoreCase("short.methodName")
                     && !var10.equalsIgnoreCase("short.lineNumber")
                     && !var10.equalsIgnoreCase("short.fileName")
                     && !var10.equalsIgnoreCase("short.message")
                     && !var10.equalsIgnoreCase("short.localizedMessage")) {
                     if ((!var10.startsWith("ansi(") || !var10.endsWith(")")) && !var10.equals("ansi")) {
                        if (var10.startsWith("S(") && var10.endsWith(")")) {
                           var5 = var10.substring("S(".length(), var10.length() - 1);
                        } else if (var10.startsWith("suffix(") && var10.endsWith(")")) {
                           var5 = var10.substring("suffix(".length(), var10.length() - 1);
                        } else if (!var10.equalsIgnoreCase("full")) {
                           var24 = Integer.parseInt(var10);
                        }
                     } else if (Loader.isJansiAvailable()) {
                        String var11 = var10.equals("ansi") ? "" : var10.substring("ansi(".length(), var10.length() - 1);
                        var27 = new JAnsiTextRenderer(new String[]{null, var11}, JAnsiTextRenderer.DefaultExceptionStyleMap);
                     } else {
                        StatusLogger.getLogger()
                           .warn(
                              "You requested ANSI exception rendering but JANSI is not on the classpath. Please see https://logging.apache.org/log4j/2.x/runtime-dependencies.html"
                           );
                     }
                  } else {
                     var24 = 2;
                  }
               }
            }
         }

         return new ThrowableFormatOptions(var24, var25, (List<String>)var26, (TextRenderer)var27, var5);
      } else {
         return DEFAULT;
      }
   }

   public String getSuffix() {
      return this.suffix;
   }
}
