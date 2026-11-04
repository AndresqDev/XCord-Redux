package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.connection.LoginResult;

public class LoginEvent extends AsyncEvent<LoginEvent> implements Cancellable {
   private boolean cancelled;
   private BaseComponent[] cancelReasonComponents;
   private LoginResult loginResult;
   private final PendingConnection connection;

   public LoginEvent(PendingConnection var1, Callback<LoginEvent> var2) {
      super(var2);
      this.connection = var1;
   }

   public LoginEvent(PendingConnection var1, Callback<LoginEvent> var2, LoginResult var3) {
      super(var2);
      this.connection = var1;
      this.loginResult = var3;
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

   public LoginResult getLoginResult() {
      return this.loginResult;
   }

   public PendingConnection getConnection() {
      return this.connection;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public void setLoginResult(LoginResult var1) {
      this.loginResult = var1;
   }

   @Override
   public String toString() {
      return "LoginEvent(cancelled="
         + this.isCancelled()
         + ", cancelReasonComponents="
         + Arrays.deepToString(this.getCancelReasonComponents())
         + ", loginResult="
         + this.getLoginResult()
         + ", connection="
         + this.getConnection()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginEvent)) {
         return false;
      } else {
         LoginEvent var2 = (LoginEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else if (!Arrays.deepEquals(this.getCancelReasonComponents(), var2.getCancelReasonComponents())) {
            return false;
         } else {
            LoginResult var3 = this.getLoginResult();
            LoginResult var4 = var2.getLoginResult();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               PendingConnection var5 = this.getConnection();
               PendingConnection var6 = var2.getConnection();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      var2 = var2 * 59 + Arrays.deepHashCode(this.getCancelReasonComponents());
      LoginResult var3 = this.getLoginResult();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      PendingConnection var4 = this.getConnection();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
