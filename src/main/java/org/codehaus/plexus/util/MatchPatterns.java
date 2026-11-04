package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;

public class MatchPatterns {
   private final MatchPattern[] patterns;

   private MatchPatterns(MatchPattern[] var1) {
      this.patterns = var1;
   }

   public boolean matches(String var1, boolean var2) {
      String[] var3 = MatchPattern.tokenizePathToString(var1, File.separator);
      return this.matches(var1, var3, var2);
   }

   public boolean matches(String var1, String[] var2, boolean var3) {
      char[][] var4 = new char[var2.length][];

      for (int var5 = 0; var5 < var2.length; var5++) {
         var4[var5] = var2[var5].toCharArray();
      }

      for (MatchPattern var8 : this.patterns) {
         if (var8.matchPath(var1, var4, var3)) {
            return true;
         }
      }

      return false;
   }

   public boolean matchesPatternStart(String var1, boolean var2) {
      for (MatchPattern var6 : this.patterns) {
         if (var6.matchPatternStart(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   public static MatchPatterns from(String... var0) {
      int var1 = var0.length;
      MatchPattern[] var2 = new MatchPattern[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = MatchPattern.fromString(var0[var3]);
      }

      return new MatchPatterns(var2);
   }

   public static MatchPatterns from(Iterable<String> var0) {
      return new MatchPatterns(getMatchPatterns(var0));
   }

   private static MatchPattern[] getMatchPatterns(Iterable<String> var0) {
      ArrayList var1 = new ArrayList();

      for (String var3 : var0) {
         var1.add(MatchPattern.fromString(var3));
      }

      return var1.toArray(new MatchPattern[var1.size()]);
   }
}
