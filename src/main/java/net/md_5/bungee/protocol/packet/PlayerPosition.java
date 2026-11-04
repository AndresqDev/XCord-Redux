package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerPosition extends DefinedPacket {
   protected double x;
   protected double y;
   protected double z;
   protected boolean onGround;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      if (var3 <= 16) {
         var1.readDouble();
      }

      this.z = var1.readDouble();
      this.onGround = var1.readBoolean();
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setX(double var1) {
      this.x = var1;
   }

   public void setY(double var1) {
      this.y = var1;
   }

   public void setZ(double var1) {
      this.z = var1;
   }

   public void setOnGround(boolean var1) {
      this.onGround = var1;
   }

   @Override
   public String toString() {
      return "PlayerPosition(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", onGround=" + this.isOnGround() + ")";
   }

   public PlayerPosition() {
   }

   public PlayerPosition(double var1, double var3, double var5, boolean var7) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.onGround = var7;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerPosition)) {
         return false;
      } else {
         PlayerPosition var2 = (PlayerPosition)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getX(), var2.getX()) != 0) {
            return false;
         } else if (Double.compare(this.getY(), var2.getY()) != 0) {
            return false;
         } else {
            return Double.compare(this.getZ(), var2.getZ()) != 0 ? false : this.isOnGround() == var2.isOnGround();
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerPosition;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      long var3 = Double.doubleToLongBits(this.getX());
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      long var5 = Double.doubleToLongBits(this.getY());
      var2 = var2 * 59 + (int)(var5 >>> 32 ^ var5);
      long var7 = Double.doubleToLongBits(this.getZ());
      var2 = var2 * 59 + (int)(var7 >>> 32 ^ var7);
      return var2 * 59 + (this.isOnGround() ? 79 : 97);
   }
}
