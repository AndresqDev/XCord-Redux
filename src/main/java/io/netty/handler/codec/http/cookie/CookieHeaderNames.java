package io.netty.handler.codec.http.cookie;

public final class CookieHeaderNames {
   public static final String PATH = "Path";
   public static final String EXPIRES = "Expires";
   public static final String MAX_AGE = "Max-Age";
   public static final String DOMAIN = "Domain";
   public static final String SECURE = "Secure";
   public static final String HTTPONLY = "HTTPOnly";
   public static final String SAMESITE = "SameSite";

   private CookieHeaderNames() {
   }

   public static enum SameSite {
      Lax,
      Strict,
      None;

      static CookieHeaderNames.SameSite of(String var0) {
         if (var0 != null) {
            for (CookieHeaderNames.SameSite var4 : CookieHeaderNames.SameSite.class.getEnumConstants()) {
               if (var4.name().equalsIgnoreCase(var0)) {
                  return var4;
               }
            }
         }

         return null;
      }
   }
}
