package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultRedirectStrategy implements RedirectStrategy {
   private final Log log = LogFactory.getLog(this.getClass());
   public static final int SC_PERMANENT_REDIRECT = 308;
   @Deprecated
   public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
   public static final DefaultRedirectStrategy INSTANCE = new DefaultRedirectStrategy();
   private final String[] redirectMethods;

   public DefaultRedirectStrategy() {
      this(new String[]{"GET", "HEAD"});
   }

   public DefaultRedirectStrategy(String[] var1) {
      String[] var2 = (String[])var1.clone();
      Arrays.sort((Object[])var2);
      this.redirectMethods = var2;
   }

   @Override
   public boolean isRedirected(HttpRequest var1, HttpResponse var2, HttpContext var3) throws ProtocolException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "HTTP response");
      int var4 = var2.getStatusLine().getStatusCode();
      String var5 = var1.getRequestLine().getMethod();
      Header var6 = var2.getFirstHeader("location");
      switch (var4) {
         case 301:
         case 307:
         case 308:
            return this.isRedirectable(var5);
         case 302:
            return this.isRedirectable(var5) && var6 != null;
         case 303:
            return true;
         case 304:
         case 305:
         case 306:
         default:
            return false;
      }
   }

   public URI getLocationURI(HttpRequest var1, HttpResponse var2, HttpContext var3) throws ProtocolException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "HTTP response");
      Args.notNull(var3, "HTTP context");
      HttpClientContext var4 = HttpClientContext.adapt(var3);
      Header var5 = var2.getFirstHeader("location");
      if (var5 == null) {
         throw new ProtocolException("Received redirect response " + var2.getStatusLine() + " but no location header");
      } else {
         String var6 = var5.getValue();
         if (this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location '" + var6 + "'");
         }

         RequestConfig var7 = var4.getRequestConfig();
         URI var8 = this.createLocationURI(var6);

         try {
            if (var7.isNormalizeUri()) {
               var8 = URIUtils.normalizeSyntax(var8);
            }

            if (!var8.isAbsolute()) {
               if (!var7.isRelativeRedirectsAllowed()) {
                  throw new ProtocolException("Relative redirect location '" + var8 + "' not allowed");
               }

               HttpHost var9 = var4.getTargetHost();
               Asserts.notNull(var9, "Target host");
               URI var10 = new URI(var1.getRequestLine().getUri());
               URI var11 = URIUtils.rewriteURI(var10, var9, var7.isNormalizeUri() ? URIUtils.NORMALIZE : URIUtils.NO_FLAGS);
               var8 = URIUtils.resolve(var11, var8);
            }
         } catch (URISyntaxException var12) {
            throw new ProtocolException(var12.getMessage(), var12);
         }

         RedirectLocations var13 = (RedirectLocations)var4.getAttribute("http.protocol.redirect-locations");
         if (var13 == null) {
            var13 = new RedirectLocations();
            var3.setAttribute("http.protocol.redirect-locations", var13);
         }

         if (!var7.isCircularRedirectsAllowed() && var13.contains(var8)) {
            throw new CircularRedirectException("Circular redirect to '" + var8 + "'");
         } else {
            var13.add(var8);
            return var8;
         }
      }
   }

   protected URI createLocationURI(String var1) throws ProtocolException {
      try {
         return new URI(var1);
      } catch (URISyntaxException var3) {
         throw new ProtocolException("Invalid redirect URI: " + var1, var3);
      }
   }

   protected boolean isRedirectable(String var1) {
      return Arrays.binarySearch(this.redirectMethods, var1) >= 0;
   }

   @Override
   public HttpUriRequest getRedirect(HttpRequest var1, HttpResponse var2, HttpContext var3) throws ProtocolException {
      URI var4 = this.getLocationURI(var1, var2, var3);
      String var5 = var1.getRequestLine().getMethod();
      if (var5.equalsIgnoreCase("HEAD")) {
         return new HttpHead(var4);
      } else if (var5.equalsIgnoreCase("GET")) {
         return new HttpGet(var4);
      } else {
         int var6 = var2.getStatusLine().getStatusCode();
         return (HttpUriRequest)(var6 != 307 && var6 != 308 ? new HttpGet(var4) : RequestBuilder.copy(var1).setUri(var4).build());
      }
   }
}
