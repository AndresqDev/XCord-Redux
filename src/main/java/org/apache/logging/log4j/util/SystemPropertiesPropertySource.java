package org.apache.logging.log4j.util;

import java.util.Objects;
import java.util.Properties;

public class SystemPropertiesPropertySource implements PropertySource {
   private static final int DEFAULT_PRIORITY = 100;
   private static final String PREFIX = "log4j2.";

   @Override
   public int getPriority() {
      return 100;
   }

   @Override
   public void forEach(BiConsumer<String, String> var1) {
      Properties var2;
      try {
         var2 = System.getProperties();
      } catch (SecurityException var10) {
         return;
      }

      Object[] var3;
      synchronized (var2) {
         var3 = var2.keySet().toArray();
      }

      for (Object var7 : var3) {
         String var8 = Objects.toString(var7, null);
         var1.accept(var8, var2.getProperty(var8));
      }
   }

   @Override
   public CharSequence getNormalForm(Iterable<? extends CharSequence> var1) {
      return "log4j2." + PropertySource.Util.joinAsCamelCase(var1);
   }
}
