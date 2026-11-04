package net.md_5.bungee.api.event;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.Stopwatch;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

public class AsyncEvent<T> extends Event {
   private Callback<T> done;
   private final Map<Plugin, AtomicInteger> intents = new ConcurrentHashMap<>();
   private final AtomicBoolean fired = new AtomicBoolean();
   private final AtomicInteger latch = new AtomicInteger();
   private Stopwatch<Plugin> stopwatch = null;

   public AsyncEvent(Callback<T> var1) {
      this.done = var1;
   }

   @Override
   public void postCall() {
      if (this.latch.get() == 0) {
         this.done.done((T)this, null);
      }

      this.fired.set(true);
   }

   public void registerIntent(Plugin var1) {
      Preconditions.checkState(!this.fired.get(), "Event %s has already been fired", this);
      AtomicInteger var2 = this.intents.get(var1);
      if (var2 == null) {
         this.intents.put(var1, new AtomicInteger(1));
         if (this.stopwatch != null) {
            this.stopwatch.start(var1);
         }
      } else {
         var2.incrementAndGet();
      }

      this.latch.incrementAndGet();
   }

   public void completeIntent(Plugin var1) {
      AtomicInteger var2 = this.intents.get(var1);
      Preconditions.checkState(var2 != null && var2.get() > 0, "Plugin %s has not registered intents for event %s", var1, this);
      if (this.stopwatch != null) {
         this.stopwatch.stop(var1);
      }

      var2.decrementAndGet();
      if (this.fired.get()) {
         if (this.latch.decrementAndGet() == 0) {
            this.done.done((T)this, null);
         }
      } else {
         this.latch.decrementAndGet();
      }
   }

   public Stopwatch<Plugin> getStopwatch() {
      return this.stopwatch;
   }

   public void setStopwatch(Stopwatch<Plugin> var1) {
      this.stopwatch = var1;
   }

   public Callback<T> getDoneCallback() {
      return this.done;
   }

   public void setDoneCallback(Callback<T> var1) {
      this.done = var1;
   }

   public void setDone(Callback<T> var1) {
      this.done = var1;
   }

   @Override
   public String toString() {
      return "AsyncEvent(super="
         + super.toString()
         + ", done="
         + this.done
         + ", intents="
         + this.intents
         + ", fired="
         + this.fired
         + ", latch="
         + this.latch
         + ", stopwatch="
         + this.getStopwatch()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AsyncEvent)) {
         return false;
      } else {
         AsyncEvent var2 = (AsyncEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            Callback var3 = this.done;
            Callback var4 = var2.done;
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Map var5 = this.intents;
               Map var6 = var2.intents;
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  AtomicBoolean var7 = this.fired;
                  AtomicBoolean var8 = var2.fired;
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     AtomicInteger var9 = this.latch;
                     AtomicInteger var10 = var2.latch;
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        Stopwatch var11 = this.getStopwatch();
                        Stopwatch var12 = var2.getStopwatch();
                        return var11 == null ? var12 == null : var11.equals(var12);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof AsyncEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      Callback var3 = this.done;
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Map var4 = this.intents;
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      AtomicBoolean var5 = this.fired;
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      AtomicInteger var6 = this.latch;
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Stopwatch var7 = this.getStopwatch();
      return var2 * 59 + (var7 == null ? 43 : var7.hashCode());
   }
}
