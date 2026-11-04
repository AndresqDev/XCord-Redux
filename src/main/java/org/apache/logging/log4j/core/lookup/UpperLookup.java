package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "upper",
   category = "Lookup"
)
public class UpperLookup implements StrLookup {
   @Override
   public String lookup(String var1) {
      return var1 != null ? var1.toUpperCase() : null;
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      return this.lookup(var2);
   }
}
