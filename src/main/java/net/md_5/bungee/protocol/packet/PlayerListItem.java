package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListItem extends MultiVersionPacketV17 {
   private PlayerListItem.Action action;
   private PlayerListItem.Item[] items;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.items = new PlayerListItem.Item[1];
      PlayerListItem.Item var4 = this.items[0] = new PlayerListItem.Item();
      var4.displayName = var4.username = readString(var1);
      this.action = !var1.readBoolean() ? PlayerListItem.Action.REMOVE_PLAYER : PlayerListItem.Action.ADD_PLAYER;
      var4.ping = var1.readShort();
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.action = PlayerListItem.Action.values()[DefinedPacket.readVarInt(var1)];
      this.items = new PlayerListItem.Item[DefinedPacket.readVarInt(var1)];

      for (int var4 = 0; var4 < this.items.length; var4++) {
         PlayerListItem.Item var5 = this.items[var4] = new PlayerListItem.Item();
         var5.setUuid(DefinedPacket.readUUID(var1));
         switch (this.action) {
            case ADD_PLAYER:
               var5.username = DefinedPacket.readString(var1);
               var5.properties = DefinedPacket.readProperties(var1);
               var5.gamemode = DefinedPacket.readVarInt(var1);
               var5.ping = DefinedPacket.readVarInt(var1);
               if (var1.readBoolean()) {
                  var5.displayName = DefinedPacket.readString(var1);
               }

               if (var3 >= 759) {
                  var5.publicKey = readPublicKey(var1);
               }
               break;
            case UPDATE_GAMEMODE:
               var5.gamemode = DefinedPacket.readVarInt(var1);
               break;
            case UPDATE_LATENCY:
               var5.ping = DefinedPacket.readVarInt(var1);
               break;
            case UPDATE_DISPLAY_NAME:
               if (var1.readBoolean()) {
                  var5.displayName = DefinedPacket.readString(var1);
               }
         }
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      PlayerListItem.Item var4 = this.items[0];
      writeString(var4.displayName, var1);
      var1.writeBoolean(this.action != PlayerListItem.Action.REMOVE_PLAYER);
      var1.writeShort(var4.ping);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      DefinedPacket.writeVarInt(this.action.ordinal(), var1);
      DefinedPacket.writeVarInt(this.items.length, var1);

      for (PlayerListItem.Item var7 : this.items) {
         DefinedPacket.writeUUID(var7.uuid, var1);
         switch (this.action) {
            case ADD_PLAYER:
               DefinedPacket.writeString(var7.username, var1);
               DefinedPacket.writeProperties(var7.properties, var1);
               DefinedPacket.writeVarInt(var7.gamemode, var1);
               DefinedPacket.writeVarInt(var7.ping, var1);
               var1.writeBoolean(var7.displayName != null);
               if (var7.displayName != null) {
                  DefinedPacket.writeString(var7.displayName, var1);
               }

               if (var3 >= 759) {
                  writePublicKey(var7.publicKey, var1);
               }
               break;
            case UPDATE_GAMEMODE:
               DefinedPacket.writeVarInt(var7.gamemode, var1);
               break;
            case UPDATE_LATENCY:
               DefinedPacket.writeVarInt(var7.ping, var1);
               break;
            case UPDATE_DISPLAY_NAME:
               var1.writeBoolean(var7.displayName != null);
               if (var7.displayName != null) {
                  DefinedPacket.writeString(var7.displayName, var1);
               }
         }
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public PlayerListItem.Action getAction() {
      return this.action;
   }

   public PlayerListItem.Item[] getItems() {
      return this.items;
   }

   public void setAction(PlayerListItem.Action var1) {
      this.action = var1;
   }

   public void setItems(PlayerListItem.Item[] var1) {
      this.items = var1;
   }

   public String toString() {
      return "PlayerListItem(action=" + this.getAction() + ", items=" + Arrays.deepToString(this.getItems()) + ")";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerListItem)) {
         return false;
      } else {
         PlayerListItem var2 = (PlayerListItem)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            PlayerListItem.Action var3 = this.getAction();
            PlayerListItem.Action var4 = var2.getAction();
            return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.deepEquals(this.getItems(), var2.getItems()) : false;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerListItem;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      PlayerListItem.Action var3 = this.getAction();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      return var2 * 59 + Arrays.deepHashCode(this.getItems());
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAMEMODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public static class Item {
      private UUID uuid;
      private String username;
      private Property[] properties;
      private PlayerPublicKey publicKey;
      private int gamemode;
      private int ping;
      private String displayName;

      public UUID getUuid() {
         return this.uuid;
      }

      public String getUsername() {
         return this.username;
      }

      public Property[] getProperties() {
         return this.properties;
      }

      public PlayerPublicKey getPublicKey() {
         return this.publicKey;
      }

      public int getGamemode() {
         return this.gamemode;
      }

      public int getPing() {
         return this.ping;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public void setUuid(UUID var1) {
         this.uuid = var1;
      }

      public void setUsername(String var1) {
         this.username = var1;
      }

      public void setProperties(Property[] var1) {
         this.properties = var1;
      }

      public void setPublicKey(PlayerPublicKey var1) {
         this.publicKey = var1;
      }

      public void setGamemode(int var1) {
         this.gamemode = var1;
      }

      public void setPing(int var1) {
         this.ping = var1;
      }

      public void setDisplayName(String var1) {
         this.displayName = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PlayerListItem.Item)) {
            return false;
         } else {
            PlayerListItem.Item var2 = (PlayerListItem.Item)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else if (this.getGamemode() != var2.getGamemode()) {
               return false;
            } else if (this.getPing() != var2.getPing()) {
               return false;
            } else {
               UUID var3 = this.getUuid();
               UUID var4 = var2.getUuid();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  String var5 = this.getUsername();
                  String var6 = var2.getUsername();
                  if (var5 == null ? var6 == null : var5.equals(var6)) {
                     if (!Arrays.deepEquals(this.getProperties(), var2.getProperties())) {
                        return false;
                     } else {
                        PlayerPublicKey var7 = this.getPublicKey();
                        PlayerPublicKey var8 = var2.getPublicKey();
                        if (var7 == null ? var8 == null : var7.equals(var8)) {
                           String var9 = this.getDisplayName();
                           String var10 = var2.getDisplayName();
                           return var9 == null ? var10 == null : var9.equals(var10);
                        } else {
                           return false;
                        }
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
         return var1 instanceof PlayerListItem.Item;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getGamemode();
         var2 = var2 * 59 + this.getPing();
         UUID var3 = this.getUuid();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         String var4 = this.getUsername();
         var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
         var2 = var2 * 59 + Arrays.deepHashCode(this.getProperties());
         PlayerPublicKey var5 = this.getPublicKey();
         var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
         String var6 = this.getDisplayName();
         return var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      }

      @Override
      public String toString() {
         return "PlayerListItem.Item(uuid="
            + this.getUuid()
            + ", username="
            + this.getUsername()
            + ", properties="
            + Arrays.deepToString(this.getProperties())
            + ", publicKey="
            + this.getPublicKey()
            + ", gamemode="
            + this.getGamemode()
            + ", ping="
            + this.getPing()
            + ", displayName="
            + this.getDisplayName()
            + ")";
      }
   }
}
