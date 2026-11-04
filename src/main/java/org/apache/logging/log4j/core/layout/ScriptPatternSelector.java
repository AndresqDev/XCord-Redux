package org.apache.logging.log4j.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ScriptPatternSelector",
   category = "Core",
   elementType = "patternSelector",
   printObject = true
)
public class ScriptPatternSelector implements LocationAware, PatternSelector {
   private final Map<String, PatternFormatter[]> formatterMap = new HashMap<>();
   private final Map<String, String> patternMap = new HashMap<>();
   private final PatternFormatter[] defaultFormatters;
   private final String defaultPattern;
   private static Logger LOGGER = StatusLogger.getLogger();
   private final AbstractScript script;
   private final Configuration configuration;
   private final boolean requiresLocation;

   @Deprecated
   public ScriptPatternSelector(AbstractScript var1, PatternMatch[] var2, String var3, boolean var4, boolean var5, boolean var6, Configuration var7) {
      this.script = var1;
      this.configuration = var7;
      if (!(var1 instanceof ScriptRef)) {
         var7.getScriptManager().addScript(var1);
      }

      PatternParser var8 = PatternLayout.createPatternParser(var7);
      boolean var9 = false;

      for (PatternMatch var13 : var2) {
         try {
            List var14 = var8.parse(var13.getPattern(), var4, var5, var6);
            PatternFormatter[] var15 = var14.toArray(PatternFormatter.EMPTY_ARRAY);
            this.formatterMap.put(var13.getKey(), var15);
            this.patternMap.put(var13.getKey(), var13.getPattern());

            for (int var16 = 0; !var9 && var16 < var15.length; var16++) {
               var9 = var15[var16].requiresLocation();
            }
         } catch (RuntimeException var18) {
            throw new IllegalArgumentException("Cannot parse pattern '" + var13.getPattern() + "'", var18);
         }
      }

      try {
         List var19 = var8.parse(var3, var4, var5, var6);
         this.defaultFormatters = var19.toArray(PatternFormatter.EMPTY_ARRAY);
         this.defaultPattern = var3;

         for (int var20 = 0; !var9 && var20 < this.defaultFormatters.length; var20++) {
            var9 = this.defaultFormatters[var20].requiresLocation();
         }
      } catch (RuntimeException var17) {
         throw new IllegalArgumentException("Cannot parse pattern '" + var3 + "'", var17);
      }

      this.requiresLocation = var9;
   }

   @Override
   public boolean requiresLocation() {
      return this.requiresLocation;
   }

   @Override
   public PatternFormatter[] getFormatters(LogEvent var1) {
      SimpleBindings var2 = new SimpleBindings();
      var2.putAll(this.configuration.getProperties());
      var2.put("substitutor", this.configuration.getStrSubstitutor());
      var2.put("logEvent", var1);
      Object var3 = this.configuration.getScriptManager().execute(this.script.getName(), var2);
      if (var3 == null) {
         return this.defaultFormatters;
      } else {
         PatternFormatter[] var4 = this.formatterMap.get(var3.toString());
         return var4 == null ? this.defaultFormatters : var4;
      }
   }

   @PluginBuilderFactory
   public static ScriptPatternSelector.Builder newBuilder() {
      return new ScriptPatternSelector.Builder();
   }

   @Deprecated
   public static ScriptPatternSelector createSelector(AbstractScript var0, PatternMatch[] var1, String var2, boolean var3, boolean var4, Configuration var5) {
      ScriptPatternSelector.Builder var6 = newBuilder();
      var6.setScript(var0);
      var6.setProperties(var1);
      var6.setDefaultPattern(var2);
      var6.setAlwaysWriteExceptions(var3);
      var6.setNoConsoleNoAnsi(var4);
      var6.setConfiguration(var5);
      return var6.build();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = true;

      for (Entry var4 : this.patternMap.entrySet()) {
         if (!var2) {
            var1.append(", ");
         }

         var1.append("key=\"").append((String)var4.getKey()).append("\", pattern=\"").append((String)var4.getValue()).append("\"");
         var2 = false;
      }

      if (!var2) {
         var1.append(", ");
      }

      var1.append("default=\"").append(this.defaultPattern).append("\"");
      return var1.toString();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<ScriptPatternSelector> {
      @PluginElement("Script")
      private AbstractScript script;
      @PluginElement("PatternMatch")
      private PatternMatch[] properties;
      @PluginBuilderAttribute("defaultPattern")
      private String defaultPattern;
      @PluginBuilderAttribute("alwaysWriteExceptions")
      private boolean alwaysWriteExceptions = true;
      @PluginBuilderAttribute("disableAnsi")
      private boolean disableAnsi;
      @PluginBuilderAttribute("noConsoleNoAnsi")
      private boolean noConsoleNoAnsi;
      @PluginConfiguration
      private Configuration configuration;

      private Builder() {
      }

      public ScriptPatternSelector build() {
         if (this.script == null) {
            ScriptPatternSelector.LOGGER.error("A Script, ScriptFile or ScriptRef element must be provided for this ScriptFilter");
            return null;
         } else if (this.script instanceof ScriptRef && this.configuration.getScriptManager().getScript(this.script.getName()) == null) {
            ScriptPatternSelector.LOGGER.error("No script with name {} has been declared.", this.script.getName());
            return null;
         } else {
            if (this.defaultPattern == null) {
               this.defaultPattern = "%m%n";
            }

            if (this.properties != null && this.properties.length != 0) {
               return new ScriptPatternSelector(
                  this.script, this.properties, this.defaultPattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.configuration
               );
            } else {
               ScriptPatternSelector.LOGGER.warn("No marker patterns were provided");
               return null;
            }
         }
      }

      public ScriptPatternSelector.Builder setScript(AbstractScript var1) {
         this.script = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setProperties(PatternMatch[] var1) {
         this.properties = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }
   }
}
