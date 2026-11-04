package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "logger",
   category = "Core",
   printObject = true
)
public class LoggerConfig extends AbstractFilterable implements LocationAware {
   public static final String ROOT = "root";
   private static LogEventFactory LOG_EVENT_FACTORY = null;
   private List<AppenderRef> appenderRefs = new ArrayList<>();
   private final AppenderControlArraySet appenders = new AppenderControlArraySet();
   private final String name;
   private LogEventFactory logEventFactory;
   private Level level;
   private boolean additive = true;
   private boolean includeLocation = true;
   private LoggerConfig parent;
   private Map<Property, Boolean> propertiesMap;
   private final List<Property> properties;
   private final boolean propertiesRequireLookup;
   private final Configuration config;
   private final ReliabilityStrategy reliabilityStrategy;

   public LoggerConfig() {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.level = Level.ERROR;
      this.name = "";
      this.properties = null;
      this.propertiesRequireLookup = false;
      this.config = null;
      this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
   }

   public LoggerConfig(String var1, Level var2, boolean var3) {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = var1;
      this.level = var2;
      this.additive = var3;
      this.properties = null;
      this.propertiesRequireLookup = false;
      this.config = null;
      this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
   }

   protected LoggerConfig(String var1, List<AppenderRef> var2, Filter var3, Level var4, boolean var5, Property[] var6, Configuration var7, boolean var8) {
      super(var3);
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = var1;
      this.appenderRefs = var2;
      this.level = var4;
      this.additive = var5;
      this.includeLocation = var8;
      this.config = var7;
      if (var6 != null && var6.length > 0) {
         this.properties = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(var6, var6.length)));
      } else {
         this.properties = null;
      }

      this.propertiesRequireLookup = containsPropertyRequiringLookup(var6);
      this.reliabilityStrategy = var7.getReliabilityStrategy(this);
   }

   private static boolean containsPropertyRequiringLookup(Property[] var0) {
      if (var0 == null) {
         return false;
      } else {
         for (int var1 = 0; var1 < var0.length; var1++) {
            if (var0[var1].isValueNeedsLookup()) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public Filter getFilter() {
      return super.getFilter();
   }

   public String getName() {
      return this.name;
   }

   public void setParent(LoggerConfig var1) {
      this.parent = var1;
   }

   public LoggerConfig getParent() {
      return this.parent;
   }

   public void addAppender(Appender var1, Level var2, Filter var3) {
      this.appenders.add(new AppenderControl(var1, var2, var3));
   }

   public void removeAppender(String var1) {
      Object var2 = null;

      while ((var2 = this.appenders.remove(var1)) != null) {
         this.cleanupFilter((AppenderControl)var2);
      }
   }

   public Map<String, Appender> getAppenders() {
      return this.appenders.asMap();
   }

   protected void clearAppenders() {
      do {
         AppenderControl[] var1 = this.appenders.clear();

         for (AppenderControl var5 : var1) {
            this.cleanupFilter(var5);
         }
      } while (!this.appenders.isEmpty());
   }

   private void cleanupFilter(AppenderControl var1) {
      Filter var2 = var1.getFilter();
      if (var2 != null) {
         var1.removeFilter(var2);
         var2.stop();
      }
   }

   public List<AppenderRef> getAppenderRefs() {
      return this.appenderRefs;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public Level getLevel() {
      return this.level == null ? (this.parent == null ? Level.ERROR : this.parent.getLevel()) : this.level;
   }

   public LogEventFactory getLogEventFactory() {
      return this.logEventFactory;
   }

   public void setLogEventFactory(LogEventFactory var1) {
      this.logEventFactory = var1;
   }

   public boolean isAdditive() {
      return this.additive;
   }

   public void setAdditive(boolean var1) {
      this.additive = var1;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   @Deprecated
   public Map<Property, Boolean> getProperties() {
      if (this.properties == null) {
         return null;
      } else {
         if (this.propertiesMap == null) {
            HashMap var1 = new HashMap(this.properties.size() * 2);

            for (int var2 = 0; var2 < this.properties.size(); var2++) {
               var1.put(this.properties.get(var2), this.properties.get(var2).isValueNeedsLookup());
            }

            this.propertiesMap = Collections.unmodifiableMap(var1);
         }

         return this.propertiesMap;
      }
   }

   public List<Property> getPropertyList() {
      return this.properties;
   }

   public boolean isPropertiesRequireLookup() {
      return this.propertiesRequireLookup;
   }

   @PerformanceSensitive({"allocation"})
   public void log(String var1, String var2, Marker var3, Level var4, Message var5, Throwable var6) {
      List var7 = this.getProperties(var1, var2, var3, var4, var5, var6);
      LogEvent var8 = this.logEventFactory.createEvent(var1, var3, var2, this.location(var2), var4, var5, var7, var6);

      try {
         this.log(var8, LoggerConfig.LoggerConfigPredicate.ALL);
      } finally {
         ReusableLogEventFactory.release(var8);
      }
   }

   private StackTraceElement location(String var1) {
      return this.requiresLocation() ? StackLocatorUtil.calcLocation(var1) : null;
   }

   @PerformanceSensitive({"allocation"})
   public void log(String var1, String var2, StackTraceElement var3, Marker var4, Level var5, Message var6, Throwable var7) {
      List var8 = this.getProperties(var1, var2, var4, var5, var6, var7);
      LogEvent var9 = this.logEventFactory.createEvent(var1, var4, var2, var3, var5, var6, var8, var7);

      try {
         this.log(var9, LoggerConfig.LoggerConfigPredicate.ALL);
      } finally {
         ReusableLogEventFactory.release(var9);
      }
   }

   private List<Property> getProperties(String var1, String var2, Marker var3, Level var4, Message var5, Throwable var6) {
      List var7 = this.properties;
      return var7 != null && this.propertiesRequireLookup ? this.getPropertiesWithLookups(var1, var2, var3, var4, var5, var6, var7) : var7;
   }

   private List<Property> getPropertiesWithLookups(String var1, String var2, Marker var3, Level var4, Message var5, Throwable var6, List<Property> var7) {
      ArrayList var8 = new ArrayList(var7.size());
      Log4jLogEvent var9 = Log4jLogEvent.newBuilder()
         .setMessage(var5)
         .setMarker(var3)
         .setLevel(var4)
         .setLoggerName(var1)
         .setLoggerFqcn(var2)
         .setThrown(var6)
         .build();

      for (int var10 = 0; var10 < var7.size(); var10++) {
         Property var11 = (Property)var7.get(var10);
         String var12 = var11.isValueNeedsLookup() ? this.config.getStrSubstitutor().replace(var9, var11.getValue()) : var11.getValue();
         var8.add(Property.createProperty(var11.getName(), var12));
      }

      return var8;
   }

   public void log(LogEvent var1) {
      this.log(var1, LoggerConfig.LoggerConfigPredicate.ALL);
   }

   protected void log(LogEvent var1, LoggerConfig.LoggerConfigPredicate var2) {
      if (!this.isFiltered(var1)) {
         this.processLogEvent(var1, var2);
      }
   }

   public ReliabilityStrategy getReliabilityStrategy() {
      return this.reliabilityStrategy;
   }

   private void processLogEvent(LogEvent var1, LoggerConfig.LoggerConfigPredicate var2) {
      var1.setIncludeLocation(this.isIncludeLocation());
      if (var2.allow(this)) {
         this.callAppenders(var1);
      }

      this.logParent(var1, var2);
   }

   @Override
   public boolean requiresLocation() {
      if (!this.includeLocation) {
         return false;
      } else {
         AppenderControl[] var1 = this.appenders.get();
         LoggerConfig var2 = this;

         while (var2 != null) {
            for (AppenderControl var6 : var1) {
               Appender var7 = var6.getAppender();
               if (var7 instanceof LocationAware && ((LocationAware)var7).requiresLocation()) {
                  return true;
               }
            }

            if (!var2.additive) {
               break;
            }

            var2 = var2.parent;
            if (var2 != null) {
               var1 = var2.appenders.get();
            }
         }

         return false;
      }
   }

   private void logParent(LogEvent var1, LoggerConfig.LoggerConfigPredicate var2) {
      if (this.additive && this.parent != null) {
         this.parent.log(var1, var2);
      }
   }

   @PerformanceSensitive({"allocation"})
   protected void callAppenders(LogEvent var1) {
      AppenderControl[] var2 = this.appenders.get();

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3].callAppender(var1);
      }
   }

   @Override
   public String toString() {
      return Strings.isEmpty(this.name) ? "root" : this.name;
   }

   @Deprecated
   public static LoggerConfig createLogger(
      String var0,
      Level var1,
      @PluginAttribute("name") String var2,
      String var3,
      AppenderRef[] var4,
      Property[] var5,
      @PluginConfiguration Configuration var6,
      Filter var7
   ) {
      if (var2 == null) {
         LOGGER.error("Loggers cannot be configured without a name");
         return null;
      } else {
         List var8 = Arrays.asList(var4);
         String var9 = var2.equals("root") ? "" : var2;
         boolean var10 = Booleans.parseBoolean(var0, true);
         return new LoggerConfig(var9, var8, var7, var1, var10, var5, var6, includeLocation(var3, var6));
      }
   }

   @PluginFactory
   public static LoggerConfig createLogger(
      @PluginAttribute(value = "additivity",defaultBoolean = true) boolean var0,
      @PluginAttribute("level") Level var1,
      @Required(message = "Loggers cannot be configured without a name") @PluginAttribute("name") String var2,
      @PluginAttribute("includeLocation") String var3,
      @PluginElement("AppenderRef") AppenderRef[] var4,
      @PluginElement("Properties") Property[] var5,
      @PluginConfiguration Configuration var6,
      @PluginElement("Filter") Filter var7
   ) {
      String var8 = var2.equals("root") ? "" : var2;
      return new LoggerConfig(var8, Arrays.asList(var4), var7, var1, var0, var5, var6, includeLocation(var3, var6));
   }

   @Deprecated
   protected static boolean includeLocation(String var0) {
      return includeLocation(var0, null);
   }

   protected static boolean includeLocation(String var0, Configuration var1) {
      if (var0 == null) {
         LoggerContext var2 = null;
         if (var1 != null) {
            var2 = var1.getLoggerContext();
         }

         return var2 != null ? !(var2 instanceof AsyncLoggerContext) : !AsyncLoggerContextSelector.isSelected();
      } else {
         return Boolean.parseBoolean(var0);
      }
   }

   protected final boolean hasAppenders() {
      return !this.appenders.isEmpty();
   }

   static {
      String var0 = PropertiesUtil.getProperties().getStringProperty("Log4jLogEventFactory");
      if (var0 != null) {
         try {
            Class var1 = Loader.loadClass(var0);
            if (var1 != null && LogEventFactory.class.isAssignableFrom(var1)) {
               LOG_EVENT_FACTORY = (LogEventFactory)var1.newInstance();
            }
         } catch (Exception var2) {
            LOGGER.error("Unable to create LogEventFactory {}", var0, var2);
         }
      }

      if (LOG_EVENT_FACTORY == null) {
         LOG_EVENT_FACTORY = (LogEventFactory)(Constants.ENABLE_THREADLOCALS ? new ReusableLogEventFactory() : new DefaultLogEventFactory());
      }
   }

   protected static enum LoggerConfigPredicate {
      ALL {
         @Override
         boolean allow(LoggerConfig var1) {
            return true;
         }
      },
      ASYNCHRONOUS_ONLY {
         @Override
         boolean allow(LoggerConfig var1) {
            return var1 instanceof AsyncLoggerConfig;
         }
      },
      SYNCHRONOUS_ONLY {
         @Override
         boolean allow(LoggerConfig var1) {
            return !ASYNCHRONOUS_ONLY.allow(var1);
         }
      };

      private LoggerConfigPredicate() {
      }

      abstract boolean allow(LoggerConfig var1);
   }

   @Plugin(
      name = "root",
      category = "Core",
      printObject = true
   )
   public static class RootLogger extends LoggerConfig {
      @PluginFactory
      public static LoggerConfig createLogger(
         @PluginAttribute("additivity") String var0,
         @PluginAttribute("level") Level var1,
         @PluginAttribute("includeLocation") String var2,
         @PluginElement("AppenderRef") AppenderRef[] var3,
         @PluginElement("Properties") Property[] var4,
         @PluginConfiguration Configuration var5,
         @PluginElement("Filter") Filter var6
      ) {
         List var7 = Arrays.asList(var3);
         Level var8 = var1 == null ? Level.ERROR : var1;
         boolean var9 = Booleans.parseBoolean(var0, true);
         return new LoggerConfig("", var7, var6, var8, var9, var4, var5, includeLocation(var2, var5));
      }
   }
}
