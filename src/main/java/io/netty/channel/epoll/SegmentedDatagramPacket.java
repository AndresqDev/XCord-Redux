package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;

@Deprecated
public final class SegmentedDatagramPacket extends io.netty.channel.unix.SegmentedDatagramPacket {
   public SegmentedDatagramPacket(ByteBuf var1, int var2, InetSocketAddress var3) {
      super(var1, var2, var3);
      checkIsSupported();
   }

   public SegmentedDatagramPacket(ByteBuf var1, int var2, InetSocketAddress var3, InetSocketAddress var4) {
      super(var1, var2, var3, var4);
      checkIsSupported();
   }

   public static boolean isSupported() {
      return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
   }

   public SegmentedDatagramPacket copy() {
      return new SegmentedDatagramPacket(this.content().copy(), this.segmentSize(), this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket duplicate() {
      return new SegmentedDatagramPacket(this.content().duplicate(), this.segmentSize(), this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket retainedDuplicate() {
      return new SegmentedDatagramPacket(this.content().retainedDuplicate(), this.segmentSize(), this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket replace(ByteBuf var1) {
      return new SegmentedDatagramPacket(var1, this.segmentSize(), this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket retain() {
      super.retain();
      return this;
   }

   public SegmentedDatagramPacket retain(int var1) {
      super.retain(var1);
      return this;
   }

   public SegmentedDatagramPacket touch() {
      super.touch();
      return this;
   }

   public SegmentedDatagramPacket touch(Object var1) {
      super.touch(var1);
      return this;
   }

   private static void checkIsSupported() {
      if (!isSupported()) {
         throw new IllegalStateException();
      }
   }
}
