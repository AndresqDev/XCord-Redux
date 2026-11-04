package org.apache.logging.log4j.core.appender.mom;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.status.StatusLogger;

public class JmsManager extends AbstractManager {
   static final JmsManager.JmsManagerFactory FACTORY = new JmsManager.JmsManagerFactory();
   private final JmsManager.JmsManagerConfiguration configuration;
   private volatile JmsManager.Reconnector reconnector;
   private volatile JndiManager jndiManager;
   private volatile Connection connection;
   private volatile Session session;
   private volatile Destination destination;
   private volatile MessageProducer messageProducer;

   public static JmsManager getJmsManager(String var0, Properties var1, String var2, String var3, String var4, char[] var5, boolean var6, long var7) {
      JmsManager.JmsManagerConfiguration var9 = new JmsManager.JmsManagerConfiguration(var1, var2, var3, var4, var5, var6, var7);
      return getManager(var0, FACTORY, var9);
   }

   private JmsManager(String var1, JmsManager.JmsManagerConfiguration var2) {
      super(null, var1);
      this.configuration = var2;
      this.jndiManager = var2.getJndiManager();

      try {
         this.connection = this.createConnection(this.jndiManager);
         this.session = this.createSession(this.connection);
         this.destination = this.createDestination(this.jndiManager);
         this.messageProducer = this.createMessageProducer(this.session, this.destination);
         this.connection.start();
      } catch (JMSException | NamingException var4) {
         this.reconnector = this.createReconnector();
         this.reconnector.start();
      }
   }

   private boolean closeConnection() {
      if (this.connection == null) {
         return true;
      } else {
         Connection var1 = this.connection;
         this.connection = null;

         try {
            var1.close();
            return true;
         } catch (JMSException var3) {
            StatusLogger.getLogger()
               .debug("Caught exception closing JMS Connection: {} ({}); continuing JMS manager shutdown", var3.getLocalizedMessage(), var1, var3);
            return false;
         }
      }
   }

   private boolean closeJndiManager() {
      if (this.jndiManager == null) {
         return true;
      } else {
         JndiManager var1 = this.jndiManager;
         this.jndiManager = null;
         var1.close();
         return true;
      }
   }

   private boolean closeMessageProducer() {
      if (this.messageProducer == null) {
         return true;
      } else {
         MessageProducer var1 = this.messageProducer;
         this.messageProducer = null;

         try {
            var1.close();
            return true;
         } catch (JMSException var3) {
            StatusLogger.getLogger()
               .debug("Caught exception closing JMS MessageProducer: {} ({}); continuing JMS manager shutdown", var3.getLocalizedMessage(), var1, var3);
            return false;
         }
      }
   }

   private boolean closeSession() {
      if (this.session == null) {
         return true;
      } else {
         Session var1 = this.session;
         this.session = null;

         try {
            var1.close();
            return true;
         } catch (JMSException var3) {
            StatusLogger.getLogger()
               .debug("Caught exception closing JMS Session: {} ({}); continuing JMS manager shutdown", var3.getLocalizedMessage(), var1, var3);
            return false;
         }
      }
   }

   private Connection createConnection(JndiManager var1) throws NamingException, JMSException {
      ConnectionFactory var2 = var1.lookup(this.configuration.getConnectionFactoryName());
      return this.configuration.getUserName() != null && this.configuration.getPassword() != null
         ? var2.createConnection(
            this.configuration.getUserName(), this.configuration.getPassword() == null ? null : String.valueOf(this.configuration.getPassword())
         )
         : var2.createConnection();
   }

   private Destination createDestination(JndiManager var1) throws NamingException {
      return var1.lookup(this.configuration.getDestinationName());
   }

   public Message createMessage(Serializable var1) throws JMSException {
      if (var1 instanceof String) {
         return this.session.createTextMessage((String)var1);
      } else {
         return (Message)(var1 instanceof MapMessage
            ? this.map((MapMessage<?, ?>)var1, this.session.createMapMessage())
            : this.session.createObjectMessage(var1));
      }
   }

   private void createMessageAndSend(LogEvent var1, Serializable var2) throws JMSException {
      Message var3 = this.createMessage(var2);
      var3.setJMSTimestamp(var1.getTimeMillis());
      this.messageProducer.send(var3);
   }

   public MessageConsumer createMessageConsumer() throws JMSException {
      return this.session.createConsumer(this.destination);
   }

   public MessageProducer createMessageProducer(Session var1, Destination var2) throws JMSException {
      return var1.createProducer(var2);
   }

   private JmsManager.Reconnector createReconnector() {
      JmsManager.Reconnector var1 = new JmsManager.Reconnector(this);
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   }

   private Session createSession(Connection var1) throws JMSException {
      return var1.createSession(false, 1);
   }

   public JmsManager.JmsManagerConfiguration getJmsManagerConfiguration() {
      return this.configuration;
   }

   JndiManager getJndiManager() {
      return this.configuration.getJndiManager();
   }

   <T> T lookup(String var1) throws NamingException {
      return this.jndiManager.lookup(var1);
   }

   private javax.jms.MapMessage map(MapMessage<?, ?> var1, javax.jms.MapMessage var2) {
      var1.forEach(
         (var1x, var2x) -> {
            try {
               var2.setObject(var1x, var2x);
            } catch (JMSException var4) {
               throw new IllegalArgumentException(
                  String.format("%s mapping key '%s' to value '%s': %s", var4.getClass(), var1x, var2x, var4.getLocalizedMessage()), var4
               );
            }
         }
      );
      return var2;
   }

   @Override
   protected boolean releaseSub(long var1, TimeUnit var3) {
      if (this.reconnector != null) {
         this.reconnector.shutdown();
         this.reconnector.interrupt();
         this.reconnector = null;
      }

      boolean var4 = false;
      var4 &= this.closeJndiManager();
      var4 &= this.closeMessageProducer();
      var4 &= this.closeSession();
      var4 &= this.closeConnection();
      return var4 && this.jndiManager.stop(var1, var3);
   }

   void send(LogEvent var1, Serializable var2) {
      if (this.messageProducer == null && this.reconnector != null && !this.configuration.isImmediateFail()) {
         this.reconnector.latch();
         if (this.messageProducer == null) {
            throw new AppenderLoggingException("Error sending to JMS Manager '" + this.getName() + "': JMS message producer not available");
         }
      }

      synchronized (this) {
         try {
            this.createMessageAndSend(var1, var2);
         } catch (JMSException var9) {
            if (this.configuration.isRetry() && this.reconnector == null) {
               this.reconnector = this.createReconnector();

               try {
                  this.closeJndiManager();
                  this.reconnector.reconnect();
               } catch (JMSException | NamingException var8) {
                  logger()
                     .debug(
                        "Cannot reestablish JMS connection to {}: {}; starting reconnector thread {}",
                        this.configuration,
                        var8.getLocalizedMessage(),
                        this.reconnector.getName(),
                        var8
                     );
                  this.reconnector.start();
                  throw new AppenderLoggingException(String.format("JMS exception sending to %s for %s", this.getName(), this.configuration), var9);
               }

               try {
                  this.createMessageAndSend(var1, var2);
               } catch (JMSException var7) {
                  throw new AppenderLoggingException(
                     String.format("Error sending to %s after reestablishing JMS connection for %s", this.getName(), this.configuration), var9
                  );
               }
            }
         }
      }
   }

   public static class JmsManagerConfiguration {
      private final Properties jndiProperties;
      private final String connectionFactoryName;
      private final String destinationName;
      private final String userName;
      private final char[] password;
      private final boolean immediateFail;
      private final boolean retry;
      private final long reconnectIntervalMillis;

      JmsManagerConfiguration(Properties var1, String var2, String var3, String var4, char[] var5, boolean var6, long var7) {
         this.jndiProperties = var1;
         this.connectionFactoryName = var2;
         this.destinationName = var3;
         this.userName = var4;
         this.password = var5;
         this.immediateFail = var6;
         this.reconnectIntervalMillis = var7;
         this.retry = var7 > 0L;
      }

      public String getConnectionFactoryName() {
         return this.connectionFactoryName;
      }

      public String getDestinationName() {
         return this.destinationName;
      }

      public JndiManager getJndiManager() {
         return JndiManager.getJndiManager(this.getJndiProperties());
      }

      public Properties getJndiProperties() {
         return this.jndiProperties;
      }

      public char[] getPassword() {
         return this.password;
      }

      public long getReconnectIntervalMillis() {
         return this.reconnectIntervalMillis;
      }

      public String getUserName() {
         return this.userName;
      }

      public boolean isImmediateFail() {
         return this.immediateFail;
      }

      public boolean isRetry() {
         return this.retry;
      }

      @Override
      public String toString() {
         return "JmsManagerConfiguration [jndiProperties="
            + this.jndiProperties
            + ", connectionFactoryName="
            + this.connectionFactoryName
            + ", destinationName="
            + this.destinationName
            + ", userName="
            + this.userName
            + ", immediateFail="
            + this.immediateFail
            + ", retry="
            + this.retry
            + ", reconnectIntervalMillis="
            + this.reconnectIntervalMillis
            + "]";
      }
   }

   private static class JmsManagerFactory implements ManagerFactory<JmsManager, JmsManager.JmsManagerConfiguration> {
      private JmsManagerFactory() {
      }

      public JmsManager createManager(String var1, JmsManager.JmsManagerConfiguration var2) {
         if (JndiManager.isJndiJmsEnabled()) {
            try {
               return new JmsManager(var1, var2);
            } catch (Exception var4) {
               JmsManager.logger().error("Error creating JmsManager using JmsManagerConfiguration [{}]", var2, var4);
               return null;
            }
         } else {
            JmsManager.logger().error("JNDI must be enabled by setting log4j2.enableJndiJms=true");
            return null;
         }
      }
   }

   private class Reconnector extends Log4jThread {
      private final CountDownLatch latch = new CountDownLatch(1);
      private volatile boolean shutdown;
      private final Object owner;

      private Reconnector(Object var2) {
         super("JmsManager-Reconnector");
         this.owner = var2;
      }

      public void latch() {
         try {
            this.latch.await();
         } catch (InterruptedException var2) {
         }
      }

      void reconnect() throws NamingException, JMSException {
         JndiManager var1 = JmsManager.this.getJndiManager();
         Connection var2 = JmsManager.this.createConnection(var1);
         Session var3 = JmsManager.this.createSession(var2);
         Destination var4 = JmsManager.this.createDestination(var1);
         MessageProducer var5 = JmsManager.this.createMessageProducer(var3, var4);
         var2.start();
         synchronized (this.owner) {
            JmsManager.this.jndiManager = var1;
            JmsManager.this.connection = var2;
            JmsManager.this.session = var3;
            JmsManager.this.destination = var4;
            JmsManager.this.messageProducer = var5;
            JmsManager.this.reconnector = null;
            this.shutdown = true;
         }

         JmsManager.logger().debug("Connection reestablished to {}", JmsManager.this.configuration);
      }

      @Override
      public void run() {
         while (!this.shutdown) {
            try {
               sleep(JmsManager.this.configuration.getReconnectIntervalMillis());
               this.reconnect();
            } catch (JMSException | NamingException | InterruptedException var5) {
               JmsManager.logger().debug("Cannot reestablish JMS connection to {}: {}", JmsManager.this.configuration, var5.getLocalizedMessage(), var5);
            } finally {
               this.latch.countDown();
            }
         }
      }

      public void shutdown() {
         this.shutdown = true;
      }
   }
}
