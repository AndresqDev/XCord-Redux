package org.apache.http.impl.client;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class StandardHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
   private final Map<String, Boolean> idempotentMethods = new ConcurrentHashMap<>();

   public StandardHttpRequestRetryHandler(int var1, boolean var2) {
      super(var1, var2);
      this.idempotentMethods.put("GET", Boolean.TRUE);
      this.idempotentMethods.put("HEAD", Boolean.TRUE);
      this.idempotentMethods.put("PUT", Boolean.TRUE);
      this.idempotentMethods.put("DELETE", Boolean.TRUE);
      this.idempotentMethods.put("OPTIONS", Boolean.TRUE);
      this.idempotentMethods.put("TRACE", Boolean.TRUE);
   }

   public StandardHttpRequestRetryHandler() {
      this(3, false);
   }

   @Override
   protected boolean handleAsIdempotent(HttpRequest var1) {
      String var2 = var1.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
      Boolean var3 = this.idempotentMethods.get(var2);
      return var3 != null && var3;
   }
}
