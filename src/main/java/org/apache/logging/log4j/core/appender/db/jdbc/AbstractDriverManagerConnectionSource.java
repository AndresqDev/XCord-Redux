package org.apache.logging.log4j.core.appender.db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.status.StatusLogger;

public class AbstractDriverManagerConnectionSource extends AbstractConnectionSource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String actualConnectionString;
   private final String connectionString;
   private final String driverClassName;
   private final char[] password;
   private final Property[] properties;
   private final char[] userName;

   public static Logger getLogger() {
      return LOGGER;
   }

   public AbstractDriverManagerConnectionSource(String var1, String var2, String var3, char[] var4, char[] var5, Property[] var6) {
      this.driverClassName = var1;
      this.connectionString = var2;
      this.actualConnectionString = var3;
      this.userName = var4;
      this.password = var5;
      this.properties = var6;
   }

   public String getActualConnectionString() {
      return this.actualConnectionString;
   }

   @Override
   public Connection getConnection() throws SQLException {
      this.loadDriver();
      String var1 = this.getActualConnectionString();
      LOGGER.debug("{} getting connection for '{}'", this.getClass().getSimpleName(), var1);
      Connection var2;
      if (this.properties != null && this.properties.length > 0) {
         if (this.userName != null || this.password != null) {
            throw new SQLException("Either set the userName and password, or set the Properties, but not both.");
         }

         var2 = DriverManager.getConnection(var1, this.toProperties(this.properties));
      } else {
         var2 = DriverManager.getConnection(var1, this.toString(this.userName), this.toString(this.password));
      }

      LOGGER.debug(
         "{} acquired connection for '{}': {} ({}@{})",
         this.getClass().getSimpleName(),
         var1,
         var2,
         var2.getClass().getName(),
         Integer.toHexString(var2.hashCode())
      );
      return var2;
   }

   public String getConnectionString() {
      return this.connectionString;
   }

   public String getDriverClassName() {
      return this.driverClassName;
   }

   public char[] getPassword() {
      return this.password;
   }

   public Property[] getProperties() {
      return this.properties;
   }

   public char[] getUserName() {
      return this.userName;
   }

   protected void loadDriver() throws SQLException {
      this.loadDriver(this.driverClassName);
   }

   protected void loadDriver(String var1) throws SQLException {
      if (var1 != null) {
         LOGGER.debug("Loading driver class {}", var1);

         try {
            Class.forName(var1);
         } catch (Exception var3) {
            throw new SQLException(String.format("The %s could not load the JDBC driver %s: %s", this.getClass().getSimpleName(), var1, var3.toString()), var3);
         }
      }
   }

   protected Properties toProperties(Property[] var1) {
      Properties var2 = new Properties();

      for (Property var6 : var1) {
         var2.setProperty(var6.getName(), var6.getValue());
      }

      return var2;
   }

   @Override
   public String toString() {
      return this.connectionString;
   }

   protected String toString(char[] var1) {
      return var1 == null ? null : String.valueOf(var1);
   }

   public static class Builder<B extends AbstractDriverManagerConnectionSource.Builder<B>> {
      @PluginBuilderAttribute
      @Required
      protected String connectionString;
      @PluginBuilderAttribute
      protected String driverClassName;
      @PluginBuilderAttribute
      protected char[] password;
      @PluginElement("Properties")
      protected Property[] properties;
      @PluginBuilderAttribute
      protected char[] userName;

      protected B asBuilder() {
         return (B)this;
      }

      public String getConnectionString() {
         return this.connectionString;
      }

      public String getDriverClassName() {
         return this.driverClassName;
      }

      public char[] getPassword() {
         return this.password;
      }

      public Property[] getProperties() {
         return this.properties;
      }

      public char[] getUserName() {
         return this.userName;
      }

      public B setConnectionString(String var1) {
         this.connectionString = var1;
         return this.asBuilder();
      }

      public B setDriverClassName(String var1) {
         this.driverClassName = var1;
         return this.asBuilder();
      }

      public B setPassword(char[] var1) {
         this.password = var1;
         return this.asBuilder();
      }

      public B setProperties(Property[] var1) {
         this.properties = var1;
         return this.asBuilder();
      }

      public B setUserName(char[] var1) {
         this.userName = var1;
         return this.asBuilder();
      }
   }
}
