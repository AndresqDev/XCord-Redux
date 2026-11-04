package org.codehaus.plexus.util;

import java.io.File;
import java.util.StringTokenizer;

public class PathTool {
   public static final String getRelativePath(String var0, String var1) {
      var0 = uppercaseDrive(var0);
      var1 = uppercaseDrive(var1);
      if (var0 != null && var0.length() != 0 && var1 != null && var1.length() != 0 && var1.startsWith(var0)) {
         String var2 = determineSeparator(var1);
         var0 = StringUtils.chompLast(var0, var2);
         var1 = StringUtils.chompLast(var1, var2);
         String var3 = var1.substring(var0.length());
         return determineRelativePath(var3, var2);
      } else {
         return "";
      }
   }

   public static final String getRelativePath(String var0) {
      var0 = uppercaseDrive(var0);
      if (var0 != null && var0.length() != 0) {
         String var1 = determineSeparator(var0);
         var0 = StringUtils.chompLast(var0, var1);
         if (!var0.startsWith(var1)) {
            var0 = var1 + var0;
         }

         return determineRelativePath(var0, var1);
      } else {
         return "";
      }
   }

   public static final String getDirectoryComponent(String var0) {
      if (var0 != null && var0.length() != 0) {
         String var1 = determineSeparator(var0);
         String var2 = StringUtils.chomp(var0, var1);
         return var0.equals(var2) ? "." : var2;
      } else {
         return "";
      }
   }

   public static final String calculateLink(String var0, String var1) {
      if (var0 == null) {
         var0 = "";
      }

      if (var1 == null) {
         var1 = "";
      }

      if (var0.startsWith("/site/")) {
         return var0.substring(5);
      } else if (var0.startsWith("/absolute/")) {
         return var0.substring(10);
      } else if (var0.contains(":")) {
         return var0;
      } else if (StringUtils.equals(var1, ".")) {
         return var0.startsWith("/") ? var0.substring(1) : var0;
      } else if (var1.endsWith("/") && var0.startsWith("/")) {
         return var1 + "." + var0.substring(1);
      } else {
         return !var1.endsWith("/") && !var0.startsWith("/") ? var1 + "/" + var0 : var1 + var0;
      }
   }

   public static final String getRelativeWebPath(String var0, String var1) {
      if (!StringUtils.isEmpty(var0) && !StringUtils.isEmpty(var1)) {
         String var2 = buildRelativePath(var1, var0, '/');
         return var1.endsWith("/") && !var2.endsWith("/") ? var2 + "/" : var2;
      } else {
         return "";
      }
   }

   public static final String getRelativeFilePath(String var0, String var1) {
      if (!StringUtils.isEmpty(var0) && !StringUtils.isEmpty(var1)) {
         String var2 = new File(var0).getPath();
         String var3 = new File(var1).getPath();
         if (var3.matches("^\\[a-zA-Z]:")) {
            var3 = var3.substring(1);
         }

         if (var2.matches("^\\[a-zA-Z]:")) {
            var2 = var2.substring(1);
         }

         if (var2.startsWith(":", 1)) {
            var2 = Character.toLowerCase(var2.charAt(0)) + var2.substring(1);
         }

         if (var3.startsWith(":", 1)) {
            var3 = Character.toLowerCase(var3.charAt(0)) + var3.substring(1);
         }

         if (var3.startsWith(":", 1) && var2.startsWith(":", 1) && !var3.substring(0, 1).equals(var2.substring(0, 1))) {
            return null;
         } else if ((!var3.startsWith(":", 1) || var2.startsWith(":", 1)) && (var3.startsWith(":", 1) || !var2.startsWith(":", 1))) {
            String var4 = buildRelativePath(var3, var2, File.separatorChar);
            return var1.endsWith(File.separator) && !var4.endsWith(File.separator) ? var4 + File.separator : var4;
         } else {
            return null;
         }
      } else {
         return "";
      }
   }

   private static final String determineRelativePath(String var0, String var1) {
      if (var0.length() == 0) {
         return "";
      } else {
         int var2 = StringUtils.countMatches(var0, var1) - 1;
         if (var2 <= 0) {
            return ".";
         } else {
            StringBuilder var3 = new StringBuilder();

            for (int var4 = 0; var4 < var2; var4++) {
               var3.append("../");
            }

            return StringUtils.chop(var3.toString());
         }
      }
   }

   private static final String determineSeparator(String var0) {
      int var1 = StringUtils.countMatches(var0, "/");
      int var2 = StringUtils.countMatches(var0, "\\");
      return var1 >= var2 ? "/" : "\\";
   }

   static final String uppercaseDrive(String var0) {
      if (var0 == null) {
         return null;
      } else {
         if (var0.length() >= 2 && var0.charAt(1) == ':') {
            var0 = Character.toUpperCase(var0.charAt(0)) + var0.substring(1);
         }

         return var0;
      }
   }

   private static final String buildRelativePath(String var0, String var1, char var2) {
      StringTokenizer var3 = new StringTokenizer(var0, String.valueOf(var2));
      StringTokenizer var4 = new StringTokenizer(var1, String.valueOf(var2));
      int var5 = 0;

      while (
         var3.hasMoreTokens()
            && var4.hasMoreTokens()
            && (var2 == '\\' ? var4.nextToken().equalsIgnoreCase(var3.nextToken()) : var4.nextToken().equals(var3.nextToken()))
      ) {
         var5++;
      }

      var3 = new StringTokenizer(var0, String.valueOf(var2));
      var4 = new StringTokenizer(var1, String.valueOf(var2));

      while (var5-- > 0) {
         var4.nextToken();
         var3.nextToken();
      }

      String var6 = "";

      while (var4.hasMoreTokens()) {
         var4.nextToken();
         var6 = var6 + "..";
         if (var4.hasMoreTokens()) {
            var6 = var6 + var2;
         }
      }

      if (var6.length() != 0 && var3.hasMoreTokens()) {
         var6 = var6 + var2;
      }

      while (var3.hasMoreTokens()) {
         var6 = var6 + var3.nextToken();
         if (var3.hasMoreTokens()) {
            var6 = var6 + var2;
         }
      }

      return var6;
   }
}
