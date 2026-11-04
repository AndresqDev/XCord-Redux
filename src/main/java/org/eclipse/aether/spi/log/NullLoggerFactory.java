package org.eclipse.aether.spi.log;

public final class NullLoggerFactory implements LoggerFactory {
   public static final LoggerFactory INSTANCE = new NullLoggerFactory();
   public static final Logger LOGGER = new NullLogger();

   @Override
   public Logger getLogger(String var1) {
      return LOGGER;
   }

   private NullLoggerFactory() {
   }

   public static Logger getSafeLogger(LoggerFactory var0, Class<?> var1) {
      if (var0 == null) {
         return LOGGER;
      } else {
         Logger var2 = var0.getLogger(var1.getName());
         return var2 == null ? LOGGER : var2;
      }
   }
}
