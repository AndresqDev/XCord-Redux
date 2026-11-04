package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedLongs;
import io.github.waterfallmc.waterfall.utils.Hex;
import io.netty.channel.unix.DomainSocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;

public class Util {
   public static final int DEFAULT_PORT = 25565;

   public static String unicode(char var0) {
      return "\\u" + String.format("%04x", Integer.valueOf(var0)).toUpperCase(Locale.ROOT);
   }

   public static SocketAddress getAddr(String var0) {
      return getAddr(var0, false);
   }

   public static SocketAddress getAddr(String var0, boolean var1) {
      URI var2 = null;

      try {
         var2 = new URI(var0);
      } catch (URISyntaxException var5) {
      }

      if (var2 != null && "unix".equals(var2.getScheme())) {
         return new DomainSocketAddress(var2.getPath());
      } else {
         if (var2 == null || var2.getHost() == null) {
            try {
               var2 = new URI("tcp://" + var0);
            } catch (URISyntaxException var4) {
               throw new IllegalArgumentException("Bad hostline: " + var0, var4);
            }
         }

         if (var2.getHost() == null) {
            throw new IllegalArgumentException("Invalid host/address: " + var0);
         } else {
            return var1
               ? InetSocketAddress.createUnresolved(var2.getHost(), var2.getPort() == -1 ? 25565 : var2.getPort())
               : new InetSocketAddress(var2.getHost(), var2.getPort() == -1 ? 25565 : var2.getPort());
         }
      }
   }

   public static String hex(int var0) {
      return Hex.encodeString(Ints.toByteArray(var0));
   }

   public static String exception(Throwable var0) {
      StackTraceElement[] var1 = var0.getStackTrace();
      return var0.getClass().getSimpleName()
         + " : "
         + var0.getMessage()
         + (var1.length > 0 ? " @ " + var0.getStackTrace()[0].getClassName() + ":" + var0.getStackTrace()[0].getLineNumber() : "");
   }

   public static String csv(Iterable<?> var0) {
      return format(var0, ", ");
   }

   public static String format(Iterable<?> var0, String var1) {
      return Joiner.on(var1).join(var0);
   }

   public static UUID getUUID(String var0) {
      return new UUID(UnsignedLongs.parseUnsignedLong(var0.substring(0, 16), 16), UnsignedLongs.parseUnsignedLong(var0.substring(16), 16));
   }
}
