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
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "File",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class FileAppender extends AbstractOutputStreamAppender<FileManager> {
   public static final String PLUGIN_NAME = "File";
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private final String fileName;
   private final Advertiser advertiser;
   private final Object advertisement;

   @Deprecated
   public static <B extends FileAppender.Builder<B>> FileAppender createAppender(
      String var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      Layout<? extends Serializable> var8,
      Filter var9,
      String var10,
      String var11,
      Configuration var12
   ) {
      return ((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)newBuilder()
                                 .withAdvertise(Boolean.parseBoolean(var10))
                                 .withAdvertiseUri(var11)
                                 .withAppend(Booleans.parseBoolean(var1, true))
                                 .withBufferedIo(Booleans.parseBoolean(var6, true)))
                              .withBufferSize(Integers.parseInt(var7, 8192)))
                           .setConfiguration(var12))
                        .withFileName(var0)
                        .setFilter(var9))
                     .setIgnoreExceptions(Booleans.parseBoolean(var5, true)))
                  .withImmediateFlush(Booleans.parseBoolean(var4, true)))
               .setLayout(var8))
            .withLocking(Boolean.parseBoolean(var2))
            .setName(var3))
         .build();
   }

   @PluginBuilderFactory
   public static <B extends FileAppender.Builder<B>> B newBuilder() {
      return new FileAppender.Builder<B>().asBuilder();
   }

   private FileAppender(
      String var1,
      Layout<? extends Serializable> var2,
      Filter var3,
      FileManager var4,
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
      } else {
         this.advertisement = null;
      }

      this.fileName = var5;
      this.advertiser = var8;
   }

   public String getFileName() {
      return this.fileName;
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

   public static class Builder<B extends FileAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<FileAppender> {
      @PluginBuilderAttribute
      @Required
      private String fileName;
      @PluginBuilderAttribute
      private boolean append = true;
      @PluginBuilderAttribute
      private boolean locking;
      @PluginBuilderAttribute
      private boolean advertise;
      @PluginBuilderAttribute
      private String advertiseUri;
      @PluginBuilderAttribute
      private boolean createOnDemand;
      @PluginBuilderAttribute
      private String filePermissions;
      @PluginBuilderAttribute
      private String fileOwner;
      @PluginBuilderAttribute
      private String fileGroup;

      public FileAppender build() {
         boolean var1 = this.isBufferedIo();
         int var2 = this.getBufferSize();
         if (this.locking && var1) {
            FileAppender.LOGGER.warn("Locking and buffering are mutually exclusive. No buffering will occur for {}", this.fileName);
            var1 = false;
         }

         if (!var1 && var2 > 0) {
            FileAppender.LOGGER.warn("The bufferSize is set to {} but bufferedIo is false: {}", var2, var1);
         }

         Layout var3 = this.getOrCreateLayout();
         FileManager var4 = FileManager.getFileManager(
            this.fileName,
            this.append,
            this.locking,
            var1,
            this.createOnDemand,
            this.advertiseUri,
            var3,
            var2,
            this.filePermissions,
            this.fileOwner,
            this.fileGroup,
            this.getConfiguration()
         );
         return var4 == null
            ? null
            : new FileAppender(
               this.getName(),
               var3,
               this.getFilter(),
               var4,
               this.fileName,
               this.isIgnoreExceptions(),
               !var1 || this.isImmediateFlush(),
               this.advertise ? this.getConfiguration().getAdvertiser() : null,
               this.getPropertyArray()
            );
      }

      public String getAdvertiseUri() {
         return this.advertiseUri;
      }

      public String getFileName() {
         return this.fileName;
      }

      public boolean isAdvertise() {
         return this.advertise;
      }

      public boolean isAppend() {
         return this.append;
      }

      public boolean isCreateOnDemand() {
         return this.createOnDemand;
      }

      public boolean isLocking() {
         return this.locking;
      }

      public String getFilePermissions() {
         return this.filePermissions;
      }

      public String getFileOwner() {
         return this.fileOwner;
      }

      public String getFileGroup() {
         return this.fileGroup;
      }

      public B withAdvertise(boolean var1) {
         this.advertise = var1;
         return this.asBuilder();
      }

      public B withAdvertiseUri(String var1) {
         this.advertiseUri = var1;
         return this.asBuilder();
      }

      public B withAppend(boolean var1) {
         this.append = var1;
         return this.asBuilder();
      }

      public B withFileName(String var1) {
         this.fileName = var1;
         return this.asBuilder();
      }

      public B withCreateOnDemand(boolean var1) {
         this.createOnDemand = var1;
         return this.asBuilder();
      }

      public B withLocking(boolean var1) {
         this.locking = var1;
         return this.asBuilder();
      }

      public B withFilePermissions(String var1) {
         this.filePermissions = var1;
         return this.asBuilder();
      }

      public B withFileOwner(String var1) {
         this.fileOwner = var1;
         return this.asBuilder();
      }

      public B withFileGroup(String var1) {
         this.fileGroup = var1;
         return this.asBuilder();
      }
   }
}
