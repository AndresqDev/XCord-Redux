package org.apache.logging.log4j.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.lookup.ConfigurationStrSubstitutor;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.util.AuthorizationProvider;
import org.apache.logging.log4j.core.util.BasicAuthorizationProvider;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public abstract class ConfigurationFactory extends ConfigurationBuilderFactory {
   public static final String CONFIGURATION_FACTORY_PROPERTY = "log4j.configurationFactory";
   public static final String CONFIGURATION_FILE_PROPERTY = "log4j.configurationFile";
   public static final String LOG4J1_CONFIGURATION_FILE_PROPERTY = "log4j.configuration";
   public static final String LOG4J1_EXPERIMENTAL = "log4j1.compatibility";
   public static final String AUTHORIZATION_PROVIDER = "log4j2.authorizationProvider";
   public static final String CATEGORY = "ConfigurationFactory";
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected static final String TEST_PREFIX = "log4j2-test";
   protected static final String DEFAULT_PREFIX = "log4j2";
   protected static final String LOG4J1_VERSION = "1";
   protected static final String LOG4J2_VERSION = "2";
   private static final String CLASS_LOADER_SCHEME = "classloader";
   private static final String CLASS_PATH_SCHEME = "classpath";
   private static final String OVERRIDE_PARAM = "override";
   private static volatile List<ConfigurationFactory> factories;
   private static ConfigurationFactory configFactory = new ConfigurationFactory.Factory();
   protected final StrSubstitutor substitutor = new ConfigurationStrSubstitutor(new Interpolator());
   private static final Lock LOCK = new ReentrantLock();
   private static final String HTTPS = "https";
   private static final String HTTP = "http";
   private static volatile AuthorizationProvider authorizationProvider;

   public static ConfigurationFactory getInstance() {
      if (factories == null) {
         LOCK.lock();

         try {
            if (factories == null) {
               ArrayList var0 = new ArrayList();
               PropertiesUtil var1 = PropertiesUtil.getProperties();
               String var2 = var1.getStringProperty("log4j.configurationFactory");
               if (var2 != null) {
                  addFactory(var0, var2);
               }

               PluginManager var3 = new PluginManager("ConfigurationFactory");
               var3.collectPlugins();
               Map var4 = var3.getPlugins();
               ArrayList var5 = new ArrayList(var4.size());

               for (PluginType var7 : var4.values()) {
                  try {
                     var5.add(var7.getPluginClass().asSubclass(ConfigurationFactory.class));
                  } catch (Exception var12) {
                     LOGGER.warn("Unable to add class {}", var7.getPluginClass(), var12);
                  }
               }

               Collections.sort(var5, OrderComparator.getInstance());

               for (Class var15 : var5) {
                  addFactory(var0, var15);
               }

               factories = Collections.unmodifiableList(var0);
               authorizationProvider = authorizationProvider(var1);
            }
         } finally {
            LOCK.unlock();
         }
      }

      LOGGER.debug("Using configurationFactory {}", configFactory);
      return configFactory;
   }

   public static AuthorizationProvider authorizationProvider(PropertiesUtil var0) {
      String var1 = var0.getStringProperty("log4j2.authorizationProvider");
      Object var2 = null;
      if (var1 != null) {
         try {
            Object var3 = LoaderUtil.newInstanceOf(var1);
            if (var3 instanceof AuthorizationProvider) {
               var2 = (AuthorizationProvider)var3;
            } else {
               LOGGER.warn("{} is not an AuthorizationProvider, using default", var3.getClass().getName());
            }
         } catch (Exception var4) {
            LOGGER.warn("Unable to create {}, using default: {}", var1, var4.getMessage());
         }
      }

      if (var2 == null) {
         var2 = new BasicAuthorizationProvider(var0);
      }

      return (AuthorizationProvider)var2;
   }

   public static AuthorizationProvider getAuthorizationProvider() {
      return authorizationProvider;
   }

   private static void addFactory(Collection<ConfigurationFactory> var0, String var1) {
      try {
         addFactory(var0, Loader.loadClass(var1).asSubclass(ConfigurationFactory.class));
      } catch (Exception var3) {
         LOGGER.error("Unable to load class {}", var1, var3);
      }
   }

   private static void addFactory(Collection<ConfigurationFactory> var0, Class<? extends ConfigurationFactory> var1) {
      try {
         var0.add(ReflectionUtil.instantiate(var1));
      } catch (Exception var3) {
         LOGGER.error("Unable to create instance of {}", var1.getName(), var3);
      }
   }

   public static void setConfigurationFactory(ConfigurationFactory var0) {
      configFactory = var0;
   }

   public static void resetConfigurationFactory() {
      configFactory = new ConfigurationFactory.Factory();
   }

   public static void removeConfigurationFactory(ConfigurationFactory var0) {
      if (configFactory == var0) {
         configFactory = new ConfigurationFactory.Factory();
      }
   }

   protected abstract String[] getSupportedTypes();

   protected String getTestPrefix() {
      return "log4j2-test";
   }

   protected String getDefaultPrefix() {
      return "log4j2";
   }

   protected String getVersion() {
      return "2";
   }

   protected boolean isActive() {
      return true;
   }

   public abstract Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2);

   public Configuration getConfiguration(LoggerContext var1, String var2, URI var3) {
      if (!this.isActive()) {
         return null;
      } else {
         if (var3 != null) {
            ConfigurationSource var4 = ConfigurationSource.fromUri(var3);
            if (var4 != null) {
               return this.getConfiguration(var1, var4);
            }
         }

         return null;
      }
   }

   public Configuration getConfiguration(LoggerContext var1, String var2, URI var3, ClassLoader var4) {
      if (!this.isActive()) {
         return null;
      } else if (var4 == null) {
         return this.getConfiguration(var1, var2, var3);
      } else {
         if (isClassLoaderUri(var3)) {
            String var5 = extractClassLoaderUriPath(var3);
            ConfigurationSource var6 = ConfigurationSource.fromResource(var5, var4);
            if (var6 != null) {
               Configuration var7 = this.getConfiguration(var1, var6);
               if (var7 != null) {
                  return var7;
               }
            }
         }

         return this.getConfiguration(var1, var2, var3);
      }
   }

   static boolean isClassLoaderUri(URI var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.getScheme();
         return var1 == null || var1.equals("classloader") || var1.equals("classpath");
      }
   }

   static String extractClassLoaderUriPath(URI var0) {
      return var0.getScheme() == null ? var0.getPath() : var0.getSchemeSpecificPart();
   }

   protected ConfigurationSource getInputFromString(String var1, ClassLoader var2) {
      try {
         URL var3 = new URL(var1);
         URLConnection var8 = UrlConnectionFactory.createConnection(var3);
         File var9 = FileUtils.fileFromUri(var3.toURI());
         return var9 != null
            ? new ConfigurationSource(var8.getInputStream(), FileUtils.fileFromUri(var3.toURI()))
            : new ConfigurationSource(var8.getInputStream(), var3, var8.getLastModified());
      } catch (Exception var7) {
         ConfigurationSource var4 = ConfigurationSource.fromResource(var1, var2);
         if (var4 == null) {
            try {
               File var5 = new File(var1);
               return new ConfigurationSource(new FileInputStream(var5), var5);
            } catch (FileNotFoundException var6) {
               LOGGER.catching(Level.DEBUG, var6);
            }
         }

         return var4;
      }
   }

   static List<ConfigurationFactory> getFactories() {
      return factories;
   }

   private static class Factory extends ConfigurationFactory {
      private static final String ALL_TYPES = "*";

      private Factory() {
      }

      @Override
      public Configuration getConfiguration(LoggerContext var1, String var2, URI var3) {
         if (var3 == null) {
            String var4 = this.substitutor.replace(PropertiesUtil.getProperties().getStringProperty("log4j.configurationFile"));
            if (var4 != null) {
               String[] var16 = this.parseConfigLocations(var4);
               if (var16.length > 1) {
                  ArrayList var19 = new ArrayList();

                  for (String var31 : var16) {
                     Configuration var34 = this.getConfiguration(var1, var31.trim());
                     if (var34 != null) {
                        if (!(var34 instanceof AbstractConfiguration)) {
                           LOGGER.error("Failed to created configuration at {}", var31);
                           return null;
                        }

                        var19.add((AbstractConfiguration)var34);
                     } else {
                        LOGGER.warn("Unable to create configuration for {}, ignoring", var31);
                     }
                  }

                  if (var19.size() > 1) {
                     return new CompositeConfiguration(var19);
                  }

                  if (var19.size() == 1) {
                     return (Configuration)var19.get(0);
                  }
               }

               return this.getConfiguration(var1, var4);
            }

            String var5 = this.substitutor.replace(PropertiesUtil.getProperties().getStringProperty("log4j.configuration"));
            if (var5 != null) {
               System.setProperty("log4j1.compatibility", "true");
               return this.getConfiguration("1", var1, var5);
            }

            for (ConfigurationFactory var7 : getFactories()) {
               String[] var8 = var7.getSupportedTypes();
               if (var8 != null) {
                  for (String var12 : var8) {
                     if (var12.equals("*")) {
                        Configuration var13 = var7.getConfiguration(var1, var2, var3);
                        if (var13 != null) {
                           return var13;
                        }
                     }
                  }
               }
            }
         } else {
            String[] var14 = this.parseConfigLocations(var3);
            if (var14.length > 1) {
               ArrayList var18 = new ArrayList();

               for (String var30 : var14) {
                  Configuration var33 = this.getConfiguration(var1, var30.trim());
                  if (!(var33 instanceof AbstractConfiguration)) {
                     LOGGER.error("Failed to created configuration at {}", var30);
                     return null;
                  }

                  var18.add((AbstractConfiguration)var33);
               }

               return new CompositeConfiguration(var18);
            }

            String var17 = var3.toString();

            for (ConfigurationFactory var23 : getFactories()) {
               String[] var26 = var23.getSupportedTypes();
               if (var26 != null) {
                  for (String var36 : var26) {
                     if (var36.equals("*") || var17.endsWith(var36)) {
                        Configuration var37 = var23.getConfiguration(var1, var2, var3);
                        if (var37 != null) {
                           return var37;
                        }
                     }
                  }
               }
            }
         }

         Configuration var15 = this.getConfiguration(var1, true, var2);
         if (var15 == null) {
            var15 = this.getConfiguration(var1, true, null);
            if (var15 == null) {
               var15 = this.getConfiguration(var1, false, var2);
               if (var15 == null) {
                  var15 = this.getConfiguration(var1, false, null);
               }
            }
         }

         if (var15 != null) {
            return var15;
         } else {
            LOGGER.warn(
               "No Log4j 2 configuration file found. Using default configuration (logging only errors to the console), or user programmatically provided configurations. Set system property 'log4j2.debug' to show Log4j 2 internal initialization logging. See https://logging.apache.org/log4j/2.x/manual/configuration.html for instructions on how to configure Log4j 2"
            );
            return new DefaultConfiguration();
         }
      }

      private Configuration getConfiguration(LoggerContext var1, String var2) {
         return this.getConfiguration(null, var1, var2);
      }

      private Configuration getConfiguration(String var1, LoggerContext var2, String var3) {
         ConfigurationSource var4 = null;

         try {
            var4 = ConfigurationSource.fromUri(NetUtils.toURI(var3));
         } catch (Exception var13) {
            LOGGER.catching(Level.DEBUG, var13);
         }

         if (var4 == null) {
            ClassLoader var5 = LoaderUtil.getThreadContextClassLoader();
            var4 = this.getInputFromString(var3, var5);
         }

         if (var4 != null) {
            for (ConfigurationFactory var6 : getFactories()) {
               if (var1 == null || var6.getVersion().equals(var1)) {
                  String[] var7 = var6.getSupportedTypes();
                  if (var7 != null) {
                     for (String var11 : var7) {
                        if (var11.equals("*") || var3.endsWith(var11)) {
                           Configuration var12 = var6.getConfiguration(var2, var4);
                           if (var12 != null) {
                              return var12;
                           }
                        }
                     }
                  }
               }
            }
         }

         return null;
      }

      private Configuration getConfiguration(LoggerContext var1, boolean var2, String var3) {
         boolean var4 = Strings.isNotEmpty(var3);
         ClassLoader var5 = LoaderUtil.getThreadContextClassLoader();

         for (ConfigurationFactory var7 : getFactories()) {
            String var9 = var2 ? var7.getTestPrefix() : var7.getDefaultPrefix();
            String[] var10 = var7.getSupportedTypes();
            if (var10 != null) {
               for (String var14 : var10) {
                  if (!var14.equals("*")) {
                     String var8 = var4 ? var9 + var3 + var14 : var9 + var14;
                     ConfigurationSource var15 = ConfigurationSource.fromResource(var8, var5);
                     if (var15 != null) {
                        if (!var7.isActive()) {
                           LOGGER.warn("Found configuration file {} for inactive ConfigurationFactory {}", var8, var7.getClass().getName());
                        }

                        return var7.getConfiguration(var1, var15);
                     }
                  }
               }
            }
         }

         return null;
      }

      @Override
      public String[] getSupportedTypes() {
         return null;
      }

      @Override
      public Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2) {
         if (var2 != null) {
            String var3 = var2.getLocation();

            for (ConfigurationFactory var5 : getFactories()) {
               String[] var6 = var5.getSupportedTypes();
               if (var6 != null) {
                  for (String var10 : var6) {
                     if (var10.equals("*") || var3 != null && var3.endsWith(var10)) {
                        Configuration var11 = var5.getConfiguration(var1, var2);
                        if (var11 != null) {
                           LOGGER.debug("Loaded configuration from {}", var2);
                           return var11;
                        }

                        LOGGER.error("Cannot determine the ConfigurationFactory to use for {}", var3);
                        return null;
                     }
                  }
               }
            }
         }

         LOGGER.error("Cannot process configuration, input source is null");
         return null;
      }

      private String[] parseConfigLocations(URI var1) {
         String[] var2 = var1.toString().split("\\?");
         ArrayList var3 = new ArrayList();
         if (var2.length <= 1) {
            return new String[]{var2[0]};
         } else {
            var3.add(var2[0]);
            String[] var4 = var1.getQuery().split("&");

            for (String var8 : var4) {
               int var9 = var8.indexOf("=");

               try {
                  String var10 = var9 > 0 ? URLDecoder.decode(var8.substring(0, var9), "UTF-8") : var8;
                  if (var10.equalsIgnoreCase("override")) {
                     var3.add(URLDecoder.decode(var8.substring(var9 + 1), "UTF-8"));
                  }
               } catch (UnsupportedEncodingException var11) {
                  LOGGER.warn("Invalid query parameter in {}", var1);
               }
            }

            return var3.toArray(Strings.EMPTY_ARRAY);
         }
      }

      private String[] parseConfigLocations(String var1) {
         String[] var2 = var1.split(",");
         if (var2.length > 1) {
            return var2;
         } else {
            try {
               return this.parseConfigLocations(new URI(var1));
            } catch (URISyntaxException var4) {
               LOGGER.warn("Error parsing URI {}", var1);
               return new String[]{var1};
            }
         }
      }
   }
}
