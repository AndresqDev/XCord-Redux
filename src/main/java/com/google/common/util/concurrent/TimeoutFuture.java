package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class TimeoutFuture<V> extends FluentFuture.TrustedFuture<V> {
   private ListenableFuture<V> delegateRef;
   private ScheduledFuture<?> timer;

   static <V> ListenableFuture<V> create(ListenableFuture<V> var0, long var1, TimeUnit var3, ScheduledExecutorService var4) {
      TimeoutFuture var5 = new TimeoutFuture(var0);
      TimeoutFuture.Fire var6 = new TimeoutFuture.Fire(var5);
      var5.timer = var4.schedule(var6, var1, var3);
      var0.addListener(var6, MoreExecutors.directExecutor());
      return var5;
   }

   private TimeoutFuture(ListenableFuture<V> var1) {
      this.delegateRef = Preconditions.checkNotNull(var1);
   }

   @Override
   protected String pendingToString() {
      ListenableFuture var1 = this.delegateRef;
      ScheduledFuture var2 = this.timer;
      if (var1 != null) {
         String var4 = String.valueOf(var1);
         String var3 = new StringBuilder(14 + String.valueOf(var4).length()).append("inputFuture=[").append(var4).append("]").toString();
         if (var2 != null) {
            long var7 = var2.getDelay(TimeUnit.MILLISECONDS);
            if (var7 > 0L) {
               String var6 = String.valueOf(var3);
               var3 = new StringBuilder(43 + String.valueOf(var6).length()).append(var6).append(", remaining delay=[").append(var7).append(" ms]").toString();
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   @Override
   protected void afterDone() {
      this.maybePropagateCancellationTo(this.delegateRef);
      ScheduledFuture var1 = this.timer;
      if (var1 != null) {
         var1.cancel(false);
      }

      this.delegateRef = null;
      this.timer = null;
   }

   private static final class Fire<V> implements Runnable {
      TimeoutFuture<V> timeoutFutureRef;

      Fire(TimeoutFuture<V> var1) {
         this.timeoutFutureRef = var1;
      }

      @Override
      public void run() {
         TimeoutFuture var1 = this.timeoutFutureRef;
         if (var1 != null) {
            ListenableFuture var2 = var1.delegateRef;
            if (var2 != null) {
               this.timeoutFutureRef = null;
               if (var2.isDone()) {
                  var1.setFuture(var2);
               } else {
                  try {
                     ScheduledFuture var3 = var1.timer;
                     var1.timer = null;
                     String var4 = "Timed out";

                     try {
                        if (var3 != null) {
                           long var5 = Math.abs(var3.getDelay(TimeUnit.MILLISECONDS));
                           if (var5 > 10L) {
                              String var7 = String.valueOf(var4);
                              var4 = new StringBuilder(66 + String.valueOf(var7).length())
                                 .append(var7)
                                 .append(" (timeout delayed by ")
                                 .append(var5)
                                 .append(" ms after scheduled time)")
                                 .toString();
                           }
                        }

                        String var16 = String.valueOf(var4);
                        String var6 = String.valueOf(var2);
                        var4 = new StringBuilder(2 + String.valueOf(var16).length() + String.valueOf(var6).length())
                           .append(var16)
                           .append(": ")
                           .append(var6)
                           .toString();
                     } finally {
                        var1.setException(new TimeoutFuture.TimeoutFutureException(var4));
                     }
                  } finally {
                     var2.cancel(true);
                  }
               }
            }
         }
      }
   }

   private static final class TimeoutFutureException extends TimeoutException {
      private TimeoutFutureException(String var1) {
         super(var1);
      }

      @Override
      public synchronized Throwable fillInStackTrace() {
         this.setStackTrace(new StackTraceElement[0]);
         return this;
      }
   }
}
