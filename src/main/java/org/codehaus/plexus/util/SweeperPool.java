package org.codehaus.plexus.util;

import java.util.ArrayList;

public class SweeperPool {
   private static final boolean DEBUG = false;
   private transient SweeperPool.Sweeper sweeper;
   private transient int maxSize;
   private transient int minSize;
   private int triggerSize;
   private ArrayList<Object> pooledObjects;
   private boolean shuttingDown = false;

   public SweeperPool(int var1, int var2, int var3, int var4, int var5) {
      this.maxSize = this.saneConvert(var1);
      this.minSize = this.saneConvert(var2);
      this.triggerSize = this.saneConvert(var5);
      this.pooledObjects = new ArrayList<>(var3);
      if (var4 > 0) {
         this.sweeper = new SweeperPool.Sweeper(this, var4);
         this.sweeper.start();
      }
   }

   private int saneConvert(int var1) {
      return var1 < 0 ? 0 : var1;
   }

   public synchronized Object get() {
      if (this.pooledObjects.size() != 0 && !this.shuttingDown) {
         Object var1 = this.pooledObjects.remove(0);
         this.objectRetrieved(var1);
         return var1;
      } else {
         return null;
      }
   }

   public synchronized boolean put(Object var1) {
      this.objectAdded(var1);
      if (var1 != null && this.pooledObjects.size() < this.maxSize && !this.shuttingDown) {
         this.pooledObjects.add(var1);
         return true;
      } else {
         if (var1 != null) {
            this.objectDisposed(var1);
         }

         return false;
      }
   }

   public synchronized int getSize() {
      return this.pooledObjects.size();
   }

   public void dispose() {
      this.shuttingDown = true;
      if (this.sweeper != null) {
         this.sweeper.stop();

         try {
            this.sweeper.join();
         } catch (InterruptedException var8) {
            System.err.println("Unexpected exception occurred: ");
            var8.printStackTrace();
         }
      }

      synchronized (this) {
         Object[] var2 = this.pooledObjects.toArray();

         for (Object var6 : var2) {
            this.objectDisposed(var6);
         }

         this.pooledObjects.clear();
      }
   }

   boolean isDisposed() {
      if (!this.shuttingDown) {
         return false;
      } else {
         return this.sweeper == null ? true : this.sweeper.hasStopped();
      }
   }

   public synchronized void trim() {
      if (this.triggerSize > 0 && this.pooledObjects.size() >= this.triggerSize || this.maxSize > 0 && this.pooledObjects.size() >= this.maxSize) {
         while (this.pooledObjects.size() > this.minSize) {
            this.objectDisposed(this.pooledObjects.remove(0));
         }
      }
   }

   public void objectDisposed(Object var1) {
   }

   public void objectAdded(Object var1) {
   }

   public void objectRetrieved(Object var1) {
   }

   private static class Sweeper implements Runnable {
      private final transient SweeperPool pool;
      private transient boolean service = false;
      private final transient int sweepInterval;
      private transient Thread t = null;

      public Sweeper(SweeperPool var1, int var2) {
         this.sweepInterval = var2;
         this.pool = var1;
      }

      @Override
      public void run() {
         this.debug("started");
         if (this.sweepInterval > 0) {
            synchronized (this) {
               for (; this.service; this.runSweep()) {
                  try {
                     this.wait((long)(this.sweepInterval * 1000));
                  } catch (InterruptedException var4) {
                  }
               }
            }
         }

         this.debug("stopped");
      }

      public void start() {
         if (!this.service) {
            this.service = true;
            this.t = new Thread(this);
            this.t.setName("Sweeper");
            this.t.start();
         }
      }

      public synchronized void stop() {
         this.service = false;
         this.notifyAll();
      }

      void join() throws InterruptedException {
         this.t.join();
      }

      boolean hasStopped() {
         return !this.service && !this.t.isAlive();
      }

      private final void debug(String var1) {
      }

      private void runSweep() {
         this.debug("runningSweep. time=" + System.currentTimeMillis());
         this.pool.trim();
      }
   }
}
