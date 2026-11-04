package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class ExecutionList {
   private static final Logger log = Logger.getLogger(ExecutionList.class.getName());
   @GuardedBy("this")
   private ExecutionList.RunnableExecutorPair runnables;
   @GuardedBy("this")
   private boolean executed;

   public void add(Runnable var1, Executor var2) {
      Preconditions.checkNotNull(var1, "Runnable was null.");
      Preconditions.checkNotNull(var2, "Executor was null.");
      synchronized (this) {
         if (!this.executed) {
            this.runnables = new ExecutionList.RunnableExecutorPair(var1, var2, this.runnables);
            return;
         }
      }

      executeListener(var1, var2);
   }

   public void execute() {
      ExecutionList.RunnableExecutorPair var1;
      synchronized (this) {
         if (this.executed) {
            return;
         }

         this.executed = true;
         var1 = this.runnables;
         this.runnables = null;
      }

      ExecutionList.RunnableExecutorPair var2 = null;

      while (var1 != null) {
         ExecutionList.RunnableExecutorPair var3 = var1;
         var1 = var1.next;
         var3.next = var2;
         var2 = var3;
      }

      while (var2 != null) {
         executeListener(var2.runnable, var2.executor);
         var2 = var2.next;
      }
   }

   private static void executeListener(Runnable var0, Executor var1) {
      try {
         var1.execute(var0);
      } catch (RuntimeException var5) {
         Logger var10000 = log;
         Level var10001 = Level.SEVERE;
         String var3 = String.valueOf(var0);
         String var4 = String.valueOf(var1);
         var10000.log(
            var10001,
            new StringBuilder(57 + String.valueOf(var3).length() + String.valueOf(var4).length())
               .append("RuntimeException while executing runnable ")
               .append(var3)
               .append(" with executor ")
               .append(var4)
               .toString(),
            (Throwable)var5
         );
      }
   }

   private static final class RunnableExecutorPair {
      final Runnable runnable;
      final Executor executor;
      ExecutionList.RunnableExecutorPair next;

      RunnableExecutorPair(Runnable var1, Executor var2, ExecutionList.RunnableExecutorPair var3) {
         this.runnable = var1;
         this.executor = var2;
         this.next = var3;
      }
   }
}
