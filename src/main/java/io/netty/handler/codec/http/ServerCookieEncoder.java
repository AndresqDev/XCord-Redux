package io.netty.handler.codec.http;

import java.util.Collection;
import java.util.List;

@Deprecated
public final class ServerCookieEncoder {
   @Deprecated
   public static String encode(String var0, String var1) {
      return io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(var0, var1);
   }

   @Deprecated
   public static String encode(Cookie var0) {
      return io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(var0);
   }

   @Deprecated
   public static List<String> encode(Cookie... var0) {
      return io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(var0);
   }

   @Deprecated
   public static List<String> encode(Collection<Cookie> var0) {
      return io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(var0);
   }

   @Deprecated
   public static List<String> encode(Iterable<Cookie> var0) {
      return io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(var0);
   }

   private ServerCookieEncoder() {
   }
}
