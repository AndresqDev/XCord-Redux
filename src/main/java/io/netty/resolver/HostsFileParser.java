package io.netty.resolver;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class HostsFileParser {
   public static HostsFileEntries parseSilently() {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently());
   }

   public static HostsFileEntries parseSilently(Charset... var0) {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parseSilently(var0));
   }

   public static HostsFileEntries parse() throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse());
   }

   public static HostsFileEntries parse(File var0) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(var0));
   }

   public static HostsFileEntries parse(File var0, Charset... var1) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(var0, var1));
   }

   public static HostsFileEntries parse(Reader var0) throws IOException {
      return hostsFileEntries(HostsFileEntriesProvider.parser().parse(var0));
   }

   private HostsFileParser() {
   }

   private static HostsFileEntries hostsFileEntries(HostsFileEntriesProvider var0) {
      return var0 == HostsFileEntriesProvider.EMPTY
         ? HostsFileEntries.EMPTY
         : new HostsFileEntries(
            (Map<String, Inet4Address>)toMapWithSingleValue(var0.ipv4Entries()), (Map<String, Inet6Address>)toMapWithSingleValue(var0.ipv6Entries())
         );
   }

   private static Map<String, ?> toMapWithSingleValue(Map<String, List<InetAddress>> var0) {
      HashMap var1 = new HashMap(var0.size());

      for (Entry var3 : var0.entrySet()) {
         List var4 = (List)var3.getValue();
         if (!var4.isEmpty()) {
            var1.put(var3.getKey(), var4.get(0));
         }
      }

      return var1;
   }
}
