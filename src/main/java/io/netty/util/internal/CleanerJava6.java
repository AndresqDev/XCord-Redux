package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class CleanerJava6 implements Cleaner {
   private static final long CLEANER_FIELD_OFFSET;
   private static final Method CLEAN_METHOD;
   private static final Field CLEANER_FIELD;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava6.class);

   static boolean isSupported() {
      return CLEANER_FIELD_OFFSET != -1L || CLEANER_FIELD != null;
   }

   @Override
   public void freeDirectBuffer(ByteBuffer var1) {
      if (var1.isDirect()) {
         if (System.getSecurityManager() == null) {
            try {
               freeDirectBuffer0(var1);
            } catch (Throwable var3) {
               PlatformDependent0.throwException(var3);
            }
         } else {
            freeDirectBufferPrivileged(var1);
         }
      }
   }

   private static void freeDirectBufferPrivileged(final ByteBuffer var0) {
      Throwable var1 = AccessController.doPrivileged(new PrivilegedAction<Throwable>() {
         public Throwable run() {
            try {
               CleanerJava6.freeDirectBuffer0(var0);
               return null;
            } catch (Throwable var2) {
               return var2;
            }
         }
      });
      if (var1 != null) {
         PlatformDependent0.throwException(var1);
      }
   }

   private static void freeDirectBuffer0(ByteBuffer var0) throws Exception {
      Object var1;
      if (CLEANER_FIELD_OFFSET == -1L) {
         var1 = CLEANER_FIELD.get(var0);
      } else {
         var1 = PlatformDependent0.getObject(var0, CLEANER_FIELD_OFFSET);
      }

      if (var1 != null) {
         CLEAN_METHOD.invoke(var1);
      }
   }

   static {
      Throwable var4 = null;
      final ByteBuffer var5 = ByteBuffer.allocateDirect(1);

      long var0;
      Method var2;
      Field var3;
      try {
         Object var6 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Field var1 = var5.getClass().getDeclaredField("cleaner");
                  if (!PlatformDependent.hasUnsafe()) {
                     var1.setAccessible(true);
                  }

                  return var1;
               } catch (Throwable var2) {
                  return var2;
               }
            }
         });
         if (var6 instanceof Throwable) {
            throw (Throwable)var6;
         }

         var3 = (Field)var6;
         Object var7;
         if (PlatformDependent.hasUnsafe()) {
            var0 = PlatformDependent0.objectFieldOffset(var3);
            var7 = PlatformDependent0.getObject(var5, var0);
         } else {
            var0 = -1L;
            var7 = var3.get(var5);
         }

         var2 = var7.getClass().getDeclaredMethod("clean");
         var2.invoke(var7);
      } catch (Throwable var8) {
         var0 = -1L;
         var2 = null;
         var4 = var8;
         var3 = null;
      }

      if (var4 == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", var4);
      }

      CLEANER_FIELD = var3;
      CLEANER_FIELD_OFFSET = var0;
      CLEAN_METHOD = var2;
   }
}
