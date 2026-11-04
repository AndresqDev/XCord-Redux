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
import xcord.network.DnsResolver;

public class HttpClient {
    public static final int TIMEOUT = 10000;
    private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

    public static void get(String url, EventLoop eventLoop, final Callback<String> callback) {
        Preconditions.checkNotNull(url, "url");
        Preconditions.checkNotNull(eventLoop, "eventLoop");
        Preconditions.checkNotNull(callback, "callBack");
        final URI uri = URI.create(url);
        Preconditions.checkNotNull(uri.getScheme(), "scheme");
        Preconditions.checkNotNull(uri.getHost(), "host");
        boolean ssl = uri.getScheme().equals("https");
        int port = uri.getPort();
        if (port == -1) {
            switch (uri.getScheme()) {
                case "http":
                    port = 80;
                    break;
                case "https":
                    port = 443;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
            }
        }

        ChannelFutureListener connectionListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    String path = uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
                    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                    request.headers().set(HttpHeaderNames.HOST, uri.getHost());
                    future.channel().writeAndFlush(request);
                } else {
                    HttpClient.addressCache.invalidate(uri.getHost());
                    callback.done(null, future.cause());
                }
            }
        };

        if (ProxyServer.getInstance().getConfig().isUseNettyDnsResolver()) {
            getWithNettyResolver(eventLoop, uri, port, connectionListener, callback, ssl);
        } else {
            getWithDefaultResolver(eventLoop, uri, port, connectionListener, callback, ssl);
        }
    }

    private static void getWithNettyResolver(
        EventLoop eventLoop, URI uri, int port, ChannelFutureListener listener, Callback<String> callback, boolean ssl
    ) {
        Bootstrap bootstrap = new Bootstrap()
            .channel(PipelineUtils.getChannel(null))
            .group(eventLoop)
            .handler(new HttpInitializer(callback, ssl, uri.getHost(), port))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT);
        bootstrap.resolver(DnsResolver.getResolverGroup())
            .remoteAddress(InetSocketAddress.createUnresolved(uri.getHost(), port))
            .connect()
            .addListener(listener);
    }

    private static void getWithDefaultResolver(
        EventLoop eventLoop, URI uri, int port, ChannelFutureListener listener, Callback<String> callback, boolean ssl
    ) {
        InetAddress address = addressCache.getIfPresent(uri.getHost());
        if (address == null) {
            try {
                address = InetAddress.getByName(uri.getHost());
            } catch (UnknownHostException ex) {
                callback.done(null, ex);
                return;
            }
            addressCache.put(uri.getHost(), address);
        }

        Bootstrap bootstrap = new Bootstrap()
            .channel(PipelineUtils.getChannel(null))
            .group(eventLoop)
            .handler(new HttpInitializer(callback, ssl, uri.getHost(), port))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT);
        bootstrap.remoteAddress(address, port)
            .connect()
            .addListener(listener);
    }

    private HttpClient() {
    }
}
