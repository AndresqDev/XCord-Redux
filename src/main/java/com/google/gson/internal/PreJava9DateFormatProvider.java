package com.google.gson.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PreJava9DateFormatProvider {
   public static DateFormat getUSDateFormat(int var0) {
      return new SimpleDateFormat(getDateFormatPattern(var0), Locale.US);
   }

   public static DateFormat getUSDateTimeFormat(int var0, int var1) {
      String var2 = getDatePartOfDateTimePattern(var0) + " " + getTimePartOfDateTimePattern(var1);
      return new SimpleDateFormat(var2, Locale.US);
   }

   private static String getDateFormatPattern(int var0) {
      switch (var0) {
         case 0:
            return "EEEE, MMMM d, y";
         case 1:
            return "MMMM d, y";
         case 2:
            return "MMM d, y";
         case 3:
            return "M/d/yy";
         default:
            throw new IllegalArgumentException("Unknown DateFormat style: " + var0);
      }
   }

   private static String getDatePartOfDateTimePattern(int var0) {
      switch (var0) {
         case 0:
            return "EEEE, MMMM d, yyyy";
         case 1:
            return "MMMM d, yyyy";
         case 2:
            return "MMM d, yyyy";
         case 3:
            return "M/d/yy";
         default:
            throw new IllegalArgumentException("Unknown DateFormat style: " + var0);
      }
   }

   private static String getTimePartOfDateTimePattern(int var0) {
      switch (var0) {
         case 0:
         case 1:
            return "h:mm:ss a z";
         case 2:
            return "h:mm:ss a";
         case 3:
            return "h:mm a";
         default:
            throw new IllegalArgumentException("Unknown DateFormat style: " + var0);
      }
   }
}
