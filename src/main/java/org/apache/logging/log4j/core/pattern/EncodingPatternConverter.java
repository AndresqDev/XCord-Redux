package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@Plugin(
   name = "encode",
   category = "Converter"
)
@ConverterKeys({"enc", "encode"})
@PerformanceSensitive({"allocation"})
public final class EncodingPatternConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;
   private final EncodingPatternConverter.EscapeFormat escapeFormat;

   private EncodingPatternConverter(List<PatternFormatter> var1, EncodingPatternConverter.EscapeFormat var2) {
      super("encode", "encode");
      this.formatters = var1;
      this.escapeFormat = var2;
   }

   @Override
   public boolean handlesThrowable() {
      return this.formatters != null && this.formatters.stream().map(PatternFormatter::getConverter).anyMatch(LogEventPatternConverter::handlesThrowable);
   }

   public static EncodingPatternConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length > 2 || var1.length == 0) {
         LOGGER.error("Incorrect number of options on escape. Expected 1 or 2, but received {}", var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on escape");
         return null;
      } else {
         EncodingPatternConverter.EscapeFormat var2 = var1.length < 2
            ? EncodingPatternConverter.EscapeFormat.HTML
            : EnglishEnums.valueOf(EncodingPatternConverter.EscapeFormat.class, var1[1], EncodingPatternConverter.EscapeFormat.HTML);
         PatternParser var3 = PatternLayout.createPatternParser(var0);
         List var4 = var3.parse(var1[0]);
         return new EncodingPatternConverter(var4, var2);
      }
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();

      for (int var4 = 0; var4 < this.formatters.size(); var4++) {
         this.formatters.get(var4).format(var1, var2);
      }

      this.escapeFormat.escape(var2, var3);
   }

   private static enum EscapeFormat {
      HTML {
         @Override
         void escape(StringBuilder var1, int var2) {
            int var3 = var1.length();
            int var4 = var3;

            for (int var5 = var3 - 1; var5 >= var2; var5--) {
               char var6 = var1.charAt(var5);
               String var7 = this.escapeChar(var6);
               if (var7 != null) {
                  var4 = var5;

                  for (int var8 = 0; var8 < var7.length() - 1; var8++) {
                     var1.append(' ');
                  }
               }
            }

            int var9 = var3 - 1;

            for (int var10 = var1.length(); var9 >= var4; var9--) {
               char var11 = var1.charAt(var9);
               String var12 = this.escapeChar(var11);
               if (var12 == null) {
                  var1.setCharAt(--var10, var11);
               } else {
                  var1.replace(var10 - var12.length(), var10, var12);
                  var10 -= var12.length();
               }
            }
         }

         private String escapeChar(char var1) {
            switch (var1) {
               case '\n':
                  return "\\n";
               case '\r':
                  return "\\r";
               case '"':
                  return "&quot;";
               case '&':
                  return "&amp;";
               case '\'':
                  return "&apos;";
               case '/':
                  return "&#x2F;";
               case '<':
                  return "&lt;";
               case '>':
                  return "&gt;";
               default:
                  return null;
            }
         }
      },
      JSON {
         @Override
         void escape(StringBuilder var1, int var2) {
            StringBuilders.escapeJson(var1, var2);
         }
      },
      CRLF {
         @Override
         void escape(StringBuilder var1, int var2) {
            int var3 = var1.length();
            int var4 = var3;

            for (int var5 = var3 - 1; var5 >= var2; var5--) {
               char var6 = var1.charAt(var5);
               if (var6 == '\r' || var6 == '\n') {
                  var4 = var5;
                  var1.append(' ');
               }
            }

            int var8 = var3 - 1;

            for (int var9 = var1.length(); var8 >= var4; var8--) {
               char var7 = var1.charAt(var8);
               switch (var7) {
                  case '\n':
                     var1.setCharAt(--var9, 'n');
                     var1.setCharAt(--var9, '\\');
                     break;
                  case '\r':
                     var1.setCharAt(--var9, 'r');
                     var1.setCharAt(--var9, '\\');
                     break;
                  default:
                     var1.setCharAt(--var9, var7);
               }
            }
         }
      },
      XML {
         @Override
         void escape(StringBuilder var1, int var2) {
            StringBuilders.escapeXml(var1, var2);
         }
      };

      private EscapeFormat() {
      }

      abstract void escape(StringBuilder var1, int var2);
   }
}
