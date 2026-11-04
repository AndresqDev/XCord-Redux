package com.google.gson;

import java.lang.reflect.Field;
import java.util.Locale;

public enum FieldNamingPolicy implements FieldNamingStrategy {
   IDENTITY {
      @Override
      public String translateName(Field var1) {
         return var1.getName();
      }
   },
   UPPER_CAMEL_CASE {
      @Override
      public String translateName(Field var1) {
         return upperCaseFirstLetter(var1.getName());
      }
   },
   UPPER_CAMEL_CASE_WITH_SPACES {
      @Override
      public String translateName(Field var1) {
         return upperCaseFirstLetter(separateCamelCase(var1.getName(), " "));
      }
   },
   LOWER_CASE_WITH_UNDERSCORES {
      @Override
      public String translateName(Field var1) {
         return separateCamelCase(var1.getName(), "_").toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DASHES {
      @Override
      public String translateName(Field var1) {
         return separateCamelCase(var1.getName(), "-").toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DOTS {
      @Override
      public String translateName(Field var1) {
         return separateCamelCase(var1.getName(), ".").toLowerCase(Locale.ENGLISH);
      }
   };

   private FieldNamingPolicy() {
   }

   static String separateCamelCase(String var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      for (int var4 = var0.length(); var3 < var4; var3++) {
         char var5 = var0.charAt(var3);
         if (Character.isUpperCase(var5) && var2.length() != 0) {
            var2.append(var1);
         }

         var2.append(var5);
      }

      return var2.toString();
   }

   static String upperCaseFirstLetter(String var0) {
      int var1 = 0;
      int var2 = var0.length() - 1;

      while (!Character.isLetter(var0.charAt(var1)) && var1 < var2) {
         var1++;
      }

      char var3 = var0.charAt(var1);
      if (Character.isUpperCase(var3)) {
         return var0;
      } else {
         char var4 = Character.toUpperCase(var3);
         return var1 == 0 ? var4 + var0.substring(1) : var0.substring(0, var1) + var4 + var0.substring(var1 + 1);
      }
   }
}
