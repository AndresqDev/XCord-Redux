package org.apache.logging.log4j.core.config.json;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Loader;

@Plugin(
   name = "JsonConfigurationFactory",
   category = "ConfigurationFactory"
)
@Order(6)
public class JsonConfigurationFactory extends ConfigurationFactory {
   private static final String[] SUFFIXES = new String[]{".json", ".jsn"};
   private static final String[] dependencies = new String[]{
      "com.fasterxml.jackson.databind.ObjectMapper", "com.fasterxml.jackson.databind.JsonNode", "com.fasterxml.jackson.core.JsonParser"
   };
   private final boolean isActive;

   public JsonConfigurationFactory() {
      for (String var4 : dependencies) {
         if (!Loader.isClassAvailable(var4)) {
            LOGGER.debug("Missing dependencies for Json support, ConfigurationFactory {} is inactive", this.getClass().getName());
            this.isActive = false;
            return;
         }
      }

      this.isActive = true;
   }

   @Override
   protected boolean isActive() {
      return this.isActive;
   }

   @Override
   public Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2) {
      return !this.isActive ? null : new JsonConfiguration(var1, var2);
   }

   @Override
   public String[] getSupportedTypes() {
      return SUFFIXES;
   }
}
