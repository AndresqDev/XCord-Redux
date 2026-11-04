package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefaultDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDnsServerAddressStreamProvider.class);
   public static final DefaultDnsServerAddressStreamProvider INSTANCE = new DefaultDnsServerAddressStreamProvider();
   private static final List<InetSocketAddress> DEFAULT_NAME_SERVER_LIST;
   private static final DnsServerAddresses DEFAULT_NAME_SERVERS;
   static final int DNS_PORT = 53;

   private DefaultDnsServerAddressStreamProvider() {
   }

   @Override
   public DnsServerAddressStream nameServerAddressStream(String var1) {
      return DEFAULT_NAME_SERVERS.stream();
   }

   public static List<InetSocketAddress> defaultAddressList() {
      return DEFAULT_NAME_SERVER_LIST;
   }

   public static DnsServerAddresses defaultAddresses() {
      return DEFAULT_NAME_SERVERS;
   }

   static {
      ArrayList var0 = new ArrayList(2);
      if (!PlatformDependent.isAndroid()) {
         DirContextUtils.addNameServers(var0, 53);
      }

      if (PlatformDependent.javaVersion() < 9 && var0.isEmpty()) {
         try {
            Class var1 = Class.forName("sun.net.dns.ResolverConfiguration");
            Method var2 = var1.getMethod("open");
            Method var3 = var1.getMethod("nameservers");
            Object var4 = var2.invoke(null);

            for (String var7 : (List)var3.invoke(var4)) {
               if (var7 != null) {
                  var0.add(new InetSocketAddress(SocketUtils.addressByName(var7), 53));
               }
            }
         } catch (Exception var8) {
         }
      }

      if (!var0.isEmpty()) {
         if (logger.isDebugEnabled()) {
            logger.debug("Default DNS servers: {} (sun.net.dns.ResolverConfiguration)", var0);
         }
      } else {
         if (!NetUtil.isIpV6AddressesPreferred() && (!(NetUtil.LOCALHOST instanceof Inet6Address) || NetUtil.isIpV4StackPreferred())) {
            Collections.addAll(var0, SocketUtils.socketAddress("8.8.8.8", 53), SocketUtils.socketAddress("8.8.4.4", 53));
         } else {
            Collections.addAll(var0, SocketUtils.socketAddress("2001:4860:4860::8888", 53), SocketUtils.socketAddress("2001:4860:4860::8844", 53));
         }

         if (logger.isWarnEnabled()) {
            logger.warn("Default DNS servers: {} (Google Public DNS as a fallback)", var0);
         }
      }

      DEFAULT_NAME_SERVER_LIST = Collections.unmodifiableList(var0);
      DEFAULT_NAME_SERVERS = DnsServerAddresses.sequential(DEFAULT_NAME_SERVER_LIST);
   }
}
