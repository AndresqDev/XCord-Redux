package org.apache.logging.log4j.core.lookup;

import java.util.Collections;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;

public final class PropertiesLookup implements StrLookup {
   private final Map<String, String> properties;

   public PropertiesLookup(Map<String, String> var1) {
      this.properties = var1 == null ? Collections.emptyMap() : var1;
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      return this.lookup(var2);
   }

   @Override
   public String lookup(String var1) {
      return var1 == null ? null : this.properties.get(var1);
   }

   @Override
   public String toString() {
      return "PropertiesLookup{properties=" + this.properties + '}';
   }
}
