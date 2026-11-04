package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.util.OptionConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
abstract class SimpleLiteralPatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private SimpleLiteralPatternConverter() {
      super("SimpleLiteral", "literal");
   }

   static LogEventPatternConverter of(String var0, boolean var1) {
      String var2 = var1 ? OptionConverter.convertSpecialChars(var0) : var0;
      return of(var2);
   }

   static LogEventPatternConverter of(String var0) {
      if (var0 == null || var0.isEmpty()) {
         return SimpleLiteralPatternConverter.Noop.INSTANCE;
      } else {
         return (LogEventPatternConverter)(" ".equals(var0)
            ? SimpleLiteralPatternConverter.Space.INSTANCE
            : new SimpleLiteralPatternConverter.StringValue(var0));
      }
   }

   @Override
   public final void format(LogEvent var1, StringBuilder var2) {
      this.format(var2);
   }

   @Override
   public final void format(Object var1, StringBuilder var2) {
      this.format(var2);
   }

   @Override
   public final void format(StringBuilder var1, Object... var2) {
      this.format(var1);
   }

   abstract void format(StringBuilder var1);

   @Override
   public final boolean isVariable() {
      return false;
   }

   @Override
   public final boolean handlesThrowable() {
      return false;
   }

   private static final class Noop extends SimpleLiteralPatternConverter {
      private static final SimpleLiteralPatternConverter.Noop INSTANCE = new SimpleLiteralPatternConverter.Noop();

      @Override
      void format(StringBuilder var1) {
      }
   }

   private static final class Space extends SimpleLiteralPatternConverter {
      private static final SimpleLiteralPatternConverter.Space INSTANCE = new SimpleLiteralPatternConverter.Space();

      @Override
      void format(StringBuilder var1) {
         var1.append(' ');
      }
   }

   private static final class StringValue extends SimpleLiteralPatternConverter {
      private final String literal;

      StringValue(String var1) {
         this.literal = var1;
      }

      @Override
      void format(StringBuilder var1) {
         var1.append(this.literal);
      }
   }
}
