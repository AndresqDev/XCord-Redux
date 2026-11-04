package org.apache.logging.log4j.core.impl;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

class ThrowableProxyHelper {
   private ThrowableProxyHelper() {
   }

   static ExtendedStackTraceElement[] toExtendedStackTrace(
      ThrowableProxy var0, Stack<Class<?>> var1, Map<String, ThrowableProxyHelper.CacheEntry> var2, StackTraceElement[] var3, StackTraceElement[] var4
   ) {
      int var5;
      if (var3 != null) {
         int var6 = var3.length - 1;

         int var7;
         for (var7 = var4.length - 1; var6 >= 0 && var7 >= 0 && var3[var6].equals(var4[var7]); var7--) {
            var6--;
         }

         var0.setCommonElementCount(var4.length - 1 - var7);
         var5 = var7 + 1;
      } else {
         var0.setCommonElementCount(0);
         var5 = var4.length;
      }

      ExtendedStackTraceElement[] var15 = new ExtendedStackTraceElement[var5];
      Class var16 = var1.isEmpty() ? null : (Class)var1.peek();
      ClassLoader var8 = null;

      for (int var9 = var5 - 1; var9 >= 0; var9--) {
         StackTraceElement var10 = var4[var9];
         String var11 = var10.getClassName();
         ExtendedClassInfo var12;
         if (var16 != null && var11.equals(var16.getName())) {
            ThrowableProxyHelper.CacheEntry var17 = toCacheEntry(var16, true);
            var12 = var17.element;
            var8 = var17.loader;
            var1.pop();
            var16 = var1.isEmpty() ? null : (Class)var1.peek();
         } else {
            ThrowableProxyHelper.CacheEntry var13 = (ThrowableProxyHelper.CacheEntry)var2.get(var11);
            if (var13 != null) {
               var12 = var13.element;
               if (var13.loader != null) {
                  var8 = var13.loader;
               }
            } else {
               ThrowableProxyHelper.CacheEntry var14 = toCacheEntry(loadClass(var8, var11), false);
               var12 = var14.element;
               var2.put(var11, var14);
               if (var14.loader != null) {
                  var8 = var14.loader;
               }
            }
         }

         var15[var9] = new ExtendedStackTraceElement(var10, var12);
      }

      return var15;
   }

   static ThrowableProxy[] toSuppressedProxies(Throwable var0, Set<Throwable> var1) {
      try {
         Throwable[] var2 = var0.getSuppressed();
         if (var2 != null && var2.length != 0) {
            ArrayList var3 = new ArrayList(var2.length);
            if (var1 == null) {
               var1 = new HashSet(var2.length);
            }

            for (int var4 = 0; var4 < var2.length; var4++) {
               Throwable var5 = var2[var4];
               if (var1.add(var5)) {
                  var3.add(new ThrowableProxy(var5, (Set<Throwable>)var1));
               }
            }

            return var3.toArray(new ThrowableProxy[var3.size()]);
         } else {
            return ThrowableProxy.EMPTY_ARRAY;
         }
      } catch (Exception var6) {
         StatusLogger.getLogger().error(var6);
         return null;
      }
   }

   private static ThrowableProxyHelper.CacheEntry toCacheEntry(Class<?> var0, boolean var1) {
      String var2 = "?";
      String var3 = "?";
      ClassLoader var4 = null;
      if (var0 != null) {
         try {
            CodeSource var5 = var0.getProtectionDomain().getCodeSource();
            if (var5 != null) {
               URL var6 = var5.getLocation();
               if (var6 != null) {
                  String var7 = var6.toString().replace('\\', '/');
                  int var8 = var7.lastIndexOf("/");
                  if (var8 >= 0 && var8 == var7.length() - 1) {
                     var8 = var7.lastIndexOf("/", var8 - 1);
                  }

                  var2 = var7.substring(var8 + 1);
               }
            }
         } catch (Exception var10) {
         }

         Package var11 = var0.getPackage();
         if (var11 != null) {
            String var12 = var11.getImplementationVersion();
            if (var12 != null) {
               var3 = var12;
            }
         }

         try {
            var4 = var0.getClassLoader();
         } catch (SecurityException var9) {
            var4 = null;
         }
      }

      return new ThrowableProxyHelper.CacheEntry(new ExtendedClassInfo(var1, var2, var3), var4);
   }

   private static Class<?> loadClass(ClassLoader var0, String var1) {
      if (var0 != null) {
         try {
            Class var2 = var0.loadClass(var1);
            if (var2 != null) {
               return var2;
            }
         } catch (Throwable var6) {
         }
      }

      try {
         return LoaderUtil.loadClass(var1);
      } catch (NoClassDefFoundError | ClassNotFoundException var4) {
         return loadClass(var1);
      } catch (SecurityException var5) {
         return null;
      }
   }

   private static Class<?> loadClass(String var0) {
      try {
         return Loader.loadClass(var0, ThrowableProxyHelper.class.getClassLoader());
      } catch (NoClassDefFoundError | SecurityException | ClassNotFoundException var2) {
         return null;
      }
   }

   static final class CacheEntry {
      private final ExtendedClassInfo element;
      private final ClassLoader loader;

      private CacheEntry(ExtendedClassInfo var1, ClassLoader var2) {
         this.element = var1;
         this.loader = var2;
      }
   }
}
