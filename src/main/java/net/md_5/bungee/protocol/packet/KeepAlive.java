package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class KeepAlive extends MultiVersionPacketV17 {
   private long randomId;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.randomId = (long)var1.readInt();
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.randomId = var3 >= 340 ? var1.readLong() : (long)readVarInt(var1);
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      var1.writeInt((int)this.randomId);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 340) {
         var1.writeLong(this.randomId);
      } else {
         writeVarInt((int)this.randomId, var1);
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public long getRandomId() {
      return this.randomId;
   }

   public void setRandomId(long var1) {
      this.randomId = var1;
   }

   public String toString() {
      return "KeepAlive(randomId=" + this.getRandomId() + ")";
   }

   public KeepAlive() {
   }

   public KeepAlive(long var1) {
      this.randomId = var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KeepAlive)) {
         return false;
      } else {
         KeepAlive var2 = (KeepAlive)var1;
         return !var2.canEqual(this) ? false : this.getRandomId() == var2.getRandomId();
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof KeepAlive;
   }

   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      long var3 = this.getRandomId();
      return var2 * 59 + (int)(var3 >>> 32 ^ var3);
   }
}
