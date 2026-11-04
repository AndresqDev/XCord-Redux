package io.netty.resolver.dns.macos;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.util.internal.ClassInitializerUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class MacOSDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private static final Comparator<DnsResolver> RESOLVER_COMPARATOR = new Comparator<DnsResolver>() {
      public int compare(DnsResolver var1, DnsResolver var2) {
         return var1.searchOrder() < var2.searchOrder() ? 1 : (var1.searchOrder() == var2.searchOrder() ? 0 : -1);
      }
   };
   private static final Throwable UNAVAILABILITY_CAUSE;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacOSDnsServerAddressStreamProvider.class);
   private static final long REFRESH_INTERVAL = TimeUnit.SECONDS.toNanos(10L);
   private volatile Map<String, DnsServerAddresses> currentMappings;
   private final AtomicLong lastRefresh;

   private static void loadNativeLibrary() {
      if (!PlatformDependent.isOsx()) {
         throw new IllegalStateException("Only supported on MacOS/OSX");
      } else {
         String var0 = "netty_resolver_dns_native_macos";
         String var1 = var0 + '_' + PlatformDependent.normalizedArch();
         ClassLoader var2 = PlatformDependent.getClassLoader(MacOSDnsServerAddressStreamProvider.class);

         try {
            NativeLibraryLoader.load(var1, var2);
         } catch (UnsatisfiedLinkError var6) {
            UnsatisfiedLinkError var3 = var6;

            try {
               NativeLibraryLoader.load(var0, var2);
               logger.debug("Failed to load {}", var1, var3);
            } catch (UnsatisfiedLinkError var5) {
               ThrowableUtil.addSuppressed(var6, var5);
               throw var6;
            }
         }
      }
   }

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   public MacOSDnsServerAddressStreamProvider() {
      ensureAvailability();
      this.currentMappings = retrieveCurrentMappings();
      this.lastRefresh = new AtomicLong(System.nanoTime());
   }

   private static Map<String, DnsServerAddresses> retrieveCurrentMappings() {
      DnsResolver[] var0 = resolvers();
      if (var0 != null && var0.length != 0) {
         Arrays.sort(var0, RESOLVER_COMPARATOR);
         HashMap var1 = new HashMap(var0.length);

         for (DnsResolver var5 : var0) {
            if (!"mdns".equalsIgnoreCase(var5.options())) {
               InetSocketAddress[] var6 = var5.nameservers();
               if (var6 != null && var6.length != 0) {
                  String var7 = var5.domain();
                  if (var7 == null) {
                     var7 = "";
                  }

                  InetSocketAddress[] var8 = var5.nameservers();

                  for (int var9 = 0; var9 < var8.length; var9++) {
                     InetSocketAddress var10 = var8[var9];
                     if (var10.getPort() == 0) {
                        int var11 = var5.port();
                        if (var11 == 0) {
                           var11 = 53;
                        }

                        var8[var9] = new InetSocketAddress(var10.getAddress(), var11);
                     }
                  }

                  var1.put(var7, DnsServerAddresses.sequential(var8));
               }
            }
         }

         return var1;
      } else {
         return Collections.emptyMap();
      }
   }

   @Override
   public DnsServerAddressStream nameServerAddressStream(String var1) {
      long var2 = this.lastRefresh.get();
      Map var4 = this.currentMappings;
      if (System.nanoTime() - var2 > REFRESH_INTERVAL && this.lastRefresh.compareAndSet(var2, System.nanoTime())) {
         var4 = this.currentMappings = retrieveCurrentMappings();
      }

      String var5 = var1;

      while (true) {
         int var6 = var1.indexOf(46, 1);
         if (var6 < 0 || var6 == var1.length() - 1) {
            DnsServerAddresses var8 = (DnsServerAddresses)var4.get("");
            return var8 != null ? var8.stream() : DnsServerAddressStreamProviders.unixDefault().nameServerAddressStream(var5);
         }

         DnsServerAddresses var7 = (DnsServerAddresses)var4.get(var1);
         if (var7 != null) {
            return var7.stream();
         }

         var1 = var1.substring(var6 + 1);
      }
   }

   private static native DnsResolver[] resolvers();

   static {
      ClassInitializerUtil.tryLoadClasses(MacOSDnsServerAddressStreamProvider.class, byte[].class, String.class);
      Throwable var0 = null;

      try {
         loadNativeLibrary();
      } catch (Throwable var2) {
         var0 = var2;
      }

      UNAVAILABILITY_CAUSE = var0;
   }
}
