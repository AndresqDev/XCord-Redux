package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public final class Streams {
   private static final boolean USE_CACHES;

   static {
      boolean var0;
      try {
         String var1 = System.getProperty("sisu.url.caches");
         if (var1 != null && !var1.isEmpty()) {
            var0 = Boolean.parseBoolean(var1);
         } else {
            String var2 = System.getProperty("os.name").toLowerCase(Locale.US);
            var0 = !var2.contains("windows");
         }
      } catch (RuntimeException var3) {
         var0 = true;
      }

      USE_CACHES = var0;
   }

   private Streams() {
   }

   public static InputStream open(URL var0) throws IOException {
      if (USE_CACHES) {
         return var0.openStream();
      } else {
         URLConnection var1 = var0.openConnection();
         var1.setUseCaches(false);
         return var1.getInputStream();
      }
   }
}
