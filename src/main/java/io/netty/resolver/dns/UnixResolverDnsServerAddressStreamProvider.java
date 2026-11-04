package io.netty.resolver.dns;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class UnixResolverDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
   private static final String RES_OPTIONS = System.getenv("RES_OPTIONS");
   private static final String ETC_RESOLV_CONF_FILE = "/etc/resolv.conf";
   private static final String ETC_RESOLVER_DIR = "/etc/resolver";
   private static final String NAMESERVER_ROW_LABEL = "nameserver";
   private static final String SORTLIST_ROW_LABEL = "sortlist";
   private static final String OPTIONS_ROW_LABEL = "options ";
   private static final String OPTIONS_ROTATE_FLAG = "rotate";
   private static final String DOMAIN_ROW_LABEL = "domain";
   private static final String SEARCH_ROW_LABEL = "search";
   private static final String PORT_ROW_LABEL = "port";
   private final DnsServerAddresses defaultNameServerAddresses;
   private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;

   static DnsServerAddressStreamProvider parseSilently() {
      try {
         UnixResolverDnsServerAddressStreamProvider var0 = new UnixResolverDnsServerAddressStreamProvider("/etc/resolv.conf", "/etc/resolver");
         return (DnsServerAddressStreamProvider)(var0.mayOverrideNameServers() ? var0 : DefaultDnsServerAddressStreamProvider.INSTANCE);
      } catch (Exception var1) {
         if (logger.isDebugEnabled()) {
            logger.debug("failed to parse {} and/or {}", "/etc/resolv.conf", "/etc/resolver", var1);
         }

         return DefaultDnsServerAddressStreamProvider.INSTANCE;
      }
   }

   public UnixResolverDnsServerAddressStreamProvider(File var1, File... var2) throws IOException {
      Map var3 = parse(ObjectUtil.checkNotNull(var1, "etcResolvConf"));
      boolean var4 = var2 != null && var2.length != 0;
      this.domainToNameServerStreamMap = var4 ? parse(var2) : var3;
      DnsServerAddresses var5 = (DnsServerAddresses)var3.get(var1.getName());
      if (var5 == null) {
         Collection var6 = var3.values();
         if (var6.isEmpty()) {
            throw new IllegalArgumentException(var1 + " didn't provide any name servers");
         }

         this.defaultNameServerAddresses = (DnsServerAddresses)var6.iterator().next();
      } else {
         this.defaultNameServerAddresses = var5;
      }

      if (var4) {
         this.domainToNameServerStreamMap.putAll(var3);
      }
   }

   public UnixResolverDnsServerAddressStreamProvider(String var1, String var2) throws IOException {
      this(var1 == null ? null : new File(var1), var2 == null ? null : new File(var2).listFiles());
   }

   @Override
   public DnsServerAddressStream nameServerAddressStream(String var1) {
      while (true) {
         int var2 = var1.indexOf(46, 1);
         if (var2 < 0 || var2 == var1.length() - 1) {
            return this.defaultNameServerAddresses.stream();
         }

         DnsServerAddresses var3 = this.domainToNameServerStreamMap.get(var1);
         if (var3 != null) {
            return var3.stream();
         }

         var1 = var1.substring(var2 + 1);
      }
   }

   private boolean mayOverrideNameServers() {
      return !this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses.stream().next() != null;
   }

   private static Map<String, DnsServerAddresses> parse(File... var0) throws IOException {
      HashMap var1 = new HashMap(var0.length << 1);
      boolean var2 = RES_OPTIONS != null && RES_OPTIONS.contains("rotate");

      for (File var6 : var0) {
         if (var6.isFile()) {
            FileReader var7 = new FileReader(var6);
            BufferedReader var8 = null;

            try {
               var8 = new BufferedReader(var7);
               ArrayList var9 = new ArrayList(2);
               String var10 = var6.getName();
               boolean var11 = var2;
               int var12 = 53;

               String var13;
               while ((var13 = var8.readLine()) != null) {
                  var13 = var13.trim();

                  try {
                     char var14;
                     if (!var13.isEmpty() && (var14 = var13.charAt(0)) != '#' && var14 != ';') {
                        if (!var11 && var13.startsWith("options ")) {
                           var11 = var13.contains("rotate");
                        } else if (var13.startsWith("nameserver")) {
                           int var26 = StringUtil.indexOfNonWhiteSpace(var13, "nameserver".length());
                           if (var26 < 0) {
                              throw new IllegalArgumentException("error parsing label nameserver in file " + var6 + ". value: " + var13);
                           }

                           int var17 = StringUtil.indexOfWhiteSpace(var13, var26);
                           String var16;
                           if (var17 == -1) {
                              var16 = var13.substring(var26);
                           } else {
                              int var18 = StringUtil.indexOfNonWhiteSpace(var13, var17);
                              if (var18 == -1 || var13.charAt(var18) != '#') {
                                 throw new IllegalArgumentException("error parsing label nameserver in file " + var6 + ". value: " + var13);
                              }

                              var16 = var13.substring(var26, var17);
                           }

                           if (!NetUtil.isValidIpV4Address(var16) && !NetUtil.isValidIpV6Address(var16)) {
                              var26 = var16.lastIndexOf(46);
                              if (var26 + 1 >= var16.length()) {
                                 throw new IllegalArgumentException("error parsing label nameserver in file " + var6 + ". invalid IP value: " + var13);
                              }

                              var12 = Integer.parseInt(var16.substring(var26 + 1));
                              var16 = var16.substring(0, var26);
                           }

                           var9.add(SocketUtils.socketAddress(var16, var12));
                        } else if (var13.startsWith("domain")) {
                           int var15 = StringUtil.indexOfNonWhiteSpace(var13, "domain".length());
                           if (var15 < 0) {
                              throw new IllegalArgumentException("error parsing label domain in file " + var6 + " value: " + var13);
                           }

                           var10 = var13.substring(var15);
                           if (!var9.isEmpty()) {
                              putIfAbsent(var1, var10, var9, var11);
                           }

                           var9 = new ArrayList(2);
                        } else if (var13.startsWith("port")) {
                           int var25 = StringUtil.indexOfNonWhiteSpace(var13, "port".length());
                           if (var25 < 0) {
                              throw new IllegalArgumentException("error parsing label port in file " + var6 + " value: " + var13);
                           }

                           var12 = Integer.parseInt(var13.substring(var25));
                        } else if (var13.startsWith("sortlist")) {
                           logger.info("row type {} not supported. Ignoring line: {}", "sortlist", var13);
                        }
                     }
                  } catch (IllegalArgumentException var22) {
                     logger.warn("Could not parse entry. Ignoring line: {}", var13, var22);
                  }
               }

               if (!var9.isEmpty()) {
                  putIfAbsent(var1, var10, var9, var11);
               }
            } finally {
               if (var8 == null) {
                  var7.close();
               } else {
                  var8.close();
               }
            }
         }
      }

      return var1;
   }

   private static void putIfAbsent(Map<String, DnsServerAddresses> var0, String var1, List<InetSocketAddress> var2, boolean var3) {
      DnsServerAddresses var4 = var3 ? DnsServerAddresses.rotational(var2) : DnsServerAddresses.sequential(var2);
      putIfAbsent(var0, var1, var4);
   }

   private static void putIfAbsent(Map<String, DnsServerAddresses> var0, String var1, DnsServerAddresses var2) {
      DnsServerAddresses var3 = var0.put(var1, var2);
      if (var3 != null) {
         var0.put(var1, var3);
         if (logger.isDebugEnabled()) {
            logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", var1, var3, var2);
         }
      }
   }

   static UnixResolverOptions parseEtcResolverOptions() throws IOException {
      return parseEtcResolverOptions(new File("/etc/resolv.conf"));
   }

   static UnixResolverOptions parseEtcResolverOptions(File var0) throws IOException {
      UnixResolverOptions.Builder var1 = UnixResolverOptions.newBuilder();
      FileReader var2 = new FileReader(var0);
      BufferedReader var3 = null;

      try {
         var3 = new BufferedReader(var2);

         String var4;
         while ((var4 = var3.readLine()) != null) {
            if (var4.startsWith("options ")) {
               parseResOptions(var4.substring("options ".length()), var1);
               break;
            }
         }
      } finally {
         if (var3 == null) {
            var2.close();
         } else {
            var3.close();
         }
      }

      if (RES_OPTIONS != null) {
         parseResOptions(RES_OPTIONS, var1);
      }

      return var1.build();
   }

   private static void parseResOptions(String var0, UnixResolverOptions.Builder var1) {
      String[] var2 = WHITESPACE_PATTERN.split(var0);

      for (String var6 : var2) {
         try {
            if (var6.startsWith("ndots:")) {
               var1.setNdots(parseResIntOption(var6, "ndots:"));
            } else if (var6.startsWith("attempts:")) {
               var1.setAttempts(parseResIntOption(var6, "attempts:"));
            } else if (var6.startsWith("timeout:")) {
               var1.setTimeout(parseResIntOption(var6, "timeout:"));
            }
         } catch (NumberFormatException var8) {
         }
      }
   }

   private static int parseResIntOption(String var0, String var1) {
      String var2 = var0.substring(var1.length());
      return Integer.parseInt(var2);
   }

   static List<String> parseEtcResolverSearchDomains() throws IOException {
      return parseEtcResolverSearchDomains(new File("/etc/resolv.conf"));
   }

   static List<String> parseEtcResolverSearchDomains(File var0) throws IOException {
      String var1 = null;
      ArrayList var2 = new ArrayList();
      FileReader var3 = new FileReader(var0);
      BufferedReader var4 = null;

      try {
         var4 = new BufferedReader(var3);

         String var5;
         while ((var5 = var4.readLine()) != null) {
            if (var1 == null && var5.startsWith("domain")) {
               int var11 = StringUtil.indexOfNonWhiteSpace(var5, "domain".length());
               if (var11 >= 0) {
                  var1 = var5.substring(var11);
               }
            } else if (var5.startsWith("search")) {
               int var6 = StringUtil.indexOfNonWhiteSpace(var5, "search".length());
               if (var6 >= 0) {
                  String[] var7 = WHITESPACE_PATTERN.split(var5.substring(var6));
                  Collections.addAll(var2, var7);
               }
            }
         }
      } finally {
         if (var4 == null) {
            var3.close();
         } else {
            var4.close();
         }
      }

      return (List<String>)(var1 != null && var2.isEmpty() ? Collections.singletonList(var1) : var2);
   }
}
