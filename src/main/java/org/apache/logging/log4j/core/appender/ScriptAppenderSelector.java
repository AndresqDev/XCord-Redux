package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.Objects;
import javax.script.Bindings;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;

@Plugin(
   name = "ScriptAppenderSelector",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class ScriptAppenderSelector extends AbstractAppender {
   @PluginBuilderFactory
   public static ScriptAppenderSelector.Builder newBuilder() {
      return new ScriptAppenderSelector.Builder();
   }

   private ScriptAppenderSelector(String var1, Filter var2, Layout<? extends Serializable> var3, Property[] var4) {
      super(var1, var2, var3, true, Property.EMPTY_ARRAY);
   }

   @Override
   public void append(LogEvent var1) {
   }

   public static final class Builder implements org.apache.logging.log4j.core.util.Builder<Appender> {
      @PluginElement("AppenderSet")
      @Required
      private AppenderSet appenderSet;
      @PluginConfiguration
      @Required
      private Configuration configuration;
      @PluginBuilderAttribute
      @Required
      private String name;
      @PluginElement("Script")
      @Required
      private AbstractScript script;

      public Appender build() {
         if (this.name == null) {
            ScriptAppenderSelector.LOGGER.error("Name missing.");
            return null;
         } else if (this.script == null) {
            ScriptAppenderSelector.LOGGER.error("Script missing for ScriptAppenderSelector appender {}", this.name);
            return null;
         } else if (this.appenderSet == null) {
            ScriptAppenderSelector.LOGGER.error("AppenderSet missing for ScriptAppenderSelector appender {}", this.name);
            return null;
         } else if (this.configuration == null) {
            ScriptAppenderSelector.LOGGER.error("Configuration missing for ScriptAppenderSelector appender {}", this.name);
            return null;
         } else {
            ScriptManager var1 = this.configuration.getScriptManager();
            var1.addScript(this.script);
            Bindings var2 = var1.createBindings(this.script);
            ScriptAppenderSelector.LOGGER
               .debug(
                  "ScriptAppenderSelector '{}' executing {} '{}': {}", this.name, this.script.getLanguage(), this.script.getName(), this.script.getScriptText()
               );
            Object var3 = var1.execute(this.script.getName(), var2);
            String var4 = Objects.toString(var3, null);
            ScriptAppenderSelector.LOGGER.debug("ScriptAppenderSelector '{}' selected '{}'", this.name, var4);
            return this.appenderSet.createAppender(var4, this.name);
         }
      }

      public AppenderSet getAppenderSet() {
         return this.appenderSet;
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public String getName() {
         return this.name;
      }

      public AbstractScript getScript() {
         return this.script;
      }

      public ScriptAppenderSelector.Builder withAppenderNodeSet(AppenderSet var1) {
         this.appenderSet = var1;
         return this;
      }

      public ScriptAppenderSelector.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public ScriptAppenderSelector.Builder withName(String var1) {
         this.name = var1;
         return this;
      }

      public ScriptAppenderSelector.Builder withScript(AbstractScript var1) {
         this.script = var1;
         return this;
      }
   }
}
