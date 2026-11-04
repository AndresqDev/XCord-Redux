package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PropertySource {
   int getPriority();

   default void forEach(BiConsumer<String, String> var1) {
   }

   default CharSequence getNormalForm(Iterable<? extends CharSequence> var1) {
      return null;
   }

   default String getProperty(String var1) {
      return null;
   }

   default boolean containsProperty(String var1) {
      return false;
   }

   public static class Comparator implements Serializable, java.util.Comparator<PropertySource> {
      private static final long serialVersionUID = 1L;

      public int compare(PropertySource var1, PropertySource var2) {
         return Integer.compare(Objects.requireNonNull(var1).getPriority(), Objects.requireNonNull(var2).getPriority());
      }
   }

   public static final class Util {
      private static final String PREFIXES = "(?i:^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)?";
      private static final Pattern PROPERTY_TOKENIZER = Pattern.compile(
         "(?i:^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)?([A-Z]*[a-z0-9]+|[A-Z0-9]+)[-._/]?"
      );
      private static final Map<CharSequence, List<CharSequence>> CACHE = new ConcurrentHashMap<>();

      public static List<CharSequence> tokenize(CharSequence var0) {
         if (CACHE.containsKey(var0)) {
            return CACHE.get(var0);
         } else {
            ArrayList var1 = new ArrayList();
            Matcher var2 = PROPERTY_TOKENIZER.matcher(var0);

            while (var2.find()) {
               var1.add(var2.group(1).toLowerCase());
            }

            CACHE.put(var0, var1);
            return var1;
         }
      }

      public static CharSequence joinAsCamelCase(Iterable<? extends CharSequence> var0) {
         StringBuilder var1 = new StringBuilder();
         boolean var2 = true;

         for (CharSequence var4 : var0) {
            if (var2) {
               var1.append(var4);
            } else {
               var1.append(Character.toUpperCase(var4.charAt(0)));
               if (var4.length() > 1) {
                  var1.append(var4.subSequence(1, var4.length()));
               }
            }

            var2 = false;
         }

         return var1.toString();
      }

      private Util() {
      }
   }
}
