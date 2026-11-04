package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "ConnectionFactory",
   category = "Core",
   elementType = "connectionSource",
   printObject = true
)
public final class FactoryMethodConnectionSource extends AbstractConnectionSource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final DataSource dataSource;
   private final String description;

   private FactoryMethodConnectionSource(DataSource var1, String var2, String var3, String var4) {
      this.dataSource = var1;
      this.description = "factory{ public static " + var4 + ' ' + var2 + '.' + var3 + "() }";
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
   public static FactoryMethodConnectionSource createConnectionSource(@PluginAttribute("class") String var0, @PluginAttribute("method") String var1) {
      if (!Strings.isEmpty(var0) && !Strings.isEmpty(var1)) {
         final Method var2;
         try {
            Class var3 = Loader.loadClass(var0);
            var2 = var3.getMethod(var1);
         } catch (Exception var8) {
            LOGGER.error(var8.toString(), (Throwable)var8);
            return null;
         }

         Class var9 = var2.getReturnType();
         String var4 = var9.getName();
         DataSource var5;
         if (var9 == DataSource.class) {
            try {
               var5 = (DataSource)var2.invoke(null);
               var4 = var4 + "[" + var5 + ']';
            } catch (Exception var7) {
               LOGGER.error(var7.toString(), (Throwable)var7);
               return null;
            }
         } else {
            if (var9 != Connection.class) {
               LOGGER.error("Method [{}.{}()] returns unsupported type [{}].", var0, var1, var9.getName());
               return null;
            }

            var5 = new DataSource() {
               @Override
               public Connection getConnection() throws SQLException {
                  try {
                     return (Connection)var2.invoke(null);
                  } catch (Exception var2x) {
                     throw new SQLException("Failed to obtain connection from factory method.", var2x);
                  }
               }

               @Override
               public Connection getConnection(String var1, String var2x) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               @Override
               public int getLoginTimeout() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               @Override
               public PrintWriter getLogWriter() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               @Override
               public java.util.logging.Logger getParentLogger() {
                  throw new UnsupportedOperationException();
               }

               @Override
               public boolean isWrapperFor(Class<?> var1) throws SQLException {
                  return false;
               }

               @Override
               public void setLoginTimeout(int var1) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               @Override
               public void setLogWriter(PrintWriter var1) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               @Override
               public <T> T unwrap(Class<T> var1) throws SQLException {
                  return null;
               }
            };
         }

         return new FactoryMethodConnectionSource(var5, var0, var1, var4);
      } else {
         LOGGER.error("No class name or method name specified for the connection factory method.");
         return null;
      }
   }
}
