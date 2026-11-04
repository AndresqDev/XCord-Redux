package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.MembershipKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class NioDatagramChannel extends AbstractNioMessageChannel implements DatagramChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private static final String EXPECTED_TYPES = " (expected: "
      + StringUtil.simpleClassName(DatagramPacket.class)
      + ", "
      + StringUtil.simpleClassName(AddressedEnvelope.class)
      + '<'
      + StringUtil.simpleClassName(ByteBuf.class)
      + ", "
      + StringUtil.simpleClassName(SocketAddress.class)
      + ">, "
      + StringUtil.simpleClassName(ByteBuf.class)
      + ')';
   private final DatagramChannelConfig config;
   private Map<InetAddress, List<MembershipKey>> memberships;

   private static java.nio.channels.DatagramChannel newSocket(SelectorProvider var0) {
      try {
         return var0.openDatagramChannel();
      } catch (IOException var2) {
         throw new ChannelException("Failed to open a socket.", var2);
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   private static java.nio.channels.DatagramChannel newSocket(SelectorProvider var0, InternetProtocolFamily var1) {
      if (var1 == null) {
         return newSocket(var0);
      } else {
         checkJavaVersion();

         try {
            return var0.openDatagramChannel(ProtocolFamilyConverter.convert(var1));
         } catch (IOException var3) {
            throw new ChannelException("Failed to open a socket.", var3);
         }
      }
   }

   private static void checkJavaVersion() {
      if (PlatformDependent.javaVersion() < 7) {
         throw new UnsupportedOperationException("Only supported on java 7+.");
      }
   }

   public NioDatagramChannel() {
      this(newSocket(DEFAULT_SELECTOR_PROVIDER));
   }

   public NioDatagramChannel(SelectorProvider var1) {
      this(newSocket(var1));
   }

   public NioDatagramChannel(InternetProtocolFamily var1) {
      this(newSocket(DEFAULT_SELECTOR_PROVIDER, var1));
   }

   public NioDatagramChannel(SelectorProvider var1, InternetProtocolFamily var2) {
      this(newSocket(var1, var2));
   }

   public NioDatagramChannel(java.nio.channels.DatagramChannel var1) {
      super(null, var1, 1);
      this.config = new NioDatagramChannelConfig(this, var1);
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public DatagramChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isActive() {
      java.nio.channels.DatagramChannel var1 = this.javaChannel();
      return var1.isOpen() && (this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && this.isRegistered() || var1.socket().isBound());
   }

   @Override
   public boolean isConnected() {
      return this.javaChannel().isConnected();
   }

   protected java.nio.channels.DatagramChannel javaChannel() {
      return (java.nio.channels.DatagramChannel)super.javaChannel();
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.javaChannel().socket().getLocalSocketAddress();
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.javaChannel().socket().getRemoteSocketAddress();
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      this.doBind0(var1);
   }

   private void doBind0(SocketAddress var1) throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         SocketUtils.bind(this.javaChannel(), var1);
      } else {
         this.javaChannel().socket().bind(var1);
      }
   }

   @Override
   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 != null) {
         this.doBind0(var2);
      }

      boolean var3 = false;

      boolean var4;
      try {
         this.javaChannel().connect(var1);
         var3 = true;
         var4 = true;
      } finally {
         if (!var3) {
            this.doClose();
         }
      }

      return var4;
   }

   @Override
   protected void doFinishConnect() throws Exception {
      throw new Error();
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.javaChannel().disconnect();
   }

   @Override
   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   @Override
   protected int doReadMessages(List<Object> var1) throws Exception {
      java.nio.channels.DatagramChannel var2 = this.javaChannel();
      DatagramChannelConfig var3 = this.config();
      RecvByteBufAllocator.Handle var4 = this.unsafe().recvBufAllocHandle();
      ByteBuf var5 = var4.allocate(var3.getAllocator());
      var4.attemptedBytesRead(var5.writableBytes());
      boolean var6 = true;

      byte var10;
      try {
         ByteBuffer var7 = var5.internalNioBuffer(var5.writerIndex(), var5.writableBytes());
         int var16 = var7.position();
         InetSocketAddress var9 = (InetSocketAddress)var2.receive(var7);
         if (var9 != null) {
            var4.lastBytesRead(var7.position() - var16);
            var1.add(new DatagramPacket(var5.writerIndex(var5.writerIndex() + var4.lastBytesRead()), this.localAddress(), var9));
            var6 = false;
            return 1;
         }

         var10 = 0;
      } catch (Throwable var14) {
         PlatformDependent.throwException(var14);
         return -1;
      } finally {
         if (var6) {
            var5.release();
         }
      }

      return var10;
   }

   @Override
   protected boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception {
      SocketAddress var3;
      ByteBuf var4;
      if (var1 instanceof AddressedEnvelope) {
         AddressedEnvelope var5 = (AddressedEnvelope)var1;
         var3 = var5.recipient();
         var4 = (ByteBuf)var5.content();
      } else {
         var4 = (ByteBuf)var1;
         var3 = null;
      }

      int var8 = var4.readableBytes();
      if (var8 == 0) {
         return true;
      } else {
         ByteBuffer var6 = var4.nioBufferCount() == 1 ? var4.internalNioBuffer(var4.readerIndex(), var8) : var4.nioBuffer(var4.readerIndex(), var8);
         int var7;
         if (var3 != null) {
            var7 = this.javaChannel().send(var6, var3);
         } else {
            var7 = this.javaChannel().write(var6);
         }

         return var7 > 0;
      }
   }

   @Override
   protected Object filterOutboundMessage(Object var1) {
      if (var1 instanceof DatagramPacket) {
         DatagramPacket var5 = (DatagramPacket)var1;
         ByteBuf var6 = var5.content();
         return isSingleDirectBuffer(var6) ? var5 : new DatagramPacket(this.newDirectBuffer(var5, var6), var5.recipient());
      } else if (var1 instanceof ByteBuf) {
         ByteBuf var4 = (ByteBuf)var1;
         return isSingleDirectBuffer(var4) ? var4 : this.newDirectBuffer(var4);
      } else {
         if (var1 instanceof AddressedEnvelope) {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (var2.content() instanceof ByteBuf) {
               ByteBuf var3 = (ByteBuf)var2.content();
               if (isSingleDirectBuffer(var3)) {
                  return var2;
               }

               return new DefaultAddressedEnvelope<>(this.newDirectBuffer(var2, var3), var2.recipient());
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
   }

   private static boolean isSingleDirectBuffer(ByteBuf var0) {
      return var0.isDirect() && var0.nioBufferCount() == 1;
   }

   @Override
   protected boolean continueOnWriteError() {
      return true;
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   public ChannelFuture joinGroup(InetAddress var1) {
      return this.joinGroup(var1, this.newPromise());
   }

   @Override
   public ChannelFuture joinGroup(InetAddress var1, ChannelPromise var2) {
      try {
         NetworkInterface var3 = this.config.getNetworkInterface();
         if (var3 == null) {
            var3 = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
         }

         return this.joinGroup(var1, var3, null, var2);
      } catch (SocketException var4) {
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

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      checkJavaVersion();
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var2, "networkInterface");

      try {
         MembershipKey var5;
         if (var3 == null) {
            var5 = this.javaChannel().join(var1, var2);
         } else {
            var5 = this.javaChannel().join(var1, var2, var3);
         }

         synchronized (this) {
            Object var7 = null;
            if (this.memberships == null) {
               this.memberships = new HashMap<>();
            } else {
               var7 = this.memberships.get(var1);
            }

            if (var7 == null) {
               var7 = new ArrayList();
               this.memberships.put(var1, (List<MembershipKey>)var7);
            }

            var7.add(var5);
         }

         var4.setSuccess();
      } catch (Throwable var10) {
         var4.setFailure(var10);
      }

      return var4;
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress var1) {
      return this.leaveGroup(var1, this.newPromise());
   }

   @Override
   public ChannelFuture leaveGroup(InetAddress var1, ChannelPromise var2) {
      try {
         return this.leaveGroup(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, var2);
      } catch (SocketException var4) {
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

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      checkJavaVersion();
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var2, "networkInterface");
      synchronized (this) {
         if (this.memberships != null) {
            List var6 = this.memberships.get(var1);
            if (var6 != null) {
               Iterator var7 = var6.iterator();

               while (var7.hasNext()) {
                  MembershipKey var8 = (MembershipKey)var7.next();
                  if (var2.equals(var8.networkInterface())
                     && (var3 == null && var8.sourceAddress() == null || var3 != null && var3.equals(var8.sourceAddress()))) {
                     var8.drop();
                     var7.remove();
                  }
               }

               if (var6.isEmpty()) {
                  this.memberships.remove(var1);
               }
            }
         }
      }

      var4.setSuccess();
      return var4;
   }

   @Override
   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.block(var1, var2, var3, this.newPromise());
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      checkJavaVersion();
      ObjectUtil.checkNotNull(var1, "multicastAddress");
      ObjectUtil.checkNotNull(var3, "sourceToBlock");
      ObjectUtil.checkNotNull(var2, "networkInterface");
      synchronized (this) {
         if (this.memberships != null) {
            for (MembershipKey var8 : this.memberships.get(var1)) {
               if (var2.equals(var8.networkInterface())) {
                  try {
                     var8.block(var3);
                  } catch (IOException var11) {
                     var4.setFailure(var11);
                  }
               }
            }
         }
      }

      var4.setSuccess();
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
      } catch (SocketException var5) {
         var3.setFailure(var5);
         return var3;
      }
   }

   @Deprecated
   @Override
   protected void setReadPending(boolean var1) {
      super.setReadPending(var1);
   }

   @Override
   void clearReadPending0() {
      this.clearReadPending();
   }

   @Override
   protected boolean closeOnReadError(Throwable var1) {
      return var1 instanceof SocketException ? false : super.closeOnReadError(var1);
   }

   @Override
   protected boolean continueReading(RecvByteBufAllocator.Handle var1) {
      return var1 instanceof RecvByteBufAllocator.ExtendedHandle
         ? ((RecvByteBufAllocator.ExtendedHandle)var1).continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER)
         : var1.continueReading();
   }
}
