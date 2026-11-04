package org.apache.http.impl.execchain;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.HttpClientConnectionManager;

@Contract(
   threading = ThreadingBehavior.SAFE
)
class ConnectionHolder implements Closeable, Cancellable, ConnectionReleaseTrigger {
   private final Log log;
   private final HttpClientConnectionManager manager;
   private final HttpClientConnection managedConn;
   private final AtomicBoolean released;
   private volatile boolean reusable;
   private volatile Object state;
   private volatile long validDuration;
   private volatile TimeUnit timeUnit;

   public ConnectionHolder(Log var1, HttpClientConnectionManager var2, HttpClientConnection var3) {
      this.log = var1;
      this.manager = var2;
      this.managedConn = var3;
      this.released = new AtomicBoolean(false);
   }

   public boolean isReusable() {
      return this.reusable;
   }

   public void markReusable() {
      this.reusable = true;
   }

   public void markNonReusable() {
      this.reusable = false;
   }

   public void setState(Object var1) {
      this.state = var1;
   }

   public void setValidFor(long var1, TimeUnit var3) {
      synchronized (this.managedConn) {
         this.validDuration = var1;
         this.timeUnit = var3;
      }
   }

   private void releaseConnection(boolean var1) {
      if (this.released.compareAndSet(false, true)) {
         synchronized (this.managedConn) {
            if (var1) {
               this.manager.releaseConnection(this.managedConn, this.state, this.validDuration, this.timeUnit);
            } else {
               try {
                  this.managedConn.close();
                  this.log.debug("Connection discarded");
               } catch (IOException var9) {
                  if (this.log.isDebugEnabled()) {
                     this.log.debug(var9.getMessage(), var9);
                  }
               } finally {
                  this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
               }
            }
         }
      }
   }

   @Override
   public void releaseConnection() {
      this.releaseConnection(this.reusable);
   }

   @Override
   public void abortConnection() {
      if (this.released.compareAndSet(false, true)) {
         synchronized (this.managedConn) {
            try {
               this.managedConn.shutdown();
               this.log.debug("Connection discarded");
            } catch (IOException var8) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug(var8.getMessage(), var8);
               }
            } finally {
               this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   @Override
   public boolean cancel() {
      boolean var1 = this.released.get();
      this.log.debug("Cancelling request execution");
      this.abortConnection();
      return !var1;
   }

   public boolean isReleased() {
      return this.released.get();
   }

   @Override
   public void close() throws IOException {
      this.releaseConnection(false);
   }
}
