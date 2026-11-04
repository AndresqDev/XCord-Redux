package org.apache.logging.log4j.core.pattern;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "ThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"ex", "throwable", "exception"})
public class ThrowablePatternConverter extends LogEventPatternConverter {
   protected final List<PatternFormatter> formatters;
   private String rawOption;
   private final boolean subShortOption;
   private final boolean nonStandardLineSeparator;
   protected final ThrowableFormatOptions options;

   @Deprecated
   protected ThrowablePatternConverter(String var1, String var2, String[] var3) {
      this(var1, var2, var3, null);
   }

   protected ThrowablePatternConverter(String var1, String var2, String[] var3, Configuration var4) {
      super(var1, var2);
      this.options = ThrowableFormatOptions.newInstance(var3);
      if (var3 != null && var3.length > 0) {
         this.rawOption = var3[0];
      }

      if (this.options.getSuffix() != null) {
         PatternParser var5 = PatternLayout.createPatternParser(var4);
         List var6 = var5.parse(this.options.getSuffix());
         boolean var7 = false;

         for (PatternFormatter var9 : var6) {
            if (var9.handlesThrowable()) {
               var7 = true;
            }
         }

         if (!var7) {
            this.formatters = var6;
         } else {
            ArrayList var11 = new ArrayList();

            for (PatternFormatter var10 : var6) {
               if (!var10.handlesThrowable()) {
                  var11.add(var10);
               }
            }

            this.formatters = var11;
         }
      } else {
         this.formatters = Collections.emptyList();
      }

      this.subShortOption = "short.message".equalsIgnoreCase(this.rawOption)
         || "short.localizedMessage".equalsIgnoreCase(this.rawOption)
         || "short.fileName".equalsIgnoreCase(this.rawOption)
         || "short.lineNumber".equalsIgnoreCase(this.rawOption)
         || "short.methodName".equalsIgnoreCase(this.rawOption)
         || "short.className".equalsIgnoreCase(this.rawOption);
      this.nonStandardLineSeparator = !Strings.LINE_SEPARATOR.equals(this.options.getSeparator());
   }

   public static ThrowablePatternConverter newInstance(Configuration var0, String[] var1) {
      return new ThrowablePatternConverter("Throwable", "throwable", var1, var0);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      Throwable var3 = var1.getThrown();
      if (this.subShortOption) {
         this.formatSubShortOption(var3, this.getSuffix(var1), var2);
      } else if (var3 != null && this.options.anyLines()) {
         this.formatOption(var3, this.getSuffix(var1), var2);
      }
   }

   private void formatSubShortOption(Throwable var1, String var2, StringBuilder var3) {
      StackTraceElement var5 = null;
      if (var1 != null) {
         StackTraceElement[] var4 = var1.getStackTrace();
         if (var4 != null && var4.length > 0) {
            var5 = var4[0];
         }
      }

      if (var1 != null && var5 != null) {
         String var7 = "";
         if ("short.className".equalsIgnoreCase(this.rawOption)) {
            var7 = var5.getClassName();
         } else if ("short.methodName".equalsIgnoreCase(this.rawOption)) {
            var7 = var5.getMethodName();
         } else if ("short.lineNumber".equalsIgnoreCase(this.rawOption)) {
            var7 = String.valueOf(var5.getLineNumber());
         } else if ("short.message".equalsIgnoreCase(this.rawOption)) {
            var7 = var1.getMessage();
         } else if ("short.localizedMessage".equalsIgnoreCase(this.rawOption)) {
            var7 = var1.getLocalizedMessage();
         } else if ("short.fileName".equalsIgnoreCase(this.rawOption)) {
            var7 = var5.getFileName();
         }

         int var6 = var3.length();
         if (var6 > 0 && !Character.isWhitespace(var3.charAt(var6 - 1))) {
            var3.append(' ');
         }

         var3.append(var7);
         if (Strings.isNotBlank(var2)) {
            var3.append(' ');
            var3.append(var2);
         }
      }
   }

   private void formatOption(Throwable var1, String var2, StringBuilder var3) {
      int var4 = var3.length();
      if (var4 > 0 && !Character.isWhitespace(var3.charAt(var4 - 1))) {
         var3.append(' ');
      }

      if (this.options.allLines() && !this.nonStandardLineSeparator && !Strings.isNotBlank(var2)) {
         var1.printStackTrace(new PrintWriter(new StringBuilderWriter(var3)));
      } else {
         StringWriter var5 = new StringWriter();
         var1.printStackTrace(new PrintWriter(var5));
         String[] var6 = var5.toString().split(Strings.LINE_SEPARATOR);
         int var7 = this.options.minLines(var6.length) - 1;
         boolean var8 = Strings.isNotBlank(var2);

         for (int var9 = 0; var9 <= var7; var9++) {
            var3.append(var6[var9]);
            if (var8) {
               var3.append(' ');
               var3.append(var2);
            }

            if (var9 < var7) {
               var3.append(this.options.getSeparator());
            }
         }
      }
   }

   @Override
   public boolean handlesThrowable() {
      return true;
   }

   protected String getSuffix(LogEvent var1) {
      if (this.formatters.isEmpty()) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder();
         int var3 = 0;

         for (int var4 = this.formatters.size(); var3 < var4; var3++) {
            this.formatters.get(var3).format(var1, var2);
         }

         return var2.toString();
      }
   }

   public ThrowableFormatOptions getOptions() {
      return this.options;
   }
}
