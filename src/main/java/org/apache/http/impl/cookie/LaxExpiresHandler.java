package org.apache.http.impl.cookie;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class LaxExpiresHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {
   static final TimeZone UTC = TimeZone.getTimeZone("UTC");
   private static final BitSet DELIMS;
   private static final Map<String, Integer> MONTHS;
   private static final Pattern TIME_PATTERN;
   private static final Pattern DAY_OF_MONTH_PATTERN;
   private static final Pattern MONTH_PATTERN;
   private static final Pattern YEAR_PATTERN;

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (!TextUtils.isBlank(var2)) {
         ParserCursor var3 = new ParserCursor(0, var2.length());
         StringBuilder var4 = new StringBuilder();
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         boolean var11 = false;
         boolean var12 = false;
         boolean var13 = false;
         boolean var14 = false;

         try {
            while (!var3.atEnd()) {
               this.skipDelims(var2, var3);
               var4.setLength(0);
               this.copyContent(var2, var3, var4);
               if (var4.length() == 0) {
                  break;
               }

               if (!var11) {
                  Matcher var15 = TIME_PATTERN.matcher(var4);
                  if (var15.matches()) {
                     var11 = true;
                     var7 = Integer.parseInt(var15.group(1));
                     var6 = Integer.parseInt(var15.group(2));
                     var5 = Integer.parseInt(var15.group(3));
                     continue;
                  }
               }

               if (!var12) {
                  Matcher var17 = DAY_OF_MONTH_PATTERN.matcher(var4);
                  if (var17.matches()) {
                     var12 = true;
                     var8 = Integer.parseInt(var17.group(1));
                     continue;
                  }
               }

               if (!var13) {
                  Matcher var18 = MONTH_PATTERN.matcher(var4);
                  if (var18.matches()) {
                     var13 = true;
                     var9 = MONTHS.get(var18.group(1).toLowerCase(Locale.ROOT));
                     continue;
                  }
               }

               if (!var14) {
                  Matcher var19 = YEAR_PATTERN.matcher(var4);
                  if (var19.matches()) {
                     var14 = true;
                     var10 = Integer.parseInt(var19.group(1));
                  }
               }
            }
         } catch (NumberFormatException var16) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + var2);
         }

         if (var11 && var12 && var13 && var14) {
            if (var10 >= 70 && var10 <= 99) {
               var10 += 1900;
            }

            if (var10 >= 0 && var10 <= 69) {
               var10 += 2000;
            }

            if (var8 >= 1 && var8 <= 31 && var10 >= 1601 && var7 <= 23 && var6 <= 59 && var5 <= 59) {
               Calendar var20 = Calendar.getInstance();
               var20.setTimeZone(UTC);
               var20.setTimeInMillis(0L);
               var20.set(13, var5);
               var20.set(12, var6);
               var20.set(11, var7);
               var20.set(5, var8);
               var20.set(2, var9);
               var20.set(1, var10);
               var1.setExpiryDate(var20.getTime());
            } else {
               throw new MalformedCookieException("Invalid 'expires' attribute: " + var2);
            }
         } else {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + var2);
         }
      }
   }

   private void skipDelims(CharSequence var1, ParserCursor var2) {
      int var3 = var2.getPos();
      int var4 = var2.getPos();
      int var5 = var2.getUpperBound();

      for (int var6 = var4; var6 < var5; var6++) {
         char var7 = var1.charAt(var6);
         if (!DELIMS.get(var7)) {
            break;
         }

         var3++;
      }

      var2.updatePos(var3);
   }

   private void copyContent(CharSequence var1, ParserCursor var2, StringBuilder var3) {
      int var4 = var2.getPos();
      int var5 = var2.getPos();
      int var6 = var2.getUpperBound();

      for (int var7 = var5; var7 < var6; var7++) {
         char var8 = var1.charAt(var7);
         if (DELIMS.get(var8)) {
            break;
         }

         var4++;
         var3.append(var8);
      }

      var2.updatePos(var4);
   }

   @Override
   public String getAttributeName() {
      return "expires";
   }

   static {
      BitSet var0 = new BitSet();
      var0.set(9);

      for (int var1 = 32; var1 <= 47; var1++) {
         var0.set(var1);
      }

      for (int var3 = 59; var3 <= 64; var3++) {
         var0.set(var3);
      }

      for (int var4 = 91; var4 <= 96; var4++) {
         var0.set(var4);
      }

      for (int var5 = 123; var5 <= 126; var5++) {
         var0.set(var5);
      }

      DELIMS = var0;
      ConcurrentHashMap var2 = new ConcurrentHashMap(12);
      var2.put("jan", 0);
      var2.put("feb", 1);
      var2.put("mar", 2);
      var2.put("apr", 3);
      var2.put("may", 4);
      var2.put("jun", 5);
      var2.put("jul", 6);
      var2.put("aug", 7);
      var2.put("sep", 8);
      var2.put("oct", 9);
      var2.put("nov", 10);
      var2.put("dec", 11);
      MONTHS = var2;
      TIME_PATTERN = Pattern.compile("^([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2})([^0-9].*)?$");
      DAY_OF_MONTH_PATTERN = Pattern.compile("^([0-9]{1,2})([^0-9].*)?$");
      MONTH_PATTERN = Pattern.compile("^(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)(.*)?$", 2);
      YEAR_PATTERN = Pattern.compile("^([0-9]{2,4})([^0-9].*)?$");
   }
}
