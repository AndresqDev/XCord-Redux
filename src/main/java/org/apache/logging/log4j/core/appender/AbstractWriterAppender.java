package org.apache.logging.log4j.core.appender;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Property;

public abstract class AbstractWriterAppender<M extends WriterManager> extends AbstractAppender {
   protected final boolean immediateFlush;
   private final M manager;
   private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private final Lock readLock = this.readWriteLock.readLock();

   protected AbstractWriterAppender(String var1, StringLayout var2, Filter var3, boolean var4, boolean var5, Property[] var6, M var7) {
      super(var1, var3, var2, var4, var6);
      this.manager = (M)var7;
      this.immediateFlush = var5;
   }

   @Deprecated
   protected AbstractWriterAppender(String var1, StringLayout var2, Filter var3, boolean var4, boolean var5, M var6) {
      super(var1, var3, var2, var4, Property.EMPTY_ARRAY);
      this.manager = (M)var6;
      this.immediateFlush = var5;
   }

   @Override
   public void append(LogEvent var1) {
      this.readLock.lock();

      try {
         String var2 = this.getStringLayout().toSerializable(var1);
         if (var2.length() > 0) {
            this.manager.write(var2);
            if (this.immediateFlush || var1.isEndOfBatch()) {
               this.manager.flush();
            }
         }
      } catch (AppenderLoggingException var6) {
         this.error("Unable to write " + this.manager.getName() + " for appender " + this.getName(), var1, var6);
         throw var6;
      } finally {
         this.readLock.unlock();
      }
   }

   public M getManager() {
      return this.manager;
   }

   public StringLayout getStringLayout() {
      return (StringLayout)this.getLayout();
   }

   @Override
   public void start() {
      if (this.getLayout() == null) {
         LOGGER.error("No layout set for the appender named [{}].", this.getName());
      }

      if (this.manager == null) {
         LOGGER.error("No OutputStreamManager set for the appender named [{}].", this.getName());
      }

      super.start();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }
}
