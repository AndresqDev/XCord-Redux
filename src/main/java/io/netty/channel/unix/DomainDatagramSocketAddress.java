package io.netty.channel.unix;

import io.netty.util.CharsetUtil;

public final class DomainDatagramSocketAddress extends DomainSocketAddress {
   private static final long serialVersionUID = -5925732678737768223L;
   private final DomainDatagramSocketAddress localAddress;
   private final int receivedAmount;

   public DomainDatagramSocketAddress(byte[] var1, int var2, DomainDatagramSocketAddress var3) {
      super(new String(var1, CharsetUtil.UTF_8));
      this.localAddress = var3;
      this.receivedAmount = var2;
   }

   public DomainDatagramSocketAddress localAddress() {
      return this.localAddress;
   }

   public int receivedAmount() {
      return this.receivedAmount;
   }
}
