package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocaleUtils {
   private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry = new ConcurrentHashMap<>();
   private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage = new ConcurrentHashMap<>();

   public static Locale toLocale(String var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isEmpty()) {
         return new Locale("", "");
      } else if (var0.contains("#")) {
         throw new IllegalArgumentException("Invalid locale format: " + var0);
      } else {
         int var1 = var0.length();
         if (var1 < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + var0);
         } else {
            char var2 = var0.charAt(0);
            if (var2 == '_') {
               if (var1 < 3) {
                  throw new IllegalArgumentException("Invalid locale format: " + var0);
               } else {
                  char var3 = var0.charAt(1);
                  char var4 = var0.charAt(2);
                  if (!Character.isUpperCase(var3) || !Character.isUpperCase(var4)) {
                     throw new IllegalArgumentException("Invalid locale format: " + var0);
                  } else if (var1 == 3) {
                     return new Locale("", var0.substring(1, 3));
                  } else if (var1 < 5) {
                     throw new IllegalArgumentException("Invalid locale format: " + var0);
                  } else if (var0.charAt(3) != '_') {
                     throw new IllegalArgumentException("Invalid locale format: " + var0);
                  } else {
                     return new Locale("", var0.substring(1, 3), var0.substring(4));
                  }
               }
            } else {
               return parseLocale(var0);
            }
         }
      }
   }

   private static Locale parseLocale(String var0) {
      if (isISO639LanguageCode(var0)) {
         return new Locale(var0);
      } else {
         String[] var1 = var0.split("_", -1);
         String var2 = var1[0];
         if (var1.length == 2) {
            String var3 = var1[1];
            if (isISO639LanguageCode(var2) && isISO3166CountryCode(var3) || isNumericAreaCode(var3)) {
               return new Locale(var2, var3);
            }
         } else if (var1.length == 3) {
            String var5 = var1[1];
            String var4 = var1[2];
            if (isISO639LanguageCode(var2) && (var5.isEmpty() || isISO3166CountryCode(var5) || isNumericAreaCode(var5)) && !var4.isEmpty()) {
               return new Locale(var2, var5, var4);
            }
         }

         throw new IllegalArgumentException("Invalid locale format: " + var0);
      }
   }

   private static boolean isISO639LanguageCode(String var0) {
      return StringUtils.isAllLowerCase(var0) && (var0.length() == 2 || var0.length() == 3);
   }

   private static boolean isISO3166CountryCode(String var0) {
      return StringUtils.isAllUpperCase(var0) && var0.length() == 2;
   }

   private static boolean isNumericAreaCode(String var0) {
      return StringUtils.isNumeric(var0) && var0.length() == 3;
   }

   public static List<Locale> localeLookupList(Locale var0) {
      return localeLookupList(var0, var0);
   }

   public static List<Locale> localeLookupList(Locale var0, Locale var1) {
      ArrayList var2 = new ArrayList(4);
      if (var0 != null) {
         var2.add(var0);
         if (!var0.getVariant().isEmpty()) {
            var2.add(new Locale(var0.getLanguage(), var0.getCountry()));
         }

         if (!var0.getCountry().isEmpty()) {
            var2.add(new Locale(var0.getLanguage(), ""));
         }

         if (!var2.contains(var1)) {
            var2.add(var1);
         }
      }

      return Collections.unmodifiableList(var2);
   }

   public static List<Locale> availableLocaleList() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_LIST;
   }

   public static Set<Locale> availableLocaleSet() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_SET;
   }

   public static boolean isAvailableLocale(Locale var0) {
      return availableLocaleList().contains(var0);
   }

   public static List<Locale> languagesByCountry(String var0) {
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         List var1 = cLanguagesByCountry.get(var0);
         if (var1 == null) {
            ArrayList var5 = new ArrayList();

            for (Locale var4 : availableLocaleList()) {
               if (var0.equals(var4.getCountry()) && var4.getVariant().isEmpty()) {
                  var5.add(var4);
               }
            }

            var1 = Collections.unmodifiableList(var5);
            cLanguagesByCountry.putIfAbsent(var0, var1);
            var1 = cLanguagesByCountry.get(var0);
         }

         return var1;
      }
   }

   public static List<Locale> countriesByLanguage(String var0) {
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         List var1 = cCountriesByLanguage.get(var0);
         if (var1 == null) {
            ArrayList var5 = new ArrayList();

            for (Locale var4 : availableLocaleList()) {
               if (var0.equals(var4.getLanguage()) && !var4.getCountry().isEmpty() && var4.getVariant().isEmpty()) {
                  var5.add(var4);
               }
            }

            var1 = Collections.unmodifiableList(var5);
            cCountriesByLanguage.putIfAbsent(var0, var1);
            var1 = cCountriesByLanguage.get(var0);
         }

         return var1;
      }
   }

   static class SyncAvoid {
      private static final List<Locale> AVAILABLE_LOCALE_LIST;
      private static final Set<Locale> AVAILABLE_LOCALE_SET;

      static {
         ArrayList var0 = new ArrayList<>(Arrays.asList(Locale.getAvailableLocales()));
         AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(var0);
         AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet<>(var0));
      }
   }
}
