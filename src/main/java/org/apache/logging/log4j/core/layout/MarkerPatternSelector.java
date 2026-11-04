package org.apache.logging.log4j.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
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
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "MarkerPatternSelector",
   category = "Core",
   elementType = "patternSelector",
   printObject = true
)
public class MarkerPatternSelector implements LocationAware, PatternSelector {
   private final Map<String, PatternFormatter[]> formatterMap = new HashMap<>();
   private final Map<String, String> patternMap = new HashMap<>();
   private final PatternFormatter[] defaultFormatters;
   private final String defaultPattern;
   private static Logger LOGGER = StatusLogger.getLogger();
   private final boolean requiresLocation;

   @Deprecated
   public MarkerPatternSelector(PatternMatch[] var1, String var2, boolean var3, boolean var4, Configuration var5) {
      this(var1, var2, var3, false, var4, var5);
   }

   private MarkerPatternSelector(PatternMatch[] var1, String var2, boolean var3, boolean var4, boolean var5, Configuration var6) {
      boolean var7 = false;
      PatternParser var8 = PatternLayout.createPatternParser(var6);

      for (PatternMatch var12 : var1) {
         try {
            List var13 = var8.parse(var12.getPattern(), var3, var4, var5);
            PatternFormatter[] var14 = var13.toArray(PatternFormatter.EMPTY_ARRAY);
            this.formatterMap.put(var12.getKey(), var14);

            for (int var15 = 0; !var7 && var15 < var14.length; var15++) {
               var7 = var14[var15].requiresLocation();
            }

            this.patternMap.put(var12.getKey(), var12.getPattern());
         } catch (RuntimeException var17) {
            throw new IllegalArgumentException("Cannot parse pattern '" + var12.getPattern() + "'", var17);
         }
      }

      try {
         List var18 = var8.parse(var2, var3, var4, var5);
         this.defaultFormatters = var18.toArray(PatternFormatter.EMPTY_ARRAY);
         this.defaultPattern = var2;

         for (int var19 = 0; !var7 && var19 < this.defaultFormatters.length; var19++) {
            var7 = this.defaultFormatters[var19].requiresLocation();
         }
      } catch (RuntimeException var16) {
         throw new IllegalArgumentException("Cannot parse pattern '" + var2 + "'", var16);
      }

      this.requiresLocation = var7;
   }

   @Override
   public boolean requiresLocation() {
      return this.requiresLocation;
   }

   @Override
   public PatternFormatter[] getFormatters(LogEvent var1) {
      Marker var2 = var1.getMarker();
      if (var2 == null) {
         return this.defaultFormatters;
      } else {
         for (String var4 : this.formatterMap.keySet()) {
            if (var2.isInstanceOf(var4)) {
               return this.formatterMap.get(var4);
            }
         }

         return this.defaultFormatters;
      }
   }

   @PluginBuilderFactory
   public static MarkerPatternSelector.Builder newBuilder() {
      return new MarkerPatternSelector.Builder();
   }

   @Deprecated
   public static MarkerPatternSelector createSelector(PatternMatch[] var0, String var1, boolean var2, boolean var3, Configuration var4) {
      MarkerPatternSelector.Builder var5 = newBuilder();
      var5.setProperties(var0);
      var5.setDefaultPattern(var1);
      var5.setAlwaysWriteExceptions(var2);
      var5.setNoConsoleNoAnsi(var3);
      var5.setConfiguration(var4);
      return var5.build();
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

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<MarkerPatternSelector> {
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

      public MarkerPatternSelector build() {
         if (this.defaultPattern == null) {
            this.defaultPattern = "%m%n";
         }

         if (this.properties != null && this.properties.length != 0) {
            return new MarkerPatternSelector(
               this.properties, this.defaultPattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.configuration
            );
         } else {
            MarkerPatternSelector.LOGGER.warn("No marker patterns were provided with PatternMatch");
            return null;
         }
      }

      public MarkerPatternSelector.Builder setProperties(PatternMatch[] var1) {
         this.properties = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }
   }
}
