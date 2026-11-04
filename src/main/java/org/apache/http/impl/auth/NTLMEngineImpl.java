package org.apache.http.impl.auth;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;

final class NTLMEngineImpl implements NTLMEngine {
   private static final Charset UNICODE_LITTLE_UNMARKED = Charset.forName("UnicodeLittleUnmarked");
   private static final Charset DEFAULT_CHARSET = Consts.ASCII;
   static final int FLAG_REQUEST_UNICODE_ENCODING = 1;
   static final int FLAG_REQUEST_OEM_ENCODING = 2;
   static final int FLAG_REQUEST_TARGET = 4;
   static final int FLAG_REQUEST_SIGN = 16;
   static final int FLAG_REQUEST_SEAL = 32;
   static final int FLAG_REQUEST_LAN_MANAGER_KEY = 128;
   static final int FLAG_REQUEST_NTLMv1 = 512;
   static final int FLAG_DOMAIN_PRESENT = 4096;
   static final int FLAG_WORKSTATION_PRESENT = 8192;
   static final int FLAG_REQUEST_ALWAYS_SIGN = 32768;
   static final int FLAG_REQUEST_NTLM2_SESSION = 524288;
   static final int FLAG_REQUEST_VERSION = 33554432;
   static final int FLAG_TARGETINFO_PRESENT = 8388608;
   static final int FLAG_REQUEST_128BIT_KEY_EXCH = 536870912;
   static final int FLAG_REQUEST_EXPLICIT_KEY_EXCH = 1073741824;
   static final int FLAG_REQUEST_56BIT_ENCRYPTION = Integer.MIN_VALUE;
   static final int MSV_AV_EOL = 0;
   static final int MSV_AV_NB_COMPUTER_NAME = 1;
   static final int MSV_AV_NB_DOMAIN_NAME = 2;
   static final int MSV_AV_DNS_COMPUTER_NAME = 3;
   static final int MSV_AV_DNS_DOMAIN_NAME = 4;
   static final int MSV_AV_DNS_TREE_NAME = 5;
   static final int MSV_AV_FLAGS = 6;
   static final int MSV_AV_TIMESTAMP = 7;
   static final int MSV_AV_SINGLE_HOST = 8;
   static final int MSV_AV_TARGET_NAME = 9;
   static final int MSV_AV_CHANNEL_BINDINGS = 10;
   static final int MSV_AV_FLAGS_ACCOUNT_AUTH_CONSTAINED = 1;
   static final int MSV_AV_FLAGS_MIC = 2;
   static final int MSV_AV_FLAGS_UNTRUSTED_TARGET_SPN = 4;
   private static final SecureRandom RND_GEN;
   private static final byte[] SIGNATURE;
   private static final byte[] SIGN_MAGIC_SERVER;
   private static final byte[] SIGN_MAGIC_CLIENT;
   private static final byte[] SEAL_MAGIC_SERVER;
   private static final byte[] SEAL_MAGIC_CLIENT;
   private static final byte[] MAGIC_TLS_SERVER_ENDPOINT;
   private static final String TYPE_1_MESSAGE;

   private static byte[] getNullTerminatedAsciiString(String var0) {
      byte[] var1 = var0.getBytes(Consts.ASCII);
      byte[] var2 = new byte[var1.length + 1];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      var2[var1.length] = 0;
      return var2;
   }

   static String getType1Message(String var0, String var1) {
      return TYPE_1_MESSAGE;
   }

   static String getType3Message(String var0, String var1, String var2, String var3, byte[] var4, int var5, String var6, byte[] var7) throws NTLMEngineException {
      return new NTLMEngineImpl.Type3Message(var3, var2, var0, var1, var4, var5, var6, var7).getResponse();
   }

   static String getType3Message(
      String var0, String var1, String var2, String var3, byte[] var4, int var5, String var6, byte[] var7, Certificate var8, byte[] var9, byte[] var10
   ) throws NTLMEngineException {
      return new NTLMEngineImpl.Type3Message(var3, var2, var0, var1, var4, var5, var6, var7, var8, var9, var10).getResponse();
   }

   private static int readULong(byte[] var0, int var1) {
      return var0.length < var1 + 4 ? 0 : var0[var1] & 0xFF | (var0[var1 + 1] & 0xFF) << 8 | (var0[var1 + 2] & 0xFF) << 16 | (var0[var1 + 3] & 0xFF) << 24;
   }

   private static int readUShort(byte[] var0, int var1) {
      return var0.length < var1 + 2 ? 0 : var0[var1] & 0xFF | (var0[var1 + 1] & 0xFF) << 8;
   }

   private static byte[] readSecurityBuffer(byte[] var0, int var1) {
      int var2 = readUShort(var0, var1);
      int var3 = readULong(var0, var1 + 4);
      if (var0.length < var3 + var2) {
         return new byte[var2];
      } else {
         byte[] var4 = new byte[var2];
         System.arraycopy(var0, var3, var4, 0, var2);
         return var4;
      }
   }

   private static byte[] makeRandomChallenge(Random var0) {
      byte[] var1 = new byte[8];
      synchronized (var0) {
         var0.nextBytes(var1);
         return var1;
      }
   }

   private static byte[] makeSecondaryKey(Random var0) {
      byte[] var1 = new byte[16];
      synchronized (var0) {
         var0.nextBytes(var1);
         return var1;
      }
   }

   static byte[] hmacMD5(byte[] var0, byte[] var1) throws NTLMEngineException {
      NTLMEngineImpl.HMACMD5 var2 = new NTLMEngineImpl.HMACMD5(var1);
      var2.update(var0);
      return var2.getOutput();
   }

   static byte[] RC4(byte[] var0, byte[] var1) throws NTLMEngineException {
      try {
         Cipher var2 = Cipher.getInstance("RC4");
         var2.init(1, new SecretKeySpec(var1, "RC4"));
         return var2.doFinal(var0);
      } catch (Exception var3) {
         throw new NTLMEngineException(var3.getMessage(), var3);
      }
   }

   static byte[] ntlm2SessionResponse(byte[] var0, byte[] var1, byte[] var2) throws NTLMEngineException {
      try {
         MessageDigest var3 = getMD5();
         var3.update(var1);
         var3.update(var2);
         byte[] var4 = var3.digest();
         byte[] var5 = new byte[8];
         System.arraycopy(var4, 0, var5, 0, 8);
         return lmResponse(var0, var5);
      } catch (Exception var6) {
         if (var6 instanceof NTLMEngineException) {
            throw (NTLMEngineException)var6;
         } else {
            throw new NTLMEngineException(var6.getMessage(), var6);
         }
      }
   }

   private static byte[] lmHash(String var0) throws NTLMEngineException {
      try {
         byte[] var1 = var0.toUpperCase(Locale.ROOT).getBytes(Consts.ASCII);
         int var2 = Math.min(var1.length, 14);
         byte[] var3 = new byte[14];
         System.arraycopy(var1, 0, var3, 0, var2);
         Key var4 = createDESKey(var3, 0);
         Key var5 = createDESKey(var3, 7);
         byte[] var6 = "KGS!@#$%".getBytes(Consts.ASCII);
         Cipher var7 = Cipher.getInstance("DES/ECB/NoPadding");
         var7.init(1, var4);
         byte[] var8 = var7.doFinal(var6);
         var7.init(1, var5);
         byte[] var9 = var7.doFinal(var6);
         byte[] var10 = new byte[16];
         System.arraycopy(var8, 0, var10, 0, 8);
         System.arraycopy(var9, 0, var10, 8, 8);
         return var10;
      } catch (Exception var11) {
         throw new NTLMEngineException(var11.getMessage(), var11);
      }
   }

   private static byte[] ntlmHash(String var0) throws NTLMEngineException {
      if (UNICODE_LITTLE_UNMARKED == null) {
         throw new NTLMEngineException("Unicode not supported");
      } else {
         byte[] var1 = var0.getBytes(UNICODE_LITTLE_UNMARKED);
         NTLMEngineImpl.MD4 var2 = new NTLMEngineImpl.MD4();
         var2.update(var1);
         return var2.getOutput();
      }
   }

   private static byte[] lmv2Hash(String var0, String var1, byte[] var2) throws NTLMEngineException {
      if (UNICODE_LITTLE_UNMARKED == null) {
         throw new NTLMEngineException("Unicode not supported");
      } else {
         NTLMEngineImpl.HMACMD5 var3 = new NTLMEngineImpl.HMACMD5(var2);
         var3.update(var1.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
         if (var0 != null) {
            var3.update(var0.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
         }

         return var3.getOutput();
      }
   }

   private static byte[] ntlmv2Hash(String var0, String var1, byte[] var2) throws NTLMEngineException {
      if (UNICODE_LITTLE_UNMARKED == null) {
         throw new NTLMEngineException("Unicode not supported");
      } else {
         NTLMEngineImpl.HMACMD5 var3 = new NTLMEngineImpl.HMACMD5(var2);
         var3.update(var1.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
         if (var0 != null) {
            var3.update(var0.getBytes(UNICODE_LITTLE_UNMARKED));
         }

         return var3.getOutput();
      }
   }

   private static byte[] lmResponse(byte[] var0, byte[] var1) throws NTLMEngineException {
      try {
         byte[] var2 = new byte[21];
         System.arraycopy(var0, 0, var2, 0, 16);
         Key var3 = createDESKey(var2, 0);
         Key var4 = createDESKey(var2, 7);
         Key var5 = createDESKey(var2, 14);
         Cipher var6 = Cipher.getInstance("DES/ECB/NoPadding");
         var6.init(1, var3);
         byte[] var7 = var6.doFinal(var1);
         var6.init(1, var4);
         byte[] var8 = var6.doFinal(var1);
         var6.init(1, var5);
         byte[] var9 = var6.doFinal(var1);
         byte[] var10 = new byte[24];
         System.arraycopy(var7, 0, var10, 0, 8);
         System.arraycopy(var8, 0, var10, 8, 8);
         System.arraycopy(var9, 0, var10, 16, 8);
         return var10;
      } catch (Exception var11) {
         throw new NTLMEngineException(var11.getMessage(), var11);
      }
   }

   private static byte[] lmv2Response(byte[] var0, byte[] var1, byte[] var2) {
      NTLMEngineImpl.HMACMD5 var3 = new NTLMEngineImpl.HMACMD5(var0);
      var3.update(var1);
      var3.update(var2);
      byte[] var4 = var3.getOutput();
      byte[] var5 = new byte[var4.length + var2.length];
      System.arraycopy(var4, 0, var5, 0, var4.length);
      System.arraycopy(var2, 0, var5, var4.length, var2.length);
      return var5;
   }

   private static byte[] encodeLong(int var0) {
      byte[] var1 = new byte[4];
      encodeLong(var1, 0, var0);
      return var1;
   }

   private static void encodeLong(byte[] var0, int var1, int var2) {
      var0[var1 + 0] = (byte)(var2 & 0xFF);
      var0[var1 + 1] = (byte)(var2 >> 8 & 0xFF);
      var0[var1 + 2] = (byte)(var2 >> 16 & 0xFF);
      var0[var1 + 3] = (byte)(var2 >> 24 & 0xFF);
   }

   private static byte[] createBlob(byte[] var0, byte[] var1, byte[] var2) {
      byte[] var3 = new byte[]{1, 1, 0, 0};
      byte[] var4 = new byte[]{0, 0, 0, 0};
      byte[] var5 = new byte[]{0, 0, 0, 0};
      byte[] var6 = new byte[]{0, 0, 0, 0};
      byte[] var7 = new byte[var3.length + var4.length + var2.length + 8 + var5.length + var1.length + var6.length];
      int var8 = 0;
      System.arraycopy(var3, 0, var7, var8, var3.length);
      var8 += var3.length;
      System.arraycopy(var4, 0, var7, var8, var4.length);
      var8 += var4.length;
      System.arraycopy(var2, 0, var7, var8, var2.length);
      var8 += var2.length;
      System.arraycopy(var0, 0, var7, var8, 8);
      var8 += 8;
      System.arraycopy(var5, 0, var7, var8, var5.length);
      var8 += var5.length;
      System.arraycopy(var1, 0, var7, var8, var1.length);
      var8 += var1.length;
      System.arraycopy(var6, 0, var7, var8, var6.length);
      var8 += var6.length;
      return var7;
   }

   private static Key createDESKey(byte[] var0, int var1) {
      byte[] var2 = new byte[7];
      System.arraycopy(var0, var1, var2, 0, 7);
      byte[] var3 = new byte[]{
         var2[0],
         (byte)(var2[0] << 7 | (var2[1] & 255) >>> 1),
         (byte)(var2[1] << 6 | (var2[2] & 255) >>> 2),
         (byte)(var2[2] << 5 | (var2[3] & 255) >>> 3),
         (byte)(var2[3] << 4 | (var2[4] & 255) >>> 4),
         (byte)(var2[4] << 3 | (var2[5] & 255) >>> 5),
         (byte)(var2[5] << 2 | (var2[6] & 255) >>> 6),
         (byte)(var2[6] << 1)
      };
      oddParity(var3);
      return new SecretKeySpec(var3, "DES");
   }

   private static void oddParity(byte[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         byte var2 = var0[var1];
         boolean var3 = ((var2 >>> 7 ^ var2 >>> 6 ^ var2 >>> 5 ^ var2 >>> 4 ^ var2 >>> 3 ^ var2 >>> 2 ^ var2 >>> 1) & 1) == 0;
         if (var3) {
            var0[var1] = (byte)(var0[var1] | 1);
         } else {
            var0[var1] &= -2;
         }
      }
   }

   private static Charset getCharset(int var0) throws NTLMEngineException {
      if ((var0 & 1) == 0) {
         return DEFAULT_CHARSET;
      } else if (UNICODE_LITTLE_UNMARKED == null) {
         throw new NTLMEngineException("Unicode not supported");
      } else {
         return UNICODE_LITTLE_UNMARKED;
      }
   }

   private static String stripDotSuffix(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.indexOf(46);
         return var1 != -1 ? var0.substring(0, var1) : var0;
      }
   }

   private static String convertHost(String var0) {
      return stripDotSuffix(var0);
   }

   private static String convertDomain(String var0) {
      return stripDotSuffix(var0);
   }

   static void writeUShort(byte[] var0, int var1, int var2) {
      var0[var2] = (byte)(var1 & 0xFF);
      var0[var2 + 1] = (byte)(var1 >> 8 & 0xFF);
   }

   static void writeULong(byte[] var0, int var1, int var2) {
      var0[var2] = (byte)(var1 & 0xFF);
      var0[var2 + 1] = (byte)(var1 >> 8 & 0xFF);
      var0[var2 + 2] = (byte)(var1 >> 16 & 0xFF);
      var0[var2 + 3] = (byte)(var1 >> 24 & 0xFF);
   }

   static int F(int var0, int var1, int var2) {
      return var0 & var1 | ~var0 & var2;
   }

   static int G(int var0, int var1, int var2) {
      return var0 & var1 | var0 & var2 | var1 & var2;
   }

   static int H(int var0, int var1, int var2) {
      return var0 ^ var1 ^ var2;
   }

   static int rotintlft(int var0, int var1) {
      return var0 << var1 | var0 >>> 32 - var1;
   }

   static MessageDigest getMD5() {
      try {
         return MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var1) {
         throw new RuntimeException("MD5 message digest doesn't seem to exist - fatal error: " + var1.getMessage(), var1);
      }
   }

   @Override
   public String generateType1Msg(String var1, String var2) throws NTLMEngineException {
      return getType1Message(var2, var1);
   }

   @Override
   public String generateType3Msg(String var1, String var2, String var3, String var4, String var5) throws NTLMEngineException {
      NTLMEngineImpl.Type2Message var6 = new NTLMEngineImpl.Type2Message(var5);
      return getType3Message(var1, var2, var4, var3, var6.getChallenge(), var6.getFlags(), var6.getTarget(), var6.getTargetInfo());
   }

   static {
      SecureRandom var0 = null;

      try {
         var0 = SecureRandom.getInstance("SHA1PRNG");
      } catch (Exception var2) {
      }

      RND_GEN = var0;
      SIGNATURE = getNullTerminatedAsciiString("NTLMSSP");
      SIGN_MAGIC_SERVER = getNullTerminatedAsciiString("session key to server-to-client signing key magic constant");
      SIGN_MAGIC_CLIENT = getNullTerminatedAsciiString("session key to client-to-server signing key magic constant");
      SEAL_MAGIC_SERVER = getNullTerminatedAsciiString("session key to server-to-client sealing key magic constant");
      SEAL_MAGIC_CLIENT = getNullTerminatedAsciiString("session key to client-to-server sealing key magic constant");
      MAGIC_TLS_SERVER_ENDPOINT = "tls-server-end-point:".getBytes(Consts.ASCII);
      TYPE_1_MESSAGE = new NTLMEngineImpl.Type1Message().getResponse();
   }

   protected static class CipherGen {
      protected final Random random;
      protected final long currentTime;
      protected final String domain;
      protected final String user;
      protected final String password;
      protected final byte[] challenge;
      protected final String target;
      protected final byte[] targetInformation;
      protected byte[] clientChallenge;
      protected byte[] clientChallenge2;
      protected byte[] secondaryKey;
      protected byte[] timestamp;
      protected byte[] lmHash = null;
      protected byte[] lmResponse = null;
      protected byte[] ntlmHash = null;
      protected byte[] ntlmResponse = null;
      protected byte[] ntlmv2Hash = null;
      protected byte[] lmv2Hash = null;
      protected byte[] lmv2Response = null;
      protected byte[] ntlmv2Blob = null;
      protected byte[] ntlmv2Response = null;
      protected byte[] ntlm2SessionResponse = null;
      protected byte[] lm2SessionResponse = null;
      protected byte[] lmUserSessionKey = null;
      protected byte[] ntlmUserSessionKey = null;
      protected byte[] ntlmv2UserSessionKey = null;
      protected byte[] ntlm2SessionResponseUserSessionKey = null;
      protected byte[] lanManagerSessionKey = null;

      @Deprecated
      public CipherGen(String var1, String var2, String var3, byte[] var4, String var5, byte[] var6, byte[] var7, byte[] var8, byte[] var9, byte[] var10) {
         this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      public CipherGen(
         Random var1,
         long var2,
         String var4,
         String var5,
         String var6,
         byte[] var7,
         String var8,
         byte[] var9,
         byte[] var10,
         byte[] var11,
         byte[] var12,
         byte[] var13
      ) {
         this.random = var1;
         this.currentTime = var2;
         this.domain = var4;
         this.target = var8;
         this.user = var5;
         this.password = var6;
         this.challenge = var7;
         this.targetInformation = var9;
         this.clientChallenge = var10;
         this.clientChallenge2 = var11;
         this.secondaryKey = var12;
         this.timestamp = var13;
      }

      @Deprecated
      public CipherGen(String var1, String var2, String var3, byte[] var4, String var5, byte[] var6) {
         this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), var1, var2, var3, var4, var5, var6);
      }

      public CipherGen(Random var1, long var2, String var4, String var5, String var6, byte[] var7, String var8, byte[] var9) {
         this(var1, var2, var4, var5, var6, var7, var8, var9, null, null, null, null);
      }

      public byte[] getClientChallenge() throws NTLMEngineException {
         if (this.clientChallenge == null) {
            this.clientChallenge = NTLMEngineImpl.makeRandomChallenge(this.random);
         }

         return this.clientChallenge;
      }

      public byte[] getClientChallenge2() throws NTLMEngineException {
         if (this.clientChallenge2 == null) {
            this.clientChallenge2 = NTLMEngineImpl.makeRandomChallenge(this.random);
         }

         return this.clientChallenge2;
      }

      public byte[] getSecondaryKey() throws NTLMEngineException {
         if (this.secondaryKey == null) {
            this.secondaryKey = NTLMEngineImpl.makeSecondaryKey(this.random);
         }

         return this.secondaryKey;
      }

      public byte[] getLMHash() throws NTLMEngineException {
         if (this.lmHash == null) {
            this.lmHash = NTLMEngineImpl.lmHash(this.password);
         }

         return this.lmHash;
      }

      public byte[] getLMResponse() throws NTLMEngineException {
         if (this.lmResponse == null) {
            this.lmResponse = NTLMEngineImpl.lmResponse(this.getLMHash(), this.challenge);
         }

         return this.lmResponse;
      }

      public byte[] getNTLMHash() throws NTLMEngineException {
         if (this.ntlmHash == null) {
            this.ntlmHash = NTLMEngineImpl.ntlmHash(this.password);
         }

         return this.ntlmHash;
      }

      public byte[] getNTLMResponse() throws NTLMEngineException {
         if (this.ntlmResponse == null) {
            this.ntlmResponse = NTLMEngineImpl.lmResponse(this.getNTLMHash(), this.challenge);
         }

         return this.ntlmResponse;
      }

      public byte[] getLMv2Hash() throws NTLMEngineException {
         if (this.lmv2Hash == null) {
            this.lmv2Hash = NTLMEngineImpl.lmv2Hash(this.domain, this.user, this.getNTLMHash());
         }

         return this.lmv2Hash;
      }

      public byte[] getNTLMv2Hash() throws NTLMEngineException {
         if (this.ntlmv2Hash == null) {
            this.ntlmv2Hash = NTLMEngineImpl.ntlmv2Hash(this.domain, this.user, this.getNTLMHash());
         }

         return this.ntlmv2Hash;
      }

      public byte[] getTimestamp() {
         if (this.timestamp == null) {
            long var1 = this.currentTime;
            var1 += 11644473600000L;
            var1 *= 10000L;
            this.timestamp = new byte[8];

            for (int var3 = 0; var3 < 8; var3++) {
               this.timestamp[var3] = (byte)((int)var1);
               var1 >>>= 8;
            }
         }

         return this.timestamp;
      }

      public byte[] getNTLMv2Blob() throws NTLMEngineException {
         if (this.ntlmv2Blob == null) {
            this.ntlmv2Blob = NTLMEngineImpl.createBlob(this.getClientChallenge2(), this.targetInformation, this.getTimestamp());
         }

         return this.ntlmv2Blob;
      }

      public byte[] getNTLMv2Response() throws NTLMEngineException {
         if (this.ntlmv2Response == null) {
            this.ntlmv2Response = NTLMEngineImpl.lmv2Response(this.getNTLMv2Hash(), this.challenge, this.getNTLMv2Blob());
         }

         return this.ntlmv2Response;
      }

      public byte[] getLMv2Response() throws NTLMEngineException {
         if (this.lmv2Response == null) {
            this.lmv2Response = NTLMEngineImpl.lmv2Response(this.getLMv2Hash(), this.challenge, this.getClientChallenge());
         }

         return this.lmv2Response;
      }

      public byte[] getNTLM2SessionResponse() throws NTLMEngineException {
         if (this.ntlm2SessionResponse == null) {
            this.ntlm2SessionResponse = NTLMEngineImpl.ntlm2SessionResponse(this.getNTLMHash(), this.challenge, this.getClientChallenge());
         }

         return this.ntlm2SessionResponse;
      }

      public byte[] getLM2SessionResponse() throws NTLMEngineException {
         if (this.lm2SessionResponse == null) {
            byte[] var1 = this.getClientChallenge();
            this.lm2SessionResponse = new byte[24];
            System.arraycopy(var1, 0, this.lm2SessionResponse, 0, var1.length);
            Arrays.fill(this.lm2SessionResponse, var1.length, this.lm2SessionResponse.length, (byte)0);
         }

         return this.lm2SessionResponse;
      }

      public byte[] getLMUserSessionKey() throws NTLMEngineException {
         if (this.lmUserSessionKey == null) {
            this.lmUserSessionKey = new byte[16];
            System.arraycopy(this.getLMHash(), 0, this.lmUserSessionKey, 0, 8);
            Arrays.fill(this.lmUserSessionKey, 8, 16, (byte)0);
         }

         return this.lmUserSessionKey;
      }

      public byte[] getNTLMUserSessionKey() throws NTLMEngineException {
         if (this.ntlmUserSessionKey == null) {
            NTLMEngineImpl.MD4 var1 = new NTLMEngineImpl.MD4();
            var1.update(this.getNTLMHash());
            this.ntlmUserSessionKey = var1.getOutput();
         }

         return this.ntlmUserSessionKey;
      }

      public byte[] getNTLMv2UserSessionKey() throws NTLMEngineException {
         if (this.ntlmv2UserSessionKey == null) {
            byte[] var1 = this.getNTLMv2Hash();
            byte[] var2 = new byte[16];
            System.arraycopy(this.getNTLMv2Response(), 0, var2, 0, 16);
            this.ntlmv2UserSessionKey = NTLMEngineImpl.hmacMD5(var2, var1);
         }

         return this.ntlmv2UserSessionKey;
      }

      public byte[] getNTLM2SessionResponseUserSessionKey() throws NTLMEngineException {
         if (this.ntlm2SessionResponseUserSessionKey == null) {
            byte[] var1 = this.getLM2SessionResponse();
            byte[] var2 = new byte[this.challenge.length + var1.length];
            System.arraycopy(this.challenge, 0, var2, 0, this.challenge.length);
            System.arraycopy(var1, 0, var2, this.challenge.length, var1.length);
            this.ntlm2SessionResponseUserSessionKey = NTLMEngineImpl.hmacMD5(var2, this.getNTLMUserSessionKey());
         }

         return this.ntlm2SessionResponseUserSessionKey;
      }

      public byte[] getLanManagerSessionKey() throws NTLMEngineException {
         if (this.lanManagerSessionKey == null) {
            try {
               byte[] var1 = new byte[14];
               System.arraycopy(this.getLMHash(), 0, var1, 0, 8);
               Arrays.fill(var1, 8, var1.length, (byte)-67);
               Key var2 = NTLMEngineImpl.createDESKey(var1, 0);
               Key var3 = NTLMEngineImpl.createDESKey(var1, 7);
               byte[] var4 = new byte[8];
               System.arraycopy(this.getLMResponse(), 0, var4, 0, var4.length);
               Cipher var5 = Cipher.getInstance("DES/ECB/NoPadding");
               var5.init(1, var2);
               byte[] var6 = var5.doFinal(var4);
               var5 = Cipher.getInstance("DES/ECB/NoPadding");
               var5.init(1, var3);
               byte[] var7 = var5.doFinal(var4);
               this.lanManagerSessionKey = new byte[16];
               System.arraycopy(var6, 0, this.lanManagerSessionKey, 0, var6.length);
               System.arraycopy(var7, 0, this.lanManagerSessionKey, var6.length, var7.length);
            } catch (Exception var8) {
               throw new NTLMEngineException(var8.getMessage(), var8);
            }
         }

         return this.lanManagerSessionKey;
      }
   }

   static class HMACMD5 {
      protected final byte[] ipad;
      protected final byte[] opad;
      protected final MessageDigest md5;

      HMACMD5(byte[] var1) {
         byte[] var2 = var1;
         this.md5 = NTLMEngineImpl.getMD5();
         this.ipad = new byte[64];
         this.opad = new byte[64];
         int var3 = var1.length;
         if (var3 > 64) {
            this.md5.update(var1);
            var2 = this.md5.digest();
            var3 = var2.length;
         }

         int var4;
         for (var4 = 0; var4 < var3; var4++) {
            this.ipad[var4] = (byte)(var2[var4] ^ 54);
            this.opad[var4] = (byte)(var2[var4] ^ 92);
         }

         while (var4 < 64) {
            this.ipad[var4] = 54;
            this.opad[var4] = 92;
            var4++;
         }

         this.md5.reset();
         this.md5.update(this.ipad);
      }

      byte[] getOutput() {
         byte[] var1 = this.md5.digest();
         this.md5.update(this.opad);
         return this.md5.digest(var1);
      }

      void update(byte[] var1) {
         this.md5.update(var1);
      }

      void update(byte[] var1, int var2, int var3) {
         this.md5.update(var1, var2, var3);
      }
   }

   static class Handle {
      private final byte[] exportedSessionKey;
      private byte[] signingKey;
      private byte[] sealingKey;
      private final Cipher rc4;
      final NTLMEngineImpl.Mode mode;
      private final boolean isConnection;
      int sequenceNumber = 0;

      Handle(byte[] var1, NTLMEngineImpl.Mode var2, boolean var3) throws NTLMEngineException {
         this.exportedSessionKey = var1;
         this.isConnection = var3;
         this.mode = var2;

         try {
            MessageDigest var4 = NTLMEngineImpl.getMD5();
            MessageDigest var5 = NTLMEngineImpl.getMD5();
            var4.update(var1);
            var5.update(var1);
            if (var2 == NTLMEngineImpl.Mode.CLIENT) {
               var4.update(NTLMEngineImpl.SIGN_MAGIC_CLIENT);
               var5.update(NTLMEngineImpl.SEAL_MAGIC_CLIENT);
            } else {
               var4.update(NTLMEngineImpl.SIGN_MAGIC_SERVER);
               var5.update(NTLMEngineImpl.SEAL_MAGIC_SERVER);
            }

            this.signingKey = var4.digest();
            this.sealingKey = var5.digest();
         } catch (Exception var6) {
            throw new NTLMEngineException(var6.getMessage(), var6);
         }

         this.rc4 = this.initCipher();
      }

      public byte[] getSigningKey() {
         return this.signingKey;
      }

      public byte[] getSealingKey() {
         return this.sealingKey;
      }

      private Cipher initCipher() throws NTLMEngineException {
         try {
            Cipher var1 = Cipher.getInstance("RC4");
            if (this.mode == NTLMEngineImpl.Mode.CLIENT) {
               var1.init(1, new SecretKeySpec(this.sealingKey, "RC4"));
            } else {
               var1.init(2, new SecretKeySpec(this.sealingKey, "RC4"));
            }

            return var1;
         } catch (Exception var3) {
            throw new NTLMEngineException(var3.getMessage(), var3);
         }
      }

      private void advanceMessageSequence() throws NTLMEngineException {
         if (!this.isConnection) {
            MessageDigest var1 = NTLMEngineImpl.getMD5();
            var1.update(this.sealingKey);
            byte[] var2 = new byte[4];
            NTLMEngineImpl.writeULong(var2, this.sequenceNumber, 0);
            var1.update(var2);
            this.sealingKey = var1.digest();
            this.initCipher();
         }

         this.sequenceNumber++;
      }

      private byte[] encrypt(byte[] var1) {
         return this.rc4.update(var1);
      }

      private byte[] decrypt(byte[] var1) {
         return this.rc4.update(var1);
      }

      private byte[] computeSignature(byte[] var1) {
         byte[] var2 = new byte[16];
         var2[0] = 1;
         var2[1] = 0;
         var2[2] = 0;
         var2[3] = 0;
         NTLMEngineImpl.HMACMD5 var3 = new NTLMEngineImpl.HMACMD5(this.signingKey);
         var3.update(NTLMEngineImpl.encodeLong(this.sequenceNumber));
         var3.update(var1);
         byte[] var4 = var3.getOutput();
         byte[] var5 = new byte[8];
         System.arraycopy(var4, 0, var5, 0, 8);
         byte[] var6 = this.encrypt(var5);
         System.arraycopy(var6, 0, var2, 4, 8);
         NTLMEngineImpl.encodeLong(var2, 12, this.sequenceNumber);
         return var2;
      }

      private boolean validateSignature(byte[] var1, byte[] var2) {
         byte[] var3 = this.computeSignature(var2);
         return Arrays.equals(var1, var3);
      }

      public byte[] signAndEncryptMessage(byte[] var1) throws NTLMEngineException {
         byte[] var2 = this.encrypt(var1);
         byte[] var3 = this.computeSignature(var1);
         byte[] var4 = new byte[var3.length + var2.length];
         System.arraycopy(var3, 0, var4, 0, var3.length);
         System.arraycopy(var2, 0, var4, var3.length, var2.length);
         this.advanceMessageSequence();
         return var4;
      }

      public byte[] decryptAndVerifySignedMessage(byte[] var1) throws NTLMEngineException {
         byte[] var2 = new byte[16];
         System.arraycopy(var1, 0, var2, 0, var2.length);
         byte[] var3 = new byte[var1.length - 16];
         System.arraycopy(var1, 16, var3, 0, var3.length);
         byte[] var4 = this.decrypt(var3);
         if (!this.validateSignature(var2, var4)) {
            throw new NTLMEngineException("Wrong signature");
         } else {
            this.advanceMessageSequence();
            return var4;
         }
      }
   }

   static class MD4 {
      protected int A = 1732584193;
      protected int B = -271733879;
      protected int C = -1732584194;
      protected int D = 271733878;
      protected long count = 0L;
      protected final byte[] dataBuffer = new byte[64];

      void update(byte[] var1) {
         int var2 = (int)(this.count & 63L);
         int var3 = 0;

         while (var1.length - var3 + var2 >= this.dataBuffer.length) {
            int var4 = this.dataBuffer.length - var2;
            System.arraycopy(var1, var3, this.dataBuffer, var2, var4);
            this.count += (long)var4;
            var2 = 0;
            var3 += var4;
            this.processBuffer();
         }

         if (var3 < var1.length) {
            int var6 = var1.length - var3;
            System.arraycopy(var1, var3, this.dataBuffer, var2, var6);
            this.count += (long)var6;
            var2 += var6;
         }
      }

      byte[] getOutput() {
         int var1 = (int)(this.count & 63L);
         int var2 = var1 < 56 ? 56 - var1 : 120 - var1;
         byte[] var3 = new byte[var2 + 8];
         var3[0] = -128;

         for (int var4 = 0; var4 < 8; var4++) {
            var3[var2 + var4] = (byte)((int)(this.count * 8L >>> 8 * var4));
         }

         this.update(var3);
         byte[] var5 = new byte[16];
         NTLMEngineImpl.writeULong(var5, this.A, 0);
         NTLMEngineImpl.writeULong(var5, this.B, 4);
         NTLMEngineImpl.writeULong(var5, this.C, 8);
         NTLMEngineImpl.writeULong(var5, this.D, 12);
         return var5;
      }

      protected void processBuffer() {
         int[] var1 = new int[16];

         for (int var2 = 0; var2 < 16; var2++) {
            var1[var2] = (this.dataBuffer[var2 * 4] & 255)
               + ((this.dataBuffer[var2 * 4 + 1] & 255) << 8)
               + ((this.dataBuffer[var2 * 4 + 2] & 255) << 16)
               + ((this.dataBuffer[var2 * 4 + 3] & 255) << 24);
         }

         int var6 = this.A;
         int var3 = this.B;
         int var4 = this.C;
         int var5 = this.D;
         this.round1(var1);
         this.round2(var1);
         this.round3(var1);
         this.A += var6;
         this.B += var3;
         this.C += var4;
         this.D += var5;
      }

      protected void round1(int[] var1) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + var1[0], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + var1[1], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + var1[2], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + var1[3], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + var1[4], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + var1[5], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + var1[6], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + var1[7], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + var1[8], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + var1[9], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + var1[10], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + var1[11], 19);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + var1[12], 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + var1[13], 7);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + var1[14], 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + var1[15], 19);
      }

      protected void round2(int[] var1) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + var1[0] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + var1[4] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + var1[8] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + var1[12] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + var1[1] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + var1[5] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + var1[9] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + var1[13] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + var1[2] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + var1[6] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + var1[10] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + var1[14] + 1518500249, 13);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + var1[3] + 1518500249, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + var1[7] + 1518500249, 5);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + var1[11] + 1518500249, 9);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + var1[15] + 1518500249, 13);
      }

      protected void round3(int[] var1) {
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + var1[0] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + var1[8] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + var1[4] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + var1[12] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + var1[2] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + var1[10] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + var1[6] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + var1[14] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + var1[1] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + var1[9] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + var1[5] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + var1[13] + 1859775393, 15);
         this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + var1[3] + 1859775393, 3);
         this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + var1[11] + 1859775393, 9);
         this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + var1[7] + 1859775393, 11);
         this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + var1[15] + 1859775393, 15);
      }
   }

   static enum Mode {
      CLIENT,
      SERVER;
   }

   static class NTLMMessage {
      protected byte[] messageContents = null;
      protected int currentOutputPosition = 0;

      NTLMMessage() {
      }

      NTLMMessage(String var1, int var2) throws NTLMEngineException {
         this(Base64.decodeBase64(var1.getBytes(NTLMEngineImpl.DEFAULT_CHARSET)), var2);
      }

      NTLMMessage(byte[] var1, int var2) throws NTLMEngineException {
         this.messageContents = var1;
         if (this.messageContents.length < NTLMEngineImpl.SIGNATURE.length) {
            throw new NTLMEngineException("NTLM message decoding error - packet too short");
         } else {
            for (int var3 = 0; var3 < NTLMEngineImpl.SIGNATURE.length; var3++) {
               if (this.messageContents[var3] != NTLMEngineImpl.SIGNATURE[var3]) {
                  throw new NTLMEngineException("NTLM message expected - instead got unrecognized bytes");
               }
            }

            int var4 = this.readULong(NTLMEngineImpl.SIGNATURE.length);
            if (var4 != var2) {
               throw new NTLMEngineException("NTLM type " + Integer.toString(var2) + " message expected - instead got type " + Integer.toString(var4));
            } else {
               this.currentOutputPosition = this.messageContents.length;
            }
         }
      }

      protected int getPreambleLength() {
         return NTLMEngineImpl.SIGNATURE.length + 4;
      }

      protected int getMessageLength() {
         return this.currentOutputPosition;
      }

      protected byte readByte(int var1) throws NTLMEngineException {
         if (this.messageContents.length < var1 + 1) {
            throw new NTLMEngineException("NTLM: Message too short");
         } else {
            return this.messageContents[var1];
         }
      }

      protected void readBytes(byte[] var1, int var2) throws NTLMEngineException {
         if (this.messageContents.length < var2 + var1.length) {
            throw new NTLMEngineException("NTLM: Message too short");
         } else {
            System.arraycopy(this.messageContents, var2, var1, 0, var1.length);
         }
      }

      protected int readUShort(int var1) throws NTLMEngineException {
         return NTLMEngineImpl.readUShort(this.messageContents, var1);
      }

      protected int readULong(int var1) throws NTLMEngineException {
         return NTLMEngineImpl.readULong(this.messageContents, var1);
      }

      protected byte[] readSecurityBuffer(int var1) throws NTLMEngineException {
         return NTLMEngineImpl.readSecurityBuffer(this.messageContents, var1);
      }

      protected void prepareResponse(int var1, int var2) {
         this.messageContents = new byte[var1];
         this.currentOutputPosition = 0;
         this.addBytes(NTLMEngineImpl.SIGNATURE);
         this.addULong(var2);
      }

      protected void addByte(byte var1) {
         this.messageContents[this.currentOutputPosition] = var1;
         this.currentOutputPosition++;
      }

      protected void addBytes(byte[] var1) {
         if (var1 != null) {
            for (byte var5 : var1) {
               this.messageContents[this.currentOutputPosition] = var5;
               this.currentOutputPosition++;
            }
         }
      }

      protected void addUShort(int var1) {
         this.addByte((byte)(var1 & 0xFF));
         this.addByte((byte)(var1 >> 8 & 0xFF));
      }

      protected void addULong(int var1) {
         this.addByte((byte)(var1 & 0xFF));
         this.addByte((byte)(var1 >> 8 & 0xFF));
         this.addByte((byte)(var1 >> 16 & 0xFF));
         this.addByte((byte)(var1 >> 24 & 0xFF));
      }

      public String getResponse() {
         return new String(Base64.encodeBase64(this.getBytes()), Consts.ASCII);
      }

      public byte[] getBytes() {
         if (this.messageContents == null) {
            this.buildMessage();
         }

         if (this.messageContents.length > this.currentOutputPosition) {
            byte[] var2 = new byte[this.currentOutputPosition];
            System.arraycopy(this.messageContents, 0, var2, 0, this.currentOutputPosition);
            this.messageContents = var2;
         }

         return this.messageContents;
      }

      protected void buildMessage() {
         throw new RuntimeException("Message builder not implemented for " + this.getClass().getName());
      }
   }

   static class Type1Message extends NTLMEngineImpl.NTLMMessage {
      private final byte[] hostBytes;
      private final byte[] domainBytes;
      private final int flags;

      Type1Message(String var1, String var2) throws NTLMEngineException {
         this(var1, var2, null);
      }

      Type1Message(String var1, String var2, Integer var3) throws NTLMEngineException {
         this.flags = var3 == null ? this.getDefaultFlags() : var3;
         String var4 = NTLMEngineImpl.convertHost(var2);
         String var5 = NTLMEngineImpl.convertDomain(var1);
         this.hostBytes = var4 != null ? var4.getBytes(NTLMEngineImpl.UNICODE_LITTLE_UNMARKED) : null;
         this.domainBytes = var5 != null ? var5.toUpperCase(Locale.ROOT).getBytes(NTLMEngineImpl.UNICODE_LITTLE_UNMARKED) : null;
      }

      Type1Message() {
         this.hostBytes = null;
         this.domainBytes = null;
         this.flags = this.getDefaultFlags();
      }

      private int getDefaultFlags() {
         return -1576500735;
      }

      @Override
      protected void buildMessage() {
         int var1 = 0;
         if (this.domainBytes != null) {
            var1 = this.domainBytes.length;
         }

         int var2 = 0;
         if (this.hostBytes != null) {
            var2 = this.hostBytes.length;
         }

         int var3 = 40 + var2 + var1;
         this.prepareResponse(var3, 1);
         this.addULong(this.flags);
         this.addUShort(var1);
         this.addUShort(var1);
         this.addULong(var2 + 32 + 8);
         this.addUShort(var2);
         this.addUShort(var2);
         this.addULong(40);
         this.addUShort(261);
         this.addULong(2600);
         this.addUShort(3840);
         if (this.hostBytes != null) {
            this.addBytes(this.hostBytes);
         }

         if (this.domainBytes != null) {
            this.addBytes(this.domainBytes);
         }
      }
   }

   static class Type2Message extends NTLMEngineImpl.NTLMMessage {
      protected final byte[] challenge = new byte[8];
      protected String target;
      protected byte[] targetInfo;
      protected final int flags;

      Type2Message(String var1) throws NTLMEngineException {
         this(Base64.decodeBase64(var1.getBytes(NTLMEngineImpl.DEFAULT_CHARSET)));
      }

      Type2Message(byte[] var1) throws NTLMEngineException {
         super(var1, 2);
         this.readBytes(this.challenge, 24);
         this.flags = this.readULong(20);
         this.target = null;
         if (this.getMessageLength() >= 20) {
            byte[] var2 = this.readSecurityBuffer(12);
            if (var2.length != 0) {
               this.target = new String(var2, NTLMEngineImpl.getCharset(this.flags));
            }
         }

         this.targetInfo = null;
         if (this.getMessageLength() >= 48) {
            byte[] var3 = this.readSecurityBuffer(40);
            if (var3.length != 0) {
               this.targetInfo = var3;
            }
         }
      }

      byte[] getChallenge() {
         return this.challenge;
      }

      String getTarget() {
         return this.target;
      }

      byte[] getTargetInfo() {
         return this.targetInfo;
      }

      int getFlags() {
         return this.flags;
      }
   }

   static class Type3Message extends NTLMEngineImpl.NTLMMessage {
      protected final byte[] type1Message;
      protected final byte[] type2Message;
      protected final int type2Flags;
      protected final byte[] domainBytes;
      protected final byte[] hostBytes;
      protected final byte[] userBytes;
      protected byte[] lmResp;
      protected byte[] ntResp;
      protected final byte[] sessionKey;
      protected final byte[] exportedSessionKey;
      protected final boolean computeMic;

      Type3Message(String var1, String var2, String var3, String var4, byte[] var5, int var6, String var7, byte[] var8) throws NTLMEngineException {
         this(var1, var2, var3, var4, var5, var6, var7, var8, null, null, null);
      }

      Type3Message(Random var1, long var2, String var4, String var5, String var6, String var7, byte[] var8, int var9, String var10, byte[] var11) throws NTLMEngineException {
         this(var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, null, null, null);
      }

      Type3Message(
         String var1, String var2, String var3, String var4, byte[] var5, int var6, String var7, byte[] var8, Certificate var9, byte[] var10, byte[] var11
      ) throws NTLMEngineException {
         this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }

      Type3Message(
         Random var1,
         long var2,
         String var4,
         String var5,
         String var6,
         String var7,
         byte[] var8,
         int var9,
         String var10,
         byte[] var11,
         Certificate var12,
         byte[] var13,
         byte[] var14
      ) throws NTLMEngineException {
         if (var1 == null) {
            throw new NTLMEngineException("Random generator not available");
         } else {
            this.type2Flags = var9;
            this.type1Message = var13;
            this.type2Message = var14;
            String var15 = NTLMEngineImpl.convertHost(var5);
            String var16 = NTLMEngineImpl.convertDomain(var4);
            byte[] var17 = var11;
            if (var12 != null) {
               var17 = this.addGssMicAvsToTargetInfo(var11, var12);
               this.computeMic = true;
            } else {
               this.computeMic = false;
            }

            NTLMEngineImpl.CipherGen var18 = new NTLMEngineImpl.CipherGen(var1, var2, var16, var6, var7, var8, var10, var17);

            byte[] var19;
            try {
               if ((var9 & 8388608) != 0 && var11 != null && var10 != null) {
                  this.ntResp = var18.getNTLMv2Response();
                  this.lmResp = var18.getLMv2Response();
                  if ((var9 & 128) != 0) {
                     var19 = var18.getLanManagerSessionKey();
                  } else {
                     var19 = var18.getNTLMv2UserSessionKey();
                  }
               } else if ((var9 & 524288) != 0) {
                  this.ntResp = var18.getNTLM2SessionResponse();
                  this.lmResp = var18.getLM2SessionResponse();
                  if ((var9 & 128) != 0) {
                     var19 = var18.getLanManagerSessionKey();
                  } else {
                     var19 = var18.getNTLM2SessionResponseUserSessionKey();
                  }
               } else {
                  this.ntResp = var18.getNTLMResponse();
                  this.lmResp = var18.getLMResponse();
                  if ((var9 & 128) != 0) {
                     var19 = var18.getLanManagerSessionKey();
                  } else {
                     var19 = var18.getNTLMUserSessionKey();
                  }
               }
            } catch (NTLMEngineException var21) {
               this.ntResp = new byte[0];
               this.lmResp = var18.getLMResponse();
               if ((var9 & 128) != 0) {
                  var19 = var18.getLanManagerSessionKey();
               } else {
                  var19 = var18.getLMUserSessionKey();
               }
            }

            if ((var9 & 16) != 0) {
               if ((var9 & 1073741824) != 0) {
                  this.exportedSessionKey = var18.getSecondaryKey();
                  this.sessionKey = NTLMEngineImpl.RC4(this.exportedSessionKey, var19);
               } else {
                  this.sessionKey = var19;
                  this.exportedSessionKey = this.sessionKey;
               }
            } else {
               if (this.computeMic) {
                  throw new NTLMEngineException("Cannot sign/seal: no exported session key");
               }

               this.sessionKey = null;
               this.exportedSessionKey = null;
            }

            Charset var20 = NTLMEngineImpl.getCharset(var9);
            this.hostBytes = var15 != null ? var15.getBytes(var20) : null;
            this.domainBytes = var16 != null ? var16.toUpperCase(Locale.ROOT).getBytes(var20) : null;
            this.userBytes = var6.getBytes(var20);
         }
      }

      public byte[] getEncryptedRandomSessionKey() {
         return this.sessionKey;
      }

      public byte[] getExportedSessionKey() {
         return this.exportedSessionKey;
      }

      @Override
      protected void buildMessage() {
         int var1 = this.ntResp.length;
         int var2 = this.lmResp.length;
         int var3 = this.domainBytes != null ? this.domainBytes.length : 0;
         int var4 = this.hostBytes != null ? this.hostBytes.length : 0;
         int var5 = this.userBytes.length;
         int var6;
         if (this.sessionKey != null) {
            var6 = this.sessionKey.length;
         } else {
            var6 = 0;
         }

         int var7 = 72 + (this.computeMic ? 16 : 0);
         int var8 = var7 + var2;
         int var9 = var8 + var1;
         int var10 = var9 + var3;
         int var11 = var10 + var5;
         int var12 = var11 + var4;
         int var13 = var12 + var6;
         this.prepareResponse(var13, 3);
         this.addUShort(var2);
         this.addUShort(var2);
         this.addULong(var7);
         this.addUShort(var1);
         this.addUShort(var1);
         this.addULong(var8);
         this.addUShort(var3);
         this.addUShort(var3);
         this.addULong(var9);
         this.addUShort(var5);
         this.addUShort(var5);
         this.addULong(var10);
         this.addUShort(var4);
         this.addUShort(var4);
         this.addULong(var11);
         this.addUShort(var6);
         this.addUShort(var6);
         this.addULong(var12);
         this.addULong(this.type2Flags);
         this.addUShort(261);
         this.addULong(2600);
         this.addUShort(3840);
         int var14 = -1;
         if (this.computeMic) {
            var14 = this.currentOutputPosition;
            this.currentOutputPosition += 16;
         }

         this.addBytes(this.lmResp);
         this.addBytes(this.ntResp);
         this.addBytes(this.domainBytes);
         this.addBytes(this.userBytes);
         this.addBytes(this.hostBytes);
         if (this.sessionKey != null) {
            this.addBytes(this.sessionKey);
         }

         if (this.computeMic) {
            NTLMEngineImpl.HMACMD5 var15 = new NTLMEngineImpl.HMACMD5(this.exportedSessionKey);
            var15.update(this.type1Message);
            var15.update(this.type2Message);
            var15.update(this.messageContents);
            byte[] var16 = var15.getOutput();
            System.arraycopy(var16, 0, this.messageContents, var14, var16.length);
         }
      }

      private byte[] addGssMicAvsToTargetInfo(byte[] var1, Certificate var2) throws NTLMEngineException {
         byte[] var3 = new byte[var1.length + 8 + 20];
         int var4 = var1.length - 4;
         System.arraycopy(var1, 0, var3, 0, var4);
         NTLMEngineImpl.writeUShort(var3, 6, var4);
         NTLMEngineImpl.writeUShort(var3, 4, var4 + 2);
         NTLMEngineImpl.writeULong(var3, 2, var4 + 4);
         NTLMEngineImpl.writeUShort(var3, 10, var4 + 8);
         NTLMEngineImpl.writeUShort(var3, 16, var4 + 10);

         byte[] var5;
         try {
            byte[] var6 = var2.getEncoded();
            MessageDigest var7 = MessageDigest.getInstance("SHA-256");
            byte[] var8 = var7.digest(var6);
            byte[] var9 = new byte[20 + NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length + var8.length];
            NTLMEngineImpl.writeULong(var9, 53, 16);
            System.arraycopy(NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT, 0, var9, 20, NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length);
            System.arraycopy(var8, 0, var9, 20 + NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length, var8.length);
            MessageDigest var10 = NTLMEngineImpl.getMD5();
            var5 = var10.digest(var9);
         } catch (CertificateEncodingException var11) {
            throw new NTLMEngineException(var11.getMessage(), var11);
         } catch (NoSuchAlgorithmException var12) {
            throw new NTLMEngineException(var12.getMessage(), var12);
         }

         System.arraycopy(var5, 0, var3, var4 + 12, 16);
         return var3;
      }
   }
}
