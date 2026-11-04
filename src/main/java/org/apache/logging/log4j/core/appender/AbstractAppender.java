package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Integers;

public abstract class AbstractAppender extends AbstractFilterable implements Appender, LocationAware {
   private final String name;
   private final boolean ignoreExceptions;
   private final Layout<? extends Serializable> layout;
   private ErrorHandler handler = new DefaultErrorHandler(this);

   public static int parseInt(String var0, int var1) {
      try {
         return Integers.parseInt(var0, var1);
      } catch (NumberFormatException var3) {
         LOGGER.error("Could not parse \"{}\" as an integer,  using default value {}: {}", var0, var1, var3);
         return var1;
      }
   }

   @Override
   public boolean requiresLocation() {
      return this.layout instanceof LocationAware && ((LocationAware)this.layout).requiresLocation();
   }

   @Deprecated
   protected AbstractAppender(String var1, Filter var2, Layout<? extends Serializable> var3) {
      this(var1, var2, var3, true, Property.EMPTY_ARRAY);
   }

   @Deprecated
   protected AbstractAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4) {
      this(var1, var2, var3, var4, Property.EMPTY_ARRAY);
   }

   protected AbstractAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, Property[] var5) {
      super(var2, var5);
      this.name = Objects.requireNonNull(var1, "name");
      this.layout = var3;
      this.ignoreExceptions = var4;
   }

   public void error(String var1) {
      this.handler.error(var1);
   }

   public void error(String var1, LogEvent var2, Throwable var3) {
      this.handler.error(var1, var2, var3);
   }

   public void error(String var1, Throwable var2) {
      this.handler.error(var1, var2);
   }

   @Override
   public ErrorHandler getHandler() {
      return this.handler;
   }

   @Override
   public Layout<? extends Serializable> getLayout() {
      return this.layout;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean ignoreExceptions() {
      return this.ignoreExceptions;
   }

   @Override
   public void setHandler(ErrorHandler var1) {
      if (var1 == null) {
         LOGGER.error("The handler cannot be set to null");
      } else if (this.isStarted()) {
         LOGGER.error("The handler cannot be changed once the appender is started");
      } else {
         this.handler = var1;
      }
   }

   protected Serializable toSerializable(LogEvent var1) {
      return this.layout != null ? this.layout.toSerializable(var1) : null;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public abstract static class Builder<B extends AbstractAppender.Builder<B>> extends AbstractFilterable.Builder<B> {
      @PluginBuilderAttribute
      private boolean ignoreExceptions = true;
      @PluginElement("Layout")
      private Layout<? extends Serializable> layout;
      @PluginBuilderAttribute
      @Required(
         message = "No appender name provided"
      )
      private String name;
      @PluginConfiguration
      private Configuration configuration;

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public Layout<? extends Serializable> getLayout() {
         return this.layout;
      }

      public String getName() {
         return this.name;
      }

      public Layout<? extends Serializable> getOrCreateLayout() {
         return (Layout<? extends Serializable>)(this.layout == null ? PatternLayout.createDefaultLayout() : this.layout);
      }

      public Layout<? extends Serializable> getOrCreateLayout(Charset var1) {
         return (Layout<? extends Serializable>)(this.layout == null ? PatternLayout.newBuilder().withCharset(var1).build() : this.layout);
      }

      public boolean isIgnoreExceptions() {
         return this.ignoreExceptions;
      }

      public B setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this.asBuilder();
      }

      public B setIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return this.asBuilder();
      }

      public B setLayout(Layout<? extends Serializable> var1) {
         this.layout = var1;
         return this.asBuilder();
      }

      public B setName(String var1) {
         this.name = var1;
         return this.asBuilder();
      }

      @Deprecated
      public B withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this.asBuilder();
      }

      @Deprecated
      public B withIgnoreExceptions(boolean var1) {
         return this.setIgnoreExceptions(var1);
      }

      @Deprecated
      public B withLayout(Layout<? extends Serializable> var1) {
         return this.setLayout(var1);
      }

      @Deprecated
      public B withName(String var1) {
         return this.setName(var1);
      }
   }
}
