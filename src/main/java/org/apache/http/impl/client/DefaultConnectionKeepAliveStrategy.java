package org.apache.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
   public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

   @Override
   public long getKeepAliveDuration(HttpResponse var1, HttpContext var2) {
      Args.notNull(var1, "HTTP response");
      BasicHeaderElementIterator var3 = new BasicHeaderElementIterator(var1.headerIterator("Keep-Alive"));

      while (var3.hasNext()) {
         HeaderElement var4 = var3.nextElement();
         String var5 = var4.getName();
         String var6 = var4.getValue();
         if (var6 != null && var5.equalsIgnoreCase("timeout")) {
            try {
               return Long.parseLong(var6) * 1000L;
            } catch (NumberFormatException var8) {
            }
         }
      }

      return -1L;
   }
}
