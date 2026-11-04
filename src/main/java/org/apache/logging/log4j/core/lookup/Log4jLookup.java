package org.apache.logging.log4j.core.lookup;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "log4j",
   category = "Lookup"
)
public class Log4jLookup extends AbstractConfigurationAwareLookup {
   public static final String KEY_CONFIG_LOCATION = "configLocation";
   public static final String KEY_CONFIG_PARENT_LOCATION = "configParentLocation";
   private static final Logger LOGGER = StatusLogger.getLogger();

   private static String asPath(URI var0) {
      return var0.getScheme() != null && !var0.getScheme().equals("file") ? var0.toString() : var0.getPath();
   }

   private static URI getParent(URI var0) throws URISyntaxException {
      String var1 = var0.toString();
      int var2 = var1.lastIndexOf(47);
      return var2 > -1 ? new URI(var1.substring(0, var2)) : new URI("../");
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      if (this.configuration != null) {
         ConfigurationSource var3 = this.configuration.getConfigurationSource();
         File var4 = var3.getFile();
         if (var4 != null) {
            switch (var2) {
               case "configLocation":
                  return var4.getAbsolutePath();
               case "configParentLocation":
                  return var4.getParentFile().getAbsolutePath();
               default:
                  return null;
            }
         }

         URL var5 = var3.getURL();
         if (var5 != null) {
            try {
               switch (var2) {
                  case "configLocation":
                     return asPath(var5.toURI());
                  case "configParentLocation":
                     return asPath(getParent(var5.toURI()));
                  default:
                     return null;
               }
            } catch (URISyntaxException var8) {
               LOGGER.error(var8);
            }
         }
      }

      return null;
   }
}
