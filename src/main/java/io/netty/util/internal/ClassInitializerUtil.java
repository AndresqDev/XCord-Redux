package io.netty.util.internal;

public final class ClassInitializerUtil {
   private ClassInitializerUtil() {
   }

   public static void tryLoadClasses(Class<?> var0, Class<?>... var1) {
      ClassLoader var2 = PlatformDependent.getClassLoader(var0);

      for (Class var6 : var1) {
         tryLoadClass(var2, var6.getName());
      }
   }

   private static void tryLoadClass(ClassLoader var0, String var1) {
      try {
         Class.forName(var1, true, var0);
      } catch (ClassNotFoundException var3) {
      } catch (SecurityException var4) {
      }
   }
}
