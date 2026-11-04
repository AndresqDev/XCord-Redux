package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.NativeInetAddress;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

final class NativeDatagramPacketArray {
   private final NativeDatagramPacketArray.NativeDatagramPacket[] packets = new NativeDatagramPacketArray.NativeDatagramPacket[Limits.UIO_MAX_IOV];
   private final IovArray iovArray = new IovArray();
   private final byte[] ipv4Bytes = new byte[4];
   private final NativeDatagramPacketArray.MyMessageProcessor processor = new NativeDatagramPacketArray.MyMessageProcessor();
   private int count;

   NativeDatagramPacketArray() {
      for (int var1 = 0; var1 < this.packets.length; var1++) {
         this.packets[var1] = new NativeDatagramPacketArray.NativeDatagramPacket();
      }
   }

   boolean addWritable(ByteBuf var1, int var2, int var3) {
      return this.add0(var1, var2, var3, 0, null);
   }

   private boolean add0(ByteBuf var1, int var2, int var3, int var4, InetSocketAddress var5) {
      if (this.count == this.packets.length) {
         return false;
      } else if (var3 == 0) {
         return true;
      } else {
         int var6 = this.iovArray.count();
         if (var6 != Limits.IOV_MAX && this.iovArray.add(var1, var2, var3)) {
            NativeDatagramPacketArray.NativeDatagramPacket var7 = this.packets[this.count];
            var7.init(this.iovArray.memoryAddress(var6), this.iovArray.count() - var6, var4, var5);
            this.count++;
            return true;
         } else {
            return false;
         }
      }
   }

   void add(ChannelOutboundBuffer var1, boolean var2, int var3) throws Exception {
      this.processor.connected = var2;
      this.processor.maxMessagesPerWrite = var3;
      var1.forEachFlushedMessage(this.processor);
   }

   int count() {
      return this.count;
   }

   NativeDatagramPacketArray.NativeDatagramPacket[] packets() {
      return this.packets;
   }

   void clear() {
      this.count = 0;
      this.iovArray.clear();
   }

   void release() {
      this.iovArray.release();
   }

   private static InetSocketAddress newAddress(byte[] var0, int var1, int var2, int var3, byte[] var4) throws UnknownHostException {
      Object var5;
      if (var1 == var4.length) {
         System.arraycopy(var0, 0, var4, 0, var1);
         var5 = InetAddress.getByAddress(var4);
      } else {
         var5 = Inet6Address.getByAddress(null, var0, var3);
      }

      return new InetSocketAddress((InetAddress)var5, var2);
   }

   private final class MyMessageProcessor implements ChannelOutboundBuffer.MessageProcessor {
      private boolean connected;
      private int maxMessagesPerWrite;

      private MyMessageProcessor() {
      }

      @Override
      public boolean processMessage(Object var1) {
         boolean var2;
         if (var1 instanceof DatagramPacket) {
            DatagramPacket var3 = (DatagramPacket)var1;
            ByteBuf var4 = var3.content();
            int var5 = 0;
            if (var3 instanceof io.netty.channel.unix.SegmentedDatagramPacket) {
               int var6 = ((io.netty.channel.unix.SegmentedDatagramPacket)var3).segmentSize();
               if (var4.readableBytes() > var6) {
                  var5 = var6;
               }
            }

            var2 = NativeDatagramPacketArray.this.add0(var4, var4.readerIndex(), var4.readableBytes(), var5, var3.recipient());
         } else if (var1 instanceof ByteBuf && this.connected) {
            ByteBuf var7 = (ByteBuf)var1;
            var2 = NativeDatagramPacketArray.this.add0(var7, var7.readerIndex(), var7.readableBytes(), 0, null);
         } else {
            var2 = false;
         }

         if (var2) {
            this.maxMessagesPerWrite--;
            return this.maxMessagesPerWrite > 0;
         } else {
            return false;
         }
      }
   }

   public final class NativeDatagramPacket {
      private long memoryAddress;
      private int count;
      private final byte[] senderAddr = new byte[16];
      private int senderAddrLen;
      private int senderScopeId;
      private int senderPort;
      private final byte[] recipientAddr = new byte[16];
      private int recipientAddrLen;
      private int recipientScopeId;
      private int recipientPort;
      private int segmentSize;

      private void init(long var1, int var3, int var4, InetSocketAddress var5) {
         this.memoryAddress = var1;
         this.count = var3;
         this.segmentSize = var4;
         this.senderScopeId = 0;
         this.senderPort = 0;
         this.senderAddrLen = 0;
         if (var5 == null) {
            this.recipientScopeId = 0;
            this.recipientPort = 0;
            this.recipientAddrLen = 0;
         } else {
            InetAddress var6 = var5.getAddress();
            if (var6 instanceof Inet6Address) {
               System.arraycopy(var6.getAddress(), 0, this.recipientAddr, 0, this.recipientAddr.length);
               this.recipientScopeId = ((Inet6Address)var6).getScopeId();
            } else {
               NativeInetAddress.copyIpv4MappedIpv6Address(var6.getAddress(), this.recipientAddr);
               this.recipientScopeId = 0;
            }

            this.recipientAddrLen = this.recipientAddr.length;
            this.recipientPort = var5.getPort();
         }
      }

      boolean hasSender() {
         return this.senderPort > 0;
      }

      DatagramPacket newDatagramPacket(ByteBuf var1, InetSocketAddress var2) throws UnknownHostException {
         InetSocketAddress var3 = NativeDatagramPacketArray.newAddress(
            this.senderAddr, this.senderAddrLen, this.senderPort, this.senderScopeId, NativeDatagramPacketArray.this.ipv4Bytes
         );
         if (this.recipientAddrLen != 0) {
            var2 = NativeDatagramPacketArray.newAddress(
               this.recipientAddr, this.recipientAddrLen, this.recipientPort, this.recipientScopeId, NativeDatagramPacketArray.this.ipv4Bytes
            );
         }

         return (DatagramPacket)(this.segmentSize > 0
            ? new io.netty.channel.unix.SegmentedDatagramPacket(var1, this.segmentSize, var2, var3)
            : new DatagramPacket(var1, var2, var3));
      }
   }
}
