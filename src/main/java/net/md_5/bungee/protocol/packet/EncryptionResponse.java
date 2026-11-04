package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionResponse extends MultiVersionPacketV17 {
   private byte[] sharedSecret;
   private byte[] verifyToken;
   private EncryptionResponse.EncryptionData encryptionData;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.sharedSecret = v17readArray(var1);
      this.verifyToken = v17readArray(var1);
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.sharedSecret = readArray(var1, 128);
      if (var3 >= 759 && !var1.readBoolean()) {
         this.encryptionData = new EncryptionResponse.EncryptionData(var1.readLong(), readArray(var1));
      } else {
         this.verifyToken = readArray(var1, 128);
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      v17writeArray(this.sharedSecret, var1, false);
      v17writeArray(this.verifyToken, var1, false);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeArray(this.sharedSecret, var1);
      if (this.verifyToken != null) {
         if (var3 >= 759) {
            var1.writeBoolean(true);
         }

         writeArray(this.verifyToken, var1);
      } else {
         var1.writeLong(this.encryptionData.getSalt());
         writeArray(this.encryptionData.getSignature(), var1);
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return var3 >= 759 ? 900000 : 500;
   }

   public byte[] getSharedSecret() {
      return this.sharedSecret;
   }

   public byte[] getVerifyToken() {
      return this.verifyToken;
   }

   public EncryptionResponse.EncryptionData getEncryptionData() {
      return this.encryptionData;
   }

   public void setSharedSecret(byte[] var1) {
      this.sharedSecret = var1;
   }

   public void setVerifyToken(byte[] var1) {
      this.verifyToken = var1;
   }

   public void setEncryptionData(EncryptionResponse.EncryptionData var1) {
      this.encryptionData = var1;
   }

   public String toString() {
      return "EncryptionResponse(sharedSecret="
         + Arrays.toString(this.getSharedSecret())
         + ", verifyToken="
         + Arrays.toString(this.getVerifyToken())
         + ", encryptionData="
         + this.getEncryptionData()
         + ")";
   }

   public EncryptionResponse() {
   }

   public EncryptionResponse(byte[] var1, byte[] var2, EncryptionResponse.EncryptionData var3) {
      this.sharedSecret = var1;
      this.verifyToken = var2;
      this.encryptionData = var3;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EncryptionResponse)) {
         return false;
      } else {
         EncryptionResponse var2 = (EncryptionResponse)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!Arrays.equals(this.getSharedSecret(), var2.getSharedSecret())) {
            return false;
         } else if (!Arrays.equals(this.getVerifyToken(), var2.getVerifyToken())) {
            return false;
         } else {
            EncryptionResponse.EncryptionData var3 = this.getEncryptionData();
            EncryptionResponse.EncryptionData var4 = var2.getEncryptionData();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EncryptionResponse;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Arrays.hashCode(this.getSharedSecret());
      var2 = var2 * 59 + Arrays.hashCode(this.getVerifyToken());
      EncryptionResponse.EncryptionData var3 = this.getEncryptionData();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   public static class EncryptionData {
      private final long salt;
      private final byte[] signature;

      public EncryptionData(long var1, byte[] var3) {
         this.salt = var1;
         this.signature = var3;
      }

      public long getSalt() {
         return this.salt;
      }

      public byte[] getSignature() {
         return this.signature;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof EncryptionResponse.EncryptionData)) {
            return false;
         } else {
            EncryptionResponse.EncryptionData var2 = (EncryptionResponse.EncryptionData)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               return this.getSalt() != var2.getSalt() ? false : Arrays.equals(this.getSignature(), var2.getSignature());
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof EncryptionResponse.EncryptionData;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         long var3 = this.getSalt();
         var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
         return var2 * 59 + Arrays.hashCode(this.getSignature());
      }

      @Override
      public String toString() {
         return "EncryptionResponse.EncryptionData(salt=" + this.getSalt() + ", signature=" + Arrays.toString(this.getSignature()) + ")";
      }
   }
}
