package org.apache.logging.log4j.core.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationAware;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;

public class Interpolator extends AbstractConfigurationAwareLookup {
   public static final char PREFIX_SEPARATOR = ':';
   private static final String LOOKUP_KEY_WEB = "web";
   private static final String LOOKUP_KEY_DOCKER = "docker";
   private static final String LOOKUP_KEY_KUBERNETES = "kubernetes";
   private static final String LOOKUP_KEY_SPRING = "spring";
   private static final String LOOKUP_KEY_JNDI = "jndi";
   private static final String LOOKUP_KEY_JVMRUNARGS = "jvmrunargs";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Map<String, StrLookup> strLookupMap = new HashMap<>();
   private final StrLookup defaultLookup;

   public Interpolator(StrLookup var1) {
      this(var1, null);
   }

   public Interpolator(StrLookup var1, List<String> var2) {
      this.defaultLookup = (StrLookup)(var1 == null ? new PropertiesLookup(new HashMap<>()) : var1);
      PluginManager var3 = new PluginManager("Lookup");
      var3.collectPlugins(var2);
      Map var4 = var3.getPlugins();

      for (Entry var6 : var4.entrySet()) {
         try {
            Class var7 = ((PluginType)var6.getValue()).getPluginClass().asSubclass(StrLookup.class);
            if (!var7.getName().equals("org.apache.logging.log4j.core.lookup.JndiLookup") || JndiManager.isJndiLookupEnabled()) {
               this.strLookupMap.put(((String)var6.getKey()).toLowerCase(), ReflectionUtil.instantiate(var7));
            }
         } catch (Throwable var8) {
            this.handleError((String)var6.getKey(), var8);
         }
      }
   }

   public Interpolator() {
      this((Map<String, String>)null);
   }

   public Interpolator(Map<String, String> var1) {
      this.defaultLookup = new PropertiesLookup(var1);
      this.strLookupMap.put("log4j", new Log4jLookup());
      this.strLookupMap.put("sys", new SystemPropertiesLookup());
      this.strLookupMap.put("env", new EnvironmentLookup());
      this.strLookupMap.put("main", MainMapLookup.MAIN_SINGLETON);
      this.strLookupMap.put("map", new MapLookup(var1));
      this.strLookupMap.put("marker", new MarkerLookup());
      this.strLookupMap.put("java", new JavaLookup());
      this.strLookupMap.put("lower", new LowerLookup());
      this.strLookupMap.put("upper", new UpperLookup());
      if (JndiManager.isJndiLookupEnabled()) {
         try {
            this.strLookupMap.put("jndi", Loader.newCheckedInstanceOf("org.apache.logging.log4j.core.lookup.JndiLookup", StrLookup.class));
         } catch (Exception | LinkageError var8) {
            this.handleError("jndi", var8);
         }
      }

      try {
         this.strLookupMap
            .put("jvmrunargs", Loader.newCheckedInstanceOf("org.apache.logging.log4j.core.lookup.JmxRuntimeInputArgumentsLookup", StrLookup.class));
      } catch (Exception | LinkageError var7) {
         this.handleError("jvmrunargs", var7);
      }

      this.strLookupMap.put("date", new DateLookup());
      if (Constants.IS_WEB_APP) {
         try {
            this.strLookupMap.put("web", Loader.newCheckedInstanceOf("org.apache.logging.log4j.web.WebLookup", StrLookup.class));
         } catch (Exception var6) {
            this.handleError("web", var6);
         }
      } else {
         LOGGER.debug("Not in a ServletContext environment, thus not loading WebLookup plugin.");
      }

      try {
         this.strLookupMap.put("docker", Loader.newCheckedInstanceOf("org.apache.logging.log4j.docker.DockerLookup", StrLookup.class));
      } catch (Exception var5) {
         this.handleError("docker", var5);
      }

      try {
         this.strLookupMap.put("spring", Loader.newCheckedInstanceOf("org.apache.logging.log4j.spring.boot.SpringLookup", StrLookup.class));
      } catch (Exception var4) {
         this.handleError("spring", var4);
      }

      try {
         this.strLookupMap.put("kubernetes", Loader.newCheckedInstanceOf("org.apache.logging.log4j.kubernetes.KubernetesLookup", StrLookup.class));
      } catch (NoClassDefFoundError | Exception var3) {
         this.handleError("kubernetes", var3);
      }
   }

   public Map<String, StrLookup> getStrLookupMap() {
      return this.strLookupMap;
   }

   private void handleError(String var1, Throwable var2) {
      switch (var1) {
         case "jndi":
            LOGGER.warn(
               "JNDI lookup class is not available because this JRE does not support JNDI. JNDI string lookups will not be available, continuing configuration. Ignoring "
                  + var2
            );
            break;
         case "jvmrunargs":
            LOGGER.warn(
               "JMX runtime input lookup class is not available because this JRE does not support JMX. JMX lookups will not be available, continuing configuration. Ignoring "
                  + var2
            );
            break;
         case "web":
            LOGGER.info(
               "Log4j appears to be running in a Servlet environment, but there's no log4j-web module available. If you want better web container support, please add the log4j-web JAR to your web archive or server lib directory."
            );
         case "docker":
         case "spring":
            break;
         case "kubernetes":
            if (var2 instanceof NoClassDefFoundError) {
               LOGGER.warn("Unable to create Kubernetes lookup due to missing dependency: {}", var2.getMessage());
            }
            break;
         default:
            LOGGER.error("Unable to create Lookup for {}", var1, var2);
      }
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         int var3 = var2.indexOf(58);
         if (var3 >= 0) {
            String var4 = var2.substring(0, var3).toLowerCase(Locale.US);
            String var5 = var2.substring(var3 + 1);
            StrLookup var6 = this.strLookupMap.get(var4);
            if (var6 instanceof ConfigurationAware) {
               ((ConfigurationAware)var6).setConfiguration(this.configuration);
            }

            String var7 = null;
            if (var6 != null) {
               var7 = var1 == null ? var6.lookup(var5) : var6.lookup(var1, var5);
            }

            if (var7 != null) {
               return var7;
            }

            var2 = var2.substring(var3 + 1);
         }

         if (this.defaultLookup != null) {
            return var1 == null ? this.defaultLookup.lookup(var2) : this.defaultLookup.lookup(var1, var2);
         } else {
            return null;
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for (String var3 : this.strLookupMap.keySet()) {
         if (var1.length() == 0) {
            var1.append('{');
         } else {
            var1.append(", ");
         }

         var1.append(var3);
      }

      if (var1.length() > 0) {
         var1.append('}');
      }

      return var1.toString();
   }
}
