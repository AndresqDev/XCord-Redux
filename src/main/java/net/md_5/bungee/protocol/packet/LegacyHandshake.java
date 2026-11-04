package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class LegacyHandshake extends DefinedPacket {
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

   @Override
   public String toString() {
      return "LegacyHandshake()";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LegacyHandshake)) {
         return false;
      } else {
         LegacyHandshake var2 = (LegacyHandshake)var1;
         return var2.canEqual(this);
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LegacyHandshake;
   }

   @Override
   public int hashCode() {
      boolean var1 = true;
      return 1;
   }
}
