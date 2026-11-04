package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class ChatEvent extends TargetedEvent implements Cancellable {
   private boolean cancelled;
   private String message;

   public ChatEvent(Connection var1, Connection var2, String var3) {
      super(var1, var2);
      this.message = var3;
   }

   public boolean isCommand() {
      return this.message.length() > 0 && this.message.charAt(0) == '/';
   }

   public boolean isProxyCommand() {
      if (!this.isCommand()) {
         return false;
      } else {
         int var1 = this.message.indexOf(" ");
         String var2 = var1 == -1 ? this.message.substring(1) : this.message.substring(1, var1);
         CommandSender var3 = this.getSender() instanceof CommandSender ? (CommandSender)this.getSender() : null;
         return ProxyServer.getInstance().getPluginManager().isExecutableCommand(var2, var3);
      }
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public String getMessage() {
      return this.message;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   @Override
   public String toString() {
      return "ChatEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", message=" + this.getMessage() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ChatEvent)) {
         return false;
      } else {
         ChatEvent var2 = (ChatEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof ChatEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      String var3 = this.getMessage();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
