package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.LocationAware;

public class PatternFormatter {
   public static final PatternFormatter[] EMPTY_ARRAY = new PatternFormatter[0];
   private final LogEventPatternConverter converter;
   private final FormattingInfo field;
   private final boolean skipFormattingInfo;

   public PatternFormatter(LogEventPatternConverter var1, FormattingInfo var2) {
      this.converter = var1;
      this.field = var2;
      this.skipFormattingInfo = var2 == FormattingInfo.getDefault();
   }

   public void format(LogEvent var1, StringBuilder var2) {
      if (this.skipFormattingInfo) {
         this.converter.format(var1, var2);
      } else {
         this.formatWithInfo(var1, var2);
      }
   }

   private void formatWithInfo(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();
      this.converter.format(var1, var2);
      this.field.format(var3, var2);
   }

   public LogEventPatternConverter getConverter() {
      return this.converter;
   }

   public FormattingInfo getFormattingInfo() {
      return this.field;
   }

   public boolean handlesThrowable() {
      return this.converter.handlesThrowable();
   }

   public boolean requiresLocation() {
      return this.converter instanceof LocationAware && ((LocationAware)this.converter).requiresLocation();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString());
      var1.append("[converter=");
      var1.append(this.converter);
      var1.append(", field=");
      var1.append(this.field);
      var1.append(']');
      return var1.toString();
   }
}
