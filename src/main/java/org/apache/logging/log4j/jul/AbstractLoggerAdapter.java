package org.apache.logging.log4j.jul;

import java.util.logging.Logger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

public abstract class AbstractLoggerAdapter extends org.apache.logging.log4j.spi.AbstractLoggerAdapter<Logger> {
   @Override
   protected LoggerContext getContext() {
      return this.getContext(
         org.apache.logging.log4j.LogManager.getFactory().isClassLoaderDependent() ? StackLocatorUtil.getCallerClass(java.util.logging.LogManager.class) : null
      );
   }
}
