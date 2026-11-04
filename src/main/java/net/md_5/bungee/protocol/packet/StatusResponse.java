package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class StatusResponse extends DefinedPacket {
   private String response;

   @Override
   public void read(ByteBuf var1) {
      this.response = readString(var1);
   }

   @Override
   public void write(ByteBuf var1) {
      writeString(this.response, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getResponse() {
      return this.response;
   }

   public void setResponse(String var1) {
      this.response = var1;
   }

   @Override
   public String toString() {
      return "StatusResponse(response=" + this.getResponse() + ")";
   }

   public StatusResponse() {
   }

   public StatusResponse(String var1) {
      this.response = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof StatusResponse)) {
         return false;
      } else {
         StatusResponse var2 = (StatusResponse)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getResponse();
            String var4 = var2.getResponse();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof StatusResponse;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      String var3 = this.getResponse();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
