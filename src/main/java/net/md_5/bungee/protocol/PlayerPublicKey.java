package net.md_5.bungee.protocol;

import java.util.Arrays;

public class PlayerPublicKey {
   private final long expiry;
   private final byte[] key;
   private final byte[] signature;

   public PlayerPublicKey(long var1, byte[] var3, byte[] var4) {
      this.expiry = var1;
      this.key = var3;
      this.signature = var4;
   }

   public long getExpiry() {
      return this.expiry;
   }

   public byte[] getKey() {
      return this.key;
   }

   public byte[] getSignature() {
      return this.signature;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerPublicKey)) {
         return false;
      } else {
         PlayerPublicKey var2 = (PlayerPublicKey)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getExpiry() != var2.getExpiry()) {
            return false;
         } else {
            return !Arrays.equals(this.getKey(), var2.getKey()) ? false : Arrays.equals(this.getSignature(), var2.getSignature());
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerPublicKey;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      long var3 = this.getExpiry();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      var2 = var2 * 59 + Arrays.hashCode(this.getKey());
      return var2 * 59 + Arrays.hashCode(this.getSignature());
   }

   @Override
   public String toString() {
      return "PlayerPublicKey(expiry="
         + this.getExpiry()
         + ", key="
         + Arrays.toString(this.getKey())
         + ", signature="
         + Arrays.toString(this.getSignature())
         + ")";
   }
}
