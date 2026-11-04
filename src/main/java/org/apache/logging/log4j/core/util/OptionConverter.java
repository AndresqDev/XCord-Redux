package org.apache.logging.log4j.core.util;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.status.StatusLogger;

public final class OptionConverter {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int ONE_K = 1024;

   private OptionConverter() {
   }

   public static String[] concatenateArrays(String[] var0, String[] var1) {
      int var2 = var0.length + var1.length;
      String[] var3 = new String[var2];
      System.arraycopy(var0, 0, var3, 0, var0.length);
      System.arraycopy(var1, 0, var3, var0.length, var1.length);
      return var3;
   }

   public static String convertSpecialChars(String var0) {
      int var2 = var0.length();
      StringBuilder var3 = new StringBuilder(var2);
      int var4 = 0;

      while (var4 < var2) {
         char var1 = var0.charAt(var4++);
         if (var1 == '\\') {
            var1 = var0.charAt(var4++);
            switch (var1) {
               case '"':
                  var1 = '"';
                  break;
               case '\'':
                  var1 = '\'';
                  break;
               case '\\':
                  var1 = '\\';
                  break;
               case 'b':
                  var1 = '\b';
                  break;
               case 'f':
                  var1 = '\f';
                  break;
               case 'n':
                  var1 = '\n';
                  break;
               case 'r':
                  var1 = '\r';
                  break;
               case 't':
                  var1 = '\t';
            }
         }

         var3.append(var1);
      }

      return var3.toString();
   }

   public static Object instantiateByKey(Properties var0, String var1, Class<?> var2, Object var3) {
      String var4 = findAndSubst(var1, var0);
      if (var4 == null) {
         LOGGER.error("Could not find value for key {}", var1);
         return var3;
      } else {
         return instantiateByClassName(var4.trim(), var2, var3);
      }
   }

   public static boolean toBoolean(String var0, boolean var1) {
      if (var0 == null) {
         return var1;
      } else {
         String var2 = var0.trim();
         if ("true".equalsIgnoreCase(var2)) {
            return true;
         } else {
            return "false".equalsIgnoreCase(var2) ? false : var1;
         }
      }
   }

   public static int toInt(String var0, int var1) {
      if (var0 != null) {
         String var2 = var0.trim();

         try {
            return Integer.parseInt(var2);
         } catch (NumberFormatException var4) {
            LOGGER.error("[{}] is not in proper int form.", var2, var4);
         }
      }

      return var1;
   }

   public static Level toLevel(String var0, Level var1) {
      if (var0 == null) {
         return var1;
      } else {
         var0 = var0.trim();
         int var2 = var0.indexOf(35);
         if (var2 == -1) {
            return "NULL".equalsIgnoreCase(var0) ? null : Level.toLevel(var0, var1);
         } else {
            Level var3 = var1;
            String var4 = var0.substring(var2 + 1);
            String var5 = var0.substring(0, var2);
            if ("NULL".equalsIgnoreCase(var5)) {
               return null;
            } else {
               LOGGER.debug("toLevel:class=[" + var4 + "]:pri=[" + var5 + "]");

               try {
                  Class var6 = Loader.loadClass(var4);
                  Class[] var7 = new Class[]{String.class, Level.class};
                  Method var8 = var6.getMethod("toLevel", var7);
                  Object[] var9 = new Object[]{var5, var1};
                  Object var10 = var8.invoke(null, var9);
                  var3 = (Level)var10;
               } catch (ClassNotFoundException var11) {
                  LOGGER.warn("custom level class [" + var4 + "] not found.");
               } catch (NoSuchMethodException var12) {
                  LOGGER.warn("custom level class [" + var4 + "] does not have a class function toLevel(String, Level)", (Throwable)var12);
               } catch (InvocationTargetException var13) {
                  if (var13.getTargetException() instanceof InterruptedException || var13.getTargetException() instanceof InterruptedIOException) {
                     Thread.currentThread().interrupt();
                  }

                  LOGGER.warn("custom level class [" + var4 + "] could not be instantiated", (Throwable)var13);
               } catch (ClassCastException var14) {
                  LOGGER.warn("class [" + var4 + "] is not a subclass of org.apache.log4j.Level", (Throwable)var14);
               } catch (IllegalAccessException var15) {
                  LOGGER.warn("class [" + var4 + "] cannot be instantiated due to access restrictions", (Throwable)var15);
               } catch (RuntimeException var16) {
                  LOGGER.warn("class [" + var4 + "], level [" + var5 + "] conversion failed.", (Throwable)var16);
               }

               return var3;
            }
         }
      }
   }

   public static long toFileSize(String var0, long var1) {
      if (var0 == null) {
         return var1;
      } else {
         String var3 = var0.trim().toUpperCase(Locale.ENGLISH);
         long var4 = 1L;
         int var6;
         if ((var6 = var3.indexOf("KB")) != -1) {
            var4 = 1024L;
            var3 = var3.substring(0, var6);
         } else if ((var6 = var3.indexOf("MB")) != -1) {
            var4 = 1048576L;
            var3 = var3.substring(0, var6);
         } else if ((var6 = var3.indexOf("GB")) != -1) {
            var4 = 1073741824L;
            var3 = var3.substring(0, var6);
         }

         try {
            return Long.parseLong(var3) * var4;
         } catch (NumberFormatException var8) {
            LOGGER.error("[{}] is not in proper int form.", var3);
            LOGGER.error("[{}] not in expected format.", var0, var8);
            return var1;
         }
      }
   }

   public static String findAndSubst(String var0, Properties var1) {
      String var2 = var1.getProperty(var0);
      if (var2 == null) {
         return null;
      } else {
         try {
            return substVars(var2, var1);
         } catch (IllegalArgumentException var4) {
            LOGGER.error("Bad option value [{}].", var2, var4);
            return var2;
         }
      }
   }

   public static Object instantiateByClassName(String var0, Class<?> var1, Object var2) {
      if (var0 != null) {
         try {
            Class var3 = Loader.loadClass(var0);
            if (!var1.isAssignableFrom(var3)) {
               LOGGER.error("A \"{}\" object is not assignable to a \"{}\" variable.", var0, var1.getName());
               LOGGER.error(
                  "The class \"{}\" was loaded by [{}] whereas object of type [{}] was loaded by [{}].",
                  var1.getName(),
                  var1.getClassLoader(),
                  var3.getTypeName(),
                  var3.getName()
               );
               return var2;
            }

            return var3.newInstance();
         } catch (Exception var4) {
            LOGGER.error("Could not instantiate class [{}].", var0, var4);
         }
      }

      return var2;
   }

   public static String substVars(String var0, Properties var1) throws IllegalArgumentException {
      return StrSubstitutor.replace(var0, var1);
   }
}
