package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpConnection;

@Deprecated
public class IdleConnectionHandler {
   private final Log log = LogFactory.getLog(this.getClass());
   private final Map<HttpConnection, IdleConnectionHandler.TimeValues> connectionToTimes = new HashMap<>();

   public void add(HttpConnection var1, long var2, TimeUnit var4) {
      long var5 = System.currentTimeMillis();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Adding connection at: " + var5);
      }

      this.connectionToTimes.put(var1, new IdleConnectionHandler.TimeValues(var5, var2, var4));
   }

   public boolean remove(HttpConnection var1) {
      IdleConnectionHandler.TimeValues var2 = this.connectionToTimes.remove(var1);
      if (var2 == null) {
         this.log.warn("Removing a connection that never existed!");
         return true;
      } else {
         return System.currentTimeMillis() <= var2.timeExpires;
      }
   }

   public void removeAll() {
      this.connectionToTimes.clear();
   }

   public void closeIdleConnections(long var1) {
      long var3 = System.currentTimeMillis() - var1;
      if (this.log.isDebugEnabled()) {
         this.log.debug("Checking for connections, idle timeout: " + var3);
      }

      for (Entry var6 : this.connectionToTimes.entrySet()) {
         HttpConnection var7 = (HttpConnection)var6.getKey();
         IdleConnectionHandler.TimeValues var8 = (IdleConnectionHandler.TimeValues)var6.getValue();
         long var9 = var8.timeAdded;
         if (var9 <= var3) {
            if (this.log.isDebugEnabled()) {
               this.log.debug("Closing idle connection, connection time: " + var9);
            }

            try {
               var7.close();
            } catch (IOException var12) {
               this.log.debug("I/O error closing connection", var12);
            }
         }
      }
   }

   public void closeExpiredConnections() {
      long var1 = System.currentTimeMillis();
      if (this.log.isDebugEnabled()) {
         this.log.debug("Checking for expired connections, now: " + var1);
      }

      for (Entry var4 : this.connectionToTimes.entrySet()) {
         HttpConnection var5 = (HttpConnection)var4.getKey();
         IdleConnectionHandler.TimeValues var6 = (IdleConnectionHandler.TimeValues)var4.getValue();
         if (var6.timeExpires <= var1) {
            if (this.log.isDebugEnabled()) {
               this.log.debug("Closing connection, expired @: " + var6.timeExpires);
            }

            try {
               var5.close();
            } catch (IOException var8) {
               this.log.debug("I/O error closing connection", var8);
            }
         }
      }
   }

   private static class TimeValues {
      private final long timeAdded;
      private final long timeExpires;

      TimeValues(long var1, long var3, TimeUnit var5) {
         this.timeAdded = var1;
         if (var3 > 0L) {
            this.timeExpires = var1 + var5.toMillis(var3);
         } else {
            this.timeExpires = Long.MAX_VALUE;
         }
      }
   }
}
