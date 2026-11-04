package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class StatusRequest extends DefinedPacket {
   @Override
   public void read(ByteBuf var1) {
   }

   @Override
   public void write(ByteBuf var1) {
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   @Override
   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return 0;
   }

   @Override
   public String toString() {
      return "StatusRequest()";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof StatusRequest)) {
         return false;
      } else {
         StatusRequest var2 = (StatusRequest)var1;
         return var2.canEqual(this);
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof StatusRequest;
   }

   @Override
   public int hashCode() {
      boolean var1 = true;
      return 1;
   }
}
