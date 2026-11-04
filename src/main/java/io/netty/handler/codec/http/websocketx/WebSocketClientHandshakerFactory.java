package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;

public final class WebSocketClientHandshakerFactory {
   private WebSocketClientHandshakerFactory() {
   }

   public static WebSocketClientHandshaker newHandshaker(URI var0, WebSocketVersion var1, String var2, boolean var3, HttpHeaders var4) {
      return newHandshaker(var0, var1, var2, var3, var4, 65536);
   }

   public static WebSocketClientHandshaker newHandshaker(URI var0, WebSocketVersion var1, String var2, boolean var3, HttpHeaders var4, int var5) {
      return newHandshaker(var0, var1, var2, var3, var4, var5, true, false);
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI var0, WebSocketVersion var1, String var2, boolean var3, HttpHeaders var4, int var5, boolean var6, boolean var7
   ) {
      return newHandshaker(var0, var1, var2, var3, var4, var5, var6, var7, -1L);
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI var0, WebSocketVersion var1, String var2, boolean var3, HttpHeaders var4, int var5, boolean var6, boolean var7, long var8
   ) {
      if (var1 == WebSocketVersion.V13) {
         return new WebSocketClientHandshaker13(var0, WebSocketVersion.V13, var2, var3, var4, var5, var6, var7, var8);
      } else if (var1 == WebSocketVersion.V08) {
         return new WebSocketClientHandshaker08(var0, WebSocketVersion.V08, var2, var3, var4, var5, var6, var7, var8);
      } else if (var1 == WebSocketVersion.V07) {
         return new WebSocketClientHandshaker07(var0, WebSocketVersion.V07, var2, var3, var4, var5, var6, var7, var8);
      } else if (var1 == WebSocketVersion.V00) {
         return new WebSocketClientHandshaker00(var0, WebSocketVersion.V00, var2, var4, var5, var8);
      } else {
         throw new WebSocketClientHandshakeException("Protocol version " + var1 + " not supported.");
      }
   }

   public static WebSocketClientHandshaker newHandshaker(
      URI var0, WebSocketVersion var1, String var2, boolean var3, HttpHeaders var4, int var5, boolean var6, boolean var7, long var8, boolean var10
   ) {
      if (var1 == WebSocketVersion.V13) {
         return new WebSocketClientHandshaker13(var0, WebSocketVersion.V13, var2, var3, var4, var5, var6, var7, var8, var10);
      } else if (var1 == WebSocketVersion.V08) {
         return new WebSocketClientHandshaker08(var0, WebSocketVersion.V08, var2, var3, var4, var5, var6, var7, var8, var10);
      } else if (var1 == WebSocketVersion.V07) {
         return new WebSocketClientHandshaker07(var0, WebSocketVersion.V07, var2, var3, var4, var5, var6, var7, var8, var10);
      } else if (var1 == WebSocketVersion.V00) {
         return new WebSocketClientHandshaker00(var0, WebSocketVersion.V00, var2, var4, var5, var8, var10);
      } else {
         throw new WebSocketClientHandshakeException("Protocol version " + var1 + " not supported.");
      }
   }
}
