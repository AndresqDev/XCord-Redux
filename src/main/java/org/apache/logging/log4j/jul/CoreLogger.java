package org.apache.logging.log4j.jul;

import java.util.logging.Level;
import org.apache.logging.log4j.core.Logger;

public class CoreLogger extends ApiLogger {
   private final Logger logger;

   CoreLogger(Logger var1) {
      super(var1);
      this.logger = var1;
   }

   @Override
   public void setLevel(Level var1) throws SecurityException {
      super.doSetLevel(var1);
      this.logger.setLevel(LevelTranslator.toLevel(var1));
   }

   @Override
   public synchronized void setUseParentHandlers(boolean var1) {
      this.logger.setAdditive(var1);
   }

   @Override
   public synchronized boolean getUseParentHandlers() {
      return this.logger.isAdditive();
   }

   @Override
   public java.util.logging.Logger getParent() {
      Logger var1 = this.logger.getParent();
      return var1 == null ? null : java.util.logging.Logger.getLogger(var1.getName());
   }
}
