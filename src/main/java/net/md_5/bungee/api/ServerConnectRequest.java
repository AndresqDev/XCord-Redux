package net.md_5.bungee.api;

import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class ServerConnectRequest {
   @NonNull
   private final ServerInfo target;
   @NonNull
   private final ServerConnectEvent.Reason reason;
   private final Callback<ServerConnectRequest.Result> callback;
   private int connectTimeout;
   private boolean retry;

   ServerConnectRequest(@NonNull ServerInfo var1, @NonNull ServerConnectEvent.Reason var2, Callback<ServerConnectRequest.Result> var3, int var4, boolean var5) {
      if (var1 == null) {
         throw new NullPointerException("target is marked non-null but is null");
      } else if (var2 == null) {
         throw new NullPointerException("reason is marked non-null but is null");
      } else {
         this.target = var1;
         this.reason = var2;
         this.callback = var3;
         this.connectTimeout = var4;
         this.retry = var5;
      }
   }

   public static ServerConnectRequest.Builder builder() {
      return new ServerConnectRequest.Builder();
   }

   @NonNull
   public ServerInfo getTarget() {
      return this.target;
   }

   @NonNull
   public ServerConnectEvent.Reason getReason() {
      return this.reason;
   }

   public Callback<ServerConnectRequest.Result> getCallback() {
      return this.callback;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public boolean isRetry() {
      return this.retry;
   }

   public void setConnectTimeout(int var1) {
      this.connectTimeout = var1;
   }

   public void setRetry(boolean var1) {
      this.retry = var1;
   }

   public static class Builder {
      private ServerInfo target;
      private ServerConnectEvent.Reason reason;
      private Callback<ServerConnectRequest.Result> callback;
      private boolean retry;
      private int connectTimeout = ProxyServer.getInstance().getConfig().getServerConnectTimeout();

      Builder() {
      }

      public ServerConnectRequest.Builder target(@NonNull ServerInfo var1) {
         if (var1 == null) {
            throw new NullPointerException("target is marked non-null but is null");
         } else {
            this.target = var1;
            return this;
         }
      }

      public ServerConnectRequest.Builder reason(@NonNull ServerConnectEvent.Reason var1) {
         if (var1 == null) {
            throw new NullPointerException("reason is marked non-null but is null");
         } else {
            this.reason = var1;
            return this;
         }
      }

      public ServerConnectRequest.Builder callback(Callback<ServerConnectRequest.Result> var1) {
         this.callback = var1;
         return this;
      }

      public ServerConnectRequest.Builder connectTimeout(int var1) {
         this.connectTimeout = var1;
         return this;
      }

      public ServerConnectRequest.Builder retry(boolean var1) {
         this.retry = var1;
         return this;
      }

      public ServerConnectRequest build() {
         return new ServerConnectRequest(this.target, this.reason, this.callback, this.connectTimeout, this.retry);
      }

      @Override
      public String toString() {
         return "ServerConnectRequest.Builder(target="
            + this.target
            + ", reason="
            + this.reason
            + ", callback="
            + this.callback
            + ", connectTimeout="
            + this.connectTimeout
            + ", retry="
            + this.retry
            + ")";
      }
   }

   public static enum Result {
      EVENT_CANCEL,
      ALREADY_CONNECTED,
      ALREADY_CONNECTING,
      SUCCESS,
      FAIL;
   }
}
