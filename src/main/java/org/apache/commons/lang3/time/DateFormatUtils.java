package org.apache.commons.lang3.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatUtils {
   private static final TimeZone UTC_TIME_ZONE = FastTimeZone.getGmtTimeZone();
   public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
   @Deprecated
   public static final FastDateFormat ISO_DATETIME_FORMAT = ISO_8601_EXTENDED_DATETIME_FORMAT;
   public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");
   @Deprecated
   public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;
   public static final FastDateFormat ISO_8601_EXTENDED_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
   @Deprecated
   public static final FastDateFormat ISO_DATE_FORMAT = ISO_8601_EXTENDED_DATE_FORMAT;
   @Deprecated
   public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");
   @Deprecated
   public static final FastDateFormat ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");
   @Deprecated
   public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");
   public static final FastDateFormat ISO_8601_EXTENDED_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
   @Deprecated
   public static final FastDateFormat ISO_TIME_NO_T_FORMAT = ISO_8601_EXTENDED_TIME_FORMAT;
   public static final FastDateFormat ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");
   @Deprecated
   public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT = ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT;
   public static final FastDateFormat SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

   public static String formatUTC(long var0, String var2) {
      return format(new Date(var0), var2, UTC_TIME_ZONE, null);
   }

   public static String formatUTC(Date var0, String var1) {
      return format(var0, var1, UTC_TIME_ZONE, null);
   }

   public static String formatUTC(long var0, String var2, Locale var3) {
      return format(new Date(var0), var2, UTC_TIME_ZONE, var3);
   }

   public static String formatUTC(Date var0, String var1, Locale var2) {
      return format(var0, var1, UTC_TIME_ZONE, var2);
   }

   public static String format(long var0, String var2) {
      return format(new Date(var0), var2, null, null);
   }

   public static String format(Date var0, String var1) {
      return format(var0, var1, null, null);
   }

   public static String format(Calendar var0, String var1) {
      return format(var0, var1, null, null);
   }

   public static String format(long var0, String var2, TimeZone var3) {
      return format(new Date(var0), var2, var3, null);
   }

   public static String format(Date var0, String var1, TimeZone var2) {
      return format(var0, var1, var2, null);
   }

   public static String format(Calendar var0, String var1, TimeZone var2) {
      return format(var0, var1, var2, null);
   }

   public static String format(long var0, String var2, Locale var3) {
      return format(new Date(var0), var2, null, var3);
   }

   public static String format(Date var0, String var1, Locale var2) {
      return format(var0, var1, null, var2);
   }

   public static String format(Calendar var0, String var1, Locale var2) {
      return format(var0, var1, null, var2);
   }

   public static String format(long var0, String var2, TimeZone var3, Locale var4) {
      return format(new Date(var0), var2, var3, var4);
   }

   public static String format(Date var0, String var1, TimeZone var2, Locale var3) {
      FastDateFormat var4 = FastDateFormat.getInstance(var1, var2, var3);
      return var4.format(var0);
   }

   public static String format(Calendar var0, String var1, TimeZone var2, Locale var3) {
      FastDateFormat var4 = FastDateFormat.getInstance(var1, var2, var3);
      return var4.format(var0);
   }
}
