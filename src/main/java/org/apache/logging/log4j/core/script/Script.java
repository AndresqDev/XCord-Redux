package org.apache.logging.log4j.core.script;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;

@Plugin(
   name = "Script",
   category = "Core",
   printObject = true
)
public class Script extends AbstractScript {
   private static final String ATTR_LANGUAGE = "language";
   private static final String ATTR_SCRIPT_TEXT = "scriptText";
   static final String PLUGIN_NAME = "Script";

   public Script(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   @PluginFactory
   public static Script createScript(@PluginAttribute("name") String var0, @PluginAttribute("language") String var1, @PluginValue("scriptText") String var2) {
      if (var1 == null) {
         LOGGER.error("No '{}' attribute provided for {} plugin '{}'", "language", "Script", var0);
         var1 = "JavaScript";
      }

      if (var2 == null) {
         LOGGER.error("No '{}' attribute provided for {} plugin '{}'", "scriptText", "Script", var0);
         return null;
      } else {
         return new Script(var0, var1, var2);
      }
   }

   @Override
   public String toString() {
      return this.getName() != null ? this.getName() : super.toString();
   }
}
