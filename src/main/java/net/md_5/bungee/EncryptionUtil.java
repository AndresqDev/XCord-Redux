package net.md_5.bungee;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.Longs;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.Base64.Encoder;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.JavaCipher;
import net.md_5.bungee.jni.cipher.NativeCipher;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;

public class EncryptionUtil {
   private static final Random random = new Random();
   private static final Encoder MIME_ENCODER = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
   public static final KeyPair keys;
   private static final SecretKey secret = new SecretKeySpec(new byte[16], "AES");
   public static final NativeCode<BungeeCipher> nativeFactory = new NativeCode<>("native-cipher", JavaCipher::new, NativeCipher::new);
   private static final PublicKey MOJANG_KEY;

   public static EncryptionRequest encryptRequest() {
      String var0 = Long.toString(random.nextLong(), 16);
      byte[] var1 = keys.getPublic().getEncoded();
      byte[] var2 = new byte[4];
      random.nextBytes(var2);
      return new EncryptionRequest(var0, var1, var2);
   }

   public static boolean check(PlayerPublicKey var0, UUID var1) throws GeneralSecurityException {
      Signature var2 = Signature.getInstance("SHA1withRSA");
      var2.initVerify(MOJANG_KEY);
      byte[] var3;
      if (var1 != null) {
         byte[] var4 = getPubkey(var0.getKey()).getEncoded();
         var3 = new byte[24 + var4.length];
         ByteBuffer.wrap(var3)
            .order(ByteOrder.BIG_ENDIAN)
            .putLong(var1.getMostSignificantBits())
            .putLong(var1.getLeastSignificantBits())
            .putLong(var0.getExpiry())
            .put(var4);
      } else {
         var3 = (var0.getExpiry()
               + "-----BEGIN RSA PUBLIC KEY-----\n"
               + MIME_ENCODER.encodeToString(getPubkey(var0.getKey()).getEncoded())
               + "\n-----END RSA PUBLIC KEY-----\n")
            .getBytes(StandardCharsets.US_ASCII);
      }

      var2.update(var3);
      return var2.verify(var0.getSignature());
   }

   public static boolean check(PlayerPublicKey var0, EncryptionResponse var1, EncryptionRequest var2) throws GeneralSecurityException {
      if (var0 != null) {
         Signature var5 = Signature.getInstance("SHA256withRSA");
         var5.initVerify(getPubkey(var0.getKey()));
         var5.update(var2.getVerifyToken());
         var5.update(Longs.toByteArray(var1.getEncryptionData().getSalt()));
         return var5.verify(var1.getEncryptionData().getSignature());
      } else {
         Cipher var3 = Cipher.getInstance("RSA");
         var3.init(2, keys.getPrivate());
         byte[] var4 = var3.doFinal(var1.getVerifyToken());
         return Arrays.equals(var2.getVerifyToken(), var4);
      }
   }

   public static SecretKey getSecret(EncryptionResponse var0, EncryptionRequest var1) throws GeneralSecurityException {
      Cipher var2 = Cipher.getInstance("RSA");
      var2.init(2, keys.getPrivate());
      return new SecretKeySpec(var2.doFinal(var0.getSharedSecret()), "AES");
   }

   public static BungeeCipher getCipher(boolean var0, SecretKey var1) throws GeneralSecurityException {
      BungeeCipher var2 = nativeFactory.newInstance();
      var2.init(var0, var1);
      return var2;
   }

   public static PublicKey getPubkey(EncryptionRequest var0) throws GeneralSecurityException {
      return getPubkey(var0.getPublicKey());
   }

   private static PublicKey getPubkey(byte[] var0) throws GeneralSecurityException {
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(var0));
   }

   public static byte[] encrypt(Key var0, byte[] var1) throws GeneralSecurityException {
      Cipher var2 = Cipher.getInstance("RSA");
      var2.init(1, var0);
      return var2.doFinal(var1);
   }

   public static SecretKey getSecret() {
      return secret;
   }

   static {
      try {
         KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
         var0.initialize(1024);
         keys = var0.generateKeyPair();
      } catch (NoSuchAlgorithmException var2) {
         throw new ExceptionInInitializerError(var2);
      }

      try {
         MOJANG_KEY = KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(ByteStreams.toByteArray(EncryptionUtil.class.getResourceAsStream("/yggdrasil_session_pubkey.der"))));
      } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
