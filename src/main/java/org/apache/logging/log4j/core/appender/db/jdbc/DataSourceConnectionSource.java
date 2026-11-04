package org.apache.logging.log4j.core.appender.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "DataSource",
   category = "Core",
   elementType = "connectionSource",
   printObject = true
)
public final class DataSourceConnectionSource extends AbstractConnectionSource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final DataSource dataSource;
   private final String description;

   private DataSourceConnectionSource(String var1, DataSource var2) {
      this.dataSource = Objects.requireNonNull(var2, "dataSource");
      this.description = "dataSource{ name=" + var1 + ", value=" + var2 + " }";
   }

   @Override
   public Connection getConnection() throws SQLException {
      return this.dataSource.getConnection();
   }

   @Override
   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static DataSourceConnectionSource createConnectionSource(@PluginAttribute("jndiName") String var0) {
      if (!JndiManager.isJndiJdbcEnabled()) {
         LOGGER.error("JNDI must be enabled by setting log4j2.enableJndiJdbc=true");
         return null;
      } else if (Strings.isEmpty(var0)) {
         LOGGER.error("No JNDI name provided.");
         return null;
      } else {
         try {
            DataSource var1 = JndiManager.getDefaultManager(DataSourceConnectionSource.class.getCanonicalName()).lookup(var0);
            if (var1 == null) {
               LOGGER.error("No DataSource found with JNDI name [" + var0 + "].");
               return null;
            } else {
               return new DataSourceConnectionSource(var0, var1);
            }
         } catch (NamingException var2) {
            LOGGER.error(var2.getMessage(), (Throwable)var2);
            return null;
         }
      }
   }
}
