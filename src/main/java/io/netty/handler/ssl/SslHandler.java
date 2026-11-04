package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.unix.UnixChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
   private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
   private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
   private static final int STATE_SENT_FIRST_MESSAGE = 1;
   private static final int STATE_FLUSHED_BEFORE_HANDSHAKE = 2;
   private static final int STATE_READ_DURING_HANDSHAKE = 4;
   private static final int STATE_HANDSHAKE_STARTED = 8;
   private static final int STATE_NEEDS_FLUSH = 16;
   private static final int STATE_OUTBOUND_CLOSED = 32;
   private static final int STATE_CLOSE_NOTIFY = 64;
   private static final int STATE_PROCESS_TASK = 128;
   private static final int STATE_FIRE_CHANNEL_READ = 256;
   private static final int STATE_UNWRAP_REENTRY = 512;
   private static final int MAX_PLAINTEXT_LENGTH = 16384;
   private volatile ChannelHandlerContext ctx;
   private final SSLEngine engine;
   private final SslHandler.SslEngineType engineType;
   private final Executor delegatedTaskExecutor;
   private final boolean jdkCompatibilityMode;
   private final ByteBuffer[] singleBuffer = new ByteBuffer[1];
   private final boolean startTls;
   private final SslHandler.SslTasksRunner sslTaskRunnerForUnwrap = new SslHandler.SslTasksRunner(true);
   private final SslHandler.SslTasksRunner sslTaskRunner = new SslHandler.SslTasksRunner(false);
   private SslHandler.SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
   private Promise<Channel> handshakePromise = new SslHandler.LazyChannelPromise();
   private final SslHandler.LazyChannelPromise sslClosePromise = new SslHandler.LazyChannelPromise();
   private int packetLength;
   private short state;
   private volatile long handshakeTimeoutMillis = 10000L;
   private volatile long closeNotifyFlushTimeoutMillis = 3000L;
   private volatile long closeNotifyReadTimeoutMillis;
   volatile int wrapDataSize = 16384;

   public SslHandler(SSLEngine var1) {
      this(var1, false);
   }

   public SslHandler(SSLEngine var1, boolean var2) {
      this(var1, var2, ImmediateExecutor.INSTANCE);
   }

   public SslHandler(SSLEngine var1, Executor var2) {
      this(var1, false, var2);
   }

   public SslHandler(SSLEngine var1, boolean var2, Executor var3) {
      this.engine = ObjectUtil.checkNotNull(var1, "engine");
      this.delegatedTaskExecutor = ObjectUtil.checkNotNull(var3, "delegatedTaskExecutor");
      this.engineType = SslHandler.SslEngineType.forEngine(var1);
      this.startTls = var2;
      this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(var1);
      this.setCumulator(this.engineType.cumulator);
   }

   public long getHandshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public void setHandshakeTimeout(long var1, TimeUnit var3) {
      ObjectUtil.checkNotNull(var3, "unit");
      this.setHandshakeTimeoutMillis(var3.toMillis(var1));
   }

   public void setHandshakeTimeoutMillis(long var1) {
      this.handshakeTimeoutMillis = ObjectUtil.checkPositiveOrZero(var1, "handshakeTimeoutMillis");
   }

   public final void setWrapDataSize(int var1) {
      this.wrapDataSize = var1;
   }

   @Deprecated
   public long getCloseNotifyTimeoutMillis() {
      return this.getCloseNotifyFlushTimeoutMillis();
   }

   @Deprecated
   public void setCloseNotifyTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyFlushTimeout(var1, var3);
   }

   @Deprecated
   public void setCloseNotifyTimeoutMillis(long var1) {
      this.setCloseNotifyFlushTimeoutMillis(var1);
   }

   public final long getCloseNotifyFlushTimeoutMillis() {
      return this.closeNotifyFlushTimeoutMillis;
   }

   public final void setCloseNotifyFlushTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyFlushTimeoutMillis(var3.toMillis(var1));
   }

   public final void setCloseNotifyFlushTimeoutMillis(long var1) {
      this.closeNotifyFlushTimeoutMillis = ObjectUtil.checkPositiveOrZero(var1, "closeNotifyFlushTimeoutMillis");
   }

   public final long getCloseNotifyReadTimeoutMillis() {
      return this.closeNotifyReadTimeoutMillis;
   }

   public final void setCloseNotifyReadTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyReadTimeoutMillis(var3.toMillis(var1));
   }

   public final void setCloseNotifyReadTimeoutMillis(long var1) {
      this.closeNotifyReadTimeoutMillis = ObjectUtil.checkPositiveOrZero(var1, "closeNotifyReadTimeoutMillis");
   }

   public SSLEngine engine() {
      return this.engine;
   }

   public String applicationProtocol() {
      SSLEngine var1 = this.engine();
      return !(var1 instanceof ApplicationProtocolAccessor) ? null : ((ApplicationProtocolAccessor)var1).getNegotiatedApplicationProtocol();
   }

   public Future<Channel> handshakeFuture() {
      return this.handshakePromise;
   }

   @Deprecated
   public ChannelFuture close() {
      return this.closeOutbound();
   }

   @Deprecated
   public ChannelFuture close(ChannelPromise var1) {
      return this.closeOutbound(var1);
   }

   public ChannelFuture closeOutbound() {
      return this.closeOutbound(this.ctx.newPromise());
   }

   public ChannelFuture closeOutbound(final ChannelPromise var1) {
      ChannelHandlerContext var2 = this.ctx;
      if (var2.executor().inEventLoop()) {
         this.closeOutbound0(var1);
      } else {
         var2.executor().execute(new Runnable() {
            @Override
            public void run() {
               SslHandler.this.closeOutbound0(var1);
            }
         });
      }

      return var1;
   }

   private void closeOutbound0(ChannelPromise var1) {
      this.setState(32);
      this.engine.closeOutbound();

      try {
         this.flush(this.ctx, var1);
      } catch (Exception var3) {
         if (!var1.tryFailure(var3)) {
            logger.warn("{} flush() raised a masked exception.", this.ctx.channel(), var3);
         }
      }
   }

   public Future<Channel> sslCloseFuture() {
      return this.sslClosePromise;
   }

   @Override
   public void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      try {
         if (this.pendingUnencryptedWrites != null && !this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.releaseAndFailAll(var1, new ChannelException("Pending write on removal of SslHandler"));
         }

         this.pendingUnencryptedWrites = null;
         Object var2 = null;
         if (!this.handshakePromise.isDone()) {
            var2 = new SSLHandshakeException("SslHandler removed before handshake completed");
            if (this.handshakePromise.tryFailure((Throwable)var2)) {
               var1.fireUserEventTriggered(new SslHandshakeCompletionEvent((Throwable)var2));
            }
         }

         if (!this.sslClosePromise.isDone()) {
            if (var2 == null) {
               var2 = new SSLException("SslHandler removed before SSLEngine was closed");
            }

            this.notifyClosePromise((Throwable)var2);
         }
      } finally {
         ReferenceCountUtil.release(this.engine);
      }
   }

   @Override
   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   @Override
   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   @Override
   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.closeOutboundAndChannel(var1, var2, true);
   }

   @Override
   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.closeOutboundAndChannel(var1, var2, false);
   }

   @Override
   public void read(ChannelHandlerContext var1) throws Exception {
      if (!this.handshakePromise.isDone()) {
         this.setState(4);
      }

      var1.read();
   }

   private static IllegalStateException newPendingWritesNullException() {
      return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (!(var2 instanceof ByteBuf)) {
         UnsupportedMessageTypeException var4 = new UnsupportedMessageTypeException(var2, ByteBuf.class);
         ReferenceCountUtil.safeRelease(var2);
         var3.setFailure(var4);
      } else if (this.pendingUnencryptedWrites == null) {
         ReferenceCountUtil.safeRelease(var2);
         var3.setFailure(newPendingWritesNullException());
      } else {
         this.pendingUnencryptedWrites.add((ByteBuf)var2, var3);
      }
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.startTls && !this.isStateSet(1)) {
         this.setState(1);
         this.pendingUnencryptedWrites.writeAndRemoveAll(var1);
         this.forceFlush(var1);
         this.startHandshakeProcessing(true);
      } else if (!this.isStateSet(128)) {
         try {
            this.wrapAndFlush(var1);
         } catch (Throwable var3) {
            this.setHandshakeFailure(var1, var3);
            PlatformDependent.throwException(var3);
         }
      }
   }

   private void wrapAndFlush(ChannelHandlerContext var1) throws SSLException {
      if (this.pendingUnencryptedWrites.isEmpty()) {
         this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, var1.newPromise());
      }

      if (!this.handshakePromise.isDone()) {
         this.setState(2);
      }

      try {
         this.wrap(var1, false);
      } finally {
         this.forceFlush(var1);
      }
   }

   private void wrap(ChannelHandlerContext var1, boolean var2) throws SSLException {
      ByteBuf var3 = null;
      ByteBufAllocator var4 = var1.alloc();

      try {
         int var5 = this.wrapDataSize;

         while (!var1.isRemoved()) {
            ChannelPromise var6 = var1.newPromise();
            ByteBuf var7 = var5 > 0 ? this.pendingUnencryptedWrites.remove(var4, var5, var6) : this.pendingUnencryptedWrites.removeFirst(var6);
            if (var7 == null) {
               return;
            }

            if (var3 == null) {
               var3 = this.allocateOutNetBuf(var1, var7.readableBytes(), var7.nioBufferCount());
            }

            SSLEngineResult var8 = this.wrap(var4, this.engine, var7, var3);
            if (var7.isReadable()) {
               this.pendingUnencryptedWrites.addFirst(var7, var6);
               var6 = null;
            } else {
               var7.release();
            }

            if (var3.isReadable()) {
               ByteBuf var9 = var3;
               var3 = null;
               if (var6 != null) {
                  var1.write(var9, var6);
               } else {
                  var1.write(var9);
               }
            } else if (var6 != null) {
               var1.write(Unpooled.EMPTY_BUFFER, var6);
            }

            if (var8.getStatus() == Status.CLOSED) {
               Object var13 = this.handshakePromise.cause();
               if (var13 == null) {
                  var13 = this.sslClosePromise.cause();
                  if (var13 == null) {
                     var13 = new SslClosedEngineException("SSLEngine closed already");
                  }
               }

               this.pendingUnencryptedWrites.releaseAndFailAll(var1, (Throwable)var13);
               return;
            }

            switch (var8.getHandshakeStatus()) {
               case NEED_TASK:
                  if (!this.runDelegatedTasks(var2)) {
                     return;
                  }
                  break;
               case FINISHED:
               case NOT_HANDSHAKING:
                  this.setHandshakeSuccess();
                  break;
               case NEED_WRAP:
                  if (var8.bytesProduced() > 0 && this.pendingUnencryptedWrites.isEmpty()) {
                     this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER);
                  }
                  break;
               case NEED_UNWRAP:
                  this.readIfNeeded(var1);
                  return;
               default:
                  throw new IllegalStateException("Unknown handshake status: " + var8.getHandshakeStatus());
            }
         }
      } finally {
         if (var3 != null) {
            var3.release();
         }

         if (var2) {
            this.setState(16);
         }
      }
   }

   private boolean wrapNonAppData(final ChannelHandlerContext var1, boolean var2) throws SSLException {
      ByteBuf var3 = null;
      ByteBufAllocator var4 = var1.alloc();

      try {
         while (!var1.isRemoved()) {
            if (var3 == null) {
               var3 = this.allocateOutNetBuf(var1, 2048, 1);
            }

            SSLEngineResult var5 = this.wrap(var4, this.engine, Unpooled.EMPTY_BUFFER, var3);
            if (var5.bytesProduced() > 0) {
               var1.write(var3).addListener(new ChannelFutureListener() {
                  public void operationComplete(ChannelFuture var1x) {
                     Throwable var2 = var1x.cause();
                     if (var2 != null) {
                        SslHandler.this.setHandshakeFailureTransportFailure(var1, var2);
                     }
                  }
               });
               if (var2) {
                  this.setState(16);
               }

               var3 = null;
            }

            HandshakeStatus var6 = var5.getHandshakeStatus();
            switch (var6) {
               case NEED_TASK:
                  if (!this.runDelegatedTasks(var2)) {
                     return false;
                  }
                  break;
               case FINISHED:
                  if (this.setHandshakeSuccess() && var2 && !this.pendingUnencryptedWrites.isEmpty()) {
                     this.wrap(var1, true);
                  }

                  return false;
               case NOT_HANDSHAKING:
                  if (this.setHandshakeSuccess() && var2 && !this.pendingUnencryptedWrites.isEmpty()) {
                     this.wrap(var1, true);
                  }

                  if (!var2) {
                     this.unwrapNonAppData(var1);
                  }

                  return true;
               case NEED_WRAP:
                  break;
               case NEED_UNWRAP:
                  if (var2 || this.unwrapNonAppData(var1) <= 0) {
                     return false;
                  }
                  break;
               default:
                  throw new IllegalStateException("Unknown handshake status: " + var5.getHandshakeStatus());
            }

            if (var5.bytesProduced() == 0 && var6 != HandshakeStatus.NEED_TASK
               || var5.bytesConsumed() == 0 && var5.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
               return false;
            }
         }

         return false;
      } finally {
         if (var3 != null) {
            var3.release();
         }
      }
   }

   private SSLEngineResult wrap(ByteBufAllocator var1, SSLEngine var2, ByteBuf var3, ByteBuf var4) throws SSLException {
      ByteBuf var5 = null;

      try {
         int var6 = var3.readerIndex();
         int var7 = var3.readableBytes();
         ByteBuffer[] var8;
         if (!var3.isDirect() && this.engineType.wantsDirectBuffer) {
            var5 = var1.directBuffer(var7);
            var5.writeBytes(var3, var6, var7);
            var8 = this.singleBuffer;
            var8[0] = var5.internalNioBuffer(var5.readerIndex(), var7);
         } else if (!(var3 instanceof CompositeByteBuf) && var3.nioBufferCount() == 1) {
            var8 = this.singleBuffer;
            var8[0] = var3.internalNioBuffer(var6, var7);
         } else {
            var8 = var3.nioBuffers();
         }

         while (true) {
            ByteBuffer var9 = var4.nioBuffer(var4.writerIndex(), var4.writableBytes());
            SSLEngineResult var10 = var2.wrap(var8, var9);
            var3.skipBytes(var10.bytesConsumed());
            var4.writerIndex(var4.writerIndex() + var10.bytesProduced());
            if (var10.getStatus() != Status.BUFFER_OVERFLOW) {
               return var10;
            }

            var4.ensureWritable(var2.getSession().getPacketBufferSize());
         }
      } finally {
         this.singleBuffer[0] = null;
         if (var5 != null) {
            var5.release();
         }
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      boolean var2 = this.handshakePromise.cause() != null;
      ClosedChannelException var3 = new ClosedChannelException();
      this.setHandshakeFailure(var1, var3, !this.isStateSet(32), this.isStateSet(8), false);
      this.notifyClosePromise(var3);

      try {
         super.channelInactive(var1);
      } catch (DecoderException var5) {
         if (!var2 || !(var5.getCause() instanceof SSLException)) {
            throw var5;
         }
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.ignoreException(var2)) {
         if (logger.isDebugEnabled()) {
            logger.debug(
               "{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify",
               var1.channel(),
               var2
            );
         }

         if (var1.channel().isActive()) {
            var1.close();
         }
      } else {
         var1.fireExceptionCaught(var2);
      }
   }

   private boolean ignoreException(Throwable var1) {
      if (!(var1 instanceof SSLException) && var1 instanceof IOException && this.sslClosePromise.isDone()) {
         String var2 = var1.getMessage();
         if (var2 != null && IGNORABLE_ERROR_MESSAGE.matcher(var2).matches()) {
            return true;
         }

         StackTraceElement[] var3 = var1.getStackTrace();

         for (StackTraceElement var7 : var3) {
            String var8 = var7.getClassName();
            String var9 = var7.getMethodName();
            if (!var8.startsWith("io.netty.") && "read".equals(var9)) {
               if (IGNORABLE_CLASS_IN_STACK.matcher(var8).matches()) {
                  return true;
               }

               try {
                  Class var10 = PlatformDependent.getClassLoader(this.getClass()).loadClass(var8);
                  if (SocketChannel.class.isAssignableFrom(var10) || DatagramChannel.class.isAssignableFrom(var10)) {
                     return true;
                  }

                  if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(var10.getSuperclass().getName())) {
                     return true;
                  }
               } catch (Throwable var11) {
                  if (logger.isDebugEnabled()) {
                     logger.debug("Unexpected exception while loading class {} classname {}", this.getClass(), var8, var11);
                  }
               }
            }
         }
      }

      return false;
   }

   public static boolean isEncrypted(ByteBuf var0) {
      if (var0.readableBytes() < 5) {
         throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
      } else {
         return SslUtils.getEncryptedPacketLength(var0, var0.readerIndex()) != -2;
      }
   }

   private void decodeJdkCompatible(ChannelHandlerContext var1, ByteBuf var2) throws NotSslRecordException {
      int var3 = this.packetLength;
      if (var3 > 0) {
         if (var2.readableBytes() < var3) {
            return;
         }
      } else {
         int var4 = var2.readableBytes();
         if (var4 < 5) {
            return;
         }

         var3 = SslUtils.getEncryptedPacketLength(var2, var2.readerIndex());
         if (var3 == -2) {
            NotSslRecordException var5 = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(var2));
            var2.skipBytes(var2.readableBytes());
            this.setHandshakeFailure(var1, var5);
            throw var5;
         }

         assert var3 > 0;

         if (var3 > var4) {
            this.packetLength = var3;
            return;
         }
      }

      this.packetLength = 0;

      try {
         int var7 = this.unwrap(var1, var2, var3);

         assert var7 == var3 || this.engine.isInboundDone() : "we feed the SSLEngine a packets worth of data: " + var3 + " but it only consumed: " + var7;
      } catch (Throwable var6) {
         this.handleUnwrapThrowable(var1, var6);
      }
   }

   private void decodeNonJdkCompatible(ChannelHandlerContext var1, ByteBuf var2) {
      try {
         this.unwrap(var1, var2, var2.readableBytes());
      } catch (Throwable var4) {
         this.handleUnwrapThrowable(var1, var4);
      }
   }

   private void handleUnwrapThrowable(ChannelHandlerContext var1, Throwable var2) {
      try {
         if (this.handshakePromise.tryFailure(var2)) {
            var1.fireUserEventTriggered(new SslHandshakeCompletionEvent(var2));
         }

         if (this.pendingUnencryptedWrites != null) {
            this.wrapAndFlush(var1);
         }
      } catch (SSLException var7) {
         logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", (Throwable)var7);
      } finally {
         this.setHandshakeFailure(var1, var2, true, false, true);
      }

      PlatformDependent.throwException(var2);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws SSLException {
      if (!this.isStateSet(128)) {
         if (this.jdkCompatibilityMode) {
            this.decodeJdkCompatible(var1, var2);
         } else {
            this.decodeNonJdkCompatible(var1, var2);
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.channelReadComplete0(var1);
   }

   private void channelReadComplete0(ChannelHandlerContext var1) {
      this.discardSomeReadBytes();
      this.flushIfNeeded(var1);
      this.readIfNeeded(var1);
      this.clearState(256);
      var1.fireChannelReadComplete();
   }

   private void readIfNeeded(ChannelHandlerContext var1) {
      if (!var1.channel().config().isAutoRead() && (!this.isStateSet(256) || !this.handshakePromise.isDone())) {
         var1.read();
      }
   }

   private void flushIfNeeded(ChannelHandlerContext var1) {
      if (this.isStateSet(16)) {
         this.forceFlush(var1);
      }
   }

   private int unwrapNonAppData(ChannelHandlerContext var1) throws SSLException {
      return this.unwrap(var1, Unpooled.EMPTY_BUFFER, 0);
   }

   private int unwrap(ChannelHandlerContext var1, ByteBuf var2, int var3) throws SSLException {
      int var4 = var3;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      ByteBuf var8 = this.allocate(var1, var3);

      try {
         do {
            SSLEngineResult var9 = this.engineType.unwrap(this, var2, var3, var8);
            Status var10 = var9.getStatus();
            HandshakeStatus var11 = var9.getHandshakeStatus();
            int var12 = var9.bytesProduced();
            int var13 = var9.bytesConsumed();
            var2.skipBytes(var13);
            var3 -= var13;
            if (var11 == HandshakeStatus.FINISHED || var11 == HandshakeStatus.NOT_HANDSHAKING) {
               var5 |= (var8.isReadable() ? this.setHandshakeSuccessUnwrapMarkReentry() : this.setHandshakeSuccess()) || var11 == HandshakeStatus.FINISHED;
            }

            if (var8.isReadable()) {
               this.setState(256);
               if (this.isStateSet(512)) {
                  var7 = true;
                  this.executeChannelRead(var1, var8);
               } else {
                  var1.fireChannelRead(var8);
               }

               var8 = null;
            }

            if (var10 == Status.CLOSED) {
               var6 = true;
            } else if (var10 == Status.BUFFER_OVERFLOW) {
               if (var8 != null) {
                  var8.release();
               }

               int var18 = this.engine.getSession().getApplicationBufferSize();
               var8 = this.allocate(var1, this.engineType.calculatePendingData(this, var18 < var12 ? var18 : var18 - var12));
               continue;
            }

            if (var11 == HandshakeStatus.NEED_TASK) {
               boolean var14 = this.runDelegatedTasks(true);
               if (!var14) {
                  var5 = false;
                  break;
               }
            } else if (var11 == HandshakeStatus.NEED_WRAP && this.wrapNonAppData(var1, true) && var3 == 0) {
               break;
            }

            if (var10 == Status.BUFFER_UNDERFLOW
               || var11 != HandshakeStatus.NEED_TASK && (var13 == 0 && var12 == 0 || var3 == 0 && var11 == HandshakeStatus.NOT_HANDSHAKING)) {
               if (var11 == HandshakeStatus.NEED_UNWRAP) {
                  this.readIfNeeded(var1);
               }
               break;
            }

            if (var8 == null) {
               var8 = this.allocate(var1, var3);
            }
         } while (!var1.isRemoved());

         if (this.isStateSet(2) && this.handshakePromise.isDone()) {
            this.clearState(2);
            var5 = true;
         }

         if (var5) {
            this.wrap(var1, true);
         }
      } finally {
         if (var8 != null) {
            var8.release();
         }

         if (var6) {
            if (var7) {
               this.executeNotifyClosePromise(var1);
            } else {
               this.notifyClosePromise(null);
            }
         }
      }

      return var4 - var3;
   }

   private boolean setHandshakeSuccessUnwrapMarkReentry() {
      boolean var1 = !this.isStateSet(512);
      if (var1) {
         this.setState(512);
      }

      boolean var2;
      try {
         var2 = this.setHandshakeSuccess();
      } finally {
         if (var1) {
            this.clearState(512);
         }
      }

      return var2;
   }

   private void executeNotifyClosePromise(ChannelHandlerContext var1) {
      try {
         var1.executor().execute(new Runnable() {
            @Override
            public void run() {
               SslHandler.this.notifyClosePromise(null);
            }
         });
      } catch (RejectedExecutionException var3) {
         this.notifyClosePromise(var3);
      }
   }

   private void executeChannelRead(final ChannelHandlerContext var1, final ByteBuf var2) {
      try {
         var1.executor().execute(new Runnable() {
            @Override
            public void run() {
               var1.fireChannelRead(var2);
            }
         });
      } catch (RejectedExecutionException var4) {
         var2.release();
         throw var4;
      }
   }

   private static ByteBuffer toByteBuffer(ByteBuf var0, int var1, int var2) {
      return var0.nioBufferCount() == 1 ? var0.internalNioBuffer(var1, var2) : var0.nioBuffer(var1, var2);
   }

   private static boolean inEventLoop(Executor var0) {
      return var0 instanceof EventExecutor && ((EventExecutor)var0).inEventLoop();
   }

   private boolean runDelegatedTasks(boolean var1) {
      if (this.delegatedTaskExecutor != ImmediateExecutor.INSTANCE && !inEventLoop(this.delegatedTaskExecutor)) {
         this.executeDelegatedTask(var1);
         return false;
      } else {
         while (true) {
            Runnable var2 = this.engine.getDelegatedTask();
            if (var2 == null) {
               return true;
            }

            this.setState(128);
            if (var2 instanceof AsyncRunnable) {
               boolean var3 = false;

               try {
                  AsyncRunnable var4 = (AsyncRunnable)var2;
                  SslHandler.AsyncTaskCompletionHandler var5 = new SslHandler.AsyncTaskCompletionHandler(var1);
                  var4.run(var5);
                  var3 = var5.resumeLater();
                  if (var3) {
                     return false;
                  }
               } finally {
                  if (!var3) {
                     this.clearState(128);
                  }
               }
            } else {
               try {
                  var2.run();
               } finally {
                  this.clearState(128);
               }
            }
         }
      }
   }

   private SslHandler.SslTasksRunner getTaskRunner(boolean var1) {
      return var1 ? this.sslTaskRunnerForUnwrap : this.sslTaskRunner;
   }

   private void executeDelegatedTask(boolean var1) {
      this.executeDelegatedTask(this.getTaskRunner(var1));
   }

   private void executeDelegatedTask(SslHandler.SslTasksRunner var1) {
      this.setState(128);

      try {
         this.delegatedTaskExecutor.execute(var1);
      } catch (RejectedExecutionException var3) {
         this.clearState(128);
         throw var3;
      }
   }

   private boolean setHandshakeSuccess() {
      boolean var1;
      if (var1 = !this.handshakePromise.isDone() && this.handshakePromise.trySuccess(this.ctx.channel())) {
         if (logger.isDebugEnabled()) {
            SSLSession var2 = this.engine.getSession();
            logger.debug("{} HANDSHAKEN: protocol:{} cipher suite:{}", this.ctx.channel(), var2.getProtocol(), var2.getCipherSuite());
         }

         this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
      }

      if (this.isStateSet(4)) {
         this.clearState(4);
         if (!this.ctx.channel().config().isAutoRead()) {
            this.ctx.read();
         }
      }

      return var1;
   }

   private void setHandshakeFailure(ChannelHandlerContext var1, Throwable var2) {
      this.setHandshakeFailure(var1, var2, true, true, false);
   }

   private void setHandshakeFailure(ChannelHandlerContext var1, Throwable var2, boolean var3, boolean var4, boolean var5) {
      try {
         this.setState(32);
         this.engine.closeOutbound();
         if (var3) {
            try {
               this.engine.closeInbound();
            } catch (SSLException var11) {
               if (logger.isDebugEnabled()) {
                  String var7 = var11.getMessage();
                  if (var7 == null || !var7.contains("possible truncation attack") && !var7.contains("closing inbound before receiving peer's close_notify")) {
                     logger.debug("{} SSLEngine.closeInbound() raised an exception.", var1.channel(), var11);
                  }
               }
            }
         }

         if (this.handshakePromise.tryFailure(var2) || var5) {
            SslUtils.handleHandshakeFailure(var1, var2, var4);
         }
      } finally {
         this.releaseAndFailAll(var1, var2);
      }
   }

   private void setHandshakeFailureTransportFailure(ChannelHandlerContext var1, Throwable var2) {
      try {
         SSLException var3 = new SSLException("failure when writing TLS control frames", var2);
         this.releaseAndFailAll(var1, var3);
         if (this.handshakePromise.tryFailure(var3)) {
            var1.fireUserEventTriggered(new SslHandshakeCompletionEvent(var3));
         }
      } finally {
         var1.close();
      }
   }

   private void releaseAndFailAll(ChannelHandlerContext var1, Throwable var2) {
      if (this.pendingUnencryptedWrites != null) {
         this.pendingUnencryptedWrites.releaseAndFailAll(var1, var2);
      }
   }

   private void notifyClosePromise(Throwable var1) {
      if (var1 == null) {
         if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
            this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS);
         }
      } else if (this.sslClosePromise.tryFailure(var1)) {
         this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(var1));
      }
   }

   private void closeOutboundAndChannel(ChannelHandlerContext var1, final ChannelPromise var2, boolean var3) throws Exception {
      this.setState(32);
      this.engine.closeOutbound();
      if (!var1.channel().isActive()) {
         if (var3) {
            var1.disconnect(var2);
         } else {
            var1.close(var2);
         }
      } else {
         ChannelPromise var4 = var1.newPromise();

         try {
            this.flush(var1, var4);
         } finally {
            if (!this.isStateSet(64)) {
               this.setState(64);
               this.safeClose(var1, var4, PromiseNotifier.cascade(false, var1.newPromise(), var2));
            } else {
               this.sslClosePromise.addListener(new FutureListener<Channel>() {
                  @Override
                  public void operationComplete(Future<Channel> var1) {
                     var2.setSuccess();
                  }
               });
            }
         }
      }
   }

   private void flush(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.pendingUnencryptedWrites != null) {
         this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, var2);
      } else {
         var2.setFailure(newPendingWritesNullException());
      }

      this.flush(var1);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      Channel var2 = var1.channel();
      this.pendingUnencryptedWrites = new SslHandler.SslHandlerCoalescingBufferQueue(var2, 16);
      this.setOpensslEngineSocketFd(var2);
      boolean var3 = Boolean.TRUE.equals(var2.config().getOption(ChannelOption.TCP_FASTOPEN_CONNECT));
      boolean var4 = var2.isActive();
      if (var4 || var3) {
         this.startHandshakeProcessing(var4);
         ChannelOutboundBuffer var5;
         if (var3 && ((var5 = var2.unsafe().outboundBuffer()) == null || var5.totalPendingWriteBytes() > 0L)) {
            this.setState(16);
         }
      }
   }

   private void startHandshakeProcessing(boolean var1) {
      if (!this.isStateSet(8)) {
         this.setState(8);
         if (this.engine.getUseClientMode()) {
            this.handshake(var1);
         }

         this.applyHandshakeTimeout();
      } else if (this.isStateSet(16)) {
         this.forceFlush(this.ctx);
      }
   }

   public Future<Channel> renegotiate() {
      ChannelHandlerContext var1 = this.ctx;
      if (var1 == null) {
         throw new IllegalStateException();
      } else {
         return this.renegotiate(var1.executor().newPromise());
      }
   }

   public Future<Channel> renegotiate(final Promise<Channel> var1) {
      ObjectUtil.checkNotNull(var1, "promise");
      ChannelHandlerContext var2 = this.ctx;
      if (var2 == null) {
         throw new IllegalStateException();
      } else {
         EventExecutor var3 = var2.executor();
         if (!var3.inEventLoop()) {
            var3.execute(new Runnable() {
               @Override
               public void run() {
                  SslHandler.this.renegotiateOnEventLoop(var1);
               }
            });
            return var1;
         } else {
            this.renegotiateOnEventLoop(var1);
            return var1;
         }
      }
   }

   private void renegotiateOnEventLoop(Promise<Channel> var1) {
      Promise var2 = this.handshakePromise;
      if (!var2.isDone()) {
         PromiseNotifier.cascade(var2, var1);
      } else {
         this.handshakePromise = var1;
         this.handshake(true);
         this.applyHandshakeTimeout();
      }
   }

   private void handshake(boolean var1) {
      if (this.engine.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
         if (!this.handshakePromise.isDone()) {
            ChannelHandlerContext var2 = this.ctx;

            try {
               this.engine.beginHandshake();
               this.wrapNonAppData(var2, false);
            } catch (Throwable var7) {
               this.setHandshakeFailure(var2, var7);
            } finally {
               if (var1) {
                  this.forceFlush(var2);
               }
            }
         }
      }
   }

   private void applyHandshakeTimeout() {
      final Promise var1 = this.handshakePromise;
      final long var2 = this.handshakeTimeoutMillis;
      if (var2 > 0L && !var1.isDone()) {
         final ScheduledFuture var4 = this.ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
               if (!var1.isDone()) {
                  SslHandshakeTimeoutException var1x = new SslHandshakeTimeoutException("handshake timed out after " + var2 + "ms");

                  try {
                     if (var1.tryFailure(var1x)) {
                        SslUtils.handleHandshakeFailure(SslHandler.this.ctx, var1x, true);
                     }
                  } finally {
                     SslHandler.this.releaseAndFailAll(SslHandler.this.ctx, var1x);
                  }
               }
            }
         }, var2, TimeUnit.MILLISECONDS);
         var1.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> var1) throws Exception {
               var4.cancel(false);
            }
         });
      }
   }

   private void forceFlush(ChannelHandlerContext var1) {
      this.clearState(16);
      var1.flush();
   }

   private void setOpensslEngineSocketFd(Channel var1) {
      if (var1 instanceof UnixChannel && this.engine instanceof ReferenceCountedOpenSslEngine) {
         ((ReferenceCountedOpenSslEngine)this.engine).bioSetFd(((UnixChannel)var1).fd().intValue());
      }
   }

   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      this.setOpensslEngineSocketFd(var1.channel());
      if (!this.startTls) {
         this.startHandshakeProcessing(true);
      }

      var1.fireChannelActive();
   }

   private void safeClose(final ChannelHandlerContext var1, final ChannelFuture var2, final ChannelPromise var3) {
      if (!var1.channel().isActive()) {
         var1.close(var3);
      } else {
         final ScheduledFuture var4;
         if (!var2.isDone()) {
            long var5 = this.closeNotifyFlushTimeoutMillis;
            if (var5 > 0L) {
               var4 = var1.executor().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (!var2.isDone()) {
                        SslHandler.logger.warn("{} Last write attempt timed out; force-closing the connection.", var1.channel());
                        SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                     }
                  }
               }, var5, TimeUnit.MILLISECONDS);
            } else {
               var4 = null;
            }
         } else {
            var4 = null;
         }

         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) {
               if (var4 != null) {
                  var4.cancel(false);
               }

               final long var2 = SslHandler.this.closeNotifyReadTimeoutMillis;
               if (var2 <= 0L) {
                  SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
               } else {
                  final ScheduledFuture var4x;
                  if (!SslHandler.this.sslClosePromise.isDone()) {
                     var4x = var1.executor().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (!SslHandler.this.sslClosePromise.isDone()) {
                              SslHandler.logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", var1.channel(), var2);
                              SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                           }
                        }
                     }, var2, TimeUnit.MILLISECONDS);
                  } else {
                     var4x = null;
                  }

                  SslHandler.this.sslClosePromise.addListener(new FutureListener<Channel>() {
                     @Override
                     public void operationComplete(Future<Channel> var1x) throws Exception {
                        if (var4x != null) {
                           var4x.cancel(false);
                        }

                        SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                     }
                  });
               }
            }
         });
      }
   }

   private static void addCloseListener(ChannelFuture var0, ChannelPromise var1) {
      PromiseNotifier.cascade(false, var0, var1);
   }

   private ByteBuf allocate(ChannelHandlerContext var1, int var2) {
      ByteBufAllocator var3 = var1.alloc();
      return this.engineType.wantsDirectBuffer ? var3.directBuffer(var2) : var3.buffer(var2);
   }

   private ByteBuf allocateOutNetBuf(ChannelHandlerContext var1, int var2, int var3) {
      return this.engineType.allocateWrapBuffer(this, var1.alloc(), var2, var3);
   }

   private boolean isStateSet(int var1) {
      return (this.state & var1) == var1;
   }

   private void setState(int var1) {
      this.state = (short)(this.state | var1);
   }

   private void clearState(int var1) {
      this.state = (short)(this.state & ~var1);
   }

   private static boolean attemptCopyToCumulation(ByteBuf var0, ByteBuf var1, int var2) {
      int var3 = var1.readableBytes();
      int var4 = var0.capacity();
      if (var2 - var0.readableBytes() < var3
         || (!var0.isWritable(var3) || var4 < var2) && (var4 >= var2 || !ByteBufUtil.ensureWritableSuccess(var0.ensureWritable(var3, false)))) {
         return false;
      } else {
         var0.writeBytes(var1);
         var1.release();
         return true;
      }
   }

   private final class AsyncTaskCompletionHandler implements Runnable {
      private final boolean inUnwrap;
      boolean didRun;
      boolean resumeLater;

      AsyncTaskCompletionHandler(boolean var2) {
         this.inUnwrap = var2;
      }

      @Override
      public void run() {
         this.didRun = true;
         if (this.resumeLater) {
            SslHandler.this.getTaskRunner(this.inUnwrap).runComplete();
         }
      }

      boolean resumeLater() {
         if (!this.didRun) {
            this.resumeLater = true;
            return true;
         } else {
            return false;
         }
      }
   }

   private final class LazyChannelPromise extends DefaultPromise<Channel> {
      private LazyChannelPromise() {
      }

      @Override
      protected EventExecutor executor() {
         if (SslHandler.this.ctx == null) {
            throw new IllegalStateException();
         } else {
            return SslHandler.this.ctx.executor();
         }
      }

      @Override
      protected void checkDeadLock() {
         if (SslHandler.this.ctx != null) {
            super.checkDeadLock();
         }
      }
   }

   private static enum SslEngineType {
      TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
         @Override
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, ByteBuf var4) throws SSLException {
            int var5 = var2.nioBufferCount();
            int var6 = var4.writerIndex();
            SSLEngineResult var7;
            if (var5 > 1) {
               ReferenceCountedOpenSslEngine var8 = (ReferenceCountedOpenSslEngine)var1.engine;

               try {
                  var1.singleBuffer[0] = SslHandler.toByteBuffer(var4, var6, var4.writableBytes());
                  var7 = var8.unwrap(var2.nioBuffers(var2.readerIndex(), var3), var1.singleBuffer);
               } finally {
                  var1.singleBuffer[0] = null;
               }
            } else {
               var7 = var1.engine.unwrap(SslHandler.toByteBuffer(var2, var2.readerIndex(), var3), SslHandler.toByteBuffer(var4, var6, var4.writableBytes()));
            }

            var4.writerIndex(var6 + var7.bytesProduced());
            return var7;
         }

         @Override
         ByteBuf allocateWrapBuffer(SslHandler var1, ByteBufAllocator var2, int var3, int var4) {
            return var2.directBuffer(((ReferenceCountedOpenSslEngine)var1.engine).calculateMaxLengthForWrap(var3, var4));
         }

         @Override
         int calculatePendingData(SslHandler var1, int var2) {
            int var3 = ((ReferenceCountedOpenSslEngine)var1.engine).sslPending();
            return var3 > 0 ? var3 : var2;
         }

         @Override
         boolean jdkCompatibilityMode(SSLEngine var1) {
            return ((ReferenceCountedOpenSslEngine)var1).jdkCompatibilityMode;
         }
      },
      CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
         @Override
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, ByteBuf var4) throws SSLException {
            int var5 = var2.nioBufferCount();
            int var6 = var4.writerIndex();
            SSLEngineResult var7;
            if (var5 > 1) {
               try {
                  var1.singleBuffer[0] = SslHandler.toByteBuffer(var4, var6, var4.writableBytes());
                  var7 = ((ConscryptAlpnSslEngine)var1.engine).unwrap(var2.nioBuffers(var2.readerIndex(), var3), var1.singleBuffer);
               } finally {
                  var1.singleBuffer[0] = null;
               }
            } else {
               var7 = var1.engine.unwrap(SslHandler.toByteBuffer(var2, var2.readerIndex(), var3), SslHandler.toByteBuffer(var4, var6, var4.writableBytes()));
            }

            var4.writerIndex(var6 + var7.bytesProduced());
            return var7;
         }

         @Override
         ByteBuf allocateWrapBuffer(SslHandler var1, ByteBufAllocator var2, int var3, int var4) {
            return var2.directBuffer(((ConscryptAlpnSslEngine)var1.engine).calculateOutNetBufSize(var3, var4));
         }

         @Override
         int calculatePendingData(SslHandler var1, int var2) {
            return var2;
         }

         @Override
         boolean jdkCompatibilityMode(SSLEngine var1) {
            return true;
         }
      },
      JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR) {
         @Override
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, ByteBuf var4) throws SSLException {
            int var5 = var4.writerIndex();
            ByteBuffer var6 = SslHandler.toByteBuffer(var2, var2.readerIndex(), var3);
            int var7 = var6.position();
            SSLEngineResult var8 = var1.engine.unwrap(var6, SslHandler.toByteBuffer(var4, var5, var4.writableBytes()));
            var4.writerIndex(var5 + var8.bytesProduced());
            if (var8.bytesConsumed() == 0) {
               int var9 = var6.position() - var7;
               if (var9 != var8.bytesConsumed()) {
                  return new SSLEngineResult(var8.getStatus(), var8.getHandshakeStatus(), var9, var8.bytesProduced());
               }
            }

            return var8;
         }

         @Override
         ByteBuf allocateWrapBuffer(SslHandler var1, ByteBufAllocator var2, int var3, int var4) {
            return var2.heapBuffer(var1.engine.getSession().getPacketBufferSize());
         }

         @Override
         int calculatePendingData(SslHandler var1, int var2) {
            return var2;
         }

         @Override
         boolean jdkCompatibilityMode(SSLEngine var1) {
            return true;
         }
      };

      final boolean wantsDirectBuffer;
      final ByteToMessageDecoder.Cumulator cumulator;

      static SslHandler.SslEngineType forEngine(SSLEngine var0) {
         return var0 instanceof ReferenceCountedOpenSslEngine ? TCNATIVE : (var0 instanceof ConscryptAlpnSslEngine ? CONSCRYPT : JDK);
      }

      private SslEngineType(boolean var3, ByteToMessageDecoder.Cumulator var4) {
         this.wantsDirectBuffer = var3;
         this.cumulator = var4;
      }

      abstract SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, ByteBuf var4) throws SSLException;

      abstract int calculatePendingData(SslHandler var1, int var2);

      abstract boolean jdkCompatibilityMode(SSLEngine var1);

      abstract ByteBuf allocateWrapBuffer(SslHandler var1, ByteBufAllocator var2, int var3, int var4);
   }

   private final class SslHandlerCoalescingBufferQueue extends AbstractCoalescingBufferQueue {
      SslHandlerCoalescingBufferQueue(Channel var2, int var3) {
         super(var2, var3);
      }

      @Override
      protected ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         int var4 = SslHandler.this.wrapDataSize;
         if (!(var2 instanceof CompositeByteBuf)) {
            return SslHandler.attemptCopyToCumulation(var2, var3, var4) ? var2 : this.copyAndCompose(var1, var2, var3);
         } else {
            CompositeByteBuf var5 = (CompositeByteBuf)var2;
            int var6 = var5.numComponents();
            if (var6 == 0 || !SslHandler.attemptCopyToCumulation(var5.internalComponent(var6 - 1), var3, var4)) {
               var5.addComponent(true, var3);
            }

            return var5;
         }
      }

      @Override
      protected ByteBuf composeFirst(ByteBufAllocator var1, ByteBuf var2) {
         if (var2 instanceof CompositeByteBuf) {
            CompositeByteBuf var3 = (CompositeByteBuf)var2;
            if (SslHandler.this.engineType.wantsDirectBuffer) {
               var2 = var1.directBuffer(var3.readableBytes());
            } else {
               var2 = var1.heapBuffer(var3.readableBytes());
            }

            try {
               var2.writeBytes(var3);
            } catch (Throwable var5) {
               var2.release();
               PlatformDependent.throwException(var5);
            }

            var3.release();
         }

         return var2;
      }

      @Override
      protected ByteBuf removeEmptyValue() {
         return null;
      }
   }

   private final class SslTasksRunner implements Runnable {
      private final boolean inUnwrap;
      private final Runnable runCompleteTask = new Runnable() {
         @Override
         public void run() {
            SslTasksRunner.this.runComplete();
         }
      };

      SslTasksRunner(boolean var2) {
         this.inUnwrap = var2;
      }

      private void taskError(Throwable var1) {
         if (this.inUnwrap) {
            try {
               SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, var1);
            } catch (Throwable var3) {
               this.safeExceptionCaught(var3);
            }
         } else {
            SslHandler.this.setHandshakeFailure(SslHandler.this.ctx, var1);
            SslHandler.this.forceFlush(SslHandler.this.ctx);
         }
      }

      private void safeExceptionCaught(Throwable var1) {
         try {
            SslHandler.this.exceptionCaught(SslHandler.this.ctx, this.wrapIfNeeded(var1));
         } catch (Throwable var3) {
            SslHandler.this.ctx.fireExceptionCaught(var3);
         }
      }

      private Throwable wrapIfNeeded(Throwable var1) {
         if (!this.inUnwrap) {
            return var1;
         } else {
            return (Throwable)(var1 instanceof DecoderException ? var1 : new DecoderException(var1));
         }
      }

      private void tryDecodeAgain() {
         try {
            SslHandler.this.channelRead(SslHandler.this.ctx, Unpooled.EMPTY_BUFFER);
         } catch (Throwable var5) {
            this.safeExceptionCaught(var5);
         } finally {
            SslHandler.this.channelReadComplete0(SslHandler.this.ctx);
         }
      }

      private void resumeOnEventExecutor() {
         assert SslHandler.this.ctx.executor().inEventLoop();

         SslHandler.this.clearState(128);

         try {
            HandshakeStatus var1 = SslHandler.this.engine.getHandshakeStatus();
            switch (var1) {
               case NEED_TASK:
                  SslHandler.this.executeDelegatedTask(this);
                  break;
               case FINISHED:
               case NOT_HANDSHAKING:
                  SslHandler.this.setHandshakeSuccess();

                  try {
                     SslHandler.this.wrap(SslHandler.this.ctx, this.inUnwrap);
                  } catch (Throwable var4) {
                     this.taskError(var4);
                     return;
                  }

                  if (this.inUnwrap) {
                     SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                  }

                  SslHandler.this.forceFlush(SslHandler.this.ctx);
                  this.tryDecodeAgain();
                  break;
               case NEED_WRAP:
                  try {
                     if (!SslHandler.this.wrapNonAppData(SslHandler.this.ctx, false) && this.inUnwrap) {
                        SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                     }

                     SslHandler.this.forceFlush(SslHandler.this.ctx);
                  } catch (Throwable var5) {
                     this.taskError(var5);
                     return;
                  }

                  this.tryDecodeAgain();
                  break;
               case NEED_UNWRAP:
                  try {
                     SslHandler.this.unwrapNonAppData(SslHandler.this.ctx);
                  } catch (SSLException var3) {
                     SslHandler.this.handleUnwrapThrowable(SslHandler.this.ctx, var3);
                     return;
                  }

                  this.tryDecodeAgain();
                  break;
               default:
                  throw new AssertionError();
            }
         } catch (Throwable var6) {
            this.safeExceptionCaught(var6);
         }
      }

      void runComplete() {
         EventExecutor var1 = SslHandler.this.ctx.executor();
         var1.execute(new Runnable() {
            @Override
            public void run() {
               SslTasksRunner.this.resumeOnEventExecutor();
            }
         });
      }

      @Override
      public void run() {
         try {
            Runnable var1 = SslHandler.this.engine.getDelegatedTask();
            if (var1 == null) {
               return;
            }

            if (var1 instanceof AsyncRunnable) {
               AsyncRunnable var2 = (AsyncRunnable)var1;
               var2.run(this.runCompleteTask);
            } else {
               var1.run();
               this.runComplete();
            }
         } catch (Throwable var3) {
            this.handleException(var3);
         }
      }

      private void handleException(final Throwable var1) {
         EventExecutor var2 = SslHandler.this.ctx.executor();
         if (var2.inEventLoop()) {
            SslHandler.this.clearState(128);
            this.safeExceptionCaught(var1);
         } else {
            try {
               var2.execute(new Runnable() {
                  @Override
                  public void run() {
                     SslHandler.this.clearState(128);
                     SslTasksRunner.this.safeExceptionCaught(var1);
                  }
               });
            } catch (RejectedExecutionException var4) {
               SslHandler.this.clearState(128);
               SslHandler.this.ctx.fireExceptionCaught(var1);
            }
         }
      }
   }
}
