package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicClientConnectionManager implements ClientConnectionManager {
   private final Log log = LogFactory.getLog(this.getClass());
   private static final AtomicLong COUNTER = new AtomicLong();
   public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
   private final SchemeRegistry schemeRegistry;
   private final ClientConnectionOperator connOperator;
   private HttpPoolEntry poolEntry;
   private ManagedClientConnectionImpl conn;
   private volatile boolean shutdown;

   public BasicClientConnectionManager(SchemeRegistry var1) {
      Args.notNull(var1, "Scheme registry");
      this.schemeRegistry = var1;
      this.connOperator = this.createConnectionOperator(var1);
   }

   public BasicClientConnectionManager() {
      this(SchemeRegistryFactory.createDefault());
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
   public SchemeRegistry getSchemeRegistry() {
      return this.schemeRegistry;
   }

   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry var1) {
      return new DefaultClientConnectionOperator(var1);
   }

   @Override
   public final ClientConnectionRequest requestConnection(final HttpRoute var1, final Object var2) {
      return new ClientConnectionRequest() {
         @Override
         public void abortRequest() {
         }

         @Override
         public ManagedClientConnection getConnection(long var1x, TimeUnit var3) {
            return BasicClientConnectionManager.this.getConnection(var1, var2);
         }
      };
   }

   private void assertNotShutdown() {
      Asserts.check(!this.shutdown, "Connection manager has been shut down");
   }

   ManagedClientConnection getConnection(HttpRoute var1, Object var2) {
      Args.notNull(var1, "Route");
      synchronized (this) {
         this.assertNotShutdown();
         if (this.log.isDebugEnabled()) {
            this.log.debug("Get connection for route " + var1);
         }

         Asserts.check(
            this.conn == null,
            "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one."
         );
         if (this.poolEntry != null && !this.poolEntry.getPlannedRoute().equals(var1)) {
            this.poolEntry.close();
            this.poolEntry = null;
         }

         if (this.poolEntry == null) {
            String var4 = Long.toString(COUNTER.getAndIncrement());
            OperatedClientConnection var5 = this.connOperator.createConnection();
            this.poolEntry = new HttpPoolEntry(this.log, var4, var1, var5, 0L, TimeUnit.MILLISECONDS);
         }

         long var8 = System.currentTimeMillis();
         if (this.poolEntry.isExpired(var8)) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }

         this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
         return this.conn;
      }
   }

   private void shutdownConnection(HttpClientConnection var1) {
      try {
         var1.shutdown();
      } catch (IOException var3) {
         if (this.log.isDebugEnabled()) {
            this.log.debug("I/O exception shutting down connection", var3);
         }
      }
   }

   @Override
   public void releaseConnection(ManagedClientConnection var1, long var2, TimeUnit var4) {
      Args.check(var1 instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
      ManagedClientConnectionImpl var5 = (ManagedClientConnectionImpl)var1;
      synchronized (var5) {
         if (this.log.isDebugEnabled()) {
            this.log.debug("Releasing connection " + var1);
         }

         if (var5.getPoolEntry() != null) {
            ClientConnectionManager var7 = var5.getManager();
            Asserts.check(var7 == this, "Connection not obtained from this manager");
            synchronized (this) {
               if (this.shutdown) {
                  this.shutdownConnection(var5);
                  return;
               }

               try {
                  if (var5.isOpen() && !var5.isMarkedReusable()) {
                     this.shutdownConnection(var5);
                  }

                  if (var5.isMarkedReusable()) {
                     this.poolEntry.updateExpiry(var2, var4 != null ? var4 : TimeUnit.MILLISECONDS);
                     if (this.log.isDebugEnabled()) {
                        String var9;
                        if (var2 > 0L) {
                           var9 = "for " + var2 + " " + var4;
                        } else {
                           var9 = "indefinitely";
                        }

                        this.log.debug("Connection can be kept alive " + var9);
                     }
                  }
               } finally {
                  var5.detach();
                  this.conn = null;
                  if (this.poolEntry.isClosed()) {
                     this.poolEntry = null;
                  }
               }
            }
         }
      }
   }

   @Override
   public void closeExpiredConnections() {
      synchronized (this) {
         this.assertNotShutdown();
         long var2 = System.currentTimeMillis();
         if (this.poolEntry != null && this.poolEntry.isExpired(var2)) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }
      }
   }

   @Override
   public void closeIdleConnections(long var1, TimeUnit var3) {
      Args.notNull(var3, "Time unit");
      synchronized (this) {
         this.assertNotShutdown();
         long var5 = var3.toMillis(var1);
         if (var5 < 0L) {
            var5 = 0L;
         }

         long var7 = System.currentTimeMillis() - var5;
         if (this.poolEntry != null && this.poolEntry.getUpdated() <= var7) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }
      }
   }

   @Override
   public void shutdown() {
      synchronized (this) {
         this.shutdown = true;

         try {
            if (this.poolEntry != null) {
               this.poolEntry.close();
            }
         } finally {
            this.poolEntry = null;
            this.conn = null;
         }
      }
   }
}
