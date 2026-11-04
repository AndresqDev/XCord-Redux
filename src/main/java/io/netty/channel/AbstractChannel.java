package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.ChannelOutputShutdownEvent;
import io.netty.channel.socket.ChannelOutputShutdownException;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
   private final Channel parent;
   private final ChannelId id;
   private final Channel.Unsafe unsafe;
   private final DefaultChannelPipeline pipeline;
   private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
   private final AbstractChannel.CloseFuture closeFuture = new AbstractChannel.CloseFuture(this);
   private volatile SocketAddress localAddress;
   private volatile SocketAddress remoteAddress;
   private volatile EventLoop eventLoop;
   private volatile boolean registered;
   private boolean closeInitiated;
   private Throwable initialCloseCause;
   private boolean strValActive;
   private String strVal;

   protected AbstractChannel(Channel var1) {
      this.parent = var1;
      this.id = this.newId();
      this.unsafe = this.newUnsafe();
      this.pipeline = this.newChannelPipeline();
   }

   protected AbstractChannel(Channel var1, ChannelId var2) {
      this.parent = var1;
      this.id = var2;
      this.unsafe = this.newUnsafe();
      this.pipeline = this.newChannelPipeline();
   }

   protected final int maxMessagesPerWrite() {
      ChannelConfig var1 = this.config();
      if (var1 instanceof DefaultChannelConfig) {
         return ((DefaultChannelConfig)var1).getMaxMessagesPerWrite();
      } else {
         Integer var2 = var1.getOption(ChannelOption.MAX_MESSAGES_PER_WRITE);
         return var2 == null ? Integer.MAX_VALUE : var2;
      }
   }

   @Override
   public final ChannelId id() {
      return this.id;
   }

   protected ChannelId newId() {
      return DefaultChannelId.newInstance();
   }

   protected DefaultChannelPipeline newChannelPipeline() {
      return new DefaultChannelPipeline(this);
   }

   @Override
   public boolean isWritable() {
      ChannelOutboundBuffer var1 = this.unsafe.outboundBuffer();
      return var1 != null && var1.isWritable();
   }

   @Override
   public long bytesBeforeUnwritable() {
      ChannelOutboundBuffer var1 = this.unsafe.outboundBuffer();
      return var1 != null ? var1.bytesBeforeUnwritable() : 0L;
   }

   @Override
   public long bytesBeforeWritable() {
      ChannelOutboundBuffer var1 = this.unsafe.outboundBuffer();
      return var1 != null ? var1.bytesBeforeWritable() : Long.MAX_VALUE;
   }

   @Override
   public Channel parent() {
      return this.parent;
   }

   @Override
   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.config().getAllocator();
   }

   @Override
   public EventLoop eventLoop() {
      EventLoop var1 = this.eventLoop;
      if (var1 == null) {
         throw new IllegalStateException("channel not registered to an event loop");
      } else {
         return var1;
      }
   }

   @Override
   public SocketAddress localAddress() {
      SocketAddress var1 = this.localAddress;
      if (var1 == null) {
         try {
            this.localAddress = var1 = this.unsafe().localAddress();
         } catch (Error var3) {
            throw var3;
         } catch (Throwable var4) {
            return null;
         }
      }

      return var1;
   }

   @Deprecated
   protected void invalidateLocalAddress() {
      this.localAddress = null;
   }

   @Override
   public SocketAddress remoteAddress() {
      SocketAddress var1 = this.remoteAddress;
      if (var1 == null) {
         try {
            this.remoteAddress = var1 = this.unsafe().remoteAddress();
         } catch (Error var3) {
            throw var3;
         } catch (Throwable var4) {
            return null;
         }
      }

      return var1;
   }

   @Deprecated
   protected void invalidateRemoteAddress() {
      this.remoteAddress = null;
   }

   @Override
   public boolean isRegistered() {
      return this.registered;
   }

   @Override
   public ChannelFuture bind(SocketAddress var1) {
      return this.pipeline.bind(var1);
   }

   @Override
   public ChannelFuture connect(SocketAddress var1) {
      return this.pipeline.connect(var1);
   }

   @Override
   public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
      return this.pipeline.connect(var1, var2);
   }

   @Override
   public ChannelFuture disconnect() {
      return this.pipeline.disconnect();
   }

   @Override
   public ChannelFuture close() {
      return this.pipeline.close();
   }

   @Override
   public ChannelFuture deregister() {
      return this.pipeline.deregister();
   }

   @Override
   public Channel flush() {
      this.pipeline.flush();
      return this;
   }

   @Override
   public ChannelFuture bind(SocketAddress var1, ChannelPromise var2) {
      return this.pipeline.bind(var1, var2);
   }

   @Override
   public ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
      return this.pipeline.connect(var1, var2);
   }

   @Override
   public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
      return this.pipeline.connect(var1, var2, var3);
   }

   @Override
   public ChannelFuture disconnect(ChannelPromise var1) {
      return this.pipeline.disconnect(var1);
   }

   @Override
   public ChannelFuture close(ChannelPromise var1) {
      return this.pipeline.close(var1);
   }

   @Override
   public ChannelFuture deregister(ChannelPromise var1) {
      return this.pipeline.deregister(var1);
   }

   @Override
   public Channel read() {
      this.pipeline.read();
      return this;
   }

   @Override
   public ChannelFuture write(Object var1) {
      return this.pipeline.write(var1);
   }

   @Override
   public ChannelFuture write(Object var1, ChannelPromise var2) {
      return this.pipeline.write(var1, var2);
   }

   @Override
   public ChannelFuture writeAndFlush(Object var1) {
      return this.pipeline.writeAndFlush(var1);
   }

   @Override
   public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
      return this.pipeline.writeAndFlush(var1, var2);
   }

   @Override
   public ChannelPromise newPromise() {
      return this.pipeline.newPromise();
   }

   @Override
   public ChannelProgressivePromise newProgressivePromise() {
      return this.pipeline.newProgressivePromise();
   }

   @Override
   public ChannelFuture newSucceededFuture() {
      return this.pipeline.newSucceededFuture();
   }

   @Override
   public ChannelFuture newFailedFuture(Throwable var1) {
      return this.pipeline.newFailedFuture(var1);
   }

   @Override
   public ChannelFuture closeFuture() {
      return this.closeFuture;
   }

   @Override
   public Channel.Unsafe unsafe() {
      return this.unsafe;
   }

   protected abstract AbstractChannel.AbstractUnsafe newUnsafe();

   @Override
   public final int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public final boolean equals(Object var1) {
      return this == var1;
   }

   public final int compareTo(Channel var1) {
      return this == var1 ? 0 : this.id().compareTo(var1.id());
   }

   @Override
   public String toString() {
      boolean var1 = this.isActive();
      if (this.strValActive == var1 && this.strVal != null) {
         return this.strVal;
      } else {
         SocketAddress var2 = this.remoteAddress();
         SocketAddress var3 = this.localAddress();
         if (var2 != null) {
            StringBuilder var4 = new StringBuilder(96)
               .append("[id: 0x")
               .append(this.id.asShortText())
               .append(", L:")
               .append(var3)
               .append(var1 ? " - " : " ! ")
               .append("R:")
               .append(var2)
               .append(']');
            this.strVal = var4.toString();
         } else if (var3 != null) {
            StringBuilder var5 = new StringBuilder(64).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(var3).append(']');
            this.strVal = var5.toString();
         } else {
            StringBuilder var6 = new StringBuilder(16).append("[id: 0x").append(this.id.asShortText()).append(']');
            this.strVal = var6.toString();
         }

         this.strValActive = var1;
         return this.strVal;
      }
   }

   @Override
   public final ChannelPromise voidPromise() {
      return this.pipeline.voidPromise();
   }

   protected abstract boolean isCompatible(EventLoop var1);

   protected abstract SocketAddress localAddress0();

   protected abstract SocketAddress remoteAddress0();

   protected void doRegister() throws Exception {
   }

   protected abstract void doBind(SocketAddress var1) throws Exception;

   protected abstract void doDisconnect() throws Exception;

   protected abstract void doClose() throws Exception;

   protected void doShutdownOutput() throws Exception {
      this.doClose();
   }

   protected void doDeregister() throws Exception {
   }

   protected abstract void doBeginRead() throws Exception;

   protected abstract void doWrite(ChannelOutboundBuffer var1) throws Exception;

   protected Object filterOutboundMessage(Object var1) throws Exception {
      return var1;
   }

   protected void validateFileRegion(DefaultFileRegion var1, long var2) throws IOException {
      DefaultFileRegion.validate(var1, var2);
   }

   protected abstract class AbstractUnsafe implements Channel.Unsafe {
      private volatile ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
      private RecvByteBufAllocator.Handle recvHandle;
      private boolean inFlush0;
      private boolean neverRegistered = true;

      private void assertEventLoop() {
         assert !AbstractChannel.this.registered || AbstractChannel.this.eventLoop.inEventLoop();
      }

      @Override
      public RecvByteBufAllocator.Handle recvBufAllocHandle() {
         if (this.recvHandle == null) {
            this.recvHandle = AbstractChannel.this.config().<RecvByteBufAllocator>getRecvByteBufAllocator().newHandle();
         }

         return this.recvHandle;
      }

      @Override
      public final ChannelOutboundBuffer outboundBuffer() {
         return this.outboundBuffer;
      }

      @Override
      public final SocketAddress localAddress() {
         return AbstractChannel.this.localAddress0();
      }

      @Override
      public final SocketAddress remoteAddress() {
         return AbstractChannel.this.remoteAddress0();
      }

      @Override
      public final void register(EventLoop var1, final ChannelPromise var2) {
         ObjectUtil.checkNotNull(var1, "eventLoop");
         if (AbstractChannel.this.isRegistered()) {
            var2.setFailure(new IllegalStateException("registered to an event loop already"));
         } else if (!AbstractChannel.this.isCompatible(var1)) {
            var2.setFailure(new IllegalStateException("incompatible event loop type: " + var1.getClass().getName()));
         } else {
            AbstractChannel.this.eventLoop = var1;
            if (var1.inEventLoop()) {
               this.register0(var2);
            } else {
               try {
                  var1.execute(new Runnable() {
                     @Override
                     public void run() {
                        AbstractUnsafe.this.register0(var2);
                     }
                  });
               } catch (Throwable var4) {
                  AbstractChannel.logger
                     .warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", AbstractChannel.this, var4);
                  this.closeForcibly();
                  AbstractChannel.this.closeFuture.setClosed();
                  this.safeSetFailure(var2, var4);
               }
            }
         }
      }

      private void register0(ChannelPromise var1) {
         try {
            if (!var1.setUncancellable() || !this.ensureOpen(var1)) {
               return;
            }

            boolean var2 = this.neverRegistered;
            AbstractChannel.this.doRegister();
            this.neverRegistered = false;
            AbstractChannel.this.registered = true;
            AbstractChannel.this.pipeline.invokeHandlerAddedIfNeeded();
            this.safeSetSuccess(var1);
            AbstractChannel.this.pipeline.fireChannelRegistered();
            if (AbstractChannel.this.isActive()) {
               if (var2) {
                  AbstractChannel.this.pipeline.fireChannelActive();
               } else if (AbstractChannel.this.config().isAutoRead()) {
                  this.beginRead();
               }
            }
         } catch (Throwable var3) {
            this.closeForcibly();
            AbstractChannel.this.closeFuture.setClosed();
            this.safeSetFailure(var1, var3);
         }
      }

      @Override
      public final void bind(SocketAddress var1, ChannelPromise var2) {
         this.assertEventLoop();
         if (var2.setUncancellable() && this.ensureOpen(var2)) {
            if (Boolean.TRUE.equals(AbstractChannel.this.config().getOption(ChannelOption.SO_BROADCAST))
               && var1 instanceof InetSocketAddress
               && !((InetSocketAddress)var1).getAddress().isAnyLocalAddress()
               && !PlatformDependent.isWindows()
               && !PlatformDependent.maybeSuperUser()) {
               AbstractChannel.logger
                  .warn(
                     "A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address ("
                        + var1
                        + ") anyway as requested."
                  );
            }

            boolean var3 = AbstractChannel.this.isActive();

            try {
               AbstractChannel.this.doBind(var1);
            } catch (Throwable var5) {
               this.safeSetFailure(var2, var5);
               this.closeIfClosed();
               return;
            }

            if (!var3 && AbstractChannel.this.isActive()) {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     AbstractChannel.this.pipeline.fireChannelActive();
                  }
               });
            }

            this.safeSetSuccess(var2);
         }
      }

      @Override
      public final void disconnect(ChannelPromise var1) {
         this.assertEventLoop();
         if (var1.setUncancellable()) {
            boolean var2 = AbstractChannel.this.isActive();

            try {
               AbstractChannel.this.doDisconnect();
               AbstractChannel.this.remoteAddress = null;
               AbstractChannel.this.localAddress = null;
            } catch (Throwable var4) {
               this.safeSetFailure(var1, var4);
               this.closeIfClosed();
               return;
            }

            if (var2 && !AbstractChannel.this.isActive()) {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     AbstractChannel.this.pipeline.fireChannelInactive();
                  }
               });
            }

            this.safeSetSuccess(var1);
            this.closeIfClosed();
         }
      }

      @Override
      public void close(ChannelPromise var1) {
         this.assertEventLoop();
         StacklessClosedChannelException var2 = StacklessClosedChannelException.newInstance(AbstractChannel.class, "close(ChannelPromise)");
         this.close(var1, var2, var2, false);
      }

      public final void shutdownOutput(ChannelPromise var1) {
         this.assertEventLoop();
         this.shutdownOutput(var1, null);
      }

      private void shutdownOutput(ChannelPromise var1, Throwable var2) {
         if (var1.setUncancellable()) {
            ChannelOutboundBuffer var3 = this.outboundBuffer;
            if (var3 == null) {
               var1.setFailure(new ClosedChannelException());
            } else {
               this.outboundBuffer = null;
               ChannelOutputShutdownException var4 = var2 == null
                  ? new ChannelOutputShutdownException("Channel output shutdown")
                  : new ChannelOutputShutdownException("Channel output shutdown", var2);

               try {
                  AbstractChannel.this.doShutdownOutput();
                  var1.setSuccess();
               } catch (Throwable var9) {
                  var1.setFailure(var9);
               } finally {
                  this.closeOutboundBufferForShutdown(AbstractChannel.this.pipeline, var3, var4);
               }
            }
         }
      }

      private void closeOutboundBufferForShutdown(ChannelPipeline var1, ChannelOutboundBuffer var2, Throwable var3) {
         var2.failFlushed(var3, false);
         var2.close(var3, true);
         var1.fireUserEventTriggered(ChannelOutputShutdownEvent.INSTANCE);
      }

      private void close(final ChannelPromise var1, final Throwable var2, final ClosedChannelException var3, final boolean var4) {
         if (var1.setUncancellable()) {
            if (AbstractChannel.this.closeInitiated) {
               if (AbstractChannel.this.closeFuture.isDone()) {
                  this.safeSetSuccess(var1);
               } else if (!(var1 instanceof VoidChannelPromise)) {
                  AbstractChannel.this.closeFuture.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1x) throws Exception {
                        var1.setSuccess();
                     }
                  });
               }
            } else {
               AbstractChannel.this.closeInitiated = true;
               final boolean var5 = AbstractChannel.this.isActive();
               final ChannelOutboundBuffer var6 = this.outboundBuffer;
               this.outboundBuffer = null;
               Executor var7 = this.prepareToClose();
               if (var7 != null) {
                  var7.execute(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           AbstractUnsafe.this.doClose0(var1);
                        } finally {
                           AbstractUnsafe.this.invokeLater(new Runnable() {
                              @Override
                              public void run() {
                                 if (var6 != null) {
                                    var6.failFlushed(var2, var4);
                                    var6.close(var3);
                                 }

                                 AbstractUnsafe.this.fireChannelInactiveAndDeregister(var5);
                              }
                           });
                        }
                     }
                  });
               } else {
                  try {
                     this.doClose0(var1);
                  } finally {
                     if (var6 != null) {
                        var6.failFlushed(var2, var4);
                        var6.close(var3);
                     }
                  }

                  if (this.inFlush0) {
                     this.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           AbstractUnsafe.this.fireChannelInactiveAndDeregister(var5);
                        }
                     });
                  } else {
                     this.fireChannelInactiveAndDeregister(var5);
                  }
               }
            }
         }
      }

      private void doClose0(ChannelPromise var1) {
         try {
            AbstractChannel.this.doClose();
            AbstractChannel.this.closeFuture.setClosed();
            this.safeSetSuccess(var1);
         } catch (Throwable var3) {
            AbstractChannel.this.closeFuture.setClosed();
            this.safeSetFailure(var1, var3);
         }
      }

      private void fireChannelInactiveAndDeregister(boolean var1) {
         this.deregister(this.voidPromise(), var1 && !AbstractChannel.this.isActive());
      }

      @Override
      public final void closeForcibly() {
         this.assertEventLoop();

         try {
            AbstractChannel.this.doClose();
         } catch (Exception var2) {
            AbstractChannel.logger.warn("Failed to close a channel.", (Throwable)var2);
         }
      }

      @Override
      public final void deregister(ChannelPromise var1) {
         this.assertEventLoop();
         this.deregister(var1, false);
      }

      private void deregister(final ChannelPromise var1, final boolean var2) {
         if (var1.setUncancellable()) {
            if (!AbstractChannel.this.registered) {
               this.safeSetSuccess(var1);
            } else {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        AbstractChannel.this.doDeregister();
                     } catch (Throwable var5) {
                        AbstractChannel.logger.warn("Unexpected exception occurred while deregistering a channel.", var5);
                     } finally {
                        if (var2) {
                           AbstractChannel.this.pipeline.fireChannelInactive();
                        }

                        if (AbstractChannel.this.registered) {
                           AbstractChannel.this.registered = false;
                           AbstractChannel.this.pipeline.fireChannelUnregistered();
                        }

                        AbstractUnsafe.this.safeSetSuccess(var1);
                     }
                  }
               });
            }
         }
      }

      @Override
      public final void beginRead() {
         this.assertEventLoop();

         try {
            AbstractChannel.this.doBeginRead();
         } catch (final Exception var2) {
            this.invokeLater(new Runnable() {
               @Override
               public void run() {
                  AbstractChannel.this.pipeline.fireExceptionCaught(var2);
               }
            });
            this.close(this.voidPromise());
         }
      }

      @Override
      public final void write(Object var1, ChannelPromise var2) {
         this.assertEventLoop();
         ChannelOutboundBuffer var3 = this.outboundBuffer;
         if (var3 == null) {
            try {
               ReferenceCountUtil.release(var1);
            } finally {
               this.safeSetFailure(var2, this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "write(Object, ChannelPromise)"));
            }
         } else {
            int var4;
            try {
               var1 = AbstractChannel.this.filterOutboundMessage(var1);
               var4 = AbstractChannel.this.pipeline.estimatorHandle().size(var1);
               if (var4 < 0) {
                  var4 = 0;
               }
            } catch (Throwable var15) {
               try {
                  ReferenceCountUtil.release(var1);
               } finally {
                  this.safeSetFailure(var2, var15);
               }

               return;
            }

            var3.addMessage(var1, var4, var2);
         }
      }

      @Override
      public final void flush() {
         this.assertEventLoop();
         ChannelOutboundBuffer var1 = this.outboundBuffer;
         if (var1 != null) {
            var1.addFlush();
            this.flush0();
         }
      }

      protected void flush0() {
         if (!this.inFlush0) {
            ChannelOutboundBuffer var1 = this.outboundBuffer;
            if (var1 != null && !var1.isEmpty()) {
               this.inFlush0 = true;
               if (AbstractChannel.this.isActive()) {
                  try {
                     AbstractChannel.this.doWrite(var1);
                  } catch (Throwable var10) {
                     this.handleWriteError(var10);
                  } finally {
                     this.inFlush0 = false;
                  }
               } else {
                  try {
                     if (!var1.isEmpty()) {
                        if (AbstractChannel.this.isOpen()) {
                           var1.failFlushed(new NotYetConnectedException(), true);
                        } else {
                           var1.failFlushed(this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "flush0()"), false);
                        }
                     }
                  } finally {
                     this.inFlush0 = false;
                  }
               }
            }
         }
      }

      protected final void handleWriteError(Throwable var1) {
         if (var1 instanceof IOException && AbstractChannel.this.config().isAutoClose()) {
            AbstractChannel.this.initialCloseCause = var1;
            this.close(this.voidPromise(), var1, this.newClosedChannelException(var1, "flush0()"), false);
         } else {
            try {
               this.shutdownOutput(this.voidPromise(), var1);
            } catch (Throwable var3) {
               AbstractChannel.this.initialCloseCause = var1;
               this.close(this.voidPromise(), var3, this.newClosedChannelException(var1, "flush0()"), false);
            }
         }
      }

      private ClosedChannelException newClosedChannelException(Throwable var1, String var2) {
         StacklessClosedChannelException var3 = StacklessClosedChannelException.newInstance(AbstractChannel.AbstractUnsafe.class, var2);
         if (var1 != null) {
            var3.initCause(var1);
         }

         return var3;
      }

      @Override
      public final ChannelPromise voidPromise() {
         this.assertEventLoop();
         return AbstractChannel.this.unsafeVoidPromise;
      }

      protected final boolean ensureOpen(ChannelPromise var1) {
         if (AbstractChannel.this.isOpen()) {
            return true;
         } else {
            this.safeSetFailure(var1, this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "ensureOpen(ChannelPromise)"));
            return false;
         }
      }

      protected final void safeSetSuccess(ChannelPromise var1) {
         if (!(var1 instanceof VoidChannelPromise) && !var1.trySuccess()) {
            AbstractChannel.logger.warn("Failed to mark a promise as success because it is done already: {}", var1);
         }
      }

      protected final void safeSetFailure(ChannelPromise var1, Throwable var2) {
         if (!(var1 instanceof VoidChannelPromise) && !var1.tryFailure(var2)) {
            AbstractChannel.logger.warn("Failed to mark a promise as failure because it's done already: {}", var1, var2);
         }
      }

      protected final void closeIfClosed() {
         if (!AbstractChannel.this.isOpen()) {
            this.close(this.voidPromise());
         }
      }

      private void invokeLater(Runnable var1) {
         try {
            AbstractChannel.this.eventLoop().execute(var1);
         } catch (RejectedExecutionException var3) {
            AbstractChannel.logger.warn("Can't invoke task later as EventLoop rejected it", (Throwable)var3);
         }
      }

      protected final Throwable annotateConnectException(Throwable var1, SocketAddress var2) {
         if (var1 instanceof ConnectException) {
            return new AbstractChannel.AnnotatedConnectException((ConnectException)var1, var2);
         } else if (var1 instanceof NoRouteToHostException) {
            return new AbstractChannel.AnnotatedNoRouteToHostException((NoRouteToHostException)var1, var2);
         } else {
            return (Throwable)(var1 instanceof SocketException ? new AbstractChannel.AnnotatedSocketException((SocketException)var1, var2) : var1);
         }
      }

      protected Executor prepareToClose() {
         return null;
      }
   }

   private static final class AnnotatedConnectException extends ConnectException {
      private static final long serialVersionUID = 3901958112696433556L;

      AnnotatedConnectException(ConnectException var1, SocketAddress var2) {
         super(var1.getMessage() + ": " + var2);
         this.initCause(var1);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   private static final class AnnotatedNoRouteToHostException extends NoRouteToHostException {
      private static final long serialVersionUID = -6801433937592080623L;

      AnnotatedNoRouteToHostException(NoRouteToHostException var1, SocketAddress var2) {
         super(var1.getMessage() + ": " + var2);
         this.initCause(var1);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   private static final class AnnotatedSocketException extends SocketException {
      private static final long serialVersionUID = 3896743275010454039L;

      AnnotatedSocketException(SocketException var1, SocketAddress var2) {
         super(var1.getMessage() + ": " + var2);
         this.initCause(var1);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   static final class CloseFuture extends DefaultChannelPromise {
      CloseFuture(AbstractChannel var1) {
         super(var1);
      }

      @Override
      public ChannelPromise setSuccess() {
         throw new IllegalStateException();
      }

      @Override
      public ChannelPromise setFailure(Throwable var1) {
         throw new IllegalStateException();
      }

      @Override
      public boolean trySuccess() {
         throw new IllegalStateException();
      }

      @Override
      public boolean tryFailure(Throwable var1) {
         throw new IllegalStateException();
      }

      boolean setClosed() {
         return super.trySuccess();
      }
   }
}
