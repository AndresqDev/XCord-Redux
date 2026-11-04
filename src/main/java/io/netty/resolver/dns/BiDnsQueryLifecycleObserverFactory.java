package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.internal.ObjectUtil;

public final class BiDnsQueryLifecycleObserverFactory implements DnsQueryLifecycleObserverFactory {
   private final DnsQueryLifecycleObserverFactory a;
   private final DnsQueryLifecycleObserverFactory b;

   public BiDnsQueryLifecycleObserverFactory(DnsQueryLifecycleObserverFactory var1, DnsQueryLifecycleObserverFactory var2) {
      this.a = ObjectUtil.checkNotNull(var1, "a");
      this.b = ObjectUtil.checkNotNull(var2, "b");
   }

   @Override
   public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1) {
      return new BiDnsQueryLifecycleObserver(this.a.newDnsQueryLifecycleObserver(var1), this.b.newDnsQueryLifecycleObserver(var1));
   }
}
