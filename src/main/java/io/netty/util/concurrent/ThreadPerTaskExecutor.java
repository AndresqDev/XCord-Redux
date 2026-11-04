package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadPerTaskExecutor implements Executor {
   private final ThreadFactory threadFactory;

   public ThreadPerTaskExecutor(ThreadFactory var1) {
      this.threadFactory = ObjectUtil.checkNotNull(var1, "threadFactory");
   }

   @Override
   public void execute(Runnable var1) {
      this.threadFactory.newThread(var1).start();
   }
}
