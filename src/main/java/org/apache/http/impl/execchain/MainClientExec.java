package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.HttpAuthenticator;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class MainClientExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final HttpRequestExecutor requestExecutor;
   private final HttpClientConnectionManager connManager;
   private final ConnectionReuseStrategy reuseStrategy;
   private final ConnectionKeepAliveStrategy keepAliveStrategy;
   private final HttpProcessor proxyHttpProcessor;
   private final AuthenticationStrategy targetAuthStrategy;
   private final AuthenticationStrategy proxyAuthStrategy;
   private final HttpAuthenticator authenticator;
   private final UserTokenHandler userTokenHandler;
   private final HttpRouteDirector routeDirector;

   public MainClientExec(
      HttpRequestExecutor var1,
      HttpClientConnectionManager var2,
      ConnectionReuseStrategy var3,
      ConnectionKeepAliveStrategy var4,
      HttpProcessor var5,
      AuthenticationStrategy var6,
      AuthenticationStrategy var7,
      UserTokenHandler var8
   ) {
      Args.notNull(var1, "HTTP request executor");
      Args.notNull(var2, "Client connection manager");
      Args.notNull(var3, "Connection reuse strategy");
      Args.notNull(var4, "Connection keep alive strategy");
      Args.notNull(var5, "Proxy HTTP processor");
      Args.notNull(var6, "Target authentication strategy");
      Args.notNull(var7, "Proxy authentication strategy");
      Args.notNull(var8, "User token handler");
      this.authenticator = new HttpAuthenticator();
      this.routeDirector = new BasicRouteDirector();
      this.requestExecutor = var1;
      this.connManager = var2;
      this.reuseStrategy = var3;
      this.keepAliveStrategy = var4;
      this.proxyHttpProcessor = var5;
      this.targetAuthStrategy = var6;
      this.proxyAuthStrategy = var7;
      this.userTokenHandler = var8;
   }

   public MainClientExec(
      HttpRequestExecutor var1,
      HttpClientConnectionManager var2,
      ConnectionReuseStrategy var3,
      ConnectionKeepAliveStrategy var4,
      AuthenticationStrategy var5,
      AuthenticationStrategy var6,
      UserTokenHandler var7
   ) {
      this(var1, var2, var3, var4, new ImmutableHttpProcessor(new RequestTargetHost()), var5, var6, var7);
   }

   @Override
   public CloseableHttpResponse execute(HttpRoute var1, HttpRequestWrapper var2, HttpClientContext var3, HttpExecutionAware var4) throws IOException, HttpException {
      Args.notNull(var1, "HTTP route");
      Args.notNull(var2, "HTTP request");
      Args.notNull(var3, "HTTP context");
      AuthState var5 = var3.getTargetAuthState();
      if (var5 == null) {
         var5 = new AuthState();
         var3.setAttribute("http.auth.target-scope", var5);
      }

      AuthState var6 = var3.getProxyAuthState();
      if (var6 == null) {
         var6 = new AuthState();
         var3.setAttribute("http.auth.proxy-scope", var6);
      }

      if (var2 instanceof HttpEntityEnclosingRequest) {
         RequestEntityProxy.enhance((HttpEntityEnclosingRequest)var2);
      }

      Object var7 = var3.getUserToken();
      ConnectionRequest var8 = this.connManager.requestConnection(var1, var7);
      if (var4 != null) {
         if (var4.isAborted()) {
            var8.cancel();
            throw new RequestAbortedException("Request aborted");
         }

         var4.setCancellable(var8);
      }

      RequestConfig var9 = var3.getRequestConfig();

      HttpClientConnection var10;
      try {
         int var11 = var9.getConnectionRequestTimeout();
         var10 = var8.get(var11 > 0 ? (long)var11 : 0L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var18) {
         Thread.currentThread().interrupt();
         throw new RequestAbortedException("Request aborted", var18);
      } catch (ExecutionException var19) {
         Object var12 = var19.getCause();
         if (var12 == null) {
            var12 = var19;
         }

         throw new RequestAbortedException("Request execution failed", (Throwable)var12);
      }

      var3.setAttribute("http.connection", var10);
      if (var9.isStaleConnectionCheckEnabled() && var10.isOpen()) {
         this.log.debug("Stale connection check");
         if (var10.isStale()) {
            this.log.debug("Stale connection detected");
            var10.close();
         }
      }

      ConnectionHolder var26 = new ConnectionHolder(this.log, this.connManager, var10);

      try {
         if (var4 != null) {
            var4.setCancellable(var26);
         }

         int var28 = 1;

         HttpResponse var27;
         while (true) {
            if (var28 > 1 && !RequestEntityProxy.isRepeatable(var2)) {
               throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
            }

            if (var4 != null && var4.isAborted()) {
               throw new RequestAbortedException("Request aborted");
            }

            if (!var10.isOpen()) {
               this.log.debug("Opening connection " + var1);

               try {
                  this.establishRoute(var6, var10, var1, var2, var3);
               } catch (TunnelRefusedException var20) {
                  if (this.log.isDebugEnabled()) {
                     this.log.debug(var20.getMessage());
                  }

                  var27 = var20.getResponse();
                  break;
               }
            }

            int var14 = var9.getSocketTimeout();
            if (var14 >= 0) {
               var10.setSocketTimeout(var14);
            }

            if (var4 != null && var4.isAborted()) {
               throw new RequestAbortedException("Request aborted");
            }

            if (this.log.isDebugEnabled()) {
               this.log.debug("Executing request " + var2.getRequestLine());
            }

            if (!var2.containsHeader("Authorization")) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Target auth state: " + var5.getState());
               }

               this.authenticator.generateAuthResponse(var2, var5, var3);
            }

            if (!var2.containsHeader("Proxy-Authorization") && !var1.isTunnelled()) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Proxy auth state: " + var6.getState());
               }

               this.authenticator.generateAuthResponse(var2, var6, var3);
            }

            var3.setAttribute("http.request", var2);
            var27 = this.requestExecutor.execute(var2, var10, var3);
            if (this.reuseStrategy.keepAlive(var27, var3)) {
               long var15 = this.keepAliveStrategy.getKeepAliveDuration(var27, var3);
               if (this.log.isDebugEnabled()) {
                  String var17;
                  if (var15 > 0L) {
                     var17 = "for " + var15 + " " + TimeUnit.MILLISECONDS;
                  } else {
                     var17 = "indefinitely";
                  }

                  this.log.debug("Connection can be kept alive " + var17);
               }

               var26.setValidFor(var15, TimeUnit.MILLISECONDS);
               var26.markReusable();
            } else {
               var26.markNonReusable();
            }

            if (!this.needAuthentication(var5, var6, var1, var27, var3)) {
               break;
            }

            HttpEntity var30 = var27.getEntity();
            if (var26.isReusable()) {
               EntityUtils.consume(var30);
            } else {
               var10.close();
               if (var6.getState() == AuthProtocolState.SUCCESS && var6.isConnectionBased()) {
                  this.log.debug("Resetting proxy auth state");
                  var6.reset();
               }

               if (var5.getState() == AuthProtocolState.SUCCESS && var5.isConnectionBased()) {
                  this.log.debug("Resetting target auth state");
                  var5.reset();
               }
            }

            HttpRequest var16 = var2.getOriginal();
            if (!var16.containsHeader("Authorization")) {
               var2.removeHeaders("Authorization");
            }

            if (!var16.containsHeader("Proxy-Authorization")) {
               var2.removeHeaders("Proxy-Authorization");
            }

            var28++;
         }

         if (var7 == null) {
            var7 = this.userTokenHandler.getUserToken(var3);
            var3.setAttribute("http.user-token", var7);
         }

         if (var7 != null) {
            var26.setState(var7);
         }

         HttpEntity var29 = var27.getEntity();
         if (var29 != null && var29.isStreaming()) {
            return new HttpResponseProxy(var27, var26);
         } else {
            var26.releaseConnection();
            return new HttpResponseProxy(var27, null);
         }
      } catch (ConnectionShutdownException var21) {
         InterruptedIOException var13 = new InterruptedIOException("Connection has been shut down");
         var13.initCause(var21);
         throw var13;
      } catch (HttpException var22) {
         var26.abortConnection();
         throw var22;
      } catch (IOException var23) {
         var26.abortConnection();
         if (var6.isConnectionBased()) {
            var6.reset();
         }

         if (var5.isConnectionBased()) {
            var5.reset();
         }

         throw var23;
      } catch (RuntimeException var24) {
         var26.abortConnection();
         if (var6.isConnectionBased()) {
            var6.reset();
         }

         if (var5.isConnectionBased()) {
            var5.reset();
         }

         throw var24;
      } catch (Error var25) {
         this.connManager.shutdown();
         throw var25;
      }
   }

   void establishRoute(AuthState var1, HttpClientConnection var2, HttpRoute var3, HttpRequest var4, HttpClientContext var5) throws HttpException, IOException {
      RequestConfig var6 = var5.getRequestConfig();
      int var7 = var6.getConnectTimeout();
      RouteTracker var8 = new RouteTracker(var3);

      int var9;
      do {
         HttpRoute var10 = var8.toRoute();
         var9 = this.routeDirector.nextStep(var3, var10);
         switch (var9) {
            case -1:
               throw new HttpException("Unable to establish route: planned = " + var3 + "; current = " + var10);
            case 0:
               this.connManager.routeComplete(var2, var3, var5);
               break;
            case 1:
               this.connManager.connect(var2, var3, var7 > 0 ? var7 : 0, var5);
               var8.connectTarget(var3.isSecure());
               break;
            case 2:
               this.connManager.connect(var2, var3, var7 > 0 ? var7 : 0, var5);
               HttpHost var11 = var3.getProxyHost();
               var8.connectProxy(var11, var3.isSecure() && !var3.isTunnelled());
               break;
            case 3:
               boolean var14 = this.createTunnelToTarget(var1, var2, var3, var4, var5);
               this.log.debug("Tunnel to target created.");
               var8.tunnelTarget(var14);
               break;
            case 4:
               int var12 = var10.getHopCount() - 1;
               boolean var13 = this.createTunnelToProxy(var3, var12, var5);
               this.log.debug("Tunnel to proxy created.");
               var8.tunnelProxy(var3.getHopTarget(var12), var13);
               break;
            case 5:
               this.connManager.upgrade(var2, var3, var5);
               var8.layerProtocol(var3.isSecure());
               break;
            default:
               throw new IllegalStateException("Unknown step indicator " + var9 + " from RouteDirector.");
         }
      } while (var9 > 0);
   }

   private boolean createTunnelToTarget(AuthState var1, HttpClientConnection var2, HttpRoute var3, HttpRequest var4, HttpClientContext var5) throws HttpException, IOException {
      RequestConfig var6 = var5.getRequestConfig();
      int var7 = var6.getConnectTimeout();
      HttpHost var8 = var3.getTargetHost();
      HttpHost var9 = var3.getProxyHost();
      HttpResponse var10 = null;
      String var11 = var8.toHostString();
      BasicHttpRequest var12 = new BasicHttpRequest("CONNECT", var11, var4.getProtocolVersion());
      this.requestExecutor.preProcess(var12, this.proxyHttpProcessor, var5);

      while (var10 == null) {
         if (!var2.isOpen()) {
            this.connManager.connect(var2, var3, var7 > 0 ? var7 : 0, var5);
         }

         var12.removeHeaders("Proxy-Authorization");
         this.authenticator.generateAuthResponse(var12, var1, var5);
         var10 = this.requestExecutor.execute(var12, var2, var5);
         this.requestExecutor.postProcess(var10, this.proxyHttpProcessor, var5);
         int var13 = var10.getStatusLine().getStatusCode();
         if (var13 < 200) {
            throw new HttpException("Unexpected response to CONNECT request: " + var10.getStatusLine());
         }

         if (var6.isAuthenticationEnabled()
            && this.authenticator.isAuthenticationRequested(var9, var10, this.proxyAuthStrategy, var1, var5)
            && this.authenticator.handleAuthChallenge(var9, var10, this.proxyAuthStrategy, var1, var5)) {
            if (this.reuseStrategy.keepAlive(var10, var5)) {
               this.log.debug("Connection kept alive");
               HttpEntity var14 = var10.getEntity();
               EntityUtils.consume(var14);
            } else {
               var2.close();
            }

            var10 = null;
         }
      }

      int var15 = var10.getStatusLine().getStatusCode();
      if (var15 > 299) {
         HttpEntity var16 = var10.getEntity();
         if (var16 != null) {
            var10.setEntity(new BufferedHttpEntity(var16));
         }

         var2.close();
         throw new TunnelRefusedException("CONNECT refused by proxy: " + var10.getStatusLine(), var10);
      } else {
         return false;
      }
   }

   private boolean createTunnelToProxy(HttpRoute var1, int var2, HttpClientContext var3) throws HttpException {
      throw new HttpException("Proxy chains are not supported.");
   }

   private boolean needAuthentication(AuthState var1, AuthState var2, HttpRoute var3, HttpResponse var4, HttpClientContext var5) {
      RequestConfig var6 = var5.getRequestConfig();
      if (var6.isAuthenticationEnabled()) {
         HttpHost var7 = var5.getTargetHost();
         if (var7 == null) {
            var7 = var3.getTargetHost();
         }

         if (var7.getPort() < 0) {
            var7 = new HttpHost(var7.getHostName(), var3.getTargetHost().getPort(), var7.getSchemeName());
         }

         boolean var8 = this.authenticator.isAuthenticationRequested(var7, var4, this.targetAuthStrategy, var1, var5);
         HttpHost var9 = var3.getProxyHost();
         if (var9 == null) {
            var9 = var3.getTargetHost();
         }

         boolean var10 = this.authenticator.isAuthenticationRequested(var9, var4, this.proxyAuthStrategy, var2, var5);
         if (var8) {
            return this.authenticator.handleAuthChallenge(var7, var4, this.targetAuthStrategy, var1, var5);
         }

         if (var10) {
            return this.authenticator.handleAuthChallenge(var9, var4, this.proxyAuthStrategy, var2, var5);
         }
      }

      return false;
   }
}
