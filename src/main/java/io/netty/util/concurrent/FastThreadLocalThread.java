package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class FastThreadLocalThread extends Thread {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(FastThreadLocalThread.class);
   private final boolean cleanupFastThreadLocals;
   private InternalThreadLocalMap threadLocalMap;

   public FastThreadLocalThread() {
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable var1) {
      super(FastThreadLocalRunnable.wrap(var1));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2) {
      super(var1, FastThreadLocalRunnable.wrap(var2));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(String var1) {
      super(var1);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(ThreadGroup var1, String var2) {
      super(var1, var2);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable var1, String var2) {
      super(FastThreadLocalRunnable.wrap(var1), var2);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2, String var3) {
      super(var1, FastThreadLocalRunnable.wrap(var2), var3);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup var1, Runnable var2, String var3, long var4) {
      super(var1, FastThreadLocalRunnable.wrap(var2), var3, var4);
      this.cleanupFastThreadLocals = true;
   }

   public final InternalThreadLocalMap threadLocalMap() {
      if (this != Thread.currentThread() && logger.isWarnEnabled()) {
         logger.warn(new RuntimeException("It's not thread-safe to get 'threadLocalMap' which doesn't belong to the caller thread"));
      }

      return this.threadLocalMap;
   }

   public final void setThreadLocalMap(InternalThreadLocalMap var1) {
      if (this != Thread.currentThread() && logger.isWarnEnabled()) {
         logger.warn(new RuntimeException("It's not thread-safe to set 'threadLocalMap' which doesn't belong to the caller thread"));
      }

      this.threadLocalMap = var1;
   }

   public boolean willCleanupFastThreadLocals() {
      return this.cleanupFastThreadLocals;
   }

   public static boolean willCleanupFastThreadLocals(Thread var0) {
      return var0 instanceof FastThreadLocalThread && ((FastThreadLocalThread)var0).willCleanupFastThreadLocals();
   }
}
