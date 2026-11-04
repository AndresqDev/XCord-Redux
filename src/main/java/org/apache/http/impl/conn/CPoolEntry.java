package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.PoolEntry;

@Contract(
   threading = ThreadingBehavior.SAFE
)
class CPoolEntry extends PoolEntry<HttpRoute, ManagedHttpClientConnection> {
   private final Log log;
   private volatile boolean routeComplete;

   public CPoolEntry(Log var1, String var2, HttpRoute var3, ManagedHttpClientConnection var4, long var5, TimeUnit var7) {
      super(var2, var3, var4, var5, var7);
      this.log = var1;
   }

   public void markRouteComplete() {
      this.routeComplete = true;
   }

   public boolean isRouteComplete() {
      return this.routeComplete;
   }

   public void closeConnection() throws IOException {
      HttpClientConnection var1 = this.getConnection();
      var1.close();
   }

   public void shutdownConnection() throws IOException {
      HttpClientConnection var1 = this.getConnection();
      var1.shutdown();
   }

   @Override
   public boolean isExpired(long var1) {
      boolean var3 = super.isExpired(var1);
      if (var3 && this.log.isDebugEnabled()) {
         this.log.debug("Connection " + this + " expired @ " + new Date(this.getExpiry()));
      }

      return var3;
   }

   @Override
   public boolean isClosed() {
      HttpClientConnection var1 = this.getConnection();
      return !var1.isOpen();
   }

   @Override
   public void close() {
      try {
         this.closeConnection();
      } catch (IOException var2) {
         this.log.debug("I/O error closing connection", var2);
      }
   }
}
