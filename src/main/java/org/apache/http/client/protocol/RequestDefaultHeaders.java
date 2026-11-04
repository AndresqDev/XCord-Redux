package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.Collection;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RequestDefaultHeaders implements HttpRequestInterceptor {
   private final Collection<? extends Header> defaultHeaders;

   public RequestDefaultHeaders(Collection<? extends Header> var1) {
      this.defaultHeaders = var1;
   }

   public RequestDefaultHeaders() {
      this(null);
   }

   @Override
   public void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException {
      Args.notNull(var1, "HTTP request");
      String var3 = var1.getRequestLine().getMethod();
      if (!var3.equalsIgnoreCase("CONNECT")) {
         Collection var4 = (Collection)var1.getParams().getParameter("http.default-headers");
         if (var4 == null) {
            var4 = this.defaultHeaders;
         }

         if (var4 != null) {
            for (Header var6 : var4) {
               var1.addHeader(var6);
            }
         }
      }
   }
}
