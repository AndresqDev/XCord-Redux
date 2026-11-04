package io.netty.handler.codec.http;

@Deprecated
public final class ClientCookieEncoder {
   @Deprecated
   public static String encode(String var0, String var1) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0, var1);
   }

   @Deprecated
   public static String encode(Cookie var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0);
   }

   @Deprecated
   public static String encode(Cookie... var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0);
   }

   @Deprecated
   public static String encode(Iterable<Cookie> var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0);
   }

   private ClientCookieEncoder() {
   }
}
