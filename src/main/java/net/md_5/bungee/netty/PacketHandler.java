package net.md_5.bungee.netty;

import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;

public abstract class PacketHandler extends AbstractPacketHandler {
   @Override
   public abstract String toString();

   public boolean shouldHandle(PacketWrapper var1) throws Exception {
      return true;
   }

   public void exception(Throwable var1) throws Exception {
   }

   public void handle(PacketWrapper var1) throws Exception {
   }

   public void connected(ChannelWrapper var1) throws Exception {
   }

   public void disconnected(ChannelWrapper var1) throws Exception {
   }

   public void writabilityChanged(ChannelWrapper var1) throws Exception {
   }

   public void readCompleted() throws Exception {
   }
}
