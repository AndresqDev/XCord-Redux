package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class GameState extends DefinedPacket {
   public static final short IMMEDIATE_RESPAWN = 11;
   private short state;
   private float value;

   @Override
   public void read(ByteBuf var1) {
      this.state = var1.readUnsignedByte();
      this.value = var1.readFloat();
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeByte(this.state);
      var1.writeFloat(this.value);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public short getState() {
      return this.state;
   }

   public float getValue() {
      return this.value;
   }

   public void setState(short var1) {
      this.state = var1;
   }

   public void setValue(float var1) {
      this.value = var1;
   }

   @Override
   public String toString() {
      return "GameState(state=" + this.getState() + ", value=" + this.getValue() + ")";
   }

   public GameState() {
   }

   public GameState(short var1, float var2) {
      this.state = var1;
      this.value = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GameState)) {
         return false;
      } else {
         GameState var2 = (GameState)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            return this.getState() != var2.getState() ? false : Float.compare(this.getValue(), var2.getValue()) == 0;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof GameState;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getState();
      return var2 * 59 + Float.floatToIntBits(this.getValue());
   }
}
