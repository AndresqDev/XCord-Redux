package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.Log4jFilter;

public class Chat extends MultiVersionPacketV17 {
   private static final UUID EMPTY_UUID = new UUID(0L, 0L);
   private String message;
   private byte position;
   private UUID sender;

   public Chat(String var1) {
      this(var1, (byte)0);
   }

   public Chat(String var1, byte var2) {
      this(var1, var2, EMPTY_UUID);
   }

   public Chat(String var1, byte var2, UUID var3) {
      this.message = var1;
      this.position = var2;
      this.sender = var3 == null ? EMPTY_UUID : var3;
   }

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.message = Log4jFilter.a(var2, readString(var1, 2000));
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var2 == ProtocolConstants.Direction.TO_CLIENT) {
         this.message = Log4jFilter.a(var2, readString(var1, 262144));
      } else {
         this.message = Log4jFilter.a(var2, readString(var1, 256));
      }

      if (var2 == ProtocolConstants.Direction.TO_CLIENT) {
         this.position = var1.readByte();
         if (var3 >= 735) {
            this.sender = readUUID(var1);
         }
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.message, var1);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var2 == ProtocolConstants.Direction.TO_CLIENT) {
         writeString(this.message, 262144, var1);
      } else {
         writeString(this.message, var1);
      }

      if (var2 == ProtocolConstants.Direction.TO_CLIENT) {
         var1.writeByte(this.position);
         if (var3 >= 735) {
            writeUUID(this.sender, var1);
         }
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getMessage() {
      return this.message;
   }

   public byte getPosition() {
      return this.position;
   }

   public UUID getSender() {
      return this.sender;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public void setPosition(byte var1) {
      this.position = var1;
   }

   public void setSender(UUID var1) {
      this.sender = var1;
   }

   public String toString() {
      return "Chat(message=" + this.getMessage() + ", position=" + this.getPosition() + ", sender=" + this.getSender() + ")";
   }

   public Chat() {
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Chat)) {
         return false;
      } else {
         Chat var2 = (Chat)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getPosition() != var2.getPosition()) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               UUID var5 = this.getSender();
               UUID var6 = var2.getSender();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Chat;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getPosition();
      String var3 = this.getMessage();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      UUID var4 = this.getSender();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
