package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WebSocketClientExtensionHandler extends ChannelDuplexHandler {
   private final List<WebSocketClientExtensionHandshaker> extensionHandshakers;

   public WebSocketClientExtensionHandler(WebSocketClientExtensionHandshaker... var1) {
      this.extensionHandshakers = Arrays.asList(ObjectUtil.checkNonEmpty(var1, "extensionHandshakers"));
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpRequest)var2).headers())) {
         HttpRequest var4 = (HttpRequest)var2;
         String var5 = var4.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
         ArrayList var6 = new ArrayList(this.extensionHandshakers.size());

         for (WebSocketClientExtensionHandshaker var8 : this.extensionHandshakers) {
            var6.add(var8.newRequestData());
         }

         String var9 = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(var5, var6);
         var4.headers().set(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, var9);
      }

      super.write(var1, var2, var3);
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var3 = (HttpResponse)var2;
         if (WebSocketExtensionUtil.isWebsocketUpgrade(var3.headers())) {
            String var4 = var3.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (var4 != null) {
               List var5 = WebSocketExtensionUtil.extractExtensions(var4);
               ArrayList var6 = new ArrayList(var5.size());
               int var7 = 0;

               for (WebSocketExtensionData var9 : var5) {
                  Iterator var10 = this.extensionHandshakers.iterator();
                  WebSocketClientExtension var11 = null;

                  while (var11 == null && var10.hasNext()) {
                     WebSocketClientExtensionHandshaker var12 = (WebSocketClientExtensionHandshaker)var10.next();
                     var11 = var12.handshakeExtension(var9);
                  }

                  if (var11 == null || (var11.rsv() & var7) != 0) {
                     throw new CodecException("invalid WebSocket Extension handshake for \"" + var4 + '"');
                  }

                  var7 |= var11.rsv();
                  var6.add(var11);
               }

               for (WebSocketClientExtension var14 : var6) {
                  WebSocketExtensionDecoder var15 = var14.newExtensionDecoder();
                  WebSocketExtensionEncoder var16 = var14.newExtensionEncoder();
                  var1.pipeline().addAfter(var1.name(), var15.getClass().getName(), var15);
                  var1.pipeline().addAfter(var1.name(), var16.getClass().getName(), var16);
               }
            }

            var1.pipeline().remove(var1.name());
         }
      }

      super.channelRead(var1, var2);
   }
}
