package io.netty.resolver;

import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class HostsFileEntriesProvider {
   static final HostsFileEntriesProvider EMPTY = new HostsFileEntriesProvider(Collections.emptyMap(), Collections.emptyMap());
   private final Map<String, List<InetAddress>> ipv4Entries;
   private final Map<String, List<InetAddress>> ipv6Entries;

   public static HostsFileEntriesProvider.Parser parser() {
      return HostsFileEntriesProvider.ParserImpl.INSTANCE;
   }

   HostsFileEntriesProvider(Map<String, List<InetAddress>> var1, Map<String, List<InetAddress>> var2) {
      this.ipv4Entries = Collections.unmodifiableMap(new HashMap<>(var1));
      this.ipv6Entries = Collections.unmodifiableMap(new HashMap<>(var2));
   }

   public Map<String, List<InetAddress>> ipv4Entries() {
      return this.ipv4Entries;
   }

   public Map<String, List<InetAddress>> ipv6Entries() {
      return this.ipv6Entries;
   }

   public interface Parser {
      HostsFileEntriesProvider parse() throws IOException;

      HostsFileEntriesProvider parse(Charset... var1) throws IOException;

      HostsFileEntriesProvider parse(File var1, Charset... var2) throws IOException;

      HostsFileEntriesProvider parse(Reader var1) throws IOException;

      HostsFileEntriesProvider parseSilently();

      HostsFileEntriesProvider parseSilently(Charset... var1);

      HostsFileEntriesProvider parseSilently(File var1, Charset... var2);
   }

   private static final class ParserImpl implements HostsFileEntriesProvider.Parser {
      private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
      private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
      private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
      private static final Pattern WHITESPACES = Pattern.compile("[ \t]+");
      private static final InternalLogger logger = InternalLoggerFactory.getInstance(HostsFileEntriesProvider.Parser.class);
      static final HostsFileEntriesProvider.ParserImpl INSTANCE = new HostsFileEntriesProvider.ParserImpl();

      @Override
      public HostsFileEntriesProvider parse() throws IOException {
         return this.parse(locateHostsFile(), Charset.defaultCharset());
      }

      @Override
      public HostsFileEntriesProvider parse(Charset... var1) throws IOException {
         return this.parse(locateHostsFile(), var1);
      }

      @Override
      public HostsFileEntriesProvider parse(File var1, Charset... var2) throws IOException {
         ObjectUtil.checkNotNull(var1, "file");
         ObjectUtil.checkNotNull(var2, "charsets");
         if (var2.length == 0) {
            var2 = new Charset[]{Charset.defaultCharset()};
         }

         if (var1.exists() && var1.isFile()) {
            for (Charset var6 : var2) {
               BufferedReader var7 = new BufferedReader(new InputStreamReader(new FileInputStream(var1), var6));

               try {
                  HostsFileEntriesProvider var8 = this.parse(var7);
                  if (var8 != HostsFileEntriesProvider.EMPTY) {
                     return var8;
                  }
               } finally {
                  var7.close();
               }
            }
         }

         return HostsFileEntriesProvider.EMPTY;
      }

      @Override
      public HostsFileEntriesProvider parse(Reader var1) throws IOException {
         ObjectUtil.checkNotNull(var1, "reader");
         BufferedReader var2 = new BufferedReader(var1);

         HostsFileEntriesProvider var22;
         try {
            HashMap var3 = new HashMap();
            HashMap var4 = new HashMap();

            String var5;
            while ((var5 = var2.readLine()) != null) {
               int var6 = var5.indexOf(35);
               if (var6 != -1) {
                  var5 = var5.substring(0, var6);
               }

               var5 = var5.trim();
               if (!var5.isEmpty()) {
                  ArrayList var7 = new ArrayList();

                  for (String var11 : WHITESPACES.split(var5)) {
                     if (!var11.isEmpty()) {
                        var7.add(var11);
                     }
                  }

                  if (var7.size() >= 2) {
                     byte[] var23 = NetUtil.createByteArrayFromIpAddressString((String)var7.get(0));
                     if (var23 != null) {
                        for (int var24 = 1; var24 < var7.size(); var24++) {
                           String var25 = (String)var7.get(var24);
                           String var26 = var25.toLowerCase(Locale.ENGLISH);
                           InetAddress var12 = InetAddress.getByAddress(var25, var23);
                           Object var13;
                           if (var12 instanceof Inet4Address) {
                              var13 = (List)var3.get(var26);
                              if (var13 == null) {
                                 var13 = new ArrayList();
                                 var3.put(var26, var13);
                              }
                           } else {
                              var13 = (List)var4.get(var26);
                              if (var13 == null) {
                                 var13 = new ArrayList();
                                 var4.put(var26, var13);
                              }
                           }

                           var13.add(var12);
                        }
                     }
                  }
               }
            }

            var22 = var3.isEmpty() && var4.isEmpty() ? HostsFileEntriesProvider.EMPTY : new HostsFileEntriesProvider(var3, var4);
         } finally {
            try {
               var2.close();
            } catch (IOException var19) {
               logger.warn("Failed to close a reader", (Throwable)var19);
            }
         }

         return var22;
      }

      @Override
      public HostsFileEntriesProvider parseSilently() {
         return this.parseSilently(locateHostsFile(), Charset.defaultCharset());
      }

      @Override
      public HostsFileEntriesProvider parseSilently(Charset... var1) {
         return this.parseSilently(locateHostsFile(), var1);
      }

      @Override
      public HostsFileEntriesProvider parseSilently(File var1, Charset... var2) {
         try {
            return this.parse(var1, var2);
         } catch (IOException var4) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to load and parse hosts file at " + var1.getPath(), (Throwable)var4);
            }

            return HostsFileEntriesProvider.EMPTY;
         }
      }

      private static File locateHostsFile() {
         File var0;
         if (PlatformDependent.isWindows()) {
            var0 = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\etc\\hosts");
            if (!var0.exists()) {
               var0 = new File("C:\\Windows\\system32\\drivers\\etc\\hosts");
            }
         } else {
            var0 = new File("/etc/hosts");
         }

         return var0;
      }
   }
}
