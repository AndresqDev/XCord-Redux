package org.apache.logging.log4j.util;

import java.util.Stack;

final class PrivateSecurityManagerStackTraceUtil {
   private static final PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager SECURITY_MANAGER;

   private PrivateSecurityManagerStackTraceUtil() {
   }

   static boolean isEnabled() {
      return SECURITY_MANAGER != null;
   }

   static Stack<Class<?>> getCurrentStackTrace() {
      Class[] var0 = SECURITY_MANAGER.getClassContext();
      Stack var1 = new Stack();
      var1.ensureCapacity(var0.length);

      for (Class var5 : var0) {
         var1.push(var5);
      }

      return var1;
   }

   static {
      PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager var0;
      try {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(new RuntimePermission("createSecurityManager"));
         }

         var0 = new PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager();
      } catch (SecurityException var2) {
         var0 = null;
      }

      SECURITY_MANAGER = var0;
   }

   private static final class PrivateSecurityManager extends SecurityManager {
      private PrivateSecurityManager() {
      }

      @Override
      protected Class<?>[] getClassContext() {
         return super.getClassContext();
      }
   }
}
