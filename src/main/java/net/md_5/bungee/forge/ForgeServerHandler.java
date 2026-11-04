package net.md_5.bungee.forge;

import java.util.ArrayDeque;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeServerHandler {
   private final UserConnection con;
   private final ChannelWrapper ch;
   private final ServerInfo serverInfo;
   private ForgeServerHandshakeState state = ForgeServerHandshakeState.START;
   private boolean serverForge = false;
   private final ArrayDeque<PluginMessage> packetQueue = new ArrayDeque<>();

   public void handle(PluginMessage var1) throws IllegalArgumentException {
      if (!var1.getTag().equalsIgnoreCase("FML|HS") && !var1.getTag().equalsIgnoreCase("FORGE")) {
         throw new IllegalArgumentException("Expecting a Forge REGISTER or FML Handshake packet.");
      } else {
         var1.setAllowExtendedPacket(true);
         ForgeServerHandshakeState var2 = this.state;
         this.packetQueue.add(var1);
         this.state = this.state.send(var1, this.con);
         if (this.state == ForgeServerHandshakeState.DONE || this.state != var2) {
            synchronized (this.packetQueue) {
               while (!this.packetQueue.isEmpty()) {
                  ForgeLogger.logServer(ForgeLogger.LogDirection.SENDING, var2.name(), this.packetQueue.getFirst());
                  this.con.getForgeClientHandler().receive(this.packetQueue.removeFirst());
               }
            }
         }
      }
   }

   public void receive(PluginMessage var1) throws IllegalArgumentException {
      this.state = this.state.handle(var1, this.ch);
   }

   public void setServerAsForgeServer() {
      this.serverForge = true;
   }

   public ForgeServerHandler(UserConnection var1, ChannelWrapper var2, ServerInfo var3) {
      this.con = var1;
      this.ch = var2;
      this.serverInfo = var3;
   }

   public ChannelWrapper getCh() {
      return this.ch;
   }

   ServerInfo getServerInfo() {
      return this.serverInfo;
   }

   public ForgeServerHandshakeState getState() {
      return this.state;
   }

   public boolean isServerForge() {
      return this.serverForge;
   }
}
