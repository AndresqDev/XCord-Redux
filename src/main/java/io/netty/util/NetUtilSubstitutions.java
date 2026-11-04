package io.netty.util;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.TargetClass;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

@TargetClass(NetUtil.class)
final class NetUtilSubstitutions {
   @Alias
   @InjectAccessors(NetUtilSubstitutions.NetUtilLocalhost4Accessor.class)
   public static Inet4Address LOCALHOST4;
   @Alias
   @InjectAccessors(NetUtilSubstitutions.NetUtilLocalhost6Accessor.class)
   public static Inet6Address LOCALHOST6;
   @Alias
   @InjectAccessors(NetUtilSubstitutions.NetUtilLocalhostAccessor.class)
   public static InetAddress LOCALHOST;

   private NetUtilSubstitutions() {
   }

   private static final class NetUtilLocalhost4Accessor {
      static Inet4Address get() {
         return NetUtilSubstitutions.NetUtilLocalhost4LazyHolder.LOCALHOST4;
      }

      static void set(Inet4Address var0) {
      }
   }

   private static final class NetUtilLocalhost4LazyHolder {
      private static final Inet4Address LOCALHOST4 = NetUtilInitializations.createLocalhost4();
   }

   private static final class NetUtilLocalhost6Accessor {
      static Inet6Address get() {
         return NetUtilSubstitutions.NetUtilLocalhost6LazyHolder.LOCALHOST6;
      }

      static void set(Inet6Address var0) {
      }
   }

   private static final class NetUtilLocalhost6LazyHolder {
      private static final Inet6Address LOCALHOST6 = NetUtilInitializations.createLocalhost6();
   }

   private static final class NetUtilLocalhostAccessor {
      static InetAddress get() {
         return NetUtilSubstitutions.NetUtilLocalhostLazyHolder.LOCALHOST;
      }

      static void set(InetAddress var0) {
      }
   }

   private static final class NetUtilLocalhostLazyHolder {
      private static final InetAddress LOCALHOST = NetUtilInitializations.determineLoopback(
            NetUtilSubstitutions.NetUtilLocalhost4LazyHolder.LOCALHOST4, NetUtilSubstitutions.NetUtilLocalhost6LazyHolder.LOCALHOST6
         )
         .address();
   }
}
