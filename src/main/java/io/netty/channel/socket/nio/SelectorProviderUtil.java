package io.netty.channel.socket.nio;

import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;

final class SelectorProviderUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelectorProviderUtil.class);

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   static Method findOpenMethod(String var0) {
      if (PlatformDependent.javaVersion() >= 15) {
         try {
            return SelectorProvider.class.getMethod(var0, ProtocolFamily.class);
         } catch (Throwable var2) {
            logger.debug("SelectorProvider.{}(ProtocolFamily) not available, will use default", var0, var2);
         }
      }

      return null;
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   static <C extends Channel> C newChannel(Method var0, SelectorProvider var1, InternetProtocolFamily var2) throws IOException {
      if (var2 != null && var0 != null) {
         try {
            return (C)var0.invoke(var1, ProtocolFamilyConverter.convert(var2));
         } catch (InvocationTargetException var4) {
            throw new IOException(var4);
         } catch (IllegalAccessException var5) {
            throw new IOException(var5);
         }
      } else {
         return null;
      }
   }

   private SelectorProviderUtil() {
   }
}
