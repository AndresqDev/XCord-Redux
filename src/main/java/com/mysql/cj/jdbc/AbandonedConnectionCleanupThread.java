package com.mysql.cj.jdbc;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.protocol.NetworkResources;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbandonedConnectionCleanupThread implements Runnable {
   private static final Set<AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference> connectionFinalizerPhantomRefs = ConcurrentHashMap.newKeySet();
   private static final ReferenceQueue<MysqlConnection> referenceQueue = new ReferenceQueue<>();
   private static final ExecutorService cleanupThreadExecutorService;
   private static Thread threadRef = null;
   private static Lock threadRefLock = new ReentrantLock();
   private static boolean abandonedConnectionCleanupDisabled = Boolean.getBoolean("com.mysql.cj.disableAbandonedConnectionCleanup");

   private AbandonedConnectionCleanupThread() {
   }

   @Override
   public void run() {
      while (true) {
         try {
            this.checkThreadContextClassLoader();
            Reference<? extends MysqlConnection> reference = referenceQueue.remove(5000L);
            if (reference != null) {
               finalizeResource((AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference)reference);
            }
         } catch (InterruptedException var8) {
            threadRefLock.lock();

            try {
               threadRef = null;

               Reference<? extends MysqlConnection> referencex;
               while ((referencex = referenceQueue.poll()) != null) {
                  finalizeResource((AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference)referencex);
               }

               connectionFinalizerPhantomRefs.clear();
            } finally {
               threadRefLock.unlock();
            }

            return;
         } catch (Exception var9) {
         }
      }
   }

   private void checkThreadContextClassLoader() {
      try {
         threadRef.getContextClassLoader().getResource("");
      } catch (Throwable var2) {
         uncheckedShutdown();
      }
   }

   private static boolean consistentClassLoaders() {
      threadRefLock.lock();

      boolean callerCtxClassLoader;
      try {
         if (threadRef != null) {
            ClassLoader callerCtxClassLoaderx = Thread.currentThread().getContextClassLoader();
            ClassLoader threadCtxClassLoader = threadRef.getContextClassLoader();
            return callerCtxClassLoaderx != null && threadCtxClassLoader != null && callerCtxClassLoaderx == threadCtxClassLoader;
         }

         callerCtxClassLoader = false;
      } finally {
         threadRefLock.unlock();
      }

      return callerCtxClassLoader;
   }

   private static void shutdown(boolean checked) {
      if (!checked || consistentClassLoaders()) {
         if (cleanupThreadExecutorService != null) {
            cleanupThreadExecutorService.shutdownNow();
         }
      }
   }

   public static void checkedShutdown() {
      shutdown(true);
   }

   public static void uncheckedShutdown() {
      shutdown(false);
   }

   public static boolean isAlive() {
      threadRefLock.lock();

      boolean var0;
      try {
         var0 = threadRef != null && threadRef.isAlive();
      } finally {
         threadRefLock.unlock();
      }

      return var0;
   }

   protected static void trackConnection(MysqlConnection conn, NetworkResources io) {
      if (!abandonedConnectionCleanupDisabled) {
         threadRefLock.lock();

         try {
            if (isAlive()) {
               AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference reference = new AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference(
                  conn, io, referenceQueue
               );
               connectionFinalizerPhantomRefs.add(reference);
            }
         } finally {
            threadRefLock.unlock();
         }
      }
   }

   private static void finalizeResource(AbandonedConnectionCleanupThread.ConnectionFinalizerPhantomReference reference) {
      try {
         reference.finalizeResources();
         reference.clear();
      } finally {
         connectionFinalizerPhantomRefs.remove(reference);
      }
   }

   static {
      if (abandonedConnectionCleanupDisabled) {
         cleanupThreadExecutorService = null;
      } else {
         cleanupThreadExecutorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "mysql-cj-abandoned-connection-cleanup");
            t.setDaemon(true);
            ClassLoader classLoader = AbandonedConnectionCleanupThread.class.getClassLoader();
            if (classLoader == null) {
               classLoader = ClassLoader.getSystemClassLoader();
            }

            t.setContextClassLoader(classLoader);
            threadRef = t;
            return t;
         });
         cleanupThreadExecutorService.execute(new AbandonedConnectionCleanupThread());
      }
   }

   private static class ConnectionFinalizerPhantomReference extends PhantomReference<MysqlConnection> {
      private NetworkResources networkResources;

      ConnectionFinalizerPhantomReference(MysqlConnection conn, NetworkResources networkResources, ReferenceQueue<? super MysqlConnection> refQueue) {
         super(conn, refQueue);
         this.networkResources = networkResources;
      }

      void finalizeResources() {
         if (this.networkResources != null) {
            try {
               this.networkResources.forceClose();
            } finally {
               this.networkResources = null;
            }
         }
      }
   }
}
