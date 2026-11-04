package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WebSocketServerExtensionHandler extends ChannelDuplexHandler {
   private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
   private List<WebSocketServerExtension> validExtensions;

   public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker... var1) {
      this.extensionHandshakers = Arrays.asList(ObjectUtil.checkNonEmpty(var1, "extensionHandshakers"));
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         HttpRequest var3 = (HttpRequest)var2;
         if (WebSocketExtensionUtil.isWebsocketUpgrade(var3.headers())) {
            String var4 = var3.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (var4 != null) {
               List var5 = WebSocketExtensionUtil.extractExtensions(var4);
               int var6 = 0;

               for (WebSocketExtensionData var8 : var5) {
                  Iterator var9 = this.extensionHandshakers.iterator();
                  WebSocketServerExtension var10 = null;

                  while (var10 == null && var9.hasNext()) {
                     WebSocketServerExtensionHandshaker var11 = (WebSocketServerExtensionHandshaker)var9.next();
                     var10 = var11.handshakeExtension(var8);
                  }

                  if (var10 != null && (var10.rsv() & var6) == 0) {
                     if (this.validExtensions == null) {
                        this.validExtensions = new ArrayList<>(1);
                     }

                     var6 |= var10.rsv();
                     this.validExtensions.add(var10);
                  }
               }
            }
         }
      }

      super.channelRead(var1, var2);
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var4 = (HttpResponse)var2;
         if (HttpResponseStatus.SWITCHING_PROTOCOLS.equals(var4.status())) {
            this.handlePotentialUpgrade(var1, var3, var4);
         }
      }

      super.write(var1, var2, var3);
   }

   private void handlePotentialUpgrade(final ChannelHandlerContext var1, ChannelPromise var2, HttpResponse var3) {
      HttpHeaders var4 = var3.headers();
      if (WebSocketExtensionUtil.isWebsocketUpgrade(var4)) {
         if (this.validExtensions != null) {
            String var5 = var4.getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            ArrayList var6 = new ArrayList(this.extensionHandshakers.size());

            for (WebSocketServerExtension var8 : this.validExtensions) {
               var6.add(var8.newReponseData());
            }

            String var9 = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(var5, var6);
            var2.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) {
                  if (var1x.isSuccess()) {
                     for (WebSocketServerExtension var3 : WebSocketServerExtensionHandler.this.validExtensions) {
                        WebSocketExtensionDecoder var4 = var3.newExtensionDecoder();
                        WebSocketExtensionEncoder var5 = var3.newExtensionEncoder();
                        String var6 = var1.name();
                        var1.pipeline().addAfter(var6, var4.getClass().getName(), var4).addAfter(var6, var5.getClass().getName(), var5);
                     }
                  }
               }
            });
            if (var9 != null) {
               var4.set(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, var9);
            }
         }

         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) {
               if (var1x.isSuccess()) {
                  var1.pipeline().remove(WebSocketServerExtensionHandler.this);
               }
            }
         });
      }
   }
}
