package org.apache.commons.lang3;

import java.util.regex.Pattern;

public class RegExUtils {
   public static String removeAll(String var0, Pattern var1) {
      return replaceAll(var0, var1, "");
   }

   public static String removeAll(String var0, String var1) {
      return replaceAll(var0, var1, "");
   }

   public static String removeFirst(String var0, Pattern var1) {
      return replaceFirst(var0, var1, "");
   }

   public static String removeFirst(String var0, String var1) {
      return replaceFirst(var0, var1, "");
   }

   public static String removePattern(String var0, String var1) {
      return replacePattern(var0, var1, "");
   }

   public static String replaceAll(String var0, Pattern var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var1.matcher(var0).replaceAll(var2) : var0;
   }

   public static String replaceAll(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var0.replaceAll(var1, var2) : var0;
   }

   public static String replaceFirst(String var0, Pattern var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var1.matcher(var0).replaceFirst(var2) : var0;
   }

   public static String replaceFirst(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var0.replaceFirst(var1, var2) : var0;
   }

   public static String replacePattern(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? Pattern.compile(var1, 32).matcher(var0).replaceAll(var2) : var0;
   }
}
