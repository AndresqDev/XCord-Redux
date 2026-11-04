package io.netty.util.internal.logging;

import io.netty.util.internal.ObjectUtil;

public abstract class InternalLoggerFactory {
   private static volatile InternalLoggerFactory defaultFactory;

   private static InternalLoggerFactory newDefaultFactory(String var0) {
      InternalLoggerFactory var1 = useSlf4JLoggerFactory(var0);
      if (var1 != null) {
         return var1;
      } else {
         var1 = useLog4J2LoggerFactory(var0);
         if (var1 != null) {
            return var1;
         } else {
            var1 = useLog4JLoggerFactory(var0);
            return var1 != null ? var1 : useJdkLoggerFactory(var0);
         }
      }
   }

   private static InternalLoggerFactory useSlf4JLoggerFactory(String var0) {
      try {
         InternalLoggerFactory var1 = Slf4JLoggerFactory.getInstanceWithNopCheck();
         var1.newInstance(var0).debug("Using SLF4J as the default logging framework");
         return var1;
      } catch (LinkageError var2) {
         return null;
      } catch (Exception var3) {
         return null;
      }
   }

   private static InternalLoggerFactory useLog4J2LoggerFactory(String var0) {
      try {
         InternalLoggerFactory var1 = Log4J2LoggerFactory.INSTANCE;
         var1.newInstance(var0).debug("Using Log4J2 as the default logging framework");
         return var1;
      } catch (LinkageError var2) {
         return null;
      } catch (Exception var3) {
         return null;
      }
   }

   private static InternalLoggerFactory useLog4JLoggerFactory(String var0) {
      try {
         InternalLoggerFactory var1 = Log4JLoggerFactory.INSTANCE;
         var1.newInstance(var0).debug("Using Log4J as the default logging framework");
         return var1;
      } catch (LinkageError var2) {
         return null;
      } catch (Exception var3) {
         return null;
      }
   }

   private static InternalLoggerFactory useJdkLoggerFactory(String var0) {
      InternalLoggerFactory var1 = JdkLoggerFactory.INSTANCE;
      var1.newInstance(var0).debug("Using java.util.logging as the default logging framework");
      return var1;
   }

   public static InternalLoggerFactory getDefaultFactory() {
      if (defaultFactory == null) {
         defaultFactory = newDefaultFactory(InternalLoggerFactory.class.getName());
      }

      return defaultFactory;
   }

   public static void setDefaultFactory(InternalLoggerFactory var0) {
      defaultFactory = ObjectUtil.checkNotNull(var0, "defaultFactory");
   }

   public static InternalLogger getInstance(Class<?> var0) {
      return getInstance(var0.getName());
   }

   public static InternalLogger getInstance(String var0) {
      return getDefaultFactory().newInstance(var0);
   }

   protected abstract InternalLogger newInstance(String var1);
}
