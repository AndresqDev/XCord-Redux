package org.apache.logging.log4j.util;

public final class Constants {
   public static final boolean IS_WEB_APP = PropertiesUtil.getProperties()
      .getBooleanProperty("log4j2.is.webapp", isClassAvailable("javax.servlet.Servlet") || isClassAvailable("jakarta.servlet.Servlet"));
   public static final boolean ENABLE_THREADLOCALS = !IS_WEB_APP && PropertiesUtil.getProperties().getBooleanProperty("log4j2.enable.threadlocals", true);
   public static final int JAVA_MAJOR_VERSION = getMajorVersion();
   public static final int MAX_REUSABLE_MESSAGE_SIZE = size("log4j.maxReusableMsgSize", 518);
   public static final String LOG4J2_DEBUG = "log4j2.debug";
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

   private static int size(String var0, int var1) {
      return PropertiesUtil.getProperties().getIntegerProperty(var0, var1);
   }

   private static boolean isClassAvailable(String var0) {
      try {
         return LoaderUtil.loadClass(var0) != null;
      } catch (Throwable var2) {
         return false;
      }
   }

   private Constants() {
   }

   private static int getMajorVersion() {
      return getMajorVersion(System.getProperty("java.version"));
   }

   static int getMajorVersion(String var0) {
      String[] var1 = var0.split("-|\\.");

      try {
         int var3 = Integer.parseInt(var1[0]);
         boolean var2 = var3 != 1;
         return var2 ? var3 : Integer.parseInt(var1[1]);
      } catch (Exception var4) {
         return 0;
      }
   }
}
