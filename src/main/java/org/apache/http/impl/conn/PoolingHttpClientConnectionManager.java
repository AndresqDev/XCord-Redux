package org.apache.http.impl.conn;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.pool.ConnFactory;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryCallback;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public class PoolingHttpClientConnectionManager implements Closeable, HttpClientConnectionManager, ConnPoolControl<HttpRoute> {
   private final Log log = LogFactory.getLog(this.getClass());
   private final PoolingHttpClientConnectionManager.ConfigData configData = new PoolingHttpClientConnectionManager.ConfigData();
   private final CPool pool;
   private final HttpClientConnectionOperator connectionOperator;
   private final AtomicBoolean isShutDown;

   private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
      return RegistryBuilder.<ConnectionSocketFactory>create()
         .register("http", PlainConnectionSocketFactory.getSocketFactory())
         .register("https", SSLConnectionSocketFactory.getSocketFactory())
         .build();
   }

   public PoolingHttpClientConnectionManager() {
      this(getDefaultRegistry());
   }

   public PoolingHttpClientConnectionManager(long var1, TimeUnit var3) {
      this(getDefaultRegistry(), null, null, null, var1, var3);
   }

   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> var1) {
      this(var1, null, null);
   }

   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> var1, DnsResolver var2) {
      this(var1, null, var2);
   }

   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> var1, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var2) {
      this(var1, var2, null);
   }

   public PoolingHttpClientConnectionManager(HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var1) {
      this(getDefaultRegistry(), var1, null);
   }

   public PoolingHttpClientConnectionManager(
      Registry<ConnectionSocketFactory> var1, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var2, DnsResolver var3
   ) {
      this(var1, var2, null, var3, -1L, TimeUnit.MILLISECONDS);
   }

   public PoolingHttpClientConnectionManager(
      Registry<ConnectionSocketFactory> var1,
      HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var2,
      SchemePortResolver var3,
      DnsResolver var4,
      long var5,
      TimeUnit var7
   ) {
      this(new DefaultHttpClientConnectionOperator(var1, var3, var4), var2, var5, var7);
   }

   public PoolingHttpClientConnectionManager(
      HttpClientConnectionOperator var1, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var2, long var3, TimeUnit var5
   ) {
      this.pool = new CPool(new PoolingHttpClientConnectionManager.InternalConnectionFactory(this.configData, var2), 2, 20, var3, var5);
      this.pool.setValidateAfterInactivity(2000);
      this.connectionOperator = Args.notNull(var1, "HttpClientConnectionOperator");
      this.isShutDown = new AtomicBoolean(false);
   }

   PoolingHttpClientConnectionManager(CPool var1, Lookup<ConnectionSocketFactory> var2, SchemePortResolver var3, DnsResolver var4) {
      this.pool = var1;
      this.connectionOperator = new DefaultHttpClientConnectionOperator(var2, var3, var4);
      this.isShutDown = new AtomicBoolean(false);
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }
   }

   @Override
   public void close() {
      this.shutdown();
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
      var2.append("[total available: ").append(var3.getAvailable()).append("; ");
      var2.append("route allocated: ").append(var4.getLeased() + var4.getAvailable());
      var2.append(" of ").append(var4.getMax()).append("; ");
      var2.append("total allocated: ").append(var3.getLeased() + var3.getAvailable());
      var2.append(" of ").append(var3.getMax()).append("]");
      return var2.toString();
   }

   private String format(CPoolEntry var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("[id: ").append(var1.getId()).append("]");
      var2.append("[route: ").append(var1.getRoute()).append("]");
      Object var3 = var1.getState();
      if (var3 != null) {
         var2.append("[state: ").append(var3).append("]");
      }

      return var2.toString();
   }

   private SocketConfig resolveSocketConfig(HttpHost var1) {
      SocketConfig var2 = this.configData.getSocketConfig(var1);
      if (var2 == null) {
         var2 = this.configData.getDefaultSocketConfig();
      }

      if (var2 == null) {
         var2 = SocketConfig.DEFAULT;
      }

      return var2;
   }

   @Override
   public ConnectionRequest requestConnection(final HttpRoute var1, Object var2) {
      Args.notNull(var1, "HTTP route");
      if (this.log.isDebugEnabled()) {
         this.log.debug("Connection request: " + this.format(var1, var2) + this.formatStats(var1));
      }

      Asserts.check(!this.isShutDown.get(), "Connection pool shut down");
      final Future var3 = this.pool.lease(var1, var2, null);
      return new ConnectionRequest() {
         @Override
         public boolean cancel() {
            return var3.cancel(true);
         }

         @Override
         public HttpClientConnection get(long var1x, TimeUnit var3x) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
            HttpClientConnection var4 = PoolingHttpClientConnectionManager.this.leaseConnection(var3, var1x, var3x);
            if (var4.isOpen()) {
               HttpHost var5;
               if (var1.getProxyHost() != null) {
                  var5 = var1.getProxyHost();
               } else {
                  var5 = var1.getTargetHost();
               }

               SocketConfig var6 = PoolingHttpClientConnectionManager.this.resolveSocketConfig(var5);
               var4.setSocketTimeout(var6.getSoTimeout());
            }

            return var4;
         }
      };
   }

   protected HttpClientConnection leaseConnection(Future<CPoolEntry> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
      try {
         CPoolEntry var5 = (CPoolEntry)var1.get(var2, var4);
         if (var5 != null && !var1.isCancelled()) {
            Asserts.check(var5.getConnection() != null, "Pool entry with no connection");
            if (this.log.isDebugEnabled()) {
               this.log.debug("Connection leased: " + this.format(var5) + this.formatStats(var5.getRoute()));
            }

            return CPoolProxy.newProxy(var5);
         } else {
            throw new ExecutionException(new CancellationException("Operation cancelled"));
         }
      } catch (TimeoutException var7) {
         throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
      }
   }

   @Override
   public void releaseConnection(HttpClientConnection var1, Object var2, long var3, TimeUnit var5) {
      Args.notNull(var1, "Managed connection");
      synchronized (var1) {
         CPoolEntry var7 = CPoolProxy.detach(var1);
         if (var7 != null) {
            ManagedHttpClientConnection var8 = var7.getConnection();

            try {
               if (var8.isOpen()) {
                  TimeUnit var9 = var5 != null ? var5 : TimeUnit.MILLISECONDS;
                  var7.setState(var2);
                  var7.updateExpiry(var3, var9);
                  if (this.log.isDebugEnabled()) {
                     String var10;
                     if (var3 > 0L) {
                        var10 = "for " + (double)var9.toMillis(var3) / 1000.0 + " seconds";
                     } else {
                        var10 = "indefinitely";
                     }

                     this.log.debug("Connection " + this.format(var7) + " can be kept alive " + var10);
                  }

                  var8.setSocketTimeout(0);
               }
            } finally {
               this.pool.release(var7, var8.isOpen() && var7.isRouteComplete());
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Connection released: " + this.format(var7) + this.formatStats(var7.getRoute()));
               }
            }
         }
      }
   }

   @Override
   public void connect(HttpClientConnection var1, HttpRoute var2, int var3, HttpContext var4) throws IOException {
      Args.notNull(var1, "Managed Connection");
      Args.notNull(var2, "HTTP route");
      ManagedHttpClientConnection var5;
      synchronized (var1) {
         CPoolEntry var7 = CPoolProxy.getPoolEntry(var1);
         var5 = var7.getConnection();
      }

      HttpHost var6;
      if (var2.getProxyHost() != null) {
         var6 = var2.getProxyHost();
      } else {
         var6 = var2.getTargetHost();
      }

      this.connectionOperator.connect(var5, var6, var2.getLocalSocketAddress(), var3, this.resolveSocketConfig(var6), var4);
   }

   @Override
   public void upgrade(HttpClientConnection var1, HttpRoute var2, HttpContext var3) throws IOException {
      Args.notNull(var1, "Managed Connection");
      Args.notNull(var2, "HTTP route");
      ManagedHttpClientConnection var4;
      synchronized (var1) {
         CPoolEntry var6 = CPoolProxy.getPoolEntry(var1);
         var4 = var6.getConnection();
      }

      this.connectionOperator.upgrade(var4, var2.getTargetHost(), var3);
   }

   @Override
   public void routeComplete(HttpClientConnection var1, HttpRoute var2, HttpContext var3) throws IOException {
      Args.notNull(var1, "Managed Connection");
      Args.notNull(var2, "HTTP route");
      synchronized (var1) {
         CPoolEntry var5 = CPoolProxy.getPoolEntry(var1);
         var5.markRouteComplete();
      }
   }

   @Override
   public void shutdown() {
      if (this.isShutDown.compareAndSet(false, true)) {
         this.log.debug("Connection manager is shutting down");

         try {
            this.pool.enumLeased(new PoolEntryCallback<HttpRoute, ManagedHttpClientConnection>() {
               @Override
               public void process(PoolEntry<HttpRoute, ManagedHttpClientConnection> var1) {
                  ManagedHttpClientConnection var2 = (ManagedHttpClientConnection)var1.getConnection();
                  if (var2 != null) {
                     try {
                        var2.shutdown();
                     } catch (IOException var4) {
                        if (PoolingHttpClientConnectionManager.this.log.isDebugEnabled()) {
                           PoolingHttpClientConnectionManager.this.log.debug("I/O exception shutting down connection", var4);
                        }
                     }
                  }
               }
            });
            this.pool.shutdown();
         } catch (IOException var2) {
            this.log.debug("I/O exception shutting down connection manager", var2);
         }

         this.log.debug("Connection manager shut down");
      }
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

   protected void enumAvailable(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> var1) {
      this.pool.enumAvailable(var1);
   }

   protected void enumLeased(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> var1) {
      this.pool.enumLeased(var1);
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

   public Set<HttpRoute> getRoutes() {
      return this.pool.getRoutes();
   }

   public SocketConfig getDefaultSocketConfig() {
      return this.configData.getDefaultSocketConfig();
   }

   public void setDefaultSocketConfig(SocketConfig var1) {
      this.configData.setDefaultSocketConfig(var1);
   }

   public ConnectionConfig getDefaultConnectionConfig() {
      return this.configData.getDefaultConnectionConfig();
   }

   public void setDefaultConnectionConfig(ConnectionConfig var1) {
      this.configData.setDefaultConnectionConfig(var1);
   }

   public SocketConfig getSocketConfig(HttpHost var1) {
      return this.configData.getSocketConfig(var1);
   }

   public void setSocketConfig(HttpHost var1, SocketConfig var2) {
      this.configData.setSocketConfig(var1, var2);
   }

   public ConnectionConfig getConnectionConfig(HttpHost var1) {
      return this.configData.getConnectionConfig(var1);
   }

   public void setConnectionConfig(HttpHost var1, ConnectionConfig var2) {
      this.configData.setConnectionConfig(var1, var2);
   }

   public int getValidateAfterInactivity() {
      return this.pool.getValidateAfterInactivity();
   }

   public void setValidateAfterInactivity(int var1) {
      this.pool.setValidateAfterInactivity(var1);
   }

   static class ConfigData {
      private final Map<HttpHost, SocketConfig> socketConfigMap = new ConcurrentHashMap<>();
      private final Map<HttpHost, ConnectionConfig> connectionConfigMap = new ConcurrentHashMap<>();
      private volatile SocketConfig defaultSocketConfig;
      private volatile ConnectionConfig defaultConnectionConfig;

      public SocketConfig getDefaultSocketConfig() {
         return this.defaultSocketConfig;
      }

      public void setDefaultSocketConfig(SocketConfig var1) {
         this.defaultSocketConfig = var1;
      }

      public ConnectionConfig getDefaultConnectionConfig() {
         return this.defaultConnectionConfig;
      }

      public void setDefaultConnectionConfig(ConnectionConfig var1) {
         this.defaultConnectionConfig = var1;
      }

      public SocketConfig getSocketConfig(HttpHost var1) {
         return this.socketConfigMap.get(var1);
      }

      public void setSocketConfig(HttpHost var1, SocketConfig var2) {
         this.socketConfigMap.put(var1, var2);
      }

      public ConnectionConfig getConnectionConfig(HttpHost var1) {
         return this.connectionConfigMap.get(var1);
      }

      public void setConnectionConfig(HttpHost var1, ConnectionConfig var2) {
         this.connectionConfigMap.put(var1, var2);
      }
   }

   static class InternalConnectionFactory implements ConnFactory<HttpRoute, ManagedHttpClientConnection> {
      private final PoolingHttpClientConnectionManager.ConfigData configData;
      private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;

      InternalConnectionFactory(PoolingHttpClientConnectionManager.ConfigData var1, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> var2) {
         this.configData = var1 != null ? var1 : new PoolingHttpClientConnectionManager.ConfigData();
         this.connFactory = (HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>)(var2 != null ? var2 : ManagedHttpClientConnectionFactory.INSTANCE);
      }

      public ManagedHttpClientConnection create(HttpRoute var1) throws IOException {
         ConnectionConfig var2 = null;
         if (var1.getProxyHost() != null) {
            var2 = this.configData.getConnectionConfig(var1.getProxyHost());
         }

         if (var2 == null) {
            var2 = this.configData.getConnectionConfig(var1.getTargetHost());
         }

         if (var2 == null) {
            var2 = this.configData.getDefaultConnectionConfig();
         }

         if (var2 == null) {
            var2 = ConnectionConfig.DEFAULT;
         }

         return this.connFactory.create(var1, var2);
      }
   }
}
