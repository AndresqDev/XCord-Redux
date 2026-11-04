package org.apache.http.impl.bootstrap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class WorkerPoolExecutor extends ThreadPoolExecutor {
   private final Map<org.apache.http.impl.bootstrap.Worker, Boolean> workerSet = new ConcurrentHashMap<>();

   public WorkerPoolExecutor(int var1, int var2, long var3, TimeUnit var5, BlockingQueue<Runnable> var6, ThreadFactory var7) {
      super(var1, var2, var3, var5, var6, var7);
   }

   @Override
   protected void beforeExecute(Thread var1, Runnable var2) {
      if (var2 instanceof org.apache.http.impl.bootstrap.Worker) {
         this.workerSet.put((org.apache.http.impl.bootstrap.Worker)var2, Boolean.TRUE);
      }
   }

   @Override
   protected void afterExecute(Runnable var1, Throwable var2) {
      if (var1 instanceof org.apache.http.impl.bootstrap.Worker) {
         this.workerSet.remove(var1);
      }
   }

   public Set<org.apache.http.impl.bootstrap.Worker> getWorkers() {
      return new HashSet<>(this.workerSet.keySet());
   }
}
