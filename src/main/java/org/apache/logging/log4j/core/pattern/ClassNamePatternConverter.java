package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.LocationAware;

@Plugin(
   name = "ClassNamePatternConverter",
   category = "Converter"
)
@ConverterKeys({"C", "class"})
public final class ClassNamePatternConverter extends NamePatternConverter implements LocationAware {
   private static final String NA = "?";

   private ClassNamePatternConverter(String[] var1) {
      super("Class Name", "class name", var1);
   }

   public static ClassNamePatternConverter newInstance(String[] var0) {
      return new ClassNamePatternConverter(var0);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      StackTraceElement var3 = var1.getSource();
      if (var3 == null) {
         var2.append("?");
      } else {
         this.abbreviate(var3.getClassName(), var2);
      }
   }

   @Override
   public boolean requiresLocation() {
      return true;
   }
}
