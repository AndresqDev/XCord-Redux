package org.apache.http.impl.pool;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.pool.PoolEntry;

@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection> {
   public BasicPoolEntry(String var1, HttpHost var2, HttpClientConnection var3) {
      super(var1, var2, var3);
   }

   @Override
   public void close() {
      try {
         HttpClientConnection var1 = this.getConnection();

         try {
            int var2 = var1.getSocketTimeout();
            if (var2 <= 0 || var2 > 1000) {
               var1.setSocketTimeout(1000);
            }

            var1.close();
         } catch (IOException var3) {
            var1.shutdown();
         }
      } catch (IOException var4) {
      }
   }

   @Override
   public boolean isClosed() {
      return !this.getConnection().isOpen();
   }
}
