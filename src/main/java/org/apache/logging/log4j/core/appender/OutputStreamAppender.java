package org.apache.logging.log4j.core.appender;

import java.io.OutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.core.util.NullOutputStream;

@Plugin(
   name = "OutputStream",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class OutputStreamAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
   private static OutputStreamAppender.OutputStreamManagerFactory factory = new OutputStreamAppender.OutputStreamManagerFactory();

   @PluginFactory
   public static OutputStreamAppender createAppender(
      Layout<? extends Serializable> var0, Filter var1, OutputStream var2, String var3, boolean var4, boolean var5
   ) {
      if (var3 == null) {
         LOGGER.error("No name provided for OutputStreamAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         return new OutputStreamAppender(
            var3, (Layout<? extends Serializable>)var0, var1, getManager(var2, var4, (Layout<? extends Serializable>)var0), var5, null
         );
      }
   }

   private static OutputStreamManager getManager(OutputStream var0, boolean var1, Layout<? extends Serializable> var2) {
      Object var3 = var0 == null ? NullOutputStream.getInstance() : new CloseShieldOutputStream(var0);
      Object var4 = var0 == null ? var3 : var0;
      String var5 = var4.getClass().getName() + "@" + Integer.toHexString(var4.hashCode()) + '.' + var1;
      return OutputStreamManager.getManager(var5, new OutputStreamAppender.FactoryData((OutputStream)var3, var5, var2), factory);
   }

   @PluginBuilderFactory
   public static <B extends OutputStreamAppender.Builder<B>> B newBuilder() {
      return new OutputStreamAppender.Builder<B>().asBuilder();
   }

   private OutputStreamAppender(String var1, Layout<? extends Serializable> var2, Filter var3, OutputStreamManager var4, boolean var5, Property[] var6) {
      super(var1, var2, var3, var5, true, var6, var4);
   }

   public static class Builder<B extends OutputStreamAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<OutputStreamAppender> {
      private boolean follow = false;
      private final boolean ignoreExceptions = true;
      private OutputStream target;

      public OutputStreamAppender build() {
         Layout var1 = this.getLayout();
         Object var2 = var1 == null ? PatternLayout.createDefaultLayout() : var1;
         return new OutputStreamAppender(
            this.getName(),
            (Layout)var2,
            this.getFilter(),
            OutputStreamAppender.getManager(this.target, this.follow, (Layout<? extends Serializable>)var2),
            true,
            this.getPropertyArray()
         );
      }

      public B setFollow(boolean var1) {
         this.follow = var1;
         return this.asBuilder();
      }

      public B setTarget(OutputStream var1) {
         this.target = var1;
         return this.asBuilder();
      }
   }

   private static class FactoryData {
      private final Layout<? extends Serializable> layout;
      private final String name;
      private final OutputStream os;

      public FactoryData(OutputStream var1, String var2, Layout<? extends Serializable> var3) {
         this.os = var1;
         this.name = var2;
         this.layout = var3;
      }
   }

   private static class OutputStreamManagerFactory implements ManagerFactory<OutputStreamManager, OutputStreamAppender.FactoryData> {
      private OutputStreamManagerFactory() {
      }

      public OutputStreamManager createManager(String var1, OutputStreamAppender.FactoryData var2) {
         return new OutputStreamManager(var2.os, var2.name, var2.layout, true);
      }
   }
}
