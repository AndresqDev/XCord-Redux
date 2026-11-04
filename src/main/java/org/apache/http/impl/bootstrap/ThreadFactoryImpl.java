package org.apache.http.impl.bootstrap;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

class ThreadFactoryImpl implements ThreadFactory {
   private final String namePrefix;
   private final ThreadGroup group;
   private final AtomicLong count;

   ThreadFactoryImpl(String var1, ThreadGroup var2) {
      this.namePrefix = var1;
      this.group = var2;
      this.count = new AtomicLong();
   }

   ThreadFactoryImpl(String var1) {
      this(var1, null);
   }

   @Override
   public Thread newThread(Runnable var1) {
      return new Thread(this.group, var1, this.namePrefix + "-" + this.count.incrementAndGet());
   }
}
