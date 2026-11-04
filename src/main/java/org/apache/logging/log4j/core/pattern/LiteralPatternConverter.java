package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.OptionConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public final class LiteralPatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private final String literal;
   private final Configuration config;
   private final boolean substitute;

   public LiteralPatternConverter(Configuration var1, String var2, boolean var3) {
      super("Literal", "literal");
      this.literal = var3 ? OptionConverter.convertSpecialChars(var2) : var2;
      this.config = var1;
      this.substitute = var1 != null && containsSubstitutionSequence(var2);
   }

   static boolean containsSubstitutionSequence(String var0) {
      return var0 != null && var0.contains("${");
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(this.substitute ? this.config.getStrSubstitutor().replace(var1, this.literal) : this.literal);
   }

   @Override
   public void format(Object var1, StringBuilder var2) {
      var2.append(this.substitute ? this.config.getStrSubstitutor().replace(this.literal) : this.literal);
   }

   @Override
   public void format(StringBuilder var1, Object... var2) {
      var1.append(this.substitute ? this.config.getStrSubstitutor().replace(this.literal) : this.literal);
   }

   public String getLiteral() {
      return this.literal;
   }

   @Override
   public boolean isVariable() {
      return false;
   }

   @Override
   public String toString() {
      return "LiteralPatternConverter[literal=" + this.literal + ", config=" + this.config + ", substitute=" + this.substitute + "]";
   }
}
