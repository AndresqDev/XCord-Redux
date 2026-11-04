package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class EpollDatagramChannel extends AbstractEpollChannel implements DatagramChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(InetSocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private final EpollDatagramChannelConfig config = new EpollDatagramChannelConfig(this);
   private volatile boolean connected;

   public static boolean isSegmentedDatagramPacketSupported() {
      return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
   }

   public EpollDatagramChannel() {
      this(null);
   }

   public EpollDatagramChannel(InternetProtocolFamily var1) {
      this(LinuxSocket.newSocketDgram(var1), false);
   }

   public EpollDatagramChannel(int var1) {
      this(new LinuxSocket(var1), true);
   }

   private EpollDatagramChannel(LinuxSocket var1, boolean var2) {
      super(null, var1, var2);
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public boolean isActive() {
      return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
   }

   @Override
   public boolean isConnected() {
      return this.connected;
   }

   @Override
   public ChannelFuture joinGroup(InetAddress var1) {
      return this.joinGroup(var1, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetAddress var1, ChannelPromise var2) {
      try {
         NetworkInterface var3 = this.config().getNetworkInterface();
         if (var3 == null) {
            var3 = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
         }

         return this.joinGroup(var1, var3, null, var2);
      } catch (IOException var4) {
         var2.setFailure(var4);
         return var2;
      }
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.joinGroup(var1, var2, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      return this.joinGroup(var1.getAddress(), var2, null, var3);
   }

   @Override
   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.joinGroup(var1, var2, var3, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(final InetAddress var1, final NetworkInterface var2, final InetAddress var3, final ChannelPromise var4) {
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var2, "networkInterface");
      if (this.eventLoop().inEventLoop()) {
         this.joinGroup0(var1, var2, var3, var4);
      } else {
         this.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               EpollDatagramChannel.this.joinGroup0(var1, var2, var3, var4);
            }
         });
      }

      return var4;
   }

   private void joinGroup0(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      assert this.eventLoop().inEventLoop();

      try {
         this.socket.joinGroup(var1, var2, var3);
         var4.setSuccess();
      } catch (IOException var6) {
         var4.setFailure(var6);
      }
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress var1) {
      return this.leaveGroup(var1, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress var1, ChannelPromise var2) {
      try {
         return this.leaveGroup(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, var2);
      } catch (IOException var4) {
         var2.setFailure(var4);
         return var2;
      }
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.leaveGroup(var1, var2, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      return this.leaveGroup(var1.getAddress(), var2, null, var3);
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.leaveGroup(var1, var2, var3, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(final InetAddress var1, final NetworkInterface var2, final InetAddress var3, final ChannelPromise var4) {
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var2, "networkInterface");
      if (this.eventLoop().inEventLoop()) {
         this.leaveGroup0(var1, var2, var3, var4);
      } else {
         this.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               EpollDatagramChannel.this.leaveGroup0(var1, var2, var3, var4);
            }
         });
      }

      return var4;
   }

   private void leaveGroup0(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      assert this.eventLoop().inEventLoop();

      try {
         this.socket.leaveGroup(var1, var2, var3);
         var4.setSuccess();
      } catch (IOException var6) {
         var4.setFailure(var6);
      }
   }

   @Override
   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.block(var1, var2, var3, this.newPromise());
   }

   @Override
   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var3, "sourceToBlock");
      ObjectUtil.checkNotNull(var2, "networkInterface");
      var4.setFailure(new UnsupportedOperationException("Multicast block not supported"));
      return var4;
   }

   @Override
   public ChannelFuture block(InetAddress var1, InetAddress var2) {
      return this.block(var1, var2, this.newPromise());
   }

   @Override
   public ChannelFuture block(InetAddress var1, InetAddress var2, ChannelPromise var3) {
      try {
         return this.block(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), var2, var3);
      } catch (Throwable var5) {
         var3.setFailure(var5);
         return var3;
      }
   }

   @Override
   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollDatagramChannel.EpollDatagramChannelUnsafe();
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      if (var1 instanceof InetSocketAddress) {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         if (var2.getAddress().isAnyLocalAddress() && var2.getAddress() instanceof Inet4Address && this.socket.family() == InternetProtocolFamily.IPv6) {
            var1 = new InetSocketAddress(LinuxSocket.INET6_ANY, var2.getPort());
         }
      }

      super.doBind((SocketAddress)var1);
      this.active = true;
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      int var2 = this.maxMessagesPerWrite();

      while (var2 > 0) {
         Object var3 = var1.current();
         if (var3 == null) {
            break;
         }

         try {
            if (Native.IS_SUPPORTING_SENDMMSG && var1.size() > 1 || var1.current() instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
               NativeDatagramPacketArray var4 = this.cleanDatagramPacketArray();
               var4.add(var1, this.isConnected(), var2);
               int var5 = var4.count();
               if (var5 >= 1) {
                  byte var6 = 0;
                  NativeDatagramPacketArray.NativeDatagramPacket[] var7 = var4.packets();
                  int var8 = this.socket.sendmmsg(var7, var6, var5);
                  if (var8 == 0) {
                     break;
                  }

                  for (int var9 = 0; var9 < var8; var9++) {
                     var1.remove();
                  }

                  var2 -= var8;
                  continue;
               }
            }

            boolean var11 = false;

            for (int var12 = this.config().getWriteSpinCount(); var12 > 0; var12--) {
               if (this.doWriteMessage(var3)) {
                  var11 = true;
                  break;
               }
            }

            if (!var11) {
               break;
            }

            var1.remove();
            var2--;
         } catch (IOException var10) {
            var2--;
            var1.remove(var10);
         }
      }

      if (var1.isEmpty()) {
         this.clearFlag(Native.EPOLLOUT);
      } else {
         this.setFlag(Native.EPOLLOUT);
      }
   }

   private boolean doWriteMessage(Object var1) throws Exception {
      ByteBuf var2;
      InetSocketAddress var3;
      if (var1 instanceof AddressedEnvelope) {
         AddressedEnvelope var4 = (AddressedEnvelope)var1;
         var2 = (ByteBuf)var4.content();
         var3 = (InetSocketAddress)var4.recipient();
      } else {
         var2 = (ByteBuf)var1;
         var3 = null;
      }

      int var5 = var2.readableBytes();
      return var5 == 0 ? true : this.doWriteOrSendBytes(var2, var3, false) > 0L;
   }

   @Override
   protected Object filterOutboundMessage(Object var1) {
      if (var1 instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
         if (!Native.IS_SUPPORTING_UDP_SEGMENT) {
            throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
         } else {
            io.netty.channel.unix.SegmentedDatagramPacket var6 = (io.netty.channel.unix.SegmentedDatagramPacket)var1;
            ByteBuf var8 = var6.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(var8) ? var6.replace(this.newDirectBuffer(var6, var8)) : var1;
         }
      } else if (var1 instanceof DatagramPacket) {
         DatagramPacket var5 = (DatagramPacket)var1;
         ByteBuf var7 = var5.content();
         return UnixChannelUtil.isBufferCopyNeededForWrite(var7) ? new DatagramPacket(this.newDirectBuffer(var5, var7), var5.recipient()) : var1;
      } else if (var1 instanceof ByteBuf) {
         ByteBuf var4 = (ByteBuf)var1;
         return UnixChannelUtil.isBufferCopyNeededForWrite(var4) ? this.newDirectBuffer(var4) : var4;
      } else {
         if (var1 instanceof AddressedEnvelope) {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (var2.content() instanceof ByteBuf && (var2.recipient() == null || var2.recipient() instanceof InetSocketAddress)) {
               ByteBuf var3 = (ByteBuf)var2.content();
               return UnixChannelUtil.isBufferCopyNeededForWrite(var3)
                  ? new DefaultAddressedEnvelope<>(this.newDirectBuffer(var2, var3), (InetSocketAddress)var2.recipient())
                  : var2;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
   }

   public EpollDatagramChannelConfig config() {
      return this.config;
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
      this.connected = this.active = false;
      this.resetCachedAddresses();
   }

   @Override
   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (super.doConnect(var1, var2)) {
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void doClose() throws Exception {
      super.doClose();
      this.connected = false;
   }

   private boolean connectedRead(EpollRecvByteAllocatorHandle var1, ByteBuf var2, int var3) throws Exception {
      boolean var12;
      try {
         int var4 = var3 != 0 ? Math.min(var2.writableBytes(), var3) : var2.writableBytes();
         var1.attemptedBytesRead(var4);
         int var5 = var2.writerIndex();
         int var6;
         if (var2.hasMemoryAddress()) {
            var6 = this.socket.recvAddress(var2.memoryAddress(), var5, var5 + var4);
         } else {
            ByteBuffer var7 = var2.internalNioBuffer(var5, var4);
            var6 = this.socket.recv(var7, var7.position(), var7.limit());
         }

         if (var6 > 0) {
            var2.writerIndex(var5 + var6);
            var1.lastBytesRead(var3 <= 0 ? var6 : var4);
            DatagramPacket var13 = new DatagramPacket(var2, this.localAddress(), this.remoteAddress());
            var1.incMessagesRead(1);
            this.pipeline().fireChannelRead(var13);
            var2 = null;
            return true;
         }

         var1.lastBytesRead(var6);
         var12 = false;
      } finally {
         if (var2 != null) {
            var2.release();
         }
      }

      return var12;
   }

   private IOException translateForConnected(Errors.NativeIoException var1) {
      if (var1.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
         PortUnreachableException var2 = new PortUnreachableException(var1.getMessage());
         var2.initCause(var1);
         return var2;
      } else {
         return var1;
      }
   }

   private static void addDatagramPacketToOut(DatagramPacket var0, RecyclableArrayList var1) {
      if (var0 instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
         io.netty.channel.unix.SegmentedDatagramPacket var2 = (io.netty.channel.unix.SegmentedDatagramPacket)var0;
         ByteBuf var3 = var2.content();
         InetSocketAddress var4 = var2.recipient();
         InetSocketAddress var5 = var2.sender();
         int var6 = var2.segmentSize();

         do {
            var1.add(new DatagramPacket(var3.readRetainedSlice(Math.min(var3.readableBytes(), var6)), var4, var5));
         } while (var3.isReadable());

         var2.release();
      } else {
         var1.add(var0);
      }
   }

   private static void releaseAndRecycle(ByteBuf var0, RecyclableArrayList var1) {
      if (var0 != null) {
         var0.release();
      }

      if (var1 != null) {
         for (int var2 = 0; var2 < var1.size(); var2++) {
            ReferenceCountUtil.release(var1.get(var2));
         }

         var1.recycle();
      }
   }

   private static void processPacket(ChannelPipeline var0, EpollRecvByteAllocatorHandle var1, int var2, DatagramPacket var3) {
      var1.lastBytesRead(Math.max(1, var2));
      var1.incMessagesRead(1);
      var0.fireChannelRead(var3);
   }

   private static void processPacketList(ChannelPipeline var0, EpollRecvByteAllocatorHandle var1, int var2, RecyclableArrayList var3) {
      int var4 = var3.size();
      var1.lastBytesRead(Math.max(1, var2));
      var1.incMessagesRead(var4);

      for (int var5 = 0; var5 < var4; var5++) {
         var0.fireChannelRead(var3.set(var5, Unpooled.EMPTY_BUFFER));
      }
   }

   private boolean recvmsg(EpollRecvByteAllocatorHandle var1, NativeDatagramPacketArray var2, ByteBuf var3) throws IOException {
      RecyclableArrayList var4 = null;

      boolean var9;
      try {
         int var5 = var3.writableBytes();
         boolean var6 = var2.addWritable(var3, var3.writerIndex(), var5);

         assert var6;

         var1.attemptedBytesRead(var5);
         NativeDatagramPacketArray.NativeDatagramPacket var7 = var2.packets()[0];
         int var8 = this.socket.recvmsg(var7);
         if (var7.hasSender()) {
            var3.writerIndex(var8);
            InetSocketAddress var15 = this.localAddress();
            DatagramPacket var10 = var7.newDatagramPacket(var3, var15);
            if (!(var10 instanceof io.netty.channel.unix.SegmentedDatagramPacket)) {
               processPacket(this.pipeline(), var1, var8, var10);
               var3 = null;
            } else {
               var4 = RecyclableArrayList.newInstance();
               addDatagramPacketToOut(var10, var4);
               var3 = null;
               processPacketList(this.pipeline(), var1, var8, var4);
               var4.recycle();
               var4 = null;
            }

            return true;
         }

         var1.lastBytesRead(-1);
         var9 = false;
      } finally {
         releaseAndRecycle(var3, var4);
      }

      return var9;
   }

   private boolean scatteringRead(EpollRecvByteAllocatorHandle var1, NativeDatagramPacketArray var2, ByteBuf var3, int var4, int var5) throws IOException {
      RecyclableArrayList var6 = null;

      try {
         int var7 = var3.writerIndex();
         int var8 = 0;

         while (var8 < var5 && var2.addWritable(var3, var7, var4)) {
            var8++;
            var7 += var4;
         }

         var1.attemptedBytesRead(var7 - var3.writerIndex());
         NativeDatagramPacketArray.NativeDatagramPacket[] var17 = var2.packets();
         int var9 = this.socket.recvmmsg(var17, 0, var2.count());
         if (var9 == 0) {
            var1.lastBytesRead(-1);
            return false;
         } else {
            int var10 = var9 * var4;
            var3.writerIndex(var10);
            InetSocketAddress var11 = this.localAddress();
            if (var9 == 1) {
               DatagramPacket var12 = var17[0].newDatagramPacket(var3, var11);
               if (!(var12 instanceof io.netty.channel.unix.SegmentedDatagramPacket)) {
                  processPacket(this.pipeline(), var1, var4, var12);
                  var3 = null;
                  return true;
               }
            }

            var6 = RecyclableArrayList.newInstance();

            for (int var19 = 0; var19 < var9; var19++) {
               DatagramPacket var13 = var17[var19].newDatagramPacket(var3.readRetainedSlice(var4), var11);
               addDatagramPacketToOut(var13, var6);
            }

            var3.release();
            var3 = null;
            processPacketList(this.pipeline(), var1, var10, var6);
            var6.recycle();
            var6 = null;
            return true;
         }
      } finally {
         releaseAndRecycle(var3, var6);
      }
   }

   private NativeDatagramPacketArray cleanDatagramPacketArray() {
      return ((EpollEventLoop)this.eventLoop()).cleanDatagramPacketArray();
   }

   final class EpollDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      @Override
      void epollInReady() {
         assert EpollDatagramChannel.this.eventLoop().inEventLoop();

         EpollDatagramChannelConfig var1 = EpollDatagramChannel.this.config();
         if (EpollDatagramChannel.this.shouldBreakEpollInReady(var1)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(EpollDatagramChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = EpollDatagramChannel.this.pipeline();
            ByteBufAllocator var4 = var1.getAllocator();
            var2.reset(var1);
            this.epollInBefore();
            Throwable var5 = null;

            try {
               try {
                  boolean var6 = EpollDatagramChannel.this.isConnected();

                  do {
                     int var8 = EpollDatagramChannel.this.config().getMaxDatagramPayloadSize();
                     ByteBuf var9 = var2.allocate(var4);
                     int var10 = Native.IS_SUPPORTING_RECVMMSG ? (var8 == 0 ? 1 : var9.writableBytes() / var8) : 0;

                     boolean var7;
                     try {
                        if (var10 <= 1) {
                           if (var6 && !var1.isUdpGro()) {
                              var7 = EpollDatagramChannel.this.connectedRead(var2, var9, var8);
                           } else {
                              var7 = EpollDatagramChannel.this.recvmsg(var2, EpollDatagramChannel.this.cleanDatagramPacketArray(), var9);
                           }
                        } else {
                           var7 = EpollDatagramChannel.this.scatteringRead(var2, EpollDatagramChannel.this.cleanDatagramPacketArray(), var9, var8, var10);
                        }
                     } catch (Errors.NativeIoException var16) {
                        if (var6) {
                           throw EpollDatagramChannel.this.translateForConnected(var16);
                        }

                        throw var16;
                     }

                     if (!var7) {
                        break;
                     }

                     this.readPending = false;
                  } while (var2.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
               } catch (Throwable var17) {
                  var5 = var17;
               }

               var2.readComplete();
               var3.fireChannelReadComplete();
               if (var5 != null) {
                  var3.fireExceptionCaught(var5);
               }
            } finally {
               this.epollInFinally(var1);
            }
         }
      }
   }
}
