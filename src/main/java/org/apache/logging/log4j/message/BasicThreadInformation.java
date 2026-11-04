package org.apache.logging.log4j.message;

import java.lang.Thread.State;
import org.apache.logging.log4j.util.StringBuilders;

class BasicThreadInformation implements ThreadInformation {
   private static final int HASH_SHIFT = 32;
   private static final int HASH_MULTIPLIER = 31;
   private final long id;
   private final String name;
   private final String longName;
   private final State state;
   private final int priority;
   private final boolean isAlive;
   private final boolean isDaemon;
   private final String threadGroupName;

   BasicThreadInformation(Thread var1) {
      this.id = var1.getId();
      this.name = var1.getName();
      this.longName = var1.toString();
      this.state = var1.getState();
      this.priority = var1.getPriority();
      this.isAlive = var1.isAlive();
      this.isDaemon = var1.isDaemon();
      ThreadGroup var2 = var1.getThreadGroup();
      this.threadGroupName = var2 == null ? null : var2.getName();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         BasicThreadInformation var2 = (BasicThreadInformation)var1;
         if (this.id != var2.id) {
            return false;
         } else {
            return this.name != null ? this.name.equals(var2.name) : var2.name == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = (int)(this.id ^ this.id >>> 32);
      return 31 * var1 + (this.name != null ? this.name.hashCode() : 0);
   }

   @Override
   public void printThreadInfo(StringBuilder var1) {
      StringBuilders.appendDqValue(var1, this.name).append(' ');
      if (this.isDaemon) {
         var1.append("daemon ");
      }

      var1.append("prio=").append(this.priority).append(" tid=").append(this.id).append(' ');
      if (this.threadGroupName != null) {
         StringBuilders.appendKeyDqValue(var1, "group", this.threadGroupName);
      }

      var1.append('\n');
      var1.append("\tThread state: ").append(this.state.name()).append('\n');
   }

   @Override
   public void printStack(StringBuilder var1, StackTraceElement[] var2) {
      for (StackTraceElement var6 : var2) {
         var1.append("\tat ").append(var6).append('\n');
      }
   }
}
