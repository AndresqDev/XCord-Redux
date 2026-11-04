package org.eclipse.aether.transport.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.utils.URIUtils;

final class UriUtils {
   public static URI resolve(URI var0, URI var1) {
      String var2 = var1.getRawPath();
      if (var2 != null && var2.length() > 0) {
         var2 = var0.getRawPath();
         if (var2 == null || !var2.endsWith("/")) {
            try {
               var0 = new URI(var0.getScheme(), var0.getAuthority(), var0.getPath() + '/', null, null);
            } catch (URISyntaxException var4) {
               throw new IllegalStateException(var4);
            }
         }
      }

      return URIUtils.resolve(var0, var1);
   }

   public static List<URI> getDirectories(URI var0, URI var1) {
      ArrayList var2 = new ArrayList();

      for (URI var3 = var1.resolve("."); !isBase(var0, var3); var3 = var3.resolve("..")) {
         var2.add(var3);
      }

      return var2;
   }

   private static boolean isBase(URI var0, URI var1) {
      String var2 = var1.getRawPath();
      if (var2 != null && !"/".equals(var2)) {
         if (var0 != null) {
            URI var3 = var0.relativize(var1);
            if (var3.getRawPath() == null || var3.getRawPath().isEmpty() || var3.equals(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
