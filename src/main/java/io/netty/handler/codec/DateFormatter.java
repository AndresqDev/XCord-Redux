package io.netty.handler.codec;

import io.netty.util.AsciiString;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import java.util.BitSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateFormatter {
   private static final BitSet DELIMITERS = new BitSet();
   private static final String[] DAY_OF_WEEK_TO_SHORT_NAME;
   private static final String[] CALENDAR_MONTH_TO_SHORT_NAME;
   private static final FastThreadLocal<DateFormatter> INSTANCES;
   private final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
   private final StringBuilder sb = new StringBuilder(29);
   private boolean timeFound;
   private int hours;
   private int minutes;
   private int seconds;
   private boolean dayOfMonthFound;
   private int dayOfMonth;
   private boolean monthFound;
   private int month;
   private boolean yearFound;
   private int year;

   public static Date parseHttpDate(CharSequence var0) {
      return parseHttpDate(var0, 0, var0.length());
   }

   public static Date parseHttpDate(CharSequence var0, int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 == 0) {
         return null;
      } else if (var3 < 0) {
         throw new IllegalArgumentException("Can't have end < start");
      } else if (var3 > 64) {
         throw new IllegalArgumentException("Can't parse more than 64 chars, looks like a user error or a malformed header");
      } else {
         return formatter().parse0(ObjectUtil.checkNotNull(var0, "txt"), var1, var2);
      }
   }

   public static String format(Date var0) {
      return formatter().format0(ObjectUtil.checkNotNull(var0, "date"));
   }

   public static StringBuilder append(Date var0, StringBuilder var1) {
      return formatter().append0(ObjectUtil.checkNotNull(var0, "date"), ObjectUtil.checkNotNull(var1, "sb"));
   }

   private static DateFormatter formatter() {
      DateFormatter var0 = INSTANCES.get();
      var0.reset();
      return var0;
   }

   private static boolean isDelim(char var0) {
      return DELIMITERS.get(var0);
   }

   private static boolean isDigit(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   private static int getNumericalValue(char var0) {
      return var0 - 48;
   }

   private DateFormatter() {
      this.reset();
   }

   public void reset() {
      this.timeFound = false;
      this.hours = -1;
      this.minutes = -1;
      this.seconds = -1;
      this.dayOfMonthFound = false;
      this.dayOfMonth = -1;
      this.monthFound = false;
      this.month = -1;
      this.yearFound = false;
      this.year = -1;
      this.cal.clear();
      this.sb.setLength(0);
   }

   private boolean tryParseTime(CharSequence var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 >= 5 && var4 <= 8) {
         int var5 = -1;
         int var6 = -1;
         int var7 = -1;
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;

         for (int var11 = var2; var11 < var3; var11++) {
            char var12 = var1.charAt(var11);
            if (isDigit(var12)) {
               var9 = var9 * 10 + getNumericalValue(var12);
               if (++var10 > 2) {
                  return false;
               }
            } else {
               if (var12 != ':') {
                  return false;
               }

               if (var10 == 0) {
                  return false;
               }

               switch (var8) {
                  case 0:
                     var5 = var9;
                     break;
                  case 1:
                     var6 = var9;
                     break;
                  default:
                     return false;
               }

               var9 = 0;
               var8++;
               var10 = 0;
            }
         }

         if (var10 > 0) {
            var7 = var9;
         }

         if (var5 >= 0 && var6 >= 0 && var7 >= 0) {
            this.hours = var5;
            this.minutes = var6;
            this.seconds = var7;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean tryParseDayOfMonth(CharSequence var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 == 1) {
         char var5 = var1.charAt(var2);
         if (isDigit(var5)) {
            this.dayOfMonth = getNumericalValue(var5);
            return true;
         }
      } else if (var4 == 2) {
         char var7 = var1.charAt(var2);
         char var6 = var1.charAt(var2 + 1);
         if (isDigit(var7) && isDigit(var6)) {
            this.dayOfMonth = getNumericalValue(var7) * 10 + getNumericalValue(var6);
            return true;
         }
      }

      return false;
   }

   private boolean tryParseMonth(CharSequence var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 != 3) {
         return false;
      } else {
         char var5 = AsciiString.toLowerCase(var1.charAt(var2));
         char var6 = AsciiString.toLowerCase(var1.charAt(var2 + 1));
         char var7 = AsciiString.toLowerCase(var1.charAt(var2 + 2));
         if (var5 == 'j' && var6 == 'a' && var7 == 'n') {
            this.month = 0;
         } else if (var5 == 'f' && var6 == 'e' && var7 == 'b') {
            this.month = 1;
         } else if (var5 == 'm' && var6 == 'a' && var7 == 'r') {
            this.month = 2;
         } else if (var5 == 'a' && var6 == 'p' && var7 == 'r') {
            this.month = 3;
         } else if (var5 == 'm' && var6 == 'a' && var7 == 'y') {
            this.month = 4;
         } else if (var5 == 'j' && var6 == 'u' && var7 == 'n') {
            this.month = 5;
         } else if (var5 == 'j' && var6 == 'u' && var7 == 'l') {
            this.month = 6;
         } else if (var5 == 'a' && var6 == 'u' && var7 == 'g') {
            this.month = 7;
         } else if (var5 == 's' && var6 == 'e' && var7 == 'p') {
            this.month = 8;
         } else if (var5 == 'o' && var6 == 'c' && var7 == 't') {
            this.month = 9;
         } else if (var5 == 'n' && var6 == 'o' && var7 == 'v') {
            this.month = 10;
         } else {
            if (var5 != 'd' || var6 != 'e' || var7 != 'c') {
               return false;
            }

            this.month = 11;
         }

         return true;
      }
   }

   private boolean tryParseYear(CharSequence var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 == 2) {
         char var5 = var1.charAt(var2);
         char var6 = var1.charAt(var2 + 1);
         if (isDigit(var5) && isDigit(var6)) {
            this.year = getNumericalValue(var5) * 10 + getNumericalValue(var6);
            return true;
         }
      } else if (var4 == 4) {
         char var9 = var1.charAt(var2);
         char var10 = var1.charAt(var2 + 1);
         char var7 = var1.charAt(var2 + 2);
         char var8 = var1.charAt(var2 + 3);
         if (isDigit(var9) && isDigit(var10) && isDigit(var7) && isDigit(var8)) {
            this.year = getNumericalValue(var9) * 1000 + getNumericalValue(var10) * 100 + getNumericalValue(var7) * 10 + getNumericalValue(var8);
            return true;
         }
      }

      return false;
   }

   private boolean parseToken(CharSequence var1, int var2, int var3) {
      if (!this.timeFound) {
         this.timeFound = this.tryParseTime(var1, var2, var3);
         if (this.timeFound) {
            return this.dayOfMonthFound && this.monthFound && this.yearFound;
         }
      }

      if (!this.dayOfMonthFound) {
         this.dayOfMonthFound = this.tryParseDayOfMonth(var1, var2, var3);
         if (this.dayOfMonthFound) {
            return this.timeFound && this.monthFound && this.yearFound;
         }
      }

      if (!this.monthFound) {
         this.monthFound = this.tryParseMonth(var1, var2, var3);
         if (this.monthFound) {
            return this.timeFound && this.dayOfMonthFound && this.yearFound;
         }
      }

      if (!this.yearFound) {
         this.yearFound = this.tryParseYear(var1, var2, var3);
      }

      return this.timeFound && this.dayOfMonthFound && this.monthFound && this.yearFound;
   }

   private Date parse0(CharSequence var1, int var2, int var3) {
      boolean var4 = this.parse1(var1, var2, var3);
      return var4 && this.normalizeAndValidate() ? this.computeDate() : null;
   }

   private boolean parse1(CharSequence var1, int var2, int var3) {
      int var4 = -1;

      for (int var5 = var2; var5 < var3; var5++) {
         char var6 = var1.charAt(var5);
         if (isDelim(var6)) {
            if (var4 != -1) {
               if (this.parseToken(var1, var4, var5)) {
                  return true;
               }

               var4 = -1;
            }
         } else if (var4 == -1) {
            var4 = var5;
         }
      }

      return var4 != -1 && this.parseToken(var1, var4, var1.length());
   }

   private boolean normalizeAndValidate() {
      if (this.dayOfMonth >= 1 && this.dayOfMonth <= 31 && this.hours <= 23 && this.minutes <= 59 && this.seconds <= 59) {
         if (this.year >= 70 && this.year <= 99) {
            this.year += 1900;
         } else if (this.year >= 0 && this.year < 70) {
            this.year += 2000;
         } else if (this.year < 1601) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   private Date computeDate() {
      this.cal.set(5, this.dayOfMonth);
      this.cal.set(2, this.month);
      this.cal.set(1, this.year);
      this.cal.set(11, this.hours);
      this.cal.set(12, this.minutes);
      this.cal.set(13, this.seconds);
      return this.cal.getTime();
   }

   private String format0(Date var1) {
      this.append0(var1, this.sb);
      return this.sb.toString();
   }

   private StringBuilder append0(Date var1, StringBuilder var2) {
      this.cal.setTime(var1);
      var2.append(DAY_OF_WEEK_TO_SHORT_NAME[this.cal.get(7) - 1]).append(", ");
      appendZeroLeftPadded(this.cal.get(5), var2).append(' ');
      var2.append(CALENDAR_MONTH_TO_SHORT_NAME[this.cal.get(2)]).append(' ');
      var2.append(this.cal.get(1)).append(' ');
      appendZeroLeftPadded(this.cal.get(11), var2).append(':');
      appendZeroLeftPadded(this.cal.get(12), var2).append(':');
      return appendZeroLeftPadded(this.cal.get(13), var2).append(" GMT");
   }

   private static StringBuilder appendZeroLeftPadded(int var0, StringBuilder var1) {
      if (var0 < 10) {
         var1.append('0');
      }

      return var1.append(var0);
   }

   static {
      DELIMITERS.set(9);

      for (char var0 = ' '; var0 <= '/'; var0++) {
         DELIMITERS.set(var0);
      }

      for (char var1 = ';'; var1 <= '@'; var1++) {
         DELIMITERS.set(var1);
      }

      for (char var2 = '['; var2 <= '`'; var2++) {
         DELIMITERS.set(var2);
      }

      for (char var3 = '{'; var3 <= '~'; var3++) {
         DELIMITERS.set(var3);
      }

      DAY_OF_WEEK_TO_SHORT_NAME = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
      CALENDAR_MONTH_TO_SHORT_NAME = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
      INSTANCES = new FastThreadLocal<DateFormatter>() {
         protected DateFormatter initialValue() {
            return new DateFormatter();
         }
      };
   }
}
