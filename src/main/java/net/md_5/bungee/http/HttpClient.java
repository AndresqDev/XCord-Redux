package net.md_5.bungee.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.netty.PipelineUtils;
import xcord.DnsResolver;

public class HttpClient {
   public static final int TIMEOUT = 10000;
   private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

   public static void get(String var0, EventLoop var1, final Callback<String> var2) {
      Preconditions.checkNotNull(var0, "url");
      Preconditions.checkNotNull(var1, "eventLoop");
      Preconditions.checkNotNull(var2, "callBack");
      final URI var3 = URI.create(var0);
      Preconditions.checkNotNull(var3.getScheme(), "scheme");
      Preconditions.checkNotNull(var3.getHost(), "host");
      boolean var4 = var3.getScheme().equals("https");
      int var5 = var3.getPort();
      if (var5 == -1) {
         String var6 = var3.getScheme();
         switch (var6) {
            case "http":
               var5 = 80;
               break;
            case "https":
               var5 = 443;
               break;
            default:
               throw new IllegalArgumentException("Unknown scheme " + var3.getScheme());
         }
      }

      ChannelFutureListener var8 = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            if (var1.isSuccess()) {
               String var2x = var3.getRawPath() + (var3.getRawQuery() == null ? "" : "?" + var3.getRawQuery());
               DefaultHttpRequest var3x = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, var2x);
               var3x.headers().set(HttpHeaderNames.HOST, var3.getHost());
               var1.channel().writeAndFlush(var3x);
            } else {
               HttpClient.addressCache.invalidate(var3.getHost());
               var2.done(null, var1.cause());
            }
         }
      };
      if (ProxyServer.getInstance().getConfig().isUseNettyDnsResolver()) {
         getWithNettyResolver(var1, var3, var5, var8, var2, var4);
      } else {
         getWithDefaultResolver(var1, var3, var5, var8, var2, var4);
      }
   }

   private static void getWithNettyResolver(EventLoop var0, URI var1, int var2, ChannelFutureListener var3, Callback<String> var4, boolean var5) {
      InetSocketAddress var6 = InetSocketAddress.createUnresolved(var1.getHost(), var2);
      ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(PipelineUtils.getChannel(null))).group(var0))
               .handler(new HttpInitializer(var4, var5, var1.getHost(), var2)))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000))
         .resolver(DnsResolver.getResolverGroup())
         .remoteAddress(var6)
         .connect()
         .addListener(var3);
   }

   private static void getWithDefaultResolver(EventLoop var0, URI var1, int var2, ChannelFutureListener var3, Callback<String> var4, boolean var5) {
      InetAddress var6 = (InetAddress)addressCache.getIfPresent(var1.getHost());
      if (var6 == null) {
         try {
            var6 = InetAddress.getByName(var1.getHost());
         } catch (UnknownHostException var8) {
            var4.done(null, var8);
            return;
         }

         addressCache.put(var1.getHost(), var6);
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(PipelineUtils.getChannel(null))).group(var0))
               .handler(new HttpInitializer(var4, var5, var1.getHost(), var2)))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000))
         .remoteAddress(var6, var2)
         .connect()
         .addListener(var3);
   }

   private HttpClient() {
   }
}
