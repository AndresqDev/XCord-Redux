package net.md_5.bungee.protocol;

import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClearTitles;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.ClientStatus;
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
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
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

public abstract class AbstractPacketHandler {
   public void handle(LegacyPing var1) throws Exception {
   }

   public void handle(TabCompleteResponse var1) throws Exception {
   }

   public void handle(PingPacket var1) throws Exception {
   }

   public void handle(StatusRequest var1) throws Exception {
   }

   public void handle(StatusResponse var1) throws Exception {
   }

   public void handle(Handshake var1) throws Exception {
   }

   public void handle(KeepAlive var1) throws Exception {
   }

   public void handle(Login var1) throws Exception {
   }

   public void handle(Chat var1) throws Exception {
   }

   public void handle(ClientChat var1) throws Exception {
   }

   public void handle(SystemChat var1) throws Exception {
   }

   public void handle(ClientCommand var1) throws Exception {
   }

   public void handle(Respawn var1) throws Exception {
   }

   public void handle(LoginRequest var1) throws Exception {
   }

   public void handle(ClientSettings var1) throws Exception {
   }

   public void handle(ClientStatus var1) throws Exception {
   }

   public void handle(PlayerListItem var1) throws Exception {
   }

   public void handle(PlayerListHeaderFooter var1) throws Exception {
   }

   public void handle(TabCompleteRequest var1) throws Exception {
   }

   public void handle(ScoreboardObjective var1) throws Exception {
   }

   public void handle(ScoreboardScore var1) throws Exception {
   }

   public void handle(EncryptionRequest var1) throws Exception {
   }

   public void handle(ScoreboardDisplay var1) throws Exception {
   }

   public void handle(Team var1) throws Exception {
   }

   public void handle(Title var1) throws Exception {
   }

   public void handle(Subtitle var1) throws Exception {
   }

   public void handle(TitleTimes var1) throws Exception {
   }

   public void handle(ClearTitles var1) throws Exception {
   }

   public void handle(PluginMessage var1) throws Exception {
   }

   public void handle(Kick var1) throws Exception {
   }

   public void handle(EncryptionResponse var1) throws Exception {
   }

   public void handle(LoginSuccess var1) throws Exception {
   }

   public void handle(LegacyHandshake var1) throws Exception {
   }

   public void handle(SetCompression var1) throws Exception {
   }

   public void handle(BossBar var1) throws Exception {
   }

   public void handle(LoginPayloadRequest var1) throws Exception {
   }

   public void handle(LoginPayloadResponse var1) throws Exception {
   }

   public void handle(EntityStatus var1) throws Exception {
   }

   public void handle(Commands var1) throws Exception {
   }

   public void handle(ViewDistance var1) throws Exception {
   }

   public void handle(GameState var1) throws Exception {
   }

   public void handle(EntityEffect var1) throws Exception {
   }

   public void handle(EntityRemoveEffect var1) throws Exception {
   }

   public void handle(PlayerPositionAndLook var1) {
   }

   public void handle(PlayerPosition var1) {
   }

   public void handle(ServerData var1) throws Exception {
   }
}
