package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.execchain.BackoffStrategyExec;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.execchain.ProtocolExec;
import org.apache.http.impl.execchain.RedirectExec;
import org.apache.http.impl.execchain.RetryExec;
import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

public class HttpClientBuilder {
   private HttpRequestExecutor requestExec;
   private HostnameVerifier hostnameVerifier;
   private LayeredConnectionSocketFactory sslSocketFactory;
   private SSLContext sslContext;
   private HttpClientConnectionManager connManager;
   private boolean connManagerShared;
   private SchemePortResolver schemePortResolver;
   private ConnectionReuseStrategy reuseStrategy;
   private ConnectionKeepAliveStrategy keepAliveStrategy;
   private AuthenticationStrategy targetAuthStrategy;
   private AuthenticationStrategy proxyAuthStrategy;
   private UserTokenHandler userTokenHandler;
   private HttpProcessor httpprocessor;
   private DnsResolver dnsResolver;
   private LinkedList<HttpRequestInterceptor> requestFirst;
   private LinkedList<HttpRequestInterceptor> requestLast;
   private LinkedList<HttpResponseInterceptor> responseFirst;
   private LinkedList<HttpResponseInterceptor> responseLast;
   private HttpRequestRetryHandler retryHandler;
   private HttpRoutePlanner routePlanner;
   private RedirectStrategy redirectStrategy;
   private ConnectionBackoffStrategy connectionBackoffStrategy;
   private BackoffManager backoffManager;
   private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
   private Lookup<AuthSchemeProvider> authSchemeRegistry;
   private Lookup<CookieSpecProvider> cookieSpecRegistry;
   private Map<String, InputStreamFactory> contentDecoderMap;
   private CookieStore cookieStore;
   private CredentialsProvider credentialsProvider;
   private String userAgent;
   private HttpHost proxy;
   private Collection<? extends Header> defaultHeaders;
   private SocketConfig defaultSocketConfig;
   private ConnectionConfig defaultConnectionConfig;
   private RequestConfig defaultRequestConfig;
   private boolean evictExpiredConnections;
   private boolean evictIdleConnections;
   private long maxIdleTime;
   private TimeUnit maxIdleTimeUnit;
   private boolean systemProperties;
   private boolean redirectHandlingDisabled;
   private boolean automaticRetriesDisabled;
   private boolean contentCompressionDisabled;
   private boolean cookieManagementDisabled;
   private boolean authCachingDisabled;
   private boolean connectionStateDisabled;
   private boolean defaultUserAgentDisabled;
   private int maxConnTotal = 0;
   private int maxConnPerRoute = 0;
   private long connTimeToLive = -1L;
   private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;
   private List<Closeable> closeables;
   private PublicSuffixMatcher publicSuffixMatcher;

   public static HttpClientBuilder create() {
      return new HttpClientBuilder();
   }

   protected HttpClientBuilder() {
   }

   public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor var1) {
      this.requestExec = var1;
      return this;
   }

   @Deprecated
   public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier var1) {
      this.hostnameVerifier = var1;
      return this;
   }

   public final HttpClientBuilder setSSLHostnameVerifier(HostnameVerifier var1) {
      this.hostnameVerifier = var1;
      return this;
   }

   public final HttpClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher var1) {
      this.publicSuffixMatcher = var1;
      return this;
   }

   @Deprecated
   public final HttpClientBuilder setSslcontext(SSLContext var1) {
      return this.setSSLContext(var1);
   }

   public final HttpClientBuilder setSSLContext(SSLContext var1) {
      this.sslContext = var1;
      return this;
   }

   public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory var1) {
      this.sslSocketFactory = var1;
      return this;
   }

   public final HttpClientBuilder setMaxConnTotal(int var1) {
      this.maxConnTotal = var1;
      return this;
   }

   public final HttpClientBuilder setMaxConnPerRoute(int var1) {
      this.maxConnPerRoute = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultSocketConfig(SocketConfig var1) {
      this.defaultSocketConfig = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig var1) {
      this.defaultConnectionConfig = var1;
      return this;
   }

   public final HttpClientBuilder setConnectionTimeToLive(long var1, TimeUnit var3) {
      this.connTimeToLive = var1;
      this.connTimeToLiveTimeUnit = var3;
      return this;
   }

   public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager var1) {
      this.connManager = var1;
      return this;
   }

   public final HttpClientBuilder setConnectionManagerShared(boolean var1) {
      this.connManagerShared = var1;
      return this;
   }

   public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy var1) {
      this.reuseStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy var1) {
      this.keepAliveStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy var1) {
      this.targetAuthStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy var1) {
      this.proxyAuthStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setUserTokenHandler(UserTokenHandler var1) {
      this.userTokenHandler = var1;
      return this;
   }

   public final HttpClientBuilder disableConnectionState() {
      this.connectionStateDisabled = true;
      return this;
   }

   public final HttpClientBuilder setSchemePortResolver(SchemePortResolver var1) {
      this.schemePortResolver = var1;
      return this;
   }

   public final HttpClientBuilder setUserAgent(String var1) {
      this.userAgent = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> var1) {
      this.defaultHeaders = var1;
      return this;
   }

   public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.responseFirst == null) {
            this.responseFirst = new LinkedList<>();
         }

         this.responseFirst.addFirst(var1);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.responseLast == null) {
            this.responseLast = new LinkedList<>();
         }

         this.responseLast.addLast(var1);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.requestFirst == null) {
            this.requestFirst = new LinkedList<>();
         }

         this.requestFirst.addFirst(var1);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.requestLast == null) {
            this.requestLast = new LinkedList<>();
         }

         this.requestLast.addLast(var1);
         return this;
      }
   }

   public final HttpClientBuilder disableCookieManagement() {
      this.cookieManagementDisabled = true;
      return this;
   }

   public final HttpClientBuilder disableContentCompression() {
      this.contentCompressionDisabled = true;
      return this;
   }

   public final HttpClientBuilder disableAuthCaching() {
      this.authCachingDisabled = true;
      return this;
   }

   public final HttpClientBuilder setHttpProcessor(HttpProcessor var1) {
      this.httpprocessor = var1;
      return this;
   }

   public final HttpClientBuilder setDnsResolver(DnsResolver var1) {
      this.dnsResolver = var1;
      return this;
   }

   public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler var1) {
      this.retryHandler = var1;
      return this;
   }

   public final HttpClientBuilder disableAutomaticRetries() {
      this.automaticRetriesDisabled = true;
      return this;
   }

   public final HttpClientBuilder setProxy(HttpHost var1) {
      this.proxy = var1;
      return this;
   }

   public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner var1) {
      this.routePlanner = var1;
      return this;
   }

   public final HttpClientBuilder setRedirectStrategy(RedirectStrategy var1) {
      this.redirectStrategy = var1;
      return this;
   }

   public final HttpClientBuilder disableRedirectHandling() {
      this.redirectHandlingDisabled = true;
      return this;
   }

   public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy var1) {
      this.connectionBackoffStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setBackoffManager(BackoffManager var1) {
      this.backoffManager = var1;
      return this;
   }

   public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy var1) {
      this.serviceUnavailStrategy = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultCookieStore(CookieStore var1) {
      this.cookieStore = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider var1) {
      this.credentialsProvider = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> var1) {
      this.authSchemeRegistry = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> var1) {
      this.cookieSpecRegistry = var1;
      return this;
   }

   public final HttpClientBuilder setContentDecoderRegistry(Map<String, InputStreamFactory> var1) {
      this.contentDecoderMap = var1;
      return this;
   }

   public final HttpClientBuilder setDefaultRequestConfig(RequestConfig var1) {
      this.defaultRequestConfig = var1;
      return this;
   }

   public final HttpClientBuilder useSystemProperties() {
      this.systemProperties = true;
      return this;
   }

   public final HttpClientBuilder evictExpiredConnections() {
      this.evictExpiredConnections = true;
      return this;
   }

   @Deprecated
   public final HttpClientBuilder evictIdleConnections(Long var1, TimeUnit var2) {
      return this.evictIdleConnections(var1.longValue(), var2);
   }

   public final HttpClientBuilder evictIdleConnections(long var1, TimeUnit var3) {
      this.evictIdleConnections = true;
      this.maxIdleTime = var1;
      this.maxIdleTimeUnit = var3;
      return this;
   }

   public final HttpClientBuilder disableDefaultUserAgent() {
      this.defaultUserAgentDisabled = true;
      return this;
   }

   protected ClientExecChain createMainExec(
      HttpRequestExecutor var1,
      HttpClientConnectionManager var2,
      ConnectionReuseStrategy var3,
      ConnectionKeepAliveStrategy var4,
      HttpProcessor var5,
      AuthenticationStrategy var6,
      AuthenticationStrategy var7,
      UserTokenHandler var8
   ) {
      return new MainClientExec(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected ClientExecChain decorateMainExec(ClientExecChain var1) {
      return var1;
   }

   protected ClientExecChain decorateProtocolExec(ClientExecChain var1) {
      return var1;
   }

   protected void addCloseable(Closeable var1) {
      if (var1 != null) {
         if (this.closeables == null) {
            this.closeables = new ArrayList<>();
         }

         this.closeables.add(var1);
      }
   }

   private static String[] split(String var0) {
      return TextUtils.isBlank(var0) ? null : var0.split(" *, *");
   }

   public CloseableHttpClient build() {
      PublicSuffixMatcher var1 = this.publicSuffixMatcher;
      if (var1 == null) {
         var1 = PublicSuffixMatcherLoader.getDefault();
      }

      HttpRequestExecutor var2 = this.requestExec;
      if (var2 == null) {
         var2 = new HttpRequestExecutor();
      }

      final Object var3 = this.connManager;
      if (var3 == null) {
         Object var4 = this.sslSocketFactory;
         if (var4 == null) {
            String[] var5 = this.systemProperties ? split(System.getProperty("https.protocols")) : null;
            String[] var6 = this.systemProperties ? split(System.getProperty("https.cipherSuites")) : null;
            Object var7 = this.hostnameVerifier;
            if (var7 == null) {
               var7 = new DefaultHostnameVerifier(var1);
            }

            if (this.sslContext != null) {
               var4 = new SSLConnectionSocketFactory(this.sslContext, var5, var6, (HostnameVerifier)var7);
            } else if (this.systemProperties) {
               var4 = new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), var5, var6, (HostnameVerifier)var7);
            } else {
               var4 = new SSLConnectionSocketFactory(SSLContexts.createDefault(), (HostnameVerifier)var7);
            }
         }

         PoolingHttpClientConnectionManager var22 = new PoolingHttpClientConnectionManager(
            RegistryBuilder.<ConnectionSocketFactory>create()
               .register("http", PlainConnectionSocketFactory.getSocketFactory())
               .register("https", (ConnectionSocketFactory)var4)
               .build(),
            null,
            null,
            this.dnsResolver,
            this.connTimeToLive,
            this.connTimeToLiveTimeUnit != null ? this.connTimeToLiveTimeUnit : TimeUnit.MILLISECONDS
         );
         if (this.defaultSocketConfig != null) {
            var22.setDefaultSocketConfig(this.defaultSocketConfig);
         }

         if (this.defaultConnectionConfig != null) {
            var22.setDefaultConnectionConfig(this.defaultConnectionConfig);
         }

         if (this.systemProperties) {
            String var25 = System.getProperty("http.keepAlive", "true");
            if ("true".equalsIgnoreCase(var25)) {
               var25 = System.getProperty("http.maxConnections", "5");
               int var28 = Integer.parseInt(var25);
               var22.setDefaultMaxPerRoute(var28);
               var22.setMaxTotal(2 * var28);
            }
         }

         if (this.maxConnTotal > 0) {
            var22.setMaxTotal(this.maxConnTotal);
         }

         if (this.maxConnPerRoute > 0) {
            var22.setDefaultMaxPerRoute(this.maxConnPerRoute);
         }

         var3 = var22;
      }

      Object var21 = this.reuseStrategy;
      if (var21 == null) {
         if (this.systemProperties) {
            String var23 = System.getProperty("http.keepAlive", "true");
            if ("true".equalsIgnoreCase(var23)) {
               var21 = DefaultClientConnectionReuseStrategy.INSTANCE;
            } else {
               var21 = NoConnectionReuseStrategy.INSTANCE;
            }
         } else {
            var21 = DefaultClientConnectionReuseStrategy.INSTANCE;
         }
      }

      Object var24 = this.keepAliveStrategy;
      if (var24 == null) {
         var24 = DefaultConnectionKeepAliveStrategy.INSTANCE;
      }

      Object var27 = this.targetAuthStrategy;
      if (var27 == null) {
         var27 = TargetAuthenticationStrategy.INSTANCE;
      }

      Object var29 = this.proxyAuthStrategy;
      if (var29 == null) {
         var29 = ProxyAuthenticationStrategy.INSTANCE;
      }

      Object var8 = this.userTokenHandler;
      if (var8 == null) {
         if (!this.connectionStateDisabled) {
            var8 = DefaultUserTokenHandler.INSTANCE;
         } else {
            var8 = NoopUserTokenHandler.INSTANCE;
         }
      }

      String var9 = this.userAgent;
      if (var9 == null) {
         if (this.systemProperties) {
            var9 = System.getProperty("http.agent");
         }

         if (var9 == null && !this.defaultUserAgentDisabled) {
            var9 = VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", this.getClass());
         }
      }

      ClientExecChain var10 = this.createMainExec(
         var2,
         (HttpClientConnectionManager)var3,
         (ConnectionReuseStrategy)var21,
         (ConnectionKeepAliveStrategy)var24,
         new ImmutableHttpProcessor(new RequestTargetHost(), new RequestUserAgent(var9)),
         (AuthenticationStrategy)var27,
         (AuthenticationStrategy)var29,
         (UserTokenHandler)var8
      );
      var10 = this.decorateMainExec(var10);
      HttpProcessor var11 = this.httpprocessor;
      if (var11 == null) {
         HttpProcessorBuilder var12 = HttpProcessorBuilder.create();
         if (this.requestFirst != null) {
            for (HttpRequestInterceptor var14 : this.requestFirst) {
               var12.addFirst(var14);
            }
         }

         if (this.responseFirst != null) {
            for (HttpResponseInterceptor var42 : this.responseFirst) {
               var12.addFirst(var42);
            }
         }

         var12.addAll(
            new RequestDefaultHeaders(this.defaultHeaders),
            new RequestContent(),
            new RequestTargetHost(),
            new RequestClientConnControl(),
            new RequestUserAgent(var9),
            new RequestExpectContinue()
         );
         if (!this.cookieManagementDisabled) {
            var12.add(new RequestAddCookies());
         }

         if (!this.contentCompressionDisabled) {
            if (this.contentDecoderMap != null) {
               ArrayList var36 = new ArrayList<>(this.contentDecoderMap.keySet());
               Collections.sort(var36);
               var12.add(new RequestAcceptEncoding(var36));
            } else {
               var12.add(new RequestAcceptEncoding());
            }
         }

         if (!this.authCachingDisabled) {
            var12.add(new RequestAuthCache());
         }

         if (!this.cookieManagementDisabled) {
            var12.add(new ResponseProcessCookies());
         }

         if (!this.contentCompressionDisabled) {
            if (this.contentDecoderMap == null) {
               var12.add(new ResponseContentEncoding());
            } else {
               RegistryBuilder var37 = RegistryBuilder.create();

               for (Entry var15 : this.contentDecoderMap.entrySet()) {
                  var37.register((String)var15.getKey(), var15.getValue());
               }

               var12.add(new ResponseContentEncoding(var37.build()));
            }
         }

         if (this.requestLast != null) {
            for (HttpRequestInterceptor var44 : this.requestLast) {
               var12.addLast(var44);
            }
         }

         if (this.responseLast != null) {
            for (HttpResponseInterceptor var45 : this.responseLast) {
               var12.addLast(var45);
            }
         }

         var11 = var12.build();
      }

      ProtocolExec var31 = new ProtocolExec(var10, var11);
      var10 = this.decorateProtocolExec(var31);
      if (!this.automaticRetriesDisabled) {
         Object var33 = this.retryHandler;
         if (var33 == null) {
            var33 = DefaultHttpRequestRetryHandler.INSTANCE;
         }

         var10 = new RetryExec(var10, (HttpRequestRetryHandler)var33);
      }

      Object var34 = this.routePlanner;
      if (var34 == null) {
         Object var40 = this.schemePortResolver;
         if (var40 == null) {
            var40 = DefaultSchemePortResolver.INSTANCE;
         }

         if (this.proxy != null) {
            var34 = new DefaultProxyRoutePlanner(this.proxy, (SchemePortResolver)var40);
         } else if (this.systemProperties) {
            var34 = new SystemDefaultRoutePlanner((SchemePortResolver)var40, ProxySelector.getDefault());
         } else {
            var34 = new DefaultRoutePlanner((SchemePortResolver)var40);
         }
      }

      ServiceUnavailableRetryStrategy var41 = this.serviceUnavailStrategy;
      if (var41 != null) {
         var10 = new ServiceUnavailableRetryExec(var10, var41);
      }

      if (!this.redirectHandlingDisabled) {
         Object var46 = this.redirectStrategy;
         if (var46 == null) {
            var46 = DefaultRedirectStrategy.INSTANCE;
         }

         var10 = new RedirectExec(var10, (HttpRoutePlanner)var34, (RedirectStrategy)var46);
      }

      if (this.backoffManager != null && this.connectionBackoffStrategy != null) {
         var10 = new BackoffStrategyExec(var10, this.connectionBackoffStrategy, this.backoffManager);
      }

      Object var47 = this.authSchemeRegistry;
      if (var47 == null) {
         var47 = RegistryBuilder.<BasicSchemeFactory>create()
            .register("Basic", new BasicSchemeFactory())
            .register("Digest", new DigestSchemeFactory())
            .register("NTLM", new NTLMSchemeFactory())
            .register("Negotiate", new SPNegoSchemeFactory())
            .register("Kerberos", new KerberosSchemeFactory())
            .build();
      }

      Lookup var48 = this.cookieSpecRegistry;
      if (var48 == null) {
         var48 = CookieSpecRegistries.createDefault(var1);
      }

      Object var16 = this.cookieStore;
      if (var16 == null) {
         var16 = new BasicCookieStore();
      }

      Object var17 = this.credentialsProvider;
      if (var17 == null) {
         if (this.systemProperties) {
            var17 = new SystemDefaultCredentialsProvider();
         } else {
            var17 = new BasicCredentialsProvider();
         }
      }

      ArrayList var18 = this.closeables != null ? new ArrayList<>(this.closeables) : null;
      if (!this.connManagerShared) {
         if (var18 == null) {
            var18 = new ArrayList(1);
         }

         if (this.evictExpiredConnections || this.evictIdleConnections) {
            final IdleConnectionEvictor var20 = new IdleConnectionEvictor(
               (HttpClientConnectionManager)var3,
               this.maxIdleTime > 0L ? this.maxIdleTime : 10L,
               this.maxIdleTimeUnit != null ? this.maxIdleTimeUnit : TimeUnit.SECONDS,
               this.maxIdleTime,
               this.maxIdleTimeUnit
            );
            var18.add(new Closeable() {
               @Override
               public void close() throws IOException {
                  var20.shutdown();

                  try {
                     var20.awaitTermination(1L, TimeUnit.SECONDS);
                  } catch (InterruptedException var2) {
                     Thread.currentThread().interrupt();
                  }
               }
            });
            var20.start();
         }

         var18.add(new Closeable() {
            @Override
            public void close() throws IOException {
               ((HttpClientConnectionManager)var3).shutdown();
            }
         });
      }

      return new InternalHttpClient(
         var10,
         (HttpClientConnectionManager)var3,
         (HttpRoutePlanner)var34,
         var48,
         (Lookup<AuthSchemeProvider>)var47,
         (CookieStore)var16,
         (CredentialsProvider)var17,
         this.defaultRequestConfig != null ? this.defaultRequestConfig : RequestConfig.DEFAULT,
         var18
      );
   }
}
