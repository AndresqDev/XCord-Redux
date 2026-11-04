package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ChannelHandler.Sharable
public class IpSubnetFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final boolean acceptIfNotFound;
   private final List<IpSubnetFilterRule> ipv4Rules;
   private final List<IpSubnetFilterRule> ipv6Rules;
   private final IpFilterRuleType ipFilterRuleTypeIPv4;
   private final IpFilterRuleType ipFilterRuleTypeIPv6;

   public IpSubnetFilter(IpSubnetFilterRule... var1) {
      this(true, Arrays.asList(ObjectUtil.checkNotNull(var1, "rules")));
   }

   public IpSubnetFilter(boolean var1, IpSubnetFilterRule... var2) {
      this(var1, Arrays.asList(ObjectUtil.checkNotNull(var2, "rules")));
   }

   public IpSubnetFilter(List<IpSubnetFilterRule> var1) {
      this(true, var1);
   }

   public IpSubnetFilter(boolean var1, List<IpSubnetFilterRule> var2) {
      ObjectUtil.checkNotNull(var2, "rules");
      this.acceptIfNotFound = var1;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();

      for (IpSubnetFilterRule var10 : var2) {
         ObjectUtil.checkNotNull(var10, "rule");
         if (var10.getFilterRule() instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
            var7.add(var10);
            if (var10.ruleType() == IpFilterRuleType.ACCEPT) {
               var3++;
            } else {
               var4++;
            }
         } else {
            var8.add(var10);
            if (var10.ruleType() == IpFilterRuleType.ACCEPT) {
               var5++;
            } else {
               var6++;
            }
         }
      }

      if (var3 == 0 && var4 > 0) {
         this.ipFilterRuleTypeIPv4 = IpFilterRuleType.REJECT;
      } else if (var3 > 0 && var4 == 0) {
         this.ipFilterRuleTypeIPv4 = IpFilterRuleType.ACCEPT;
      } else {
         this.ipFilterRuleTypeIPv4 = null;
      }

      if (var5 == 0 && var6 > 0) {
         this.ipFilterRuleTypeIPv6 = IpFilterRuleType.REJECT;
      } else if (var5 > 0 && var6 == 0) {
         this.ipFilterRuleTypeIPv6 = IpFilterRuleType.ACCEPT;
      } else {
         this.ipFilterRuleTypeIPv6 = null;
      }

      this.ipv4Rules = sortAndFilter(var7);
      this.ipv6Rules = sortAndFilter(var8);
   }

   protected boolean accept(ChannelHandlerContext var1, InetSocketAddress var2) {
      if (var2.getAddress() instanceof Inet4Address) {
         int var3 = Collections.binarySearch(this.ipv4Rules, var2, IpSubnetFilterRuleComparator.INSTANCE);
         if (var3 >= 0) {
            if (this.ipFilterRuleTypeIPv4 == null) {
               return this.ipv4Rules.get(var3).ruleType() == IpFilterRuleType.ACCEPT;
            }

            return this.ipFilterRuleTypeIPv4 == IpFilterRuleType.ACCEPT;
         }
      } else {
         int var4 = Collections.binarySearch(this.ipv6Rules, var2, IpSubnetFilterRuleComparator.INSTANCE);
         if (var4 >= 0) {
            if (this.ipFilterRuleTypeIPv6 == null) {
               return this.ipv6Rules.get(var4).ruleType() == IpFilterRuleType.ACCEPT;
            }

            return this.ipFilterRuleTypeIPv6 == IpFilterRuleType.ACCEPT;
         }
      }

      return this.acceptIfNotFound;
   }

   private static List<IpSubnetFilterRule> sortAndFilter(List<IpSubnetFilterRule> var0) {
      Collections.sort(var0);
      Iterator var1 = var0.iterator();
      ArrayList var2 = new ArrayList();
      IpSubnetFilterRule var3 = var1.hasNext() ? (IpSubnetFilterRule)var1.next() : null;
      if (var3 != null) {
         var2.add(var3);
      }

      while (var1.hasNext()) {
         IpSubnetFilterRule var4 = (IpSubnetFilterRule)var1.next();
         if (!var3.matches(new InetSocketAddress(var4.getIpAddress(), 1))) {
            var2.add(var4);
            var3 = var4;
         }
      }

      return var2;
   }
}
