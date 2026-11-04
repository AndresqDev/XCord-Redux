package net.md_5.bungee.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.Charset;
import net.md_5.bungee.api.Callback;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
   private final Callback<String> callback;
   private final StringBuilder buffer = new StringBuilder(640);

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      try {
         this.callback.done(null, var2);
      } finally {
         var1.channel().pipeline().remove(this);
         var1.channel().close();
      }
   }

   protected void channelRead0(ChannelHandlerContext var1, HttpObject var2) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var3 = (HttpResponse)var2;
         int var4 = var3.status().code();
         if (var4 == HttpResponseStatus.NO_CONTENT.code()) {
            this.done(var1);
            return;
         }

         if (var4 != HttpResponseStatus.OK.code()) {
            throw new IllegalStateException("Expected HTTP response 200 OK, got " + var3.status());
         }
      }

      if (var2 instanceof HttpContent) {
         HttpContent var5 = (HttpContent)var2;
         this.buffer.append(var5.content().toString(Charset.forName("UTF-8")));
         if (var2 instanceof LastHttpContent) {
            this.done(var1);
         }
      }
   }

   private void done(ChannelHandlerContext var1) {
      try {
         this.callback.done(this.buffer.toString(), null);
      } finally {
         var1.channel().pipeline().remove(this);
         var1.channel().close();
      }
   }

   public HttpHandler(Callback<String> var1) {
      this.callback = var1;
   }
}
