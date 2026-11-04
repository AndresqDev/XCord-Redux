package org.apache.logging.log4j.core.util;

public interface WatchEventService {
   void subscribe(WatchManager var1);

   void unsubscribe(WatchManager var1);
}
