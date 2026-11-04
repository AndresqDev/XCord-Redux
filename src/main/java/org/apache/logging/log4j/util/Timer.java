package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Timer implements Serializable, StringBuilderFormattable {
   private static final long serialVersionUID = 9175191792439630013L;
   private final String name;
   private Timer.Status status;
   private long elapsedTime;
   private final int iterations;
   private static long NANO_PER_SECOND = 1000000000L;
   private static long NANO_PER_MINUTE = NANO_PER_SECOND * 60L;
   private static long NANO_PER_HOUR = NANO_PER_MINUTE * 60L;
   private ThreadLocal<Long> startTime = new ThreadLocal<Long>() {
      protected Long initialValue() {
         return 0L;
      }
   };

   public Timer(String var1) {
      this(var1, 0);
   }

   public Timer(String var1, int var2) {
      this.name = var1;
      this.status = Timer.Status.Stopped;
      this.iterations = var2 > 0 ? var2 : 0;
   }

   public synchronized void start() {
      this.startTime.set(System.nanoTime());
      this.elapsedTime = 0L;
      this.status = Timer.Status.Started;
   }

   public synchronized void startOrResume() {
      if (this.status == Timer.Status.Stopped) {
         this.start();
      } else {
         this.resume();
      }
   }

   public synchronized String stop() {
      this.elapsedTime = this.elapsedTime + (System.nanoTime() - this.startTime.get());
      this.startTime.set(0L);
      this.status = Timer.Status.Stopped;
      return this.toString();
   }

   public synchronized void pause() {
      this.elapsedTime = this.elapsedTime + (System.nanoTime() - this.startTime.get());
      this.startTime.set(0L);
      this.status = Timer.Status.Paused;
   }

   public synchronized void resume() {
      this.startTime.set(System.nanoTime());
      this.status = Timer.Status.Started;
   }

   public String getName() {
      return this.name;
   }

   public long getElapsedTime() {
      return this.elapsedTime / 1000000L;
   }

   public long getElapsedNanoTime() {
      return this.elapsedTime;
   }

   public Timer.Status getStatus() {
      return this.status;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.formatTo(var1);
      return var1.toString();
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append("Timer ").append(this.name);
      switch (this.status) {
         case Started:
            var1.append(" started");
            break;
         case Paused:
            var1.append(" paused");
            break;
         case Stopped:
            long var2 = this.elapsedTime;
            long var4 = var2 / NANO_PER_HOUR;
            var2 %= NANO_PER_HOUR;
            long var6 = var2 / NANO_PER_MINUTE;
            var2 %= NANO_PER_MINUTE;
            long var8 = var2 / NANO_PER_SECOND;
            var2 %= NANO_PER_SECOND;
            String var10 = "";
            if (var4 > 0L) {
               var10 = var10 + var4 + " hours ";
            }

            if (var6 > 0L || var4 > 0L) {
               var10 = var10 + var6 + " minutes ";
            }

            DecimalFormat var11 = new DecimalFormat("#0");
            var10 = var10 + var11.format(var8) + '.';
            var11 = new DecimalFormat("000000000");
            var10 = var10 + var11.format(var2) + " seconds";
            var1.append(" stopped. Elapsed time: ").append(var10);
            if (this.iterations > 0) {
               var2 = this.elapsedTime / (long)this.iterations;
               var4 = var2 / NANO_PER_HOUR;
               var2 %= NANO_PER_HOUR;
               var6 = var2 / NANO_PER_MINUTE;
               var2 %= NANO_PER_MINUTE;
               var8 = var2 / NANO_PER_SECOND;
               var2 %= NANO_PER_SECOND;
               var10 = "";
               if (var4 > 0L) {
                  var10 = var10 + var4 + " hours ";
               }

               if (var6 > 0L || var4 > 0L) {
                  var10 = var10 + var6 + " minutes ";
               }

               var11 = new DecimalFormat("#0");
               var10 = var10 + var11.format(var8) + '.';
               var11 = new DecimalFormat("000000000");
               var10 = var10 + var11.format(var2) + " seconds";
               var1.append(" Average per iteration: ").append(var10);
            }
            break;
         default:
            var1.append(' ').append(this.status);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Timer)) {
         return false;
      } else {
         Timer var2 = (Timer)var1;
         if (this.elapsedTime != var2.elapsedTime) {
            return false;
         } else if (this.startTime != var2.startTime) {
            return false;
         } else if (this.name != null ? this.name.equals(var2.name) : var2.name == null) {
            return this.status != null ? this.status.equals(var2.status) : var2.status == null;
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.name != null ? this.name.hashCode() : 0;
      var1 = 29 * var1 + (this.status != null ? this.status.hashCode() : 0);
      long var2 = this.startTime.get();
      var1 = 29 * var1 + (int)(var2 ^ var2 >>> 32);
      return 29 * var1 + (int)(this.elapsedTime ^ this.elapsedTime >>> 32);
   }

   public static enum Status {
      Started,
      Stopped,
      Paused;
   }
}
