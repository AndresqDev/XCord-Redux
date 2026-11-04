package org.codehaus.plexus.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ExceptionUtils {
   static final String WRAPPED_MARKER = " [wrapped] ";
   protected static String[] CAUSE_METHOD_NAMES = new String[]{
      "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested"
   };

   protected ExceptionUtils() {
   }

   public static void addCauseMethodName(String var0) {
      if (var0 != null && var0.length() > 0) {
         ArrayList var1 = new ArrayList<>(Arrays.asList(CAUSE_METHOD_NAMES));
         var1.add(var0);
         CAUSE_METHOD_NAMES = var1.toArray(new String[var1.size()]);
      }
   }

   public static Throwable getCause(Throwable var0) {
      return getCause(var0, CAUSE_METHOD_NAMES);
   }

   public static Throwable getCause(Throwable var0, String[] var1) {
      Throwable var2 = getCauseUsingWellKnownTypes(var0);
      if (var2 == null) {
         for (String var6 : var1) {
            var2 = getCauseUsingMethodName(var0, var6);
            if (var2 != null) {
               break;
            }
         }

         if (var2 == null) {
            var2 = getCauseUsingFieldName(var0, "detail");
         }
      }

      return var2;
   }

   public static Throwable getRootCause(Throwable var0) {
      Throwable var1 = getCause(var0);
      if (var1 != null) {
         var0 = var1;

         while ((var0 = getCause(var0)) != null) {
            var1 = var0;
         }
      }

      return var1;
   }

   private static Throwable getCauseUsingWellKnownTypes(Throwable var0) {
      if (var0 instanceof SQLException) {
         return ((SQLException)var0).getNextException();
      } else {
         return var0 instanceof InvocationTargetException ? ((InvocationTargetException)var0).getTargetException() : null;
      }
   }

   private static Throwable getCauseUsingMethodName(Throwable var0, String var1) {
      Method var2 = null;

      try {
         var2 = var0.getClass().getMethod(var1, null);
      } catch (NoSuchMethodException var7) {
      } catch (SecurityException var8) {
      }

      if (var2 != null && Throwable.class.isAssignableFrom(var2.getReturnType())) {
         try {
            return (Throwable)var2.invoke(var0);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         } catch (InvocationTargetException var6) {
         }
      }

      return null;
   }

   private static Throwable getCauseUsingFieldName(Throwable var0, String var1) {
      Field var2 = null;

      try {
         var2 = var0.getClass().getField(var1);
      } catch (NoSuchFieldException var6) {
      } catch (SecurityException var7) {
      }

      if (var2 != null && Throwable.class.isAssignableFrom(var2.getType())) {
         try {
            return (Throwable)var2.get(var0);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         }
      }

      return null;
   }

   public static int getThrowableCount(Throwable var0) {
      int var1 = 0;

      while (var0 != null) {
         var1++;
         var0 = getCause(var0);
      }

      return var1;
   }

   public static Throwable[] getThrowables(Throwable var0) {
      ArrayList var1 = new ArrayList();

      while (var0 != null) {
         var1.add(var0);
         var0 = getCause(var0);
      }

      return var1.toArray(new Throwable[var1.size()]);
   }

   public static int indexOfThrowable(Throwable var0, Class var1) {
      return indexOfThrowable(var0, var1, 0);
   }

   public static int indexOfThrowable(Throwable var0, Class var1, int var2) {
      if (var2 < 0) {
         throw new IndexOutOfBoundsException("Throwable index out of range: " + var2);
      } else {
         Throwable[] var3 = getThrowables(var0);
         if (var2 >= var3.length) {
            throw new IndexOutOfBoundsException("Throwable index out of range: " + var2);
         } else {
            for (int var4 = var2; var4 < var3.length; var4++) {
               if (var3[var4].getClass().equals(var1)) {
                  return var4;
               }
            }

            return -1;
         }
      }
   }

   public static void printRootCauseStackTrace(Throwable var0, PrintStream var1) {
      String[] var2 = getRootCauseStackTrace(var0);

      for (String var6 : var2) {
         var1.println(var6);
      }

      var1.flush();
   }

   public static void printRootCauseStackTrace(Throwable var0) {
      printRootCauseStackTrace(var0, System.err);
   }

   public static void printRootCauseStackTrace(Throwable var0, PrintWriter var1) {
      String[] var2 = getRootCauseStackTrace(var0);

      for (String var6 : var2) {
         var1.println(var6);
      }

      var1.flush();
   }

   public static String[] getRootCauseStackTrace(Throwable var0) {
      Throwable[] var1 = getThrowables(var0);
      int var2 = var1.length;
      ArrayList var3 = new ArrayList();
      List var4 = getStackFrameList(var1[var2 - 1]);
      int var5 = var2;

      while (--var5 >= 0) {
         List var6 = var4;
         if (var5 != 0) {
            var4 = getStackFrameList(var1[var5 - 1]);
            removeCommonFrames(var6, var4);
         }

         if (var5 == var2 - 1) {
            var3.add(var1[var5].toString());
         } else {
            var3.add(" [wrapped] " + var1[var5].toString());
         }

         for (String var8 : var6) {
            var3.add(var8);
         }
      }

      return var3.toArray(new String[var3.size()]);
   }

   private static void removeCommonFrames(List<String> var0, List<String> var1) {
      int var2 = var0.size() - 1;

      for (int var3 = var1.size() - 1; var2 >= 0 && var3 >= 0; var3--) {
         String var4 = (String)var0.get(var2);
         String var5 = (String)var1.get(var3);
         if (var4.equals(var5)) {
            var0.remove(var2);
         }

         var2--;
      }
   }

   public static String getStackTrace(Throwable var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1, true);
      var0.printStackTrace(var2);
      return var1.getBuffer().toString();
   }

   public static String getFullStackTrace(Throwable var0) {
      StringWriter var1 = new StringWriter();
      PrintWriter var2 = new PrintWriter(var1, true);
      Throwable[] var3 = getThrowables(var0);

      for (Throwable var7 : var3) {
         var7.printStackTrace(var2);
         if (isNestedThrowable(var7)) {
            break;
         }
      }

      return var1.getBuffer().toString();
   }

   public static boolean isNestedThrowable(Throwable var0) {
      if (var0 == null) {
         return false;
      } else if (var0 instanceof SQLException) {
         return true;
      } else if (var0 instanceof InvocationTargetException) {
         return true;
      } else {
         for (String var4 : CAUSE_METHOD_NAMES) {
            try {
               Method var5 = var0.getClass().getMethod(var4, null);
               if (var5 != null) {
                  return true;
               }
            } catch (NoSuchMethodException var8) {
            } catch (SecurityException var9) {
            }
         }

         try {
            Field var10 = var0.getClass().getField("detail");
            if (var10 != null) {
               return true;
            }
         } catch (NoSuchFieldException var6) {
         } catch (SecurityException var7) {
         }

         return false;
      }
   }

   public static String[] getStackFrames(Throwable var0) {
      return getStackFrames(getStackTrace(var0));
   }

   static String[] getStackFrames(String var0) {
      String var1 = System.getProperty("line.separator");
      StringTokenizer var2 = new StringTokenizer(var0, var1);
      LinkedList var3 = new LinkedList();

      while (var2.hasMoreTokens()) {
         var3.add(var2.nextToken());
      }

      return var3.toArray(new String[var3.size()]);
   }

   static List<String> getStackFrameList(Throwable var0) {
      String var1 = getStackTrace(var0);
      String var2 = System.getProperty("line.separator");
      StringTokenizer var3 = new StringTokenizer(var1, var2);
      LinkedList var4 = new LinkedList();
      boolean var5 = false;

      while (var3.hasMoreTokens()) {
         String var6 = var3.nextToken();
         int var7 = var6.indexOf("at");
         if (var7 != -1 && var6.substring(0, var7).trim().length() == 0) {
            var5 = true;
            var4.add(var6);
         } else if (var5) {
            break;
         }
      }

      return var4;
   }
}
