package io.github.waterfallmc.waterfall.log4j;

import java.util.logging.Handler;
import java.util.logging.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;

public final class WaterfallLogger {
   private WaterfallLogger() {
   }

   public static Logger create() {
      org.apache.logging.log4j.Logger var0 = LogManager.getRootLogger();
      System.setOut(IoBuilder.forLogger(var0).setLevel(Level.INFO).buildPrintStream());
      System.setErr(IoBuilder.forLogger(var0).setLevel(Level.ERROR).buildPrintStream());
      Logger var1 = Logger.getLogger("");
      var1.setUseParentHandlers(false);

      for (Handler var5 : var1.getHandlers()) {
         var1.removeHandler(var5);
      }

      var1.setLevel(java.util.logging.Level.ALL);
      var1.addHandler(new Log4JLogHandler());
      return Logger.getLogger("BungeeCord");
   }
}
