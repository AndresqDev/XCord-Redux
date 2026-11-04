package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ClientStatus extends DefinedPacket {
   private byte payload;

   @Override
   public void read(ByteBuf var1) {
      this.payload = var1.readByte();
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeByte(this.payload);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public byte getPayload() {
      return this.payload;
   }

   public void setPayload(byte var1) {
      this.payload = var1;
   }

   @Override
   public String toString() {
      return "ClientStatus(payload=" + this.getPayload() + ")";
   }

   public ClientStatus() {
   }

   public ClientStatus(byte var1) {
      this.payload = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClientStatus)) {
         return false;
      } else {
         ClientStatus var2 = (ClientStatus)var1;
         return !var2.canEqual(this) ? false : this.getPayload() == var2.getPayload();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ClientStatus;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      return var2 * 59 + this.getPayload();
   }
}
