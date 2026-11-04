package io.netty.channel;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.WeakHashMap;

final class ChannelHandlerMask {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelHandlerMask.class);
   static final int MASK_EXCEPTION_CAUGHT = 1;
   static final int MASK_CHANNEL_REGISTERED = 2;
   static final int MASK_CHANNEL_UNREGISTERED = 4;
   static final int MASK_CHANNEL_ACTIVE = 8;
   static final int MASK_CHANNEL_INACTIVE = 16;
   static final int MASK_CHANNEL_READ = 32;
   static final int MASK_CHANNEL_READ_COMPLETE = 64;
   static final int MASK_USER_EVENT_TRIGGERED = 128;
   static final int MASK_CHANNEL_WRITABILITY_CHANGED = 256;
   static final int MASK_BIND = 512;
   static final int MASK_CONNECT = 1024;
   static final int MASK_DISCONNECT = 2048;
   static final int MASK_CLOSE = 4096;
   static final int MASK_DEREGISTER = 8192;
   static final int MASK_READ = 16384;
   static final int MASK_WRITE = 32768;
   static final int MASK_FLUSH = 65536;
   static final int MASK_ONLY_INBOUND = 510;
   private static final int MASK_ALL_INBOUND = 511;
   static final int MASK_ONLY_OUTBOUND = 130560;
   private static final int MASK_ALL_OUTBOUND = 130561;
   private static final FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>> MASKS = new FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>>() {
      protected Map<Class<? extends ChannelHandler>, Integer> initialValue() {
         return new WeakHashMap<>(32);
      }
   };

   static int mask(Class<? extends ChannelHandler> var0) {
      Map var1 = MASKS.get();
      Integer var2 = (Integer)var1.get(var0);
      if (var2 == null) {
         var2 = mask0(var0);
         var1.put(var0, var2);
      }

      return var2;
   }

   private static int mask0(Class<? extends ChannelHandler> var0) {
      int var1 = 1;

      try {
         if (ChannelInboundHandler.class.isAssignableFrom(var0)) {
            var1 |= 511;
            if (isSkippable(var0, "channelRegistered", ChannelHandlerContext.class)) {
               var1 &= -3;
            }

            if (isSkippable(var0, "channelUnregistered", ChannelHandlerContext.class)) {
               var1 &= -5;
            }

            if (isSkippable(var0, "channelActive", ChannelHandlerContext.class)) {
               var1 &= -9;
            }

            if (isSkippable(var0, "channelInactive", ChannelHandlerContext.class)) {
               var1 &= -17;
            }

            if (isSkippable(var0, "channelRead", ChannelHandlerContext.class, Object.class)) {
               var1 &= -33;
            }

            if (isSkippable(var0, "channelReadComplete", ChannelHandlerContext.class)) {
               var1 &= -65;
            }

            if (isSkippable(var0, "channelWritabilityChanged", ChannelHandlerContext.class)) {
               var1 &= -257;
            }

            if (isSkippable(var0, "userEventTriggered", ChannelHandlerContext.class, Object.class)) {
               var1 &= -129;
            }
         }

         if (ChannelOutboundHandler.class.isAssignableFrom(var0)) {
            var1 |= 130561;
            if (isSkippable(var0, "bind", ChannelHandlerContext.class, SocketAddress.class, ChannelPromise.class)) {
               var1 &= -513;
            }

            if (isSkippable(var0, "connect", ChannelHandlerContext.class, SocketAddress.class, SocketAddress.class, ChannelPromise.class)) {
               var1 &= -1025;
            }

            if (isSkippable(var0, "disconnect", ChannelHandlerContext.class, ChannelPromise.class)) {
               var1 &= -2049;
            }

            if (isSkippable(var0, "close", ChannelHandlerContext.class, ChannelPromise.class)) {
               var1 &= -4097;
            }

            if (isSkippable(var0, "deregister", ChannelHandlerContext.class, ChannelPromise.class)) {
               var1 &= -8193;
            }

            if (isSkippable(var0, "read", ChannelHandlerContext.class)) {
               var1 &= -16385;
            }

            if (isSkippable(var0, "write", ChannelHandlerContext.class, Object.class, ChannelPromise.class)) {
               var1 &= -32769;
            }

            if (isSkippable(var0, "flush", ChannelHandlerContext.class)) {
               var1 &= -65537;
            }
         }

         if (isSkippable(var0, "exceptionCaught", ChannelHandlerContext.class, Throwable.class)) {
            var1 &= -2;
         }
      } catch (Exception var3) {
         PlatformDependent.throwException(var3);
      }

      return var1;
   }

   private static boolean isSkippable(final Class<?> var0, final String var1, final Class<?>... var2) throws Exception {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
         public Boolean run() throws Exception {
            Method var1x;
            try {
               var1x = var0.getMethod(var1, var2);
            } catch (NoSuchMethodException var3) {
               if (ChannelHandlerMask.logger.isDebugEnabled()) {
                  ChannelHandlerMask.logger.debug("Class {} missing method {}, assume we can not skip execution", var0, var1, var3);
               }

               return false;
            }

            return var1x.isAnnotationPresent(ChannelHandlerMask.Skip.class);
         }
      });
   }

   private ChannelHandlerMask() {
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   @interface Skip {
   }
}
