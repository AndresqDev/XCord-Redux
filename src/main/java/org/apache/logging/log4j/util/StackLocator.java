package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import java.util.Stack;
import java.util.function.Predicate;

public final class StackLocator {
   static final int JDK_7u25_OFFSET;
   private static final Method GET_CALLER_CLASS;
   private static final StackLocator INSTANCE;

   public static StackLocator getInstance() {
      return INSTANCE;
   }

   private StackLocator() {
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(Class<?> var1, Predicate<Class<?>> var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("sentinelClass cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("callerPredicate cannot be null");
      } else {
         boolean var3 = false;

         Class var4;
         for (int var5 = 2; null != (var4 = this.getCallerClass(var5)); var5++) {
            if (var1.equals(var4)) {
               var3 = true;
            } else if (var3 && var2.test(var4)) {
               return var4;
            }
         }

         return null;
      }
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException(Integer.toString(var1));
      } else if (GET_CALLER_CLASS == null) {
         return null;
      } else {
         try {
            return (Class<?>)GET_CALLER_CLASS.invoke(null, var1 + 1 + JDK_7u25_OFFSET);
         } catch (Exception var3) {
            return null;
         }
      }
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(String var1, String var2) {
      boolean var3 = false;

      Class var4;
      for (int var5 = 2; null != (var4 = this.getCallerClass(var5)); var5++) {
         if (var1.equals(var4.getName())) {
            var3 = true;
         } else if (var3 && var4.getName().startsWith(var2)) {
            return var4;
         }
      }

      return null;
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(Class<?> var1) {
      boolean var2 = false;

      Class var3;
      for (int var4 = 2; null != (var3 = this.getCallerClass(var4)); var4++) {
         if (var1.equals(var3)) {
            var2 = true;
         } else if (var2) {
            return var3;
         }
      }

      return Object.class;
   }

   @PerformanceSensitive
   public Stack<Class<?>> getCurrentStackTrace() {
      if (PrivateSecurityManagerStackTraceUtil.isEnabled()) {
         return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace();
      } else {
         Stack var1 = new Stack();

         Class var2;
         for (int var3 = 1; null != (var2 = this.getCallerClass(var3)); var3++) {
            var1.push(var2);
         }

         return var1;
      }
   }

   public StackTraceElement calcLocation(String var1) {
      if (var1 == null) {
         return null;
      } else {
         StackTraceElement[] var2 = new Throwable().getStackTrace();
         boolean var3 = false;

         for (int var4 = 0; var4 < var2.length; var4++) {
            String var5 = var2[var4].getClassName();
            if (var1.equals(var5)) {
               var3 = true;
            } else if (var3 && !var1.equals(var5)) {
               return var2[var4];
            }
         }

         return null;
      }
   }

   public StackTraceElement getStackTraceElement(int var1) {
      StackTraceElement[] var2 = new Throwable().getStackTrace();
      int var3 = 0;

      for (StackTraceElement var7 : var2) {
         if (this.isValid(var7)) {
            if (var3 == var1) {
               return var7;
            }

            var3++;
         }
      }

      throw new IndexOutOfBoundsException(Integer.toString(var1));
   }

   private boolean isValid(StackTraceElement var1) {
      if (var1.isNativeMethod()) {
         return false;
      } else {
         String var2 = var1.getClassName();
         if (var2.startsWith("sun.reflect.")) {
            return false;
         } else {
            String var3 = var1.getMethodName();
            if (!var2.startsWith("java.lang.reflect.") || !var3.equals("invoke") && !var3.equals("newInstance")) {
               if (var2.startsWith("jdk.internal.reflect.")) {
                  return false;
               } else {
                  return var2.equals("java.lang.Class") && var3.equals("newInstance")
                     ? false
                     : !var2.equals("java.lang.invoke.MethodHandle") || !var3.startsWith("invoke");
               }
            } else {
               return false;
            }
         }
      }
   }

   static {
      byte var1 = 0;

      Method var0;
      try {
         Class var2 = LoaderUtil.loadClass("sun.reflect.Reflection");
         var0 = var2.getDeclaredMethod("getCallerClass", int.class);
         Object var3 = var0.invoke(null, 0);
         var0.invoke(null, 0);
         if (var3 != null && var3 == var2) {
            var3 = var0.invoke(null, 1);
            if (var3 == var2) {
               System.out
                  .println(
                     "WARNING: Java 1.7.0_25 is in use which has a broken implementation of Reflection.getCallerClass().  Please consider upgrading to Java 1.7.0_40 or later."
                  );
               var1 = 1;
            }
         } else {
            var0 = null;
            var1 = -1;
         }
      } catch (LinkageError | Exception var4) {
         System.out.println("WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.");
         var0 = null;
         var1 = -1;
      }

      GET_CALLER_CLASS = var0;
      JDK_7u25_OFFSET = var1;
      INSTANCE = new StackLocator();
   }
}
