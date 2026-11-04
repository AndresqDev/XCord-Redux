package org.apache.logging.log4j.jul;

import java.util.logging.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;

public class CoreLoggerAdapter extends AbstractLoggerAdapter {
   private static final MessageFactory MESSAGE_FACTORY = new MessageFormatMessageFactory();

   protected Logger newLogger(String var1, LoggerContext var2) {
      ExtendedLogger var3 = var2.getLogger(var1, MESSAGE_FACTORY);
      return (Logger)(var3 instanceof org.apache.logging.log4j.core.Logger ? new CoreLogger((org.apache.logging.log4j.core.Logger)var3) : new ApiLogger(var3));
   }
}
