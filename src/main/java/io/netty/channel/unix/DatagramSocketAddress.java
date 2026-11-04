package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class DatagramSocketAddress extends InetSocketAddress {
   private static final long serialVersionUID = 3094819287843178401L;
   private final int receivedAmount;
   private final DatagramSocketAddress localAddress;

   DatagramSocketAddress(byte[] var1, int var2, int var3, int var4, DatagramSocketAddress var5) throws UnknownHostException {
      super(newAddress(var1, var2), var3);
      this.receivedAmount = var4;
      this.localAddress = var5;
   }

   public DatagramSocketAddress localAddress() {
      return this.localAddress;
   }

   public int receivedAmount() {
      return this.receivedAmount;
   }

   private static InetAddress newAddress(byte[] var0, int var1) throws UnknownHostException {
      return (InetAddress)(var0.length == 4 ? InetAddress.getByAddress(var0) : Inet6Address.getByAddress(null, var0, var1));
   }
}
