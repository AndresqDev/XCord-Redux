package org.apache.maven.artifact.versioning;

import java.util.StringTokenizer;
import org.apache.commons.lang3.math.NumberUtils;

public class DefaultArtifactVersion implements ArtifactVersion {
   private Integer majorVersion;
   private Integer minorVersion;
   private Integer incrementalVersion;
   private Integer buildNumber;
   private String qualifier;
   private ComparableVersion comparable;

   public DefaultArtifactVersion(String var1) {
      this.parseVersion(var1);
   }

   @Override
   public int hashCode() {
      return 11 + this.comparable.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof ArtifactVersion) ? false : this.compareTo((ArtifactVersion)var1) == 0;
      }
   }

   public int compareTo(ArtifactVersion var1) {
      return var1 instanceof DefaultArtifactVersion
         ? this.comparable.compareTo(((DefaultArtifactVersion)var1).comparable)
         : this.compareTo((ArtifactVersion)(new DefaultArtifactVersion(var1.toString())));
   }

   @Override
   public int getMajorVersion() {
      return this.majorVersion != null ? this.majorVersion : 0;
   }

   @Override
   public int getMinorVersion() {
      return this.minorVersion != null ? this.minorVersion : 0;
   }

   @Override
   public int getIncrementalVersion() {
      return this.incrementalVersion != null ? this.incrementalVersion : 0;
   }

   @Override
   public int getBuildNumber() {
      return this.buildNumber != null ? this.buildNumber : 0;
   }

   @Override
   public String getQualifier() {
      return this.qualifier;
   }

   @Override
   public final void parseVersion(String var1) {
      this.comparable = new ComparableVersion(var1);
      int var2 = var1.indexOf(45);
      String var4 = null;
      String var3;
      if (var2 < 0) {
         var3 = var1;
      } else {
         var3 = var1.substring(0, var2);
         var4 = var1.substring(var2 + 1);
      }

      if (var4 != null) {
         if (var4.length() != 1 && var4.startsWith("0")) {
            this.qualifier = var4;
         } else {
            this.buildNumber = tryParseInt(var4);
            if (this.buildNumber == null) {
               this.qualifier = var4;
            }
         }
      }

      if (!var3.contains(".") && !var3.startsWith("0")) {
         this.majorVersion = tryParseInt(var3);
         if (this.majorVersion == null) {
            this.qualifier = var1;
            this.buildNumber = null;
         }
      } else {
         boolean var5 = false;
         StringTokenizer var6 = new StringTokenizer(var3, ".");
         if (var6.hasMoreTokens()) {
            this.majorVersion = getNextIntegerToken(var6);
            if (this.majorVersion == null) {
               var5 = true;
            }
         } else {
            var5 = true;
         }

         if (var6.hasMoreTokens()) {
            this.minorVersion = getNextIntegerToken(var6);
            if (this.minorVersion == null) {
               var5 = true;
            }
         }

         if (var6.hasMoreTokens()) {
            this.incrementalVersion = getNextIntegerToken(var6);
            if (this.incrementalVersion == null) {
               var5 = true;
            }
         }

         if (var6.hasMoreTokens()) {
            this.qualifier = var6.nextToken();
            var5 = NumberUtils.isDigits(this.qualifier);
         }

         if (var3.contains("..") || var3.startsWith(".") || var3.endsWith(".")) {
            var5 = true;
         }

         if (var5) {
            this.qualifier = var1;
            this.majorVersion = null;
            this.minorVersion = null;
            this.incrementalVersion = null;
            this.buildNumber = null;
         }
      }
   }

   private static Integer getNextIntegerToken(StringTokenizer var0) {
      String var1 = var0.nextToken();
      return var1.length() > 1 && var1.startsWith("0") ? null : tryParseInt(var1);
   }

   private static Integer tryParseInt(String var0) {
      if (!NumberUtils.isDigits(var0)) {
         return null;
      } else {
         try {
            long var1 = Long.parseLong(var0);
            return var1 > 2147483647L ? null : (int)var1;
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   @Override
   public String toString() {
      return this.comparable.toString();
   }
}
