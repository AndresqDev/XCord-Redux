package org.eclipse.sisu.space;

import java.util.Locale;
import java.util.Map;

public enum BeanScanning {
   ON,
   OFF,
   CACHE,
   INDEX,
   GLOBAL_INDEX;

   public static BeanScanning select(Map<?, ?> var0) throws IllegalArgumentException {
      String var1 = (String)var0.get(BeanScanning.class.getName());
      return var1 != null && var1.trim().length() != 0 ? Enum.valueOf(BeanScanning.class, var1.toUpperCase(Locale.ENGLISH)) : ON;
   }
}
