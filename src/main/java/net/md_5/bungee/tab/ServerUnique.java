package net.md_5.bungee.tab;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PlayerListItem;

public class ServerUnique extends TabList {
   private final Collection<UUID> uuids = new HashSet<>();
   private final Collection<String> usernames = new HashSet<>();

   public ServerUnique(ProxiedPlayer var1) {
      super(var1);
   }

   @Override
   public void onUpdate(PlayerListItem var1) {
      for (PlayerListItem.Item var5 : var1.getItems()) {
         if (var1.getAction() == PlayerListItem.Action.ADD_PLAYER) {
            if (var5.getUuid() != null) {
               this.uuids.add(var5.getUuid());
            } else {
               this.usernames.add(var5.getUsername());
            }
         } else if (var1.getAction() == PlayerListItem.Action.REMOVE_PLAYER) {
            if (var5.getUuid() != null) {
               this.uuids.remove(var5.getUuid());
            } else {
               this.usernames.remove(var5.getUsername());
            }
         }
      }

      this.player.unsafe().sendPacket(var1);
   }

   @Override
   public void onPingChange(int var1) {
   }

   @Override
   public void onServerChange() {
      PlayerListItem var1 = new PlayerListItem();
      var1.setAction(PlayerListItem.Action.REMOVE_PLAYER);
      PlayerListItem.Item[] var2 = new PlayerListItem.Item[this.uuids.size() + this.usernames.size()];
      int var3 = 0;

      for (UUID var5 : this.uuids) {
         PlayerListItem.Item var6 = var2[var3++] = new PlayerListItem.Item();
         var6.setUuid(var5);
      }

      for (String var11 : this.usernames) {
         PlayerListItem.Item var13 = var2[var3++] = new PlayerListItem.Item();
         var13.setUsername(var11);
         var13.setDisplayName(var11);
      }

      var1.setItems(var2);
      if (ProtocolConstants.isAfterOrEq(this.player.getPendingConnection().getVersion(), 47)) {
         this.player.unsafe().sendPacket(var1);
      } else {
         for (PlayerListItem.Item var7 : var1.getItems()) {
            PlayerListItem var8 = new PlayerListItem();
            var8.setAction(var1.getAction());
            var8.setItems(new PlayerListItem.Item[]{var7});
            this.player.unsafe().sendPacket(var8);
         }
      }

      this.uuids.clear();
      this.usernames.clear();
   }

   @Override
   public void onConnect() {
   }

   @Override
   public void onDisconnect() {
   }
}
