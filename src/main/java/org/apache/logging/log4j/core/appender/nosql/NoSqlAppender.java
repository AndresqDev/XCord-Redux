package org.apache.logging.log4j.core.appender.nosql;

import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "NoSql",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class NoSqlAppender extends AbstractDatabaseAppender<NoSqlDatabaseManager<?>> {
   private final String description = this.getName() + "{ manager=" + this.getManager() + " }";

   @Deprecated
   public static NoSqlAppender createAppender(String var0, String var1, Filter var2, String var3, NoSqlProvider<?> var4) {
      if (var4 == null) {
         LOGGER.error("NoSQL provider not specified for appender [{}].", var0);
         return null;
      } else {
         int var5 = AbstractAppender.parseInt(var3, 0);
         boolean var6 = Booleans.parseBoolean(var1, true);
         String var7 = "noSqlManager{ description=" + var0 + ", bufferSize=" + var5 + ", provider=" + var4 + " }";
         NoSqlDatabaseManager var8 = NoSqlDatabaseManager.getNoSqlDatabaseManager(var7, var5, var4);
         return var8 == null ? null : new NoSqlAppender(var0, var2, null, var6, null, var8);
      }
   }

   @PluginBuilderFactory
   public static <B extends NoSqlAppender.Builder<B>> B newBuilder() {
      return new NoSqlAppender.Builder<B>().asBuilder();
   }

   private NoSqlAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, Property[] var5, NoSqlDatabaseManager<?> var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public String toString() {
      return this.description;
   }

   public static class Builder<B extends NoSqlAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<NoSqlAppender> {
      @PluginBuilderAttribute("bufferSize")
      private int bufferSize;
      @PluginElement("NoSqlProvider")
      private NoSqlProvider<?> provider;

      public NoSqlAppender build() {
         String var1 = this.getName();
         if (this.provider == null) {
            NoSqlAppender.LOGGER.error("NoSQL provider not specified for appender [{}].", var1);
            return null;
         } else {
            String var2 = "noSqlManager{ description=" + var1 + ", bufferSize=" + this.bufferSize + ", provider=" + this.provider + " }";
            NoSqlDatabaseManager var3 = NoSqlDatabaseManager.getNoSqlDatabaseManager(var2, this.bufferSize, this.provider);
            return var3 == null ? null : new NoSqlAppender(var1, this.getFilter(), this.getLayout(), this.isIgnoreExceptions(), this.getPropertyArray(), var3);
         }
      }

      public B setBufferSize(int var1) {
         this.bufferSize = var1;
         return this.asBuilder();
      }

      public B setProvider(NoSqlProvider<?> var1) {
         this.provider = var1;
         return this.asBuilder();
      }
   }
}
