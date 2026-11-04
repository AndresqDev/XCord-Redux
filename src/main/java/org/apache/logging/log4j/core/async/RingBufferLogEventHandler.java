package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;

public class RingBufferLogEventHandler implements LifecycleAware, SequenceReportingEventHandler<RingBufferLogEvent> {
   private static final int NOTIFY_PROGRESS_THRESHOLD = 50;
   private Sequence sequenceCallback;
   private int counter;
   private long threadId = -1L;

   public void setSequenceCallback(Sequence var1) {
      this.sequenceCallback = var1;
   }

   public void onEvent(RingBufferLogEvent var1, long var2, boolean var4) throws Exception {
      try {
         if (var1.isPopulated()) {
            var1.execute(var4);
         }
      } finally {
         var1.clear();
         this.notifyCallback(var2);
      }
   }

   private void notifyCallback(long var1) {
      if (++this.counter > 50) {
         this.sequenceCallback.set(var1);
         this.counter = 0;
      }
   }

   public long getThreadId() {
      return this.threadId;
   }

   public void onStart() {
      this.threadId = Thread.currentThread().getId();
   }

   public void onShutdown() {
   }
}
