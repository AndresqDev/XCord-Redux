package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.util.LazyX509Certificate;
import io.netty.internal.tcnative.AsyncSSLPrivateKeyMethod;
import io.netty.internal.tcnative.CertificateCompressionAlgo;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.ResultCallback;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SSLPrivateKeyMethod;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertPathValidatorException.Reason;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public abstract class ReferenceCountedOpenSslContext extends SslContext implements ReferenceCounted {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
   private static final int DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE = Math.max(
      1, SystemPropertyUtil.getInt("io.netty.handler.ssl.openssl.bioNonApplicationBufferSize", 2048)
   );
   static final boolean USE_TASKS = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useTasks", true);
   private static final Integer DH_KEY_LENGTH;
   private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
   protected static final int VERIFY_DEPTH = 10;
   static final boolean CLIENT_ENABLE_SESSION_TICKET = SystemPropertyUtil.getBoolean("jdk.tls.client.enableSessionTicketExtension", false);
   static final boolean CLIENT_ENABLE_SESSION_TICKET_TLSV13 = SystemPropertyUtil.getBoolean("jdk.tls.client.enableSessionTicketExtension", true);
   static final boolean SERVER_ENABLE_SESSION_TICKET = SystemPropertyUtil.getBoolean("jdk.tls.server.enableSessionTicketExtension", false);
   static final boolean SERVER_ENABLE_SESSION_TICKET_TLSV13 = SystemPropertyUtil.getBoolean("jdk.tls.server.enableSessionTicketExtension", true);
   static final boolean SERVER_ENABLE_SESSION_CACHE = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.sessionCacheServer", true);
   static final boolean CLIENT_ENABLE_SESSION_CACHE = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.sessionCacheClient", false);
   protected long ctx;
   private final List<String> unmodifiableCiphers;
   private final OpenSslApplicationProtocolNegotiator apn;
   private final int mode;
   private final ResourceLeakTracker<ReferenceCountedOpenSslContext> leak;
   private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted() {
      @Override
      public ReferenceCounted touch(Object var1) {
         if (ReferenceCountedOpenSslContext.this.leak != null) {
            ReferenceCountedOpenSslContext.this.leak.record(var1);
         }

         return ReferenceCountedOpenSslContext.this;
      }

      @Override
      protected void deallocate() {
         ReferenceCountedOpenSslContext.this.destroy();
         if (ReferenceCountedOpenSslContext.this.leak != null) {
            boolean var1 = ReferenceCountedOpenSslContext.this.leak.close(ReferenceCountedOpenSslContext.this);

            assert var1;
         }
      }
   };
   final Certificate[] keyCertChain;
   final ClientAuth clientAuth;
   final String[] protocols;
   final boolean enableOcsp;
   final OpenSslEngineMap engineMap = new ReferenceCountedOpenSslContext.DefaultOpenSslEngineMap();
   final ReadWriteLock ctxLock = new ReentrantReadWriteLock();
   private volatile int bioNonApplicationBufferSize = DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
   static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator() {
      @Override
      public ApplicationProtocolConfig.Protocol protocol() {
         return ApplicationProtocolConfig.Protocol.NONE;
      }

      @Override
      public List<String> protocols() {
         return Collections.emptyList();
      }

      @Override
      public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
         return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
      }

      @Override
      public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
         return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
      }
   };
   final boolean tlsFalseStart;

   ReferenceCountedOpenSslContext(
      Iterable<String> var1,
      CipherSuiteFilter var2,
      OpenSslApplicationProtocolNegotiator var3,
      int var4,
      Certificate[] var5,
      ClientAuth var6,
      String[] var7,
      boolean var8,
      boolean var9,
      boolean var10,
      Entry<SslContextOption<?>, Object>... var11
   ) throws SSLException {
      super(var8);
      OpenSsl.ensureAvailability();
      if (var9 && !OpenSsl.isOcspSupported()) {
         throw new IllegalStateException("OCSP is not supported.");
      } else if (var4 != 1 && var4 != 0) {
         throw new IllegalArgumentException("mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
      } else {
         boolean var12 = false;
         boolean var13 = USE_TASKS;
         OpenSslPrivateKeyMethod var14 = null;
         OpenSslAsyncPrivateKeyMethod var15 = null;
         OpenSslCertificateCompressionConfig var16 = null;
         if (var11 != null) {
            for (Entry var20 : var11) {
               SslContextOption var21 = (SslContextOption)var20.getKey();
               if (var21 == OpenSslContextOption.TLS_FALSE_START) {
                  var12 = (Boolean)var20.getValue();
               } else if (var21 == OpenSslContextOption.USE_TASKS) {
                  var13 = (Boolean)var20.getValue();
               } else if (var21 == OpenSslContextOption.PRIVATE_KEY_METHOD) {
                  var14 = (OpenSslPrivateKeyMethod)var20.getValue();
               } else if (var21 == OpenSslContextOption.ASYNC_PRIVATE_KEY_METHOD) {
                  var15 = (OpenSslAsyncPrivateKeyMethod)var20.getValue();
               } else if (var21 == OpenSslContextOption.CERTIFICATE_COMPRESSION_ALGORITHMS) {
                  var16 = (OpenSslCertificateCompressionConfig)var20.getValue();
               } else {
                  logger.debug("Skipping unsupported " + SslContextOption.class.getSimpleName() + ": " + var20.getKey());
               }
            }
         }

         if (var14 != null && var15 != null) {
            throw new IllegalArgumentException(
               "You can either only use " + OpenSslAsyncPrivateKeyMethod.class.getSimpleName() + " or " + OpenSslPrivateKeyMethod.class.getSimpleName()
            );
         } else {
            this.tlsFalseStart = var12;
            this.leak = var10 ? leakDetector.track(this) : null;
            this.mode = var4;
            this.clientAuth = this.isServer() ? ObjectUtil.checkNotNull(var6, "clientAuth") : ClientAuth.NONE;
            this.protocols = var7;
            this.enableOcsp = var9;
            this.keyCertChain = var5 == null ? null : (Certificate[])var5.clone();
            String[] var37 = ObjectUtil.checkNotNull(var2, "cipherFilter")
               .filterCipherSuites(var1, OpenSsl.DEFAULT_CIPHERS, OpenSsl.availableJavaCipherSuites());
            LinkedHashSet var38 = new LinkedHashSet(var37.length);
            Collections.addAll(var38, var37);
            this.unmodifiableCiphers = new ArrayList<>(var38);
            this.apn = ObjectUtil.checkNotNull(var3, "apn");
            boolean var39 = false;

            try {
               boolean var40 = OpenSsl.isTlsv13Supported();

               try {
                  byte var41 = 30;
                  if (var40) {
                     var41 |= 32;
                  }

                  this.ctx = SSLContext.make(var41, var4);
               } catch (Exception var35) {
                  throw new SSLException("failed to create an SSL_CTX", var35);
               }

               StringBuilder var42 = new StringBuilder();
               StringBuilder var22 = new StringBuilder();

               try {
                  if (this.unmodifiableCiphers.isEmpty()) {
                     SSLContext.setCipherSuite(this.ctx, "", false);
                     if (var40) {
                        SSLContext.setCipherSuite(this.ctx, "", true);
                     }
                  } else {
                     CipherSuiteConverter.convertToCipherStrings(this.unmodifiableCiphers, var42, var22, OpenSsl.isBoringSSL());
                     SSLContext.setCipherSuite(this.ctx, var42.toString(), false);
                     if (var40) {
                        SSLContext.setCipherSuite(this.ctx, OpenSsl.checkTls13Ciphers(logger, var22.toString()), true);
                     }
                  }
               } catch (SSLException var33) {
                  throw var33;
               } catch (Exception var34) {
                  throw new SSLException("failed to set cipher suite: " + this.unmodifiableCiphers, var34);
               }

               int var23 = SSLContext.getOptions(this.ctx)
                  | SSL.SSL_OP_NO_SSLv2
                  | SSL.SSL_OP_NO_SSLv3
                  | SSL.SSL_OP_NO_TLSv1
                  | SSL.SSL_OP_NO_TLSv1_1
                  | SSL.SSL_OP_CIPHER_SERVER_PREFERENCE
                  | SSL.SSL_OP_NO_COMPRESSION
                  | SSL.SSL_OP_NO_TICKET;
               if (var42.length() == 0) {
                  var23 |= SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2;
               }

               SSLContext.setOptions(this.ctx, var23);
               SSLContext.setMode(this.ctx, SSLContext.getMode(this.ctx) | SSL.SSL_MODE_ACCEPT_MOVING_WRITE_BUFFER);
               if (DH_KEY_LENGTH != null) {
                  SSLContext.setTmpDHLength(this.ctx, DH_KEY_LENGTH);
               }

               List var24 = var3.protocols();
               if (!var24.isEmpty()) {
                  String[] var25 = var24.toArray(new String[0]);
                  int var26 = opensslSelectorFailureBehavior(var3.selectorFailureBehavior());
                  switch (var3.protocol()) {
                     case NPN:
                        SSLContext.setNpnProtos(this.ctx, var25, var26);
                        break;
                     case ALPN:
                        SSLContext.setAlpnProtos(this.ctx, var25, var26);
                        break;
                     case NPN_AND_ALPN:
                        SSLContext.setNpnProtos(this.ctx, var25, var26);
                        SSLContext.setAlpnProtos(this.ctx, var25, var26);
                        break;
                     default:
                        throw new Error();
                  }
               }

               if (var9) {
                  SSLContext.enableOcsp(this.ctx, this.isClient());
               }

               SSLContext.setUseTasks(this.ctx, var13);
               if (var14 != null) {
                  SSLContext.setPrivateKeyMethod(this.ctx, new ReferenceCountedOpenSslContext.PrivateKeyMethod(this.engineMap, var14));
               }

               if (var15 != null) {
                  SSLContext.setPrivateKeyMethod(this.ctx, new ReferenceCountedOpenSslContext.AsyncPrivateKeyMethod(this.engineMap, var15));
               }

               if (var16 != null) {
                  for (OpenSslCertificateCompressionConfig.AlgorithmConfig var44 : var16) {
                     ReferenceCountedOpenSslContext.CompressionAlgorithm var27 = new ReferenceCountedOpenSslContext.CompressionAlgorithm(
                        this.engineMap, var44.algorithm()
                     );
                     switch (var44.mode()) {
                        case Decompress:
                           SSLContext.addCertificateCompressionAlgorithm(this.ctx, SSL.SSL_CERT_COMPRESSION_DIRECTION_DECOMPRESS, var27);
                           break;
                        case Compress:
                           SSLContext.addCertificateCompressionAlgorithm(this.ctx, SSL.SSL_CERT_COMPRESSION_DIRECTION_COMPRESS, var27);
                           break;
                        case Both:
                           SSLContext.addCertificateCompressionAlgorithm(this.ctx, SSL.SSL_CERT_COMPRESSION_DIRECTION_BOTH, var27);
                           break;
                        default:
                           throw new IllegalStateException();
                     }
                  }
               }

               SSLContext.setCurvesList(this.ctx, OpenSsl.NAMED_GROUPS);
               var39 = true;
            } finally {
               if (!var39) {
                  this.release();
               }
            }
         }
      }
   }

   private static int opensslSelectorFailureBehavior(ApplicationProtocolConfig.SelectorFailureBehavior var0) {
      switch (var0) {
         case NO_ADVERTISE:
            return 0;
         case CHOOSE_MY_LAST_PROTOCOL:
            return 1;
         default:
            throw new Error();
      }
   }

   @Override
   public final List<String> cipherSuites() {
      return this.unmodifiableCiphers;
   }

   @Override
   public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.apn;
   }

   @Override
   public final boolean isClient() {
      return this.mode == 0;
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      return this.newEngine0(var1, var2, var3, true);
   }

   @Override
   protected final SslHandler newHandler(ByteBufAllocator var1, boolean var2) {
      return new SslHandler(this.newEngine0(var1, null, -1, false), var2);
   }

   @Override
   protected final SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new SslHandler(this.newEngine0(var1, var2, var3, false), var4);
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator var1, boolean var2, Executor var3) {
      return new SslHandler(this.newEngine0(var1, null, -1, false), var2, var3);
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4, Executor var5) {
      return new SslHandler(this.newEngine0(var1, var2, var3, false), var5);
   }

   SSLEngine newEngine0(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new ReferenceCountedOpenSslEngine(this, var1, var2, var3, var4, true);
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1) {
      return this.newEngine(var1, null, -1);
   }

   @Deprecated
   public final long context() {
      return this.sslCtxPointer();
   }

   @Deprecated
   public final OpenSslSessionStats stats() {
      return this.sessionContext().stats();
   }

   @Deprecated
   public void setRejectRemoteInitiatedRenegotiation(boolean var1) {
      if (!var1) {
         throw new UnsupportedOperationException("Renegotiation is not supported");
      }
   }

   @Deprecated
   public boolean getRejectRemoteInitiatedRenegotiation() {
      return true;
   }

   public void setBioNonApplicationBufferSize(int var1) {
      this.bioNonApplicationBufferSize = ObjectUtil.checkPositiveOrZero(var1, "bioNonApplicationBufferSize");
   }

   public int getBioNonApplicationBufferSize() {
      return this.bioNonApplicationBufferSize;
   }

   @Deprecated
   public final void setTicketKeys(byte[] var1) {
      this.sessionContext().setTicketKeys(var1);
   }

   public abstract OpenSslSessionContext sessionContext();

   @Deprecated
   public final long sslCtxPointer() {
      Lock var1 = this.ctxLock.readLock();
      var1.lock();

      long var2;
      try {
         var2 = SSLContext.getSslCtx(this.ctx);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   @Deprecated
   public final void setPrivateKeyMethod(OpenSslPrivateKeyMethod var1) {
      ObjectUtil.checkNotNull(var1, "method");
      Lock var2 = this.ctxLock.writeLock();
      var2.lock();

      try {
         SSLContext.setPrivateKeyMethod(this.ctx, new ReferenceCountedOpenSslContext.PrivateKeyMethod(this.engineMap, var1));
      } finally {
         var2.unlock();
      }
   }

   @Deprecated
   public final void setUseTasks(boolean var1) {
      Lock var2 = this.ctxLock.writeLock();
      var2.lock();

      try {
         SSLContext.setUseTasks(this.ctx, var1);
      } finally {
         var2.unlock();
      }
   }

   private void destroy() {
      Lock var1 = this.ctxLock.writeLock();
      var1.lock();

      try {
         if (this.ctx != 0L) {
            if (this.enableOcsp) {
               SSLContext.disableOcsp(this.ctx);
            }

            SSLContext.free(this.ctx);
            this.ctx = 0L;
            OpenSslSessionContext var2 = this.sessionContext();
            if (var2 != null) {
               var2.destroy();
            }
         }
      } finally {
         var1.unlock();
      }
   }

   protected static X509Certificate[] certificates(byte[][] var0) {
      X509Certificate[] var1 = new X509Certificate[var0.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = new LazyX509Certificate(var0[var2]);
      }

      return var1;
   }

   protected static X509TrustManager chooseTrustManager(TrustManager[] var0) {
      for (TrustManager var4 : var0) {
         if (var4 instanceof X509TrustManager) {
            if (PlatformDependent.javaVersion() >= 7) {
               return OpenSslX509TrustManagerWrapper.wrapIfNeeded((X509TrustManager)var4);
            }

            return (X509TrustManager)var4;
         }
      }

      throw new IllegalStateException("no X509TrustManager found");
   }

   protected static X509KeyManager chooseX509KeyManager(KeyManager[] var0) {
      for (KeyManager var4 : var0) {
         if (var4 instanceof X509KeyManager) {
            return (X509KeyManager)var4;
         }
      }

      throw new IllegalStateException("no X509KeyManager found");
   }

   static OpenSslApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig var0) {
      if (var0 == null) {
         return NONE_PROTOCOL_NEGOTIATOR;
      } else {
         switch (var0.protocol()) {
            case NPN:
            case ALPN:
            case NPN_AND_ALPN:
               switch (var0.selectedListenerFailureBehavior()) {
                  case CHOOSE_MY_LAST_PROTOCOL:
                  case ACCEPT:
                     switch (var0.selectorFailureBehavior()) {
                        case NO_ADVERTISE:
                        case CHOOSE_MY_LAST_PROTOCOL:
                           return new OpenSslDefaultApplicationProtocolNegotiator(var0);
                        default:
                           throw new UnsupportedOperationException("OpenSSL provider does not support " + var0.selectorFailureBehavior() + " behavior");
                     }
                  default:
                     throw new UnsupportedOperationException("OpenSSL provider does not support " + var0.selectedListenerFailureBehavior() + " behavior");
               }
            case NONE:
               return NONE_PROTOCOL_NEGOTIATOR;
            default:
               throw new Error();
         }
      }
   }

   @SuppressJava6Requirement(
      reason = "Guarded by java version check"
   )
   static boolean useExtendedTrustManager(X509TrustManager var0) {
      return PlatformDependent.javaVersion() >= 7 && var0 instanceof X509ExtendedTrustManager;
   }

   @Override
   public final int refCnt() {
      return this.refCnt.refCnt();
   }

   @Override
   public final ReferenceCounted retain() {
      this.refCnt.retain();
      return this;
   }

   @Override
   public final ReferenceCounted retain(int var1) {
      this.refCnt.retain(var1);
      return this;
   }

   @Override
   public final ReferenceCounted touch() {
      this.refCnt.touch();
      return this;
   }

   @Override
   public final ReferenceCounted touch(Object var1) {
      this.refCnt.touch(var1);
      return this;
   }

   @Override
   public final boolean release() {
      return this.refCnt.release();
   }

   @Override
   public final boolean release(int var1) {
      return this.refCnt.release(var1);
   }

   static void setKeyMaterial(long var0, X509Certificate[] var2, PrivateKey var3, String var4) throws SSLException {
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;
      PemEncoded var11 = null;

      try {
         var11 = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, var2);
         var7 = toBIO(ByteBufAllocator.DEFAULT, var11.retain());
         var9 = toBIO(ByteBufAllocator.DEFAULT, var11.retain());
         if (var3 != null) {
            var5 = toBIO(ByteBufAllocator.DEFAULT, var3);
         }

         SSLContext.setCertificateBio(var0, var7, var5, var4 == null ? "" : var4);
         SSLContext.setCertificateChainBio(var0, var9, true);
      } catch (SSLException var17) {
         throw var17;
      } catch (Exception var18) {
         throw new SSLException("failed to set certificate and key", var18);
      } finally {
         freeBio(var5);
         freeBio(var7);
         freeBio(var9);
         if (var11 != null) {
            var11.release();
         }
      }
   }

   static void freeBio(long var0) {
      if (var0 != 0L) {
         SSL.freeBIO(var0);
      }
   }

   static long toBIO(ByteBufAllocator var0, PrivateKey var1) throws Exception {
      if (var1 == null) {
         return 0L;
      } else {
         PemEncoded var2 = PemPrivateKey.toPEM(var0, true, var1);

         long var3;
         try {
            var3 = toBIO(var0, var2.retain());
         } finally {
            var2.release();
         }

         return var3;
      }
   }

   static long toBIO(ByteBufAllocator var0, X509Certificate... var1) throws Exception {
      if (var1 == null) {
         return 0L;
      } else {
         ObjectUtil.checkNonEmpty(var1, "certChain");
         PemEncoded var2 = PemX509Certificate.toPEM(var0, true, var1);

         long var3;
         try {
            var3 = toBIO(var0, var2.retain());
         } finally {
            var2.release();
         }

         return var3;
      }
   }

   static long toBIO(ByteBufAllocator var0, PemEncoded var1) throws Exception {
      long var4;
      try {
         ByteBuf var2 = var1.content();
         if (var2.isDirect()) {
            return newBIO(var2.retainedSlice());
         }

         ByteBuf var3 = var0.directBuffer(var2.readableBytes());

         try {
            var3.writeBytes(var2, var2.readerIndex(), var2.readableBytes());
            var4 = newBIO(var3.retainedSlice());
         } finally {
            try {
               if (var1.isSensitive()) {
                  SslUtils.zeroout(var3);
               }
            } finally {
               var3.release();
            }
         }
      } finally {
         var1.release();
      }

      return var4;
   }

   private static long newBIO(ByteBuf var0) throws Exception {
      long var4;
      try {
         long var1 = SSL.newMemBIO();
         int var3 = var0.readableBytes();
         if (SSL.bioWrite(var1, OpenSsl.memoryAddress(var0) + (long)var0.readerIndex(), var3) != var3) {
            SSL.freeBIO(var1);
            throw new IllegalStateException("Could not write data to memory BIO");
         }

         var4 = var1;
      } finally {
         var0.release();
      }

      return var4;
   }

   static OpenSslKeyMaterialProvider providerFor(KeyManagerFactory var0, String var1) {
      if (var0 instanceof OpenSslX509KeyManagerFactory) {
         return ((OpenSslX509KeyManagerFactory)var0).newProvider();
      } else {
         return var0 instanceof OpenSslCachingX509KeyManagerFactory
            ? ((OpenSslCachingX509KeyManagerFactory)var0).newProvider(var1)
            : new OpenSslKeyMaterialProvider(chooseX509KeyManager(var0.getKeyManagers()), var1);
      }
   }

   private static ReferenceCountedOpenSslEngine retrieveEngine(OpenSslEngineMap var0, long var1) throws SSLException {
      ReferenceCountedOpenSslEngine var3 = var0.get(var1);
      if (var3 == null) {
         throw new SSLException("Could not find a " + StringUtil.simpleClassName(ReferenceCountedOpenSslEngine.class) + " for sslPointer " + var1);
      } else {
         return var3;
      }
   }

   private static byte[] verifyResult(byte[] var0) throws SignatureException {
      if (var0 == null) {
         throw new SignatureException();
      } else {
         return var0;
      }
   }

   static {
      Integer var0 = null;

      try {
         String var1 = SystemPropertyUtil.get("jdk.tls.ephemeralDHKeySize");
         if (var1 != null) {
            try {
               var0 = Integer.valueOf(var1);
            } catch (NumberFormatException var3) {
               logger.debug("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + var1);
            }
         }
      } catch (Throwable var4) {
      }

      DH_KEY_LENGTH = var0;
   }

   abstract static class AbstractCertificateVerifier extends CertificateVerifier {
      private final OpenSslEngineMap engineMap;

      AbstractCertificateVerifier(OpenSslEngineMap var1) {
         this.engineMap = var1;
      }

      public final int verify(long var1, byte[][] var3, String var4) {
         ReferenceCountedOpenSslEngine var5 = this.engineMap.get(var1);
         if (var5 == null) {
            return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
         } else {
            X509Certificate[] var6 = ReferenceCountedOpenSslContext.certificates(var3);

            try {
               this.verify(var5, var6, var4);
               return CertificateVerifier.X509_V_OK;
            } catch (Throwable var8) {
               ReferenceCountedOpenSslContext.logger.debug("verification of certificate failed", var8);
               var5.initHandshakeException(var8);
               if (var8 instanceof OpenSslCertificateException) {
                  return ((OpenSslCertificateException)var8).errorCode();
               } else if (var8 instanceof CertificateExpiredException) {
                  return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
               } else if (var8 instanceof CertificateNotYetValidException) {
                  return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
               } else {
                  return PlatformDependent.javaVersion() >= 7 ? translateToError(var8) : CertificateVerifier.X509_V_ERR_UNSPECIFIED;
               }
            }
         }
      }

      @SuppressJava6Requirement(
         reason = "Usage guarded by java version check"
      )
      private static int translateToError(Throwable var0) {
         if (var0 instanceof CertificateRevokedException) {
            return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
         } else {
            for (Throwable var1 = var0.getCause(); var1 != null; var1 = var1.getCause()) {
               if (var1 instanceof CertPathValidatorException) {
                  CertPathValidatorException var2 = (CertPathValidatorException)var1;
                  Reason var3 = var2.getReason();
                  if (var3 == BasicReason.EXPIRED) {
                     return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
                  }

                  if (var3 == BasicReason.NOT_YET_VALID) {
                     return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
                  }

                  if (var3 == BasicReason.REVOKED) {
                     return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
                  }
               }
            }

            return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
         }
      }

      abstract void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception;
   }

   private static final class AsyncPrivateKeyMethod implements AsyncSSLPrivateKeyMethod {
      private final OpenSslEngineMap engineMap;
      private final OpenSslAsyncPrivateKeyMethod keyMethod;

      AsyncPrivateKeyMethod(OpenSslEngineMap var1, OpenSslAsyncPrivateKeyMethod var2) {
         this.engineMap = var1;
         this.keyMethod = var2;
      }

      public void sign(long var1, int var3, byte[] var4, ResultCallback<byte[]> var5) {
         try {
            ReferenceCountedOpenSslEngine var6 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);
            this.keyMethod
               .sign(var6, var3, var4)
               .addListener(new ReferenceCountedOpenSslContext.AsyncPrivateKeyMethod.ResultCallbackListener(var6, var1, var5));
         } catch (SSLException var7) {
            var5.onError(var1, var7);
         }
      }

      public void decrypt(long var1, byte[] var3, ResultCallback<byte[]> var4) {
         try {
            ReferenceCountedOpenSslEngine var5 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);
            this.keyMethod.decrypt(var5, var3).addListener(new ReferenceCountedOpenSslContext.AsyncPrivateKeyMethod.ResultCallbackListener(var5, var1, var4));
         } catch (SSLException var6) {
            var4.onError(var1, var6);
         }
      }

      private static final class ResultCallbackListener implements FutureListener<byte[]> {
         private final ReferenceCountedOpenSslEngine engine;
         private final long ssl;
         private final ResultCallback<byte[]> resultCallback;

         ResultCallbackListener(ReferenceCountedOpenSslEngine var1, long var2, ResultCallback<byte[]> var4) {
            this.engine = var1;
            this.ssl = var2;
            this.resultCallback = var4;
         }

         @Override
         public void operationComplete(Future<byte[]> var1) {
            Object var2 = var1.cause();
            if (var2 == null) {
               try {
                  byte[] var3 = ReferenceCountedOpenSslContext.verifyResult((byte[])var1.getNow());
                  this.resultCallback.onSuccess(this.ssl, var3);
                  return;
               } catch (SignatureException var4) {
                  var2 = var4;
                  this.engine.initHandshakeException(var4);
               }
            }

            this.resultCallback.onError(this.ssl, (Throwable)var2);
         }
      }
   }

   private static final class CompressionAlgorithm implements CertificateCompressionAlgo {
      private final OpenSslEngineMap engineMap;
      private final OpenSslCertificateCompressionAlgorithm compressionAlgorithm;

      CompressionAlgorithm(OpenSslEngineMap var1, OpenSslCertificateCompressionAlgorithm var2) {
         this.engineMap = var1;
         this.compressionAlgorithm = var2;
      }

      public byte[] compress(long var1, byte[] var3) throws Exception {
         ReferenceCountedOpenSslEngine var4 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);
         return this.compressionAlgorithm.compress(var4, var3);
      }

      public byte[] decompress(long var1, int var3, byte[] var4) throws Exception {
         ReferenceCountedOpenSslEngine var5 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);
         return this.compressionAlgorithm.decompress(var5, var3, var4);
      }

      public int algorithmId() {
         return this.compressionAlgorithm.algorithmId();
      }
   }

   private static final class DefaultOpenSslEngineMap implements OpenSslEngineMap {
      private final Map<Long, ReferenceCountedOpenSslEngine> engines = PlatformDependent.newConcurrentHashMap();

      private DefaultOpenSslEngineMap() {
      }

      @Override
      public ReferenceCountedOpenSslEngine remove(long var1) {
         return this.engines.remove(var1);
      }

      @Override
      public void add(ReferenceCountedOpenSslEngine var1) {
         this.engines.put(var1.sslPointer(), var1);
      }

      @Override
      public ReferenceCountedOpenSslEngine get(long var1) {
         return this.engines.get(var1);
      }
   }

   private static final class PrivateKeyMethod implements SSLPrivateKeyMethod {
      private final OpenSslEngineMap engineMap;
      private final OpenSslPrivateKeyMethod keyMethod;

      PrivateKeyMethod(OpenSslEngineMap var1, OpenSslPrivateKeyMethod var2) {
         this.engineMap = var1;
         this.keyMethod = var2;
      }

      public byte[] sign(long var1, int var3, byte[] var4) throws Exception {
         ReferenceCountedOpenSslEngine var5 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);

         try {
            return ReferenceCountedOpenSslContext.verifyResult(this.keyMethod.sign(var5, var3, var4));
         } catch (Exception var7) {
            var5.initHandshakeException(var7);
            throw var7;
         }
      }

      public byte[] decrypt(long var1, byte[] var3) throws Exception {
         ReferenceCountedOpenSslEngine var4 = ReferenceCountedOpenSslContext.retrieveEngine(this.engineMap, var1);

         try {
            return ReferenceCountedOpenSslContext.verifyResult(this.keyMethod.decrypt(var4, var3));
         } catch (Exception var6) {
            var4.initHandshakeException(var6);
            throw var6;
         }
      }
   }
}
