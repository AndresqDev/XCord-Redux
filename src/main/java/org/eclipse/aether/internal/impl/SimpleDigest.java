package org.eclipse.aether.internal.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class SimpleDigest {
   private static final String[] HASH_ALGOS = new String[]{"SHA-1", "MD5"};
   private MessageDigest digest;
   private long hash;

   SimpleDigest() {
      for (String var4 : HASH_ALGOS) {
         try {
            this.digest = MessageDigest.getInstance(var4);
            this.hash = 0L;
            break;
         } catch (NoSuchAlgorithmException var6) {
            this.digest = null;
            this.hash = 13L;
         }
      }
   }

   public void update(String var1) {
      if (var1 != null && var1.length() > 0) {
         if (this.digest != null) {
            this.digest.update(var1.getBytes(StandardCharsets.UTF_8));
         } else {
            this.hash = this.hash * 31L + (long)var1.hashCode();
         }
      }
   }

   public String digest() {
      if (this.digest != null) {
         StringBuilder var1 = new StringBuilder(64);
         byte[] var2 = this.digest.digest();

         for (byte var6 : var2) {
            int var7 = var6 & 255;
            if (var7 < 16) {
               var1.append('0');
            }

            var1.append(Integer.toHexString(var7));
         }

         return var1.toString();
      } else {
         return Long.toHexString(this.hash);
      }
   }
}
