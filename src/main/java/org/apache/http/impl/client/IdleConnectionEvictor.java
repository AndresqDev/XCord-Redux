package org.apache.http.impl.client;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.util.Args;

public final class IdleConnectionEvictor {
   private final HttpClientConnectionManager connectionManager;
   private final ThreadFactory threadFactory;
   private final Thread thread;
   private final long sleepTimeMs;
   private final long maxIdleTimeMs;
   private volatile Exception exception;

   public IdleConnectionEvictor(final HttpClientConnectionManager var1, ThreadFactory var2, long var3, TimeUnit var5, long var6, TimeUnit var8) {
      this.connectionManager = Args.notNull(var1, "Connection manager");
      this.threadFactory = (ThreadFactory)(var2 != null ? var2 : new IdleConnectionEvictor.DefaultThreadFactory());
      this.sleepTimeMs = var5 != null ? var5.toMillis(var3) : var3;
      this.maxIdleTimeMs = var8 != null ? var8.toMillis(var6) : var6;
      this.thread = this.threadFactory.newThread(new Runnable() {
         @Override
         public void run() {
            try {
               while (!Thread.currentThread().isInterrupted()) {
                  Thread.sleep(IdleConnectionEvictor.this.sleepTimeMs);
                  var1.closeExpiredConnections();
                  if (IdleConnectionEvictor.this.maxIdleTimeMs > 0L) {
                     var1.closeIdleConnections(IdleConnectionEvictor.this.maxIdleTimeMs, TimeUnit.MILLISECONDS);
                  }
               }
            } catch (Exception var2) {
               IdleConnectionEvictor.this.exception = var2;
            }
         }
      });
   }

   public IdleConnectionEvictor(HttpClientConnectionManager var1, long var2, TimeUnit var4, long var5, TimeUnit var7) {
      this(var1, null, var2, var4, var5, var7);
   }

   public IdleConnectionEvictor(HttpClientConnectionManager var1, long var2, TimeUnit var4) {
      this(var1, null, var2 > 0L ? var2 : 5L, var4 != null ? var4 : TimeUnit.SECONDS, var2, var4);
   }

   public void start() {
      this.thread.start();
   }

   public void shutdown() {
      this.thread.interrupt();
   }

   public boolean isRunning() {
      return this.thread.isAlive();
   }

   public void awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      this.thread.join((var3 != null ? var3 : TimeUnit.MILLISECONDS).toMillis(var1));
   }

   static class DefaultThreadFactory implements ThreadFactory {
      @Override
      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(var1, "Connection evictor");
         var2.setDaemon(true);
         return var2;
      }
   }
}
