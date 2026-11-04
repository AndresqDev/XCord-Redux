package org.eclipse.aether.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;

public final class ConfigUtils {
   private ConfigUtils() {
   }

   public static Object getObject(Map<?, ?> var0, Object var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 != null) {
            return var7;
         }
      }

      return var1;
   }

   public static Object getObject(RepositorySystemSession var0, Object var1, String... var2) {
      return getObject(var0.getConfigProperties(), var1, var2);
   }

   public static String getString(Map<?, ?> var0, String var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof String) {
            return (String)var7;
         }
      }

      return var1;
   }

   public static String getString(RepositorySystemSession var0, String var1, String... var2) {
      return getString(var0.getConfigProperties(), var1, var2);
   }

   public static int getInteger(Map<?, ?> var0, int var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof Number) {
            return ((Number)var7).intValue();
         }

         try {
            return Integer.parseInt((String)var7);
         } catch (Exception var9) {
         }
      }

      return var1;
   }

   public static int getInteger(RepositorySystemSession var0, int var1, String... var2) {
      return getInteger(var0.getConfigProperties(), var1, var2);
   }

   public static long getLong(Map<?, ?> var0, long var1, String... var3) {
      for (String var7 : var3) {
         Object var8 = var0.get(var7);
         if (var8 instanceof Number) {
            return ((Number)var8).longValue();
         }

         try {
            return Long.parseLong((String)var8);
         } catch (Exception var10) {
         }
      }

      return var1;
   }

   public static long getLong(RepositorySystemSession var0, long var1, String... var3) {
      return getLong(var0.getConfigProperties(), var1, var3);
   }

   public static float getFloat(Map<?, ?> var0, float var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof Number) {
            return ((Number)var7).floatValue();
         }

         try {
            return Float.parseFloat((String)var7);
         } catch (Exception var9) {
         }
      }

      return var1;
   }

   public static float getFloat(RepositorySystemSession var0, float var1, String... var2) {
      return getFloat(var0.getConfigProperties(), var1, var2);
   }

   public static boolean getBoolean(Map<?, ?> var0, boolean var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof Boolean) {
            return (Boolean)var7;
         }

         if (var7 instanceof String) {
            return Boolean.parseBoolean((String)var7);
         }
      }

      return var1;
   }

   public static boolean getBoolean(RepositorySystemSession var0, boolean var1, String... var2) {
      return getBoolean(var0.getConfigProperties(), var1, var2);
   }

   public static List<?> getList(Map<?, ?> var0, List<?> var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof List) {
            return (List<?>)var7;
         }

         if (var7 instanceof Collection) {
            return Collections.unmodifiableList(new ArrayList((Collection)var7));
         }
      }

      return var1;
   }

   public static List<?> getList(RepositorySystemSession var0, List<?> var1, String... var2) {
      return getList(var0.getConfigProperties(), var1, var2);
   }

   public static Map<?, ?> getMap(Map<?, ?> var0, Map<?, ?> var1, String... var2) {
      for (String var6 : var2) {
         Object var7 = var0.get(var6);
         if (var7 instanceof Map) {
            return (Map<?, ?>)var7;
         }
      }

      return var1;
   }

   public static Map<?, ?> getMap(RepositorySystemSession var0, Map<?, ?> var1, String... var2) {
      return getMap(var0.getConfigProperties(), var1, var2);
   }
}
