package org.apache.logging.log4j.core.appender.rolling;

public interface RolloverListener {
   void rolloverTriggered(String var1);

   void rolloverComplete(String var1);
}
