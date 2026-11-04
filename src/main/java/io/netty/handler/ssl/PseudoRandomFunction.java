package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class PseudoRandomFunction {
   private PseudoRandomFunction() {
   }

   static byte[] hash(byte[] var0, byte[] var1, byte[] var2, int var3, String var4) {
      ObjectUtil.checkPositiveOrZero(var3, "length");

      try {
         Mac var5 = Mac.getInstance(var4);
         var5.init(new SecretKeySpec(var0, var4));
         int var6 = (int)Math.ceil((double)var3 / (double)var5.getMacLength());
         byte[] var7 = EmptyArrays.EMPTY_BYTES;
         byte[] var8 = concat(var1, var2);
         byte[] var9 = var8;

         for (int var10 = 0; var10 < var6; var10++) {
            var9 = var5.doFinal(var9);
            var7 = concat(var7, var5.doFinal(concat(var9, var8)));
         }

         return Arrays.copyOf(var7, var3);
      } catch (GeneralSecurityException var11) {
         throw new IllegalArgumentException("Could not find algo: " + var4, var11);
      }
   }

   private static byte[] concat(byte[] var0, byte[] var1) {
      byte[] var2 = Arrays.copyOf(var0, var0.length + var1.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }
}
