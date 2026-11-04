package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;

final class SslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslUtils.class);
   static final Set<String> TLSV13_CIPHERS = Collections.unmodifiableSet(
      new LinkedHashSet<>(
         Arrays.asList("TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "TLS_AES_128_GCM_SHA256", "TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_SHA256")
      )
   );
   static final int GMSSL_PROTOCOL_VERSION = 257;
   static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
   static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
   static final int SSL_CONTENT_TYPE_ALERT = 21;
   static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
   static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
   static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
   static final int SSL_RECORD_HEADER_LENGTH = 5;
   static final int NOT_ENOUGH_DATA = -1;
   static final int NOT_ENCRYPTED = -2;
   static final String[] DEFAULT_CIPHER_SUITES;
   static final String[] DEFAULT_TLSV13_CIPHER_SUITES;
   static final String[] TLSV13_CIPHER_SUITES = new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384"};
   private static final boolean TLSV1_3_JDK_SUPPORTED = isTLSv13SupportedByJDK0(null);
   private static final boolean TLSV1_3_JDK_DEFAULT_ENABLED = isTLSv13EnabledByJDK0(null);

   static boolean isTLSv13SupportedByJDK(Provider var0) {
      return var0 == null ? TLSV1_3_JDK_SUPPORTED : isTLSv13SupportedByJDK0(var0);
   }

   private static boolean isTLSv13SupportedByJDK0(Provider var0) {
      try {
         return arrayContains(newInitContext(var0).getSupportedSSLParameters().getProtocols(), "TLSv1.3");
      } catch (Throwable var2) {
         logger.debug("Unable to detect if JDK SSLEngine with provider {} supports TLSv1.3, assuming no", var0, var2);
         return false;
      }
   }

   static boolean isTLSv13EnabledByJDK(Provider var0) {
      return var0 == null ? TLSV1_3_JDK_DEFAULT_ENABLED : isTLSv13EnabledByJDK0(var0);
   }

   private static boolean isTLSv13EnabledByJDK0(Provider var0) {
      try {
         return arrayContains(newInitContext(var0).getDefaultSSLParameters().getProtocols(), "TLSv1.3");
      } catch (Throwable var2) {
         logger.debug("Unable to detect if JDK SSLEngine with provider {} enables TLSv1.3 by default, assuming no", var0, var2);
         return false;
      }
   }

   private static SSLContext newInitContext(Provider var0) throws NoSuchAlgorithmException, KeyManagementException {
      SSLContext var1;
      if (var0 == null) {
         var1 = SSLContext.getInstance("TLS");
      } else {
         var1 = SSLContext.getInstance("TLS", var0);
      }

      var1.init(null, new TrustManager[0], null);
      return var1;
   }

   static SSLContext getSSLContext(String var0) throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
      SSLContext var1;
      if (StringUtil.isNullOrEmpty(var0)) {
         var1 = SSLContext.getInstance(getTlsVersion());
      } else {
         var1 = SSLContext.getInstance(getTlsVersion(), var0);
      }

      var1.init(null, new TrustManager[0], null);
      return var1;
   }

   private static String getTlsVersion() {
      return TLSV1_3_JDK_SUPPORTED ? "TLSv1.3" : "TLSv1.2";
   }

   static boolean arrayContains(String[] var0, String var1) {
      for (String var5 : var0) {
         if (var1.equals(var5)) {
            return true;
         }
      }

      return false;
   }

   static void addIfSupported(Set<String> var0, List<String> var1, String... var2) {
      for (String var6 : var2) {
         if (var0.contains(var6)) {
            var1.add(var6);
         }
      }
   }

   static void useFallbackCiphersIfDefaultIsEmpty(List<String> var0, Iterable<String> var1) {
      if (var0.isEmpty()) {
         for (String var3 : var1) {
            if (!var3.startsWith("SSL_") && !var3.contains("_RC4_")) {
               var0.add(var3);
            }
         }
      }
   }

   static void useFallbackCiphersIfDefaultIsEmpty(List<String> var0, String... var1) {
      useFallbackCiphersIfDefaultIsEmpty(var0, Arrays.asList(var1));
   }

   static SSLHandshakeException toSSLHandshakeException(Throwable var0) {
      return var0 instanceof SSLHandshakeException
         ? (SSLHandshakeException)var0
         : (SSLHandshakeException)new SSLHandshakeException(var0.getMessage()).initCause(var0);
   }

   static int getEncryptedPacketLength(ByteBuf var0, int var1) {
      int var2 = 0;
      boolean var3;
      switch (var0.getUnsignedByte(var1)) {
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
            var3 = true;
            break;
         default:
            var3 = false;
      }

      if (var3) {
         short var4 = var0.getUnsignedByte(var1 + 1);
         if (var4 != 3 && var0.getShort(var1 + 1) != 257) {
            var3 = false;
         } else {
            var2 = unsignedShortBE(var0, var1 + 3) + 5;
            if (var2 <= 5) {
               var3 = false;
            }
         }
      }

      if (!var3) {
         int var6 = (var0.getUnsignedByte(var1) & 128) != 0 ? 2 : 3;
         short var5 = var0.getUnsignedByte(var1 + var6 + 1);
         if (var5 != 2 && var5 != 3) {
            return -2;
         }

         var2 = var6 == 2 ? (shortBE(var0, var1) & 32767) + 2 : (shortBE(var0, var1) & 16383) + 3;
         if (var2 <= var6) {
            return -1;
         }
      }

      return var2;
   }

   private static int unsignedShortBE(ByteBuf var0, int var1) {
      int var2 = var0.getUnsignedShort(var1);
      if (var0.order() == ByteOrder.LITTLE_ENDIAN) {
         var2 = Integer.reverseBytes(var2) >>> 16;
      }

      return var2;
   }

   private static short shortBE(ByteBuf var0, int var1) {
      short var2 = var0.getShort(var1);
      if (var0.order() == ByteOrder.LITTLE_ENDIAN) {
         var2 = Short.reverseBytes(var2);
      }

      return var2;
   }

   private static short unsignedByte(byte var0) {
      return (short)(var0 & 255);
   }

   private static int unsignedShortBE(ByteBuffer var0, int var1) {
      return shortBE(var0, var1) & 65535;
   }

   private static short shortBE(ByteBuffer var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.getShort(var1) : ByteBufUtil.swapShort(var0.getShort(var1));
   }

   static int getEncryptedPacketLength(ByteBuffer[] var0, int var1) {
      ByteBuffer var2 = var0[var1];
      if (var2.remaining() >= 5) {
         return getEncryptedPacketLength(var2);
      } else {
         ByteBuffer var3 = ByteBuffer.allocate(5);

         do {
            var2 = var0[var1++].duplicate();
            if (var2.remaining() > var3.remaining()) {
               ((Buffer)var2).limit(var2.position() + var3.remaining());
            }

            var3.put(var2);
         } while (var3.hasRemaining());

         ((Buffer)var3).flip();
         return getEncryptedPacketLength(var3);
      }
   }

   private static int getEncryptedPacketLength(ByteBuffer var0) {
      int var1 = 0;
      int var2 = var0.position();
      boolean var3;
      switch (unsignedByte(var0.get(var2))) {
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
            var3 = true;
            break;
         default:
            var3 = false;
      }

      if (var3) {
         short var4 = unsignedByte(var0.get(var2 + 1));
         if (var4 != 3 && var0.getShort(var2 + 1) != 257) {
            var3 = false;
         } else {
            var1 = unsignedShortBE(var0, var2 + 3) + 5;
            if (var1 <= 5) {
               var3 = false;
            }
         }
      }

      if (!var3) {
         int var6 = (unsignedByte(var0.get(var2)) & 128) != 0 ? 2 : 3;
         short var5 = unsignedByte(var0.get(var2 + var6 + 1));
         if (var5 != 2 && var5 != 3) {
            return -2;
         }

         var1 = var6 == 2 ? (shortBE(var0, var2) & 32767) + 2 : (shortBE(var0, var2) & 16383) + 3;
         if (var1 <= var6) {
            return -1;
         }
      }

      return var1;
   }

   static void handleHandshakeFailure(ChannelHandlerContext var0, Throwable var1, boolean var2) {
      var0.flush();
      if (var2) {
         var0.fireUserEventTriggered(new SslHandshakeCompletionEvent(var1));
      }

      var0.close();
   }

   static void zeroout(ByteBuf var0) {
      if (!var0.isReadOnly()) {
         var0.setZero(0, var0.capacity());
      }
   }

   static void zerooutAndRelease(ByteBuf var0) {
      zeroout(var0);
      var0.release();
   }

   static ByteBuf toBase64(ByteBufAllocator var0, ByteBuf var1) {
      ByteBuf var2 = Base64.encode(var1, var1.readerIndex(), var1.readableBytes(), true, Base64Dialect.STANDARD, var0);
      var1.readerIndex(var1.writerIndex());
      return var2;
   }

   static boolean isValidHostNameForSNI(String var0) {
      return var0 != null
         && var0.indexOf(46) > 0
         && !var0.endsWith(".")
         && !var0.startsWith("/")
         && !NetUtil.isValidIpV4Address(var0)
         && !NetUtil.isValidIpV6Address(var0);
   }

   static boolean isTLSv13Cipher(String var0) {
      return TLSV13_CIPHERS.contains(var0);
   }

   private SslUtils() {
   }

   static {
      if (TLSV1_3_JDK_SUPPORTED) {
         DEFAULT_TLSV13_CIPHER_SUITES = TLSV13_CIPHER_SUITES;
      } else {
         DEFAULT_TLSV13_CIPHER_SUITES = EmptyArrays.EMPTY_STRINGS;
      }

      LinkedHashSet var0 = new LinkedHashSet();
      var0.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
      var0.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
      var0.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
      var0.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
      var0.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
      var0.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
      var0.add("TLS_RSA_WITH_AES_128_GCM_SHA256");
      var0.add("TLS_RSA_WITH_AES_128_CBC_SHA");
      var0.add("TLS_RSA_WITH_AES_256_CBC_SHA");
      Collections.addAll(var0, DEFAULT_TLSV13_CIPHER_SUITES);
      DEFAULT_CIPHER_SUITES = var0.toArray(EmptyArrays.EMPTY_STRINGS);
   }
}
