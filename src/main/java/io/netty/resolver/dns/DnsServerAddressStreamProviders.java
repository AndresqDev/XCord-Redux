package io.netty.resolver.dns;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DnsServerAddressStreamProviders {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DnsServerAddressStreamProviders.class);
   private static final Constructor<? extends DnsServerAddressStreamProvider> STREAM_PROVIDER_CONSTRUCTOR;
   private static final String MACOS_PROVIDER_CLASS_NAME = "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider";

   private DnsServerAddressStreamProviders() {
   }

   public static DnsServerAddressStreamProvider platformDefault() {
      if (STREAM_PROVIDER_CONSTRUCTOR != null) {
         try {
            return STREAM_PROVIDER_CONSTRUCTOR.newInstance();
         } catch (IllegalAccessException var1) {
         } catch (InstantiationException var2) {
         } catch (InvocationTargetException var3) {
         }
      }

      return unixDefault();
   }

   public static DnsServerAddressStreamProvider unixDefault() {
      return DnsServerAddressStreamProviders.DefaultProviderHolder.DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER;
   }

   static {
      Constructor var0 = null;
      if (PlatformDependent.isOsx()) {
         try {
            Object var1 = AccessController.doPrivileged(
               new PrivilegedAction<Object>() {
                  @Override
                  public Object run() {
                     try {
                        return Class.forName(
                           "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider", true, DnsServerAddressStreamProviders.class.getClassLoader()
                        );
                     } catch (Throwable var2) {
                        return var2;
                     }
                  }
               }
            );
            if (!(var1 instanceof Class)) {
               throw (Throwable)var1;
            }

            Class var2 = (Class)var1;
            var0 = var2.getConstructor();
            var0.newInstance();
            LOGGER.debug("{}: available", "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider");
         } catch (ClassNotFoundException var3) {
            LOGGER.warn(
               "Can not find {} in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'",
               "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider"
            );
         } catch (Throwable var4) {
            LOGGER.error(
               "Unable to load {}, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'",
               "io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider",
               var4
            );
            var0 = null;
         }
      }

      STREAM_PROVIDER_CONSTRUCTOR = var0;
   }

   private static final class DefaultProviderHolder {
      private static final long REFRESH_INTERVAL = TimeUnit.MINUTES.toNanos(5L);
      static final DnsServerAddressStreamProvider DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER = new DnsServerAddressStreamProvider() {
         private volatile DnsServerAddressStreamProvider currentProvider = this.provider();
         private final AtomicLong lastRefresh = new AtomicLong(System.nanoTime());

         @Override
         public DnsServerAddressStream nameServerAddressStream(String var1) {
            long var2 = this.lastRefresh.get();
            DnsServerAddressStreamProvider var4 = this.currentProvider;
            if (System.nanoTime() - var2 > DnsServerAddressStreamProviders.DefaultProviderHolder.REFRESH_INTERVAL
               && this.lastRefresh.compareAndSet(var2, System.nanoTime())) {
               var4 = this.currentProvider = this.provider();
            }

            return var4.nameServerAddressStream(var1);
         }

         private DnsServerAddressStreamProvider provider() {
            return (DnsServerAddressStreamProvider)(PlatformDependent.isWindows()
               ? DefaultDnsServerAddressStreamProvider.INSTANCE
               : UnixResolverDnsServerAddressStreamProvider.parseSilently());
         }
      };
   }
}
