package net.md_5.bungee.protocol;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.function.Supplier;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClearTitles;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.EntityEffect;
import net.md_5.bungee.protocol.packet.EntityRemoveEffect;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.GameState;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginPayloadRequest;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PlayerPosition;
import net.md_5.bungee.protocol.packet.PlayerPositionAndLook;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ServerData;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.protocol.packet.Subtitle;
import net.md_5.bungee.protocol.packet.SystemChat;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.protocol.packet.TitleTimes;
import net.md_5.bungee.protocol.packet.ViewDistance;

public enum Protocol {
   HANDSHAKE {
      {
         this.TO_SERVER.registerPacket(Handshake.class, Handshake::new, Protocol.map(4, 0));
      }
   },
   GAME {
      {
         this.TO_CLIENT
            .registerPacket(
               KeepAlive.class,
               KeepAlive::new,
               Protocol.map(4, 0),
               Protocol.map(107, 31),
               Protocol.map(393, 33),
               Protocol.map(477, 32),
               Protocol.map(573, 33),
               Protocol.map(735, 32),
               Protocol.map(751, 31),
               Protocol.map(755, 33),
               Protocol.map(759, 30),
               Protocol.map(760, 32)
            );
         this.TO_CLIENT
            .registerPacket(
               Login.class,
               Login::new,
               Protocol.map(4, 1),
               Protocol.map(107, 35),
               Protocol.map(393, 37),
               Protocol.map(573, 38),
               Protocol.map(735, 37),
               Protocol.map(751, 36),
               Protocol.map(755, 38),
               Protocol.map(759, 35),
               Protocol.map(760, 37)
            );
         this.TO_CLIENT
            .registerPacket(
               Chat.class,
               Chat::new,
               Protocol.map(4, 2),
               Protocol.map(107, 15),
               Protocol.map(393, 14),
               Protocol.map(573, 15),
               Protocol.map(735, 14),
               Protocol.map(755, 15)
            );
         this.TO_CLIENT
            .registerPacket(
               Respawn.class,
               Respawn::new,
               Protocol.map(4, 7),
               Protocol.map(107, 51),
               Protocol.map(335, 52),
               Protocol.map(338, 53),
               Protocol.map(393, 56),
               Protocol.map(477, 58),
               Protocol.map(573, 59),
               Protocol.map(735, 58),
               Protocol.map(751, 57),
               Protocol.map(755, 61),
               Protocol.map(759, 59),
               Protocol.map(760, 62)
            );
         this.TO_CLIENT
            .registerPacket(
               BossBar.class, BossBar::new, Protocol.map(107, 12), Protocol.map(573, 13), Protocol.map(735, 12), Protocol.map(755, 13), Protocol.map(759, 10)
            );
         this.TO_CLIENT.registerPacket(EntityEffect.class, EntityEffect::new, Protocol.map(4, 29), Protocol.map(107, Integer.MIN_VALUE));
         this.TO_CLIENT.registerPacket(EntityRemoveEffect.class, EntityRemoveEffect::new, Protocol.map(4, 30), Protocol.map(107, Integer.MIN_VALUE));
         this.TO_CLIENT
            .registerPacket(
               PlayerListItem.class,
               PlayerListItem::new,
               Protocol.map(4, 56),
               Protocol.map(107, 45),
               Protocol.map(338, 46),
               Protocol.map(393, 48),
               Protocol.map(477, 51),
               Protocol.map(573, 52),
               Protocol.map(735, 51),
               Protocol.map(751, 50),
               Protocol.map(755, 54),
               Protocol.map(759, 52),
               Protocol.map(760, 55)
            );
         this.TO_CLIENT
            .registerPacket(
               TabCompleteResponse.class,
               TabCompleteResponse::new,
               Protocol.map(4, 58),
               Protocol.map(107, 14),
               Protocol.map(393, 16),
               Protocol.map(573, 17),
               Protocol.map(735, 16),
               Protocol.map(751, 15),
               Protocol.map(755, 17),
               Protocol.map(759, 14)
            );
         this.TO_CLIENT
            .registerPacket(
               ScoreboardObjective.class,
               ScoreboardObjective::new,
               Protocol.map(4, 59),
               Protocol.map(107, 63),
               Protocol.map(335, 65),
               Protocol.map(338, 66),
               Protocol.map(393, 69),
               Protocol.map(477, 73),
               Protocol.map(573, 74),
               Protocol.map(755, 83),
               Protocol.map(760, 86)
            );
         this.TO_CLIENT
            .registerPacket(
               ScoreboardScore.class,
               ScoreboardScore::new,
               Protocol.map(4, 60),
               Protocol.map(107, 66),
               Protocol.map(335, 68),
               Protocol.map(338, 69),
               Protocol.map(393, 72),
               Protocol.map(477, 76),
               Protocol.map(573, 77),
               Protocol.map(755, 86),
               Protocol.map(760, 89)
            );
         this.TO_CLIENT
            .registerPacket(
               ScoreboardDisplay.class,
               ScoreboardDisplay::new,
               Protocol.map(4, 61),
               Protocol.map(107, 56),
               Protocol.map(335, 58),
               Protocol.map(338, 59),
               Protocol.map(393, 62),
               Protocol.map(477, 66),
               Protocol.map(573, 67),
               Protocol.map(755, 76),
               Protocol.map(760, 79)
            );
         this.TO_CLIENT
            .registerPacket(
               Team.class,
               Team::new,
               Protocol.map(4, 62),
               Protocol.map(107, 65),
               Protocol.map(335, 67),
               Protocol.map(338, 68),
               Protocol.map(393, 71),
               Protocol.map(477, 75),
               Protocol.map(573, 76),
               Protocol.map(755, 85),
               Protocol.map(760, 88)
            );
         this.TO_CLIENT
            .registerPacket(
               PluginMessage.class,
               PluginMessage::new,
               Protocol.map(4, 63),
               Protocol.map(107, 24),
               Protocol.map(393, 25),
               Protocol.map(477, 24),
               Protocol.map(573, 25),
               Protocol.map(735, 24),
               Protocol.map(751, 23),
               Protocol.map(755, 24),
               Protocol.map(759, 21),
               Protocol.map(760, 22)
            );
         this.TO_CLIENT
            .registerPacket(
               Kick.class,
               Kick::new,
               Protocol.map(4, 64),
               Protocol.map(107, 26),
               Protocol.map(393, 27),
               Protocol.map(477, 26),
               Protocol.map(573, 27),
               Protocol.map(735, 26),
               Protocol.map(751, 25),
               Protocol.map(755, 26),
               Protocol.map(759, 23),
               Protocol.map(760, 25)
            );
         this.TO_CLIENT
            .registerPacket(
               Title.class,
               Title::new,
               Protocol.map(4, 69),
               Protocol.map(335, 71),
               Protocol.map(338, 72),
               Protocol.map(393, 75),
               Protocol.map(477, 79),
               Protocol.map(573, 80),
               Protocol.map(735, 79),
               Protocol.map(755, 89),
               Protocol.map(757, 90),
               Protocol.map(760, 93)
            );
         this.TO_CLIENT.registerPacket(ClearTitles.class, ClearTitles::new, Protocol.map(755, 16), Protocol.map(759, 13));
         this.TO_CLIENT.registerPacket(Subtitle.class, Subtitle::new, Protocol.map(755, 87), Protocol.map(757, 88), Protocol.map(760, 91));
         this.TO_CLIENT.registerPacket(SystemChat.class, SystemChat::new, Protocol.map(759, 95), Protocol.map(760, 98));
         this.TO_CLIENT.registerPacket(TitleTimes.class, TitleTimes::new, Protocol.map(755, 90), Protocol.map(757, 91), Protocol.map(760, 94));
         this.TO_CLIENT
            .registerPacket(
               PlayerListHeaderFooter.class,
               PlayerListHeaderFooter::new,
               Protocol.map(4, 71),
               Protocol.map(107, 72),
               Protocol.map(110, 71),
               Protocol.map(335, 73),
               Protocol.map(338, 74),
               Protocol.map(393, 78),
               Protocol.map(477, 83),
               Protocol.map(573, 84),
               Protocol.map(735, 83),
               Protocol.map(755, 94),
               Protocol.map(757, 95),
               Protocol.map(759, 96),
               Protocol.map(760, 99)
            );
         this.TO_CLIENT
            .registerPacket(
               EntityStatus.class,
               EntityStatus::new,
               Protocol.map(4, 26),
               Protocol.map(107, 27),
               Protocol.map(393, 28),
               Protocol.map(477, 27),
               Protocol.map(573, 28),
               Protocol.map(735, 27),
               Protocol.map(751, 26),
               Protocol.map(755, 27),
               Protocol.map(759, 24),
               Protocol.map(760, 26)
            );
         this.TO_CLIENT
            .registerPacket(
               Commands.class,
               Commands::new,
               Protocol.map(393, 17),
               Protocol.map(573, 18),
               Protocol.map(735, 17),
               Protocol.map(751, 16),
               Protocol.map(755, 18),
               Protocol.map(759, 15)
            );
         this.TO_CLIENT
            .registerPacket(
               GameState.class,
               GameState::new,
               Protocol.map(573, 31),
               Protocol.map(735, 30),
               Protocol.map(751, 29),
               Protocol.map(755, 30),
               Protocol.map(759, 27),
               Protocol.map(760, 29)
            );
         this.TO_CLIENT
            .registerPacket(
               ViewDistance.class,
               ViewDistance::new,
               Protocol.map(477, 65),
               Protocol.map(573, 66),
               Protocol.map(735, 65),
               Protocol.map(755, 74),
               Protocol.map(759, 73),
               Protocol.map(760, 76)
            );
         this.TO_CLIENT.registerPacket(ServerData.class, ServerData::new, Protocol.map(759, 63), Protocol.map(760, 66));
         this.TO_SERVER
            .registerPacket(
               KeepAlive.class,
               KeepAlive::new,
               Protocol.map(4, 0),
               Protocol.map(107, 11),
               Protocol.map(335, 12),
               Protocol.map(338, 11),
               Protocol.map(393, 14),
               Protocol.map(477, 15),
               Protocol.map(735, 16),
               Protocol.map(755, 15),
               Protocol.map(759, 17),
               Protocol.map(760, 18)
            );
         this.TO_SERVER
            .registerPacket(Chat.class, Chat::new, Protocol.map(4, 1), Protocol.map(107, 2), Protocol.map(335, 3), Protocol.map(338, 2), Protocol.map(477, 3));
         this.TO_SERVER.registerPacket(ClientCommand.class, ClientCommand::new, Protocol.map(759, 3), Protocol.map(760, 4));
         this.TO_SERVER.registerPacket(ClientChat.class, ClientChat::new, Protocol.map(759, 4), Protocol.map(760, 5));
         this.TO_SERVER
            .registerPacket(
               TabCompleteRequest.class,
               TabCompleteRequest::new,
               Protocol.map(4, 20),
               Protocol.map(107, 1),
               Protocol.map(335, 2),
               Protocol.map(338, 1),
               Protocol.map(393, 5),
               Protocol.map(477, 6),
               Protocol.map(759, 8),
               Protocol.map(760, 9)
            );
         this.TO_SERVER
            .registerPacket(
               ClientSettings.class,
               ClientSettings::new,
               Protocol.map(4, 21),
               Protocol.map(107, 4),
               Protocol.map(335, 5),
               Protocol.map(338, 4),
               Protocol.map(477, 5),
               Protocol.map(759, 7),
               Protocol.map(760, 8)
            );
         this.TO_SERVER
            .registerPacket(
               PluginMessage.class,
               PluginMessage::new,
               Protocol.map(4, 23),
               Protocol.map(107, 9),
               Protocol.map(335, 10),
               Protocol.map(338, 9),
               Protocol.map(393, 10),
               Protocol.map(477, 11),
               Protocol.map(755, 10),
               Protocol.map(759, 12),
               Protocol.map(760, 13)
            );
      }
   },
   STATUS {
      {
         this.TO_CLIENT.registerPacket(StatusResponse.class, StatusResponse::new, Protocol.map(4, 0));
         this.TO_CLIENT.registerPacket(PingPacket.class, PingPacket::new, Protocol.map(4, 1));
         this.TO_SERVER.registerPacket(StatusRequest.class, StatusRequest::new, Protocol.map(4, 0));
         this.TO_SERVER.registerPacket(PingPacket.class, PingPacket::new, Protocol.map(4, 1));
      }
   },
   LOGIN {
      {
         this.TO_CLIENT.registerPacket(Kick.class, Kick::new, Protocol.map(4, 0));
         this.TO_CLIENT.registerPacket(EncryptionRequest.class, EncryptionRequest::new, Protocol.map(4, 1));
         this.TO_CLIENT.registerPacket(LoginSuccess.class, LoginSuccess::new, Protocol.map(4, 2));
         this.TO_CLIENT.registerPacket(SetCompression.class, SetCompression::new, Protocol.map(4, 3));
         this.TO_CLIENT.registerPacket(LoginPayloadRequest.class, LoginPayloadRequest::new, Protocol.map(393, 4));
         this.TO_SERVER.registerPacket(LoginRequest.class, LoginRequest::new, Protocol.map(4, 0));
         this.TO_SERVER.registerPacket(EncryptionResponse.class, EncryptionResponse::new, Protocol.map(4, 1));
         this.TO_SERVER.registerPacket(LoginPayloadResponse.class, LoginPayloadResponse::new, Protocol.map(393, 2));
      }
   },
   BOT_CHECK {
      {
         this.TO_SERVER.registerPacket(ClientChat.class, ClientChat::new, Protocol.map(759, 4), Protocol.map(760, 5));
         this.TO_SERVER
            .registerPacket(
               KeepAlive.class,
               KeepAlive::new,
               Protocol.map(4, 0),
               Protocol.map(107, 11),
               Protocol.map(335, 12),
               Protocol.map(338, 11),
               Protocol.map(393, 14),
               Protocol.map(477, 15),
               Protocol.map(735, 16),
               Protocol.map(755, 15),
               Protocol.map(759, 17),
               Protocol.map(760, 18)
            );
         this.TO_CLIENT
            .registerPacket(
               Kick.class,
               Kick::new,
               Protocol.map(4, 64),
               Protocol.map(107, 26),
               Protocol.map(393, 27),
               Protocol.map(477, 26),
               Protocol.map(573, 27),
               Protocol.map(735, 26),
               Protocol.map(751, 25),
               Protocol.map(755, 26),
               Protocol.map(759, 23),
               Protocol.map(760, 25)
            );
         this.TO_CLIENT
            .registerPacket(
               Respawn.class,
               Respawn::new,
               Protocol.map(4, 7),
               Protocol.map(107, 51),
               Protocol.map(335, 52),
               Protocol.map(338, 53),
               Protocol.map(393, 56),
               Protocol.map(477, 58),
               Protocol.map(573, 59),
               Protocol.map(735, 58),
               Protocol.map(751, 57),
               Protocol.map(755, 61),
               Protocol.map(759, 59),
               Protocol.map(760, 62)
            );
         this.TO_CLIENT
            .registerPacket(
               Chat.class,
               Chat::new,
               Protocol.map(4, 2),
               Protocol.map(107, 15),
               Protocol.map(393, 14),
               Protocol.map(573, 15),
               Protocol.map(735, 14),
               Protocol.map(755, 15)
            );
         this.TO_CLIENT
            .registerPacket(
               KeepAlive.class,
               KeepAlive::new,
               Protocol.map(4, 0),
               Protocol.map(107, 31),
               Protocol.map(393, 33),
               Protocol.map(477, 32),
               Protocol.map(573, 33),
               Protocol.map(735, 32),
               Protocol.map(751, 31),
               Protocol.map(755, 33),
               Protocol.map(759, 30),
               Protocol.map(760, 32)
            );
         this.TO_CLIENT
            .registerPacket(
               PlayerPositionAndLook.class,
               PlayerPositionAndLook::new,
               Protocol.map(4, 8),
               Protocol.map(107, 46),
               Protocol.map(338, 47),
               Protocol.map(393, 50),
               Protocol.map(477, 53),
               Protocol.map(573, 54),
               Protocol.map(735, 53),
               Protocol.map(751, 52),
               Protocol.map(755, 56),
               Protocol.map(759, 54),
               Protocol.map(760, 57)
            );
         this.TO_SERVER
            .registerPacket(
               PlayerPositionAndLook.class,
               PlayerPositionAndLook::new,
               Protocol.map(4, 6),
               Protocol.map(107, 13),
               Protocol.map(335, 15),
               Protocol.map(338, 14),
               Protocol.map(393, 17),
               Protocol.map(477, 18),
               Protocol.map(735, 19),
               Protocol.map(755, 18),
               Protocol.map(759, 20),
               Protocol.map(760, 21)
            );
         this.TO_SERVER
            .registerPacket(
               PlayerPosition.class,
               PlayerPosition::new,
               Protocol.map(4, 4),
               Protocol.map(107, 12),
               Protocol.map(335, 14),
               Protocol.map(338, 13),
               Protocol.map(393, 16),
               Protocol.map(477, 17),
               Protocol.map(735, 18),
               Protocol.map(755, 17),
               Protocol.map(759, 19),
               Protocol.map(760, 20)
            );
      }
   };

   public static final int MAX_PACKET_ID = 255;
   final Protocol.DirectionData TO_SERVER = new Protocol.DirectionData(this, ProtocolConstants.Direction.TO_SERVER);
   public final Protocol.DirectionData TO_CLIENT = new Protocol.DirectionData(this, ProtocolConstants.Direction.TO_CLIENT);

   private Protocol() {
   }

   public static void main(String[] var0) {
      for (int var2 : ProtocolConstants.SUPPORTED_VERSION_IDS) {
         dump(var2);
      }
   }

   private static void dump(int var0) {
      for (Protocol var4 : values()) {
         dump(var0, var4);
      }
   }

   private static void dump(int var0, Protocol var1) {
      dump(var0, var1.TO_CLIENT);
      dump(var0, var1.TO_SERVER);
   }

   private static void dump(int var0, Protocol.DirectionData var1) {
      for (int var2 = 0; var2 < 255; var2++) {
         DefinedPacket var3 = var1.createPacket(var2, var0);
         if (var3 != null) {
            System.out.println(var0 + " " + var1.protocolPhase + " " + var1.direction + " " + var2 + " " + var3.getClass().getSimpleName());
         }
      }
   }

   private static Protocol.ProtocolMapping map(int var0, int var1) {
      return new Protocol.ProtocolMapping(var0, var1);
   }

   public static final class DirectionData {
      private final TIntObjectMap<Protocol.ProtocolData> protocols = new TIntObjectHashMap();
      private final Protocol protocolPhase;
      private final ProtocolConstants.Direction direction;

      public DirectionData(Protocol var1, ProtocolConstants.Direction var2) {
         this.protocolPhase = var1;
         this.direction = var2;

         for (int var4 : ProtocolConstants.SUPPORTED_VERSION_IDS) {
            this.protocols.put(var4, new Protocol.ProtocolData(var4));
         }
      }

      private Protocol.ProtocolData getProtocolData(int var1) {
         Protocol.ProtocolData var2 = (Protocol.ProtocolData)this.protocols.get(var1);
         if (var2 == null && this.protocolPhase != Protocol.GAME) {
            var2 = (Protocol.ProtocolData)Iterables.getFirst(this.protocols.valueCollection(), null);
         }

         return var2;
      }

      public boolean hasPacket(int var1, boolean var2) {
         return var2 || var1 >= 0 && var1 <= 255;
      }

      public final DefinedPacket createPacket(int var1, int var2) {
         return this.createPacket(var1, var2, true);
      }

      public final DefinedPacket createPacket(int var1, int var2, boolean var3) {
         Protocol.ProtocolData var4 = this.getProtocolData(var2);
         if (var4 == null) {
            throw new BadPacketException("Unsupported protocol version " + var2);
         } else if (var1 < 0) {
            throw new BadPacketException("Packet with id " + var1 + " outside of range ");
         } else if (!this.hasPacket(var1, var3)) {
            if (ProtocolConstants.isBeforeOrEq(var2, 5)) {
               return null;
            } else {
               throw new BadPacketException("Packet with id " + var1 + " outside of range ");
            }
         } else {
            Supplier var5 = var4.packetConstructors[var1];
            return var5 == null ? null : (DefinedPacket)var5.get();
         }
      }

      private <P extends DefinedPacket> void registerPacket(Class<P> var1, Supplier<P> var2, Protocol.ProtocolMapping... var3) {
         int var4 = 0;
         Protocol.ProtocolMapping var5 = var3[var4];

         for (int var7 : ProtocolConstants.SUPPORTED_VERSION_IDS) {
            if (var7 >= var5.protocolVersion) {
               if (var5.protocolVersion < var7 && var4 + 1 < var3.length) {
                  Protocol.ProtocolMapping var8 = var3[var4 + 1];
                  if (var8.protocolVersion == var7) {
                     Preconditions.checkState(var8.packetID != var5.packetID, "Duplicate packet mapping (%s, %s)", var5.protocolVersion, var8.protocolVersion);
                     var5 = var8;
                     var4++;
                  }
               }

               if (var5.packetID < 0) {
                  break;
               }

               if (var5.packetID != Integer.MIN_VALUE) {
                  Protocol.ProtocolData var9 = (Protocol.ProtocolData)this.protocols.get(var7);
                  var9.packetMap.put(var1, var5.packetID);
                  var9.packetConstructors[var5.packetID] = var2;
               }
            }
         }
      }

      public final int getId(Class<? extends DefinedPacket> var1, int var2) {
         Protocol.ProtocolData var3 = this.getProtocolData(var2);
         if (var3 == null) {
            throw new BadPacketException("Unsupported protocol version: " + var2);
         } else {
            Preconditions.checkArgument(
               var3.packetMap.containsKey(var1),
               "Cannot get ID for packet %s in phase %s with direction %s for version ",
               var1,
               this.protocolPhase,
               this.direction,
               var2
            );
            return var3.packetMap.get(var1);
         }
      }

      public ProtocolConstants.Direction getDirection() {
         return this.direction;
      }
   }

   private static class ProtocolData {
      private final int protocolVersion;
      private final TObjectIntMap<Class<? extends DefinedPacket>> packetMap = new TObjectIntHashMap(255);
      private final Supplier<? extends DefinedPacket>[] packetConstructors = new Supplier[255];

      public ProtocolData(int var1) {
         this.protocolVersion = var1;
      }

      public int getProtocolVersion() {
         return this.protocolVersion;
      }

      public TObjectIntMap<Class<? extends DefinedPacket>> getPacketMap() {
         return this.packetMap;
      }

      public Supplier<? extends DefinedPacket>[] getPacketConstructors() {
         return this.packetConstructors;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Protocol.ProtocolData)) {
            return false;
         } else {
            Protocol.ProtocolData var2 = (Protocol.ProtocolData)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else if (this.getProtocolVersion() != var2.getProtocolVersion()) {
               return false;
            } else {
               TObjectIntMap var3 = this.getPacketMap();
               TObjectIntMap var4 = var2.getPacketMap();
               return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.deepEquals(this.getPacketConstructors(), var2.getPacketConstructors()) : false;
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof Protocol.ProtocolData;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getProtocolVersion();
         TObjectIntMap var3 = this.getPacketMap();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         return var2 * 59 + Arrays.deepHashCode(this.getPacketConstructors());
      }

      @Override
      public String toString() {
         return "Protocol.ProtocolData(protocolVersion="
            + this.getProtocolVersion()
            + ", packetMap="
            + this.getPacketMap()
            + ", packetConstructors="
            + Arrays.deepToString(this.getPacketConstructors())
            + ")";
      }
   }

   private static class ProtocolMapping {
      private final int protocolVersion;
      private final int packetID;

      public ProtocolMapping(int var1, int var2) {
         this.protocolVersion = var1;
         this.packetID = var2;
      }

      public int getProtocolVersion() {
         return this.protocolVersion;
      }

      public int getPacketID() {
         return this.packetID;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Protocol.ProtocolMapping)) {
            return false;
         } else {
            Protocol.ProtocolMapping var2 = (Protocol.ProtocolMapping)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               return this.getProtocolVersion() != var2.getProtocolVersion() ? false : this.getPacketID() == var2.getPacketID();
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof Protocol.ProtocolMapping;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getProtocolVersion();
         return var2 * 59 + this.getPacketID();
      }

      @Override
      public String toString() {
         return "Protocol.ProtocolMapping(protocolVersion=" + this.getProtocolVersion() + ", packetID=" + this.getPacketID() + ")";
      }
   }
}
