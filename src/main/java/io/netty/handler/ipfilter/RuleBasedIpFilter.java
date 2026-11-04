package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class RuleBasedIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {
   private final boolean acceptIfNotFound;
   private final List<IpFilterRule> rules;

   public RuleBasedIpFilter(IpFilterRule... var1) {
      this(true, var1);
   }

   public RuleBasedIpFilter(boolean var1, IpFilterRule... var2) {
      ObjectUtil.checkNotNull(var2, "rules");
      this.acceptIfNotFound = var1;
      this.rules = new ArrayList<>(var2.length);

      for (IpFilterRule var6 : var2) {
         if (var6 != null) {
            this.rules.add(var6);
         }
      }
   }

   protected boolean accept(ChannelHandlerContext var1, InetSocketAddress var2) throws Exception {
      for (IpFilterRule var4 : this.rules) {
         if (var4.matches(var2)) {
            return var4.ruleType() == IpFilterRuleType.ACCEPT;
         }
      }

      return this.acceptIfNotFound;
   }
}
