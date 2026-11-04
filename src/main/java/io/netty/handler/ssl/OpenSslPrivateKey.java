package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.EmptyArrays;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

final class OpenSslPrivateKey extends AbstractReferenceCounted implements PrivateKey {
   private long privateKeyAddress;

   OpenSslPrivateKey(long var1) {
      this.privateKeyAddress = var1;
   }

   @Override
   public String getAlgorithm() {
      return "unknown";
   }

   @Override
   public String getFormat() {
      return null;
   }

   @Override
   public byte[] getEncoded() {
      return null;
   }

   private long privateKeyAddress() {
      if (this.refCnt() <= 0) {
         throw new IllegalReferenceCountException();
      } else {
         return this.privateKeyAddress;
      }
   }

   @Override
   protected void deallocate() {
      SSL.freePrivateKey(this.privateKeyAddress);
      this.privateKeyAddress = 0L;
   }

   public OpenSslPrivateKey retain() {
      super.retain();
      return this;
   }

   public OpenSslPrivateKey retain(int var1) {
      super.retain(var1);
      return this;
   }

   public OpenSslPrivateKey touch() {
      super.touch();
      return this;
   }

   public OpenSslPrivateKey touch(Object var1) {
      return this;
   }

   @Override
   public void destroy() {
      this.release(this.refCnt());
   }

   @Override
   public boolean isDestroyed() {
      return this.refCnt() == 0;
   }

   OpenSslKeyMaterial newKeyMaterial(long var1, X509Certificate[] var3) {
      return new OpenSslPrivateKey.OpenSslPrivateKeyMaterial(var1, var3);
   }

   final class OpenSslPrivateKeyMaterial extends AbstractReferenceCounted implements OpenSslKeyMaterial {
      long certificateChain;
      private final X509Certificate[] x509CertificateChain;

      OpenSslPrivateKeyMaterial(long var2, X509Certificate[] var4) {
         this.certificateChain = var2;
         this.x509CertificateChain = var4 == null ? EmptyArrays.EMPTY_X509_CERTIFICATES : var4;
         OpenSslPrivateKey.this.retain();
      }

      @Override
      public X509Certificate[] certificateChain() {
         return (X509Certificate[])this.x509CertificateChain.clone();
      }

      @Override
      public long certificateChainAddress() {
         if (this.refCnt() <= 0) {
            throw new IllegalReferenceCountException();
         } else {
            return this.certificateChain;
         }
      }

      @Override
      public long privateKeyAddress() {
         if (this.refCnt() <= 0) {
            throw new IllegalReferenceCountException();
         } else {
            return OpenSslPrivateKey.this.privateKeyAddress();
         }
      }

      @Override
      public OpenSslKeyMaterial touch(Object var1) {
         OpenSslPrivateKey.this.touch(var1);
         return this;
      }

      @Override
      public OpenSslKeyMaterial retain() {
         super.retain();
         return this;
      }

      @Override
      public OpenSslKeyMaterial retain(int var1) {
         super.retain(var1);
         return this;
      }

      @Override
      public OpenSslKeyMaterial touch() {
         OpenSslPrivateKey.this.touch();
         return this;
      }

      @Override
      protected void deallocate() {
         this.releaseChain();
         OpenSslPrivateKey.this.release();
      }

      private void releaseChain() {
         SSL.freeX509Chain(this.certificateChain);
         this.certificateChain = 0L;
      }
   }
}
