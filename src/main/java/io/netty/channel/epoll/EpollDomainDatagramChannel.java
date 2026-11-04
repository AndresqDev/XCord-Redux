package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.DomainDatagramChannel;
import io.netty.channel.unix.DomainDatagramPacket;
import io.netty.channel.unix.DomainDatagramSocketAddress;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class EpollDomainDatagramChannel extends AbstractEpollChannel implements DomainDatagramChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DomainDatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(DomainSocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private volatile boolean connected;
   private volatile DomainSocketAddress local;
   private volatile DomainSocketAddress remote;
   private final EpollDomainDatagramChannelConfig config = new EpollDomainDatagramChannelConfig(this);

   public EpollDomainDatagramChannel() {
      this(LinuxSocket.newSocketDomainDgram(), false);
   }

   public EpollDomainDatagramChannel(int var1) {
      this(new LinuxSocket(var1), true);
   }

   private EpollDomainDatagramChannel(LinuxSocket var1, boolean var2) {
      super(null, var1, var2);
   }

   public EpollDomainDatagramChannelConfig config() {
      return this.config;
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      super.doBind(var1);
      this.local = (DomainSocketAddress)var1;
      this.active = true;
   }

   @Override
   protected void doClose() throws Exception {
      super.doClose();
      this.connected = this.active = false;
      this.local = null;
      this.remote = null;
   }

   @Override
   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (super.doConnect(var1, var2)) {
         if (var2 != null) {
            this.local = (DomainSocketAddress)var2;
         }

         this.remote = (DomainSocketAddress)var1;
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
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
            boolean var4 = false;

            for (int var5 = this.config().getWriteSpinCount(); var5 > 0; var5--) {
               if (this.doWriteMessage(var3)) {
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               break;
            }

            var1.remove();
            var2--;
         } catch (IOException var6) {
            var2--;
            var1.remove(var6);
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
      DomainSocketAddress var3;
      if (var1 instanceof AddressedEnvelope) {
         AddressedEnvelope var4 = (AddressedEnvelope)var1;
         var2 = (ByteBuf)var4.content();
         var3 = (DomainSocketAddress)var4.recipient();
      } else {
         var2 = (ByteBuf)var1;
         var3 = null;
      }

      int var9 = var2.readableBytes();
      if (var9 == 0) {
         return true;
      } else {
         long var5;
         if (var2.hasMemoryAddress()) {
            long var7 = var2.memoryAddress();
            if (var3 == null) {
               var5 = (long)this.socket.sendAddress(var7, var2.readerIndex(), var2.writerIndex());
            } else {
               var5 = (long)this.socket.sendToAddressDomainSocket(var7, var2.readerIndex(), var2.writerIndex(), var3.path().getBytes(CharsetUtil.UTF_8));
            }
         } else if (var2.nioBufferCount() > 1) {
            IovArray var10 = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
            var10.add(var2, var2.readerIndex(), var2.readableBytes());
            int var8 = var10.count();

            assert var8 != 0;

            if (var3 == null) {
               var5 = this.socket.writevAddresses(var10.memoryAddress(0), var8);
            } else {
               var5 = (long)this.socket.sendToAddressesDomainSocket(var10.memoryAddress(0), var8, var3.path().getBytes(CharsetUtil.UTF_8));
            }
         } else {
            ByteBuffer var11 = var2.internalNioBuffer(var2.readerIndex(), var2.readableBytes());
            if (var3 == null) {
               var5 = (long)this.socket.send(var11, var11.position(), var11.limit());
            } else {
               var5 = (long)this.socket.sendToDomainSocket(var11, var11.position(), var11.limit(), var3.path().getBytes(CharsetUtil.UTF_8));
            }
         }

         return var5 > 0L;
      }
   }

   @Override
   protected Object filterOutboundMessage(Object var1) {
      if (var1 instanceof DomainDatagramPacket) {
         DomainDatagramPacket var5 = (DomainDatagramPacket)var1;
         ByteBuf var6 = var5.content();
         return UnixChannelUtil.isBufferCopyNeededForWrite(var6) ? new DomainDatagramPacket(this.newDirectBuffer(var5, var6), var5.recipient()) : var1;
      } else if (var1 instanceof ByteBuf) {
         ByteBuf var4 = (ByteBuf)var1;
         return UnixChannelUtil.isBufferCopyNeededForWrite(var4) ? this.newDirectBuffer(var4) : var4;
      } else {
         if (var1 instanceof AddressedEnvelope) {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (var2.content() instanceof ByteBuf && (var2.recipient() == null || var2.recipient() instanceof DomainSocketAddress)) {
               ByteBuf var3 = (ByteBuf)var2.content();
               return UnixChannelUtil.isBufferCopyNeededForWrite(var3)
                  ? new DefaultAddressedEnvelope<>(this.newDirectBuffer(var2, var3), (DomainSocketAddress)var2.recipient())
                  : var2;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
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
   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollDomainDatagramChannel.EpollDomainDatagramChannelUnsafe();
   }

   public PeerCredentials peerCredentials() throws IOException {
      return this.socket.getPeerCredentials();
   }

   @Override
   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   protected DomainSocketAddress remoteAddress0() {
      return this.remote;
   }

   final class EpollDomainDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      @Override
      void epollInReady() {
         assert EpollDomainDatagramChannel.this.eventLoop().inEventLoop();

         EpollDomainDatagramChannelConfig var1 = EpollDomainDatagramChannel.this.config();
         if (EpollDomainDatagramChannel.this.shouldBreakEpollInReady(var1)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(EpollDomainDatagramChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = EpollDomainDatagramChannel.this.pipeline();
            ByteBufAllocator var4 = var1.getAllocator();
            var2.reset(var1);
            this.epollInBefore();
            Throwable var5 = null;

            try {
               ByteBuf var6 = null;

               try {
                  boolean var7 = EpollDomainDatagramChannel.this.isConnected();

                  do {
                     var6 = var2.allocate(var4);
                     var2.attemptedBytesRead(var6.writableBytes());
                     DomainDatagramPacket var8;
                     if (var7) {
                        var2.lastBytesRead(EpollDomainDatagramChannel.this.doReadBytes(var6));
                        if (var2.lastBytesRead() <= 0) {
                           var6.release();
                           break;
                        }

                        var8 = new DomainDatagramPacket(var6, (DomainSocketAddress)this.localAddress(), (DomainSocketAddress)this.remoteAddress());
                     } else {
                        DomainDatagramSocketAddress var9;
                        if (var6.hasMemoryAddress()) {
                           var9 = EpollDomainDatagramChannel.this.socket.recvFromAddressDomainSocket(var6.memoryAddress(), var6.writerIndex(), var6.capacity());
                        } else {
                           ByteBuffer var10 = var6.internalNioBuffer(var6.writerIndex(), var6.writableBytes());
                           var9 = EpollDomainDatagramChannel.this.socket.recvFromDomainSocket(var10, var10.position(), var10.limit());
                        }

                        if (var9 == null) {
                           var2.lastBytesRead(-1);
                           var6.release();
                           break;
                        }

                        Object var16 = var9.localAddress();
                        if (var16 == null) {
                           var16 = (DomainSocketAddress)this.localAddress();
                        }

                        var2.lastBytesRead(var9.receivedAmount());
                        var6.writerIndex(var6.writerIndex() + var2.lastBytesRead());
                        var8 = new DomainDatagramPacket(var6, (DomainSocketAddress)var16, var9);
                     }

                     var2.incMessagesRead(1);
                     this.readPending = false;
                     var3.fireChannelRead(var8);
                     var6 = null;
                  } while (var2.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
               } catch (Throwable var14) {
                  if (var6 != null) {
                     var6.release();
                  }

                  var5 = var14;
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
