package io.netty.handler.ipfilter;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class IpSubnetFilterRule implements IpFilterRule, Comparable<IpSubnetFilterRule> {
   private final IpFilterRule filterRule;
   private final String ipAddress;

   public IpSubnetFilterRule(String var1, int var2, IpFilterRuleType var3) {
      try {
         this.ipAddress = var1;
         this.filterRule = selectFilterRule(SocketUtils.addressByName(var1), var2, var3);
      } catch (UnknownHostException var5) {
         throw new IllegalArgumentException("ipAddress", var5);
      }
   }

   public IpSubnetFilterRule(InetAddress var1, int var2, IpFilterRuleType var3) {
      this.ipAddress = var1.getHostAddress();
      this.filterRule = selectFilterRule(var1, var2, var3);
   }

   private static IpFilterRule selectFilterRule(InetAddress var0, int var1, IpFilterRuleType var2) {
      ObjectUtil.checkNotNull(var0, "ipAddress");
      ObjectUtil.checkNotNull(var2, "ruleType");
      if (var0 instanceof Inet4Address) {
         return new IpSubnetFilterRule.Ip4SubnetFilterRule((Inet4Address)var0, var1, var2);
      } else if (var0 instanceof Inet6Address) {
         return new IpSubnetFilterRule.Ip6SubnetFilterRule((Inet6Address)var0, var1, var2);
      } else {
         throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
      }
   }

   @Override
   public boolean matches(InetSocketAddress var1) {
      return this.filterRule.matches(var1);
   }

   @Override
   public IpFilterRuleType ruleType() {
      return this.filterRule.ruleType();
   }

   String getIpAddress() {
      return this.ipAddress;
   }

   IpFilterRule getFilterRule() {
      return this.filterRule;
   }

   public int compareTo(IpSubnetFilterRule var1) {
      return this.filterRule instanceof IpSubnetFilterRule.Ip4SubnetFilterRule
         ? compareInt(
            ((IpSubnetFilterRule.Ip4SubnetFilterRule)this.filterRule).networkAddress, ((IpSubnetFilterRule.Ip4SubnetFilterRule)var1.filterRule).networkAddress
         )
         : ((IpSubnetFilterRule.Ip6SubnetFilterRule)this.filterRule)
            .networkAddress
            .compareTo(((IpSubnetFilterRule.Ip6SubnetFilterRule)var1.filterRule).networkAddress);
   }

   int compareTo(InetSocketAddress var1) {
      if (this.filterRule instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
         IpSubnetFilterRule.Ip4SubnetFilterRule var3 = (IpSubnetFilterRule.Ip4SubnetFilterRule)this.filterRule;
         return compareInt(var3.networkAddress, NetUtil.ipv4AddressToInt((Inet4Address)var1.getAddress()) & var3.subnetMask);
      } else {
         IpSubnetFilterRule.Ip6SubnetFilterRule var2 = (IpSubnetFilterRule.Ip6SubnetFilterRule)this.filterRule;
         return var2.networkAddress.compareTo(IpSubnetFilterRule.Ip6SubnetFilterRule.ipToInt((Inet6Address)var1.getAddress()).and(var2.networkAddress));
      }
   }

   private static int compareInt(int var0, int var1) {
      return var0 < var1 ? -1 : (var0 == var1 ? 0 : 1);
   }

   static final class Ip4SubnetFilterRule implements IpFilterRule {
      private final int networkAddress;
      private final int subnetMask;
      private final IpFilterRuleType ruleType;

      private Ip4SubnetFilterRule(Inet4Address var1, int var2, IpFilterRuleType var3) {
         if (var2 >= 0 && var2 <= 32) {
            this.subnetMask = prefixToSubnetMask(var2);
            this.networkAddress = NetUtil.ipv4AddressToInt(var1) & this.subnetMask;
            this.ruleType = var3;
         } else {
            throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", var2));
         }
      }

      @Override
      public boolean matches(InetSocketAddress var1) {
         InetAddress var2 = var1.getAddress();
         if (var2 instanceof Inet4Address) {
            int var3 = NetUtil.ipv4AddressToInt((Inet4Address)var2);
            return (var3 & this.subnetMask) == this.networkAddress;
         } else {
            return false;
         }
      }

      @Override
      public IpFilterRuleType ruleType() {
         return this.ruleType;
      }

      private static int prefixToSubnetMask(int var0) {
         return (int)(-1L << 32 - var0);
      }
   }

   static final class Ip6SubnetFilterRule implements IpFilterRule {
      private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1L);
      private final BigInteger networkAddress;
      private final BigInteger subnetMask;
      private final IpFilterRuleType ruleType;

      private Ip6SubnetFilterRule(Inet6Address var1, int var2, IpFilterRuleType var3) {
         if (var2 >= 0 && var2 <= 128) {
            this.subnetMask = prefixToSubnetMask(var2);
            this.networkAddress = ipToInt(var1).and(this.subnetMask);
            this.ruleType = var3;
         } else {
            throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", var2));
         }
      }

      @Override
      public boolean matches(InetSocketAddress var1) {
         InetAddress var2 = var1.getAddress();
         if (!(var2 instanceof Inet6Address)) {
            return false;
         } else {
            BigInteger var3 = ipToInt((Inet6Address)var2);
            return var3.and(this.subnetMask).equals(this.subnetMask) || var3.and(this.subnetMask).equals(this.networkAddress);
         }
      }

      @Override
      public IpFilterRuleType ruleType() {
         return this.ruleType;
      }

      private static BigInteger ipToInt(Inet6Address var0) {
         byte[] var1 = var0.getAddress();

         assert var1.length == 16;

         return new BigInteger(var1);
      }

      private static BigInteger prefixToSubnetMask(int var0) {
         return MINUS_ONE.shiftLeft(128 - var0);
      }
   }
}
