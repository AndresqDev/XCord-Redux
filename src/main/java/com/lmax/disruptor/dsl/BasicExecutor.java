package com.lmax.disruptor.dsl;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class BasicExecutor implements Executor {
   private final ThreadFactory factory;
   private final Queue<Thread> threads = new ConcurrentLinkedQueue<>();

   public BasicExecutor(ThreadFactory var1) {
      this.factory = var1;
   }

   @Override
   public void execute(Runnable var1) {
      Thread var2 = this.factory.newThread(var1);
      if (null == var2) {
         throw new RuntimeException("Failed to create thread to run: " + var1);
      } else {
         var2.start();
         this.threads.add(var2);
      }
   }

   @Override
   public String toString() {
      return "BasicExecutor{threads=" + this.dumpThreadInfo() + '}';
   }

   private String dumpThreadInfo() {
      StringBuilder var1 = new StringBuilder();
      ThreadMXBean var2 = ManagementFactory.getThreadMXBean();

      for (Thread var4 : this.threads) {
         ThreadInfo var5 = var2.getThreadInfo(var4.getId());
         var1.append("{");
         var1.append("name=").append(var4.getName()).append(",");
         var1.append("id=").append(var4.getId()).append(",");
         var1.append("state=").append(var5.getThreadState()).append(",");
         var1.append("lockInfo=").append(var5.getLockInfo());
         var1.append("}");
      }

      return var1.toString();
   }
}
