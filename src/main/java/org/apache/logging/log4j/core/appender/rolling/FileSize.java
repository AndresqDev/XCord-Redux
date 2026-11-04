package org.apache.logging.log4j.core.appender.rolling;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class FileSize {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final long KB = 1024L;
   private static final long MB = 1048576L;
   private static final long GB = 1073741824L;
   private static final Pattern VALUE_PATTERN = Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G)B?", 2);

   private FileSize() {
   }

   public static long parse(String var0, long var1) {
      Matcher var3 = VALUE_PATTERN.matcher(var0);
      if (var3.matches()) {
         try {
            double var4 = NumberFormat.getNumberInstance(Locale.ROOT).parse(var3.group(1)).doubleValue();
            String var6 = var3.group(3);
            if (var6.isEmpty()) {
               return (long)var4;
            } else if (var6.equalsIgnoreCase("K")) {
               return (long)(var4 * 1024.0);
            } else if (var6.equalsIgnoreCase("M")) {
               return (long)(var4 * 1048576.0);
            } else if (var6.equalsIgnoreCase("G")) {
               return (long)(var4 * 1.0737418E9F);
            } else {
               LOGGER.error("FileSize units not recognized: " + var0);
               return var1;
            }
         } catch (ParseException var7) {
            LOGGER.error("FileSize unable to parse numeric part: " + var0, (Throwable)var7);
            return var1;
         }
      } else {
         LOGGER.error("FileSize unable to parse bytes: " + var0);
         return var1;
      }
   }
}
