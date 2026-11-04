package org.apache.logging.log4j.core.appender.mom;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.net.JndiManager;

@Plugin(
   name = "JMS",
   category = "Core",
   elementType = "appender",
   printObject = true
)
@PluginAliases({"JMSQueue", "JMSTopic"})
public class JmsAppender extends AbstractAppender {
   private volatile JmsManager manager;

   @PluginBuilderFactory
   public static JmsAppender.Builder newBuilder() {
      return new JmsAppender.Builder();
   }

   protected JmsAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, Property[] var5, JmsManager var6) throws JMSException {
      super(var1, var2, var3, var4, var5);
      this.manager = var6;
   }

   @Deprecated
   protected JmsAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, JmsManager var5) throws JMSException {
      super(var1, var2, var3, var4, Property.EMPTY_ARRAY);
      this.manager = var5;
   }

   @Override
   public void append(LogEvent var1) {
      this.manager.send(var1, this.toSerializable(var1));
   }

   public JmsManager getManager() {
      return this.manager;
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   public static class Builder<B extends JmsAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<JmsAppender> {
      public static final int DEFAULT_RECONNECT_INTERVAL_MILLIS = 5000;
      @PluginBuilderAttribute
      private String factoryName;
      @PluginBuilderAttribute
      private String providerUrl;
      @PluginBuilderAttribute
      private String urlPkgPrefixes;
      @PluginBuilderAttribute
      private String securityPrincipalName;
      @PluginBuilderAttribute(
         sensitive = true
      )
      private String securityCredentials;
      @PluginBuilderAttribute
      @Required(
         message = "A javax.jms.ConnectionFactory JNDI name must be specified"
      )
      private String factoryBindingName;
      @PluginBuilderAttribute
      @PluginAliases({"queueBindingName", "topicBindingName"})
      @Required(
         message = "A javax.jms.Destination JNDI name must be specified"
      )
      private String destinationBindingName;
      @PluginBuilderAttribute
      private String userName;
      @PluginBuilderAttribute(
         sensitive = true
      )
      private char[] password;
      @PluginBuilderAttribute
      private long reconnectIntervalMillis = 5000L;
      @PluginBuilderAttribute
      private boolean immediateFail;
      private JmsManager jmsManager;

      private Builder() {
      }

      public JmsAppender build() {
         JmsManager var1 = this.jmsManager;
         Object var2 = null;
         if (var1 == null) {
            Properties var3 = JndiManager.createProperties(
               this.factoryName, this.providerUrl, this.urlPkgPrefixes, this.securityPrincipalName, this.securityCredentials, null
            );
            var2 = new JmsManager.JmsManagerConfiguration(
               var3, this.factoryBindingName, this.destinationBindingName, this.userName, this.password, false, this.reconnectIntervalMillis
            );
            var1 = AbstractManager.getManager(this.getName(), JmsManager.FACTORY, (JmsManager.JmsManagerConfiguration)var2);
         }

         if (var1 == null) {
            return null;
         } else {
            Layout var7 = this.getLayout();
            if (var7 == null) {
               JmsAppender.LOGGER.error("No layout provided for JmsAppender");
               return null;
            } else {
               try {
                  return new JmsAppender(this.getName(), this.getFilter(), var7, this.isIgnoreExceptions(), this.getPropertyArray(), var1);
               } catch (JMSException var5) {
                  throw new IllegalStateException(var5);
               }
            }
         }
      }

      public JmsAppender.Builder setDestinationBindingName(String var1) {
         this.destinationBindingName = var1;
         return this;
      }

      public JmsAppender.Builder setFactoryBindingName(String var1) {
         this.factoryBindingName = var1;
         return this;
      }

      public JmsAppender.Builder setFactoryName(String var1) {
         this.factoryName = var1;
         return this;
      }

      public JmsAppender.Builder setImmediateFail(boolean var1) {
         this.immediateFail = var1;
         return this;
      }

      public JmsAppender.Builder setJmsManager(JmsManager var1) {
         this.jmsManager = var1;
         return this;
      }

      public JmsAppender.Builder setPassword(char[] var1) {
         this.password = var1;
         return this;
      }

      @Deprecated
      public JmsAppender.Builder setPassword(String var1) {
         this.password = var1 == null ? null : var1.toCharArray();
         return this;
      }

      public JmsAppender.Builder setProviderUrl(String var1) {
         this.providerUrl = var1;
         return this;
      }

      public JmsAppender.Builder setReconnectIntervalMillis(long var1) {
         this.reconnectIntervalMillis = var1;
         return this;
      }

      public JmsAppender.Builder setSecurityCredentials(String var1) {
         this.securityCredentials = var1;
         return this;
      }

      public JmsAppender.Builder setSecurityPrincipalName(String var1) {
         this.securityPrincipalName = var1;
         return this;
      }

      public JmsAppender.Builder setUrlPkgPrefixes(String var1) {
         this.urlPkgPrefixes = var1;
         return this;
      }

      @Deprecated
      public JmsAppender.Builder setUsername(String var1) {
         this.userName = var1;
         return this;
      }

      public JmsAppender.Builder setUserName(String var1) {
         this.userName = var1;
         return this;
      }

      @Override
      public String toString() {
         return "Builder [name="
            + this.getName()
            + ", factoryName="
            + this.factoryName
            + ", providerUrl="
            + this.providerUrl
            + ", urlPkgPrefixes="
            + this.urlPkgPrefixes
            + ", securityPrincipalName="
            + this.securityPrincipalName
            + ", securityCredentials="
            + this.securityCredentials
            + ", factoryBindingName="
            + this.factoryBindingName
            + ", destinationBindingName="
            + this.destinationBindingName
            + ", username="
            + this.userName
            + ", layout="
            + this.getLayout()
            + ", filter="
            + this.getFilter()
            + ", ignoreExceptions="
            + this.isIgnoreExceptions()
            + ", jmsManager="
            + this.jmsManager
            + "]";
      }
   }
}
