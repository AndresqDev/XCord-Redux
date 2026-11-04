package org.apache.logging.log4j.core.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive
public class AppenderControlArraySet {
   private static final AtomicReferenceFieldUpdater<AppenderControlArraySet, AppenderControl[]> appenderArrayUpdater = AtomicReferenceFieldUpdater.newUpdater(
      AppenderControlArraySet.class, AppenderControl[].class, "appenderArray"
   );
   private volatile AppenderControl[] appenderArray = AppenderControl.EMPTY_ARRAY;

   public boolean add(AppenderControl var1) {
      boolean var2;
      do {
         AppenderControl[] var3 = this.appenderArray;

         for (AppenderControl var7 : var3) {
            if (var7.equals(var1)) {
               return false;
            }
         }

         AppenderControl[] var8 = Arrays.copyOf(var3, var3.length + 1);
         var8[var8.length - 1] = var1;
         var2 = appenderArrayUpdater.compareAndSet(this, var3, var8);
      } while (!var2);

      return true;
   }

   public AppenderControl remove(String var1) {
      boolean var2;
      do {
         var2 = true;
         AppenderControl[] var3 = this.appenderArray;

         for (int var4 = 0; var4 < var3.length; var4++) {
            AppenderControl var5 = var3[var4];
            if (Objects.equals(var1, var5.getAppenderName())) {
               AppenderControl[] var6 = this.removeElementAt(var4, var3);
               if (appenderArrayUpdater.compareAndSet(this, var3, var6)) {
                  return var5;
               }

               var2 = false;
               break;
            }
         }
      } while (!var2);

      return null;
   }

   private AppenderControl[] removeElementAt(int var1, AppenderControl[] var2) {
      AppenderControl[] var3 = Arrays.copyOf(var2, var2.length - 1);
      System.arraycopy(var2, var1 + 1, var3, var1, var3.length - var1);
      return var3;
   }

   public Map<String, Appender> asMap() {
      HashMap var1 = new HashMap();

      for (AppenderControl var5 : this.appenderArray) {
         var1.put(var5.getAppenderName(), var5.getAppender());
      }

      return var1;
   }

   public AppenderControl[] clear() {
      return appenderArrayUpdater.getAndSet(this, AppenderControl.EMPTY_ARRAY);
   }

   public boolean isEmpty() {
      return this.appenderArray.length == 0;
   }

   public AppenderControl[] get() {
      return this.appenderArray;
   }

   @Override
   public String toString() {
      return "AppenderControlArraySet [appenderArray=" + Arrays.toString((Object[])this.appenderArray) + "]";
   }
}
