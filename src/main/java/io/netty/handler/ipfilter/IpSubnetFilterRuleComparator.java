package io.netty.handler.ipfilter;

import java.net.InetSocketAddress;
import java.util.Comparator;

final class IpSubnetFilterRuleComparator implements Comparator<Object> {
   static final IpSubnetFilterRuleComparator INSTANCE = new IpSubnetFilterRuleComparator();

   private IpSubnetFilterRuleComparator() {
   }

   @Override
   public int compare(Object var1, Object var2) {
      return ((IpSubnetFilterRule)var1).compareTo((InetSocketAddress)var2);
   }
}
