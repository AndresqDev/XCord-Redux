package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509KeyManager;

class OpenSslKeyMaterialProvider {
   private final X509KeyManager keyManager;
   private final String password;

   OpenSslKeyMaterialProvider(X509KeyManager var1, String var2) {
      this.keyManager = var1;
      this.password = var2;
   }

   static void validateKeyMaterialSupported(X509Certificate[] var0, PrivateKey var1, String var2) throws SSLException {
      validateSupported(var0);
      validateSupported(var1, var2);
   }

   private static void validateSupported(PrivateKey var0, String var1) throws SSLException {
      if (var0 != null) {
         long var2 = 0L;
         long var4 = 0L;

         try {
            var2 = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, var0);
            var4 = SSL.parsePrivateKey(var2, var1);
         } catch (Exception var10) {
            throw new SSLException("PrivateKey type not supported " + var0.getFormat(), var10);
         } finally {
            SSL.freeBIO(var2);
            if (var4 != 0L) {
               SSL.freePrivateKey(var4);
            }
         }
      }
   }

   private static void validateSupported(X509Certificate[] var0) throws SSLException {
      if (var0 != null && var0.length != 0) {
         long var1 = 0L;
         long var3 = 0L;
         PemEncoded var5 = null;

         try {
            var5 = PemX509Certificate.toPEM(UnpooledByteBufAllocator.DEFAULT, true, var0);
            var1 = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, var5.retain());
            var3 = SSL.parseX509Chain(var1);
         } catch (Exception var10) {
            throw new SSLException("Certificate type not supported", var10);
         } finally {
            SSL.freeBIO(var1);
            if (var3 != 0L) {
               SSL.freeX509Chain(var3);
            }

            if (var5 != null) {
               var5.release();
            }
         }
      }
   }

   X509KeyManager keyManager() {
      return this.keyManager;
   }

   OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator var1, String var2) throws Exception {
      X509Certificate[] var3 = this.keyManager.getCertificateChain(var2);
      if (var3 != null && var3.length != 0) {
         PrivateKey var4 = this.keyManager.getPrivateKey(var2);
         PemEncoded var5 = PemX509Certificate.toPEM(var1, true, var3);
         long var6 = 0L;
         long var8 = 0L;
         long var10 = 0L;
         long var12 = 0L;

         Object var15;
         try {
            var6 = ReferenceCountedOpenSslContext.toBIO(var1, var5.retain());
            var10 = SSL.parseX509Chain(var6);
            Object var14;
            if (var4 instanceof OpenSslPrivateKey) {
               var14 = ((OpenSslPrivateKey)var4).newKeyMaterial(var10, var3);
            } else {
               var8 = ReferenceCountedOpenSslContext.toBIO(var1, var4);
               var12 = var4 == null ? 0L : SSL.parsePrivateKey(var8, this.password);
               var14 = new DefaultOpenSslKeyMaterial(var10, var12, var3);
            }

            var10 = 0L;
            var12 = 0L;
            var15 = var14;
         } finally {
            SSL.freeBIO(var6);
            SSL.freeBIO(var8);
            if (var10 != 0L) {
               SSL.freeX509Chain(var10);
            }

            if (var12 != 0L) {
               SSL.freePrivateKey(var12);
            }

            var5.release();
         }

         return (OpenSslKeyMaterial)var15;
      } else {
         return null;
      }
   }

   void destroy() {
   }
}
