package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import java.security.cert.X509Certificate;

final class DefaultOpenSslKeyMaterial extends AbstractReferenceCounted implements OpenSslKeyMaterial {
   private static final ResourceLeakDetector<DefaultOpenSslKeyMaterial> leakDetector = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(DefaultOpenSslKeyMaterial.class);
   private final ResourceLeakTracker<DefaultOpenSslKeyMaterial> leak;
   private final X509Certificate[] x509CertificateChain;
   private long chain;
   private long privateKey;

   DefaultOpenSslKeyMaterial(long var1, long var3, X509Certificate[] var5) {
      this.chain = var1;
      this.privateKey = var3;
      this.x509CertificateChain = var5;
      this.leak = leakDetector.track(this);
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
         return this.chain;
      }
   }

   @Override
   public long privateKeyAddress() {
      if (this.refCnt() <= 0) {
         throw new IllegalReferenceCountException();
      } else {
         return this.privateKey;
      }
   }

   @Override
   protected void deallocate() {
      SSL.freeX509Chain(this.chain);
      this.chain = 0L;
      SSL.freePrivateKey(this.privateKey);
      this.privateKey = 0L;
      if (this.leak != null) {
         boolean var1 = this.leak.close(this);

         assert var1;
      }
   }

   public DefaultOpenSslKeyMaterial retain() {
      if (this.leak != null) {
         this.leak.record();
      }

      super.retain();
      return this;
   }

   public DefaultOpenSslKeyMaterial retain(int var1) {
      if (this.leak != null) {
         this.leak.record();
      }

      super.retain(var1);
      return this;
   }

   public DefaultOpenSslKeyMaterial touch() {
      if (this.leak != null) {
         this.leak.record();
      }

      super.touch();
      return this;
   }

   public DefaultOpenSslKeyMaterial touch(Object var1) {
      if (this.leak != null) {
         this.leak.record(var1);
      }

      return this;
   }

   @Override
   public boolean release() {
      if (this.leak != null) {
         this.leak.record();
      }

      return super.release();
   }

   @Override
   public boolean release(int var1) {
      if (this.leak != null) {
         this.leak.record();
      }

      return super.release(var1);
   }
}
