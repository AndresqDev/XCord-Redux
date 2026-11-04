package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class FingerprintTrustManagerFactory extends SimpleTrustManagerFactory {
   private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
   private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
   private final FastThreadLocal<MessageDigest> tlmd;
   private final TrustManager tm = new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         this.checkTrusted("client", var1);
      }

      @Override
      public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         this.checkTrusted("server", var1);
      }

      private void checkTrusted(String var1, X509Certificate[] var2) throws CertificateException {
         X509Certificate var3 = var2[0];
         byte[] var4 = this.fingerprint(var3);
         boolean var5 = false;

         for (byte[] var9 : FingerprintTrustManagerFactory.this.fingerprints) {
            if (Arrays.equals(var4, var9)) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            throw new CertificateException(var1 + " certificate with unknown fingerprint: " + var3.getSubjectDN());
         }
      }

      private byte[] fingerprint(X509Certificate var1) throws CertificateEncodingException {
         MessageDigest var2 = FingerprintTrustManagerFactory.this.tlmd.get();
         var2.reset();
         return var2.digest(var1.getEncoded());
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return EmptyArrays.EMPTY_X509_CERTIFICATES;
      }
   };
   private final byte[][] fingerprints;

   public static FingerprintTrustManagerFactoryBuilder builder(String var0) {
      return new FingerprintTrustManagerFactoryBuilder(var0);
   }

   @Deprecated
   public FingerprintTrustManagerFactory(Iterable<String> var1) {
      this("SHA1", toFingerprintArray(var1));
   }

   @Deprecated
   public FingerprintTrustManagerFactory(String... var1) {
      this("SHA1", toFingerprintArray(Arrays.asList(var1)));
   }

   @Deprecated
   public FingerprintTrustManagerFactory(byte[]... var1) {
      this("SHA1", var1);
   }

   FingerprintTrustManagerFactory(final String var1, byte[][] var2) {
      ObjectUtil.checkNotNull(var1, "algorithm");
      ObjectUtil.checkNotNull(var2, "fingerprints");
      if (var2.length == 0) {
         throw new IllegalArgumentException("No fingerprints provided");
      } else {
         MessageDigest var3;
         try {
            var3 = MessageDigest.getInstance(var1);
         } catch (NoSuchAlgorithmException var10) {
            throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", var1), var10);
         }

         int var4 = var3.getDigestLength();
         ArrayList var5 = new ArrayList(var2.length);

         for (byte[] var9 : var2) {
            if (var9 == null) {
               break;
            }

            if (var9.length != var4) {
               throw new IllegalArgumentException(
                  String.format(
                     "malformed fingerprint (length is %d but expected %d): %s", var9.length, var4, ByteBufUtil.hexDump(Unpooled.wrappedBuffer(var9))
                  )
               );
            }

            var5.add(var9.clone());
         }

         this.tlmd = new FastThreadLocal<MessageDigest>() {
            protected MessageDigest initialValue() {
               try {
                  return MessageDigest.getInstance(var1);
               } catch (NoSuchAlgorithmException var2) {
                  throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", var1), var2);
               }
            }
         };
         this.fingerprints = var5.toArray(new byte[0][]);
      }
   }

   static byte[][] toFingerprintArray(Iterable<String> var0) {
      ObjectUtil.checkNotNull(var0, "fingerprints");
      ArrayList var1 = new ArrayList();

      for (String var3 : var0) {
         if (var3 == null) {
            break;
         }

         if (!FINGERPRINT_PATTERN.matcher(var3).matches()) {
            throw new IllegalArgumentException("malformed fingerprint: " + var3);
         }

         var3 = FINGERPRINT_STRIP_PATTERN.matcher(var3).replaceAll("");
         var1.add(StringUtil.decodeHexDump(var3));
      }

      return var1.toArray(new byte[0][]);
   }

   @Override
   protected void engineInit(KeyStore var1) throws Exception {
   }

   @Override
   protected void engineInit(ManagerFactoryParameters var1) throws Exception {
   }

   @Override
   protected TrustManager[] engineGetTrustManagers() {
      return new TrustManager[]{this.tm};
   }
}
