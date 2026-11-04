package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeUnit;

abstract class AbstractEpollChannel extends AbstractChannel implements UnixChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   final LinuxSocket socket;
   private ChannelPromise connectPromise;
   private Future<?> connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;
   private volatile SocketAddress local;
   private volatile SocketAddress remote;
   protected int flags = Native.EPOLLET;
   boolean inputClosedSeenErrorOnRead;
   boolean epollInReadyRunnablePending;
   protected volatile boolean active;

   AbstractEpollChannel(LinuxSocket var1) {
      this(null, var1, false);
   }

   AbstractEpollChannel(Channel var1, LinuxSocket var2, boolean var3) {
      super(var1);
      this.socket = ObjectUtil.checkNotNull(var2, "fd");
      this.active = var3;
      if (var3) {
         this.local = var2.localAddress();
         this.remote = var2.remoteAddress();
      }
   }

   AbstractEpollChannel(Channel var1, LinuxSocket var2, SocketAddress var3) {
      super(var1);
      this.socket = ObjectUtil.checkNotNull(var2, "fd");
      this.active = true;
      this.remote = var3;
      this.local = var2.localAddress();
   }

   static boolean isSoErrorZero(Socket var0) {
      try {
         return var0.getSoError() == 0;
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   void setFlag(int var1) throws IOException {
      if (!this.isFlagSet(var1)) {
         this.flags |= var1;
         this.modifyEvents();
      }
   }

   void clearFlag(int var1) throws IOException {
      if (this.isFlagSet(var1)) {
         this.flags &= ~var1;
         this.modifyEvents();
      }
   }

   boolean isFlagSet(int var1) {
      return (this.flags & var1) != 0;
   }

   @Override
   public final FileDescriptor fd() {
      return this.socket;
   }

   public abstract EpollChannelConfig config();

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   protected void doClose() throws Exception {
      this.active = false;
      this.inputClosedSeenErrorOnRead = true;

      try {
         ChannelPromise var1 = this.connectPromise;
         if (var1 != null) {
            var1.tryFailure(new ClosedChannelException());
            this.connectPromise = null;
         }

         Future var2 = this.connectTimeoutFuture;
         if (var2 != null) {
            var2.cancel(false);
            this.connectTimeoutFuture = null;
         }

         if (this.isRegistered()) {
            EventLoop var3 = this.eventLoop();
            if (var3.inEventLoop()) {
               this.doDeregister();
            } else {
               var3.execute(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        AbstractEpollChannel.this.doDeregister();
                     } catch (Throwable var2) {
                        AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
                     }
                  }
               });
            }
         }
      } finally {
         this.socket.close();
      }
   }

   void resetCachedAddresses() {
      this.local = this.socket.localAddress();
      this.remote = this.socket.remoteAddress();
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof EpollEventLoop;
   }

   @Override
   public boolean isOpen() {
      return this.socket.isOpen();
   }

   @Override
   protected void doDeregister() throws Exception {
      ((EpollEventLoop)this.eventLoop()).remove(this);
   }

   @Override
   protected final void doBeginRead() throws Exception {
      AbstractEpollChannel.AbstractEpollUnsafe var1 = (AbstractEpollChannel.AbstractEpollUnsafe)this.unsafe();
      var1.readPending = true;
      this.setFlag(Native.EPOLLIN);
      if (var1.maybeMoreDataToRead) {
         var1.executeEpollInReadyRunnable(this.config());
      }
   }

   final boolean shouldBreakEpollInReady(ChannelConfig var1) {
      return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(var1));
   }

   private static boolean isAllowHalfClosure(ChannelConfig var0) {
      return var0 instanceof EpollDomainSocketChannelConfig
         ? ((EpollDomainSocketChannelConfig)var0).isAllowHalfClosure()
         : var0 instanceof SocketChannelConfig && ((SocketChannelConfig)var0).isAllowHalfClosure();
   }

   final void clearEpollIn() {
      if (this.isRegistered()) {
         EventLoop var1 = this.eventLoop();
         final AbstractEpollChannel.AbstractEpollUnsafe var2 = (AbstractEpollChannel.AbstractEpollUnsafe)this.unsafe();
         if (var1.inEventLoop()) {
            var2.clearEpollIn0();
         } else {
            var1.execute(new Runnable() {
               @Override
               public void run() {
                  if (!var2.readPending && !AbstractEpollChannel.this.config().isAutoRead()) {
                     var2.clearEpollIn0();
                  }
               }
            });
         }
      } else {
         this.flags = this.flags & ~Native.EPOLLIN;
      }
   }

   private void modifyEvents() throws IOException {
      if (this.isOpen() && this.isRegistered()) {
         ((EpollEventLoop)this.eventLoop()).modify(this);
      }
   }

   @Override
   protected void doRegister() throws Exception {
      this.epollInReadyRunnablePending = false;
      ((EpollEventLoop)this.eventLoop()).add(this);
   }

   protected abstract AbstractEpollChannel.AbstractEpollUnsafe newUnsafe();

   protected final ByteBuf newDirectBuffer(ByteBuf var1) {
      return this.newDirectBuffer(var1, var1);
   }

   protected final ByteBuf newDirectBuffer(Object var1, ByteBuf var2) {
      int var3 = var2.readableBytes();
      if (var3 == 0) {
         ReferenceCountUtil.release(var1);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator var4 = this.alloc();
         if (var4.isDirectBufferPooled()) {
            return newDirectBuffer0(var1, var2, var4, var3);
         } else {
            ByteBuf var5 = ByteBufUtil.threadLocalDirectBuffer();
            if (var5 == null) {
               return newDirectBuffer0(var1, var2, var4, var3);
            } else {
               var5.writeBytes(var2, var2.readerIndex(), var3);
               ReferenceCountUtil.safeRelease(var1);
               return var5;
            }
         }
      }
   }

   private static ByteBuf newDirectBuffer0(Object var0, ByteBuf var1, ByteBufAllocator var2, int var3) {
      ByteBuf var4 = var2.directBuffer(var3);
      var4.writeBytes(var1, var1.readerIndex(), var3);
      ReferenceCountUtil.safeRelease(var0);
      return var4;
   }

   protected static void checkResolvable(InetSocketAddress var0) {
      if (var0.isUnresolved()) {
         throw new UnresolvedAddressException();
      }
   }

   protected final int doReadBytes(ByteBuf var1) throws Exception {
      int var2 = var1.writerIndex();
      this.unsafe().recvBufAllocHandle().attemptedBytesRead(var1.writableBytes());
      int var3;
      if (var1.hasMemoryAddress()) {
         var3 = this.socket.recvAddress(var1.memoryAddress(), var2, var1.capacity());
      } else {
         ByteBuffer var4 = var1.internalNioBuffer(var2, var1.writableBytes());
         var3 = this.socket.recv(var4, var4.position(), var4.limit());
      }

      if (var3 > 0) {
         var1.writerIndex(var2 + var3);
      }

      return var3;
   }

   protected final int doWriteBytes(ChannelOutboundBuffer var1, ByteBuf var2) throws Exception {
      if (var2.hasMemoryAddress()) {
         int var3 = this.socket.sendAddress(var2.memoryAddress(), var2.readerIndex(), var2.writerIndex());
         if (var3 > 0) {
            var1.removeBytes((long)var3);
            return 1;
         }
      } else {
         ByteBuffer var5 = var2.nioBufferCount() == 1 ? var2.internalNioBuffer(var2.readerIndex(), var2.readableBytes()) : var2.nioBuffer();
         int var4 = this.socket.send(var5, var5.position(), var5.limit());
         if (var4 > 0) {
            ((Buffer)var5).position(var5.position() + var4);
            var1.removeBytes((long)var4);
            return 1;
         }
      }

      return Integer.MAX_VALUE;
   }

   final long doWriteOrSendBytes(ByteBuf var1, InetSocketAddress var2, boolean var3) throws IOException {
      assert !var3 || var2 != null : "fastOpen requires a remote address";

      if (var1.hasMemoryAddress()) {
         long var7 = var1.memoryAddress();
         return var2 == null
            ? (long)this.socket.sendAddress(var7, var1.readerIndex(), var1.writerIndex())
            : (long)this.socket.sendToAddress(var7, var1.readerIndex(), var1.writerIndex(), var2.getAddress(), var2.getPort(), var3);
      } else if (var1.nioBufferCount() > 1) {
         IovArray var6 = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
         var6.add(var1, var1.readerIndex(), var1.readableBytes());
         int var5 = var6.count();

         assert var5 != 0;

         return var2 == null
            ? this.socket.writevAddresses(var6.memoryAddress(0), var5)
            : (long)this.socket.sendToAddresses(var6.memoryAddress(0), var5, var2.getAddress(), var2.getPort(), var3);
      } else {
         ByteBuffer var4 = var1.internalNioBuffer(var1.readerIndex(), var1.readableBytes());
         return var2 == null
            ? (long)this.socket.send(var4, var4.position(), var4.limit())
            : (long)this.socket.sendTo(var4, var4.position(), var4.limit(), var2.getAddress(), var2.getPort(), var3);
      }
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      if (var1 instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)var1);
      }

      this.socket.bind(var1);
      this.local = this.socket.localAddress();
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)var2);
      }

      InetSocketAddress var3 = var1 instanceof InetSocketAddress ? (InetSocketAddress)var1 : null;
      if (var3 != null) {
         checkResolvable(var3);
      }

      if (this.remote != null) {
         throw new AlreadyConnectedException();
      } else {
         if (var2 != null) {
            this.socket.bind(var2);
         }

         boolean var4 = this.doConnect0(var1);
         if (var4) {
            this.remote = (SocketAddress)(var3 == null ? var1 : UnixChannelUtil.computeRemoteAddr(var3, this.socket.remoteAddress()));
         }

         this.local = this.socket.localAddress();
         return var4;
      }
   }

   boolean doConnect0(SocketAddress var1) throws Exception {
      boolean var2 = false;

      boolean var4;
      try {
         boolean var3 = this.socket.connect(var1);
         if (!var3) {
            this.setFlag(Native.EPOLLOUT);
         }

         var2 = true;
         var4 = var3;
      } finally {
         if (!var2) {
            this.doClose();
         }
      }

      return var4;
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.local;
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.remote;
   }

   protected abstract class AbstractEpollUnsafe extends AbstractChannel.AbstractUnsafe {
      boolean readPending;
      boolean maybeMoreDataToRead;
      private EpollRecvByteAllocatorHandle allocHandle;
      private final Runnable epollInReadyRunnable = new Runnable() {
         @Override
         public void run() {
            AbstractEpollChannel.this.epollInReadyRunnablePending = false;
            AbstractEpollUnsafe.this.epollInReady();
         }
      };

      abstract void epollInReady();

      final void epollInBefore() {
         this.maybeMoreDataToRead = false;
      }

      final void epollInFinally(ChannelConfig var1) {
         this.maybeMoreDataToRead = this.allocHandle.maybeMoreDataToRead();
         if (!this.allocHandle.isReceivedRdHup() && (!this.readPending || !this.maybeMoreDataToRead)) {
            if (!this.readPending && !var1.isAutoRead()) {
               AbstractEpollChannel.this.clearEpollIn();
            }
         } else {
            this.executeEpollInReadyRunnable(var1);
         }
      }

      final void executeEpollInReadyRunnable(ChannelConfig var1) {
         if (!AbstractEpollChannel.this.epollInReadyRunnablePending
            && AbstractEpollChannel.this.isActive()
            && !AbstractEpollChannel.this.shouldBreakEpollInReady(var1)) {
            AbstractEpollChannel.this.epollInReadyRunnablePending = true;
            AbstractEpollChannel.this.eventLoop().execute(this.epollInReadyRunnable);
         }
      }

      final void epollRdHupReady() {
         this.recvBufAllocHandle().receivedRdHup();
         if (AbstractEpollChannel.this.isActive()) {
            this.epollInReady();
         } else {
            this.shutdownInput(true);
         }

         this.clearEpollRdHup();
      }

      private void clearEpollRdHup() {
         try {
            AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
         } catch (IOException var2) {
            AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
            this.close(this.voidPromise());
         }
      }

      void shutdownInput(boolean var1) {
         if (!AbstractEpollChannel.this.socket.isInputShutdown()) {
            if (AbstractEpollChannel.isAllowHalfClosure(AbstractEpollChannel.this.config())) {
               try {
                  AbstractEpollChannel.this.socket.shutdown(true, false);
               } catch (IOException var3) {
                  this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                  return;
               } catch (NotYetConnectedException var4) {
               }

               AbstractEpollChannel.this.clearEpollIn();
               AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
               this.close(this.voidPromise());
            }
         } else if (!var1) {
            AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
         }
      }

      private void fireEventAndClose(Object var1) {
         AbstractEpollChannel.this.pipeline().fireUserEventTriggered(var1);
         this.close(this.voidPromise());
      }

      public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
         if (this.allocHandle == null) {
            this.allocHandle = this.newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
         }

         return this.allocHandle;
      }

      EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle var1) {
         return new EpollRecvByteAllocatorHandle(var1);
      }

      @Override
      protected final void flush0() {
         if (!AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT)) {
            super.flush0();
         }
      }

      final void epollOutReady() {
         if (AbstractEpollChannel.this.connectPromise != null) {
            this.finishConnect();
         } else if (!AbstractEpollChannel.this.socket.isOutputShutdown()) {
            super.flush0();
         }
      }

      protected final void clearEpollIn0() {
         assert AbstractEpollChannel.this.eventLoop().inEventLoop();

         try {
            this.readPending = false;
            AbstractEpollChannel.this.clearFlag(Native.EPOLLIN);
         } catch (IOException var2) {
            AbstractEpollChannel.this.pipeline().fireExceptionCaught(var2);
            AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
         }
      }

      @Override
      public void connect(final SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            try {
               if (AbstractEpollChannel.this.connectPromise != null) {
                  throw new ConnectionPendingException();
               }

               boolean var4 = AbstractEpollChannel.this.isActive();
               if (AbstractEpollChannel.this.doConnect(var1, var2)) {
                  this.fulfillConnectPromise(var3, var4);
               } else {
                  AbstractEpollChannel.this.connectPromise = var3;
                  AbstractEpollChannel.this.requestedRemoteAddress = var1;
                  int var5 = AbstractEpollChannel.this.config().getConnectTimeoutMillis();
                  if (var5 > 0) {
                     AbstractEpollChannel.this.connectTimeoutFuture = AbstractEpollChannel.this.eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                           ChannelPromise var1x = AbstractEpollChannel.this.connectPromise;
                           if (var1x != null && !var1x.isDone() && var1x.tryFailure(new ConnectTimeoutException("connection timed out: " + var1))) {
                              AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                           }
                        }
                     }, (long)var5, TimeUnit.MILLISECONDS);
                  }

                  var3.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        if (var1.isCancelled()) {
                           if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                              AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractEpollChannel.this.connectPromise = null;
                           AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                        }
                     }
                  });
               }
            } catch (Throwable var6) {
               this.closeIfClosed();
               var3.tryFailure(this.annotateConnectException(var6, var1));
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise var1, boolean var2) {
         if (var1 != null) {
            AbstractEpollChannel.this.active = true;
            boolean var3 = AbstractEpollChannel.this.isActive();
            boolean var4 = var1.trySuccess();
            if (!var2 && var3) {
               AbstractEpollChannel.this.pipeline().fireChannelActive();
            }

            if (!var4) {
               this.close(this.voidPromise());
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise var1, Throwable var2) {
         if (var1 != null) {
            var1.tryFailure(var2);
            this.closeIfClosed();
         }
      }

      private void finishConnect() {
         assert AbstractEpollChannel.this.eventLoop().inEventLoop();

         boolean var1 = false;

         try {
            boolean var2 = AbstractEpollChannel.this.isActive();
            if (this.doFinishConnect()) {
               this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, var2);
               return;
            }

            var1 = true;
         } catch (Throwable var6) {
            this.fulfillConnectPromise(
               AbstractEpollChannel.this.connectPromise, this.annotateConnectException(var6, AbstractEpollChannel.this.requestedRemoteAddress)
            );
            return;
         } finally {
            if (!var1) {
               if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                  AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
               }

               AbstractEpollChannel.this.connectPromise = null;
            }
         }
      }

      private boolean doFinishConnect() throws Exception {
         if (AbstractEpollChannel.this.socket.finishConnect()) {
            AbstractEpollChannel.this.clearFlag(Native.EPOLLOUT);
            if (AbstractEpollChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
               AbstractEpollChannel.this.remote = UnixChannelUtil.computeRemoteAddr(
                  (InetSocketAddress)AbstractEpollChannel.this.requestedRemoteAddress, AbstractEpollChannel.this.socket.remoteAddress()
               );
            }

            AbstractEpollChannel.this.requestedRemoteAddress = null;
            return true;
         } else {
            AbstractEpollChannel.this.setFlag(Native.EPOLLOUT);
            return false;
         }
      }
   }
}
