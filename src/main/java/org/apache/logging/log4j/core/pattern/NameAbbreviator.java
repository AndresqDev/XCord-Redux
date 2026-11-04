package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public abstract class NameAbbreviator {
   private static final NameAbbreviator DEFAULT = new NameAbbreviator.NOPAbbreviator();

   public static NameAbbreviator getAbbreviator(String var0) {
      if (var0.length() > 0) {
         String var1 = var0.trim();
         if (var1.isEmpty()) {
            return DEFAULT;
         } else {
            boolean var2;
            String var3;
            if (var1.length() > 1 && var1.charAt(0) == '-') {
               var2 = true;
               var3 = var1.substring(1);
            } else {
               var2 = false;
               var3 = var1;
            }

            int var4 = 0;

            while (var4 < var3.length() && var3.charAt(var4) >= '0' && var3.charAt(var4) <= '9') {
               var4++;
            }

            if (var4 == var3.length()) {
               return new NameAbbreviator.MaxElementAbbreviator(
                  Integer.parseInt(var3), var2 ? NameAbbreviator.MaxElementAbbreviator.Strategy.DROP : NameAbbreviator.MaxElementAbbreviator.Strategy.RETAIN
               );
            } else {
               ArrayList var5 = new ArrayList(5);

               for (int var10 = 0; var10 < var1.length() && var10 >= 0; var10++) {
                  int var9 = var10;
                  int var7;
                  if (var1.charAt(var10) == '*') {
                     var7 = Integer.MAX_VALUE;
                     var9 = var10 + 1;
                  } else if (var1.charAt(var10) >= '0' && var1.charAt(var10) <= '9') {
                     var7 = var1.charAt(var10) - '0';
                     var9 = var10 + 1;
                  } else {
                     var7 = 0;
                  }

                  char var6 = 0;
                  if (var9 < var1.length()) {
                     var6 = var1.charAt(var9);
                     if (var6 == '.') {
                        var6 = 0;
                     }
                  }

                  var5.add(new NameAbbreviator.PatternAbbreviatorFragment(var7, var6));
                  var10 = var1.indexOf(46, var10);
                  if (var10 == -1) {
                     break;
                  }
               }

               return new NameAbbreviator.PatternAbbreviator(var5);
            }
         }
      } else {
         return DEFAULT;
      }
   }

   public static NameAbbreviator getDefaultAbbreviator() {
      return DEFAULT;
   }

   public abstract void abbreviate(String var1, StringBuilder var2);

   private static class MaxElementAbbreviator extends NameAbbreviator {
      private final int count;
      private final NameAbbreviator.MaxElementAbbreviator.Strategy strategy;

      public MaxElementAbbreviator(int var1, NameAbbreviator.MaxElementAbbreviator.Strategy var2) {
         this.count = Math.max(var1, var2.minCount);
         this.strategy = var2;
      }

      @Override
      public void abbreviate(String var1, StringBuilder var2) {
         this.strategy.abbreviate(this.count, var1, var2);
      }

      private static enum Strategy {
         DROP(0) {
            @Override
            void abbreviate(int var1, String var2, StringBuilder var3) {
               int var4 = 0;

               for (int var6 = 0; var6 < var1; var6++) {
                  int var5 = var2.indexOf(46, var4);
                  if (var5 == -1) {
                     var3.append(var2);
                     return;
                  }

                  var4 = var5 + 1;
               }

               var3.append(var2, var4, var2.length());
            }
         },
         RETAIN(1) {
            @Override
            void abbreviate(int var1, String var2, StringBuilder var3) {
               int var4 = var2.length() - 1;

               for (int var5 = var1; var5 > 0; var5--) {
                  var4 = var2.lastIndexOf(46, var4 - 1);
                  if (var4 == -1) {
                     var3.append(var2);
                     return;
                  }
               }

               var3.append(var2, var4 + 1, var2.length());
            }
         };

         final int minCount;

         private Strategy(int var3) {
            this.minCount = var3;
         }

         abstract void abbreviate(int var1, String var2, StringBuilder var3);
      }
   }

   private static class NOPAbbreviator extends NameAbbreviator {
      public NOPAbbreviator() {
      }

      @Override
      public void abbreviate(String var1, StringBuilder var2) {
         var2.append(var1);
      }
   }

   private static final class PatternAbbreviator extends NameAbbreviator {
      private final NameAbbreviator.PatternAbbreviatorFragment[] fragments;

      PatternAbbreviator(List<NameAbbreviator.PatternAbbreviatorFragment> var1) {
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("fragments must have at least one element");
         } else {
            this.fragments = var1.toArray(new NameAbbreviator.PatternAbbreviatorFragment[0]);
         }
      }

      @Override
      public void abbreviate(String var1, StringBuilder var2) {
         int var3 = 0;
         int var4 = 0;
         int var5 = var1.length();

         while (var3 >= 0 && var3 < var5) {
            var3 = this.fragment(var4++).abbreviate(var1, var3, var2);
         }
      }

      NameAbbreviator.PatternAbbreviatorFragment fragment(int var1) {
         return this.fragments[Math.min(var1, this.fragments.length - 1)];
      }
   }

   private static final class PatternAbbreviatorFragment {
      private final int charCount;
      private final char ellipsis;

      PatternAbbreviatorFragment(int var1, char var2) {
         this.charCount = var1;
         this.ellipsis = var2;
      }

      int abbreviate(String var1, int var2, StringBuilder var3) {
         int var4 = var1.indexOf(46, var2);
         if (var4 < 0) {
            var3.append(var1, var2, var1.length());
            return var4;
         } else {
            if (var4 - var2 > this.charCount) {
               var3.append(var1, var2, var2 + this.charCount);
               if (this.ellipsis != 0) {
                  var3.append(this.ellipsis);
               }

               var3.append('.');
            } else {
               var3.append(var1, var2, var4 + 1);
            }

            return var4 + 1;
         }
      }
   }
}
