package io.github.waterfallmc.waterfall.event;

import io.github.waterfallmc.waterfall.QueryResult;
import lombok.NonNull;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Event;

public class ProxyQueryEvent extends Event {
   @NonNull
   private final ListenerInfo listener;
   @NonNull
   private QueryResult result;

   public ProxyQueryEvent(@NonNull ListenerInfo var1, @NonNull QueryResult var2) {
      if (var1 == null) {
         throw new NullPointerException("listener is marked non-null but is null");
      } else if (var2 == null) {
         throw new NullPointerException("result is marked non-null but is null");
      } else {
         this.listener = var1;
         this.result = var2;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ProxyQueryEvent)) {
         return false;
      } else {
         ProxyQueryEvent var2 = (ProxyQueryEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ListenerInfo var3 = this.getListener();
            ListenerInfo var4 = var2.getListener();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               QueryResult var5 = this.getResult();
               QueryResult var6 = var2.getResult();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ProxyQueryEvent;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ListenerInfo var3 = this.getListener();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      QueryResult var4 = this.getResult();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   @NonNull
   public ListenerInfo getListener() {
      return this.listener;
   }

   @NonNull
   public QueryResult getResult() {
      return this.result;
   }

   public void setResult(@NonNull QueryResult var1) {
      if (var1 == null) {
         throw new NullPointerException("result is marked non-null but is null");
      } else {
         this.result = var1;
      }
   }

   public String toString() {
      return "ProxyQueryEvent(listener=" + this.getListener() + ", result=" + this.getResult() + ")";
   }
}
