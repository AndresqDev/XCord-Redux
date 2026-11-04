package org.apache.logging.log4j.core.util;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class NetUtils {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String UNKNOWN_LOCALHOST = "UNKNOWN_LOCALHOST";

   private NetUtils() {
   }

   public static String getLocalHostname() {
      try {
         InetAddress var0 = InetAddress.getLocalHost();
         return var0 == null ? "UNKNOWN_LOCALHOST" : var0.getHostName();
      } catch (UnknownHostException var7) {
         try {
            Enumeration var1 = NetworkInterface.getNetworkInterfaces();
            if (var1 != null) {
               while (var1.hasMoreElements()) {
                  NetworkInterface var2 = (NetworkInterface)var1.nextElement();
                  Enumeration var3 = var2.getInetAddresses();

                  while (var3.hasMoreElements()) {
                     InetAddress var4 = (InetAddress)var3.nextElement();
                     if (!var4.isLoopbackAddress()) {
                        String var5 = var4.getHostName();
                        if (var5 != null) {
                           return var5;
                        }
                     }
                  }
               }
            }
         } catch (SocketException var6) {
         }

         LOGGER.error("Could not determine local host name", (Throwable)var7);
         return "UNKNOWN_LOCALHOST";
      }
   }

   public static byte[] getMacAddress() {
      byte[] var0 = null;

      try {
         InetAddress var1 = InetAddress.getLocalHost();

         try {
            NetworkInterface var2 = NetworkInterface.getByInetAddress(var1);
            if (isUpAndNotLoopback(var2)) {
               var0 = var2.getHardwareAddress();
            }

            if (var0 == null) {
               Enumeration var3 = NetworkInterface.getNetworkInterfaces();
               if (var3 != null) {
                  while (var3.hasMoreElements() && var0 == null) {
                     NetworkInterface var4 = (NetworkInterface)var3.nextElement();
                     if (isUpAndNotLoopback(var4)) {
                        var0 = var4.getHardwareAddress();
                     }
                  }
               }
            }
         } catch (SocketException var5) {
            LOGGER.catching(var5);
         }

         if (ArrayUtils.isEmpty(var0) && var1 != null) {
            byte[] var7 = var1.getAddress();
            var0 = Arrays.copyOf(var7, 6);
         }
      } catch (UnknownHostException var6) {
      }

      return var0;
   }

   public static String getMacAddressString() {
      byte[] var0 = getMacAddress();
      if (ArrayUtils.isEmpty(var0)) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder(String.format("%02x", var0[0]));

         for (int var2 = 1; var2 < var0.length; var2++) {
            var1.append(":").append(String.format("%02x", var0[var2]));
         }

         return var1.toString();
      }
   }

   private static boolean isUpAndNotLoopback(NetworkInterface var0) throws SocketException {
      return var0 != null && !var0.isLoopback() && var0.isUp();
   }

   public static URI toURI(String var0) {
      try {
         return new URI(var0);
      } catch (URISyntaxException var4) {
         try {
            URL var2 = new URL(var0);
            return new URI(var2.getProtocol(), var2.getHost(), var2.getPath(), null);
         } catch (URISyntaxException | MalformedURLException var3) {
            return new File(var0).toURI();
         }
      }
   }
}
