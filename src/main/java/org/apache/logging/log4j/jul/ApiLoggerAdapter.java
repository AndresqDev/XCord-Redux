package org.apache.logging.log4j.jul;

import java.util.logging.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFormatMessageFactory;
import org.apache.logging.log4j.spi.LoggerContext;

public class ApiLoggerAdapter extends AbstractLoggerAdapter {
   private static final MessageFactory MESSAGE_FACTORY = new MessageFormatMessageFactory();

   protected Logger newLogger(String var1, LoggerContext var2) {
      return new ApiLogger(var2.getLogger(var1, MESSAGE_FACTORY));
   }
}
