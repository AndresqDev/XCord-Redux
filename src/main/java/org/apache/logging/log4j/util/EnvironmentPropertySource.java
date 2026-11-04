package org.apache.logging.log4j.util;

import java.util.Map;
import java.util.Map.Entry;

public class EnvironmentPropertySource implements PropertySource {
   private static final String PREFIX = "LOG4J_";
   private static final int DEFAULT_PRIORITY = -100;

   @Override
   public int getPriority() {
      return -100;
   }

   @Override
   public void forEach(BiConsumer<String, String> var1) {
      Map var2;
      try {
         var2 = System.getenv();
      } catch (SecurityException var6) {
         LowLevelLogUtil.logException("The system environment variables are not available to Log4j due to security restrictions: " + var6, var6);
         return;
      }

      for (Entry var4 : var2.entrySet()) {
         String var5 = (String)var4.getKey();
         if (var5.startsWith("LOG4J_")) {
            var1.accept(var5.substring("LOG4J_".length()), var4.getValue());
         }
      }
   }

   @Override
   public CharSequence getNormalForm(Iterable<? extends CharSequence> var1) {
      StringBuilder var2 = new StringBuilder("LOG4J");

      for (CharSequence var4 : var1) {
         var2.append('_');

         for (int var5 = 0; var5 < var4.length(); var5++) {
            var2.append(Character.toUpperCase(var4.charAt(var5)));
         }
      }

      return var2.toString();
   }
}
