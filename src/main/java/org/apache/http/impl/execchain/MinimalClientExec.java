package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.VersionInfo;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class MinimalClientExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final HttpRequestExecutor requestExecutor;
   private final HttpClientConnectionManager connManager;
   private final ConnectionReuseStrategy reuseStrategy;
   private final ConnectionKeepAliveStrategy keepAliveStrategy;
   private final HttpProcessor httpProcessor;

   public MinimalClientExec(HttpRequestExecutor var1, HttpClientConnectionManager var2, ConnectionReuseStrategy var3, ConnectionKeepAliveStrategy var4) {
      Args.notNull(var1, "HTTP request executor");
      Args.notNull(var2, "Client connection manager");
      Args.notNull(var3, "Connection reuse strategy");
      Args.notNull(var4, "Connection keep alive strategy");
      this.httpProcessor = new ImmutableHttpProcessor(
         new RequestContent(),
         new RequestTargetHost(),
         new RequestClientConnControl(),
         new RequestUserAgent(VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", this.getClass()))
      );
      this.requestExecutor = var1;
      this.connManager = var2;
      this.reuseStrategy = var3;
      this.keepAliveStrategy = var4;
   }

   static void rewriteRequestURI(HttpRequestWrapper var0, HttpRoute var1, boolean var2) throws ProtocolException {
      try {
         URI var3 = var0.getURI();
         if (var3 != null) {
            if (var3.isAbsolute()) {
               var3 = URIUtils.rewriteURI(var3, null, var2 ? URIUtils.DROP_FRAGMENT_AND_NORMALIZE : URIUtils.DROP_FRAGMENT);
            } else {
               var3 = URIUtils.rewriteURI(var3);
            }

            var0.setURI(var3);
         }
      } catch (URISyntaxException var4) {
         throw new ProtocolException("Invalid URI: " + var0.getRequestLine().getUri(), var4);
      }
   }

   @Override
   public CloseableHttpResponse execute(HttpRoute var1, HttpRequestWrapper var2, HttpClientContext var3, HttpExecutionAware var4) throws IOException, HttpException {
      Args.notNull(var1, "HTTP route");
      Args.notNull(var2, "HTTP request");
      Args.notNull(var3, "HTTP context");
      rewriteRequestURI(var2, var1, var3.getRequestConfig().isNormalizeUri());
      ConnectionRequest var5 = this.connManager.requestConnection(var1, null);
      if (var4 != null) {
         if (var4.isAborted()) {
            var5.cancel();
            throw new RequestAbortedException("Request aborted");
         }

         var4.setCancellable(var5);
      }

      RequestConfig var6 = var3.getRequestConfig();

      HttpClientConnection var7;
      try {
         int var8 = var6.getConnectionRequestTimeout();
         var7 = var5.get(var8 > 0 ? (long)var8 : 0L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var20) {
         Thread.currentThread().interrupt();
         throw new RequestAbortedException("Request aborted", var20);
      } catch (ExecutionException var21) {
         Object var9 = var21.getCause();
         if (var9 == null) {
            var9 = var21;
         }

         throw new RequestAbortedException("Request execution failed", (Throwable)var9);
      }

      ConnectionHolder var22 = new ConnectionHolder(this.log, this.connManager, var7);

      try {
         if (var4 != null) {
            if (var4.isAborted()) {
               var22.close();
               throw new RequestAbortedException("Request aborted");
            }

            var4.setCancellable(var22);
         }

         if (!var7.isOpen()) {
            int var23 = var6.getConnectTimeout();
            this.connManager.connect(var7, var1, var23 > 0 ? var23 : 0, var3);
            this.connManager.routeComplete(var7, var1, var3);
         }

         int var24 = var6.getSocketTimeout();
         if (var24 >= 0) {
            var7.setSocketTimeout(var24);
         }

         HttpHost var25 = null;
         HttpRequest var11 = var2.getOriginal();
         if (var11 instanceof HttpUriRequest) {
            URI var12 = ((HttpUriRequest)var11).getURI();
            if (var12.isAbsolute()) {
               var25 = new HttpHost(var12.getHost(), var12.getPort(), var12.getScheme());
            }
         }

         if (var25 == null) {
            var25 = var1.getTargetHost();
         }

         var3.setAttribute("http.target_host", var25);
         var3.setAttribute("http.request", var2);
         var3.setAttribute("http.connection", var7);
         var3.setAttribute("http.route", var1);
         this.httpProcessor.process(var2, var3);
         HttpResponse var26 = this.requestExecutor.execute(var2, var7, var3);
         this.httpProcessor.process(var26, var3);
         if (this.reuseStrategy.keepAlive(var26, var3)) {
            long var13 = this.keepAliveStrategy.getKeepAliveDuration(var26, var3);
            var22.setValidFor(var13, TimeUnit.MILLISECONDS);
            var22.markReusable();
         } else {
            var22.markNonReusable();
         }

         HttpEntity var27 = var26.getEntity();
         if (var27 != null && var27.isStreaming()) {
            return new HttpResponseProxy(var26, var22);
         } else {
            var22.releaseConnection();
            return new HttpResponseProxy(var26, null);
         }
      } catch (ConnectionShutdownException var15) {
         InterruptedIOException var10 = new InterruptedIOException("Connection has been shut down");
         var10.initCause(var15);
         throw var10;
      } catch (HttpException var16) {
         var22.abortConnection();
         throw var16;
      } catch (IOException var17) {
         var22.abortConnection();
         throw var17;
      } catch (RuntimeException var18) {
         var22.abortConnection();
         throw var18;
      } catch (Error var19) {
         this.connManager.shutdown();
         throw var19;
      }
   }
}
