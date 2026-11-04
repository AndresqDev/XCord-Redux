package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;

public final class DomainDatagramPacket extends DefaultAddressedEnvelope<ByteBuf, DomainSocketAddress> implements ByteBufHolder {
   public DomainDatagramPacket(ByteBuf var1, DomainSocketAddress var2) {
      super(var1, var2);
   }

   public DomainDatagramPacket(ByteBuf var1, DomainSocketAddress var2, DomainSocketAddress var3) {
      super(var1, var2, var3);
   }

   public DomainDatagramPacket copy() {
      return this.replace(this.content().copy());
   }

   public DomainDatagramPacket duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DomainDatagramPacket replace(ByteBuf var1) {
      return new DomainDatagramPacket(var1, this.recipient(), this.sender());
   }

   public DomainDatagramPacket retain() {
      super.retain();
      return this;
   }

   public DomainDatagramPacket retain(int var1) {
      super.retain(var1);
      return this;
   }

   public DomainDatagramPacket retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DomainDatagramPacket touch() {
      super.touch();
      return this;
   }

   public DomainDatagramPacket touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
