package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class RawDataPacket extends DefinedPacket {
   private ByteBuf rawData;

   public RawDataPacket(ByteBuf var1) {
      this.rawData = var1;
   }

   public RawDataPacket() {
   }

   public ByteBuf getRawData() {
      return this.rawData;
   }

   @Override
   public void read(ByteBuf var1) {
      this.rawData = var1.copy();
      this.rawData.retain();
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeBytes(this.rawData.slice());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 instanceof RawDataPacket ? ((RawDataPacket)var1).rawData.equals(this.rawData) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.rawData.hashCode();
   }

   @Override
   public String toString() {
      return "xxx";
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
   }
}
