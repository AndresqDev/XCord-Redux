package org.apache.logging.log4j.core.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.util.JndiCloser;
import org.apache.logging.log4j.util.PropertiesUtil;

public class JndiManager extends AbstractManager {
   private static final JndiManager.JndiManagerFactory FACTORY = new JndiManager.JndiManagerFactory();
   private static final String PREFIX = "log4j2.enableJndi";
   private static final String JAVA_SCHEME = "java";
   private static final boolean JNDI_CONTEXT_SELECTOR_ENABLED = isJndiEnabled("ContextSelector");
   private static final boolean JNDI_JDBC_ENABLED = isJndiEnabled("Jdbc");
   private static final boolean JNDI_JMS_ENABLED = isJndiEnabled("Jms");
   private static final boolean JNDI_LOOKUP_ENABLED = isJndiEnabled("Lookup");
   private final InitialContext context;

   private static boolean isJndiEnabled(String var0) {
      return PropertiesUtil.getProperties().getBooleanProperty("log4j2.enableJndi" + var0, false);
   }

   public static boolean isJndiEnabled() {
      return isJndiContextSelectorEnabled() || isJndiJdbcEnabled() || isJndiJmsEnabled() || isJndiLookupEnabled();
   }

   public static boolean isJndiContextSelectorEnabled() {
      return JNDI_CONTEXT_SELECTOR_ENABLED;
   }

   public static boolean isJndiJdbcEnabled() {
      return JNDI_JDBC_ENABLED;
   }

   public static boolean isJndiJmsEnabled() {
      return JNDI_JMS_ENABLED;
   }

   public static boolean isJndiLookupEnabled() {
      return JNDI_LOOKUP_ENABLED;
   }

   private JndiManager(String var1, InitialContext var2) {
      super(null, var1);
      this.context = var2;
   }

   public static JndiManager getDefaultManager() {
      return getManager(JndiManager.class.getName(), FACTORY, null);
   }

   public static JndiManager getDefaultManager(String var0) {
      return getManager(var0, FACTORY, null);
   }

   public static JndiManager getJndiManager(String var0, String var1, String var2, String var3, String var4, Properties var5) {
      Properties var6 = createProperties(var0, var1, var2, var3, var4, var5);
      return getManager(createManagerName(), FACTORY, var6);
   }

   public static JndiManager getJndiManager(Properties var0) {
      return getManager(createManagerName(), FACTORY, var0);
   }

   private static String createManagerName() {
      return JndiManager.class.getName() + '@' + JndiManager.class.hashCode();
   }

   public static Properties createProperties(String var0, String var1, String var2, String var3, String var4, Properties var5) {
      if (var0 == null) {
         return null;
      } else {
         Properties var6 = new Properties();
         var6.setProperty("java.naming.factory.initial", var0);
         if (var1 != null) {
            var6.setProperty("java.naming.provider.url", var1);
         } else {
            LOGGER.warn(
               "The JNDI InitialContextFactory class name [{}] was provided, but there was no associated provider URL. This is likely to cause problems.", var0
            );
         }

         if (var2 != null) {
            var6.setProperty("java.naming.factory.url.pkgs", var2);
         }

         if (var3 != null) {
            var6.setProperty("java.naming.security.principal", var3);
            if (var4 != null) {
               var6.setProperty("java.naming.security.credentials", var4);
            } else {
               LOGGER.warn("A security principal [{}] was provided, but with no corresponding security credentials.", var3);
            }
         }

         if (var5 != null) {
            var6.putAll(var5);
         }

         return var6;
      }
   }

   @Override
   protected boolean releaseSub(long var1, TimeUnit var3) {
      return JndiCloser.closeSilently(this.context);
   }

   public <T> T lookup(String var1) throws NamingException {
      if (this.context == null) {
         return null;
      } else {
         try {
            URI var2 = new URI(var1);
            if (var2.getScheme() == null || var2.getScheme().equals("java")) {
               return (T)this.context.lookup(var1);
            }

            LOGGER.warn("Unsupported JNDI URI - {}", var1);
         } catch (URISyntaxException var3) {
            LOGGER.warn("Invalid JNDI URI - {}", var1);
         }

         return null;
      }
   }

   @Override
   public String toString() {
      return "JndiManager [context=" + this.context + ", count=" + this.count + "]";
   }

   private static class JndiManagerFactory implements ManagerFactory<JndiManager, Properties> {
      private JndiManagerFactory() {
      }

      public JndiManager createManager(String var1, Properties var2) {
         if (!JndiManager.isJndiEnabled()) {
            throw new IllegalStateException(String.format("JNDI must be enabled by setting one of the %s* properties to true", "log4j2.enableJndi"));
         } else {
            try {
               return new JndiManager(var1, new InitialContext(var2));
            } catch (NamingException var4) {
               JndiManager.LOGGER.error("Error creating JNDI InitialContext for '{}'.", var1, var4);
               return null;
            }
         }
      }
   }
}
