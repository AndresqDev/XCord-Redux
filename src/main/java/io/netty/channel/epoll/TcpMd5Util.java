package io.netty.channel.epoll;

import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

final class TcpMd5Util {
   static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel var0, Collection<InetAddress> var1, Map<InetAddress, byte[]> var2) throws IOException {
      ObjectUtil.checkNotNull(var0, "channel");
      ObjectUtil.checkNotNull(var1, "current");
      ObjectUtil.checkNotNull(var2, "newKeys");

      for (Entry var4 : var2.entrySet()) {
         byte[] var5 = (byte[])var4.getValue();
         ObjectUtil.checkNotNullWithIAE(var4.getKey(), "e.getKey");
         ObjectUtil.checkNonEmpty(var5, ((InetAddress)var4.getKey()).toString());
         if (var5.length > Native.TCP_MD5SIG_MAXKEYLEN) {
            throw new IllegalArgumentException(
               "newKeys[" + var4.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')'
            );
         }
      }

      for (InetAddress var8 : var1) {
         if (!var2.containsKey(var8)) {
            var0.socket.setTcpMd5Sig(var8, null);
         }
      }

      if (var2.isEmpty()) {
         return Collections.emptySet();
      } else {
         ArrayList var7 = new ArrayList(var2.size());

         for (Entry var10 : var2.entrySet()) {
            var0.socket.setTcpMd5Sig((InetAddress)var10.getKey(), (byte[])var10.getValue());
            var7.add(var10.getKey());
         }

         return var7;
      }
   }

   private TcpMd5Util() {
   }
}
