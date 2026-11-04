package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClearTitles extends DefinedPacket {
   private boolean reset;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.reset = var1.readBoolean();
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      var1.writeBoolean(this.reset);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public boolean isReset() {
      return this.reset;
   }

   public void setReset(boolean var1) {
      this.reset = var1;
   }

   @Override
   public String toString() {
      return "ClearTitles(reset=" + this.isReset() + ")";
   }

   public ClearTitles() {
   }

   public ClearTitles(boolean var1) {
      this.reset = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClearTitles)) {
         return false;
      } else {
         ClearTitles var2 = (ClearTitles)var1;
         return !var2.canEqual(this) ? false : this.isReset() == var2.isReset();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ClearTitles;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      return var2 * 59 + (this.isReset() ? 79 : 97);
   }
}
