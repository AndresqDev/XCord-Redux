package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public final class HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> implements HttpServerUpgradeHandler.SourceCodec {
   private final Queue<HttpMethod> queue = new ArrayDeque<>();

   public HttpServerCodec() {
      this(4096, 8192, 8192);
   }

   public HttpServerCodec(int var1, int var2, int var3) {
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4) {
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4, int var5) {
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4, var5), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4, int var5, boolean var6) {
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4, var5, var6), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4, int var5, boolean var6, boolean var7) {
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4, var5, var6, var7), new HttpServerCodec.HttpServerResponseEncoder());
   }

   @Override
   public void upgradeFrom(ChannelHandlerContext var1) {
      var1.pipeline().remove(this);
   }

   private final class HttpServerRequestDecoder extends HttpRequestDecoder {
      HttpServerRequestDecoder(int var2, int var3, int var4) {
         super(var2, var3, var4);
      }

      HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5, int var6) {
         super(var2, var3, var4, var5, var6);
      }

      HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5, int var6, boolean var7) {
         super(var2, var3, var4, var5, var6, var7);
      }

      HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5, int var6, boolean var7, boolean var8) {
         super(var2, var3, var4, var5, var6, var7, var8);
      }

      @Override
      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         int var4 = var3.size();
         super.decode(var1, var2, var3);
         int var5 = var3.size();

         for (int var6 = var4; var6 < var5; var6++) {
            Object var7 = var3.get(var6);
            if (var7 instanceof HttpRequest) {
               HttpServerCodec.this.queue.add(((HttpRequest)var7).method());
            }
         }
      }
   }

   private final class HttpServerResponseEncoder extends HttpResponseEncoder {
      private HttpMethod method;

      private HttpServerResponseEncoder() {
      }

      @Override
      protected void sanitizeHeadersBeforeEncode(HttpResponse var1, boolean var2) {
         if (!var2 && HttpMethod.CONNECT.equals(this.method) && var1.status().codeClass() == HttpStatusClass.SUCCESS) {
            var1.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
         } else {
            super.sanitizeHeadersBeforeEncode(var1, var2);
         }
      }

      @Override
      protected boolean isContentAlwaysEmpty(HttpResponse var1) {
         this.method = HttpServerCodec.this.queue.poll();
         return HttpMethod.HEAD.equals(this.method) || super.isContentAlwaysEmpty(var1);
      }
   }
}
