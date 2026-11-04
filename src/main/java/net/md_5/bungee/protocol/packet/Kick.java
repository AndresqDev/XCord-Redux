package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class Kick extends DefinedPacket {
   private String message;

   @Override
   public void read(ByteBuf var1) {
      this.message = readString(var1);
   }

   @Override
   public void write(ByteBuf var1) {
      writeString(this.message, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   @Override
   public String toString() {
      return "Kick(message=" + this.getMessage() + ")";
   }

   public Kick() {
   }

   public Kick(String var1) {
      this.message = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Kick)) {
         return false;
      } else {
         Kick var2 = (Kick)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Kick;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      String var3 = this.getMessage();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
