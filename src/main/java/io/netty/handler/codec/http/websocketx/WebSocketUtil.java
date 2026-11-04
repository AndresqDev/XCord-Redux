package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

final class WebSocketUtil {
   private static final FastThreadLocal<MessageDigest> MD5 = new FastThreadLocal<MessageDigest>() {
      protected MessageDigest initialValue() throws Exception {
         try {
            return MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException var2) {
            throw new InternalError("MD5 not supported on this platform - Outdated?");
         }
      }
   };
   private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>() {
      protected MessageDigest initialValue() throws Exception {
         try {
            return MessageDigest.getInstance("SHA1");
         } catch (NoSuchAlgorithmException var2) {
            throw new InternalError("SHA-1 not supported on this platform - Outdated?");
         }
      }
   };

   static byte[] md5(byte[] var0) {
      return digest(MD5, var0);
   }

   static byte[] sha1(byte[] var0) {
      return digest(SHA1, var0);
   }

   private static byte[] digest(FastThreadLocal<MessageDigest> var0, byte[] var1) {
      MessageDigest var2 = (MessageDigest)var0.get();
      var2.reset();
      return var2.digest(var1);
   }

   @SuppressJava6Requirement(
      reason = "Guarded with java version check"
   )
   static String base64(byte[] var0) {
      if (PlatformDependent.javaVersion() >= 8) {
         return Base64.getEncoder().encodeToString(var0);
      } else {
         ByteBuf var2 = Unpooled.wrappedBuffer(var0);

         String var1;
         try {
            ByteBuf var3 = io.netty.handler.codec.base64.Base64.encode(var2);

            try {
               var1 = var3.toString(CharsetUtil.UTF_8);
            } finally {
               var3.release();
            }
         } finally {
            var2.release();
         }

         return var1;
      }
   }

   static byte[] randomBytes(int var0) {
      byte[] var1 = new byte[var0];
      PlatformDependent.threadLocalRandom().nextBytes(var1);
      return var1;
   }

   static int randomNumber(int var0, int var1) {
      assert var0 < var1;

      double var2 = PlatformDependent.threadLocalRandom().nextDouble();
      return (int)((double)var0 + var2 * (double)(var1 - var0));
   }

   private WebSocketUtil() {
   }
}
