package org.eclipse.sisu.space;

import java.util.regex.Pattern;

enum GlobberStrategy {
   ANYTHING {
      @Override
      final Object compile(String var1) {
         return null;
      }

      @Override
      final boolean matches(Object var1, String var2) {
         return true;
      }
   },
   SUFFIX {
      @Override
      final Object compile(String var1) {
         return var1.substring(1);
      }

      @Override
      final boolean matches(Object var1, String var2) {
         return var2.endsWith((String)var1);
      }
   },
   PREFIX {
      @Override
      final Object compile(String var1) {
         return var1.substring(0, var1.length() - 1);
      }

      @Override
      final boolean matches(Object var1, String var2) {
         return basename(var2).startsWith((String)var1);
      }
   },
   EXACT {
      @Override
      final Object compile(String var1) {
         return var1;
      }

      @Override
      final boolean matches(Object var1, String var2) {
         return var1.equals(basename(var2));
      }
   },
   PATTERN {
      @Override
      final Object compile(String var1) {
         return Pattern.compile("\\Q" + var1.replaceAll("\\*+", "\\\\E.*\\\\Q") + "\\E");
      }

      @Override
      final boolean matches(Object var1, String var2) {
         return ((Pattern)var1).matcher(basename(var2)).matches();
      }
   };

   private GlobberStrategy() {
   }

   static final GlobberStrategy selectFor(String var0) {
      if (var0 != null && !"*".equals(var0)) {
         int var1 = var0.indexOf(42);
         if (var1 < 0) {
            return EXACT;
         } else {
            int var2 = var0.lastIndexOf(42);
            if (var1 == var2) {
               if (var1 == 0) {
                  return SUFFIX;
               }

               if (var2 == var0.length() - 1) {
                  return PREFIX;
               }
            }

            return PATTERN;
         }
      } else {
         return ANYTHING;
      }
   }

   abstract Object compile(String var1);

   abstract boolean matches(Object var1, String var2);

   static final String basename(String var0) {
      return var0.substring(1 + var0.lastIndexOf(47));
   }
}
