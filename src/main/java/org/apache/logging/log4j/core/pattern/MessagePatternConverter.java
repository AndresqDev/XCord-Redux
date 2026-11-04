package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.Locale;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@Plugin(
   name = "MessagePatternConverter",
   category = "Converter"
)
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive({"allocation"})
public class MessagePatternConverter extends LogEventPatternConverter {
   private static final String LOOKUPS = "lookups";
   private static final String NOLOOKUPS = "nolookups";

   private MessagePatternConverter() {
      super("Message", "message");
   }

   private static TextRenderer loadMessageRenderer(String[] var0) {
      if (var0 != null) {
         String[] var1 = var0;
         int var2 = var0.length;
         int var3 = 0;

         while (var3 < var2) {
            String var4 = var1[var3];
            String var5 = var4.toUpperCase(Locale.ROOT);
            switch (var5) {
               case "ANSI":
                  if (Loader.isJansiAvailable()) {
                     return new JAnsiTextRenderer(var0, JAnsiTextRenderer.DefaultMessageStyleMap);
                  }

                  StatusLogger.getLogger().warn("You requested ANSI message rendering but JANSI is not on the classpath.");
                  return null;
               case "HTML":
                  return new HtmlTextRenderer(var0);
               default:
                  var3++;
            }
         }
      }

      return null;
   }

   public static MessagePatternConverter newInstance(Configuration var0, String[] var1) {
      String[] var2 = withoutLookupOptions(var1);
      TextRenderer var3 = loadMessageRenderer(var2);
      Object var4 = var2 != null && var2.length != 0
         ? new MessagePatternConverter.FormattedMessagePatternConverter(var2)
         : MessagePatternConverter.SimpleMessagePatternConverter.INSTANCE;
      if (var3 != null) {
         var4 = new MessagePatternConverter.RenderingPatternConverter((MessagePatternConverter)var4, var3);
      }

      return (MessagePatternConverter)var4;
   }

   private static String[] withoutLookupOptions(String[] var0) {
      if (var0 != null && var0.length != 0) {
         ArrayList var1 = new ArrayList(var0.length);

         for (String var5 : var0) {
            if (!"lookups".equalsIgnoreCase(var5) && !"nolookups".equalsIgnoreCase(var5)) {
               var1.add(var5);
            } else {
               LOGGER.info("The {} option will be ignored. Message Lookups are no longer supported.", var5);
            }
         }

         return var1.toArray(new String[0]);
      } else {
         return var0;
      }
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      throw new UnsupportedOperationException();
   }

   private static final class FormattedMessagePatternConverter extends MessagePatternConverter {
      private final String[] formats;

      FormattedMessagePatternConverter(String[] var1) {
         this.formats = var1;
      }

      @Override
      public void format(LogEvent var1, StringBuilder var2) {
         Message var3 = var1.getMessage();
         if (var3 instanceof StringBuilderFormattable) {
            if (var3 instanceof MultiFormatStringBuilderFormattable) {
               ((MultiFormatStringBuilderFormattable)var3).formatTo(this.formats, var2);
            } else {
               ((StringBuilderFormattable)var3).formatTo(var2);
            }
         } else if (var3 != null) {
            var2.append(var3 instanceof MultiformatMessage ? ((MultiformatMessage)var3).getFormattedMessage(this.formats) : var3.getFormattedMessage());
         }
      }
   }

   private static final class RenderingPatternConverter extends MessagePatternConverter {
      private final MessagePatternConverter delegate;
      private final TextRenderer textRenderer;

      RenderingPatternConverter(MessagePatternConverter var1, TextRenderer var2) {
         this.delegate = var1;
         this.textRenderer = var2;
      }

      @Override
      public void format(LogEvent var1, StringBuilder var2) {
         StringBuilder var3 = new StringBuilder(80);
         this.delegate.format(var1, var3);
         this.textRenderer.render(var3, var2);
      }
   }

   private static final class SimpleMessagePatternConverter extends MessagePatternConverter {
      private static final MessagePatternConverter INSTANCE = new MessagePatternConverter.SimpleMessagePatternConverter();

      @Override
      public void format(LogEvent var1, StringBuilder var2) {
         Message var3 = var1.getMessage();
         if (var3 instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable)var3).formatTo(var2);
         } else if (var3 != null) {
            var2.append(var3.getFormattedMessage());
         }
      }
   }
}
