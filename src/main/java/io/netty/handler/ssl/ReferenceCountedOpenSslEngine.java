package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.util.LazyJavaxX509Certificate;
import io.netty.handler.ssl.util.LazyX509Certificate;
import io.netty.internal.tcnative.AsyncTask;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.security.cert.X509Certificate;

public class ReferenceCountedOpenSslEngine extends SSLEngine implements ApplicationProtocolAccessor, ReferenceCounted {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
   private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
   private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_3 = 5;
   private static final int[] OPENSSL_OP_NO_PROTOCOLS = new int[]{
      SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2, SSL.SSL_OP_NO_TLSv1_3
   };
   static final int MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
   static final int MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
   private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult(Status.OK, HandshakeStatus.NEED_UNWRAP, 0, 0);
   private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NEED_UNWRAP, 0, 0);
   private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult(Status.OK, HandshakeStatus.NEED_WRAP, 0, 0);
   private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NEED_WRAP, 0, 0);
   private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult(Status.CLOSED, HandshakeStatus.NOT_HANDSHAKING, 0, 0);
   private long ssl;
   private long networkBIO;
   private ReferenceCountedOpenSslEngine.HandshakeState handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED;
   private boolean receivedShutdown;
   private volatile boolean destroyed;
   private volatile String applicationProtocol;
   private volatile boolean needTask;
   private String[] explicitlyEnabledProtocols;
   private boolean sessionSet;
   private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
   private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted() {
      @Override
      public ReferenceCounted touch(Object var1) {
         if (ReferenceCountedOpenSslEngine.this.leak != null) {
            ReferenceCountedOpenSslEngine.this.leak.record(var1);
         }

         return ReferenceCountedOpenSslEngine.this;
      }

      @Override
      protected void deallocate() {
         ReferenceCountedOpenSslEngine.this.shutdown();
         if (ReferenceCountedOpenSslEngine.this.leak != null) {
            boolean var1 = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this);

            assert var1;
         }

         ReferenceCountedOpenSslEngine.this.parentContext.release();
      }
   };
   private volatile ClientAuth clientAuth = ClientAuth.NONE;
   private volatile long lastAccessed = -1L;
   private String endPointIdentificationAlgorithm;
   private Object algorithmConstraints;
   private List<String> sniHostNames;
   private volatile Collection<?> matchers;
   private boolean isInboundDone;
   private boolean outboundClosed;
   final boolean jdkCompatibilityMode;
   private final boolean clientMode;
   final ByteBufAllocator alloc;
   private final OpenSslEngineMap engineMap;
   private final OpenSslApplicationProtocolNegotiator apn;
   private final ReferenceCountedOpenSslContext parentContext;
   private final OpenSslSession session;
   private final ByteBuffer[] singleSrcBuffer = new ByteBuffer[1];
   private final ByteBuffer[] singleDstBuffer = new ByteBuffer[1];
   private final boolean enableOcsp;
   private int maxWrapOverhead;
   private int maxWrapBufferSize;
   private Throwable pendingException;

   ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext var1, ByteBufAllocator var2, String var3, int var4, boolean var5, boolean var6) {
      super(var3, var4);
      OpenSsl.ensureAvailability();
      this.engineMap = var1.engineMap;
      this.enableOcsp = var1.enableOcsp;
      this.jdkCompatibilityMode = var5;
      this.alloc = ObjectUtil.checkNotNull(var2, "alloc");
      this.apn = (OpenSslApplicationProtocolNegotiator)var1.applicationProtocolNegotiator();
      this.clientMode = var1.isClient();
      if (PlatformDependent.javaVersion() >= 7) {
         this.session = new ExtendedOpenSslSession(new ReferenceCountedOpenSslEngine.DefaultOpenSslSession(var1.sessionContext())) {
            private String[] peerSupportedSignatureAlgorithms;
            private List requestedServerNames;

            @Override
            public List getRequestedServerNames() {
               if (ReferenceCountedOpenSslEngine.this.clientMode) {
                  return Java8SslUtils.getSniHostNames(ReferenceCountedOpenSslEngine.this.sniHostNames);
               } else {
                  synchronized (ReferenceCountedOpenSslEngine.this) {
                     if (this.requestedServerNames == null) {
                        if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                           this.requestedServerNames = Collections.emptyList();
                        } else {
                           String var2 = SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl);
                           if (var2 == null) {
                              this.requestedServerNames = Collections.emptyList();
                           } else {
                              this.requestedServerNames = Java8SslUtils.getSniHostName(
                                 SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl).getBytes(CharsetUtil.UTF_8)
                              );
                           }
                        }
                     }

                     return this.requestedServerNames;
                  }
               }
            }

            @Override
            public String[] getPeerSupportedSignatureAlgorithms() {
               synchronized (ReferenceCountedOpenSslEngine.this) {
                  if (this.peerSupportedSignatureAlgorithms == null) {
                     if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                        this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                     } else {
                        String[] var2 = SSL.getSigAlgs(ReferenceCountedOpenSslEngine.this.ssl);
                        if (var2 == null) {
                           this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                        } else {
                           LinkedHashSet var3 = new LinkedHashSet(var2.length);

                           for (String var7 : var2) {
                              String var8 = SignatureAlgorithmConverter.toJavaName(var7);
                              if (var8 != null) {
                                 var3.add(var8);
                              }
                           }

                           this.peerSupportedSignatureAlgorithms = var3.toArray(new String[0]);
                        }
                     }
                  }

                  return (String[])this.peerSupportedSignatureAlgorithms.clone();
               }
            }

            @Override
            public List<byte[]> getStatusResponses() {
               byte[] var1 = null;
               if (ReferenceCountedOpenSslEngine.this.enableOcsp && ReferenceCountedOpenSslEngine.this.clientMode) {
                  synchronized (ReferenceCountedOpenSslEngine.this) {
                     if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                        var1 = SSL.getOcspResponse(ReferenceCountedOpenSslEngine.this.ssl);
                     }
                  }
               }

               return var1 == null ? Collections.emptyList() : Collections.singletonList(var1);
            }
         };
      } else {
         this.session = new ReferenceCountedOpenSslEngine.DefaultOpenSslSession(var1.sessionContext());
      }

      if (!var1.sessionContext().useKeyManager()) {
         this.session.setLocalCertificate(var1.keyCertChain);
      }

      Lock var7 = var1.ctxLock.readLock();
      var7.lock();

      long var8;
      try {
         var8 = SSL.newSSL(var1.ctx, !var1.isClient());
      } finally {
         var7.unlock();
      }

      synchronized (this) {
         this.ssl = var8;

         try {
            this.networkBIO = SSL.bioNewByteBuffer(this.ssl, var1.getBioNonApplicationBufferSize());
            this.setClientAuth(this.clientMode ? ClientAuth.NONE : var1.clientAuth);
            if (var1.protocols != null) {
               this.setEnabledProtocols0(var1.protocols, true);
            } else {
               this.explicitlyEnabledProtocols = this.getEnabledProtocols();
            }

            if (this.clientMode && SslUtils.isValidHostNameForSNI(var3)) {
               if (PlatformDependent.javaVersion() >= 8) {
                  if (Java8SslUtils.isValidHostNameForSNI(var3)) {
                     SSL.setTlsExtHostName(this.ssl, var3);
                     this.sniHostNames = Collections.singletonList(var3);
                  }
               } else {
                  SSL.setTlsExtHostName(this.ssl, var3);
                  this.sniHostNames = Collections.singletonList(var3);
               }
            }

            if (this.enableOcsp) {
               SSL.enableOcsp(this.ssl);
            }

            if (!var5) {
               SSL.setMode(this.ssl, SSL.getMode(this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE);
            }

            if (isProtocolEnabled(SSL.getOptions(this.ssl), SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
               boolean var11 = this.clientMode
                  ? ReferenceCountedOpenSslContext.CLIENT_ENABLE_SESSION_TICKET_TLSV13
                  : ReferenceCountedOpenSslContext.SERVER_ENABLE_SESSION_TICKET_TLSV13;
               if (var11) {
                  SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_TICKET);
               }
            }

            if (OpenSsl.isBoringSSL() && this.clientMode) {
               SSL.setRenegotiateMode(this.ssl, SSL.SSL_RENEGOTIATE_ONCE);
            }

            this.calculateMaxWrapOverhead();
         } catch (Throwable var16) {
            this.shutdown();
            PlatformDependent.throwException(var16);
         }
      }

      this.parentContext = var1;
      this.parentContext.retain();
      this.leak = var6 ? leakDetector.track(this) : null;
   }

   final synchronized String[] authMethods() {
      return this.isDestroyed() ? EmptyArrays.EMPTY_STRINGS : SSL.authenticationMethods(this.ssl);
   }

   final boolean setKeyMaterial(OpenSslKeyMaterial var1) throws Exception {
      synchronized (this) {
         if (this.isDestroyed()) {
            return false;
         }

         SSL.setKeyMaterial(this.ssl, var1.certificateChainAddress(), var1.privateKeyAddress());
      }

      this.session.setLocalCertificate(var1.certificateChain());
      return true;
   }

   final synchronized SecretKeySpec masterKey() {
      return this.isDestroyed() ? null : new SecretKeySpec(SSL.getMasterKey(this.ssl), "AES");
   }

   synchronized boolean isSessionReused() {
      return this.isDestroyed() ? false : SSL.isSessionReused(this.ssl);
   }

   public void setOcspResponse(byte[] var1) {
      if (!this.enableOcsp) {
         throw new IllegalStateException("OCSP stapling is not enabled");
      } else if (this.clientMode) {
         throw new IllegalStateException("Not a server SSLEngine");
      } else {
         synchronized (this) {
            if (!this.isDestroyed()) {
               SSL.setOcspResponse(this.ssl, var1);
            }
         }
      }
   }

   public byte[] getOcspResponse() {
      if (!this.enableOcsp) {
         throw new IllegalStateException("OCSP stapling is not enabled");
      } else if (!this.clientMode) {
         throw new IllegalStateException("Not a client SSLEngine");
      } else {
         synchronized (this) {
            return this.isDestroyed() ? EmptyArrays.EMPTY_BYTES : SSL.getOcspResponse(this.ssl);
         }
      }
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

   @Override
   public String getApplicationProtocol() {
      return this.applicationProtocol;
   }

   @Override
   public String getHandshakeApplicationProtocol() {
      return this.applicationProtocol;
   }

   @Override
   public final synchronized SSLSession getHandshakeSession() {
      switch (this.handshakeState) {
         case NOT_STARTED:
         case FINISHED:
            return null;
         default:
            return this.session;
      }
   }

   public final synchronized long sslPointer() {
      return this.ssl;
   }

   public final synchronized void shutdown() {
      if (!this.destroyed) {
         this.destroyed = true;
         if (this.engineMap != null) {
            this.engineMap.remove(this.ssl);
         }

         SSL.freeSSL(this.ssl);
         this.ssl = this.networkBIO = 0L;
         this.isInboundDone = this.outboundClosed = true;
      }

      SSL.clearError();
   }

   private int writePlaintextData(ByteBuffer var1, int var2) {
      int var3 = var1.position();
      int var4 = var1.limit();
      int var5;
      if (var1.isDirect()) {
         var5 = SSL.writeToSSL(this.ssl, bufferAddress(var1) + (long)var3, var2);
         if (var5 > 0) {
            ((Buffer)var1).position(var3 + var5);
         }
      } else {
         ByteBuf var6 = this.alloc.directBuffer(var2);

         try {
            ((Buffer)var1).limit(var3 + var2);
            var6.setBytes(0, var1);
            ((Buffer)var1).limit(var4);
            var5 = SSL.writeToSSL(this.ssl, OpenSsl.memoryAddress(var6), var2);
            if (var5 > 0) {
               ((Buffer)var1).position(var3 + var5);
            } else {
               ((Buffer)var1).position(var3);
            }
         } finally {
            var6.release();
         }
      }

      return var5;
   }

   synchronized void bioSetFd(int var1) {
      if (!this.isDestroyed()) {
         SSL.bioSetFd(this.ssl, var1);
      }
   }

   private ByteBuf writeEncryptedData(ByteBuffer var1, int var2) throws SSLException {
      int var3 = var1.position();
      if (var1.isDirect()) {
         SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(var1) + (long)var3, var2, false);
      } else {
         ByteBuf var4 = this.alloc.directBuffer(var2);

         try {
            int var5 = var1.limit();
            ((Buffer)var1).limit(var3 + var2);
            var4.writeBytes(var1);
            ((Buffer)var1).position(var3);
            ((Buffer)var1).limit(var5);
            SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(var4), var2, false);
            return var4;
         } catch (Throwable var6) {
            var4.release();
            PlatformDependent.throwException(var6);
         }
      }

      return null;
   }

   private int readPlaintextData(ByteBuffer var1) throws SSLException {
      int var3 = var1.position();
      int var2;
      if (var1.isDirect()) {
         var2 = SSL.readFromSSL(this.ssl, bufferAddress(var1) + (long)var3, var1.limit() - var3);
         if (var2 > 0) {
            ((Buffer)var1).position(var3 + var2);
         }
      } else {
         int var4 = var1.limit();
         int var5 = Math.min(this.maxEncryptedPacketLength0(), var4 - var3);
         ByteBuf var6 = this.alloc.directBuffer(var5);

         try {
            var2 = SSL.readFromSSL(this.ssl, OpenSsl.memoryAddress(var6), var5);
            if (var2 > 0) {
               ((Buffer)var1).limit(var3 + var2);
               var6.getBytes(var6.readerIndex(), var1);
               ((Buffer)var1).limit(var4);
            }
         } finally {
            var6.release();
         }
      }

      return var2;
   }

   final synchronized int maxWrapOverhead() {
      return this.maxWrapOverhead;
   }

   final synchronized int maxEncryptedPacketLength() {
      return this.maxEncryptedPacketLength0();
   }

   final int maxEncryptedPacketLength0() {
      return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
   }

   final int calculateMaxLengthForWrap(int var1, int var2) {
      return (int)Math.min((long)this.maxWrapBufferSize, (long)var1 + (long)this.maxWrapOverhead * (long)var2);
   }

   final synchronized int sslPending() {
      return this.sslPending0();
   }

   private void calculateMaxWrapOverhead() {
      this.maxWrapOverhead = SSL.getMaxWrapOverhead(this.ssl);
      this.maxWrapBufferSize = this.jdkCompatibilityMode ? this.maxEncryptedPacketLength0() : this.maxEncryptedPacketLength0() << 4;
   }

   private int sslPending0() {
      return this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED ? 0 : SSL.sslPending(this.ssl);
   }

   private boolean isBytesAvailableEnoughForWrap(int var1, int var2, int var3) {
      return (long)var1 - (long)this.maxWrapOverhead * (long)var3 >= (long)var2;
   }

   @Override
   public final SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      ObjectUtil.checkNotNullWithIAE(var1, "srcs");
      ObjectUtil.checkNotNullWithIAE(var4, "dst");
      if (var2 >= var1.length || var2 + var3 > var1.length) {
         throw new IndexOutOfBoundsException(
            "offset: " + var2 + ", length: " + var3 + " (expected: offset <= offset + length <= srcs.length (" + var1.length + "))"
         );
      } else if (var4.isReadOnly()) {
         throw new ReadOnlyBufferException();
      } else {
         synchronized (this) {
            if (this.isOutboundDone()) {
               return !this.isInboundDone() && !this.isDestroyed() ? NEED_UNWRAP_CLOSED : CLOSED_NOT_HANDSHAKING;
            } else {
               int var6 = 0;
               ByteBuf var7 = null;

               try {
                  if (var4.isDirect()) {
                     SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(var4) + (long)var4.position(), var4.remaining(), true);
                  } else {
                     var7 = this.alloc.directBuffer(var4.remaining());
                     SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(var7), var7.writableBytes(), true);
                  }

                  int var8 = SSL.bioLengthByteBuffer(this.networkBIO);
                  if (!this.outboundClosed) {
                     HandshakeStatus var9 = HandshakeStatus.NOT_HANDSHAKING;
                     if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
                        if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY) {
                           this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_IMPLICITLY;
                        }

                        var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                        if (this.pendingException != null) {
                           if (var6 <= 0) {
                              return this.newResult(this.handshakeException(), 0, 0);
                           }

                           return this.newResult(HandshakeStatus.NEED_WRAP, 0, var6);
                        }

                        var9 = this.handshake();
                        var6 = var8 - SSL.bioLengthByteBuffer(this.networkBIO);
                        if (var9 == HandshakeStatus.NEED_TASK) {
                           return this.newResult(var9, 0, var6);
                        }

                        if (var6 > 0) {
                           return this.newResult(
                              this.mayFinishHandshake(
                                 var9 != HandshakeStatus.FINISHED
                                    ? (var6 == var8 ? HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO)))
                                    : HandshakeStatus.FINISHED
                              ),
                              0,
                              var6
                           );
                        }

                        if (var9 == HandshakeStatus.NEED_UNWRAP) {
                           return this.isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK;
                        }

                        if (this.outboundClosed) {
                           var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                           return this.newResultMayFinishHandshake(var9, 0, var6);
                        }
                     }

                     int var10 = var2 + var3;
                     if (this.jdkCompatibilityMode) {
                        int var11 = 0;

                        for (int var12 = var2; var12 < var10; var12++) {
                           ByteBuffer var13 = var1[var12];
                           if (var13 == null) {
                              throw new IllegalArgumentException("srcs[" + var12 + "] is null");
                           }

                           if (var11 != MAX_PLAINTEXT_LENGTH) {
                              var11 += var13.remaining();
                              if (var11 > MAX_PLAINTEXT_LENGTH || var11 < 0) {
                                 var11 = MAX_PLAINTEXT_LENGTH;
                              }
                           }
                        }

                        if (!this.isBytesAvailableEnoughForWrap(var4.remaining(), var11, 1)) {
                           return new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                        }
                     }

                     int var35 = 0;

                     assert var6 == 0;

                     var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                     if (var6 > 0) {
                        return this.newResultMayFinishHandshake(var9, var35, var6);
                     } else if (this.pendingException != null) {
                        Throwable var38 = this.pendingException;
                        this.pendingException = null;
                        this.shutdown();
                        throw new SSLException(var38);
                     } else {
                        for (; var2 < var10; var2++) {
                           ByteBuffer var36 = var1[var2];
                           int var41 = var36.remaining();
                           if (var41 != 0) {
                              int var14;
                              if (this.jdkCompatibilityMode) {
                                 var14 = this.writePlaintextData(var36, Math.min(var41, MAX_PLAINTEXT_LENGTH - var35));
                              } else {
                                 int var15 = var4.remaining() - var6 - this.maxWrapOverhead;
                                 if (var15 <= 0) {
                                    return new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), var35, var6);
                                 }

                                 var14 = this.writePlaintextData(var36, Math.min(var41, var15));
                              }

                              int var42 = SSL.bioLengthByteBuffer(this.networkBIO);
                              var6 += var8 - var42;
                              var8 = var42;
                              if (var14 <= 0) {
                                 int var43 = SSL.getError(this.ssl, var14);
                                 if (var43 == SSL.SSL_ERROR_ZERO_RETURN) {
                                    if (this.receivedShutdown) {
                                       return this.newResult(HandshakeStatus.NOT_HANDSHAKING, var35, var6);
                                    }

                                    this.closeAll();
                                    var6 += var42 - SSL.bioLengthByteBuffer(this.networkBIO);
                                    HandshakeStatus var48 = this.mayFinishHandshake(
                                       var9 != HandshakeStatus.FINISHED
                                          ? (
                                             var6 == var4.remaining()
                                                ? HandshakeStatus.NEED_WRAP
                                                : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))
                                          )
                                          : HandshakeStatus.FINISHED
                                    );
                                    return this.newResult(var48, var35, var6);
                                 }

                                 if (var43 == SSL.SSL_ERROR_WANT_READ) {
                                    return this.newResult(HandshakeStatus.NEED_UNWRAP, var35, var6);
                                 }

                                 if (var43 != SSL.SSL_ERROR_WANT_WRITE) {
                                    if (var43 != SSL.SSL_ERROR_WANT_X509_LOOKUP
                                       && var43 != SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY
                                       && var43 != SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                                       throw this.shutdownWithError("SSL_write", var43);
                                    }

                                    return this.newResult(HandshakeStatus.NEED_TASK, var35, var6);
                                 }

                                 if (var6 <= 0) {
                                    return this.newResult(Status.BUFFER_OVERFLOW, var9, var35, var6);
                                 }

                                 return this.newResult(HandshakeStatus.NEED_WRAP, var35, var6);
                              }

                              var35 += var14;
                              if (this.jdkCompatibilityMode || var6 == var4.remaining()) {
                                 return this.newResultMayFinishHandshake(var9, var35, var6);
                              }
                           }
                        }

                        return this.newResultMayFinishHandshake(var9, var35, var6);
                     }
                  } else if (!this.isBytesAvailableEnoughForWrap(var4.remaining(), 2, 1)) {
                     return new SSLEngineResult(Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                  } else {
                     var6 = SSL.bioFlushByteBuffer(this.networkBIO);
                     if (var6 <= 0) {
                        return this.newResultMayFinishHandshake(HandshakeStatus.NOT_HANDSHAKING, 0, 0);
                     } else if (this.doSSLShutdown()) {
                        var6 = var8 - SSL.bioLengthByteBuffer(this.networkBIO);
                        return this.newResultMayFinishHandshake(HandshakeStatus.NEED_WRAP, 0, var6);
                     } else {
                        return this.newResultMayFinishHandshake(HandshakeStatus.NOT_HANDSHAKING, 0, var6);
                     }
                  }
               } finally {
                  SSL.bioClearByteBuffer(this.networkBIO);
                  if (var7 == null) {
                     ((Buffer)var4).position(var4.position() + var6);
                  } else {
                     assert var7.readableBytes() <= var4.remaining() : "The destination buffer "
                        + var4
                        + " didn't have enough remaining space to hold the encrypted content in "
                        + var7;

                     var4.put(var7.internalNioBuffer(var7.readerIndex(), var6));
                     var7.release();
                  }
               }
            }
         }
      }
   }

   private SSLEngineResult newResult(HandshakeStatus var1, int var2, int var3) {
      return this.newResult(Status.OK, var1, var2, var3);
   }

   private SSLEngineResult newResult(Status var1, HandshakeStatus var2, int var3, int var4) {
      if (this.isOutboundDone()) {
         if (this.isInboundDone()) {
            var2 = HandshakeStatus.NOT_HANDSHAKING;
            this.shutdown();
         }

         return new SSLEngineResult(Status.CLOSED, var2, var3, var4);
      } else {
         if (var2 == HandshakeStatus.NEED_TASK) {
            this.needTask = true;
         }

         return new SSLEngineResult(var1, var2, var3, var4);
      }
   }

   private SSLEngineResult newResultMayFinishHandshake(HandshakeStatus var1, int var2, int var3) throws SSLException {
      return this.newResult(this.mayFinishHandshake(var1, var2, var3), var2, var3);
   }

   private SSLEngineResult newResultMayFinishHandshake(Status var1, HandshakeStatus var2, int var3, int var4) throws SSLException {
      return this.newResult(var1, this.mayFinishHandshake(var2, var3, var4), var3, var4);
   }

   private SSLException shutdownWithError(String var1, int var2) {
      return this.shutdownWithError(var1, var2, SSL.getLastErrorNumber());
   }

   private SSLException shutdownWithError(String var1, int var2, int var3) {
      String var4 = SSL.getErrorString((long)var3);
      if (logger.isDebugEnabled()) {
         logger.debug("{} failed with {}: OpenSSL error: {} {}", var1, var2, var3, var4);
      }

      this.shutdown();
      if (this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
         return new SSLException(var4);
      } else {
         SSLHandshakeException var5 = new SSLHandshakeException(var4);
         if (this.pendingException != null) {
            var5.initCause(this.pendingException);
            this.pendingException = null;
         }

         return var5;
      }
   }

   private SSLEngineResult handleUnwrapException(int var1, int var2, SSLException var3) throws SSLException {
      int var4 = SSL.getLastErrorNumber();
      if (var4 != 0) {
         return this.sslReadErrorResult(SSL.SSL_ERROR_SSL, var4, var1, var2);
      } else {
         throw var3;
      }
   }

   public final SSLEngineResult unwrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer[] var4, int var5, int var6) throws SSLException {
      ObjectUtil.checkNotNullWithIAE(var1, "srcs");
      if (var2 < var1.length && var2 + var3 <= var1.length) {
         ObjectUtil.checkNotNullWithIAE(var4, "dsts");
         if (var5 < var4.length && var5 + var6 <= var4.length) {
            long var7 = 0L;
            int var9 = var5 + var6;

            for (int var10 = var5; var10 < var9; var10++) {
               ByteBuffer var11 = ObjectUtil.checkNotNullArrayParam(var4[var10], var10, "dsts");
               if (var11.isReadOnly()) {
                  throw new ReadOnlyBufferException();
               }

               var7 += (long)var11.remaining();
            }

            int var46 = var2 + var3;
            long var47 = 0L;

            for (int var13 = var2; var13 < var46; var13++) {
               ByteBuffer var14 = ObjectUtil.checkNotNullArrayParam(var1[var13], var13, "srcs");
               var47 += (long)var14.remaining();
            }

            synchronized (this) {
               if (this.isInboundDone()) {
                  return !this.isOutboundDone() && !this.isDestroyed() ? NEED_WRAP_CLOSED : CLOSED_NOT_HANDSHAKING;
               } else {
                  HandshakeStatus var48 = HandshakeStatus.NOT_HANDSHAKING;
                  if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
                     if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY) {
                        this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_IMPLICITLY;
                     }

                     var48 = this.handshake();
                     if (var48 == HandshakeStatus.NEED_TASK) {
                        return this.newResult(var48, 0, 0);
                     }

                     if (var48 == HandshakeStatus.NEED_WRAP) {
                        return NEED_WRAP_OK;
                     }

                     if (this.isInboundDone) {
                        return NEED_WRAP_CLOSED;
                     }
                  }

                  int var15 = this.sslPending0();
                  int var16;
                  if (this.jdkCompatibilityMode) {
                     if (var47 < 5L) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var48, 0, 0);
                     }

                     var16 = SslUtils.getEncryptedPacketLength(var1, var2);
                     if (var16 == -2) {
                        throw new NotSslRecordException("not an SSL/TLS record");
                     }

                     int var17 = var16 - 5;
                     if ((long)var17 > var7) {
                        if (var17 > MAX_RECORD_SIZE) {
                           throw new SSLException("Illegal packet length: " + var17 + " > " + this.session.getApplicationBufferSize());
                        }

                        this.session.tryExpandApplicationBufferSize(var17);
                        return this.newResultMayFinishHandshake(Status.BUFFER_OVERFLOW, var48, 0, 0);
                     }

                     if (var47 < (long)var16) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var48, 0, 0);
                     }
                  } else {
                     if (var47 == 0L && var15 <= 0) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_UNDERFLOW, var48, 0, 0);
                     }

                     if (var7 == 0L) {
                        return this.newResultMayFinishHandshake(Status.BUFFER_OVERFLOW, var48, 0, 0);
                     }

                     var16 = (int)Math.min(2147483647L, var47);
                  }

                  assert var2 < var46;

                  assert var7 > 0L;

                  int var49 = 0;
                  int var18 = 0;

                  try {
                     label702:
                     while (true) {
                        ByteBuffer var19 = var1[var2];
                        int var20 = var19.remaining();
                        ByteBuf var21;
                        int var22;
                        if (var20 == 0) {
                           if (var15 <= 0) {
                              if (++var2 < var46) {
                                 continue;
                              }
                              break;
                           }

                           var21 = null;
                           var22 = SSL.bioLengthByteBuffer(this.networkBIO);
                        } else {
                           var22 = Math.min(var16, var20);

                           try {
                              var21 = this.writeEncryptedData(var19, var22);
                           } catch (SSLException var41) {
                              return this.handleUnwrapException(var18, var49, var41);
                           }
                        }

                        try {
                           while (true) {
                              ByteBuffer var23 = var4[var5];
                              if (var23.hasRemaining()) {
                                 int var50;
                                 try {
                                    var50 = this.readPlaintextData(var23);
                                 } catch (SSLException var42) {
                                    return this.handleUnwrapException(var18, var49, var42);
                                 }

                                 int var25 = var22 - SSL.bioLengthByteBuffer(this.networkBIO);
                                 var18 += var25;
                                 var16 -= var25;
                                 var22 -= var25;
                                 ((Buffer)var19).position(var19.position() + var25);
                                 if (var50 <= 0) {
                                    int var52 = SSL.getError(this.ssl, var50);
                                    if (var52 != SSL.SSL_ERROR_WANT_READ && var52 != SSL.SSL_ERROR_WANT_WRITE) {
                                       if (var52 == SSL.SSL_ERROR_ZERO_RETURN) {
                                          if (!this.receivedShutdown) {
                                             this.closeAll();
                                          }

                                          return this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var48, var18, var49);
                                       }

                                       if (var52 == SSL.SSL_ERROR_WANT_X509_LOOKUP
                                          || var52 == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY
                                          || var52 == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                                          return this.newResult(this.isInboundDone() ? Status.CLOSED : Status.OK, HandshakeStatus.NEED_TASK, var18, var49);
                                       }

                                       return this.sslReadErrorResult(var52, SSL.getLastErrorNumber(), var18, var49);
                                    }

                                    if (++var2 < var46) {
                                       break;
                                    }
                                    break label702;
                                 }

                                 var49 += var50;
                                 if (!var23.hasRemaining()) {
                                    var15 = this.sslPending0();
                                    if (++var5 >= var9) {
                                       return var15 > 0
                                          ? this.newResult(Status.BUFFER_OVERFLOW, var48, var18, var49)
                                          : this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var48, var18, var49);
                                    }
                                 } else if (var16 == 0 || this.jdkCompatibilityMode) {
                                    break label702;
                                 }
                              } else if (++var5 >= var9) {
                                 break label702;
                              }
                           }
                        } finally {
                           if (var21 != null) {
                              var21.release();
                           }
                        }
                     }
                  } finally {
                     SSL.bioClearByteBuffer(this.networkBIO);
                     this.rejectRemoteInitiatedRenegotiation();
                  }

                  if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN) {
                     this.closeAll();
                  }

                  return this.newResultMayFinishHandshake(this.isInboundDone() ? Status.CLOSED : Status.OK, var48, var18, var49);
               }
            }
         } else {
            throw new IndexOutOfBoundsException(
               "offset: " + var5 + ", length: " + var6 + " (expected: offset <= offset + length <= dsts.length (" + var4.length + "))"
            );
         }
      } else {
         throw new IndexOutOfBoundsException(
            "offset: " + var2 + ", length: " + var3 + " (expected: offset <= offset + length <= srcs.length (" + var1.length + "))"
         );
      }
   }

   private boolean needWrapAgain(int var1) {
      if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
         String var2 = SSL.getErrorString((long)var1);
         Object var3 = this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED ? new SSLException(var2) : new SSLHandshakeException(var2);
         if (this.pendingException == null) {
            this.pendingException = (Throwable)var3;
         } else {
            ThrowableUtil.addSuppressed(this.pendingException, (Throwable)var3);
         }

         SSL.clearError();
         return true;
      } else {
         return false;
      }
   }

   private SSLEngineResult sslReadErrorResult(int var1, int var2, int var3, int var4) throws SSLException {
      if (this.needWrapAgain(var2)) {
         return new SSLEngineResult(Status.OK, HandshakeStatus.NEED_WRAP, var3, var4);
      } else {
         throw this.shutdownWithError("SSL_read", var1, var2);
      }
   }

   private void closeAll() throws SSLException {
      this.receivedShutdown = true;
      this.closeOutbound();
      this.closeInbound();
   }

   private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
      if (!this.isDestroyed()
         && (!this.clientMode && SSL.getHandshakeCount(this.ssl) > 1 || this.clientMode && SSL.getHandshakeCount(this.ssl) > 2)
         && !"TLSv1.3".equals(this.session.getProtocol())
         && this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
         this.shutdown();
         throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
      }
   }

   public final SSLEngineResult unwrap(ByteBuffer[] var1, ByteBuffer[] var2) throws SSLException {
      return this.unwrap(var1, 0, var1.length, var2, 0, var2.length);
   }

   private ByteBuffer[] singleSrcBuffer(ByteBuffer var1) {
      this.singleSrcBuffer[0] = var1;
      return this.singleSrcBuffer;
   }

   private void resetSingleSrcBuffer() {
      this.singleSrcBuffer[0] = null;
   }

   private ByteBuffer[] singleDstBuffer(ByteBuffer var1) {
      this.singleDstBuffer[0] = var1;
      return this.singleDstBuffer;
   }

   private void resetSingleDstBuffer() {
      this.singleDstBuffer[0] = null;
   }

   @Override
   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      SSLEngineResult var5;
      try {
         var5 = this.unwrap(this.singleSrcBuffer(var1), 0, 1, var2, var3, var4);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var5;
   }

   @Override
   public final synchronized SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.wrap(this.singleSrcBuffer(var1), var2);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var3;
   }

   @Override
   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.unwrap(this.singleSrcBuffer(var1), this.singleDstBuffer(var2));
      } finally {
         this.resetSingleSrcBuffer();
         this.resetSingleDstBuffer();
      }

      return var3;
   }

   @Override
   public final synchronized SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      SSLEngineResult var3;
      try {
         var3 = this.unwrap(this.singleSrcBuffer(var1), var2);
      } finally {
         this.resetSingleSrcBuffer();
      }

      return var3;
   }

   private synchronized void runAndResetNeedTask(Runnable var1) {
      try {
         if (!this.isDestroyed()) {
            var1.run();
            return;
         }
      } finally {
         this.needTask = false;
      }
   }

   @Override
   public final synchronized Runnable getDelegatedTask() {
      if (this.isDestroyed()) {
         return null;
      } else {
         Runnable var1 = SSL.getTask(this.ssl);
         if (var1 == null) {
            return null;
         } else {
            return (Runnable)(var1 instanceof AsyncTask
               ? new ReferenceCountedOpenSslEngine.AsyncTaskDecorator((AsyncTask)var1)
               : new ReferenceCountedOpenSslEngine.TaskDecorator<>(var1));
         }
      }
   }

   @Override
   public final synchronized void closeInbound() throws SSLException {
      if (!this.isInboundDone) {
         this.isInboundDone = true;
         if (this.isOutboundDone()) {
            this.shutdown();
         }

         if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED && !this.receivedShutdown) {
            throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
         }
      }
   }

   @Override
   public final synchronized boolean isInboundDone() {
      return this.isInboundDone;
   }

   @Override
   public final synchronized void closeOutbound() {
      if (!this.outboundClosed) {
         this.outboundClosed = true;
         if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED && !this.isDestroyed()) {
            int var1 = SSL.getShutdown(this.ssl);
            if ((var1 & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN) {
               this.doSSLShutdown();
            }
         } else {
            this.shutdown();
         }
      }
   }

   private boolean doSSLShutdown() {
      if (SSL.isInInit(this.ssl) != 0) {
         return false;
      } else {
         int var1 = SSL.shutdownSSL(this.ssl);
         if (var1 < 0) {
            int var2 = SSL.getError(this.ssl, var1);
            if (var2 == SSL.SSL_ERROR_SYSCALL || var2 == SSL.SSL_ERROR_SSL) {
               if (logger.isDebugEnabled()) {
                  int var3 = SSL.getLastErrorNumber();
                  logger.debug("SSL_shutdown failed: OpenSSL error: {} {}", var3, SSL.getErrorString((long)var3));
               }

               this.shutdown();
               return false;
            }

            SSL.clearError();
         }

         return true;
      }
   }

   @Override
   public final synchronized boolean isOutboundDone() {
      return this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication(this.networkBIO) == 0);
   }

   @Override
   public final String[] getSupportedCipherSuites() {
      return OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[0]);
   }

   @Override
   public final String[] getEnabledCipherSuites() {
      String[] var1;
      String[] var2;
      boolean var3;
      synchronized (this) {
         if (this.isDestroyed()) {
            return EmptyArrays.EMPTY_STRINGS;
         }

         var2 = SSL.getCiphers(this.ssl);
         int var5 = SSL.getOptions(this.ssl);
         if (isProtocolEnabled(var5, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
            var1 = OpenSsl.EXTRA_SUPPORTED_TLS_1_3_CIPHERS;
            var3 = true;
         } else {
            var1 = EmptyArrays.EMPTY_STRINGS;
            var3 = false;
         }
      }

      if (var2 == null) {
         return EmptyArrays.EMPTY_STRINGS;
      } else {
         LinkedHashSet var4 = new LinkedHashSet(var2.length + var1.length);
         synchronized (this) {
            for (int var6 = 0; var6 < var2.length; var6++) {
               String var7 = this.toJavaCipherSuite(var2[var6]);
               String var8 = var7 == null ? var2[var6] : var7;
               if (var3 && OpenSsl.isTlsv13Supported() || !SslUtils.isTLSv13Cipher(var8)) {
                  var4.add(var8);
               }
            }

            Collections.addAll(var4, var1);
         }

         return var4.toArray(new String[0]);
      }
   }

   @Override
   public final void setEnabledCipherSuites(String[] var1) {
      ObjectUtil.checkNotNull(var1, "cipherSuites");
      StringBuilder var2 = new StringBuilder();
      StringBuilder var3 = new StringBuilder();
      CipherSuiteConverter.convertToCipherStrings(Arrays.asList(var1), var2, var3, OpenSsl.isBoringSSL());
      String var4 = var2.toString();
      String var5 = var3.toString();
      if (!OpenSsl.isTlsv13Supported() && !var5.isEmpty()) {
         throw new IllegalArgumentException("TLSv1.3 is not supported by this java version.");
      } else {
         synchronized (this) {
            if (this.isDestroyed()) {
               throw new IllegalStateException("failed to enable cipher suites: " + var4);
            } else {
               try {
                  SSL.setCipherSuites(this.ssl, var4, false);
                  if (OpenSsl.isTlsv13Supported()) {
                     SSL.setCipherSuites(this.ssl, OpenSsl.checkTls13Ciphers(logger, var5), true);
                  }

                  HashSet var7 = new HashSet(this.explicitlyEnabledProtocols.length);
                  Collections.addAll(var7, this.explicitlyEnabledProtocols);
                  if (var4.isEmpty()) {
                     var7.remove("TLSv1");
                     var7.remove("TLSv1.1");
                     var7.remove("TLSv1.2");
                     var7.remove("SSLv3");
                     var7.remove("SSLv2");
                     var7.remove("SSLv2Hello");
                  }

                  if (var5.isEmpty()) {
                     var7.remove("TLSv1.3");
                  }

                  this.setEnabledProtocols0(var7.toArray(EmptyArrays.EMPTY_STRINGS), false);
               } catch (Exception var9) {
                  throw new IllegalStateException("failed to enable cipher suites: " + var4, var9);
               }
            }
         }
      }
   }

   @Override
   public final String[] getSupportedProtocols() {
      return OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[0]);
   }

   @Override
   public final String[] getEnabledProtocols() {
      ArrayList var1 = new ArrayList(6);
      var1.add("SSLv2Hello");
      int var2;
      synchronized (this) {
         if (this.isDestroyed()) {
            return var1.toArray(new String[0]);
         }

         var2 = SSL.getOptions(this.ssl);
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1, "TLSv1")) {
         var1.add("TLSv1");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1")) {
         var1.add("TLSv1.1");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2")) {
         var1.add("TLSv1.2");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
         var1.add("TLSv1.3");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_SSLv2, "SSLv2")) {
         var1.add("SSLv2");
      }

      if (isProtocolEnabled(var2, SSL.SSL_OP_NO_SSLv3, "SSLv3")) {
         var1.add("SSLv3");
      }

      return var1.toArray(new String[0]);
   }

   private static boolean isProtocolEnabled(int var0, int var1, String var2) {
      return (var0 & var1) == 0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(var2);
   }

   @Override
   public final void setEnabledProtocols(String[] var1) {
      this.setEnabledProtocols0(var1, true);
   }

   private void setEnabledProtocols0(String[] var1, boolean var2) {
      ObjectUtil.checkNotNullWithIAE(var1, "protocols");
      int var3 = OPENSSL_OP_NO_PROTOCOLS.length;
      byte var4 = 0;

      for (String var8 : var1) {
         if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(var8)) {
            throw new IllegalArgumentException("Protocol " + var8 + " is not supported.");
         }

         if (var8.equals("SSLv2")) {
            if (var3 > 0) {
               var3 = 0;
            }

            if (var4 < 0) {
               var4 = 0;
            }
         } else if (var8.equals("SSLv3")) {
            if (var3 > 1) {
               var3 = 1;
            }

            if (var4 < 1) {
               var4 = 1;
            }
         } else if (var8.equals("TLSv1")) {
            if (var3 > 2) {
               var3 = 2;
            }

            if (var4 < 2) {
               var4 = 2;
            }
         } else if (var8.equals("TLSv1.1")) {
            if (var3 > 3) {
               var3 = 3;
            }

            if (var4 < 3) {
               var4 = 3;
            }
         } else if (var8.equals("TLSv1.2")) {
            if (var3 > 4) {
               var3 = 4;
            }

            if (var4 < 4) {
               var4 = 4;
            }
         } else if (var8.equals("TLSv1.3")) {
            if (var3 > 5) {
               var3 = 5;
            }

            if (var4 < 5) {
               var4 = 5;
            }
         }
      }

      synchronized (this) {
         if (var2) {
            this.explicitlyEnabledProtocols = var1;
         }

         if (this.isDestroyed()) {
            throw new IllegalStateException("failed to enable protocols: " + Arrays.<String>asList(var1));
         } else {
            SSL.clearOptions(
               this.ssl,
               SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2 | SSL.SSL_OP_NO_TLSv1_3
            );
            int var11 = 0;

            for (int var12 = 0; var12 < var3; var12++) {
               var11 |= OPENSSL_OP_NO_PROTOCOLS[var12];
            }

            assert var4 != Integer.MAX_VALUE;

            for (int var13 = var4 + 1; var13 < OPENSSL_OP_NO_PROTOCOLS.length; var13++) {
               var11 |= OPENSSL_OP_NO_PROTOCOLS[var13];
            }

            SSL.setOptions(this.ssl, var11);
         }
      }
   }

   @Override
   public final SSLSession getSession() {
      return this.session;
   }

   @Override
   public final synchronized void beginHandshake() throws SSLException {
      switch (this.handshakeState) {
         case NOT_STARTED:
            this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY;
            if (this.handshake() == HandshakeStatus.NEED_TASK) {
               this.needTask = true;
            }

            this.calculateMaxWrapOverhead();
            break;
         case FINISHED:
            throw new SSLException("renegotiation unsupported");
         case STARTED_IMPLICITLY:
            this.checkEngineClosed();
            this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.STARTED_EXPLICITLY;
            this.calculateMaxWrapOverhead();
         case STARTED_EXPLICITLY:
            break;
         default:
            throw new Error();
      }
   }

   private void checkEngineClosed() throws SSLException {
      if (this.isDestroyed()) {
         throw new SSLException("engine closed");
      }
   }

   private static HandshakeStatus pendingStatus(int var0) {
      return var0 > 0 ? HandshakeStatus.NEED_WRAP : HandshakeStatus.NEED_UNWRAP;
   }

   private static boolean isEmpty(Object[] var0) {
      return var0 == null || var0.length == 0;
   }

   private static boolean isEmpty(byte[] var0) {
      return var0 == null || var0.length == 0;
   }

   private HandshakeStatus handshakeException() throws SSLException {
      if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
         return HandshakeStatus.NEED_WRAP;
      } else {
         Throwable var1 = this.pendingException;

         assert var1 != null;

         this.pendingException = null;
         this.shutdown();
         if (var1 instanceof SSLHandshakeException) {
            throw (SSLHandshakeException)var1;
         } else {
            SSLHandshakeException var2 = new SSLHandshakeException("General OpenSslEngine problem");
            var2.initCause(var1);
            throw var2;
         }
      }
   }

   final void initHandshakeException(Throwable var1) {
      if (this.pendingException == null) {
         this.pendingException = var1;
      } else {
         ThrowableUtil.addSuppressed(this.pendingException, var1);
      }
   }

   private HandshakeStatus handshake() throws SSLException {
      if (this.needTask) {
         return HandshakeStatus.NEED_TASK;
      } else if (this.handshakeState == ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
         return HandshakeStatus.FINISHED;
      } else {
         this.checkEngineClosed();
         if (this.pendingException != null) {
            if (SSL.doHandshake(this.ssl) <= 0) {
               SSL.clearError();
            }

            return this.handshakeException();
         } else {
            this.engineMap.add(this);
            if (!this.sessionSet) {
               this.parentContext.sessionContext().setSessionFromCache(this.getPeerHost(), this.getPeerPort(), this.ssl);
               this.sessionSet = true;
            }

            if (this.lastAccessed == -1L) {
               this.lastAccessed = System.currentTimeMillis();
            }

            int var1 = SSL.doHandshake(this.ssl);
            if (var1 <= 0) {
               int var2 = SSL.getError(this.ssl, var1);
               if (var2 == SSL.SSL_ERROR_WANT_READ || var2 == SSL.SSL_ERROR_WANT_WRITE) {
                  return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
               } else if (var2 == SSL.SSL_ERROR_WANT_X509_LOOKUP
                  || var2 == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY
                  || var2 == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                  return HandshakeStatus.NEED_TASK;
               } else if (this.needWrapAgain(SSL.getLastErrorNumber())) {
                  return HandshakeStatus.NEED_WRAP;
               } else if (this.pendingException != null) {
                  return this.handshakeException();
               } else {
                  throw this.shutdownWithError("SSL_do_handshake", var2);
               }
            } else if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
               return HandshakeStatus.NEED_WRAP;
            } else {
               this.session
                  .handshakeFinished(
                     SSL.getSessionId(this.ssl),
                     SSL.getCipherForSSL(this.ssl),
                     SSL.getVersion(this.ssl),
                     SSL.getPeerCertificate(this.ssl),
                     SSL.getPeerCertChain(this.ssl),
                     SSL.getTime(this.ssl) * 1000L,
                     this.parentContext.sessionTimeout() * 1000L
                  );
               this.selectApplicationProtocol();
               return HandshakeStatus.FINISHED;
            }
         }
      }
   }

   private HandshakeStatus mayFinishHandshake(HandshakeStatus var1, int var2, int var3) throws SSLException {
      return (var1 != HandshakeStatus.NEED_UNWRAP || var3 <= 0) && (var1 != HandshakeStatus.NEED_WRAP || var2 <= 0)
         ? this.mayFinishHandshake(var1 != HandshakeStatus.FINISHED ? this.getHandshakeStatus() : HandshakeStatus.FINISHED)
         : this.handshake();
   }

   private HandshakeStatus mayFinishHandshake(HandshakeStatus var1) throws SSLException {
      if (var1 == HandshakeStatus.NOT_HANDSHAKING) {
         if (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED) {
            return this.handshake();
         }

         if (!this.isDestroyed() && SSL.bioLengthNonApplication(this.networkBIO) > 0) {
            return HandshakeStatus.NEED_WRAP;
         }
      }

      return var1;
   }

   @Override
   public final synchronized HandshakeStatus getHandshakeStatus() {
      if (this.needPendingStatus()) {
         return this.needTask ? HandshakeStatus.NEED_TASK : pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
      } else {
         return HandshakeStatus.NOT_HANDSHAKING;
      }
   }

   private HandshakeStatus getHandshakeStatus(int var1) {
      if (this.needPendingStatus()) {
         return this.needTask ? HandshakeStatus.NEED_TASK : pendingStatus(var1);
      } else {
         return HandshakeStatus.NOT_HANDSHAKING;
      }
   }

   private boolean needPendingStatus() {
      return this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.NOT_STARTED
         && !this.isDestroyed()
         && (this.handshakeState != ReferenceCountedOpenSslEngine.HandshakeState.FINISHED || this.isInboundDone() || this.isOutboundDone());
   }

   private String toJavaCipherSuite(String var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = SSL.getVersion(this.ssl);
         String var3 = toJavaCipherSuitePrefix(var2);
         return CipherSuiteConverter.toJava(var1, var3);
      }
   }

   private static String toJavaCipherSuitePrefix(String var0) {
      char var1;
      if (var0 != null && !var0.isEmpty()) {
         var1 = var0.charAt(0);
      } else {
         var1 = 0;
      }

      switch (var1) {
         case 'S':
            return "SSL";
         case 'T':
            return "TLS";
         default:
            return "UNKNOWN";
      }
   }

   @Override
   public final void setUseClientMode(boolean var1) {
      if (var1 != this.clientMode) {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final boolean getUseClientMode() {
      return this.clientMode;
   }

   @Override
   public final void setNeedClientAuth(boolean var1) {
      this.setClientAuth(var1 ? ClientAuth.REQUIRE : ClientAuth.NONE);
   }

   @Override
   public final boolean getNeedClientAuth() {
      return this.clientAuth == ClientAuth.REQUIRE;
   }

   @Override
   public final void setWantClientAuth(boolean var1) {
      this.setClientAuth(var1 ? ClientAuth.OPTIONAL : ClientAuth.NONE);
   }

   @Override
   public final boolean getWantClientAuth() {
      return this.clientAuth == ClientAuth.OPTIONAL;
   }

   public final synchronized void setVerify(int var1, int var2) {
      if (!this.isDestroyed()) {
         SSL.setVerify(this.ssl, var1, var2);
      }
   }

   private void setClientAuth(ClientAuth var1) {
      if (!this.clientMode) {
         synchronized (this) {
            if (this.clientAuth != var1) {
               if (!this.isDestroyed()) {
                  switch (var1) {
                     case NONE:
                        SSL.setVerify(this.ssl, 0, 10);
                        break;
                     case REQUIRE:
                        SSL.setVerify(this.ssl, 2, 10);
                        break;
                     case OPTIONAL:
                        SSL.setVerify(this.ssl, 1, 10);
                        break;
                     default:
                        throw new Error(var1.toString());
                  }
               }

               this.clientAuth = var1;
            }
         }
      }
   }

   @Override
   public final void setEnableSessionCreation(boolean var1) {
      if (var1) {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final boolean getEnableSessionCreation() {
      return false;
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   public final synchronized SSLParameters getSSLParameters() {
      SSLParameters var1 = super.getSSLParameters();
      int var2 = PlatformDependent.javaVersion();
      if (var2 >= 7) {
         var1.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
         Java7SslParametersUtils.setAlgorithmConstraints(var1, this.algorithmConstraints);
         if (var2 >= 8) {
            if (this.sniHostNames != null) {
               Java8SslUtils.setSniHostNames(var1, this.sniHostNames);
            }

            if (!this.isDestroyed()) {
               Java8SslUtils.setUseCipherSuitesOrder(var1, (SSL.getOptions(this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0);
            }

            Java8SslUtils.setSNIMatchers(var1, this.matchers);
         }
      }

      return var1;
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   public final synchronized void setSSLParameters(SSLParameters var1) {
      int var2 = PlatformDependent.javaVersion();
      if (var2 >= 7) {
         if (var1.getAlgorithmConstraints() != null) {
            throw new IllegalArgumentException("AlgorithmConstraints are not supported.");
         }

         boolean var3 = this.isDestroyed();
         if (var2 >= 8) {
            if (!var3) {
               if (this.clientMode) {
                  List var4 = Java8SslUtils.getSniHostNames(var1);

                  for (String var6 : var4) {
                     SSL.setTlsExtHostName(this.ssl, var6);
                  }

                  this.sniHostNames = var4;
               }

               if (Java8SslUtils.getUseCipherSuitesOrder(var1)) {
                  SSL.setOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
               } else {
                  SSL.clearOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
               }
            }

            this.matchers = var1.getSNIMatchers();
         }

         String var7 = var1.getEndpointIdentificationAlgorithm();
         if (!var3 && this.clientMode && isEndPointVerificationEnabled(var7)) {
            SSL.setVerify(this.ssl, 2, -1);
         }

         this.endPointIdentificationAlgorithm = var7;
         this.algorithmConstraints = var1.getAlgorithmConstraints();
      }

      super.setSSLParameters(var1);
   }

   private static boolean isEndPointVerificationEnabled(String var0) {
      return var0 != null && !var0.isEmpty();
   }

   private boolean isDestroyed() {
      return this.destroyed;
   }

   final boolean checkSniHostnameMatch(byte[] var1) {
      return Java8SslUtils.checkSniHostnameMatch(this.matchers, var1);
   }

   @Override
   public String getNegotiatedApplicationProtocol() {
      return this.applicationProtocol;
   }

   private static long bufferAddress(ByteBuffer var0) {
      assert var0.isDirect();

      return PlatformDependent.hasUnsafe() ? PlatformDependent.directBufferAddress(var0) : io.netty.internal.tcnative.Buffer.address(var0);
   }

   private void selectApplicationProtocol() throws SSLException {
      ApplicationProtocolConfig.SelectedListenerFailureBehavior var1 = this.apn.selectedListenerFailureBehavior();
      List var2 = this.apn.protocols();
      switch (this.apn.protocol()) {
         case NONE:
            break;
         case ALPN:
            String var5 = SSL.getAlpnSelected(this.ssl);
            if (var5 != null) {
               this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var5);
            }
            break;
         case NPN:
            String var4 = SSL.getNextProtoNegotiated(this.ssl);
            if (var4 != null) {
               this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var4);
            }
            break;
         case NPN_AND_ALPN:
            String var3 = SSL.getAlpnSelected(this.ssl);
            if (var3 == null) {
               var3 = SSL.getNextProtoNegotiated(this.ssl);
            }

            if (var3 != null) {
               this.applicationProtocol = this.selectApplicationProtocol(var2, var1, var3);
            }
            break;
         default:
            throw new Error();
      }
   }

   private String selectApplicationProtocol(List<String> var1, ApplicationProtocolConfig.SelectedListenerFailureBehavior var2, String var3) throws SSLException {
      if (var2 == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
         return var3;
      } else {
         int var4 = var1.size();

         assert var4 > 0;

         if (var1.contains(var3)) {
            return var3;
         } else if (var2 == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
            return (String)var1.get(var4 - 1);
         } else {
            throw new SSLException("unknown protocol " + var3);
         }
      }
   }

   final void setSessionId(OpenSslSessionId var1) {
      this.session.setSessionId(var1);
   }

   private final class AsyncTaskDecorator extends ReferenceCountedOpenSslEngine.TaskDecorator<AsyncTask> implements AsyncRunnable {
      AsyncTaskDecorator(AsyncTask var2) {
         super(var2);
      }

      @Override
      public void run(Runnable var1) {
         if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
            this.task.runAsync(ReferenceCountedOpenSslEngine.this.new TaskDecorator<>(var1));
         }
      }
   }

   private final class DefaultOpenSslSession implements OpenSslSession {
      private final OpenSslSessionContext sessionContext;
      private X509Certificate[] x509PeerCerts;
      private Certificate[] peerCerts;
      private boolean valid = true;
      private String protocol;
      private String cipher;
      private OpenSslSessionId id = OpenSslSessionId.NULL_ID;
      private volatile long creationTime;
      private volatile int applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
      private volatile Certificate[] localCertificateChain;
      private Map<String, Object> values;

      DefaultOpenSslSession(OpenSslSessionContext var2) {
         this.sessionContext = var2;
      }

      private SSLSessionBindingEvent newSSLSessionBindingEvent(String var1) {
         return new SSLSessionBindingEvent(ReferenceCountedOpenSslEngine.this.session, var1);
      }

      @Override
      public void setSessionId(OpenSslSessionId var1) {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            if (this.id == OpenSslSessionId.NULL_ID) {
               this.id = var1;
               this.creationTime = System.currentTimeMillis();
            }
         }
      }

      @Override
      public OpenSslSessionId sessionId() {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            if (this.id == OpenSslSessionId.NULL_ID && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               byte[] var2 = SSL.getSessionId(ReferenceCountedOpenSslEngine.this.ssl);
               if (var2 != null) {
                  this.id = new OpenSslSessionId(var2);
               }
            }

            return this.id;
         }
      }

      @Override
      public void setLocalCertificate(Certificate[] var1) {
         this.localCertificateChain = var1;
      }

      @Override
      public byte[] getId() {
         return this.sessionId().cloneBytes();
      }

      @Override
      public OpenSslSessionContext getSessionContext() {
         return this.sessionContext;
      }

      @Override
      public long getCreationTime() {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            return this.creationTime;
         }
      }

      @Override
      public long getLastAccessedTime() {
         long var1 = ReferenceCountedOpenSslEngine.this.lastAccessed;
         return var1 == -1L ? this.getCreationTime() : var1;
      }

      @Override
      public void invalidate() {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            this.valid = false;
            this.sessionContext.removeFromCache(this.id);
         }
      }

      @Override
      public boolean isValid() {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            return this.valid || this.sessionContext.isInCache(this.id);
         }
      }

      @Override
      public void putValue(String var1, Object var2) {
         ObjectUtil.checkNotNull(var1, "name");
         ObjectUtil.checkNotNull(var2, "value");
         Object var3;
         synchronized (this) {
            Object var5 = this.values;
            if (var5 == null) {
               var5 = this.values = new HashMap<>(2);
            }

            var3 = var5.put(var1, var2);
         }

         if (var2 instanceof SSLSessionBindingListener) {
            ((SSLSessionBindingListener)var2).valueBound(this.newSSLSessionBindingEvent(var1));
         }

         this.notifyUnbound(var3, var1);
      }

      @Override
      public Object getValue(String var1) {
         ObjectUtil.checkNotNull(var1, "name");
         synchronized (this) {
            return this.values == null ? null : this.values.get(var1);
         }
      }

      @Override
      public void removeValue(String var1) {
         ObjectUtil.checkNotNull(var1, "name");
         Object var2;
         synchronized (this) {
            Map var4 = this.values;
            if (var4 == null) {
               return;
            }

            var2 = var4.remove(var1);
         }

         this.notifyUnbound(var2, var1);
      }

      @Override
      public String[] getValueNames() {
         synchronized (this) {
            Map var2 = this.values;
            return var2 != null && !var2.isEmpty() ? var2.keySet().toArray(new String[0]) : EmptyArrays.EMPTY_STRINGS;
         }
      }

      private void notifyUnbound(Object var1, String var2) {
         if (var1 instanceof SSLSessionBindingListener) {
            ((SSLSessionBindingListener)var1).valueUnbound(this.newSSLSessionBindingEvent(var2));
         }
      }

      @Override
      public void handshakeFinished(byte[] var1, String var2, String var3, byte[] var4, byte[][] var5, long var6, long var8) throws SSLException {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
               this.creationTime = var6;
               if (this.id == OpenSslSessionId.NULL_ID) {
                  this.id = var1 == null ? OpenSslSessionId.NULL_ID : new OpenSslSessionId(var1);
               }

               this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(var2);
               this.protocol = var3;
               if (ReferenceCountedOpenSslEngine.this.clientMode) {
                  if (ReferenceCountedOpenSslEngine.isEmpty(var5)) {
                     this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                     this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                  } else {
                     this.peerCerts = new Certificate[var5.length];
                     this.x509PeerCerts = new X509Certificate[var5.length];
                     this.initCerts(var5, 0);
                  }
               } else if (ReferenceCountedOpenSslEngine.isEmpty(var4)) {
                  this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                  this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
               } else if (ReferenceCountedOpenSslEngine.isEmpty(var5)) {
                  this.peerCerts = new Certificate[]{new LazyX509Certificate(var4)};
                  this.x509PeerCerts = new X509Certificate[]{new LazyJavaxX509Certificate(var4)};
               } else {
                  this.peerCerts = new Certificate[var5.length + 1];
                  this.x509PeerCerts = new X509Certificate[var5.length + 1];
                  this.peerCerts[0] = new LazyX509Certificate(var4);
                  this.x509PeerCerts[0] = new LazyJavaxX509Certificate(var4);
                  this.initCerts(var5, 1);
               }

               ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
               ReferenceCountedOpenSslEngine.this.handshakeState = ReferenceCountedOpenSslEngine.HandshakeState.FINISHED;
            } else {
               throw new SSLException("Already closed");
            }
         }
      }

      private void initCerts(byte[][] var1, int var2) {
         for (int var3 = 0; var3 < var1.length; var3++) {
            int var4 = var2 + var3;
            this.peerCerts[var4] = new LazyX509Certificate(var1[var3]);
            this.x509PeerCerts[var4] = new LazyJavaxX509Certificate(var1[var3]);
         }
      }

      @Override
      public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            if (ReferenceCountedOpenSslEngine.isEmpty(this.peerCerts)) {
               throw new SSLPeerUnverifiedException("peer not verified");
            } else {
               return (Certificate[])this.peerCerts.clone();
            }
         }
      }

      @Override
      public Certificate[] getLocalCertificates() {
         Certificate[] var1 = this.localCertificateChain;
         return var1 == null ? null : (Certificate[])var1.clone();
      }

      @Override
      public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            if (ReferenceCountedOpenSslEngine.isEmpty(this.x509PeerCerts)) {
               throw new SSLPeerUnverifiedException("peer not verified");
            } else {
               return (X509Certificate[])this.x509PeerCerts.clone();
            }
         }
      }

      @Override
      public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
         Certificate[] var1 = this.getPeerCertificates();
         return ((java.security.cert.X509Certificate)var1[0]).getSubjectX500Principal();
      }

      @Override
      public Principal getLocalPrincipal() {
         Certificate[] var1 = this.localCertificateChain;
         return var1 != null && var1.length != 0 ? ((java.security.cert.X509Certificate)var1[0]).getSubjectX500Principal() : null;
      }

      @Override
      public String getCipherSuite() {
         synchronized (ReferenceCountedOpenSslEngine.this) {
            return this.cipher == null ? "SSL_NULL_WITH_NULL_NULL" : this.cipher;
         }
      }

      @Override
      public String getProtocol() {
         String var1 = this.protocol;
         if (var1 == null) {
            synchronized (ReferenceCountedOpenSslEngine.this) {
               if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                  var1 = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
               } else {
                  var1 = "";
               }
            }
         }

         return var1;
      }

      @Override
      public String getPeerHost() {
         return ReferenceCountedOpenSslEngine.this.getPeerHost();
      }

      @Override
      public int getPeerPort() {
         return ReferenceCountedOpenSslEngine.this.getPeerPort();
      }

      @Override
      public int getPacketBufferSize() {
         return ReferenceCountedOpenSslEngine.this.maxEncryptedPacketLength();
      }

      @Override
      public int getApplicationBufferSize() {
         return this.applicationBufferSize;
      }

      @Override
      public void tryExpandApplicationBufferSize(int var1) {
         if (var1 > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE) {
            this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
         }
      }

      @Override
      public String toString() {
         return "DefaultOpenSslSession{sessionContext=" + this.sessionContext + ", id=" + this.id + '}';
      }
   }

   private static enum HandshakeState {
      NOT_STARTED,
      STARTED_IMPLICITLY,
      STARTED_EXPLICITLY,
      FINISHED;
   }

   private class TaskDecorator<R extends Runnable> implements Runnable {
      protected final R task;

      TaskDecorator(R var2) {
         this.task = (R)var2;
      }

      @Override
      public void run() {
         ReferenceCountedOpenSslEngine.this.runAndResetNeedTask(this.task);
      }
   }
}
