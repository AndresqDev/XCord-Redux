package io.netty.resolver.dns.macos;

import io.netty.channel.unix.NativeInetAddress;
import java.net.InetSocketAddress;

final class DnsResolver {
   private final String domain;
   private final InetSocketAddress[] nameservers;
   private final int port;
   private final String[] searches;
   private final String options;
   private final int timeout;
   private final int searchOrder;

   DnsResolver(String var1, byte[][] var2, int var3, String[] var4, String var5, int var6, int var7) {
      this.domain = var1;
      if (var2 == null) {
         this.nameservers = new InetSocketAddress[0];
      } else {
         this.nameservers = new InetSocketAddress[var2.length];

         for (int var8 = 0; var8 < var2.length; var8++) {
            byte[] var9 = var2[var8];
            this.nameservers[var8] = NativeInetAddress.address(var9, 0, var9.length);
         }
      }

      this.port = var3;
      this.searches = var4;
      this.options = var5;
      this.timeout = var6;
      this.searchOrder = var7;
   }

   String domain() {
      return this.domain;
   }

   InetSocketAddress[] nameservers() {
      return this.nameservers;
   }

   int port() {
      return this.port;
   }

   String[] searches() {
      return this.searches;
   }

   String options() {
      return this.options;
   }

   int timeout() {
      return this.timeout;
   }

   int searchOrder() {
      return this.searchOrder;
   }
}
