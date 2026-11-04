package org.apache.http.pool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>> implements ConnPool<T, E>, ConnPoolControl<T> {
   private final Lock lock;
   private final Condition condition;
   private final ConnFactory<T, C> connFactory;
   private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
   private final Set<E> leased;
   private final LinkedList<E> available;
   private final LinkedList<Future<E>> pending;
   private final Map<T, Integer> maxPerRoute;
   private volatile boolean isShutDown;
   private volatile int defaultMaxPerRoute;
   private volatile int maxTotal;
   private volatile int validateAfterInactivity;

   public AbstractConnPool(ConnFactory<T, C> var1, int var2, int var3) {
      this.connFactory = Args.notNull(var1, "Connection factory");
      this.defaultMaxPerRoute = Args.positive(var2, "Max per route value");
      this.maxTotal = Args.positive(var3, "Max total value");
      this.lock = new ReentrantLock();
      this.condition = this.lock.newCondition();
      this.routeToPool = new HashMap<>();
      this.leased = new HashSet<>();
      this.available = new LinkedList<>();
      this.pending = new LinkedList<>();
      this.maxPerRoute = new HashMap<>();
   }

   protected abstract E createEntry(T var1, C var2);

   protected void onLease(E var1) {
   }

   protected void onRelease(E var1) {
   }

   protected void onReuse(E var1) {
   }

   protected boolean validate(E var1) {
      return true;
   }

   public boolean isShutdown() {
      return this.isShutDown;
   }

   public void shutdown() throws IOException {
      if (!this.isShutDown) {
         this.isShutDown = true;
         this.lock.lock();

         try {
            for (PoolEntry var2 : this.available) {
               var2.close();
            }

            for (PoolEntry var8 : this.leased) {
               var8.close();
            }

            for (RouteSpecificPool var9 : this.routeToPool.values()) {
               var9.shutdown();
            }

            this.routeToPool.clear();
            this.leased.clear();
            this.available.clear();
         } finally {
            this.lock.unlock();
         }
      }
   }

   private RouteSpecificPool<T, C, E> getPool(final T var1) {
      RouteSpecificPool var2 = this.routeToPool.get(var1);
      if (var2 == null) {
         var2 = new RouteSpecificPool<T, C, E>(var1) {
            @Override
            protected E createEntry(C var1x) {
               return (E)AbstractConnPool.this.createEntry(var1, var1x);
            }
         };
         this.routeToPool.put((T)var1, var2);
      }

      return var2;
   }

   private static Exception operationAborted() {
      return new CancellationException("Operation aborted");
   }

   @Override
   public Future<E> lease(final T var1, final Object var2, final FutureCallback<E> var3) {
      Args.notNull(var1, "Route");
      Asserts.check(!this.isShutDown, "Connection pool shut down");
      return new Future<E>() {
         private final AtomicBoolean cancelled = new AtomicBoolean(false);
         private final AtomicBoolean done = new AtomicBoolean(false);
         private final AtomicReference<E> entryRef = new AtomicReference<>(null);

         @Override
         public boolean cancel(boolean var1x) {
            if (this.done.compareAndSet(false, true)) {
               this.cancelled.set(true);
               AbstractConnPool.this.lock.lock();

               try {
                  AbstractConnPool.this.condition.signalAll();
               } finally {
                  AbstractConnPool.this.lock.unlock();
               }

               if (var3 != null) {
                  var3.cancelled();
               }

               return true;
            } else {
               return false;
            }
         }

         @Override
         public boolean isCancelled() {
            return this.cancelled.get();
         }

         @Override
         public boolean isDone() {
            return this.done.get();
         }

         public E get() throws InterruptedException, ExecutionException {
            try {
               return (E)this.get(0L, TimeUnit.MILLISECONDS);
            } catch (TimeoutException var2x) {
               throw new ExecutionException(var2x);
            }
         }

         public E get(long var1x, TimeUnit var3x) throws InterruptedException, ExecutionException, TimeoutException {
            while (true) {
               synchronized (this) {
                  PoolEntry var10000;
                  try {
                     PoolEntry var5 = this.entryRef.get();
                     if (var5 != null) {
                        return (E)var5;
                     }

                     if (this.done.get()) {
                        throw new ExecutionException(AbstractConnPool.operationAborted());
                     }

                     PoolEntry var6 = AbstractConnPool.this.getPoolEntryBlocking(var1, var2, var1x, var3x, this);
                     if (AbstractConnPool.this.validateAfterInactivity > 0
                        && var6.getUpdated() + (long)AbstractConnPool.this.validateAfterInactivity <= System.currentTimeMillis()
                        && !AbstractConnPool.this.validate((E)var6)) {
                        var6.close();
                        AbstractConnPool.this.release((E)var6, false);
                        continue;
                     }

                     if (!this.done.compareAndSet(false, true)) {
                        AbstractConnPool.this.release((E)var6, true);
                        throw new ExecutionException(AbstractConnPool.operationAborted());
                     }

                     this.entryRef.set((E)var6);
                     this.done.set(true);
                     AbstractConnPool.this.onLease((E)var6);
                     if (var3 != null) {
                        var3.completed(var6);
                     }

                     var10000 = var6;
                  } catch (IOException var8) {
                     if (this.done.compareAndSet(false, true) && var3 != null) {
                        var3.failed(var8);
                     }

                     throw new ExecutionException(var8);
                  }

                  return (E)var10000;
               }
            }
         }
      };
   }

   public Future<E> lease(T var1, Object var2) {
      return this.lease((T)var1, var2, null);
   }

   private E getPoolEntryBlocking(T var1, Object var2, long var3, TimeUnit var5, Future<E> var6) throws IOException, InterruptedException, ExecutionException, TimeoutException {
      Date var7 = null;
      if (var3 > 0L) {
         var7 = new Date(System.currentTimeMillis() + var5.toMillis(var3));
      }

      this.lock.lock();

      try {
         int var29;
         do {
            Asserts.check(!this.isShutDown, "Connection pool shut down");
            if (var6.isCancelled()) {
               throw new ExecutionException(operationAborted());
            }

            RouteSpecificPool var9 = this.getPool((T)var1);

            PoolEntry var8;
            while (true) {
               var8 = var9.getFree(var2);
               if (var8 == null) {
                  break;
               }

               if (var8.isExpired(System.currentTimeMillis())) {
                  var8.close();
               }

               if (!var8.isClosed()) {
                  break;
               }

               this.available.remove(var8);
               var9.free((E)var8, false);
            }

            if (var8 != null) {
               this.available.remove(var8);
               this.leased.add((E)var8);
               this.onReuse((E)var8);
               return (E)var8;
            }

            int var10 = this.getMax((T)var1);
            int var11 = Math.max(0, var9.getAllocatedCount() + 1 - var10);
            if (var11 > 0) {
               for (int var12 = 0; var12 < var11; var12++) {
                  PoolEntry var13 = var9.getLastUsed();
                  if (var13 == null) {
                     break;
                  }

                  var13.close();
                  this.available.remove(var13);
                  var9.remove((E)var13);
               }
            }

            if (var9.getAllocatedCount() < var10) {
               var29 = this.leased.size();
               int var30 = Math.max(this.maxTotal - var29, 0);
               if (var30 > 0) {
                  int var14 = this.available.size();
                  if (var14 > var30 - 1 && !this.available.isEmpty()) {
                     PoolEntry var15 = this.available.removeLast();
                     var15.close();
                     RouteSpecificPool var16 = this.getPool((T)var15.getRoute());
                     var16.remove((E)var15);
                  }

                  Object var31 = this.connFactory.create((T)var1);
                  var8 = var9.add(var31);
                  this.leased.add((E)var8);
                  return (E)var8;
               }
            }

            boolean var28 = false;

            try {
               var9.queue(var6);
               this.pending.add(var6);
               if (var7 != null) {
                  var29 = this.condition.awaitUntil(var7);
               } else {
                  this.condition.await();
                  var29 = 1;
               }

               if (var6.isCancelled()) {
                  throw new ExecutionException(operationAborted());
               }
            } finally {
               var9.unqueue(var6);
               this.pending.remove(var6);
            }
         } while (var29 || var7 == null || var7.getTime() > System.currentTimeMillis());

         throw new TimeoutException("Timeout waiting for connection");
      } finally {
         this.lock.unlock();
      }
   }

   public void release(E var1, boolean var2) {
      this.lock.lock();

      try {
         if (this.leased.remove(var1)) {
            RouteSpecificPool var3 = this.getPool((T)var1.getRoute());
            var3.free((E)var1, var2);
            if (var2 && !this.isShutDown) {
               this.available.addFirst((E)var1);
            } else {
               var1.close();
            }

            this.onRelease((E)var1);
            Future var4 = var3.nextPending();
            if (var4 != null) {
               this.pending.remove(var4);
            } else {
               var4 = this.pending.poll();
            }

            if (var4 != null) {
               this.condition.signalAll();
            }
         }
      } finally {
         this.lock.unlock();
      }
   }

   private int getMax(T var1) {
      Integer var2 = this.maxPerRoute.get(var1);
      return var2 != null ? var2 : this.defaultMaxPerRoute;
   }

   @Override
   public void setMaxTotal(int var1) {
      Args.positive(var1, "Max value");
      this.lock.lock();

      try {
         this.maxTotal = var1;
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public int getMaxTotal() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.maxTotal;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   @Override
   public void setDefaultMaxPerRoute(int var1) {
      Args.positive(var1, "Max per route value");
      this.lock.lock();

      try {
         this.defaultMaxPerRoute = var1;
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public int getDefaultMaxPerRoute() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.defaultMaxPerRoute;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   @Override
   public void setMaxPerRoute(T var1, int var2) {
      Args.notNull(var1, "Route");
      this.lock.lock();

      try {
         if (var2 > -1) {
            this.maxPerRoute.put((T)var1, var2);
         } else {
            this.maxPerRoute.remove(var1);
         }
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public int getMaxPerRoute(T var1) {
      Args.notNull(var1, "Route");
      this.lock.lock();

      int var2;
      try {
         var2 = this.getMax((T)var1);
      } finally {
         this.lock.unlock();
      }

      return var2;
   }

   @Override
   public PoolStats getTotalStats() {
      this.lock.lock();

      PoolStats var1;
      try {
         var1 = new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   @Override
   public PoolStats getStats(T var1) {
      Args.notNull(var1, "Route");
      this.lock.lock();

      PoolStats var3;
      try {
         RouteSpecificPool var2 = this.getPool((T)var1);
         var3 = new PoolStats(var2.getLeasedCount(), var2.getPendingCount(), var2.getAvailableCount(), this.getMax((T)var1));
      } finally {
         this.lock.unlock();
      }

      return var3;
   }

   public Set<T> getRoutes() {
      this.lock.lock();

      HashSet var1;
      try {
         var1 = new HashSet<>(this.routeToPool.keySet());
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   protected void enumAvailable(PoolEntryCallback<T, C> var1) {
      this.lock.lock();

      try {
         Iterator var2 = this.available.iterator();

         while (var2.hasNext()) {
            PoolEntry var3 = (PoolEntry)var2.next();
            var1.process(var3);
            if (var3.isClosed()) {
               RouteSpecificPool var4 = this.getPool((T)var3.getRoute());
               var4.remove((E)var3);
               var2.remove();
            }
         }

         this.purgePoolMap();
      } finally {
         this.lock.unlock();
      }
   }

   protected void enumLeased(PoolEntryCallback<T, C> var1) {
      this.lock.lock();

      try {
         for (PoolEntry var3 : this.leased) {
            var1.process(var3);
         }
      } finally {
         this.lock.unlock();
      }
   }

   private void purgePoolMap() {
      Iterator var1 = this.routeToPool.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         RouteSpecificPool var3 = (RouteSpecificPool)var2.getValue();
         if (var3.getPendingCount() + var3.getAllocatedCount() == 0) {
            var1.remove();
         }
      }
   }

   public void closeIdle(long var1, TimeUnit var3) {
      Args.notNull(var3, "Time unit");
      long var4 = var3.toMillis(var1);
      if (var4 < 0L) {
         var4 = 0L;
      }

      final long var6 = System.currentTimeMillis() - var4;
      this.enumAvailable(new PoolEntryCallback<T, C>() {
         @Override
         public void process(PoolEntry<T, C> var1) {
            if (var1.getUpdated() <= var6) {
               var1.close();
            }
         }
      });
   }

   public void closeExpired() {
      final long var1 = System.currentTimeMillis();
      this.enumAvailable(new PoolEntryCallback<T, C>() {
         @Override
         public void process(PoolEntry<T, C> var1x) {
            if (var1x.isExpired(var1)) {
               var1x.close();
            }
         }
      });
   }

   public int getValidateAfterInactivity() {
      return this.validateAfterInactivity;
   }

   public void setValidateAfterInactivity(int var1) {
      this.validateAfterInactivity = var1;
   }

   @Override
   public String toString() {
      this.lock.lock();

      String var2;
      try {
         StringBuilder var1 = new StringBuilder();
         var1.append("[leased: ");
         var1.append(this.leased);
         var1.append("][available: ");
         var1.append(this.available);
         var1.append("][pending: ");
         var1.append(this.pending);
         var1.append("]");
         var2 = var1.toString();
      } finally {
         this.lock.unlock();
      }

      return var2;
   }
}
