package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class CleanerJava9 implements Cleaner {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
   private static final Method INVOKE_CLEANER;

   static boolean isSupported() {
      return INVOKE_CLEANER != null;
   }

   @Override
   public void freeDirectBuffer(ByteBuffer var1) {
      if (System.getSecurityManager() == null) {
         try {
            INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, var1);
         } catch (Throwable var3) {
            PlatformDependent0.throwException(var3);
         }
      } else {
         freeDirectBufferPrivileged(var1);
      }
   }

   private static void freeDirectBufferPrivileged(final ByteBuffer var0) {
      Exception var1 = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
         public Exception run() {
            try {
               CleanerJava9.INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, var0);
               return null;
            } catch (InvocationTargetException var2) {
               return var2;
            } catch (IllegalAccessException var3) {
               return var3;
            }
         }
      });
      if (var1 != null) {
         PlatformDependent0.throwException(var1);
      }
   }

   static {
      Method var0;
      Object var1;
      if (PlatformDependent0.hasUnsafe()) {
         final ByteBuffer var2 = ByteBuffer.allocateDirect(1);
         Object var3 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Method var1 = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
                  var1.invoke(PlatformDependent0.UNSAFE, var2);
                  return var1;
               } catch (NoSuchMethodException var2x) {
                  return var2x;
               } catch (InvocationTargetException var3) {
                  return var3;
               } catch (IllegalAccessException var4) {
                  return var4;
               }
            }
         });
         if (var3 instanceof Throwable) {
            var0 = null;
            var1 = (Throwable)var3;
         } else {
            var0 = (Method)var3;
            var1 = null;
         }
      } else {
         var0 = null;
         var1 = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
      }

      if (var1 == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", (Throwable)var1);
      }

      INVOKE_CLEANER = var0;
   }
}
