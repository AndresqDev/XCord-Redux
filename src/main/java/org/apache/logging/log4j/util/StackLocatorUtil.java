package org.apache.logging.log4j.util;

import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Predicate;
import org.apache.logging.log4j.status.StatusLogger;

public final class StackLocatorUtil {
   private static StackLocator stackLocator = null;
   private static volatile boolean errorLogged;

   private StackLocatorUtil() {
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(int var0) {
      return stackLocator.getCallerClass(var0 + 1);
   }

   public static StackTraceElement getStackTraceElement(int var0) {
      return stackLocator.getStackTraceElement(var0 + 1);
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(String var0) {
      return getCallerClass(var0, "");
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(String var0, String var1) {
      return stackLocator.getCallerClass(var0, var1);
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(Class<?> var0, Predicate<Class<?>> var1) {
      return stackLocator.getCallerClass(var0, var1);
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(Class<?> var0) {
      return stackLocator.getCallerClass(var0);
   }

   @PerformanceSensitive
   public static Stack<Class<?>> getCurrentStackTrace() {
      return stackLocator.getCurrentStackTrace();
   }

   public static StackTraceElement calcLocation(String var0) {
      try {
         return stackLocator.calcLocation(var0);
      } catch (NoSuchElementException var2) {
         if (!errorLogged) {
            errorLogged = true;
            StatusLogger.getLogger().warn("Unable to locate stack trace element for {}", var0, var2);
         }

         return null;
      }
   }

   static {
      stackLocator = StackLocator.getInstance();
   }
}
