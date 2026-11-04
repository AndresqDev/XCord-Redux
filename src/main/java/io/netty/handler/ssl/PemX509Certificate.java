package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public final class PemX509Certificate extends X509Certificate implements PemEncoded {
   private static final byte[] BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
   private static final byte[] END_CERT = "\n-----END CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
   private final ByteBuf content;

   static PemEncoded toPEM(ByteBufAllocator var0, boolean var1, X509Certificate... var2) throws CertificateEncodingException {
      ObjectUtil.checkNonEmpty(var2, "chain");
      if (var2.length == 1) {
         X509Certificate var3 = var2[0];
         if (var3 instanceof PemEncoded) {
            return ((PemEncoded)var3).retain();
         }
      }

      boolean var12 = false;
      ByteBuf var4 = null;

      PemValue var14;
      try {
         for (X509Certificate var8 : var2) {
            if (var8 == null) {
               throw new IllegalArgumentException("Null element in chain: " + Arrays.toString((Object[])var2));
            }

            if (var8 instanceof PemEncoded) {
               var4 = append(var0, var1, (PemEncoded)var8, var2.length, var4);
            } else {
               var4 = append(var0, var1, var8, var2.length, var4);
            }
         }

         PemValue var13 = new PemValue(var4, false);
         var12 = true;
         var14 = var13;
      } finally {
         if (!var12 && var4 != null) {
            var4.release();
         }
      }

      return var14;
   }

   private static ByteBuf append(ByteBufAllocator var0, boolean var1, PemEncoded var2, int var3, ByteBuf var4) {
      ByteBuf var5 = var2.content();
      if (var4 == null) {
         var4 = newBuffer(var0, var1, var5.readableBytes() * var3);
      }

      var4.writeBytes(var5.slice());
      return var4;
   }

   private static ByteBuf append(ByteBufAllocator var0, boolean var1, X509Certificate var2, int var3, ByteBuf var4) throws CertificateEncodingException {
      ByteBuf var5 = Unpooled.wrappedBuffer(var2.getEncoded());

      try {
         ByteBuf var6 = SslUtils.toBase64(var0, var5);

         try {
            if (var4 == null) {
               var4 = newBuffer(var0, var1, (BEGIN_CERT.length + var6.readableBytes() + END_CERT.length) * var3);
            }

            var4.writeBytes(BEGIN_CERT);
            var4.writeBytes(var6);
            var4.writeBytes(END_CERT);
         } finally {
            var6.release();
         }
      } finally {
         var5.release();
      }

      return var4;
   }

   private static ByteBuf newBuffer(ByteBufAllocator var0, boolean var1, int var2) {
      return var1 ? var0.directBuffer(var2) : var0.buffer(var2);
   }

   public static PemX509Certificate valueOf(byte[] var0) {
      return valueOf(Unpooled.wrappedBuffer(var0));
   }

   public static PemX509Certificate valueOf(ByteBuf var0) {
      return new PemX509Certificate(var0);
   }

   private PemX509Certificate(ByteBuf var1) {
      this.content = ObjectUtil.checkNotNull(var1, "content");
   }

   @Override
   public boolean isSensitive() {
      return false;
   }

   @Override
   public int refCnt() {
      return this.content.refCnt();
   }

   @Override
   public ByteBuf content() {
      int var1 = this.refCnt();
      if (var1 <= 0) {
         throw new IllegalReferenceCountException(var1);
      } else {
         return this.content;
      }
   }

   public PemX509Certificate copy() {
      return this.replace(this.content.copy());
   }

   public PemX509Certificate duplicate() {
      return this.replace(this.content.duplicate());
   }

   public PemX509Certificate retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public PemX509Certificate replace(ByteBuf var1) {
      return new PemX509Certificate(var1);
   }

   public PemX509Certificate retain() {
      this.content.retain();
      return this;
   }

   public PemX509Certificate retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public PemX509Certificate touch() {
      this.content.touch();
      return this;
   }

   public PemX509Certificate touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   @Override
   public boolean release() {
      return this.content.release();
   }

   @Override
   public boolean release(int var1) {
      return this.content.release(var1);
   }

   @Override
   public byte[] getEncoded() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasUnsupportedCriticalExtension() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> getCriticalExtensionOIDs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> getNonCriticalExtensionOIDs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte[] getExtensionValue(String var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void checkValidity() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void checkValidity(Date var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getVersion() {
      throw new UnsupportedOperationException();
   }

   @Override
   public BigInteger getSerialNumber() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Principal getIssuerDN() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Principal getSubjectDN() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Date getNotBefore() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Date getNotAfter() {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte[] getTBSCertificate() {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte[] getSignature() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSigAlgName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSigAlgOID() {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte[] getSigAlgParams() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean[] getIssuerUniqueID() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean[] getSubjectUniqueID() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean[] getKeyUsage() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getBasicConstraints() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void verify(PublicKey var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void verify(PublicKey var1, String var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public PublicKey getPublicKey() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PemX509Certificate)) {
         return false;
      } else {
         PemX509Certificate var2 = (PemX509Certificate)var1;
         return this.content.equals(var2.content);
      }
   }

   @Override
   public int hashCode() {
      return this.content.hashCode();
   }

   @Override
   public String toString() {
      return this.content.toString(CharsetUtil.UTF_8);
   }
}
