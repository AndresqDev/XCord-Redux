package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadRequest extends DefinedPacket {
   private int id;
   private String channel;
   private byte[] data;

   @Override
   public void read(ByteBuf var1) {
      this.id = readVarInt(var1);
      this.channel = readString(var1);
      int var2 = var1.readableBytes();
      if (var2 > 1048576) {
         throw new OverflowPacketException("Payload may not be larger than 1048576 bytes");
      } else {
         this.data = new byte[var2];
         var1.readBytes(this.data);
      }
   }

   @Override
   public void write(ByteBuf var1) {
      writeVarInt(this.id, var1);
      writeString(this.channel, var1);
      var1.writeBytes(this.data);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getId() {
      return this.id;
   }

   public String getChannel() {
      return this.channel;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setId(int var1) {
      this.id = var1;
   }

   public void setChannel(String var1) {
      this.channel = var1;
   }

   public void setData(byte[] var1) {
      this.data = var1;
   }

   @Override
   public String toString() {
      return "LoginPayloadRequest(id=" + this.getId() + ", channel=" + this.getChannel() + ", data=" + Arrays.toString(this.getData()) + ")";
   }

   public LoginPayloadRequest() {
   }

   public LoginPayloadRequest(int var1, String var2, byte[] var3) {
      this.id = var1;
      this.channel = var2;
      this.data = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginPayloadRequest)) {
         return false;
      } else {
         LoginPayloadRequest var2 = (LoginPayloadRequest)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getId() != var2.getId()) {
            return false;
         } else {
            String var3 = this.getChannel();
            String var4 = var2.getChannel();
            return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.equals(this.getData(), var2.getData()) : false;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginPayloadRequest;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getId();
      String var3 = this.getChannel();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      return var2 * 59 + Arrays.hashCode(this.getData());
   }
}
