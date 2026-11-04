package net.md_5.bungee.forge;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.PluginMessage;

public enum ForgeServerHandshakeState implements IForgeServerPacketHandler<ForgeServerHandshakeState> {
   START {
      public ForgeServerHandshakeState handle(PluginMessage var1, ChannelWrapper var2) {
         ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         var2.write(var1);
         return this;
      }

      public ForgeServerHandshakeState send(PluginMessage var1, UserConnection var2) {
         return HELLO;
      }
   },
   HELLO {
      public ForgeServerHandshakeState handle(PluginMessage var1, ChannelWrapper var2) {
         ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         if (var1.getData()[0] == 1) {
            var2.write(var1);
         }

         if (var1.getData()[0] == 2) {
            var2.write(var1);
         }

         return this;
      }

      public ForgeServerHandshakeState send(PluginMessage var1, UserConnection var2) {
         return WAITINGCACK;
      }
   },
   WAITINGCACK {
      public ForgeServerHandshakeState handle(PluginMessage var1, ChannelWrapper var2) {
         ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         var2.write(var1);
         return this;
      }

      public ForgeServerHandshakeState send(PluginMessage var1, UserConnection var2) {
         if (var1.getData()[0] == 3 && var1.getTag().equals("FML|HS")) {
            var2.getForgeClientHandler().setServerIdList(var1);
            return this;
         } else if (var1.getData()[0] == -1 && var1.getTag().equals("FML|HS")) {
            return this;
         } else {
            return (ForgeServerHandshakeState)(var1.getTag().equals("FORGE") ? COMPLETE : this);
         }
      }
   },
   COMPLETE {
      public ForgeServerHandshakeState handle(PluginMessage var1, ChannelWrapper var2) {
         ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         var2.write(var1);
         return this;
      }

      public ForgeServerHandshakeState send(PluginMessage var1, UserConnection var2) {
         return DONE;
      }
   },
   DONE {
      public ForgeServerHandshakeState handle(PluginMessage var1, ChannelWrapper var2) {
         ForgeLogger.logServer(ForgeLogger.LogDirection.RECEIVED, this.name(), var1);
         var2.write(var1);
         return this;
      }

      public ForgeServerHandshakeState send(PluginMessage var1, UserConnection var2) {
         return this;
      }
   };

   private ForgeServerHandshakeState() {
   }
}
