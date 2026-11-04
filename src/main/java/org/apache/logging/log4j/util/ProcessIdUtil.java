package org.apache.logging.log4j.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class ProcessIdUtil {
   public static final String DEFAULT_PROCESSID = "-";

   public static String getProcessId() {
      try {
         Class var0 = Class.forName("java.lang.management.ManagementFactory");
         Method var1 = var0.getDeclaredMethod("getRuntimeMXBean");
         Class var2 = Class.forName("java.lang.management.RuntimeMXBean");
         Method var3 = var2.getDeclaredMethod("getName");
         Object var4 = var1.invoke(null);
         String var5 = (String)var3.invoke(var4);
         return var5.split("@")[0];
      } catch (Exception var7) {
         try {
            return new File("/proc/self").getCanonicalFile().getName();
         } catch (IOException var6) {
            return "-";
         }
      }
   }
}
