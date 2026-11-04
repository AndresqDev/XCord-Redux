package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public class SegmentedDatagramPacket extends DatagramPacket {
   private final int segmentSize;

   public SegmentedDatagramPacket(ByteBuf var1, int var2, InetSocketAddress var3) {
      super(var1, var3);
      this.segmentSize = ObjectUtil.checkPositive(var2, "segmentSize");
   }

   public SegmentedDatagramPacket(ByteBuf var1, int var2, InetSocketAddress var3, InetSocketAddress var4) {
      super(var1, var3, var4);
      this.segmentSize = ObjectUtil.checkPositive(var2, "segmentSize");
   }

   public int segmentSize() {
      return this.segmentSize;
   }

   public SegmentedDatagramPacket copy() {
      return new SegmentedDatagramPacket(this.content().copy(), this.segmentSize, this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket duplicate() {
      return new SegmentedDatagramPacket(this.content().duplicate(), this.segmentSize, this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket retainedDuplicate() {
      return new SegmentedDatagramPacket(this.content().retainedDuplicate(), this.segmentSize, this.recipient(), this.sender());
   }

   public SegmentedDatagramPacket replace(ByteBuf var1) {
      return new SegmentedDatagramPacket(var1, this.segmentSize, this.recipient(), this.sender());
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
}
