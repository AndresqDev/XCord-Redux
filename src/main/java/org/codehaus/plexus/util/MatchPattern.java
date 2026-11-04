package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MatchPattern {
   private final String source;
   private final String regexPattern;
   private final String separator;
   private final String[] tokenized;
   private final char[][] tokenizedChar;

   private MatchPattern(String var1, String var2) {
      this.regexPattern = SelectorUtils.isRegexPrefixedPattern(var1) ? var1.substring("%regex[".length(), var1.length() - "]".length()) : null;
      this.source = SelectorUtils.isAntPrefixedPattern(var1) ? var1.substring("%ant[".length(), var1.length() - "]".length()) : var1;
      this.separator = var2;
      this.tokenized = tokenizePathToString(this.source, var2);
      this.tokenizedChar = new char[this.tokenized.length][];

      for (int var3 = 0; var3 < this.tokenized.length; var3++) {
         this.tokenizedChar[var3] = this.tokenized[var3].toCharArray();
      }
   }

   public boolean matchPath(String var1, boolean var2) {
      return this.regexPattern != null ? var1.matches(this.regexPattern) : SelectorUtils.matchAntPathPattern(this, var1, this.separator, var2);
   }

   boolean matchPath(String var1, char[][] var2, boolean var3) {
      return this.regexPattern != null ? var1.matches(this.regexPattern) : SelectorUtils.matchAntPathPattern(this.getTokenizedPathChars(), var2, var3);
   }

   public boolean matchPatternStart(String var1, boolean var2) {
      if (this.regexPattern != null) {
         return true;
      } else {
         String var3 = var1.replace('\\', '/');
         return SelectorUtils.matchAntPathPatternStart(this, var1, File.separator, var2) || SelectorUtils.matchAntPathPatternStart(this, var3, "/", var2);
      }
   }

   public String[] getTokenizedPathString() {
      return this.tokenized;
   }

   public char[][] getTokenizedPathChars() {
      return this.tokenizedChar;
   }

   public boolean startsWith(String var1) {
      return this.source.startsWith(var1);
   }

   static String[] tokenizePathToString(String var0, String var1) {
      ArrayList var2 = new ArrayList();
      StringTokenizer var3 = new StringTokenizer(var0, var1);

      while (var3.hasMoreTokens()) {
         var2.add(var3.nextToken());
      }

      return var2.toArray(new String[var2.size()]);
   }

   public static MatchPattern fromString(String var0) {
      return new MatchPattern(var0, File.separator);
   }
}
