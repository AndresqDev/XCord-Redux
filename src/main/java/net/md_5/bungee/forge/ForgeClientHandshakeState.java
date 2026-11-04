package net.md_5.bungee.forge;

import java.util.Map;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;

enum ForgeClientHandshakeState implements IForgeClientPacketHandler<ForgeClientHandshakeState> {
   START {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         var2.unsafe().sendPacket(var1);
         var2.getForgeClientHandler().setState(HELLO);
         return HELLO;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return HELLO;
      }
   },
   HELLO {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         if (var1.getData()[0] == 0) {
            var2.unsafe().sendPacket(var1);
         }

         return this;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         if (var1.getData()[0] == 1) {
            return this;
         } else if (var1.getData()[0] == 2) {
            if (var2.getForgeClientHandler().getClientModList() == null) {
               Map var3 = ForgeUtils.readModList(var1);
               var2.getForgeClientHandler().setClientModList(var3);
               if (ProtocolConstants.isBeforeOrEq(var2.getPendingConnection().getVersion(), 5)) {
                  int var4 = ForgeUtils.getFmlBuildNumber(var3);
                  if (var4 < 1209 && var4 != 0) {
                     var2.getForgeClientHandler().setForgeOutdated(true);
                  }
               }
            }

            return WAITINGSERVERDATA;
         } else {
            return this;
         }
      }
   },
   WAITINGSERVERDATA {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         if (var1.getData()[0] == 2) {
            var2.unsafe().sendPacket(var1);
         }

         return this;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return WAITINGSERVERCOMPLETE;
      }
   },
   WAITINGSERVERCOMPLETE {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         if (var1.getData()[0] == 3) {
            var2.unsafe().sendPacket(var1);
            return this;
         } else {
            var2.unsafe().sendPacket(var1);
            return this;
         }
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return PENDINGCOMPLETE;
      }
   },
   PENDINGCOMPLETE {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         if (var1.getData()[0] == -1) {
            ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
            var2.unsafe().sendPacket(var1);
         }

         return this;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return COMPLETE;
      }
   },
   COMPLETE {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         if (var1.getTag().equals("FML|HS") && var1.getData()[0] == -1 || var1.getTag().equals("FORGE")) {
            ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
            var2.unsafe().sendPacket(var1);
         }

         return this;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return DONE;
      }
   },
   DONE {
      public ForgeClientHandshakeState handle(PluginMessage var1, UserConnection var2) {
         ForgeLogger.logClient(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         if (var1.getTag().equals("FORGE")) {
            var2.unsafe().sendPacket(var1);
         }

         return this;
      }

      public ForgeClientHandshakeState send(PluginMessage var1, UserConnection var2) {
         return this;
      }
   };

   private ForgeClientHandshakeState() {
   }
}
