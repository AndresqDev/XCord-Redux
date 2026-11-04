package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RequestAcceptEncoding implements HttpRequestInterceptor {
   private final String acceptEncoding;

   public RequestAcceptEncoding(List<String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringBuilder var2 = new StringBuilder();

         for (int var3 = 0; var3 < var1.size(); var3++) {
            if (var3 > 0) {
               var2.append(",");
            }

            var2.append((String)var1.get(var3));
         }

         this.acceptEncoding = var2.toString();
      } else {
         this.acceptEncoding = "gzip,deflate";
      }
   }

   public RequestAcceptEncoding() {
      this(null);
   }

   @Override
   public void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException {
      HttpClientContext var3 = HttpClientContext.adapt(var2);
      RequestConfig var4 = var3.getRequestConfig();
      if (!var1.containsHeader("Accept-Encoding") && var4.isContentCompressionEnabled()) {
         var1.addHeader("Accept-Encoding", this.acceptEncoding);
      }
   }
}
