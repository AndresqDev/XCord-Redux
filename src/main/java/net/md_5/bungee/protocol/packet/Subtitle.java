package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Subtitle extends DefinedPacket {
   private String text;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.text = readString(var1);
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.text, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      this.text = var1;
   }

   @Override
   public String toString() {
      return "Subtitle(text=" + this.getText() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Subtitle)) {
         return false;
      } else {
         Subtitle var2 = (Subtitle)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getText();
            String var4 = var2.getText();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Subtitle;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      String var3 = this.getText();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
