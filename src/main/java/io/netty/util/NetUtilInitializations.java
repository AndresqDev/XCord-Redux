package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

final class NetUtilInitializations {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtilInitializations.class);

   private NetUtilInitializations() {
   }

   static Inet4Address createLocalhost4() {
      byte[] var0 = new byte[]{127, 0, 0, 1};
      Inet4Address var1 = null;

      try {
         var1 = (Inet4Address)InetAddress.getByAddress("localhost", var0);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return var1;
   }

   static Inet6Address createLocalhost6() {
      byte[] var0 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
      Inet6Address var1 = null;

      try {
         var1 = (Inet6Address)InetAddress.getByAddress("localhost", var0);
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return var1;
   }

   static NetUtilInitializations.NetworkIfaceAndInetAddress determineLoopback(Inet4Address var0, Inet6Address var1) {
      ArrayList var2 = new ArrayList();

      try {
         Enumeration var3 = NetworkInterface.getNetworkInterfaces();
         if (var3 != null) {
            while (var3.hasMoreElements()) {
               NetworkInterface var4 = (NetworkInterface)var3.nextElement();
               if (SocketUtils.addressesFromNetworkInterface(var4).hasMoreElements()) {
                  var2.add(var4);
               }
            }
         }
      } catch (SocketException var17) {
         logger.warn("Failed to retrieve the list of available network interfaces", (Throwable)var17);
      }

      NetworkInterface var18 = null;
      Object var19 = null;

      label141:
      for (NetworkInterface var6 : var2) {
         Enumeration var7 = SocketUtils.addressesFromNetworkInterface(var6);

         while (var7.hasMoreElements()) {
            InetAddress var8 = (InetAddress)var7.nextElement();
            if (var8.isLoopbackAddress()) {
               var18 = var6;
               var19 = var8;
               break label141;
            }
         }
      }

      if (var18 == null) {
         try {
            for (NetworkInterface var21 : var2) {
               if (var21.isLoopback()) {
                  Enumeration var22 = SocketUtils.addressesFromNetworkInterface(var21);
                  if (var22.hasMoreElements()) {
                     var18 = var21;
                     var19 = (InetAddress)var22.nextElement();
                     break;
                  }
               }
            }

            if (var18 == null) {
               logger.warn("Failed to find the loopback interface");
            }
         } catch (SocketException var16) {
            logger.warn("Failed to find the loopback interface", (Throwable)var16);
         }
      }

      if (var18 != null) {
         logger.debug("Loopback interface: {} ({}, {})", var18.getName(), var18.getDisplayName(), var19.getHostAddress());
      } else if (var19 == null) {
         try {
            if (NetworkInterface.getByInetAddress(var1) != null) {
               logger.debug("Using hard-coded IPv6 localhost address: {}", var1);
               var19 = var1;
            }
         } catch (Exception var14) {
         } finally {
            if (var19 == null) {
               logger.debug("Using hard-coded IPv4 localhost address: {}", var0);
            }
         }
      }

      return new NetUtilInitializations.NetworkIfaceAndInetAddress(var18, (InetAddress)var19);
   }

   static final class NetworkIfaceAndInetAddress {
      private final NetworkInterface iface;
      private final InetAddress address;

      NetworkIfaceAndInetAddress(NetworkInterface var1, InetAddress var2) {
         this.iface = var1;
         this.address = var2;
      }

      public NetworkInterface iface() {
         return this.iface;
      }

      public InetAddress address() {
         return this.address;
      }
   }
}
