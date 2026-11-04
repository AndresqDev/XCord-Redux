package org.apache.logging.log4j.core.appender.mom.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Log4jThread;

public class KafkaManager extends AbstractManager {
   public static final String DEFAULT_TIMEOUT_MILLIS = "30000";
   static KafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory();
   private final Properties config = new Properties();
   private Producer<byte[], byte[]> producer;
   private final int timeoutMillis;
   private final String topic;
   private final String key;
   private final boolean syncSend;
   private static final KafkaManager.KafkaManagerFactory factory = new KafkaManager.KafkaManagerFactory();

   public KafkaManager(LoggerContext var1, String var2, String var3, boolean var4, Property[] var5, String var6) {
      super(var1, var2);
      this.topic = Objects.requireNonNull(var3, "topic");
      this.syncSend = var4;
      this.config.setProperty("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
      this.config.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
      this.config.setProperty("batch.size", "0");

      for (Property var10 : var5) {
         this.config.setProperty(var10.getName(), var10.getValue());
      }

      this.key = var6;
      this.timeoutMillis = Integer.parseInt(this.config.getProperty("timeout.ms", "30000"));
   }

   @Override
   public boolean releaseSub(long var1, TimeUnit var3) {
      if (var1 > 0L) {
         this.closeProducer(var1, var3);
      } else {
         this.closeProducer((long)this.timeoutMillis, TimeUnit.MILLISECONDS);
      }

      return true;
   }

   private void closeProducer(long var1, TimeUnit var3) {
      if (this.producer != null) {
         Log4jThread var4 = new Log4jThread(() -> {
            if (this.producer != null) {
               this.producer.close();
            }
         }, "KafkaManager-CloseThread");
         var4.setDaemon(true);
         var4.start();

         try {
            var4.join(var3.toMillis(var1));
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
         }
      }
   }

   public void send(byte[] var1) throws ExecutionException, InterruptedException, TimeoutException {
      if (this.producer != null) {
         byte[] var2 = null;
         if (this.key != null && this.key.contains("${")) {
            var2 = this.getLoggerContext().getConfiguration().getStrSubstitutor().replace(this.key).getBytes(StandardCharsets.UTF_8);
         } else if (this.key != null) {
            var2 = this.key.getBytes(StandardCharsets.UTF_8);
         }

         ProducerRecord var3 = new ProducerRecord(this.topic, var2, var1);
         if (this.syncSend) {
            Future var4 = this.producer.send(var3);
            var4.get((long)this.timeoutMillis, TimeUnit.MILLISECONDS);
         } else {
            this.producer.send(var3, (var1x, var2x) -> {
               if (var2x != null) {
                  LOGGER.error("Unable to write to Kafka in appender [" + this.getName() + "]", (Throwable)var2x);
               }
            });
         }
      }
   }

   public void startup() {
      if (this.producer == null) {
         this.producer = producerFactory.newKafkaProducer(this.config);
      }
   }

   public String getTopic() {
      return this.topic;
   }

   public static KafkaManager getManager(LoggerContext var0, String var1, String var2, boolean var3, Property[] var4, String var5) {
      StringBuilder var6 = new StringBuilder(var1);
      var6.append(" ").append(var2).append(" ").append(var3 + "");

      for (Property var10 : var4) {
         var6.append(" ").append(var10.getName()).append("=").append(var10.getValue());
      }

      return getManager(var6.toString(), factory, new KafkaManager.FactoryData(var0, var2, var3, var4, var5));
   }

   private static class FactoryData {
      private final LoggerContext loggerContext;
      private final String topic;
      private final boolean syncSend;
      private final Property[] properties;
      private final String key;

      public FactoryData(LoggerContext var1, String var2, boolean var3, Property[] var4, String var5) {
         this.loggerContext = var1;
         this.topic = var2;
         this.syncSend = var3;
         this.properties = var4;
         this.key = var5;
      }
   }

   private static class KafkaManagerFactory implements ManagerFactory<KafkaManager, KafkaManager.FactoryData> {
      private KafkaManagerFactory() {
      }

      public KafkaManager createManager(String var1, KafkaManager.FactoryData var2) {
         return new KafkaManager(var2.loggerContext, var1, var2.topic, var2.syncSend, var2.properties, var2.key);
      }
   }
}
