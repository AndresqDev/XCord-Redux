package io.netty.util.internal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class ResourcesUtil {
   public static File getFile(Class var0, String var1) {
      try {
         return new File(URLDecoder.decode(var0.getResource(var1).getFile(), "UTF-8"));
      } catch (UnsupportedEncodingException var3) {
         return new File(var0.getResource(var1).getFile());
      }
   }

   private ResourcesUtil() {
   }
}
