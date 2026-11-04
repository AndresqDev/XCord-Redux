package org.apache.http.pool;

import java.util.concurrent.TimeUnit;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public abstract class PoolEntry<T, C> {
   private final String id;
   private final T route;
   private final C conn;
   private final long created;
   private final long validityDeadline;
   private long updated;
   private long expiry;
   private volatile Object state;

   public PoolEntry(String var1, T var2, C var3, long var4, TimeUnit var6) {
      Args.notNull(var2, "Route");
      Args.notNull(var3, "Connection");
      Args.notNull(var6, "Time unit");
      this.id = var1;
      this.route = (T)var2;
      this.conn = (C)var3;
      this.created = System.currentTimeMillis();
      this.updated = this.created;
      if (var4 > 0L) {
         long var7 = this.created + var6.toMillis(var4);
         this.validityDeadline = var7 > 0L ? var7 : Long.MAX_VALUE;
      } else {
         this.validityDeadline = Long.MAX_VALUE;
      }

      this.expiry = this.validityDeadline;
   }

   public PoolEntry(String var1, T var2, C var3) {
      this(var1, (T)var2, (C)var3, 0L, TimeUnit.MILLISECONDS);
   }

   public String getId() {
      return this.id;
   }

   public T getRoute() {
      return this.route;
   }

   public C getConnection() {
      return this.conn;
   }

   public long getCreated() {
      return this.created;
   }

   public long getValidityDeadline() {
      return this.validityDeadline;
   }

   @Deprecated
   public long getValidUnit() {
      return this.validityDeadline;
   }

   public Object getState() {
      return this.state;
   }

   public void setState(Object var1) {
      this.state = var1;
   }

   public synchronized long getUpdated() {
      return this.updated;
   }

   public synchronized long getExpiry() {
      return this.expiry;
   }

   public synchronized void updateExpiry(long var1, TimeUnit var3) {
      Args.notNull(var3, "Time unit");
      this.updated = System.currentTimeMillis();
      long var4;
      if (var1 > 0L) {
         var4 = this.updated + var3.toMillis(var1);
      } else {
         var4 = Long.MAX_VALUE;
      }

      this.expiry = Math.min(var4, this.validityDeadline);
   }

   public synchronized boolean isExpired(long var1) {
      return var1 >= this.expiry;
   }

   public abstract void close();

   public abstract boolean isClosed();

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[id:");
      var1.append(this.id);
      var1.append("][route:");
      var1.append(this.route);
      var1.append("][state:");
      var1.append(this.state);
      var1.append("]");
      return var1.toString();
   }
}
