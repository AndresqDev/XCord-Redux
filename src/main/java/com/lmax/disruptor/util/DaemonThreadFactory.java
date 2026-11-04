package com.lmax.disruptor.util;

import java.util.concurrent.ThreadFactory;

public enum DaemonThreadFactory implements ThreadFactory {
   INSTANCE;

   @Override
   public Thread newThread(Runnable var1) {
      Thread var2 = new Thread(var1);
      var2.setDaemon(true);
      return var2;
   }
}
