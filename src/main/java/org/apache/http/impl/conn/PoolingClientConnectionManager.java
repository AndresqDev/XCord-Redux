package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public class PoolingClientConnectionManager implements ClientConnectionManager, ConnPoolControl<HttpRoute> {
   private final Log log = LogFactory.getLog(this.getClass());
   private final SchemeRegistry schemeRegistry;
   private final HttpConnPool pool;
   private final ClientConnectionOperator operator;
   private final DnsResolver dnsResolver;

   public PoolingClientConnectionManager(SchemeRegistry var1) {
      this(var1, -1L, TimeUnit.MILLISECONDS);
   }

   public PoolingClientConnectionManager(SchemeRegistry var1, DnsResolver var2) {
      this(var1, -1L, TimeUnit.MILLISECONDS, var2);
   }

   public PoolingClientConnectionManager() {
      this(SchemeRegistryFactory.createDefault());
   }

   public PoolingClientConnectionManager(SchemeRegistry var1, long var2, TimeUnit var4) {
      this(var1, var2, var4, new SystemDefaultDnsResolver());
   }

   public PoolingClientConnectionManager(SchemeRegistry var1, long var2, TimeUnit var4, DnsResolver var5) {
      Args.notNull(var1, "Scheme registry");
      Args.notNull(var5, "DNS resolver");
      this.schemeRegistry = var1;
      this.dnsResolver = var5;
      this.operator = this.createConnectionOperator(var1);
      this.pool = new HttpConnPool(this.log, this.operator, 2, 20, var2, var4);
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }
   }

   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry var1) {
      return new DefaultClientConnectionOperator(var1, this.dnsResolver);
   }

   @Override
   public SchemeRegistry getSchemeRegistry() {
      return this.schemeRegistry;
   }

   private String format(HttpRoute var1, Object var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("[route: ").append(var1).append("]");
      if (var2 != null) {
         var3.append("[state: ").append(var2).append("]");
      }

      return var3.toString();
   }

   private String formatStats(HttpRoute var1) {
      StringBuilder var2 = new StringBuilder();
      PoolStats var3 = this.pool.getTotalStats();
      PoolStats var4 = this.pool.getStats(var1);
      var2.append("[total kept alive: ").append(var3.getAvailable()).append("; ");
      var2.append("route allocated: ").append(var4.getLeased() + var4.getAvailable());
      var2.append(" of ").append(var4.getMax()).append("; ");
      var2.append("total allocated: ").append(var3.getLeased() + var3.getAvailable());
      var2.append(" of ").append(var3.getMax()).append("]");
      return var2.toString();
   }

   private String format(HttpPoolEntry var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("[id: ").append(var1.getId()).append("]");
      var2.append("[route: ").append(var1.getRoute()).append("]");
      Object var3 = var1.getState();
      if (var3 != null) {
         var2.append("[state: ").append(var3).append("]");
      }

      return var2.toString();
   }

   @Override
   public ClientConnectionRequest requestConnection(HttpRoute var1, Object var2) {
      Args.notNull(var1, "HTTP route");
      if (this.log.isDebugEnabled()) {
         this.log.debug("Connection request: " + this.format(var1, var2) + this.formatStats(var1));
      }

      final Future var3 = this.pool.lease(var1, var2);
      return new ClientConnectionRequest() {
         @Override
         public void abortRequest() {
            var3.cancel(true);
         }

         @Override
         public ManagedClientConnection getConnection(long var1, TimeUnit var3x) throws InterruptedException, ConnectionPoolTimeoutException {
            return PoolingClientConnectionManager.this.leaseConnection(var3, var1, var3x);
         }
      };
   }

   ManagedClientConnection leaseConnection(Future<HttpPoolEntry> var1, long var2, TimeUnit var4) throws InterruptedException, ConnectionPoolTimeoutException {
      try {
         HttpPoolEntry var5 = (HttpPoolEntry)var1.get(var2, var4);
         if (var5 != null && !var1.isCancelled()) {
            Asserts.check(var5.getConnection() != null, "Pool entry with no connection");
            if (this.log.isDebugEnabled()) {
               this.log.debug("Connection leased: " + this.format(var5) + this.formatStats(var5.getRoute()));
            }

            return new ManagedClientConnectionImpl(this, this.operator, var5);
         } else {
            throw new InterruptedException();
         }
      } catch (ExecutionException var8) {
         Object var7 = var8.getCause();
         if (var7 == null) {
            var7 = var8;
         }

         this.log.error("Unexpected exception leasing connection from pool", (Throwable)var7);
         throw new InterruptedException();
      } catch (TimeoutException var9) {
         throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
      }
   }

   @Override
   public void releaseConnection(ManagedClientConnection var1, long var2, TimeUnit var4) {
      Args.check(var1 instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
      ManagedClientConnectionImpl var5 = (ManagedClientConnectionImpl)var1;
      Asserts.check(var5.getManager() == this, "Connection not obtained from this manager");
      synchronized (var5) {
         HttpPoolEntry var7 = var5.detach();
         if (var7 != null) {
            try {
               if (var5.isOpen() && !var5.isMarkedReusable()) {
                  try {
                     var5.shutdown();
                  } catch (IOException var14) {
                     if (this.log.isDebugEnabled()) {
                        this.log.debug("I/O exception shutting down released connection", var14);
                     }
                  }
               }

               if (var5.isMarkedReusable()) {
                  var7.updateExpiry(var2, var4 != null ? var4 : TimeUnit.MILLISECONDS);
                  if (this.log.isDebugEnabled()) {
                     String var8;
                     if (var2 > 0L) {
                        var8 = "for " + var2 + " " + var4;
                     } else {
                        var8 = "indefinitely";
                     }

                     this.log.debug("Connection " + this.format(var7) + " can be kept alive " + var8);
                  }
               }
            } finally {
               this.pool.release(var7, var5.isMarkedReusable());
            }

            if (this.log.isDebugEnabled()) {
               this.log.debug("Connection released: " + this.format(var7) + this.formatStats(var7.getRoute()));
            }
         }
      }
   }

   @Override
   public void shutdown() {
      this.log.debug("Connection manager is shutting down");

      try {
         this.pool.shutdown();
      } catch (IOException var2) {
         this.log.debug("I/O exception shutting down connection manager", var2);
      }

      this.log.debug("Connection manager shut down");
   }

   @Override
   public void closeIdleConnections(long var1, TimeUnit var3) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("Closing connections idle longer than " + var1 + " " + var3);
      }

      this.pool.closeIdle(var1, var3);
   }

   @Override
   public void closeExpiredConnections() {
      this.log.debug("Closing expired connections");
      this.pool.closeExpired();
   }

   @Override
   public int getMaxTotal() {
      return this.pool.getMaxTotal();
   }

   @Override
   public void setMaxTotal(int var1) {
      this.pool.setMaxTotal(var1);
   }

   @Override
   public int getDefaultMaxPerRoute() {
      return this.pool.getDefaultMaxPerRoute();
   }

   @Override
   public void setDefaultMaxPerRoute(int var1) {
      this.pool.setDefaultMaxPerRoute(var1);
   }

   public int getMaxPerRoute(HttpRoute var1) {
      return this.pool.getMaxPerRoute(var1);
   }

   public void setMaxPerRoute(HttpRoute var1, int var2) {
      this.pool.setMaxPerRoute(var1, var2);
   }

   @Override
   public PoolStats getTotalStats() {
      return this.pool.getTotalStats();
   }

   public PoolStats getStats(HttpRoute var1) {
      return this.pool.getStats(var1);
   }
}
