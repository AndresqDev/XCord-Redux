package org.apache.http.protocol;

import java.util.Map;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class HttpRequestHandlerRegistry implements HttpRequestHandlerResolver {
   private final UriPatternMatcher<HttpRequestHandler> matcher = new UriPatternMatcher<>();

   public void register(String var1, HttpRequestHandler var2) {
      Args.notNull(var1, "URI request pattern");
      Args.notNull(var2, "Request handler");
      this.matcher.register(var1, var2);
   }

   public void unregister(String var1) {
      this.matcher.unregister(var1);
   }

   public void setHandlers(Map<String, HttpRequestHandler> var1) {
      this.matcher.setObjects(var1);
   }

   public Map<String, HttpRequestHandler> getHandlers() {
      return this.matcher.getObjects();
   }

   @Override
   public HttpRequestHandler lookup(String var1) {
      return this.matcher.lookup(var1);
   }
}
