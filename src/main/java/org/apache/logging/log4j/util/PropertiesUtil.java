package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertiesUtil {
   private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
   private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
   private static final String SYSTEM = "system:";
   private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil("log4j2.component.properties");
   private final PropertiesUtil.Environment environment;

   public PropertiesUtil(Properties var1) {
      this.environment = new PropertiesUtil.Environment(new PropertiesPropertySource(var1));
   }

   public PropertiesUtil(String var1) {
      this.environment = new PropertiesUtil.Environment(new PropertyFilePropertySource(var1));
   }

   static Properties loadClose(InputStream var0, Object var1) {
      Properties var2 = new Properties();
      if (null != var0) {
         try {
            var2.load(var0);
         } catch (IOException var12) {
            LowLevelLogUtil.logException("Unable to read " + var1, var12);
         } finally {
            try {
               var0.close();
            } catch (IOException var11) {
               LowLevelLogUtil.logException("Unable to close " + var1, var11);
            }
         }
      }

      return var2;
   }

   public static PropertiesUtil getProperties() {
      return LOG4J_PROPERTIES;
   }

   public boolean hasProperty(String var1) {
      return this.environment.containsKey(var1);
   }

   public boolean getBooleanProperty(String var1) {
      return this.getBooleanProperty(var1, false);
   }

   public boolean getBooleanProperty(String var1, boolean var2) {
      String var3 = this.getStringProperty(var1);
      return var3 == null ? var2 : "true".equalsIgnoreCase(var3);
   }

   public boolean getBooleanProperty(String var1, boolean var2, boolean var3) {
      String var4 = this.getStringProperty(var1);
      return var4 == null ? var2 : (var4.isEmpty() ? var3 : "true".equalsIgnoreCase(var4));
   }

   public Boolean getBooleanProperty(String[] var1, String var2, Supplier<Boolean> var3) {
      for (String var7 : var1) {
         if (this.hasProperty(var7 + var2)) {
            return this.getBooleanProperty(var7 + var2);
         }
      }

      return var3 != null ? (Boolean)var3.get() : null;
   }

   public Charset getCharsetProperty(String var1) {
      return this.getCharsetProperty(var1, Charset.defaultCharset());
   }

   public Charset getCharsetProperty(String var1, Charset var2) {
      String var3 = this.getStringProperty(var1);
      if (var3 == null) {
         return var2;
      } else if (Charset.isSupported(var3)) {
         return Charset.forName(var3);
      } else {
         ResourceBundle var4 = getCharsetsResourceBundle();
         if (var4.containsKey(var1)) {
            String var5 = var4.getString(var1);
            if (Charset.isSupported(var5)) {
               return Charset.forName(var5);
            }
         }

         LowLevelLogUtil.log("Unable to get Charset '" + var3 + "' for property '" + var1 + "', using default " + var2 + " and continuing.");
         return var2;
      }
   }

   public double getDoubleProperty(String var1, double var2) {
      String var4 = this.getStringProperty(var1);
      if (var4 != null) {
         try {
            return Double.parseDouble(var4);
         } catch (Exception var6) {
         }
      }

      return var2;
   }

   public int getIntegerProperty(String var1, int var2) {
      String var3 = this.getStringProperty(var1);
      if (var3 != null) {
         try {
            return Integer.parseInt(var3);
         } catch (Exception var5) {
         }
      }

      return var2;
   }

   public Integer getIntegerProperty(String[] var1, String var2, Supplier<Integer> var3) {
      for (String var7 : var1) {
         if (this.hasProperty(var7 + var2)) {
            return this.getIntegerProperty(var7 + var2, 0);
         }
      }

      return var3 != null ? (Integer)var3.get() : null;
   }

   public long getLongProperty(String var1, long var2) {
      String var4 = this.getStringProperty(var1);
      if (var4 != null) {
         try {
            return Long.parseLong(var4);
         } catch (Exception var6) {
         }
      }

      return var2;
   }

   public Long getLongProperty(String[] var1, String var2, Supplier<Long> var3) {
      for (String var7 : var1) {
         if (this.hasProperty(var7 + var2)) {
            return this.getLongProperty(var7 + var2, 0L);
         }
      }

      return var3 != null ? (Long)var3.get() : null;
   }

   public Duration getDurationProperty(String var1, Duration var2) {
      String var3 = this.getStringProperty(var1);
      return var3 != null ? PropertiesUtil.TimeUnit.getDuration(var3) : var2;
   }

   public Duration getDurationProperty(String[] var1, String var2, Supplier<Duration> var3) {
      for (String var7 : var1) {
         if (this.hasProperty(var7 + var2)) {
            return this.getDurationProperty(var7 + var2, null);
         }
      }

      return var3 != null ? (Duration)var3.get() : null;
   }

   public String getStringProperty(String[] var1, String var2, Supplier<String> var3) {
      for (String var7 : var1) {
         String var8 = this.getStringProperty(var7 + var2);
         if (var8 != null) {
            return var8;
         }
      }

      return var3 != null ? (String)var3.get() : null;
   }

   public String getStringProperty(String var1) {
      return this.environment.get(var1);
   }

   public String getStringProperty(String var1, String var2) {
      String var3 = this.getStringProperty(var1);
      return var3 == null ? var2 : var3;
   }

   public static Properties getSystemProperties() {
      try {
         return new Properties(System.getProperties());
      } catch (SecurityException var1) {
         LowLevelLogUtil.logException("Unable to access system properties.", var1);
         return new Properties();
      }
   }

   public void reload() {
      this.environment.reload();
   }

   public static Properties extractSubset(Properties var0, String var1) {
      Properties var2 = new Properties();
      if (var1 != null && var1.length() != 0) {
         String var3 = var1.charAt(var1.length() - 1) != '.' ? var1 + '.' : var1;
         ArrayList var4 = new ArrayList();

         for (String var6 : var0.stringPropertyNames()) {
            if (var6.startsWith(var3)) {
               var2.setProperty(var6.substring(var3.length()), var0.getProperty(var6));
               var4.add(var6);
            }
         }

         for (String var8 : var4) {
            var0.remove(var8);
         }

         return var2;
      } else {
         return var2;
      }
   }

   static ResourceBundle getCharsetsResourceBundle() {
      return ResourceBundle.getBundle("Log4j-charsets");
   }

   public static Map<String, Properties> partitionOnCommonPrefixes(Properties var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap();

      for (String var3 : var0.stringPropertyNames()) {
         String var4 = var3.substring(0, var3.indexOf(46));
         if (!var1.containsKey(var4)) {
            var1.put(var4, new Properties());
         }

         ((Properties)var1.get(var4)).setProperty(var3.substring(var3.indexOf(46) + 1), var0.getProperty(var3));
      }

      return var1;
   }

   public boolean isOsWindows() {
      return this.getStringProperty("os.name", "").startsWith("Windows");
   }

   private static class Environment {
      private final Set<PropertySource> sources = new TreeSet<>(new PropertySource.Comparator());
      private final Map<CharSequence, String> literal = new ConcurrentHashMap<>();
      private final Map<CharSequence, String> normalized = new ConcurrentHashMap<>();
      private final Map<List<CharSequence>, String> tokenized = new ConcurrentHashMap<>();

      private Environment(PropertySource var1) {
         PropertyFilePropertySource var2 = new PropertyFilePropertySource("log4j2.system.properties");

         try {
            var2.forEach((var0, var1x) -> {
               if (System.getProperty(var0) == null) {
                  System.setProperty(var0, var1x);
               }
            });
         } catch (SecurityException var9) {
         }

         this.sources.add(var1);

         for (ClassLoader var6 : LoaderUtil.getClassLoaders()) {
            try {
               for (PropertySource var8 : ServiceLoader.load(PropertySource.class, var6)) {
                  this.sources.add(var8);
               }
            } catch (Throwable var10) {
            }
         }

         this.reload();
      }

      private synchronized void reload() {
         this.literal.clear();
         this.normalized.clear();
         this.tokenized.clear();

         for (PropertySource var2 : this.sources) {
            var2.forEach((var2x, var3) -> {
               if (var2x != null && var3 != null) {
                  this.literal.put(var2x, var3);
                  List var4 = PropertySource.Util.tokenize(var2x);
                  if (var4.isEmpty()) {
                     this.normalized.put(var2.getNormalForm(Collections.singleton(var2x)), var3);
                  } else {
                     this.normalized.put(var2.getNormalForm(var4), var3);
                     this.tokenized.put(var4, var3);
                  }
               }
            });
         }
      }

      private static boolean hasSystemProperty(String var0) {
         try {
            return System.getProperties().containsKey(var0);
         } catch (SecurityException var2) {
            return false;
         }
      }

      private String get(String var1) {
         if (this.normalized.containsKey(var1)) {
            return this.normalized.get(var1);
         } else if (this.literal.containsKey(var1)) {
            return this.literal.get(var1);
         } else if (hasSystemProperty(var1)) {
            return System.getProperty(var1);
         } else {
            for (PropertySource var3 : this.sources) {
               if (var3.containsProperty(var1)) {
                  return var3.getProperty(var1);
               }
            }

            return this.tokenized.get(PropertySource.Util.tokenize(var1));
         }
      }

      private boolean containsKey(String var1) {
         return this.normalized.containsKey(var1)
            || this.literal.containsKey(var1)
            || hasSystemProperty(var1)
            || this.tokenized.containsKey(PropertySource.Util.tokenize(var1));
      }
   }

   private static enum TimeUnit {
      NANOS("ns,nano,nanos,nanosecond,nanoseconds", ChronoUnit.NANOS),
      MICROS("us,micro,micros,microsecond,microseconds", ChronoUnit.MICROS),
      MILLIS("ms,milli,millis,millsecond,milliseconds", ChronoUnit.MILLIS),
      SECONDS("s,second,seconds", ChronoUnit.SECONDS),
      MINUTES("m,minute,minutes", ChronoUnit.MINUTES),
      HOURS("h,hour,hours", ChronoUnit.HOURS),
      DAYS("d,day,days", ChronoUnit.DAYS);

      private final String[] descriptions;
      private final ChronoUnit timeUnit;

      private TimeUnit(String var3, ChronoUnit var4) {
         this.descriptions = var3.split(",");
         this.timeUnit = var4;
      }

      ChronoUnit getTimeUnit() {
         return this.timeUnit;
      }

      static Duration getDuration(String var0) {
         String var1 = var0.trim();
         ChronoUnit var2 = ChronoUnit.MILLIS;
         long var3 = 0L;

         for (PropertiesUtil.TimeUnit var8 : values()) {
            for (String var12 : var8.descriptions) {
               if (var1.endsWith(var12)) {
                  var2 = var8.timeUnit;
                  var3 = Long.parseLong(var1.substring(0, var1.length() - var12.length()));
               }
            }
         }

         return Duration.of(var3, var2);
      }
   }
}
