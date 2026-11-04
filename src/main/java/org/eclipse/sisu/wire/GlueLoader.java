package org.eclipse.sisu.wire;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.sisu.inject.Weak;
import xcord.Provider;

final class GlueLoader extends ClassLoader {
   private static final Object SYSTEM_LOADER_LOCK = new Object();
   private static final String PROVIDER_NAME = Provider.class.getName();
   private static final String GLUE_SUFFIX = "$__sisu__$";
   private static final String DYNAMIC = "dyn";
   private static final ConcurrentMap<Integer, GlueLoader> cachedGlue = Weak.concurrentValues();

   GlueLoader() {
   }

   GlueLoader(ClassLoader var1) {
      super(var1);
   }

   public static <T> T dynamicGlue(TypeLiteral<T> var0, Provider<T> var1) {
      try {
         return (T)dynamicGlue(var0.getRawType()).getConstructor(Provider.class).newInstance(var1);
      } catch (Exception var4) {
         Object var3 = var4 instanceof InvocationTargetException ? var4.getCause() : var4;
         throw new ProvisionException("Error proxying: " + var0, (Throwable)var3);
      } catch (LinkageError var5) {
         throw new ProvisionException("Error proxying: " + var0, var5);
      }
   }

   @Override
   protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      return PROVIDER_NAME.equals(var1) ? Provider.class : super.loadClass(var1, var2);
   }

   @Override
   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      if (var1.endsWith("$__sisu__$dyn")) {
         Class var2 = this.loadClass(unwrap(var1));
         byte[] var3 = DynamicGlue.generateProxyClass(var1.replace('.', '/'), var2);
         return this.defineClass(var1, var3, 0, var3.length);
      } else {
         throw new ClassNotFoundException(var1);
      }
   }

   private static Class<?> dynamicGlue(Class<?> var0) throws ClassNotFoundException {
      return glue(var0.getClassLoader()).loadClass(wrap(var0.getName(), "dyn"));
   }

   private static String wrap(String var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      if (var0.startsWith("java.") || var0.startsWith("java/")) {
         var2.append('$');
      }

      return var2.append(var0).append("$__sisu__$").append(var1).toString();
   }

   private static String unwrap(String var0) {
      int var1 = '$' == var0.charAt(0) ? 1 : 0;
      int var2 = var0.lastIndexOf("$__sisu__$");
      return var2 > 0 ? var0.substring(var1, var2) : var0;
   }

   private static GlueLoader glue(ClassLoader var0) {
      int var1 = System.identityHashCode(var0);
      GlueLoader var2 = cachedGlue.get(Integer.valueOf(var1));
      if (var2 == null || var2.getParent() != var0) {
         synchronized (var0 != null ? var0 : SYSTEM_LOADER_LOCK) {
            GlueLoader var4 = createGlue(var0);

            do {
               var2 = cachedGlue.putIfAbsent(var1++, var4);
               if (var2 == null) {
                  return var4;
               }
            } while (var2.getParent() != var0);
         }
      }

      return var2;
   }

   private static GlueLoader createGlue(final ClassLoader var0) {
      return AccessController.doPrivileged(new PrivilegedAction<GlueLoader>() {
         public GlueLoader run() {
            return var0 != null ? new GlueLoader(var0) : new GlueLoader();
         }
      });
   }
}
