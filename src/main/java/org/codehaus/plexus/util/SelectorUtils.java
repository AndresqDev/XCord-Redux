package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public final class SelectorUtils {
   public static final String PATTERN_HANDLER_PREFIX = "[";
   public static final String PATTERN_HANDLER_SUFFIX = "]";
   public static final String REGEX_HANDLER_PREFIX = "%regex[";
   public static final String ANT_HANDLER_PREFIX = "%ant[";
   private static SelectorUtils instance = new SelectorUtils();

   private SelectorUtils() {
   }

   public static SelectorUtils getInstance() {
      return instance;
   }

   public static boolean matchPatternStart(String var0, String var1) {
      return matchPatternStart(var0, var1, true);
   }

   public static boolean matchPatternStart(String var0, String var1, boolean var2) {
      if (isRegexPrefixedPattern(var0)) {
         return true;
      } else {
         if (isAntPrefixedPattern(var0)) {
            var0 = var0.substring("%ant[".length(), var0.length() - "]".length());
         }

         String var3 = var1.replace('\\', '/');
         return matchAntPathPatternStart(var0, var1, File.separator, var2) || matchAntPathPatternStart(var0, var3, "/", var2);
      }
   }

   static boolean isAntPrefixedPattern(String var0) {
      return var0.length() > "%ant[".length() + "]".length() + 1 && var0.startsWith("%ant[") && var0.endsWith("]");
   }

   static boolean matchAntPathPatternStart(MatchPattern var0, String var1, String var2, boolean var3) {
      return separatorPatternStartSlashMismatch(var0, var1, var2) ? false : matchAntPathPatternStart(var0.getTokenizedPathString(), var1, var2, var3);
   }

   static boolean matchAntPathPatternStart(String var0, String var1, String var2, boolean var3) {
      if (separatorPatternStartSlashMismatch(var0, var1, var2)) {
         return false;
      } else {
         String[] var4 = tokenizePathToString(var0, var2);
         return matchAntPathPatternStart(var4, var1, var2, var3);
      }
   }

   private static boolean separatorPatternStartSlashMismatch(String var0, String var1, String var2) {
      return var1.startsWith(var2) != var0.startsWith(var2);
   }

   private static boolean separatorPatternStartSlashMismatch(MatchPattern var0, String var1, String var2) {
      return var1.startsWith(var2) != var0.startsWith(var2);
   }

   static boolean matchAntPathPatternStart(String[] var0, String var1, String var2, boolean var3) {
      String[] var4 = tokenizePathToString(var1, var2);
      int var5 = 0;
      int var6 = var0.length - 1;
      int var7 = 0;

      int var8;
      for (var8 = var4.length - 1; var5 <= var6 && var7 <= var8; var7++) {
         String var9 = var0[var5];
         if (var9.equals("**")) {
            break;
         }

         if (!match(var9, var4[var7], var3)) {
            return false;
         }

         var5++;
      }

      return var7 > var8 || var5 <= var6;
   }

   public static boolean matchPath(String var0, String var1) {
      return matchPath(var0, var1, true);
   }

   public static boolean matchPath(String var0, String var1, boolean var2) {
      return matchPath(var0, var1, File.separator, var2);
   }

   public static boolean matchPath(String var0, String var1, String var2, boolean var3) {
      if (isRegexPrefixedPattern(var0)) {
         var0 = var0.substring("%regex[".length(), var0.length() - "]".length());
         return var1.matches(var0);
      } else {
         if (isAntPrefixedPattern(var0)) {
            var0 = var0.substring("%ant[".length(), var0.length() - "]".length());
         }

         return matchAntPathPattern(var0, var1, var2, var3);
      }
   }

   static boolean isRegexPrefixedPattern(String var0) {
      return var0.length() > "%regex[".length() + "]".length() + 1 && var0.startsWith("%regex[") && var0.endsWith("]");
   }

   static boolean matchAntPathPattern(MatchPattern var0, String var1, String var2, boolean var3) {
      if (separatorPatternStartSlashMismatch(var0, var1, var2)) {
         return false;
      } else {
         String[] var4 = var0.getTokenizedPathString();
         String[] var5 = tokenizePathToString(var1, var2);
         return matchAntPathPattern(var4, var5, var3);
      }
   }

   static boolean matchAntPathPattern(String var0, String var1, String var2, boolean var3) {
      if (separatorPatternStartSlashMismatch(var0, var1, var2)) {
         return false;
      } else {
         String[] var4 = tokenizePathToString(var0, var2);
         String[] var5 = tokenizePathToString(var1, var2);
         return matchAntPathPattern(var4, var5, var3);
      }
   }

   static boolean matchAntPathPattern(String[] var0, String[] var1, boolean var2) {
      int var3 = 0;
      int var4 = var0.length - 1;
      int var5 = 0;

      int var6;
      for (var6 = var1.length - 1; var3 <= var4 && var5 <= var6; var5++) {
         String var7 = var0[var3];
         if (var7.equals("**")) {
            break;
         }

         if (!match(var7, var1[var5], var2)) {
            return false;
         }

         var3++;
      }

      if (var5 > var6) {
         for (int var19 = var3; var19 <= var4; var19++) {
            if (!var0[var19].equals("**")) {
               return false;
            }
         }

         return true;
      } else if (var3 > var4) {
         return false;
      } else {
         while (var3 <= var4 && var5 <= var6) {
            String var15 = var0[var4];
            if (var15.equals("**")) {
               break;
            }

            if (!match(var15, var1[var6], var2)) {
               return false;
            }

            var4--;
            var6--;
         }

         if (var5 > var6) {
            for (int var18 = var3; var18 <= var4; var18++) {
               if (!var0[var18].equals("**")) {
                  return false;
               }
            }

            return true;
         } else {
            while (var3 != var4 && var5 <= var6) {
               int var16 = -1;

               for (int var8 = var3 + 1; var8 <= var4; var8++) {
                  if (var0[var8].equals("**")) {
                     var16 = var8;
                     break;
                  }
               }

               if (var16 == var3 + 1) {
                  var3++;
               } else {
                  int var20 = var16 - var3 - 1;
                  int var9 = var6 - var5 + 1;
                  int var10 = -1;
                  int var11 = 0;

                  label108:
                  while (true) {
                     if (var11 <= var9 - var20) {
                        for (int var12 = 0; var12 < var20; var12++) {
                           String var13 = var0[var3 + var12 + 1];
                           String var14 = var1[var5 + var11 + var12];
                           if (!match(var13, var14, var2)) {
                              var11++;
                              continue label108;
                           }
                        }

                        var10 = var5 + var11;
                     }

                     if (var10 == -1) {
                        return false;
                     }

                     var3 = var16;
                     var5 = var10 + var20;
                     break;
                  }
               }
            }

            for (int var17 = var3; var17 <= var4; var17++) {
               if (!var0[var17].equals("**")) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   static boolean matchAntPathPattern(char[][] var0, char[][] var1, boolean var2) {
      int var3 = 0;
      int var4 = var0.length - 1;
      int var5 = 0;

      int var6;
      for (var6 = var1.length - 1; var3 <= var4 && var5 <= var6; var5++) {
         char[] var7 = var0[var3];
         if (isDoubleStar(var7)) {
            break;
         }

         if (!match(var7, var1[var5], var2)) {
            return false;
         }

         var3++;
      }

      if (var5 > var6) {
         for (int var19 = var3; var19 <= var4; var19++) {
            if (!isDoubleStar(var0[var19])) {
               return false;
            }
         }

         return true;
      } else if (var3 > var4) {
         return false;
      } else {
         while (var3 <= var4 && var5 <= var6) {
            char[] var15 = var0[var4];
            if (isDoubleStar(var15)) {
               break;
            }

            if (!match(var15, var1[var6], var2)) {
               return false;
            }

            var4--;
            var6--;
         }

         if (var5 > var6) {
            for (int var18 = var3; var18 <= var4; var18++) {
               if (!isDoubleStar(var0[var18])) {
                  return false;
               }
            }

            return true;
         } else {
            while (var3 != var4 && var5 <= var6) {
               int var16 = -1;

               for (int var8 = var3 + 1; var8 <= var4; var8++) {
                  if (isDoubleStar(var0[var8])) {
                     var16 = var8;
                     break;
                  }
               }

               if (var16 == var3 + 1) {
                  var3++;
               } else {
                  int var20 = var16 - var3 - 1;
                  int var9 = var6 - var5 + 1;
                  int var10 = -1;
                  int var11 = 0;

                  label108:
                  while (true) {
                     if (var11 <= var9 - var20) {
                        for (int var12 = 0; var12 < var20; var12++) {
                           char[] var13 = var0[var3 + var12 + 1];
                           char[] var14 = var1[var5 + var11 + var12];
                           if (!match(var13, var14, var2)) {
                              var11++;
                              continue label108;
                           }
                        }

                        var10 = var5 + var11;
                     }

                     if (var10 == -1) {
                        return false;
                     }

                     var3 = var16;
                     var5 = var10 + var20;
                     break;
                  }
               }
            }

            for (int var17 = var3; var17 <= var4; var17++) {
               if (!isDoubleStar(var0[var17])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean isDoubleStar(char[] var0) {
      return var0 != null && var0.length == 2 && var0[0] == '*' && var0[1] == '*';
   }

   public static boolean match(String var0, String var1) {
      return match(var0, var1, true);
   }

   public static boolean match(String var0, String var1, boolean var2) {
      char[] var3 = var0.toCharArray();
      char[] var4 = var1.toCharArray();
      return match(var3, var4, var2);
   }

   public static boolean match(char[] var0, char[] var1, boolean var2) {
      int var3 = 0;
      int var4 = var0.length - 1;
      int var5 = 0;
      int var6 = var1.length - 1;
      boolean var8 = false;

      for (char var12 : var0) {
         if (var12 == '*') {
            var8 = true;
            break;
         }
      }

      if (!var8) {
         if (var4 != var6) {
            return false;
         } else {
            for (int var22 = 0; var22 <= var4; var22++) {
               char var17 = var0[var22];
               if (var17 != '?' && !equals(var17, var1[var22], var2)) {
                  return false;
               }
            }

            return true;
         }
      } else if (var4 == 0) {
         return true;
      } else {
         char var7;
         while ((var7 = var0[var3]) != '*' && var5 <= var6) {
            if (var7 != '?' && !equals(var7, var1[var5], var2)) {
               return false;
            }

            var3++;
            var5++;
         }

         if (var5 > var6) {
            for (int var21 = var3; var21 <= var4; var21++) {
               if (var0[var21] != '*') {
                  return false;
               }
            }

            return true;
         } else {
            while ((var7 = var0[var4]) != '*' && var5 <= var6) {
               if (var7 != '?' && !equals(var7, var1[var6], var2)) {
                  return false;
               }

               var4--;
               var6--;
            }

            if (var5 > var6) {
               for (int var20 = var3; var20 <= var4; var20++) {
                  if (var0[var20] != '*') {
                     return false;
                  }
               }

               return true;
            } else {
               while (var3 != var4 && var5 <= var6) {
                  int var18 = -1;

                  for (int var23 = var3 + 1; var23 <= var4; var23++) {
                     if (var0[var23] == '*') {
                        var18 = var23;
                        break;
                     }
                  }

                  if (var18 == var3 + 1) {
                     var3++;
                  } else {
                     int var24 = var18 - var3 - 1;
                     int var25 = var6 - var5 + 1;
                     int var26 = -1;
                     int var13 = 0;

                     label134:
                     while (true) {
                        if (var13 <= var25 - var24) {
                           for (int var14 = 0; var14 < var24; var14++) {
                              var7 = var0[var3 + var14 + 1];
                              if (var7 != '?' && !equals(var7, var1[var5 + var13 + var14], var2)) {
                                 var13++;
                                 continue label134;
                              }
                           }

                           var26 = var5 + var13;
                        }

                        if (var26 == -1) {
                           return false;
                        }

                        var3 = var18;
                        var5 = var26 + var24;
                        break;
                     }
                  }
               }

               for (int var19 = var3; var19 <= var4; var19++) {
                  if (var0[var19] != '*') {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   private static boolean equals(char var0, char var1, boolean var2) {
      return var0 == var1
         ? true
         : !var2 && (Character.toUpperCase(var0) == Character.toUpperCase(var1) || Character.toLowerCase(var0) == Character.toLowerCase(var1));
   }

   private static String[] tokenizePathToString(String var0, String var1) {
      ArrayList var2 = new ArrayList();
      StringTokenizer var3 = new StringTokenizer(var0, var1);

      while (var3.hasMoreTokens()) {
         var2.add(var3.nextToken());
      }

      return var2.toArray(new String[var2.size()]);
   }

   public static boolean isOutOfDate(File var0, File var1, int var2) {
      if (!var0.exists()) {
         return false;
      } else {
         return !var1.exists() ? true : var0.lastModified() - (long)var2 > var1.lastModified();
      }
   }

   public static String removeWhitespace(String var0) {
      StringBuilder var1 = new StringBuilder();
      if (var0 != null) {
         StringTokenizer var2 = new StringTokenizer(var0);

         while (var2.hasMoreTokens()) {
            var1.append(var2.nextToken());
         }
      }

      return var1.toString();
   }
}
