package org.apache.commons.lang3.time;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastTimeZone {
   private static final Pattern GMT_PATTERN = Pattern.compile("^(?:(?i)GMT)?([+-])?(\\d\\d?)?(:?(\\d\\d?))?$");
   private static final TimeZone GREENWICH = new GmtTimeZone(false, 0, 0);

   public static TimeZone getGmtTimeZone() {
      return GREENWICH;
   }

   public static TimeZone getGmtTimeZone(String var0) {
      if (!"Z".equals(var0) && !"UTC".equals(var0)) {
         Matcher var1 = GMT_PATTERN.matcher(var0);
         if (var1.matches()) {
            int var2 = parseInt(var1.group(2));
            int var3 = parseInt(var1.group(4));
            return (TimeZone)(var2 == 0 && var3 == 0 ? GREENWICH : new GmtTimeZone(parseSign(var1.group(1)), var2, var3));
         } else {
            return null;
         }
      } else {
         return GREENWICH;
      }
   }

   public static TimeZone getTimeZone(String var0) {
      TimeZone var1 = getGmtTimeZone(var0);
      return var1 != null ? var1 : TimeZone.getTimeZone(var0);
   }

   private static int parseInt(String var0) {
      return var0 != null ? Integer.parseInt(var0) : 0;
   }

   private static boolean parseSign(String var0) {
      return var0 != null && var0.charAt(0) == '-';
   }

   private FastTimeZone() {
   }
}
