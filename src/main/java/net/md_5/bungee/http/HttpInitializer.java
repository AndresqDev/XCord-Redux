package net.md_5.bungee.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import net.md_5.bungee.api.Callback;

public class HttpInitializer extends ChannelInitializer<Channel> {
   private final Callback<String> callback;
   private final boolean ssl;
   private final String host;
   private final int port;

   protected void initChannel(Channel var1) throws Exception {
      var1.pipeline().addLast("timeout", new ReadTimeoutHandler(10000L, TimeUnit.MILLISECONDS));
      if (this.ssl) {
         SSLEngine var2 = SslContextBuilder.forClient().build().newEngine(var1.alloc(), this.host, this.port);
         var1.pipeline().addLast("ssl", new SslHandler(var2));
      }

      var1.pipeline().addLast("http", new HttpClientCodec());
      var1.pipeline().addLast("handler", new HttpHandler(this.callback));
   }

   public HttpInitializer(Callback<String> var1, boolean var2, String var3, int var4) {
      this.callback = var1;
      this.ssl = var2;
      this.host = var3;
      this.port = var4;
   }
}
