package org.apache.logging.log4j.core.appender.mom.kafka;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.SerializedLayout;

@Plugin(
   name = "Kafka",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class KafkaAppender extends AbstractAppender {
   private static final String[] KAFKA_CLIENT_PACKAGES = new String[]{"org.apache.kafka.common", "org.apache.kafka.clients"};
   private final Integer retryCount;
   private final KafkaManager manager;

   @Deprecated
   public static KafkaAppender createAppender(
      Layout<? extends Serializable> var0, Filter var1, String var2, boolean var3, String var4, Property[] var5, Configuration var6, String var7
   ) {
      if (var0 == null) {
         AbstractLifeCycle.LOGGER.error("No layout provided for KafkaAppender");
         return null;
      } else {
         KafkaManager var8 = KafkaManager.getManager(var6.getLoggerContext(), var2, var4, true, var5, var7);
         return new KafkaAppender(var2, var0, var1, var3, var8, null, null);
      }
   }

   private static boolean isRecursive(LogEvent var0) {
      return Stream.of(KAFKA_CLIENT_PACKAGES).anyMatch(var1 -> var0.getLoggerName().startsWith(var1));
   }

   @PluginBuilderFactory
   public static <B extends KafkaAppender.Builder<B>> B newBuilder() {
      return new KafkaAppender.Builder<B>().asBuilder();
   }

   private KafkaAppender(String var1, Layout<? extends Serializable> var2, Filter var3, boolean var4, KafkaManager var5, Property[] var6, Integer var7) {
      super(var1, var3, var2, var4, var6);
      this.manager = Objects.requireNonNull(var5, "manager");
      this.retryCount = var7;
   }

   @Override
   public void append(LogEvent var1) {
      if (var1.getLoggerName() != null && isRecursive(var1)) {
         LOGGER.warn("Recursive logging from [{}] for appender [{}].", var1.getLoggerName(), this.getName());
      } else {
         try {
            this.tryAppend(var1);
         } catch (Exception var6) {
            if (this.retryCount != null) {
               int var3 = 0;

               while (var3 < this.retryCount) {
                  var3++;

                  try {
                     this.tryAppend(var1);
                     break;
                  } catch (Exception var5) {
                  }
               }
            }

            this.error("Unable to write to Kafka in appender [" + this.getName() + "]", var1, var6);
         }
      }
   }

   @Override
   public void start() {
      super.start();
      this.manager.startup();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   @Override
   public String toString() {
      return "KafkaAppender{name=" + this.getName() + ", state=" + this.getState() + ", topic=" + this.manager.getTopic() + '}';
   }

   private void tryAppend(LogEvent var1) throws ExecutionException, InterruptedException, TimeoutException {
      Layout var2 = this.getLayout();
      byte[] var3;
      if (var2 instanceof SerializedLayout) {
         byte[] var4 = var2.getHeader();
         byte[] var5 = var2.toByteArray(var1);
         var3 = new byte[var4.length + var5.length];
         System.arraycopy(var4, 0, var3, 0, var4.length);
         System.arraycopy(var5, 0, var3, var4.length, var5.length);
      } else {
         var3 = var2.toByteArray(var1);
      }

      this.manager.send(var3);
   }

   public static class Builder<B extends KafkaAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<KafkaAppender> {
      @PluginAttribute("retryCount")
      private String retryCount;
      @PluginAttribute("topic")
      private String topic;
      @PluginAttribute("key")
      private String key;
      @PluginAttribute(
         value = "syncSend",
         defaultBoolean = true
      )
      private boolean syncSend;

      public KafkaAppender build() {
         Layout var1 = this.getLayout();
         if (var1 == null) {
            KafkaAppender.LOGGER.error("No layout provided for KafkaAppender");
            return null;
         } else {
            KafkaManager var2 = KafkaManager.getManager(
               this.getConfiguration().getLoggerContext(), this.getName(), this.topic, this.syncSend, this.getPropertyArray(), this.key
            );
            return new KafkaAppender(this.getName(), var1, this.getFilter(), this.isIgnoreExceptions(), var2, this.getPropertyArray(), this.getRetryCount());
         }
      }

      public Integer getRetryCount() {
         Integer var1 = null;

         try {
            var1 = Integer.valueOf(this.retryCount);
         } catch (NumberFormatException var3) {
         }

         return var1;
      }

      public String getTopic() {
         return this.topic;
      }

      public boolean isSyncSend() {
         return this.syncSend;
      }

      public B setKey(String var1) {
         this.key = var1;
         return this.asBuilder();
      }

      public B setSyncSend(boolean var1) {
         this.syncSend = var1;
         return this.asBuilder();
      }

      public B setTopic(String var1) {
         this.topic = var1;
         return this.asBuilder();
      }
   }
}
