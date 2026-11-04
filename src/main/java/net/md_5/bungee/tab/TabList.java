package net.md_5.bungee.tab;

import java.util.Map;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.packet.PlayerListItem;

public abstract class TabList {
   protected final ProxiedPlayer player;

   public abstract void onUpdate(PlayerListItem var1);

   public abstract void onPingChange(int var1);

   public abstract void onServerChange();

   public abstract void onConnect();

   public abstract void onDisconnect();

   public static PlayerListItem rewrite(PlayerListItem var0) {
      try {
         Map var1 = BungeeCord.getInstance().retainAndGetOfflineUUIDMap();

         for (PlayerListItem.Item var5 : var0.getItems()) {
            if (var5.getUuid() != null) {
               UserConnection var6 = (UserConnection)var1.get(var5.getUuid());
               if (var6 != null) {
                  var5.setUuid(var6.getUniqueId());
                  LoginResult var7 = var6.getPendingConnection().getLoginProfile();
                  if (var7 != null && var7.getProperties() != null) {
                     Property[] var8 = new Property[var7.getProperties().length];

                     for (int var9 = 0; var9 < var8.length; var9++) {
                        var8[var9] = new Property(
                           var7.getProperties()[var9].getName(), var7.getProperties()[var9].getValue(), var7.getProperties()[var9].getSignature()
                        );
                     }

                     var5.setProperties(var8);
                  } else {
                     var5.setProperties(new Property[0]);
                  }

                  if (var0.getAction() == PlayerListItem.Action.ADD_PLAYER || var0.getAction() == PlayerListItem.Action.UPDATE_GAMEMODE) {
                     var6.setGamemode(var5.getGamemode());
                  }

                  if (var0.getAction() == PlayerListItem.Action.ADD_PLAYER || var0.getAction() == PlayerListItem.Action.UPDATE_LATENCY) {
                     var6.setPing(var5.getPing());
                  }
               }
            }
         }
      } finally {
         BungeeCord.getInstance().releaseOfflineUUIDMap();
      }

      return var0;
   }

   public TabList(ProxiedPlayer var1) {
      this.player = var1;
   }
}
