package org.apache.commons.logging.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

public class SimpleLog implements Serializable, Log {
   private static final long serialVersionUID = 136942970684951178L;
   protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
   protected static final Properties simpleLogProps = new Properties();
   protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
   protected static boolean showLogName = false;
   protected static boolean showShortName = true;
   protected static boolean showDateTime = false;
   protected static String dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
   protected static DateFormat dateFormatter = null;
   public static final int LOG_LEVEL_TRACE = 1;
   public static final int LOG_LEVEL_DEBUG = 2;
   public static final int LOG_LEVEL_INFO = 3;
   public static final int LOG_LEVEL_WARN = 4;
   public static final int LOG_LEVEL_ERROR = 5;
   public static final int LOG_LEVEL_FATAL = 6;
   public static final int LOG_LEVEL_ALL = 0;
   public static final int LOG_LEVEL_OFF = 7;
   protected String logName = null;
   protected int currentLogLevel;
   private String shortLogName = null;

   private static String getStringProperty(String var0) {
      String var1 = null;

      try {
         var1 = System.getProperty(var0);
      } catch (SecurityException var3) {
      }

      return var1 == null ? simpleLogProps.getProperty(var0) : var1;
   }

   private static String getStringProperty(String var0, String var1) {
      String var2 = getStringProperty(var0);
      return var2 == null ? var1 : var2;
   }

   private static boolean getBooleanProperty(String var0, boolean var1) {
      String var2 = getStringProperty(var0);
      return var2 == null ? var1 : "true".equalsIgnoreCase(var2);
   }

   public SimpleLog(String var1) {
      this.logName = var1;
      this.setLevel(3);
      String var2 = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);

      for (int var3 = String.valueOf(var1).lastIndexOf("."); null == var2 && var3 > -1; var3 = String.valueOf(var1).lastIndexOf(".")) {
         var1 = var1.substring(0, var3);
         var2 = getStringProperty("org.apache.commons.logging.simplelog.log." + var1);
      }

      if (null == var2) {
         var2 = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
      }

      if ("all".equalsIgnoreCase(var2)) {
         this.setLevel(0);
      } else if ("trace".equalsIgnoreCase(var2)) {
         this.setLevel(1);
      } else if ("debug".equalsIgnoreCase(var2)) {
         this.setLevel(2);
      } else if ("info".equalsIgnoreCase(var2)) {
         this.setLevel(3);
      } else if ("warn".equalsIgnoreCase(var2)) {
         this.setLevel(4);
      } else if ("error".equalsIgnoreCase(var2)) {
         this.setLevel(5);
      } else if ("fatal".equalsIgnoreCase(var2)) {
         this.setLevel(6);
      } else if ("off".equalsIgnoreCase(var2)) {
         this.setLevel(7);
      }
   }

   public void setLevel(int var1) {
      this.currentLogLevel = var1;
   }

   public int getLevel() {
      return this.currentLogLevel;
   }

   protected void log(int var1, Object var2, Throwable var3) {
      StringBuffer var4 = new StringBuffer();
      if (showDateTime) {
         var4.append(dateFormatter.format(new Date()));
         var4.append(" ");
      }

      switch (var1) {
         case 1:
            var4.append("[TRACE] ");
            break;
         case 2:
            var4.append("[DEBUG] ");
            break;
         case 3:
            var4.append("[INFO] ");
            break;
         case 4:
            var4.append("[WARN] ");
            break;
         case 5:
            var4.append("[ERROR] ");
            break;
         case 6:
            var4.append("[FATAL] ");
      }

      if (showShortName) {
         if (this.shortLogName == null) {
            this.shortLogName = this.logName.substring(this.logName.lastIndexOf(".") + 1);
            this.shortLogName = this.shortLogName.substring(this.shortLogName.lastIndexOf("/") + 1);
         }

         var4.append(String.valueOf(this.shortLogName)).append(" - ");
      } else if (showLogName) {
         var4.append(String.valueOf(this.logName)).append(" - ");
      }

      var4.append(String.valueOf(var2));
      if (var3 != null) {
         var4.append(" <");
         var4.append(var3.toString());
         var4.append(">");
         StringWriter var5 = new StringWriter(1024);
         PrintWriter var6 = new PrintWriter(var5);
         var3.printStackTrace(var6);
         var6.close();
         var4.append(var5.toString());
      }

      this.write(var4);
   }

   protected void write(StringBuffer var1) {
      System.err.println(var1.toString());
   }

   protected boolean isLevelEnabled(int var1) {
      return var1 >= this.currentLogLevel;
   }

   @Override
   public final void debug(Object var1) {
      if (this.isLevelEnabled(2)) {
         this.log(2, var1, null);
      }
   }

   @Override
   public final void debug(Object var1, Throwable var2) {
      if (this.isLevelEnabled(2)) {
         this.log(2, var1, var2);
      }
   }

   @Override
   public final void trace(Object var1) {
      if (this.isLevelEnabled(1)) {
         this.log(1, var1, null);
      }
   }

   @Override
   public final void trace(Object var1, Throwable var2) {
      if (this.isLevelEnabled(1)) {
         this.log(1, var1, var2);
      }
   }

   @Override
   public final void info(Object var1) {
      if (this.isLevelEnabled(3)) {
         this.log(3, var1, null);
      }
   }

   @Override
   public final void info(Object var1, Throwable var2) {
      if (this.isLevelEnabled(3)) {
         this.log(3, var1, var2);
      }
   }

   @Override
   public final void warn(Object var1) {
      if (this.isLevelEnabled(4)) {
         this.log(4, var1, null);
      }
   }

   @Override
   public final void warn(Object var1, Throwable var2) {
      if (this.isLevelEnabled(4)) {
         this.log(4, var1, var2);
      }
   }

   @Override
   public final void error(Object var1) {
      if (this.isLevelEnabled(5)) {
         this.log(5, var1, null);
      }
   }

   @Override
   public final void error(Object var1, Throwable var2) {
      if (this.isLevelEnabled(5)) {
         this.log(5, var1, var2);
      }
   }

   @Override
   public final void fatal(Object var1) {
      if (this.isLevelEnabled(6)) {
         this.log(6, var1, null);
      }
   }

   @Override
   public final void fatal(Object var1, Throwable var2) {
      if (this.isLevelEnabled(6)) {
         this.log(6, var1, var2);
      }
   }

   @Override
   public final boolean isDebugEnabled() {
      return this.isLevelEnabled(2);
   }

   @Override
   public final boolean isErrorEnabled() {
      return this.isLevelEnabled(5);
   }

   @Override
   public final boolean isFatalEnabled() {
      return this.isLevelEnabled(6);
   }

   @Override
   public final boolean isInfoEnabled() {
      return this.isLevelEnabled(3);
   }

   @Override
   public final boolean isTraceEnabled() {
      return this.isLevelEnabled(1);
   }

   @Override
   public final boolean isWarnEnabled() {
      return this.isLevelEnabled(4);
   }

   private static ClassLoader getContextClassLoader() {
      ClassLoader var0 = null;
      if (var0 == null) {
         try {
            Method var1 = Thread.class.getMethod("getContextClassLoader");

            try {
               var0 = (ClassLoader)var1.invoke(Thread.currentThread());
            } catch (IllegalAccessException var3) {
            } catch (InvocationTargetException var4) {
               if (!(var4.getTargetException() instanceof SecurityException)) {
                  throw new LogConfigurationException("Unexpected InvocationTargetException", var4.getTargetException());
               }
            }
         } catch (NoSuchMethodException var5) {
         }
      }

      if (var0 == null) {
         var0 = SimpleLog.class.getClassLoader();
      }

      return var0;
   }

   private static InputStream getResourceAsStream(final String var0) {
      return AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            ClassLoader var1 = SimpleLog.getContextClassLoader();
            return var1 != null ? var1.getResourceAsStream(var0) : ClassLoader.getSystemResourceAsStream(var0);
         }
      });
   }

   static {
      InputStream var0 = getResourceAsStream("simplelog.properties");
      if (null != var0) {
         try {
            simpleLogProps.load(var0);
         } catch (IOException var12) {
         } finally {
            try {
               var0.close();
            } catch (IOException var10) {
            }
         }
      }

      showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
      showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", showShortName);
      showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", showDateTime);
      if (showDateTime) {
         dateTimeFormat = getStringProperty("org.apache.commons.logging.simplelog.dateTimeFormat", dateTimeFormat);

         try {
            dateFormatter = new SimpleDateFormat(dateTimeFormat);
         } catch (IllegalArgumentException var11) {
            dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
            dateFormatter = new SimpleDateFormat(dateTimeFormat);
         }
      }
   }
}
