package org.apache.maven.model.interpolation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

public class MavenBuildTimestamp {
   public static final String DEFAULT_BUILD_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   public static final String BUILD_TIMESTAMP_FORMAT_PROPERTY = "maven.build.timestamp.format";
   public static final TimeZone DEFAULT_BUILD_TIME_ZONE = TimeZone.getTimeZone("Etc/UTC");
   private String formattedTimestamp;

   public MavenBuildTimestamp() {
      this(new Date());
   }

   public MavenBuildTimestamp(Date var1) {
      this(var1, "yyyy-MM-dd'T'HH:mm:ss'Z'");
   }

   public MavenBuildTimestamp(Date var1, Properties var2) {
      this(var1, var2 != null ? var2.getProperty("maven.build.timestamp.format") : null);
   }

   public MavenBuildTimestamp(Date var1, String var2) {
      if (var2 == null) {
         var2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
      }

      if (var1 == null) {
         var1 = new Date();
      }

      SimpleDateFormat var3 = new SimpleDateFormat(var2);
      var3.setCalendar(new GregorianCalendar());
      var3.setTimeZone(DEFAULT_BUILD_TIME_ZONE);
      this.formattedTimestamp = var3.format(var1);
   }

   public String formattedTimestamp() {
      return this.formattedTimestamp;
   }
}
