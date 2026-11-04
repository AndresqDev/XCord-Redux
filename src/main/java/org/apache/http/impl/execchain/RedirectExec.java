package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthState;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RedirectExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final ClientExecChain requestExecutor;
   private final RedirectStrategy redirectStrategy;
   private final HttpRoutePlanner routePlanner;

   public RedirectExec(ClientExecChain var1, HttpRoutePlanner var2, RedirectStrategy var3) {
      Args.notNull(var1, "HTTP client request executor");
      Args.notNull(var2, "HTTP route planner");
      Args.notNull(var3, "HTTP redirect strategy");
      this.requestExecutor = var1;
      this.routePlanner = var2;
      this.redirectStrategy = var3;
   }

   @Override
   public CloseableHttpResponse execute(HttpRoute var1, HttpRequestWrapper var2, HttpClientContext var3, HttpExecutionAware var4) throws IOException, HttpException {
      Args.notNull(var1, "HTTP route");
      Args.notNull(var2, "HTTP request");
      Args.notNull(var3, "HTTP context");
      List var5 = var3.getRedirectLocations();
      if (var5 != null) {
         var5.clear();
      }

      RequestConfig var6 = var3.getRequestConfig();
      int var7 = var6.getMaxRedirects() > 0 ? var6.getMaxRedirects() : 50;
      HttpRoute var8 = var1;
      HttpRequestWrapper var9 = var2;
      int var10 = 0;

      while (true) {
         CloseableHttpResponse var11 = this.requestExecutor.execute(var8, var9, var3, var4);

         try {
            if (!var6.isRedirectsEnabled() || !this.redirectStrategy.isRedirected(var9.getOriginal(), var11, var3)) {
               return var11;
            }

            if (!RequestEntityProxy.isRepeatable(var9)) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Cannot redirect non-repeatable request");
               }

               return var11;
            }

            if (var10 >= var7) {
               throw new RedirectException("Maximum redirects (" + var7 + ") exceeded");
            }

            var10++;
            HttpUriRequest var12 = this.redirectStrategy.getRedirect(var9.getOriginal(), var11, var3);
            if (!var12.headerIterator().hasNext()) {
               HttpRequest var13 = var2.getOriginal();
               var12.setHeaders(var13.getAllHeaders());
            }

            var9 = HttpRequestWrapper.wrap(var12);
            if (var9 instanceof HttpEntityEnclosingRequest) {
               RequestEntityProxy.enhance((HttpEntityEnclosingRequest)var9);
            }

            URI var28 = var9.getURI();
            HttpHost var14 = URIUtils.extractHost(var28);
            if (var14 == null) {
               throw new ProtocolException("Redirect URI does not specify a valid host name: " + var28);
            }

            if (!var8.getTargetHost().equals(var14)) {
               AuthState var15 = var3.getTargetAuthState();
               if (var15 != null) {
                  this.log.debug("Resetting target auth state");
                  var15.reset();
               }

               AuthState var16 = var3.getProxyAuthState();
               if (var16 != null && var16.isConnectionBased()) {
                  this.log.debug("Resetting proxy auth state");
                  var16.reset();
               }
            }

            var8 = this.routePlanner.determineRoute(var14, var9, var3);
            if (this.log.isDebugEnabled()) {
               this.log.debug("Redirecting to '" + var28 + "' via " + var8);
            }

            EntityUtils.consume(var11.getEntity());
            var11.close();
         } catch (RuntimeException var25) {
            var11.close();
            throw var25;
         } catch (IOException var26) {
            var11.close();
            throw var26;
         } catch (HttpException var27) {
            try {
               EntityUtils.consume(var11.getEntity());
            } catch (IOException var23) {
               this.log.debug("I/O error while releasing connection", var23);
            } finally {
               var11.close();
            }

            throw var27;
         }
      }
   }
}
