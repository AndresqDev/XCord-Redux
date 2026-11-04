package org.apache.logging.log4j.util;

import java.util.Properties;
import java.util.Map.Entry;

public class PropertiesPropertySource implements PropertySource {
   private static final String PREFIX = "log4j2.";
   private final Properties properties;

   public PropertiesPropertySource(Properties var1) {
      this.properties = var1;
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public void forEach(BiConsumer<String, String> var1) {
      for (Entry var3 : this.properties.entrySet()) {
         var1.accept((String)var3.getKey(), (String)var3.getValue());
      }
   }

   @Override
   public CharSequence getNormalForm(Iterable<? extends CharSequence> var1) {
      return "log4j2." + PropertySource.Util.joinAsCamelCase(var1);
   }
}
