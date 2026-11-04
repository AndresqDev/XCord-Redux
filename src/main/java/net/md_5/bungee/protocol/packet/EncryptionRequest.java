package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionRequest extends MultiVersionPacketV17 {
   private String serverId;
   private byte[] publicKey;
   private byte[] verifyToken;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.serverId = readString(var1);
      this.publicKey = v17readArray(var1);
      this.verifyToken = v17readArray(var1);
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.serverId = readString(var1);
      this.publicKey = readArray(var1);
      this.verifyToken = readArray(var1);
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.serverId, var1);
      v17writeArray(this.publicKey, var1, false);
      v17writeArray(this.verifyToken, var1, false);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.serverId, var1);
      writeArray(this.publicKey, var1);
      writeArray(this.verifyToken, var1);
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getServerId() {
      return this.serverId;
   }

   public byte[] getPublicKey() {
      return this.publicKey;
   }

   public byte[] getVerifyToken() {
      return this.verifyToken;
   }

   public void setServerId(String var1) {
      this.serverId = var1;
   }

   public void setPublicKey(byte[] var1) {
      this.publicKey = var1;
   }

   public void setVerifyToken(byte[] var1) {
      this.verifyToken = var1;
   }

   public String toString() {
      return "EncryptionRequest(serverId="
         + this.getServerId()
         + ", publicKey="
         + Arrays.toString(this.getPublicKey())
         + ", verifyToken="
         + Arrays.toString(this.getVerifyToken())
         + ")";
   }

   public EncryptionRequest() {
   }

   public EncryptionRequest(String var1, byte[] var2, byte[] var3) {
      this.serverId = var1;
      this.publicKey = var2;
      this.verifyToken = var3;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EncryptionRequest)) {
         return false;
      } else {
         EncryptionRequest var2 = (EncryptionRequest)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getServerId();
            String var4 = var2.getServerId();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               return !Arrays.equals(this.getPublicKey(), var2.getPublicKey()) ? false : Arrays.equals(this.getVerifyToken(), var2.getVerifyToken());
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EncryptionRequest;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getServerId();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      var2 = var2 * 59 + Arrays.hashCode(this.getPublicKey());
      return var2 * 59 + Arrays.hashCode(this.getVerifyToken());
   }
}
