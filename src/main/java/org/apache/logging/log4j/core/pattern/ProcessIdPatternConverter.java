package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.ProcessIdUtil;

@Plugin(
   name = "ProcessIdPatternConverter",
   category = "Converter"
)
@ConverterKeys({"pid", "processId"})
public final class ProcessIdPatternConverter extends LogEventPatternConverter {
   private static final String DEFAULT_DEFAULT_VALUE = "???";
   private final String pid;

   private ProcessIdPatternConverter(String... var1) {
      super("Process ID", "pid");
      String var2 = var1.length > 0 ? var1[0] : "???";
      String var3 = ProcessIdUtil.getProcessId();
      this.pid = var3.equals("-") ? var2 : var3;
   }

   public String getProcessId() {
      return this.pid;
   }

   public static void main(String[] var0) {
      System.out.println((new ProcessIdPatternConverter()).pid);
   }

   public static ProcessIdPatternConverter newInstance(String[] var0) {
      return new ProcessIdPatternConverter(var0);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(this.pid);
   }
}
