package org.apache.logging.log4j.core.appender;

import java.lang.Thread.State;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.status.StatusLogger;

class AsyncAppenderEventDispatcher extends Log4jThread {
   private static final LogEvent STOP_EVENT = new Log4jLogEvent();
   private static final AtomicLong THREAD_COUNTER = new AtomicLong(0L);
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final AppenderControl errorAppender;
   private final List<AppenderControl> appenders;
   private final BlockingQueue<LogEvent> queue;
   private final AtomicBoolean stoppedRef;

   AsyncAppenderEventDispatcher(String var1, AppenderControl var2, List<AppenderControl> var3, BlockingQueue<LogEvent> var4) {
      super("AsyncAppenderEventDispatcher-" + THREAD_COUNTER.incrementAndGet() + "-" + var1);
      this.setDaemon(true);
      this.errorAppender = var2;
      this.appenders = var3;
      this.queue = var4;
      this.stoppedRef = new AtomicBoolean(false);
   }

   @Override
   public void run() {
      LOGGER.trace("{} has started.", this.getName());
      this.dispatchAll();
      this.dispatchRemaining();
   }

   private void dispatchAll() {
      while (!this.stoppedRef.get()) {
         LogEvent var1;
         try {
            var1 = this.queue.take();
         } catch (InterruptedException var3) {
            this.interrupt();
            break;
         }

         if (var1 == STOP_EVENT) {
            break;
         }

         var1.setEndOfBatch(this.queue.isEmpty());
         this.dispatch(var1);
      }

      LOGGER.trace("{} has stopped.", this.getName());
   }

   private void dispatchRemaining() {
      int var1 = 0;

      while (true) {
         LogEvent var2 = this.queue.poll();
         if (var2 == null) {
            LOGGER.trace("{} has processed the last {} remaining event(s).", this.getName(), var1);
            return;
         }

         if (var2 != STOP_EVENT) {
            var2.setEndOfBatch(this.queue.isEmpty());
            this.dispatch(var2);
            var1++;
         }
      }
   }

   void dispatch(LogEvent var1) {
      boolean var2 = false;

      for (int var3 = 0; var3 < this.appenders.size(); var3++) {
         AppenderControl var4 = this.appenders.get(var3);

         try {
            var4.callAppender(var1);
            var2 = true;
         } catch (Throwable var7) {
            LOGGER.trace("{} has failed to call appender {}", this.getName(), var4.getAppenderName(), var7);
         }
      }

      if (!var2 && this.errorAppender != null) {
         try {
            this.errorAppender.callAppender(var1);
         } catch (Throwable var6) {
            LOGGER.trace("{} has failed to call the error appender {}", this.getName(), this.errorAppender.getAppenderName(), var6);
         }
      }
   }

   void stop(long var1) throws InterruptedException {
      boolean var3 = this.stoppedRef.compareAndSet(false, true);
      if (var3) {
         LOGGER.trace("{} is signaled to stop.", this.getName());
      }

      while (State.NEW.equals(this.getState())) {
      }

      boolean var4 = this.queue.offer(STOP_EVENT);
      if (!var4) {
         this.interrupt();
      }

      this.join(var1);
   }
}
