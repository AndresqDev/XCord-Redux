package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TitleTimes extends DefinedPacket {
   private int fadeIn;
   private int stay;
   private int fadeOut;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.fadeIn = var1.readInt();
      this.stay = var1.readInt();
      this.fadeOut = var1.readInt();
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      var1.writeInt(this.fadeIn);
      var1.writeInt(this.stay);
      var1.writeInt(this.fadeOut);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getFadeIn() {
      return this.fadeIn;
   }

   public int getStay() {
      return this.stay;
   }

   public int getFadeOut() {
      return this.fadeOut;
   }

   public void setFadeIn(int var1) {
      this.fadeIn = var1;
   }

   public void setStay(int var1) {
      this.stay = var1;
   }

   public void setFadeOut(int var1) {
      this.fadeOut = var1;
   }

   @Override
   public String toString() {
      return "TitleTimes(fadeIn=" + this.getFadeIn() + ", stay=" + this.getStay() + ", fadeOut=" + this.getFadeOut() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TitleTimes)) {
         return false;
      } else {
         TitleTimes var2 = (TitleTimes)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getFadeIn() != var2.getFadeIn()) {
            return false;
         } else {
            return this.getStay() != var2.getStay() ? false : this.getFadeOut() == var2.getFadeOut();
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof TitleTimes;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getFadeIn();
      var2 = var2 * 59 + this.getStay();
      return var2 * 59 + this.getFadeOut();
   }
}
