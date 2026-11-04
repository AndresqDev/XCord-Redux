package org.apache.logging.log4j.core.message;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.message.ThreadDumpMessage;
import org.apache.logging.log4j.message.ThreadInformation;

public class ExtendedThreadInfoFactory implements ThreadDumpMessage.ThreadInfoFactory {
   public ExtendedThreadInfoFactory() {
      Method[] var1 = ThreadInfo.class.getMethods();
      boolean var2 = true;

      for (Method var6 : var1) {
         if (var6.getName().equals("getLockInfo")) {
            var2 = false;
            break;
         }
      }

      if (var2) {
         throw new IllegalStateException();
      }
   }

   @Override
   public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
      ThreadMXBean var1 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var2 = var1.dumpAllThreads(true, true);
      HashMap var3 = new HashMap(var2.length);

      for (ThreadInfo var7 : var2) {
         var3.put(new ExtendedThreadInformation(var7), var7.getStackTrace());
      }

      return var3;
   }
}
