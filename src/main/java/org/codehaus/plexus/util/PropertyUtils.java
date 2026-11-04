package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyUtils {
   public static Properties loadProperties(URL var0) throws IOException {
      if (var0 == null) {
         throw new NullPointerException("url");
      } else {
         return loadProperties(var0.openStream());
      }
   }

   public static Properties loadProperties(File var0) throws IOException {
      if (var0 == null) {
         throw new NullPointerException("file");
      } else {
         return loadProperties(new FileInputStream(var0));
      }
   }

   public static Properties loadProperties(InputStream var0) throws IOException {
      InputStream var1 = var0;

      Properties var3;
      try {
         Properties var2 = new Properties();
         if (var1 != null) {
            var2.load(var1);
            var1.close();
            var1 = null;
         }

         var3 = var2;
      } finally {
         IOUtil.close(var1);
      }

      return var3;
   }
}
