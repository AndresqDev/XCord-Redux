package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.util.Log4jThread;

public class DefaultAsyncQueueFullPolicy implements AsyncQueueFullPolicy {
   @Override
   public EventRoute getRoute(long var1, Level var3) {
      Thread var4 = Thread.currentThread();
      return var4.getId() != var1 && !(var4 instanceof Log4jThread) ? EventRoute.ENQUEUE : EventRoute.SYNCHRONOUS;
   }
}
