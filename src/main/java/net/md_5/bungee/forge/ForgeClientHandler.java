package net.md_5.bungee.forge;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.packet.EntityRemoveEffect;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeClientHandler {
   @NonNull
   private final UserConnection con;
   private boolean forgeOutdated = false;
   private Map<String, String> clientModList = null;
   private final ArrayDeque<PluginMessage> packetQueue = new ArrayDeque<>();
   @NonNull
   private ForgeClientHandshakeState state = ForgeClientHandshakeState.HELLO;
   private PluginMessage serverModList = null;
   private PluginMessage serverIdList = null;
   private boolean fmlTokenInHandshake = false;

   public void handle(PluginMessage var1) throws IllegalArgumentException {
      if (!var1.getTag().equalsIgnoreCase("FML|HS")) {
         throw new IllegalArgumentException("Expecting a Forge Handshake packet.");
      } else {
         var1.setAllowExtendedPacket(true);
         ForgeClientHandshakeState var2 = this.state;
         Preconditions.checkState(this.packetQueue.size() < 128, "Forge packet queue too big!");
         this.packetQueue.add(var1);
         this.state = this.state.send(var1, this.con);
         if (this.state != var2) {
            synchronized (this.packetQueue) {
               while (!this.packetQueue.isEmpty()) {
                  ForgeLogger.logClient(ForgeLogger.LogDirection.SENDING, var2.name(), this.packetQueue.getFirst());
                  this.con.getForgeServerHandler().receive(this.packetQueue.removeFirst());
               }
            }
         }
      }
   }

   public void receive(PluginMessage var1) throws IllegalArgumentException {
      this.state = this.state.handle(var1, this.con);
   }

   public void resetHandshake() {
      this.state = ForgeClientHandshakeState.HELLO;
      if (this.con.getPendingConnection().getVersion() == 47) {
         this.resetAllThePotions(this.con);
      }

      this.con.unsafe().sendPacket(ForgeConstants.FML_RESET_HANDSHAKE);
   }

   private void resetAllThePotions(UserConnection var1) {
      for (Entry var3 : var1.getPotions().entries()) {
         var1.unsafe().sendPacket(new EntityRemoveEffect((Integer)var3.getKey(), (Integer)var3.getValue()));
      }

      var1.getPotions().clear();
   }

   public void setServerModList(PluginMessage var1) throws IllegalArgumentException {
      if (var1.getTag().equalsIgnoreCase("FML|HS") && var1.getData()[0] == 2) {
         this.serverModList = var1;
      } else {
         throw new IllegalArgumentException("modList");
      }
   }

   public void setServerIdList(PluginMessage var1) throws IllegalArgumentException {
      if (var1.getTag().equalsIgnoreCase("FML|HS") && var1.getData()[0] == 3) {
         this.serverIdList = var1;
      } else {
         throw new IllegalArgumentException("idList");
      }
   }

   public boolean isHandshakeComplete() {
      return this.state == ForgeClientHandshakeState.DONE;
   }

   public void setHandshakeComplete() {
      this.state = ForgeClientHandshakeState.DONE;
   }

   public boolean isForgeUser() {
      return this.fmlTokenInHandshake || this.clientModList != null;
   }

   public boolean checkUserOutdated() {
      if (this.forgeOutdated) {
         this.con.disconnect(BungeeCord.getInstance().getTranslation("connect_kick_outdated_forge"));
      }

      return this.forgeOutdated;
   }

   public ForgeClientHandler(@NonNull UserConnection var1) {
      if (var1 == null) {
         throw new NullPointerException("con is marked non-null but is null");
      } else {
         this.con = var1;
      }
   }

   public boolean isForgeOutdated() {
      return this.forgeOutdated;
   }

   void setForgeOutdated(boolean var1) {
      this.forgeOutdated = var1;
   }

   public Map<String, String> getClientModList() {
      return this.clientModList;
   }

   void setClientModList(Map<String, String> var1) {
      this.clientModList = var1;
   }

   void setState(@NonNull ForgeClientHandshakeState var1) {
      if (var1 == null) {
         throw new NullPointerException("state is marked non-null but is null");
      } else {
         this.state = var1;
      }
   }

   public boolean isFmlTokenInHandshake() {
      return this.fmlTokenInHandshake;
   }

   public void setFmlTokenInHandshake(boolean var1) {
      this.fmlTokenInHandshake = var1;
   }
}
