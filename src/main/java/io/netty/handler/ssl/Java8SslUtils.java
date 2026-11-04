package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class Java8SslUtils {
   private Java8SslUtils() {
   }

   static List<String> getSniHostNames(SSLParameters var0) {
      List var1 = var0.getServerNames();
      if (var1 != null && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList(var1.size());

         for (SNIServerName var4 : var1) {
            if (!(var4 instanceof SNIHostName)) {
               throw new IllegalArgumentException("Only " + SNIHostName.class.getName() + " instances are supported, but found: " + var4);
            }

            var2.add(((SNIHostName)var4).getAsciiName());
         }

         return var2;
      } else {
         return Collections.emptyList();
      }
   }

   static void setSniHostNames(SSLParameters var0, List<String> var1) {
      var0.setServerNames(getSniHostNames(var1));
   }

   static boolean isValidHostNameForSNI(String var0) {
      try {
         new SNIHostName(var0);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   static List getSniHostNames(List<String> var0) {
      if (var0 != null && !var0.isEmpty()) {
         ArrayList var1 = new ArrayList(var0.size());

         for (String var3 : var0) {
            var1.add(new SNIHostName(var3.getBytes(CharsetUtil.UTF_8)));
         }

         return var1;
      } else {
         return Collections.emptyList();
      }
   }

   static List getSniHostName(byte[] var0) {
      return var0 != null && var0.length != 0 ? Collections.singletonList(new SNIHostName(var0)) : Collections.emptyList();
   }

   static boolean getUseCipherSuitesOrder(SSLParameters var0) {
      return var0.getUseCipherSuitesOrder();
   }

   static void setUseCipherSuitesOrder(SSLParameters var0, boolean var1) {
      var0.setUseCipherSuitesOrder(var1);
   }

   static void setSNIMatchers(SSLParameters var0, Collection<?> var1) {
      var0.setSNIMatchers(var1);
   }

   static boolean checkSniHostnameMatch(Collection<?> var0, byte[] var1) {
      if (var0 != null && !var0.isEmpty()) {
         SNIHostName var2 = new SNIHostName(var1);

         for (SNIMatcher var4 : var0) {
            if (var4.getType() == 0 && var4.matches(var2)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
