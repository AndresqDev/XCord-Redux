package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.apache.logging.log4j.core.net.SmtpManager;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "SMTP",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class SmtpAppender extends AbstractAppender {
   private static final int DEFAULT_BUFFER_SIZE = 512;
   private final SmtpManager manager;

   private SmtpAppender(String var1, Filter var2, Layout<? extends Serializable> var3, SmtpManager var4, boolean var5, Property[] var6) {
      super(var1, var2, var3, var5, var6);
      this.manager = var4;
   }

   @PluginBuilderFactory
   public static SmtpAppender.Builder newBuilder() {
      return new SmtpAppender.Builder();
   }

   @Deprecated
   public static SmtpAppender createAppender(
      @PluginConfiguration Configuration var0,
      @PluginAttribute("name") @Required String var1,
      @PluginAttribute("to") String var2,
      @PluginAttribute("cc") String var3,
      @PluginAttribute("bcc") String var4,
      @PluginAttribute("from") String var5,
      @PluginAttribute("replyTo") String var6,
      @PluginAttribute("subject") String var7,
      @PluginAttribute("smtpProtocol") String var8,
      @PluginAttribute("smtpHost") String var9,
      @PluginAttribute(value = "smtpPort",defaultString = "0") @ValidPort String var10,
      @PluginAttribute("smtpUsername") String var11,
      @PluginAttribute(value = "smtpPassword",sensitive = true) String var12,
      @PluginAttribute("smtpDebug") String var13,
      @PluginAttribute("bufferSize") String var14,
      @PluginElement("Layout") Layout<? extends Serializable> var15,
      @PluginElement("Filter") Filter var16,
      @PluginAttribute("ignoreExceptions") String var17
   ) {
      if (var1 == null) {
         LOGGER.error("No name provided for SmtpAppender");
         return null;
      } else {
         boolean var18 = Booleans.parseBoolean(var17, true);
         int var19 = AbstractAppender.parseInt(var10, 0);
         boolean var20 = Boolean.parseBoolean(var13);
         int var21 = var14 == null ? 512 : Integer.parseInt(var14);
         if (var15 == null) {
            var15 = HtmlLayout.createDefaultLayout();
         }

         if (var16 == null) {
            var16 = ThresholdFilter.createFilter(null, null, null);
         }

         Object var22 = var0 != null ? var0 : new DefaultConfiguration();
         SmtpManager var23 = SmtpManager.getSmtpManager(
            (Configuration)var22, var2, var3, var4, var5, var6, var7, var8, var9, var19, var11, var12, var20, var16.toString(), var21, null
         );
         return var23 == null ? null : new SmtpAppender(var1, (Filter)var16, (Layout<? extends Serializable>)var15, var23, var18, null);
      }
   }

   @Override
   public boolean isFiltered(LogEvent var1) {
      boolean var2 = super.isFiltered(var1);
      if (var2) {
         this.manager.add(var1);
      }

      return var2;
   }

   @Override
   public void append(LogEvent var1) {
      this.manager.sendEvents(this.getLayout(), var1);
   }

   public static class Builder extends AbstractAppender.Builder<SmtpAppender.Builder> implements org.apache.logging.log4j.core.util.Builder<SmtpAppender> {
      @PluginBuilderAttribute
      private String to;
      @PluginBuilderAttribute
      private String cc;
      @PluginBuilderAttribute
      private String bcc;
      @PluginBuilderAttribute
      private String from;
      @PluginBuilderAttribute
      private String replyTo;
      @PluginBuilderAttribute
      private String subject;
      @PluginBuilderAttribute
      private String smtpProtocol = "smtp";
      @PluginBuilderAttribute
      private String smtpHost;
      @PluginBuilderAttribute
      @ValidPort
      private int smtpPort;
      @PluginBuilderAttribute
      private String smtpUsername;
      @PluginBuilderAttribute(
         sensitive = true
      )
      private String smtpPassword;
      @PluginBuilderAttribute
      private boolean smtpDebug;
      @PluginBuilderAttribute
      private int bufferSize = 512;
      @PluginElement("SSL")
      private SslConfiguration sslConfiguration;

      public SmtpAppender.Builder setTo(String var1) {
         this.to = var1;
         return this;
      }

      public SmtpAppender.Builder setCc(String var1) {
         this.cc = var1;
         return this;
      }

      public SmtpAppender.Builder setBcc(String var1) {
         this.bcc = var1;
         return this;
      }

      public SmtpAppender.Builder setFrom(String var1) {
         this.from = var1;
         return this;
      }

      public SmtpAppender.Builder setReplyTo(String var1) {
         this.replyTo = var1;
         return this;
      }

      public SmtpAppender.Builder setSubject(String var1) {
         this.subject = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpProtocol(String var1) {
         this.smtpProtocol = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpHost(String var1) {
         this.smtpHost = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpPort(int var1) {
         this.smtpPort = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpUsername(String var1) {
         this.smtpUsername = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpPassword(String var1) {
         this.smtpPassword = var1;
         return this;
      }

      public SmtpAppender.Builder setSmtpDebug(boolean var1) {
         this.smtpDebug = var1;
         return this;
      }

      public SmtpAppender.Builder setBufferSize(int var1) {
         this.bufferSize = var1;
         return this;
      }

      public SmtpAppender.Builder setSslConfiguration(SslConfiguration var1) {
         this.sslConfiguration = var1;
         return this;
      }

      public SmtpAppender.Builder setLayout(Layout<? extends Serializable> var1) {
         return (SmtpAppender.Builder)super.setLayout(var1);
      }

      public SmtpAppender.Builder setFilter(Filter var1) {
         return (SmtpAppender.Builder)super.setFilter(var1);
      }

      public SmtpAppender build() {
         if (this.getLayout() == null) {
            this.setLayout(HtmlLayout.createDefaultLayout());
         }

         if (this.getFilter() == null) {
            this.setFilter(ThresholdFilter.createFilter(null, null, null));
         }

         SmtpManager var1 = SmtpManager.getSmtpManager(
            this.getConfiguration(),
            this.to,
            this.cc,
            this.bcc,
            this.from,
            this.replyTo,
            this.subject,
            this.smtpProtocol,
            this.smtpHost,
            this.smtpPort,
            this.smtpUsername,
            this.smtpPassword,
            this.smtpDebug,
            this.getFilter().toString(),
            this.bufferSize,
            this.sslConfiguration
         );
         return new SmtpAppender(this.getName(), this.getFilter(), this.getLayout(), var1, this.isIgnoreExceptions(), this.getPropertyArray());
      }
   }
}
