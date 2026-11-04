package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ViewDistance extends DefinedPacket {
   private int distance;

   @Override
   public void read(ByteBuf var1) {
      this.distance = DefinedPacket.readVarInt(var1);
   }

   @Override
   public void write(ByteBuf var1) {
      DefinedPacket.writeVarInt(this.distance, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getDistance() {
      return this.distance;
   }

   public void setDistance(int var1) {
      this.distance = var1;
   }

   @Override
   public String toString() {
      return "ViewDistance(distance=" + this.getDistance() + ")";
   }

   public ViewDistance() {
   }

   public ViewDistance(int var1) {
      this.distance = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ViewDistance)) {
         return false;
      } else {
         ViewDistance var2 = (ViewDistance)var1;
         return !var2.canEqual(this) ? false : this.getDistance() == var2.getDistance();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ViewDistance;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      return var2 * 59 + this.getDistance();
   }
}
