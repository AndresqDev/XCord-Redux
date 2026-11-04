package org.yaml.snakeyaml.util;

public class EnumUtils {
   public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> var0, String var1) {
      for (Enum var5 : (Enum[])var0.getEnumConstants()) {
         if (var5.name().compareToIgnoreCase(var1) == 0) {
            return (T)var5;
         }
      }

      throw new IllegalArgumentException("No enum constant " + var0.getCanonicalName() + "." + var1);
   }
}
