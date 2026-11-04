package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class InetAddresses {
   private static final int IPV4_PART_COUNT = 4;
   private static final int IPV6_PART_COUNT = 8;
   private static final char IPV4_DELIMITER = '.';
   private static final char IPV6_DELIMITER = ':';
   private static final CharMatcher IPV4_DELIMITER_MATCHER = CharMatcher.is('.');
   private static final CharMatcher IPV6_DELIMITER_MATCHER = CharMatcher.is(':');
   private static final Inet4Address LOOPBACK4 = (Inet4Address)forString("127.0.0.1");
   private static final Inet4Address ANY4 = (Inet4Address)forString("0.0.0.0");

   private InetAddresses() {
   }

   private static Inet4Address getInet4Address(byte[] var0) {
      Preconditions.checkArgument(var0.length == 4, "Byte array has invalid length for an IPv4 address: %s != 4.", var0.length);
      return (Inet4Address)bytesToInetAddress(var0);
   }

   public static InetAddress forString(String var0) {
      byte[] var1 = ipStringToBytes(var0);
      if (var1 == null) {
         throw formatIllegalArgumentException("'%s' is not an IP string literal.", var0);
      } else {
         return bytesToInetAddress(var1);
      }
   }

   public static boolean isInetAddress(String var0) {
      return ipStringToBytes(var0) != null;
   }

   private static byte[] ipStringToBytes(String var0) {
      String var1 = var0;
      boolean var2 = false;
      boolean var3 = false;
      int var4 = -1;

      for (int var5 = 0; var5 < var1.length(); var5++) {
         char var6 = var1.charAt(var5);
         if (var6 == '.') {
            var3 = true;
         } else if (var6 == ':') {
            if (var3) {
               return null;
            }

            var2 = true;
         } else {
            if (var6 == '%') {
               var4 = var5;
               break;
            }

            if (Character.digit(var6, 16) == -1) {
               return null;
            }
         }
      }

      if (var2) {
         if (var3) {
            var1 = convertDottedQuadToHex(var1);
            if (var1 == null) {
               return null;
            }
         }

         if (var4 != -1) {
            var1 = var1.substring(0, var4);
         }

         return textToNumericFormatV6(var1);
      } else if (var3) {
         return var4 != -1 ? null : textToNumericFormatV4(var1);
      } else {
         return null;
      }
   }

   private static byte[] textToNumericFormatV4(String var0) {
      if (IPV4_DELIMITER_MATCHER.countIn(var0) + 1 != 4) {
         return null;
      } else {
         byte[] var1 = new byte[4];
         int var2 = 0;

         for (int var3 = 0; var3 < 4; var3++) {
            int var4 = var0.indexOf(46, var2);
            if (var4 == -1) {
               var4 = var0.length();
            }

            try {
               var1[var3] = parseOctet(var0, var2, var4);
            } catch (NumberFormatException var6) {
               return null;
            }

            var2 = var4 + 1;
         }

         return var1;
      }
   }

   private static byte[] textToNumericFormatV6(String var0) {
      int var1 = IPV6_DELIMITER_MATCHER.countIn(var0);
      if (var1 >= 2 && var1 <= 8) {
         int var2 = 8 - (var1 + 1);
         boolean var3 = false;

         for (int var4 = 0; var4 < var0.length() - 1; var4++) {
            if (var0.charAt(var4) == ':' && var0.charAt(var4 + 1) == ':') {
               if (var3) {
                  return null;
               }

               var3 = true;
               var2++;
               if (var4 == 0) {
                  var2++;
               }

               if (var4 == var0.length() - 2) {
                  var2++;
               }
            }
         }

         if (var0.charAt(0) == ':' && var0.charAt(1) != ':') {
            return null;
         } else if (var0.charAt(var0.length() - 1) == ':' && var0.charAt(var0.length() - 2) != ':') {
            return null;
         } else if (var3 && var2 <= 0) {
            return null;
         } else if (!var3 && var1 + 1 != 8) {
            return null;
         } else {
            ByteBuffer var9 = ByteBuffer.allocate(16);

            try {
               int var5 = 0;
               if (var0.charAt(0) == ':') {
                  var5 = 1;
               }

               while (var5 < var0.length()) {
                  int var6 = var0.indexOf(58, var5);
                  if (var6 == -1) {
                     var6 = var0.length();
                  }

                  if (var0.charAt(var5) == ':') {
                     for (int var7 = 0; var7 < var2; var7++) {
                        var9.putShort((short)0);
                     }
                  } else {
                     var9.putShort(parseHextet(var0, var5, var6));
                  }

                  var5 = var6 + 1;
               }
            } catch (NumberFormatException var8) {
               return null;
            }

            return var9.array();
         }
      } else {
         return null;
      }
   }

   private static String convertDottedQuadToHex(String var0) {
      int var1 = var0.lastIndexOf(58);
      String var2 = var0.substring(0, var1 + 1);
      String var3 = var0.substring(var1 + 1);
      byte[] var4 = textToNumericFormatV4(var3);
      if (var4 == null) {
         return null;
      } else {
         String var5 = Integer.toHexString((var4[0] & 255) << 8 | var4[1] & 255);
         String var6 = Integer.toHexString((var4[2] & 255) << 8 | var4[3] & 255);
         return new StringBuilder(1 + String.valueOf(var2).length() + String.valueOf(var5).length() + String.valueOf(var6).length())
            .append(var2)
            .append(var5)
            .append(":")
            .append(var6)
            .toString();
      }
   }

   private static byte parseOctet(String var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 > 0 && var3 <= 3) {
         if (var3 > 1 && var0.charAt(var1) == '0') {
            throw new NumberFormatException();
         } else {
            int var4 = 0;

            for (int var5 = var1; var5 < var2; var5++) {
               var4 *= 10;
               int var6 = Character.digit(var0.charAt(var5), 10);
               if (var6 < 0) {
                  throw new NumberFormatException();
               }

               var4 += var6;
            }

            if (var4 > 255) {
               throw new NumberFormatException();
            } else {
               return (byte)var4;
            }
         }
      } else {
         throw new NumberFormatException();
      }
   }

   private static short parseHextet(String var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 > 0 && var3 <= 4) {
         int var4 = 0;

         for (int var5 = var1; var5 < var2; var5++) {
            var4 <<= 4;
            var4 |= Character.digit(var0.charAt(var5), 16);
         }

         return (short)var4;
      } else {
         throw new NumberFormatException();
      }
   }

   private static InetAddress bytesToInetAddress(byte[] var0) {
      try {
         return InetAddress.getByAddress(var0);
      } catch (UnknownHostException var2) {
         throw new AssertionError(var2);
      }
   }

   public static String toAddrString(InetAddress var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof Inet4Address) {
         return var0.getHostAddress();
      } else {
         Preconditions.checkArgument(var0 instanceof Inet6Address);
         byte[] var1 = var0.getAddress();
         int[] var2 = new int[8];

         for (int var3 = 0; var3 < var2.length; var3++) {
            var2[var3] = Ints.fromBytes((byte)0, (byte)0, var1[2 * var3], var1[2 * var3 + 1]);
         }

         compressLongestRunOfZeroes(var2);
         return hextetsToIPv6String(var2);
      }
   }

   private static void compressLongestRunOfZeroes(int[] var0) {
      int var1 = -1;
      int var2 = -1;
      int var3 = -1;

      for (int var4 = 0; var4 < var0.length + 1; var4++) {
         if (var4 < var0.length && var0[var4] == 0) {
            if (var3 < 0) {
               var3 = var4;
            }
         } else if (var3 >= 0) {
            int var5 = var4 - var3;
            if (var5 > var2) {
               var1 = var3;
               var2 = var5;
            }

            var3 = -1;
         }
      }

      if (var2 >= 2) {
         Arrays.fill(var0, var1, var1 + var2, -1);
      }
   }

   private static String hextetsToIPv6String(int[] var0) {
      StringBuilder var1 = new StringBuilder(39);
      boolean var2 = false;

      for (int var3 = 0; var3 < var0.length; var3++) {
         boolean var4 = var0[var3] >= 0;
         if (var4) {
            if (var2) {
               var1.append(':');
            }

            var1.append(Integer.toHexString(var0[var3]));
         } else if (var3 == 0 || var2) {
            var1.append("::");
         }

         var2 = var4;
      }

      return var1.toString();
   }

   public static String toUriString(InetAddress var0) {
      if (var0 instanceof Inet6Address) {
         String var1 = toAddrString(var0);
         return new StringBuilder(2 + String.valueOf(var1).length()).append("[").append(var1).append("]").toString();
      } else {
         return toAddrString(var0);
      }
   }

   public static InetAddress forUriString(String var0) {
      InetAddress var1 = forUriStringNoThrow(var0);
      if (var1 == null) {
         throw formatIllegalArgumentException("Not a valid URI IP literal: '%s'", var0);
      } else {
         return var1;
      }
   }

   private static InetAddress forUriStringNoThrow(String var0) {
      Preconditions.checkNotNull(var0);
      String var1;
      byte var2;
      if (var0.startsWith("[") && var0.endsWith("]")) {
         var1 = var0.substring(1, var0.length() - 1);
         var2 = 16;
      } else {
         var1 = var0;
         var2 = 4;
      }

      byte[] var3 = ipStringToBytes(var1);
      return var3 != null && var3.length == var2 ? bytesToInetAddress(var3) : null;
   }

   public static boolean isUriInetAddress(String var0) {
      return forUriStringNoThrow(var0) != null;
   }

   public static boolean isCompatIPv4Address(Inet6Address var0) {
      if (!var0.isIPv4CompatibleAddress()) {
         return false;
      } else {
         byte[] var1 = var0.getAddress();
         return var1[12] != 0 || var1[13] != 0 || var1[14] != 0 || var1[15] != 0 && var1[15] != 1;
      }
   }

   public static Inet4Address getCompatIPv4Address(Inet6Address var0) {
      Preconditions.checkArgument(isCompatIPv4Address(var0), "Address '%s' is not IPv4-compatible.", toAddrString(var0));
      return getInet4Address(Arrays.copyOfRange(var0.getAddress(), 12, 16));
   }

   public static boolean is6to4Address(Inet6Address var0) {
      byte[] var1 = var0.getAddress();
      return var1[0] == 32 && var1[1] == 2;
   }

   public static Inet4Address get6to4IPv4Address(Inet6Address var0) {
      Preconditions.checkArgument(is6to4Address(var0), "Address '%s' is not a 6to4 address.", toAddrString(var0));
      return getInet4Address(Arrays.copyOfRange(var0.getAddress(), 2, 6));
   }

   public static boolean isTeredoAddress(Inet6Address var0) {
      byte[] var1 = var0.getAddress();
      return var1[0] == 32 && var1[1] == 1 && var1[2] == 0 && var1[3] == 0;
   }

   public static InetAddresses.TeredoInfo getTeredoInfo(Inet6Address var0) {
      Preconditions.checkArgument(isTeredoAddress(var0), "Address '%s' is not a Teredo address.", toAddrString(var0));
      byte[] var1 = var0.getAddress();
      Inet4Address var2 = getInet4Address(Arrays.copyOfRange(var1, 4, 8));
      int var3 = ByteStreams.newDataInput(var1, 8).readShort() & '\uffff';
      int var4 = ~ByteStreams.newDataInput(var1, 10).readShort() & 65535;
      byte[] var5 = Arrays.copyOfRange(var1, 12, 16);

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = (byte)(~var5[var6]);
      }

      Inet4Address var7 = getInet4Address(var5);
      return new InetAddresses.TeredoInfo(var2, var7, var4, var3);
   }

   public static boolean isIsatapAddress(Inet6Address var0) {
      if (isTeredoAddress(var0)) {
         return false;
      } else {
         byte[] var1 = var0.getAddress();
         return (var1[8] | 3) != 3 ? false : var1[9] == 0 && var1[10] == 94 && var1[11] == -2;
      }
   }

   public static Inet4Address getIsatapIPv4Address(Inet6Address var0) {
      Preconditions.checkArgument(isIsatapAddress(var0), "Address '%s' is not an ISATAP address.", toAddrString(var0));
      return getInet4Address(Arrays.copyOfRange(var0.getAddress(), 12, 16));
   }

   public static boolean hasEmbeddedIPv4ClientAddress(Inet6Address var0) {
      return isCompatIPv4Address(var0) || is6to4Address(var0) || isTeredoAddress(var0);
   }

   public static Inet4Address getEmbeddedIPv4ClientAddress(Inet6Address var0) {
      if (isCompatIPv4Address(var0)) {
         return getCompatIPv4Address(var0);
      } else if (is6to4Address(var0)) {
         return get6to4IPv4Address(var0);
      } else if (isTeredoAddress(var0)) {
         return getTeredoInfo(var0).getClient();
      } else {
         throw formatIllegalArgumentException("'%s' has no embedded IPv4 address.", toAddrString(var0));
      }
   }

   public static boolean isMappedIPv4Address(String var0) {
      byte[] var1 = ipStringToBytes(var0);
      if (var1 != null && var1.length == 16) {
         for (int var2 = 0; var2 < 10; var2++) {
            if (var1[var2] != 0) {
               return false;
            }
         }

         for (int var3 = 10; var3 < 12; var3++) {
            if (var1[var3] != -1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static Inet4Address getCoercedIPv4Address(InetAddress var0) {
      if (var0 instanceof Inet4Address) {
         return (Inet4Address)var0;
      } else {
         byte[] var1 = var0.getAddress();
         boolean var2 = true;

         for (int var3 = 0; var3 < 15; var3++) {
            if (var1[var3] != 0) {
               var2 = false;
               break;
            }
         }

         if (var2 && var1[15] == 1) {
            return LOOPBACK4;
         } else if (var2 && var1[15] == 0) {
            return ANY4;
         } else {
            Inet6Address var7 = (Inet6Address)var0;
            long var4 = 0L;
            if (hasEmbeddedIPv4ClientAddress(var7)) {
               var4 = (long)getEmbeddedIPv4ClientAddress(var7).hashCode();
            } else {
               var4 = ByteBuffer.wrap(var7.getAddress(), 0, 8).getLong();
            }

            int var6 = Hashing.murmur3_32_fixed().hashLong(var4).asInt();
            var6 |= -536870912;
            if (var6 == -1) {
               var6 = -2;
            }

            return getInet4Address(Ints.toByteArray(var6));
         }
      }
   }

   public static int coerceToInteger(InetAddress var0) {
      return ByteStreams.newDataInput(getCoercedIPv4Address(var0).getAddress()).readInt();
   }

   public static BigInteger toBigInteger(InetAddress var0) {
      return new BigInteger(1, var0.getAddress());
   }

   public static Inet4Address fromInteger(int var0) {
      return getInet4Address(Ints.toByteArray(var0));
   }

   public static Inet4Address fromIPv4BigInteger(BigInteger var0) {
      return (Inet4Address)fromBigInteger(var0, false);
   }

   public static Inet6Address fromIPv6BigInteger(BigInteger var0) {
      return (Inet6Address)fromBigInteger(var0, true);
   }

   private static InetAddress fromBigInteger(BigInteger var0, boolean var1) {
      Preconditions.checkArgument(var0.signum() >= 0, "BigInteger must be greater than or equal to 0");
      int var2 = var1 ? 16 : 4;
      byte[] var3 = var0.toByteArray();
      byte[] var4 = new byte[var2];
      int var5 = Math.max(0, var3.length - var2);
      int var6 = var3.length - var5;
      int var7 = var2 - var6;

      for (int var8 = 0; var8 < var5; var8++) {
         if (var3[var8] != 0) {
            throw formatIllegalArgumentException("BigInteger cannot be converted to InetAddress because it has more than %d bytes: %s", var2, var0);
         }
      }

      System.arraycopy(var3, var5, var4, var7, var6);

      try {
         return InetAddress.getByAddress(var4);
      } catch (UnknownHostException var9) {
         throw new AssertionError(var9);
      }
   }

   public static InetAddress fromLittleEndianByteArray(byte[] var0) throws UnknownHostException {
      byte[] var1 = new byte[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var0.length - var2 - 1];
      }

      return InetAddress.getByAddress(var1);
   }

   public static InetAddress decrement(InetAddress var0) {
      byte[] var1 = var0.getAddress();

      int var2;
      for (var2 = var1.length - 1; var2 >= 0 && var1[var2] == 0; var2--) {
         var1[var2] = -1;
      }

      Preconditions.checkArgument(var2 >= 0, "Decrementing %s would wrap.", var0);
      var1[var2]--;
      return bytesToInetAddress(var1);
   }

   public static InetAddress increment(InetAddress var0) {
      byte[] var1 = var0.getAddress();

      int var2;
      for (var2 = var1.length - 1; var2 >= 0 && var1[var2] == -1; var2--) {
         var1[var2] = 0;
      }

      Preconditions.checkArgument(var2 >= 0, "Incrementing %s would wrap.", var0);
      var1[var2]++;
      return bytesToInetAddress(var1);
   }

   public static boolean isMaximum(InetAddress var0) {
      byte[] var1 = var0.getAddress();

      for (byte var5 : var1) {
         if (var5 != -1) {
            return false;
         }
      }

      return true;
   }

   private static IllegalArgumentException formatIllegalArgumentException(String var0, Object... var1) {
      return new IllegalArgumentException(String.format(Locale.ROOT, var0, var1));
   }

   @Beta
   public static final class TeredoInfo {
      private final Inet4Address server;
      private final Inet4Address client;
      private final int port;
      private final int flags;

      public TeredoInfo(Inet4Address var1, Inet4Address var2, int var3, int var4) {
         Preconditions.checkArgument(var3 >= 0 && var3 <= 65535, "port '%s' is out of range (0 <= port <= 0xffff)", var3);
         Preconditions.checkArgument(var4 >= 0 && var4 <= 65535, "flags '%s' is out of range (0 <= flags <= 0xffff)", var4);
         this.server = MoreObjects.firstNonNull(var1, InetAddresses.ANY4);
         this.client = MoreObjects.firstNonNull(var2, InetAddresses.ANY4);
         this.port = var3;
         this.flags = var4;
      }

      public Inet4Address getServer() {
         return this.server;
      }

      public Inet4Address getClient() {
         return this.client;
      }

      public int getPort() {
         return this.port;
      }

      public int getFlags() {
         return this.flags;
      }
   }
}
