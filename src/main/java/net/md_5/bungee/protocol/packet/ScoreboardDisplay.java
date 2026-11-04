package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class ScoreboardDisplay extends DefinedPacket {
   private byte position;
   private String name;

   @Override
   public void read(ByteBuf var1) {
      this.position = var1.readByte();
      this.name = readString(var1);
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeByte(this.position);
      writeString(this.name, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public byte getPosition() {
      return this.position;
   }

   public String getName() {
      return this.name;
   }

   public void setPosition(byte var1) {
      this.position = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   @Override
   public String toString() {
      return "ScoreboardDisplay(position=" + this.getPosition() + ", name=" + this.getName() + ")";
   }

   public ScoreboardDisplay() {
   }

   public ScoreboardDisplay(byte var1, String var2) {
      this.position = var1;
      this.name = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ScoreboardDisplay)) {
         return false;
      } else {
         ScoreboardDisplay var2 = (ScoreboardDisplay)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getPosition() != var2.getPosition()) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ScoreboardDisplay;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getPosition();
      String var3 = this.getName();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
