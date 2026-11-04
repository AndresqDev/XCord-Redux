package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class ResponseServer implements HttpResponseInterceptor {
   private final String originServer;

   public ResponseServer(String var1) {
      this.originServer = var1;
   }

   public ResponseServer() {
      this(null);
   }

   @Override
   public void process(HttpResponse var1, HttpContext var2) throws HttpException, IOException {
      Args.notNull(var1, "HTTP response");
      if (!var1.containsHeader("Server") && this.originServer != null) {
         var1.addHeader("Server", this.originServer);
      }
   }
}
