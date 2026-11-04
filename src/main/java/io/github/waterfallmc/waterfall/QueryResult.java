package io.github.waterfallmc.waterfall;

import java.util.List;
import lombok.NonNull;

public class QueryResult {
   @NonNull
   private String motd;
   @NonNull
   private String gameType;
   @NonNull
   private String worldName;
   private int onlinePlayers;
   private int maxPlayers;
   private int port;
   @NonNull
   private String address;
   @NonNull
   private String gameId;
   @NonNull
   private final List<String> players;
   @NonNull
   private String version;

   @NonNull
   public String getMotd() {
      return this.motd;
   }

   @NonNull
   public String getGameType() {
      return this.gameType;
   }

   @NonNull
   public String getWorldName() {
      return this.worldName;
   }

   public int getOnlinePlayers() {
      return this.onlinePlayers;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public int getPort() {
      return this.port;
   }

   @NonNull
   public String getAddress() {
      return this.address;
   }

   @NonNull
   public String getGameId() {
      return this.gameId;
   }

   @NonNull
   public List<String> getPlayers() {
      return this.players;
   }

   @NonNull
   public String getVersion() {
      return this.version;
   }

   public void setMotd(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("motd is marked non-null but is null");
      } else {
         this.motd = var1;
      }
   }

   public void setGameType(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("gameType is marked non-null but is null");
      } else {
         this.gameType = var1;
      }
   }

   public void setWorldName(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("worldName is marked non-null but is null");
      } else {
         this.worldName = var1;
      }
   }

   public void setOnlinePlayers(int var1) {
      this.onlinePlayers = var1;
   }

   public void setMaxPlayers(int var1) {
      this.maxPlayers = var1;
   }

   public void setPort(int var1) {
      this.port = var1;
   }

   public void setAddress(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("address is marked non-null but is null");
      } else {
         this.address = var1;
      }
   }

   public void setGameId(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("gameId is marked non-null but is null");
      } else {
         this.gameId = var1;
      }
   }

   public void setVersion(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("version is marked non-null but is null");
      } else {
         this.version = var1;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof QueryResult)) {
         return false;
      } else {
         QueryResult var2 = (QueryResult)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getOnlinePlayers() != var2.getOnlinePlayers()) {
            return false;
         } else if (this.getMaxPlayers() != var2.getMaxPlayers()) {
            return false;
         } else if (this.getPort() != var2.getPort()) {
            return false;
         } else {
            String var3 = this.getMotd();
            String var4 = var2.getMotd();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getGameType();
               String var6 = var2.getGameType();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getWorldName();
                  String var8 = var2.getWorldName();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     String var9 = this.getAddress();
                     String var10 = var2.getAddress();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        String var11 = this.getGameId();
                        String var12 = var2.getGameId();
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           List var13 = this.getPlayers();
                           List var14 = var2.getPlayers();
                           if (var13 == null ? var14 == null : var13.equals(var14)) {
                              String var15 = this.getVersion();
                              String var16 = var2.getVersion();
                              return var15 == null ? var16 == null : var15.equals(var16);
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
      return var1 instanceof QueryResult;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getOnlinePlayers();
      var2 = var2 * 59 + this.getMaxPlayers();
      var2 = var2 * 59 + this.getPort();
      String var3 = this.getMotd();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getGameType();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getWorldName();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getAddress();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      String var7 = this.getGameId();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      List var8 = this.getPlayers();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      String var9 = this.getVersion();
      return var2 * 59 + (var9 == null ? 43 : var9.hashCode());
   }

   @Override
   public String toString() {
      return "QueryResult(motd="
         + this.getMotd()
         + ", gameType="
         + this.getGameType()
         + ", worldName="
         + this.getWorldName()
         + ", onlinePlayers="
         + this.getOnlinePlayers()
         + ", maxPlayers="
         + this.getMaxPlayers()
         + ", port="
         + this.getPort()
         + ", address="
         + this.getAddress()
         + ", gameId="
         + this.getGameId()
         + ", players="
         + this.getPlayers()
         + ", version="
         + this.getVersion()
         + ")";
   }

   public QueryResult(
      @NonNull String var1,
      @NonNull String var2,
      @NonNull String var3,
      int var4,
      int var5,
      int var6,
      @NonNull String var7,
      @NonNull String var8,
      @NonNull List<String> var9,
      @NonNull String var10
   ) {
      if (var1 == null) {
         throw new NullPointerException("motd is marked non-null but is null");
      } else if (var2 == null) {
         throw new NullPointerException("gameType is marked non-null but is null");
      } else if (var3 == null) {
         throw new NullPointerException("worldName is marked non-null but is null");
      } else if (var7 == null) {
         throw new NullPointerException("address is marked non-null but is null");
      } else if (var8 == null) {
         throw new NullPointerException("gameId is marked non-null but is null");
      } else if (var9 == null) {
         throw new NullPointerException("players is marked non-null but is null");
      } else if (var10 == null) {
         throw new NullPointerException("version is marked non-null but is null");
      } else {
         this.motd = var1;
         this.gameType = var2;
         this.worldName = var3;
         this.onlinePlayers = var4;
         this.maxPlayers = var5;
         this.port = var6;
         this.address = var7;
         this.gameId = var8;
         this.players = var9;
         this.version = var10;
      }
   }
}
