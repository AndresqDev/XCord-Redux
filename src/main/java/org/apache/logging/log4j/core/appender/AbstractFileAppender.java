package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.net.Advertiser;

public abstract class AbstractFileAppender<M extends OutputStreamManager> extends AbstractOutputStreamAppender<M> {
   private final String fileName;
   private final Advertiser advertiser;
   private final Object advertisement;

   private AbstractFileAppender(
      String var1, Layout<? extends Serializable> var2, Filter var3, M var4, String var5, boolean var6, boolean var7, Advertiser var8, Property[] var9
   ) {
      super(var1, var2, var3, var6, var7, var9, (M)var4);
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

   public abstract static class Builder<B extends AbstractFileAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B> {
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
