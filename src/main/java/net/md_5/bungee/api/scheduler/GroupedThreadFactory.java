package net.md_5.bungee.api.scheduler;

import java.util.concurrent.ThreadFactory;
import net.md_5.bungee.api.plugin.Plugin;

@Deprecated
public class GroupedThreadFactory implements ThreadFactory {
   private final ThreadGroup group;

   public GroupedThreadFactory(Plugin var1, String var2) {
      this.group = new GroupedThreadFactory.BungeeGroup(var2);
   }

   @Override
   public Thread newThread(Runnable var1) {
      return new Thread(this.group, var1);
   }

   public ThreadGroup getGroup() {
      return this.group;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GroupedThreadFactory)) {
         return false;
      } else {
         GroupedThreadFactory var2 = (GroupedThreadFactory)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ThreadGroup var3 = this.getGroup();
            ThreadGroup var4 = var2.getGroup();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof GroupedThreadFactory;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      ThreadGroup var3 = this.getGroup();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   @Override
   public String toString() {
      return "GroupedThreadFactory(group=" + this.getGroup() + ")";
   }

   public static final class BungeeGroup extends ThreadGroup {
      private BungeeGroup(String var1) {
         super(var1);
      }
   }
}
