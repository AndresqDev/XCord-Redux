package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;

public class PreLoginEvent extends AsyncEvent<PreLoginEvent> implements Cancellable {
   private boolean cancelled;
   private BaseComponent[] cancelReasonComponents;
   private final PendingConnection connection;

   public PreLoginEvent(PendingConnection var1, Callback<PreLoginEvent> var2) {
      super(var2);
      this.connection = var1;
   }

   @Deprecated
   public String getCancelReason() {
      return BaseComponent.toLegacyText(this.getCancelReasonComponents());
   }

   @Deprecated
   public void setCancelReason(String var1) {
      this.setCancelReason(TextComponent.fromLegacyText(var1));
   }

   public void setCancelReason(BaseComponent... var1) {
      this.cancelReasonComponents = var1;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public BaseComponent[] getCancelReasonComponents() {
      return this.cancelReasonComponents;
   }

   public PendingConnection getConnection() {
      return this.connection;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public String toString() {
      return "PreLoginEvent(cancelled="
         + this.isCancelled()
         + ", cancelReasonComponents="
         + Arrays.deepToString(this.getCancelReasonComponents())
         + ", connection="
         + this.getConnection()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PreLoginEvent)) {
         return false;
      } else {
         PreLoginEvent var2 = (PreLoginEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else if (!Arrays.deepEquals(this.getCancelReasonComponents(), var2.getCancelReasonComponents())) {
            return false;
         } else {
            PendingConnection var3 = this.getConnection();
            PendingConnection var4 = var2.getConnection();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof PreLoginEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      var2 = var2 * 59 + Arrays.deepHashCode(this.getCancelReasonComponents());
      PendingConnection var3 = this.getConnection();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
