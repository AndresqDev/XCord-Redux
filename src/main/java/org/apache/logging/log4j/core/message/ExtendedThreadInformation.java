package org.apache.logging.log4j.core.message;

import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import org.apache.logging.log4j.message.ThreadInformation;
import org.apache.logging.log4j.util.StringBuilders;

class ExtendedThreadInformation implements ThreadInformation {
   private final ThreadInfo threadInfo;

   ExtendedThreadInformation(ThreadInfo var1) {
      this.threadInfo = var1;
   }

   @Override
   public void printThreadInfo(StringBuilder var1) {
      StringBuilders.appendDqValue(var1, this.threadInfo.getThreadName());
      var1.append(" Id=").append(this.threadInfo.getThreadId()).append(' ');
      this.formatState(var1, this.threadInfo);
      if (this.threadInfo.isSuspended()) {
         var1.append(" (suspended)");
      }

      if (this.threadInfo.isInNative()) {
         var1.append(" (in native)");
      }

      var1.append('\n');
   }

   @Override
   public void printStack(StringBuilder var1, StackTraceElement[] var2) {
      int var3 = 0;

      for (StackTraceElement var7 : var2) {
         var1.append("\tat ").append(var7.toString());
         var1.append('\n');
         if (var3 == 0 && this.threadInfo.getLockInfo() != null) {
            State var8 = this.threadInfo.getThreadState();
            switch (var8) {
               case BLOCKED:
                  var1.append("\t-  blocked on ");
                  this.formatLock(var1, this.threadInfo.getLockInfo());
                  var1.append('\n');
                  break;
               case WAITING:
                  var1.append("\t-  waiting on ");
                  this.formatLock(var1, this.threadInfo.getLockInfo());
                  var1.append('\n');
                  break;
               case TIMED_WAITING:
                  var1.append("\t-  waiting on ");
                  this.formatLock(var1, this.threadInfo.getLockInfo());
                  var1.append('\n');
            }
         }

         for (MonitorInfo var11 : this.threadInfo.getLockedMonitors()) {
            if (var11.getLockedStackDepth() == var3) {
               var1.append("\t-  locked ");
               this.formatLock(var1, var11);
               var1.append('\n');
            }
         }

         var3++;
      }

      LockInfo[] var12 = this.threadInfo.getLockedSynchronizers();
      if (var12.length > 0) {
         var1.append("\n\tNumber of locked synchronizers = ").append(var12.length).append('\n');

         for (LockInfo var17 : var12) {
            var1.append("\t- ");
            this.formatLock(var1, var17);
            var1.append('\n');
         }
      }
   }

   private void formatLock(StringBuilder var1, LockInfo var2) {
      var1.append('<').append(var2.getIdentityHashCode()).append("> (a ");
      var1.append(var2.getClassName()).append(')');
   }

   private void formatState(StringBuilder var1, ThreadInfo var2) {
      State var3 = var2.getThreadState();
      var1.append(var3);
      switch (var3) {
         case BLOCKED:
            var1.append(" (on object monitor owned by \"");
            var1.append(var2.getLockOwnerName()).append("\" Id=").append(var2.getLockOwnerId()).append(')');
            break;
         case WAITING:
            StackTraceElement var7 = var2.getStackTrace()[0];
            String var8 = var7.getClassName();
            String var9 = var7.getMethodName();
            if (var8.equals("java.lang.Object") && var9.equals("wait")) {
               var1.append(" (on object monitor");
               if (var2.getLockOwnerName() != null) {
                  var1.append(" owned by \"");
                  var1.append(var2.getLockOwnerName()).append("\" Id=").append(var2.getLockOwnerId());
               }

               var1.append(')');
            } else if (var8.equals("java.lang.Thread") && var9.equals("join")) {
               var1.append(" (on completion of thread ").append(var2.getLockOwnerId()).append(')');
            } else {
               var1.append(" (parking for lock");
               if (var2.getLockOwnerName() != null) {
                  var1.append(" owned by \"");
                  var1.append(var2.getLockOwnerName()).append("\" Id=").append(var2.getLockOwnerId());
               }

               var1.append(')');
            }
            break;
         case TIMED_WAITING:
            StackTraceElement var4 = var2.getStackTrace()[0];
            String var5 = var4.getClassName();
            String var6 = var4.getMethodName();
            if (var5.equals("java.lang.Object") && var6.equals("wait")) {
               var1.append(" (on object monitor");
               if (var2.getLockOwnerName() != null) {
                  var1.append(" owned by \"");
                  var1.append(var2.getLockOwnerName()).append("\" Id=").append(var2.getLockOwnerId());
               }

               var1.append(')');
            } else if (var5.equals("java.lang.Thread") && var6.equals("sleep")) {
               var1.append(" (sleeping)");
            } else if (var5.equals("java.lang.Thread") && var6.equals("join")) {
               var1.append(" (on completion of thread ").append(var2.getLockOwnerId()).append(')');
            } else {
               var1.append(" (parking for lock");
               if (var2.getLockOwnerName() != null) {
                  var1.append(" owned by \"");
                  var1.append(var2.getLockOwnerName()).append("\" Id=").append(var2.getLockOwnerId());
               }

               var1.append(')');
            }
      }
   }
}
