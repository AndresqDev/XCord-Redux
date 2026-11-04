package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PingPacket extends DefinedPacket {
   private long time;

   @Override
   public void read(ByteBuf var1) {
      this.time = var1.readLong();
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeLong(this.time);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   @Override
   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return 8;
   }

   @Override
   public int expectedMinLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return -1;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long var1) {
      this.time = var1;
   }

   @Override
   public String toString() {
      return "PingPacket(time=" + this.getTime() + ")";
   }

   public PingPacket() {
   }

   public PingPacket(long var1) {
      this.time = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PingPacket)) {
         return false;
      } else {
         PingPacket var2 = (PingPacket)var1;
         return !var2.canEqual(this) ? false : this.getTime() == var2.getTime();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PingPacket;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      long var3 = this.getTime();
      return var2 * 59 + (int)(var3 >>> 32 ^ var3);
   }
}
