package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class SetCompression extends DefinedPacket {
   private int threshold;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.threshold = DefinedPacket.readVarInt(var1);
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      DefinedPacket.writeVarInt(this.threshold, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getThreshold() {
      return this.threshold;
   }

   public void setThreshold(int var1) {
      this.threshold = var1;
   }

   @Override
   public String toString() {
      return "SetCompression(threshold=" + this.getThreshold() + ")";
   }

   public SetCompression() {
   }

   public SetCompression(int var1) {
      this.threshold = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SetCompression)) {
         return false;
      } else {
         SetCompression var2 = (SetCompression)var1;
         return !var2.canEqual(this) ? false : this.getThreshold() == var2.getThreshold();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof SetCompression;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      return var2 * 59 + this.getThreshold();
   }
}
