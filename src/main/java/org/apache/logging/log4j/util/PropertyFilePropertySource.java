package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyFilePropertySource extends PropertiesPropertySource {
   public PropertyFilePropertySource(String var1) {
      super(loadPropertiesFile(var1));
   }

   private static Properties loadPropertiesFile(String var0) {
      Properties var1 = new Properties();

      for (URL var3 : LoaderUtil.findResources(var0)) {
         try (InputStream var4 = var3.openStream()) {
            var1.load(var4);
         } catch (IOException var17) {
            LowLevelLogUtil.logException("Unable to read " + var3, var17);
         }
      }

      return var1;
   }

   @Override
   public int getPriority() {
      return 0;
   }
}
