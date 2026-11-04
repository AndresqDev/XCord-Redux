package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "RandomAccessFile",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RandomAccessFileAppender extends AbstractOutputStreamAppender<RandomAccessFileManager> {
   private final String fileName;
   private Object advertisement;
   private final Advertiser advertiser;

   private RandomAccessFileAppender(
      String var1,
      Layout<? extends Serializable> var2,
      Filter var3,
      RandomAccessFileManager var4,
      String var5,
      boolean var6,
      boolean var7,
      Advertiser var8,
      Property[] var9
   ) {
      super(var1, var2, var3, var6, var7, var9, var4);
      if (var8 != null) {
         HashMap var10 = new HashMap<>(var2.getContentFormat());
         var10.putAll(var4.getContentFormat());
         var10.put("contentType", var2.getContentType());
         var10.put("name", var1);
         this.advertisement = var8.advertise(var10);
      }

      this.fileName = var5;
      this.advertiser = var8;
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      if (this.advertiser != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      return true;
   }

   public String getFileName() {
      return this.fileName;
   }

   public int getBufferSize() {
      return this.getManager().getBufferSize();
   }

   @Deprecated
   public static <B extends RandomAccessFileAppender.Builder<B>> RandomAccessFileAppender createAppender(
      String var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      Layout<? extends Serializable> var6,
      Filter var7,
      String var8,
      String var9,
      Configuration var10
   ) {
      boolean var11 = Booleans.parseBoolean(var1, true);
      boolean var12 = Booleans.parseBoolean(var3, true);
      boolean var13 = Booleans.parseBoolean(var5, true);
      boolean var14 = Boolean.parseBoolean(var8);
      int var15 = Integers.parseInt(var4, 262144);
      return ((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)newBuilder()
                              .setAdvertise(var14)
                              .setAdvertiseURI(var9)
                              .setAppend(var11)
                              .withBufferSize(var15))
                           .setConfiguration(var10))
                        .setFileName(var0)
                        .setFilter(var7))
                     .setIgnoreExceptions(var13))
                  .withImmediateFlush(var12))
               .setLayout(var6))
            .setName(var2))
         .build();
   }

   @PluginBuilderFactory
   public static <B extends RandomAccessFileAppender.Builder<B>> B newBuilder() {
      return new RandomAccessFileAppender.Builder<B>().asBuilder();
   }

   public static class Builder<B extends RandomAccessFileAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<RandomAccessFileAppender> {
      @PluginBuilderAttribute("fileName")
      private String fileName;
      @PluginBuilderAttribute("append")
      private boolean append = true;
      @PluginBuilderAttribute("advertise")
      private boolean advertise;
      @PluginBuilderAttribute("advertiseURI")
      private String advertiseURI;

      public Builder() {
         this.withBufferSize(262144);
      }

      public RandomAccessFileAppender build() {
         String var1 = this.getName();
         if (var1 == null) {
            RandomAccessFileAppender.LOGGER.error("No name provided for RandomAccessFileAppender");
            return null;
         } else if (this.fileName == null) {
            RandomAccessFileAppender.LOGGER.error("No filename provided for RandomAccessFileAppender with name {}", var1);
            return null;
         } else {
            Layout var2 = this.getOrCreateLayout();
            boolean var3 = this.isImmediateFlush();
            RandomAccessFileManager var4 = RandomAccessFileManager.getFileManager(
               this.fileName, this.append, var3, this.getBufferSize(), this.advertiseURI, var2, null
            );
            return var4 == null
               ? null
               : new RandomAccessFileAppender(
                  var1,
                  var2,
                  this.getFilter(),
                  var4,
                  this.fileName,
                  this.isIgnoreExceptions(),
                  var3,
                  this.advertise ? this.getConfiguration().getAdvertiser() : null,
                  this.getPropertyArray()
               );
         }
      }

      public B setFileName(String var1) {
         this.fileName = var1;
         return this.asBuilder();
      }

      public B setAppend(boolean var1) {
         this.append = var1;
         return this.asBuilder();
      }

      public B setAdvertise(boolean var1) {
         this.advertise = var1;
         return this.asBuilder();
      }

      public B setAdvertiseURI(String var1) {
         this.advertiseURI = var1;
         return this.asBuilder();
      }
   }
}
