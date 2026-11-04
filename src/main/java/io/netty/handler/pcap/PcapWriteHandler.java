package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

public final class PcapWriteHandler extends ChannelDuplexHandler implements Closeable {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(PcapWriteHandler.class);
   private PcapWriter pCapWriter;
   private final OutputStream outputStream;
   private final boolean captureZeroByte;
   private final boolean writePcapGlobalHeader;
   private int sendSegmentNumber = 1;
   private int receiveSegmentNumber = 1;
   private PcapWriteHandler.ChannelType channelType;
   private InetSocketAddress initiatiorAddr;
   private InetSocketAddress handlerAddr;
   private boolean isServerPipeline;
   private boolean isClosed;
   private boolean initialized;

   @Deprecated
   public PcapWriteHandler(OutputStream var1) {
      this(var1, false, true);
   }

   @Deprecated
   public PcapWriteHandler(OutputStream var1, boolean var2, boolean var3) {
      this.outputStream = ObjectUtil.checkNotNull(var1, "OutputStream");
      this.captureZeroByte = var2;
      this.writePcapGlobalHeader = var3;
   }

   private PcapWriteHandler(PcapWriteHandler.Builder var1, OutputStream var2) {
      this.outputStream = var2;
      this.captureZeroByte = var1.captureZeroByte;
      this.writePcapGlobalHeader = var1.writePcapGlobalHeader;
      this.channelType = var1.channelType;
      this.handlerAddr = var1.handlerAddr;
      this.initiatiorAddr = var1.initiatiorAddr;
      this.isServerPipeline = var1.isServerPipeline;
   }

   public static PcapWriteHandler.Builder builder() {
      return new PcapWriteHandler.Builder();
   }

   private void initializeIfNecessary(ChannelHandlerContext var1) {
      if (!this.initialized) {
         ByteBufAllocator var2 = var1.alloc();
         if (this.writePcapGlobalHeader) {
            ByteBuf var3 = var2.buffer();

            try {
               this.pCapWriter = new PcapWriter(this.outputStream, var3);
            } catch (IOException var14) {
               var1.channel().close();
               var1.fireExceptionCaught(var14);
               this.logger.error("Caught Exception While Initializing PcapWriter, Closing Channel.", (Throwable)var14);
            } finally {
               var3.release();
            }
         } else {
            this.pCapWriter = new PcapWriter(this.outputStream);
         }

         if (this.channelType == null) {
            if (var1.channel() instanceof SocketChannel) {
               this.channelType = PcapWriteHandler.ChannelType.TCP;
               if (var1.channel().parent() instanceof ServerSocketChannel) {
                  this.isServerPipeline = true;
                  this.initiatiorAddr = (InetSocketAddress)var1.channel().remoteAddress();
                  this.handlerAddr = (InetSocketAddress)var1.channel().localAddress();
               } else {
                  this.isServerPipeline = false;
                  this.initiatiorAddr = (InetSocketAddress)var1.channel().localAddress();
                  this.handlerAddr = (InetSocketAddress)var1.channel().remoteAddress();
               }
            } else if (var1.channel() instanceof DatagramChannel) {
               this.channelType = PcapWriteHandler.ChannelType.UDP;
               DatagramChannel var16 = (DatagramChannel)var1.channel();
               if (var16.isConnected()) {
                  this.initiatiorAddr = (InetSocketAddress)var1.channel().localAddress();
                  this.handlerAddr = (InetSocketAddress)var1.channel().remoteAddress();
               }
            }
         }

         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.logger.debug("Initiating Fake TCP 3-Way Handshake");
            ByteBuf var17 = var2.buffer();

            try {
               TCPPacket.writePacket(var17, null, 0, 0, this.initiatiorAddr.getPort(), this.handlerAddr.getPort(), TCPPacket.TCPFlag.SYN);
               this.completeTCPWrite(this.initiatiorAddr, this.handlerAddr, var17, var2, var1);
               TCPPacket.writePacket(var17, null, 0, 1, this.handlerAddr.getPort(), this.initiatiorAddr.getPort(), TCPPacket.TCPFlag.SYN, TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(this.handlerAddr, this.initiatiorAddr, var17, var2, var1);
               TCPPacket.writePacket(var17, null, 1, 1, this.initiatiorAddr.getPort(), this.handlerAddr.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(this.initiatiorAddr, this.handlerAddr, var17, var2, var1);
            } finally {
               var17.release();
            }

            this.logger.debug("Finished Fake TCP 3-Way Handshake");
         }

         this.initialized = true;
      }
   }

   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      this.initializeIfNecessary(var1);
      super.channelActive(var1);
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (!this.isClosed) {
         this.initializeIfNecessary(var1);
         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.handleTCP(var1, var2, false);
         } else if (this.channelType == PcapWriteHandler.ChannelType.UDP) {
            this.handleUDP(var1, var2);
         } else {
            this.logDiscard();
         }
      }

      super.channelRead(var1, var2);
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (!this.isClosed) {
         this.initializeIfNecessary(var1);
         if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
            this.handleTCP(var1, var2, true);
         } else if (this.channelType == PcapWriteHandler.ChannelType.UDP) {
            this.handleUDP(var1, var2);
         } else {
            this.logDiscard();
         }
      }

      super.write(var1, var2, var3);
   }

   private void logDiscard() {
      this.logger
         .warn(
            "Discarding pcap write because channel type is unknown. The channel this handler is registered on is not a SocketChannel or DatagramChannel, so the inference does not work. Please call forceTcpChannel or forceUdpChannel before registering the handler."
         );
   }

   private void handleTCP(ChannelHandlerContext var1, Object var2, boolean var3) {
      if (var2 instanceof ByteBuf) {
         if (((ByteBuf)var2).readableBytes() == 0 && !this.captureZeroByte) {
            this.logger.debug("Discarding Zero Byte TCP Packet. isWriteOperation {}", var3);
            return;
         }

         ByteBufAllocator var4 = var1.alloc();
         ByteBuf var5 = ((ByteBuf)var2).duplicate();
         ByteBuf var6 = var4.buffer();
         int var7 = var5.readableBytes();

         try {
            if (var3) {
               InetSocketAddress var8;
               InetSocketAddress var9;
               if (this.isServerPipeline) {
                  var8 = this.handlerAddr;
                  var9 = this.initiatiorAddr;
               } else {
                  var8 = this.initiatiorAddr;
                  var9 = this.handlerAddr;
               }

               TCPPacket.writePacket(var6, var5, this.sendSegmentNumber, this.receiveSegmentNumber, var8.getPort(), var9.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(var8, var9, var6, var4, var1);
               this.logTCP(true, var7, this.sendSegmentNumber, this.receiveSegmentNumber, var8, var9, false);
               this.sendSegmentNumber += var7;
               TCPPacket.writePacket(var6, null, this.receiveSegmentNumber, this.sendSegmentNumber, var9.getPort(), var8.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(var9, var8, var6, var4, var1);
               this.logTCP(true, var7, this.sendSegmentNumber, this.receiveSegmentNumber, var9, var8, true);
            } else {
               InetSocketAddress var13;
               InetSocketAddress var14;
               if (this.isServerPipeline) {
                  var13 = this.initiatiorAddr;
                  var14 = this.handlerAddr;
               } else {
                  var13 = this.handlerAddr;
                  var14 = this.initiatiorAddr;
               }

               TCPPacket.writePacket(var6, var5, this.receiveSegmentNumber, this.sendSegmentNumber, var13.getPort(), var14.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(var13, var14, var6, var4, var1);
               this.logTCP(false, var7, this.receiveSegmentNumber, this.sendSegmentNumber, var13, var14, false);
               this.receiveSegmentNumber += var7;
               TCPPacket.writePacket(var6, null, this.sendSegmentNumber, this.receiveSegmentNumber, var14.getPort(), var13.getPort(), TCPPacket.TCPFlag.ACK);
               this.completeTCPWrite(var14, var13, var6, var4, var1);
               this.logTCP(false, var7, this.sendSegmentNumber, this.receiveSegmentNumber, var14, var13, true);
            }
         } finally {
            var6.release();
         }
      } else {
         this.logger.debug("Discarding Pcap Write for TCP Object: {}", var2);
      }
   }

   private void completeTCPWrite(InetSocketAddress var1, InetSocketAddress var2, ByteBuf var3, ByteBufAllocator var4, ChannelHandlerContext var5) {
      ByteBuf var6 = var4.buffer();
      ByteBuf var7 = var4.buffer();
      ByteBuf var8 = var4.buffer();

      try {
         try {
            if (var1.getAddress() instanceof Inet4Address && var2.getAddress() instanceof Inet4Address) {
               IPPacket.writeTCPv4(
                  var6, var3, NetUtil.ipv4AddressToInt((Inet4Address)var1.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)var2.getAddress())
               );
               EthernetPacket.writeIPv4(var7, var6);
            } else {
               if (!(var1.getAddress() instanceof Inet6Address) || !(var2.getAddress() instanceof Inet6Address)) {
                  this.logger
                     .error(
                        "Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}",
                        var1.getAddress(),
                        var2.getAddress()
                     );
                  return;
               }

               IPPacket.writeTCPv6(var6, var3, var1.getAddress().getAddress(), var2.getAddress().getAddress());
               EthernetPacket.writeIPv6(var7, var6);
            }

            this.pCapWriter.writePacket(var8, var7);
         } catch (IOException var13) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", (Throwable)var13);
            var5.fireExceptionCaught(var13);
         }
      } finally {
         var6.release();
         var7.release();
         var8.release();
      }
   }

   private void logTCP(boolean var1, int var2, int var3, int var4, InetSocketAddress var5, InetSocketAddress var6, boolean var7) {
      if (this.logger.isDebugEnabled()) {
         if (var7) {
            this.logger.debug("Writing TCP ACK, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}", var1, var3, var4, var6, var5);
         } else {
            this.logger
               .debug(
                  "Writing TCP Data of {} Bytes, isWriteOperation {}, Segment Number {}, Ack Number {}, Src Addr {}, Dst Addr {}",
                  var2,
                  var1,
                  var3,
                  var4,
                  var5,
                  var6
               );
         }
      }
   }

   private void handleUDP(ChannelHandlerContext var1, Object var2) {
      ByteBuf var3 = var1.alloc().buffer();

      try {
         if (!(var2 instanceof DatagramPacket)) {
            if (var2 instanceof ByteBuf && (!(var1.channel() instanceof DatagramChannel) || ((DatagramChannel)var1.channel()).isConnected())) {
               if (((ByteBuf)var2).readableBytes() != 0 || this.captureZeroByte) {
                  ByteBuf var10 = ((ByteBuf)var2).duplicate();
                  this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", var10.readableBytes(), this.initiatiorAddr, this.handlerAddr);
                  UDPPacket.writePacket(var3, var10, this.initiatiorAddr.getPort(), this.handlerAddr.getPort());
                  this.completeUDPWrite(this.initiatiorAddr, this.handlerAddr, var3, var1.alloc(), var1);
                  return;
               }

               this.logger.debug("Discarding Zero Byte UDP Packet");
               return;
            }

            this.logger.debug("Discarding Pcap Write for UDP Object: {}", var2);
            return;
         }

         if (((DatagramPacket)var2).content().readableBytes() != 0 || this.captureZeroByte) {
            DatagramPacket var4 = ((DatagramPacket)var2).duplicate();
            InetSocketAddress var5 = var4.sender();
            InetSocketAddress var6 = var4.recipient();
            if (var5 == null) {
               var5 = (InetSocketAddress)var1.channel().localAddress();
            }

            this.logger.debug("Writing UDP Data of {} Bytes, Src Addr {}, Dst Addr {}", var4.content().readableBytes(), var5, var6);
            UDPPacket.writePacket(var3, var4.content(), var5.getPort(), var6.getPort());
            this.completeUDPWrite(var5, var6, var3, var1.alloc(), var1);
            return;
         }

         this.logger.debug("Discarding Zero Byte UDP Packet");
      } finally {
         var3.release();
      }
   }

   private void completeUDPWrite(InetSocketAddress var1, InetSocketAddress var2, ByteBuf var3, ByteBufAllocator var4, ChannelHandlerContext var5) {
      ByteBuf var6 = var4.buffer();
      ByteBuf var7 = var4.buffer();
      ByteBuf var8 = var4.buffer();

      try {
         try {
            if (var1.getAddress() instanceof Inet4Address && var2.getAddress() instanceof Inet4Address) {
               IPPacket.writeUDPv4(
                  var6, var3, NetUtil.ipv4AddressToInt((Inet4Address)var1.getAddress()), NetUtil.ipv4AddressToInt((Inet4Address)var2.getAddress())
               );
               EthernetPacket.writeIPv4(var7, var6);
            } else {
               if (!(var1.getAddress() instanceof Inet6Address) || !(var2.getAddress() instanceof Inet6Address)) {
                  this.logger
                     .error(
                        "Source and Destination IP Address versions are not same. Source Address: {}, Destination Address: {}",
                        var1.getAddress(),
                        var2.getAddress()
                     );
                  return;
               }

               IPPacket.writeUDPv6(var6, var3, var1.getAddress().getAddress(), var2.getAddress().getAddress());
               EthernetPacket.writeIPv6(var7, var6);
            }

            this.pCapWriter.writePacket(var8, var7);
         } catch (IOException var13) {
            this.logger.error("Caught Exception While Writing Packet into Pcap", (Throwable)var13);
            var5.fireExceptionCaught(var13);
         }
      } finally {
         var6.release();
         var7.release();
         var8.release();
      }
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
         this.logger.debug("Starting Fake TCP FIN+ACK Flow to close connection");
         ByteBufAllocator var2 = var1.alloc();
         ByteBuf var3 = var2.buffer();

         try {
            TCPPacket.writePacket(
               var3,
               null,
               this.sendSegmentNumber,
               this.receiveSegmentNumber,
               this.initiatiorAddr.getPort(),
               this.handlerAddr.getPort(),
               TCPPacket.TCPFlag.FIN,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatiorAddr, this.handlerAddr, var3, var2, var1);
            TCPPacket.writePacket(
               var3,
               null,
               this.receiveSegmentNumber,
               this.sendSegmentNumber,
               this.handlerAddr.getPort(),
               this.initiatiorAddr.getPort(),
               TCPPacket.TCPFlag.FIN,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.handlerAddr, this.initiatiorAddr, var3, var2, var1);
            TCPPacket.writePacket(
               var3,
               null,
               this.sendSegmentNumber + 1,
               this.receiveSegmentNumber + 1,
               this.initiatiorAddr.getPort(),
               this.handlerAddr.getPort(),
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatiorAddr, this.handlerAddr, var3, var2, var1);
         } finally {
            var3.release();
         }

         this.logger.debug("Finished Fake TCP FIN+ACK Flow to close connection");
      }

      this.close();
      super.handlerRemoved(var1);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.channelType == PcapWriteHandler.ChannelType.TCP) {
         ByteBuf var3 = var1.alloc().buffer();

         try {
            TCPPacket.writePacket(
               var3,
               null,
               this.sendSegmentNumber,
               this.receiveSegmentNumber,
               this.initiatiorAddr.getPort(),
               this.handlerAddr.getPort(),
               TCPPacket.TCPFlag.RST,
               TCPPacket.TCPFlag.ACK
            );
            this.completeTCPWrite(this.initiatiorAddr, this.handlerAddr, var3, var1.alloc(), var1);
         } finally {
            var3.release();
         }

         this.logger.debug("Sent Fake TCP RST to close connection");
      }

      this.close();
      var1.fireExceptionCaught(var2);
   }

   @Override
   public void close() throws IOException {
      if (this.isClosed) {
         this.logger.debug("PcapWriterHandler is already closed");
      } else {
         this.isClosed = true;
         this.pCapWriter.close();
         this.logger.debug("PcapWriterHandler is now closed");
      }
   }

   public static final class Builder {
      private boolean captureZeroByte;
      private boolean writePcapGlobalHeader = true;
      private PcapWriteHandler.ChannelType channelType;
      private InetSocketAddress initiatiorAddr;
      private InetSocketAddress handlerAddr;
      private boolean isServerPipeline;

      private Builder() {
      }

      public PcapWriteHandler.Builder captureZeroByte(boolean var1) {
         this.captureZeroByte = var1;
         return this;
      }

      public PcapWriteHandler.Builder writePcapGlobalHeader(boolean var1) {
         this.writePcapGlobalHeader = var1;
         return this;
      }

      public PcapWriteHandler.Builder forceTcpChannel(InetSocketAddress var1, InetSocketAddress var2, boolean var3) {
         this.channelType = PcapWriteHandler.ChannelType.TCP;
         this.handlerAddr = ObjectUtil.checkNotNull(var1, "serverAddress");
         this.initiatiorAddr = ObjectUtil.checkNotNull(var2, "clientAddress");
         this.isServerPipeline = var3;
         return this;
      }

      public PcapWriteHandler.Builder forceUdpChannel(InetSocketAddress var1, InetSocketAddress var2) {
         this.channelType = PcapWriteHandler.ChannelType.UDP;
         this.handlerAddr = ObjectUtil.checkNotNull(var2, "remoteAddress");
         this.initiatiorAddr = ObjectUtil.checkNotNull(var1, "localAddress");
         return this;
      }

      public PcapWriteHandler build(OutputStream var1) {
         ObjectUtil.checkNotNull(var1, "outputStream");
         return new PcapWriteHandler(this, var1);
      }
   }

   private static enum ChannelType {
      TCP,
      UDP;
   }
}
