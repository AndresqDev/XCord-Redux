package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SingleClientConnManager implements ClientConnectionManager {
   private final Log log = LogFactory.getLog(this.getClass());
   public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
   protected final SchemeRegistry schemeRegistry;
   protected final ClientConnectionOperator connOperator;
   protected final boolean alwaysShutDown;
   protected volatile SingleClientConnManager.PoolEntry uniquePoolEntry;
   protected volatile SingleClientConnManager.ConnAdapter managedConn;
   protected volatile long lastReleaseTime;
   protected volatile long connectionExpiresTime;
   protected volatile boolean isShutDown;

   @Deprecated
   public SingleClientConnManager(HttpParams var1, SchemeRegistry var2) {
      this(var2);
   }

   public SingleClientConnManager(SchemeRegistry var1) {
      Args.notNull(var1, "Scheme registry");
      this.schemeRegistry = var1;
      this.connOperator = this.createConnectionOperator(var1);
      this.uniquePoolEntry = new SingleClientConnManager.PoolEntry();
      this.managedConn = null;
      this.lastReleaseTime = -1L;
      this.alwaysShutDown = false;
      this.isShutDown = false;
   }

   public SingleClientConnManager() {
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

   protected final void assertStillUp() throws IllegalStateException {
      Asserts.check(!this.isShutDown, "Manager is shut down");
   }

   @Override
   public final ClientConnectionRequest requestConnection(final HttpRoute var1, final Object var2) {
      return new ClientConnectionRequest() {
         @Override
         public void abortRequest() {
         }

         @Override
         public ManagedClientConnection getConnection(long var1x, TimeUnit var3) {
            return SingleClientConnManager.this.getConnection(var1, var2);
         }
      };
   }

   public ManagedClientConnection getConnection(HttpRoute var1, Object var2) {
      Args.notNull(var1, "Route");
      this.assertStillUp();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Get connection for route " + var1);
      }

      synchronized (this) {
         Asserts.check(
            this.managedConn == null,
            "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one."
         );
         boolean var4 = false;
         boolean var5 = false;
         this.closeExpiredConnections();
         if (this.uniquePoolEntry.connection.isOpen()) {
            RouteTracker var6 = this.uniquePoolEntry.tracker;
            var5 = var6 == null || !var6.toRoute().equals(var1);
         } else {
            var4 = true;
         }

         if (var5) {
            var4 = true;

            try {
               this.uniquePoolEntry.shutdown();
            } catch (IOException var8) {
               this.log.debug("Problem shutting down connection.", var8);
            }
         }

         if (var4) {
            this.uniquePoolEntry = new SingleClientConnManager.PoolEntry();
         }

         this.managedConn = new SingleClientConnManager.ConnAdapter(this.uniquePoolEntry, var1);
         return this.managedConn;
      }
   }

   @Override
   public void releaseConnection(ManagedClientConnection var1, long var2, TimeUnit var4) {
      Args.check(var1 instanceof SingleClientConnManager.ConnAdapter, "Connection class mismatch, connection not obtained from this manager");
      this.assertStillUp();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Releasing connection " + var1);
      }

      SingleClientConnManager.ConnAdapter var5 = (SingleClientConnManager.ConnAdapter)var1;
      synchronized (var5) {
         if (var5.poolEntry != null) {
            ClientConnectionManager var7 = var5.getManager();
            Asserts.check(var7 == this, "Connection not obtained from this manager");

            try {
               if (var5.isOpen() && (this.alwaysShutDown || !var5.isMarkedReusable())) {
                  if (this.log.isDebugEnabled()) {
                     this.log.debug("Released connection open but not reusable.");
                  }

                  var5.shutdown();
               }
            } catch (IOException var22) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Exception shutting down released connection.", var22);
               }
            } finally {
               var5.detach();
               synchronized (this) {
                  this.managedConn = null;
                  this.lastReleaseTime = System.currentTimeMillis();
                  if (var2 > 0L) {
                     this.connectionExpiresTime = var4.toMillis(var2) + this.lastReleaseTime;
                  } else {
                     this.connectionExpiresTime = Long.MAX_VALUE;
                  }
               }
            }
         }
      }
   }

   @Override
   public void closeExpiredConnections() {
      long var1 = this.connectionExpiresTime;
      if (System.currentTimeMillis() >= var1) {
         this.closeIdleConnections(0L, TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public void closeIdleConnections(long var1, TimeUnit var3) {
      this.assertStillUp();
      Args.notNull(var3, "Time unit");
      synchronized (this) {
         if (this.managedConn == null && this.uniquePoolEntry.connection.isOpen()) {
            long var5 = System.currentTimeMillis() - var3.toMillis(var1);
            if (this.lastReleaseTime <= var5) {
               try {
                  this.uniquePoolEntry.close();
               } catch (IOException var9) {
                  this.log.debug("Problem closing idle connection.", var9);
               }
            }
         }
      }
   }

   @Override
   public void shutdown() {
      this.isShutDown = true;
      synchronized (this) {
         try {
            if (this.uniquePoolEntry != null) {
               this.uniquePoolEntry.shutdown();
            }
         } catch (IOException var8) {
            this.log.debug("Problem while shutting down manager.", var8);
         } finally {
            this.uniquePoolEntry = null;
            this.managedConn = null;
         }
      }
   }

   protected void revokeConnection() {
      SingleClientConnManager.ConnAdapter var1 = this.managedConn;
      if (var1 != null) {
         var1.detach();
         synchronized (this) {
            try {
               this.uniquePoolEntry.shutdown();
            } catch (IOException var5) {
               this.log.debug("Problem while shutting down connection.", var5);
            }
         }
      }
   }

   protected class ConnAdapter extends AbstractPooledConnAdapter {
      protected ConnAdapter(SingleClientConnManager.PoolEntry var2, HttpRoute var3) {
         super(SingleClientConnManager.this, var2);
         this.markReusable();
         var2.route = var3;
      }
   }

   protected class PoolEntry extends AbstractPoolEntry {
      protected PoolEntry() {
         super(SingleClientConnManager.this.connOperator, null);
      }

      protected void close() throws IOException {
         this.shutdownEntry();
         if (this.connection.isOpen()) {
            this.connection.close();
         }
      }

      protected void shutdown() throws IOException {
         this.shutdownEntry();
         if (this.connection.isOpen()) {
            this.connection.shutdown();
         }
      }
   }
}
