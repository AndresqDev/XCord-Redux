package io.netty.resolver.dns;

import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DnsServerAddresses {
   @Deprecated
   public static List<InetSocketAddress> defaultAddressList() {
      return DefaultDnsServerAddressStreamProvider.defaultAddressList();
   }

   @Deprecated
   public static DnsServerAddresses defaultAddresses() {
      return DefaultDnsServerAddressStreamProvider.defaultAddresses();
   }

   public static DnsServerAddresses sequential(Iterable<? extends InetSocketAddress> var0) {
      return sequential0(sanitize(var0));
   }

   public static DnsServerAddresses sequential(InetSocketAddress... var0) {
      return sequential0(sanitize(var0));
   }

   private static DnsServerAddresses sequential0(List<InetSocketAddress> var0) {
      return (DnsServerAddresses)(var0.size() == 1 ? singleton((InetSocketAddress)var0.get(0)) : new DefaultDnsServerAddresses("sequential", var0) {
         @Override
         public DnsServerAddressStream stream() {
            return new SequentialDnsServerAddressStream(this.addresses, 0);
         }
      });
   }

   public static DnsServerAddresses shuffled(Iterable<? extends InetSocketAddress> var0) {
      return shuffled0(sanitize(var0));
   }

   public static DnsServerAddresses shuffled(InetSocketAddress... var0) {
      return shuffled0(sanitize(var0));
   }

   private static DnsServerAddresses shuffled0(List<InetSocketAddress> var0) {
      return (DnsServerAddresses)(var0.size() == 1 ? singleton((InetSocketAddress)var0.get(0)) : new DefaultDnsServerAddresses("shuffled", var0) {
         @Override
         public DnsServerAddressStream stream() {
            return new ShuffledDnsServerAddressStream(this.addresses);
         }
      });
   }

   public static DnsServerAddresses rotational(Iterable<? extends InetSocketAddress> var0) {
      return rotational0(sanitize(var0));
   }

   public static DnsServerAddresses rotational(InetSocketAddress... var0) {
      return rotational0(sanitize(var0));
   }

   private static DnsServerAddresses rotational0(List<InetSocketAddress> var0) {
      return (DnsServerAddresses)(var0.size() == 1 ? singleton((InetSocketAddress)var0.get(0)) : new RotationalDnsServerAddresses(var0));
   }

   public static DnsServerAddresses singleton(InetSocketAddress var0) {
      ObjectUtil.checkNotNull(var0, "address");
      if (var0.isUnresolved()) {
         throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var0);
      } else {
         return new SingletonDnsServerAddresses(var0);
      }
   }

   private static List<InetSocketAddress> sanitize(Iterable<? extends InetSocketAddress> var0) {
      ObjectUtil.checkNotNull(var0, "addresses");
      ArrayList var1;
      if (var0 instanceof Collection) {
         var1 = new ArrayList(((Collection)var0).size());
      } else {
         var1 = new ArrayList(4);
      }

      for (InetSocketAddress var3 : var0) {
         if (var3 == null) {
            break;
         }

         if (var3.isUnresolved()) {
            throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var3);
         }

         var1.add(var3);
      }

      return ObjectUtil.checkNonEmpty(var1, "list");
   }

   private static List<InetSocketAddress> sanitize(InetSocketAddress[] var0) {
      ObjectUtil.checkNotNull(var0, "addresses");
      ArrayList var1 = new ArrayList(var0.length);

      for (InetSocketAddress var5 : var0) {
         if (var5 == null) {
            break;
         }

         if (var5.isUnresolved()) {
            throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + var5);
         }

         var1.add(var5);
      }

      return (List<InetSocketAddress>)(var1.isEmpty() ? DefaultDnsServerAddressStreamProvider.defaultAddressList() : var1);
   }

   public abstract DnsServerAddressStream stream();
}
