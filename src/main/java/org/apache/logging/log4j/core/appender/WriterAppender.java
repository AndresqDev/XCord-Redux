package org.apache.logging.log4j.core.appender;

import java.io.Writer;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldWriter;

@Plugin(
   name = "Writer",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class WriterAppender extends AbstractWriterAppender<WriterManager> {
   private static WriterAppender.WriterManagerFactory factory = new WriterAppender.WriterManagerFactory();

   @PluginFactory
   public static WriterAppender createAppender(StringLayout var0, Filter var1, Writer var2, String var3, boolean var4, boolean var5) {
      if (var3 == null) {
         LOGGER.error("No name provided for WriterAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         return new WriterAppender(var3, (StringLayout)var0, var1, getManager(var2, var4, (StringLayout)var0), var5, null);
      }
   }

   private static WriterManager getManager(Writer var0, boolean var1, StringLayout var2) {
      CloseShieldWriter var3 = new CloseShieldWriter(var0);
      String var4 = var0.getClass().getName() + "@" + Integer.toHexString(var0.hashCode()) + '.' + var1;
      return WriterManager.getManager(var4, new WriterAppender.FactoryData(var3, var4, var2), factory);
   }

   @PluginBuilderFactory
   public static <B extends WriterAppender.Builder<B>> B newBuilder() {
      return new WriterAppender.Builder<B>().asBuilder();
   }

   private WriterAppender(String var1, StringLayout var2, Filter var3, WriterManager var4, boolean var5, Property[] var6) {
      super(var1, var2, var3, var5, true, var6, var4);
   }

   public static class Builder<B extends WriterAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<WriterAppender> {
      private boolean follow = false;
      private Writer target;

      public WriterAppender build() {
         StringLayout var1 = (StringLayout)this.getLayout();
         Object var2 = var1 != null ? var1 : PatternLayout.createDefaultLayout();
         return new WriterAppender(
            this.getName(),
            (StringLayout)var2,
            this.getFilter(),
            WriterAppender.getManager(this.target, this.follow, (StringLayout)var2),
            this.isIgnoreExceptions(),
            this.getPropertyArray()
         );
      }

      public B setFollow(boolean var1) {
         this.follow = var1;
         return this.asBuilder();
      }

      public B setTarget(Writer var1) {
         this.target = var1;
         return this.asBuilder();
      }
   }

   private static class FactoryData {
      private final StringLayout layout;
      private final String name;
      private final Writer writer;

      public FactoryData(Writer var1, String var2, StringLayout var3) {
         this.writer = var1;
         this.name = var2;
         this.layout = var3;
      }
   }

   private static class WriterManagerFactory implements ManagerFactory<WriterManager, WriterAppender.FactoryData> {
      private WriterManagerFactory() {
      }

      public WriterManager createManager(String var1, WriterAppender.FactoryData var2) {
         return new WriterManager(var2.writer, var2.name, var2.layout, true);
      }
   }
}
