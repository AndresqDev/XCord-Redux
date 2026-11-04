package org.apache.http.client.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Lookup;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RequestAddCookies implements HttpRequestInterceptor {
   private final Log log = LogFactory.getLog(this.getClass());

   @Override
   public void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "HTTP context");
      String var3 = var1.getRequestLine().getMethod();
      if (!var3.equalsIgnoreCase("CONNECT")) {
         HttpClientContext var4 = HttpClientContext.adapt(var2);
         CookieStore var5 = var4.getCookieStore();
         if (var5 == null) {
            this.log.debug("Cookie store not specified in HTTP context");
         } else {
            Lookup var6 = var4.getCookieSpecRegistry();
            if (var6 == null) {
               this.log.debug("CookieSpec registry not specified in HTTP context");
            } else {
               HttpHost var7 = var4.getTargetHost();
               if (var7 == null) {
                  this.log.debug("Target host not set in the context");
               } else {
                  RouteInfo var8 = var4.getHttpRoute();
                  if (var8 == null) {
                     this.log.debug("Connection route not set in the context");
                  } else {
                     RequestConfig var9 = var4.getRequestConfig();
                     String var10 = var9.getCookieSpec();
                     if (var10 == null) {
                        var10 = "default";
                     }

                     if (this.log.isDebugEnabled()) {
                        this.log.debug("CookieSpec selected: " + var10);
                     }

                     URI var11 = null;
                     if (var1 instanceof HttpUriRequest) {
                        var11 = ((HttpUriRequest)var1).getURI();
                     } else {
                        try {
                           var11 = new URI(var1.getRequestLine().getUri());
                        } catch (URISyntaxException var25) {
                        }
                     }

                     String var12 = var11 != null ? var11.getPath() : null;
                     String var13 = var7.getHostName();
                     int var14 = var7.getPort();
                     if (var14 < 0) {
                        var14 = var8.getTargetHost().getPort();
                     }

                     CookieOrigin var15 = new CookieOrigin(var13, var14 >= 0 ? var14 : 0, !TextUtils.isEmpty(var12) ? var12 : "/", var8.isSecure());
                     CookieSpecProvider var16 = (CookieSpecProvider)var6.lookup(var10);
                     if (var16 == null) {
                        if (this.log.isDebugEnabled()) {
                           this.log.debug("Unsupported cookie policy: " + var10);
                        }
                     } else {
                        CookieSpec var17 = var16.create(var4);
                        List var18 = var5.getCookies();
                        ArrayList var19 = new ArrayList();
                        Date var20 = new Date();
                        boolean var21 = false;

                        for (Cookie var23 : var18) {
                           if (!var23.isExpired(var20)) {
                              if (var17.match(var23, var15)) {
                                 if (this.log.isDebugEnabled()) {
                                    this.log.debug("Cookie " + var23 + " match " + var15);
                                 }

                                 var19.add(var23);
                              }
                           } else {
                              if (this.log.isDebugEnabled()) {
                                 this.log.debug("Cookie " + var23 + " expired");
                              }

                              var21 = true;
                           }
                        }

                        if (var21) {
                           var5.clearExpired(var20);
                        }

                        if (!var19.isEmpty()) {
                           for (Header var24 : var17.formatCookies(var19)) {
                              var1.addHeader(var24);
                           }
                        }

                        int var27 = var17.getVersion();
                        if (var27 > 0) {
                           Header var29 = var17.getVersionHeader();
                           if (var29 != null) {
                              var1.addHeader(var29);
                           }
                        }

                        var2.setAttribute("http.cookie-spec", var17);
                        var2.setAttribute("http.cookie-origin", var15);
                     }
                  }
               }
            }
         }
      }
   }
}
