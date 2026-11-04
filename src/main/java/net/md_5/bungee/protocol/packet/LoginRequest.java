package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.Log4jFilter;

public class LoginRequest extends DefinedPacket {
   private String data;
   private PlayerPublicKey publicKey;
   private UUID uuid;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.data = Log4jFilter.a(readString(var1, 16));
      if (var3 >= 759) {
         this.publicKey = readPublicKey(var1);
      }

      if (var3 >= 760 && var1.readBoolean()) {
         this.uuid = readUUID(var1);
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.data, var1);
      if (var3 >= 759) {
         writePublicKey(this.publicKey, var1);
      }

      if (var3 >= 760) {
         if (this.uuid != null) {
            var1.writeBoolean(true);
            writeUUID(this.uuid, var1);
         } else {
            var1.writeBoolean(false);
         }
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   @Override
   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return var3 >= 759 ? 90000 : 65;
   }

   public String getData() {
      return this.data;
   }

   public PlayerPublicKey getPublicKey() {
      return this.publicKey;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setData(String var1) {
      this.data = var1;
   }

   public void setPublicKey(PlayerPublicKey var1) {
      this.publicKey = var1;
   }

   public void setUuid(UUID var1) {
      this.uuid = var1;
   }

   @Override
   public String toString() {
      return "LoginRequest(data=" + this.getData() + ", publicKey=" + this.getPublicKey() + ", uuid=" + this.getUuid() + ")";
   }

   public LoginRequest() {
   }

   public LoginRequest(String var1, PlayerPublicKey var2, UUID var3) {
      this.data = var1;
      this.publicKey = var2;
      this.uuid = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginRequest)) {
         return false;
      } else {
         LoginRequest var2 = (LoginRequest)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getData();
            String var4 = var2.getData();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               PlayerPublicKey var5 = this.getPublicKey();
               PlayerPublicKey var6 = var2.getPublicKey();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  UUID var7 = this.getUuid();
                  UUID var8 = var2.getUuid();
                  return var7 == null ? var8 == null : var7.equals(var8);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginRequest;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getData();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      PlayerPublicKey var4 = this.getPublicKey();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      UUID var5 = this.getUuid();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
