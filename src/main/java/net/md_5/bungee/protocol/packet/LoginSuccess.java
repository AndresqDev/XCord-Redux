package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.ProtocolConstants;

public class LoginSuccess extends DefinedPacket {
   private UUID uuid;
   private String username;
   private Property[] properties;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 <= 4) {
         this.uuid = readUndashedUUID(var1);
      } else if (var3 >= 735) {
         this.uuid = readUUID(var1);
      } else {
         this.uuid = UUID.fromString(readString(var1));
      }

      this.username = readString(var1);
      if (var3 >= 759) {
         this.properties = readProperties(var1);
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 <= 4) {
         writeUndashedUUID(this.uuid.toString(), var1);
      } else if (var3 >= 735) {
         writeUUID(this.uuid, var1);
      } else {
         writeString(this.uuid.toString(), var1);
      }

      writeString(this.username, var1);
      if (var3 >= 759) {
         writeProperties(this.properties, var1);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   private static UUID readUndashedUUID(ByteBuf var0) {
      return UUID.fromString(new StringBuilder(readString(var0)).insert(20, '-').insert(16, '-').insert(12, '-').insert(8, '-').toString());
   }

   private static void writeUndashedUUID(String var0, ByteBuf var1) {
      writeString(new StringBuilder(32).append(var0, 0, 8).append(var0, 9, 13).append(var0, 14, 18).append(var0, 19, 23).append(var0, 24, 36).toString(), var1);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getUsername() {
      return this.username;
   }

   public Property[] getProperties() {
      return this.properties;
   }

   public void setUuid(UUID var1) {
      this.uuid = var1;
   }

   public void setUsername(String var1) {
      this.username = var1;
   }

   public void setProperties(Property[] var1) {
      this.properties = var1;
   }

   @Override
   public String toString() {
      return "LoginSuccess(uuid=" + this.getUuid() + ", username=" + this.getUsername() + ", properties=" + Arrays.deepToString(this.getProperties()) + ")";
   }

   public LoginSuccess() {
   }

   public LoginSuccess(UUID var1, String var2, Property[] var3) {
      this.uuid = var1;
      this.username = var2;
      this.properties = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginSuccess)) {
         return false;
      } else {
         LoginSuccess var2 = (LoginSuccess)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            UUID var3 = this.getUuid();
            UUID var4 = var2.getUuid();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getUsername();
               String var6 = var2.getUsername();
               return (var5 == null ? var6 == null : var5.equals(var6)) ? Arrays.deepEquals(this.getProperties(), var2.getProperties()) : false;
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginSuccess;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      UUID var3 = this.getUuid();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getUsername();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      return var2 * 59 + Arrays.deepHashCode(this.getProperties());
   }
}
