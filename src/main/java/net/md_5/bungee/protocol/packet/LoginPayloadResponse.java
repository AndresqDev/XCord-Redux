package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadResponse extends DefinedPacket {
   private int id;
   private byte[] data;

   @Override
   public void read(ByteBuf var1) {
      this.id = readVarInt(var1);
      if (var1.readBoolean()) {
         int var2 = var1.readableBytes();
         if (var2 > 1048576) {
            throw new OverflowPacketException("Payload may not be larger than 1048576 bytes");
         }

         this.data = new byte[var2];
         var1.readBytes(this.data);
      }
   }

   @Override
   public void write(ByteBuf var1) {
      writeVarInt(this.id, var1);
      if (this.data != null) {
         var1.writeBoolean(true);
         var1.writeBytes(this.data);
      } else {
         var1.writeBoolean(false);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getId() {
      return this.id;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setId(int var1) {
      this.id = var1;
   }

   public void setData(byte[] var1) {
      this.data = var1;
   }

   @Override
   public String toString() {
      return "LoginPayloadResponse(id=" + this.getId() + ", data=" + Arrays.toString(this.getData()) + ")";
   }

   public LoginPayloadResponse() {
   }

   public LoginPayloadResponse(int var1, byte[] var2) {
      this.id = var1;
      this.data = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginPayloadResponse)) {
         return false;
      } else {
         LoginPayloadResponse var2 = (LoginPayloadResponse)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            return this.getId() != var2.getId() ? false : Arrays.equals(this.getData(), var2.getData());
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginPayloadResponse;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getId();
      return var2 * 59 + Arrays.hashCode(this.getData());
   }
}
