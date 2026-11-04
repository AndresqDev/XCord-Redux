package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DnsAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {
   private final DnsNameResolverBuilder dnsResolverBuilder;
   private final ConcurrentMap<String, Promise<InetAddress>> resolvesInProgress = PlatformDependent.newConcurrentHashMap();
   private final ConcurrentMap<String, Promise<List<InetAddress>>> resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();

   public DnsAddressResolverGroup(DnsNameResolverBuilder var1) {
      this.dnsResolverBuilder = var1.copy();
   }

   public DnsAddressResolverGroup(Class<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      this.dnsResolverBuilder = new DnsNameResolverBuilder();
      this.dnsResolverBuilder.channelType(var1).nameServerProvider(var2);
   }

   public DnsAddressResolverGroup(ChannelFactory<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      this.dnsResolverBuilder = new DnsNameResolverBuilder();
      this.dnsResolverBuilder.channelFactory(var1).nameServerProvider(var2);
   }

   @Override
   protected final AddressResolver<InetSocketAddress> newResolver(EventExecutor var1) throws Exception {
      if (!(var1 instanceof EventLoop)) {
         throw new IllegalStateException(
            "unsupported executor type: " + StringUtil.simpleClassName(var1) + " (expected: " + StringUtil.simpleClassName(EventLoop.class)
         );
      } else {
         EventLoop var2 = this.dnsResolverBuilder.eventLoop;
         return this.newResolver(var2 == null ? (EventLoop)var1 : var2, this.dnsResolverBuilder.channelFactory(), this.dnsResolverBuilder.nameServerProvider());
      }
   }

   @Deprecated
   protected AddressResolver<InetSocketAddress> newResolver(EventLoop var1, ChannelFactory<? extends DatagramChannel> var2, DnsServerAddressStreamProvider var3) throws Exception {
      InflightNameResolver var4 = new InflightNameResolver<>(var1, this.newNameResolver(var1, var2, var3), this.resolvesInProgress, this.resolveAllsInProgress);
      return this.newAddressResolver(var1, var4);
   }

   protected NameResolver<InetAddress> newNameResolver(EventLoop var1, ChannelFactory<? extends DatagramChannel> var2, DnsServerAddressStreamProvider var3) throws Exception {
      DnsNameResolverBuilder var4 = this.dnsResolverBuilder.copy();
      return var4.eventLoop(var1).channelFactory(var2).nameServerProvider(var3).build();
   }

   protected AddressResolver<InetSocketAddress> newAddressResolver(EventLoop var1, NameResolver<InetAddress> var2) throws Exception {
      return new InetSocketAddressResolver(var1, var2);
   }
}
