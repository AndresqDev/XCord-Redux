package io.netty.resolver.dns;

import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

final class DirContextUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DirContextUtils.class);

   private DirContextUtils() {
   }

   static void addNameServers(List<InetSocketAddress> var0, int var1) {
      Hashtable var2 = new Hashtable();
      var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      var2.put("java.naming.provider.url", "dns://");

      try {
         InitialDirContext var3 = new InitialDirContext(var2);
         String var4 = (String)var3.getEnvironment().get("java.naming.provider.url");
         if (var4 != null && !var4.isEmpty()) {
            String[] var5 = var4.split(" ");

            for (String var9 : var5) {
               try {
                  URI var10 = new URI(var9);
                  String var11 = new URI(var9).getHost();
                  if (var11 != null && !var11.isEmpty()) {
                     int var12 = var10.getPort();
                     var0.add(SocketUtils.socketAddress(var10.getHost(), var12 == -1 ? var1 : var12));
                  } else {
                     logger.debug("Skipping a nameserver URI as host portion could not be extracted: {}", var9);
                  }
               } catch (URISyntaxException var13) {
                  logger.debug("Skipping a malformed nameserver URI: {}", var9, var13);
               }
            }
         }
      } catch (NamingException var14) {
      }
   }
}
