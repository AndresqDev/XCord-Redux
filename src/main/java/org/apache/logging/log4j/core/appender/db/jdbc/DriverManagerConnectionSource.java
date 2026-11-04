package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "DriverManager",
   category = "Core",
   elementType = "connectionSource",
   printObject = true
)
public class DriverManagerConnectionSource extends AbstractDriverManagerConnectionSource {
   @PluginBuilderFactory
   public static <B extends DriverManagerConnectionSource.Builder<B>> B newBuilder() {
      return new DriverManagerConnectionSource.Builder<B>().asBuilder();
   }

   public DriverManagerConnectionSource(String var1, String var2, String var3, char[] var4, char[] var5, Property[] var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public static class Builder<B extends DriverManagerConnectionSource.Builder<B>>
      extends AbstractDriverManagerConnectionSource.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<DriverManagerConnectionSource> {
      public DriverManagerConnectionSource build() {
         return new DriverManagerConnectionSource(
            this.getDriverClassName(), this.getConnectionString(), this.getConnectionString(), this.getUserName(), this.getPassword(), this.getProperties()
         );
      }
   }
}
