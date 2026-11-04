package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;

public class ProxyEventException extends ProxyException {
   private final Listener listener;
   private final Event event;

   public ProxyEventException(String var1, Throwable var2, Listener var3, Event var4) {
      super(var1, var2);
      this.listener = (Listener)Preconditions.checkNotNull(var3, "listener");
      this.event = (Event)Preconditions.checkNotNull(var4, "event");
   }

   public ProxyEventException(Throwable var1, Listener var2, Event var3) {
      super(var1);
      this.listener = (Listener)Preconditions.checkNotNull(var2, "listener");
      this.event = (Event)Preconditions.checkNotNull(var3, "event");
   }

   protected ProxyEventException(String var1, Throwable var2, boolean var3, boolean var4, Listener var5, Event var6) {
      super(var1, var2, var3, var4);
      this.listener = (Listener)Preconditions.checkNotNull(var5, "listener");
      this.event = (Event)Preconditions.checkNotNull(var6, "event");
   }

   public Listener getListener() {
      return this.listener;
   }

   public Event getEvent() {
      return this.event;
   }
}
