package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyPing extends DefinedPacket {
   private final boolean v1_5;

   @Override
   public void read(ByteBuf var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void write(ByteBuf var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public boolean isV1_5() {
      return this.v1_5;
   }

   @Override
   public String toString() {
      return "LegacyPing(v1_5=" + this.isV1_5() + ")";
   }

   public LegacyPing(boolean var1) {
      this.v1_5 = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LegacyPing)) {
         return false;
      } else {
         LegacyPing var2 = (LegacyPing)var1;
         return !var2.canEqual(this) ? false : this.isV1_5() == var2.isV1_5();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LegacyPing;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      return var2 * 59 + (this.isV1_5() ? 79 : 97);
   }
}
