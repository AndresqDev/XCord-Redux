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
   name = "MemoryMappedFile",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class MemoryMappedFileAppender extends AbstractOutputStreamAppender<MemoryMappedFileManager> {
   private static final int BIT_POSITION_1GB = 30;
   private static final int MAX_REGION_LENGTH = 1073741824;
   private static final int MIN_REGION_LENGTH = 256;
   private final String fileName;
   private Object advertisement;
   private final Advertiser advertiser;

   private MemoryMappedFileAppender(
      String var1,
      Layout<? extends Serializable> var2,
      Filter var3,
      MemoryMappedFileManager var4,
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

   public int getRegionLength() {
      return this.getManager().getRegionLength();
   }

   @Deprecated
   public static <B extends MemoryMappedFileAppender.Builder<B>> MemoryMappedFileAppender createAppender(
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
      boolean var12 = Booleans.parseBoolean(var3, false);
      boolean var13 = Booleans.parseBoolean(var5, true);
      boolean var14 = Boolean.parseBoolean(var8);
      int var15 = Integers.parseInt(var4, 33554432);
      return ((MemoryMappedFileAppender.Builder)((MemoryMappedFileAppender.Builder)((MemoryMappedFileAppender.Builder)((MemoryMappedFileAppender.Builder)((MemoryMappedFileAppender.Builder)((MemoryMappedFileAppender.Builder)newBuilder()
                           .setAdvertise(var14)
                           .setAdvertiseURI(var9)
                           .setAppend(var11)
                           .setConfiguration(var10))
                        .setFileName(var0)
                        .setFilter(var7))
                     .setIgnoreExceptions(var13))
                  .withImmediateFlush(var12))
               .setLayout(var6))
            .setName(var2))
         .setRegionLength(var15)
         .build();
   }

   @PluginBuilderFactory
   public static <B extends MemoryMappedFileAppender.Builder<B>> B newBuilder() {
      return new MemoryMappedFileAppender.Builder<B>().asBuilder();
   }

   private static int determineValidRegionLength(String var0, int var1) {
      if (var1 > 1073741824) {
         LOGGER.info("MemoryMappedAppender[{}] Reduced region length from {} to max length: {}", var0, var1, 1073741824);
         return 1073741824;
      } else if (var1 < 256) {
         LOGGER.info("MemoryMappedAppender[{}] Expanded region length from {} to min length: {}", var0, var1, 256);
         return 256;
      } else {
         int var2 = Integers.ceilingNextPowerOfTwo(var1);
         if (var1 != var2) {
            LOGGER.info("MemoryMappedAppender[{}] Rounded up region length from {} to next power of two: {}", var0, var1, var2);
         }

         return var2;
      }
   }

   public static class Builder<B extends MemoryMappedFileAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<MemoryMappedFileAppender> {
      @PluginBuilderAttribute("fileName")
      private String fileName;
      @PluginBuilderAttribute("append")
      private boolean append = true;
      @PluginBuilderAttribute("regionLength")
      private int regionLength = 33554432;
      @PluginBuilderAttribute("advertise")
      private boolean advertise;
      @PluginBuilderAttribute("advertiseURI")
      private String advertiseURI;

      public MemoryMappedFileAppender build() {
         String var1 = this.getName();
         int var2 = MemoryMappedFileAppender.determineValidRegionLength(var1, this.regionLength);
         if (var1 == null) {
            MemoryMappedFileAppender.LOGGER.error("No name provided for MemoryMappedFileAppender");
            return null;
         } else if (this.fileName == null) {
            MemoryMappedFileAppender.LOGGER.error("No filename provided for MemoryMappedFileAppender with name " + var1);
            return null;
         } else {
            Layout var3 = this.getOrCreateLayout();
            MemoryMappedFileManager var4 = MemoryMappedFileManager.getFileManager(
               this.fileName, this.append, this.isImmediateFlush(), var2, this.advertiseURI, var3
            );
            return var4 == null
               ? null
               : new MemoryMappedFileAppender(
                  var1,
                  var3,
                  this.getFilter(),
                  var4,
                  this.fileName,
                  this.isIgnoreExceptions(),
                  false,
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

      public B setRegionLength(int var1) {
         this.regionLength = var1;
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
