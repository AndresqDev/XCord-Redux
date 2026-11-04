package org.apache.logging.log4j.core.appender;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.ArrayBlockingQueueFactory;
import org.apache.logging.log4j.core.async.AsyncQueueFullMessageUtil;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicyFactory;
import org.apache.logging.log4j.core.async.BlockingQueueFactory;
import org.apache.logging.log4j.core.async.DiscardingAsyncQueueFullPolicy;
import org.apache.logging.log4j.core.async.EventRoute;
import org.apache.logging.log4j.core.async.InternalAsyncUtil;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.spi.AbstractLogger;

@Plugin(
   name = "Async",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class AsyncAppender extends AbstractAppender {
   private static final int DEFAULT_QUEUE_SIZE = 1024;
   private final BlockingQueue<LogEvent> queue;
   private final int queueSize;
   private final boolean blocking;
   private final long shutdownTimeout;
   private final Configuration config;
   private final AppenderRef[] appenderRefs;
   private final String errorRef;
   private final boolean includeLocation;
   private AppenderControl errorAppender;
   private AsyncAppenderEventDispatcher dispatcher;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;

   private AsyncAppender(
      String var1,
      Filter var2,
      AppenderRef[] var3,
      String var4,
      int var5,
      boolean var6,
      boolean var7,
      long var8,
      Configuration var10,
      boolean var11,
      BlockingQueueFactory<LogEvent> var12,
      Property[] var13
   ) {
      super(var1, var2, null, var7, var13);
      this.queue = var12.create(var5);
      this.queueSize = var5;
      this.blocking = var6;
      this.shutdownTimeout = var8;
      this.config = var10;
      this.appenderRefs = var3;
      this.errorRef = var4;
      this.includeLocation = var11;
   }

   @Override
   public void start() {
      Map var1 = this.config.getAppenders();
      ArrayList var2 = new ArrayList();

      for (AppenderRef var6 : this.appenderRefs) {
         Appender var7 = (Appender)var1.get(var6.getRef());
         if (var7 != null) {
            var2.add(new AppenderControl(var7, var6.getLevel(), var6.getFilter()));
         } else {
            LOGGER.error("No appender named {} was configured", var6);
         }
      }

      if (this.errorRef != null) {
         Appender var8 = (Appender)var1.get(this.errorRef);
         if (var8 != null) {
            this.errorAppender = new AppenderControl(var8, null, null);
         } else {
            LOGGER.error("Unable to set up error Appender. No appender named {} was configured", this.errorRef);
         }
      }

      if (var2.size() > 0) {
         this.dispatcher = new AsyncAppenderEventDispatcher(this.getName(), this.errorAppender, var2, this.queue);
      } else if (this.errorRef == null) {
         throw new ConfigurationException("No appenders are available for AsyncAppender " + this.getName());
      }

      this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
      this.dispatcher.start();
      super.start();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      LOGGER.trace("AsyncAppender stopping. Queue still has {} events.", this.queue.size());

      try {
         this.dispatcher.stop(this.shutdownTimeout);
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         LOGGER.warn("Interrupted while stopping AsyncAppender {}", this.getName());
      }

      LOGGER.trace("AsyncAppender stopped. Queue has {} events.", this.queue.size());
      if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
         LOGGER.trace(
            "AsyncAppender: {} discarded {} events.", this.asyncQueueFullPolicy, DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy)
         );
      }

      this.setStopped();
      return true;
   }

   @Override
   public void append(LogEvent var1) {
      if (!this.isStarted()) {
         throw new IllegalStateException("AsyncAppender " + this.getName() + " is not active");
      } else {
         Log4jLogEvent var2 = Log4jLogEvent.createMemento(var1, this.includeLocation);
         InternalAsyncUtil.makeMessageImmutable(var1.getMessage());
         if (!this.transfer(var2)) {
            if (this.blocking) {
               if (AbstractLogger.getRecursionDepth() > 1) {
                  AsyncQueueFullMessageUtil.logWarningToStatusLogger();
                  this.logMessageInCurrentThread(var1);
               } else {
                  EventRoute var3 = this.asyncQueueFullPolicy.getRoute(this.dispatcher.getId(), var2.getLevel());
                  var3.logMessage(this, var2);
               }
            } else {
               this.error("Appender " + this.getName() + " is unable to write primary appenders. queue is full");
               this.logToErrorAppenderIfNecessary(false, var2);
            }
         }
      }
   }

   private boolean transfer(LogEvent var1) {
      return this.queue instanceof TransferQueue ? ((TransferQueue)this.queue).tryTransfer(var1) : this.queue.offer(var1);
   }

   public void logMessageInCurrentThread(LogEvent var1) {
      var1.setEndOfBatch(this.queue.isEmpty());
      this.dispatcher.dispatch(var1);
   }

   public void logMessageInBackgroundThread(LogEvent var1) {
      try {
         this.queue.put(var1);
      } catch (InterruptedException var4) {
         boolean var3 = this.handleInterruptedException(var1);
         this.logToErrorAppenderIfNecessary(var3, var1);
      }
   }

   private boolean handleInterruptedException(LogEvent var1) {
      boolean var2 = this.queue.offer(var1);
      if (!var2) {
         LOGGER.warn("Interrupted while waiting for a free slot in the AsyncAppender LogEvent-queue {}", this.getName());
      }

      Thread.currentThread().interrupt();
      return var2;
   }

   private void logToErrorAppenderIfNecessary(boolean var1, LogEvent var2) {
      if (!var1 && this.errorAppender != null) {
         this.errorAppender.callAppender(var2);
      }
   }

   @Deprecated
   public static AsyncAppender createAppender(
      AppenderRef[] var0, String var1, boolean var2, long var3, int var5, String var6, boolean var7, Filter var8, Configuration var9, boolean var10
   ) {
      if (var6 == null) {
         LOGGER.error("No name provided for AsyncAppender");
         return null;
      } else {
         if (var0 == null) {
            LOGGER.error("No appender references provided to AsyncAppender {}", var6);
         }

         return new AsyncAppender(var6, var8, var0, var1, var5, var2, var10, var3, var9, var7, new ArrayBlockingQueueFactory<>(), null);
      }
   }

   @PluginBuilderFactory
   public static AsyncAppender.Builder newBuilder() {
      return new AsyncAppender.Builder();
   }

   public String[] getAppenderRefStrings() {
      String[] var1 = new String[this.appenderRefs.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.appenderRefs[var2].getRef();
      }

      return var1;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public String getErrorRef() {
      return this.errorRef;
   }

   public int getQueueCapacity() {
      return this.queueSize;
   }

   public int getQueueRemainingCapacity() {
      return this.queue.remainingCapacity();
   }

   public int getQueueSize() {
      return this.queue.size();
   }

   public static class Builder<B extends AsyncAppender.Builder<B>>
      extends AbstractFilterable.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<AsyncAppender> {
      @PluginElement("AppenderRef")
      @Required(
         message = "No appender references provided to AsyncAppender"
      )
      private AppenderRef[] appenderRefs;
      @PluginBuilderAttribute
      @PluginAliases({"error-ref"})
      private String errorRef;
      @PluginBuilderAttribute
      private boolean blocking = true;
      @PluginBuilderAttribute
      private long shutdownTimeout = 0L;
      @PluginBuilderAttribute
      private int bufferSize = 1024;
      @PluginBuilderAttribute
      @Required(
         message = "No name provided for AsyncAppender"
      )
      private String name;
      @PluginBuilderAttribute
      private boolean includeLocation = false;
      @PluginConfiguration
      private Configuration configuration;
      @PluginBuilderAttribute
      private boolean ignoreExceptions = true;
      @PluginElement("BlockingQueueFactory")
      private BlockingQueueFactory<LogEvent> blockingQueueFactory = new ArrayBlockingQueueFactory<>();

      public AsyncAppender.Builder setAppenderRefs(AppenderRef[] var1) {
         this.appenderRefs = var1;
         return this;
      }

      public AsyncAppender.Builder setErrorRef(String var1) {
         this.errorRef = var1;
         return this;
      }

      public AsyncAppender.Builder setBlocking(boolean var1) {
         this.blocking = var1;
         return this;
      }

      public AsyncAppender.Builder setShutdownTimeout(long var1) {
         this.shutdownTimeout = var1;
         return this;
      }

      public AsyncAppender.Builder setBufferSize(int var1) {
         this.bufferSize = var1;
         return this;
      }

      public AsyncAppender.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public AsyncAppender.Builder setIncludeLocation(boolean var1) {
         this.includeLocation = var1;
         return this;
      }

      public AsyncAppender.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public AsyncAppender.Builder setIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return this;
      }

      public AsyncAppender.Builder setBlockingQueueFactory(BlockingQueueFactory<LogEvent> var1) {
         this.blockingQueueFactory = var1;
         return this;
      }

      public AsyncAppender build() {
         return new AsyncAppender(
            this.name,
            this.getFilter(),
            this.appenderRefs,
            this.errorRef,
            this.bufferSize,
            this.blocking,
            this.ignoreExceptions,
            this.shutdownTimeout,
            this.configuration,
            this.includeLocation,
            this.blockingQueueFactory,
            this.getPropertyArray()
         );
      }
   }
}
