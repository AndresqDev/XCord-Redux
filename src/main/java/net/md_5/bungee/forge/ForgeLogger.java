package net.md_5.bungee.forge;

import java.util.logging.Level;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.protocol.packet.PluginMessage;

final class ForgeLogger {
   static void logServer(ForgeLogger.LogDirection var0, String var1, PluginMessage var2) {
      String var3 = var0 == ForgeLogger.LogDirection.SENDING ? "Server -> Bungee" : "Server <- Bungee";
      String var4 = "[" + var1 + " " + var3 + "][" + var0.name() + ": " + getNameFromDiscriminator(var2.getTag(), var2) + "]";
      BungeeCord.getInstance().getLogger().log(Level.FINE, var4);
   }

   static void logClient(ForgeLogger.LogDirection var0, String var1, PluginMessage var2) {
      String var3 = var0 == ForgeLogger.LogDirection.SENDING ? "Client -> Bungee" : "Client <- Bungee";
      String var4 = "[" + var1 + " " + var3 + "][" + var0.name() + ": " + getNameFromDiscriminator(var2.getTag(), var2) + "]";
      BungeeCord.getInstance().getLogger().log(Level.FINE, var4);
   }

   private static String getNameFromDiscriminator(String var0, PluginMessage var1) {
      byte var2 = var1.getData()[0];
      if (var0.equals("FML|HS")) {
         switch (var2) {
            case -2:
               return "Reset";
            case -1:
               return "HandshakeAck";
            case 0:
               return "ServerHello";
            case 1:
               return "ClientHello";
            case 2:
               return "ModList";
            case 3:
               return "ModIdData";
            default:
               return "Unknown";
         }
      } else if (var0.equals("FORGE")) {
         switch (var2) {
            case 1:
               return "DimensionRegister";
            case 2:
               return "FluidIdMap";
            default:
               return "Unknown";
         }
      } else {
         return "UnknownChannel";
      }
   }

   private ForgeLogger() {
   }

   public static enum LogDirection {
      SENDING,
      RECEIVED;
   }
}
