package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class SystemChat extends DefinedPacket {
   private String message;
   private int position;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.message = readString(var1, 262144);
      this.position = var3 >= 760 ? (var1.readBoolean() ? ChatMessageType.ACTION_BAR.ordinal() : 0) : readVarInt(var1);
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.message, var1, 262144);
      if (var3 >= 760) {
         var1.writeBoolean(this.position == ChatMessageType.ACTION_BAR.ordinal());
      } else {
         writeVarInt(this.position, var1);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getMessage() {
      return this.message;
   }

   public int getPosition() {
      return this.position;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public void setPosition(int var1) {
      this.position = var1;
   }

   @Override
   public String toString() {
      return "SystemChat(message=" + this.getMessage() + ", position=" + this.getPosition() + ")";
   }

   public SystemChat() {
   }

   public SystemChat(String var1, int var2) {
      this.message = var1;
      this.position = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SystemChat)) {
         return false;
      } else {
         SystemChat var2 = (SystemChat)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getPosition() != var2.getPosition()) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof SystemChat;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getPosition();
      String var3 = this.getMessage();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
