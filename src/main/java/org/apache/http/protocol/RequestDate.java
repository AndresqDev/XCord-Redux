package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class RequestDate implements HttpRequestInterceptor {
   private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();

   @Override
   public void process(HttpRequest var1, HttpContext var2) throws HttpException, IOException {
      Args.notNull(var1, "HTTP request");
      if (var1 instanceof HttpEntityEnclosingRequest && !var1.containsHeader("Date")) {
         String var3 = DATE_GENERATOR.getCurrentDate();
         var1.setHeader("Date", var3);
      }
   }
}
