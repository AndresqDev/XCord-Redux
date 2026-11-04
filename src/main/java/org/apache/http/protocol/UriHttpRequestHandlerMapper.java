package org.apache.http.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class UriHttpRequestHandlerMapper implements HttpRequestHandlerMapper {
   private final UriPatternMatcher<HttpRequestHandler> matcher;

   protected UriHttpRequestHandlerMapper(UriPatternMatcher<HttpRequestHandler> var1) {
      this.matcher = Args.notNull(var1, "Pattern matcher");
   }

   public UriHttpRequestHandlerMapper() {
      this(new UriPatternMatcher<>());
   }

   public void register(String var1, HttpRequestHandler var2) {
      Args.notNull(var1, "Pattern");
      Args.notNull(var2, "Handler");
      this.matcher.register(var1, var2);
   }

   public void unregister(String var1) {
      this.matcher.unregister(var1);
   }

   protected String getRequestPath(HttpRequest var1) {
      String var2 = var1.getRequestLine().getUri();
      int var3 = var2.indexOf(63);
      if (var3 != -1) {
         var2 = var2.substring(0, var3);
      } else {
         var3 = var2.indexOf(35);
         if (var3 != -1) {
            var2 = var2.substring(0, var3);
         }
      }

      return var2;
   }

   @Override
   public HttpRequestHandler lookup(HttpRequest var1) {
      Args.notNull(var1, "HTTP request");
      return this.matcher.lookup(this.getRequestPath(var1));
   }
}
