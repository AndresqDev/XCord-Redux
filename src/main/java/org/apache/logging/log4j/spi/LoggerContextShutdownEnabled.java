package org.apache.logging.log4j.spi;

import java.util.List;

public interface LoggerContextShutdownEnabled {
   void addShutdownListener(LoggerContextShutdownAware var1);

   List<LoggerContextShutdownAware> getListeners();
}
