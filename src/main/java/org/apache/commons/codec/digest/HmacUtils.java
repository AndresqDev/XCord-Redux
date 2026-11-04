package org.apache.commons.codec.digest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

public final class HmacUtils {
   private static final int STREAM_BUFFER_LENGTH = 1024;
   private final Mac mac;

   public static boolean isAvailable(String var0) {
      try {
         Mac.getInstance(var0);
         return true;
      } catch (NoSuchAlgorithmException var2) {
         return false;
      }
   }

   public static boolean isAvailable(HmacAlgorithms var0) {
      try {
         Mac.getInstance(var0.getName());
         return true;
      } catch (NoSuchAlgorithmException var2) {
         return false;
      }
   }

   @Deprecated
   public static Mac getHmacMd5(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_MD5, var0);
   }

   @Deprecated
   public static Mac getHmacSha1(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, var0);
   }

   @Deprecated
   public static Mac getHmacSha256(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, var0);
   }

   @Deprecated
   public static Mac getHmacSha384(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, var0);
   }

   @Deprecated
   public static Mac getHmacSha512(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, var0);
   }

   public static Mac getInitializedMac(HmacAlgorithms var0, byte[] var1) {
      return getInitializedMac(var0.getName(), var1);
   }

   public static Mac getInitializedMac(String var0, byte[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null key");
      } else {
         try {
            SecretKeySpec var2 = new SecretKeySpec(var1, var0);
            Mac var3 = Mac.getInstance(var0);
            var3.init(var2);
            return var3;
         } catch (NoSuchAlgorithmException var4) {
            throw new IllegalArgumentException(var4);
         } catch (InvalidKeyException var5) {
            throw new IllegalArgumentException(var5);
         }
      }
   }

   @Deprecated
   public static byte[] hmacMd5(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacMd5(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacMd5(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmac(var1);
   }

   @Deprecated
   public static String hmacMd5Hex(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacMd5Hex(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacMd5Hex(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_MD5, var0).hmacHex(var1);
   }

   @Deprecated
   public static byte[] hmacSha1(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha1(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha1(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmac(var1);
   }

   @Deprecated
   public static String hmacSha1Hex(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha1Hex(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha1Hex(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, var0).hmacHex(var1);
   }

   @Deprecated
   public static byte[] hmacSha256(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha256(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha256(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmac(var1);
   }

   @Deprecated
   public static String hmacSha256Hex(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha256Hex(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha256Hex(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, var0).hmacHex(var1);
   }

   @Deprecated
   public static byte[] hmacSha384(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha384(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha384(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmac(var1);
   }

   @Deprecated
   public static String hmacSha384Hex(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha384Hex(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha384Hex(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_384, var0).hmacHex(var1);
   }

   @Deprecated
   public static byte[] hmacSha512(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha512(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmac(var1);
   }

   @Deprecated
   public static byte[] hmacSha512(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmac(var1);
   }

   @Deprecated
   public static String hmacSha512Hex(byte[] var0, byte[] var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha512Hex(byte[] var0, InputStream var1) throws IOException {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmacHex(var1);
   }

   @Deprecated
   public static String hmacSha512Hex(String var0, String var1) {
      return new HmacUtils(HmacAlgorithms.HMAC_SHA_512, var0).hmacHex(var1);
   }

   public static Mac updateHmac(Mac var0, byte[] var1) {
      var0.reset();
      var0.update(var1);
      return var0;
   }

   public static Mac updateHmac(Mac var0, InputStream var1) throws IOException {
      var0.reset();
      byte[] var2 = new byte[1024];

      for (int var3 = var1.read(var2, 0, 1024); var3 > -1; var3 = var1.read(var2, 0, 1024)) {
         var0.update(var2, 0, var3);
      }

      return var0;
   }

   public static Mac updateHmac(Mac var0, String var1) {
      var0.reset();
      var0.update(StringUtils.getBytesUtf8(var1));
      return var0;
   }

   @Deprecated
   public HmacUtils() {
      this(null);
   }

   private HmacUtils(Mac var1) {
      this.mac = var1;
   }

   public HmacUtils(String var1, byte[] var2) {
      this(getInitializedMac(var1, var2));
   }

   public HmacUtils(String var1, String var2) {
      this(var1, StringUtils.getBytesUtf8(var2));
   }

   public HmacUtils(HmacAlgorithms var1, String var2) {
      this(var1.getName(), StringUtils.getBytesUtf8(var2));
   }

   public HmacUtils(HmacAlgorithms var1, byte[] var2) {
      this(var1.getName(), var2);
   }

   public byte[] hmac(byte[] var1) {
      return this.mac.doFinal(var1);
   }

   public String hmacHex(byte[] var1) {
      return Hex.encodeHexString(this.hmac(var1));
   }

   public byte[] hmac(String var1) {
      return this.mac.doFinal(StringUtils.getBytesUtf8(var1));
   }

   public String hmacHex(String var1) {
      return Hex.encodeHexString(this.hmac(var1));
   }

   public byte[] hmac(ByteBuffer var1) {
      this.mac.update(var1);
      return this.mac.doFinal();
   }

   public String hmacHex(ByteBuffer var1) {
      return Hex.encodeHexString(this.hmac(var1));
   }

   public byte[] hmac(InputStream var1) throws IOException {
      byte[] var2 = new byte[1024];

      int var3;
      while ((var3 = var1.read(var2, 0, 1024)) > -1) {
         this.mac.update(var2, 0, var3);
      }

      return this.mac.doFinal();
   }

   public String hmacHex(InputStream var1) throws IOException {
      return Hex.encodeHexString(this.hmac(var1));
   }

   public byte[] hmac(File var1) throws IOException {
      BufferedInputStream var2 = new BufferedInputStream(new FileInputStream(var1));

      byte[] var3;
      try {
         var3 = this.hmac(var2);
      } finally {
         var2.close();
      }

      return var3;
   }

   public String hmacHex(File var1) throws IOException {
      return Hex.encodeHexString(this.hmac(var1));
   }
}
