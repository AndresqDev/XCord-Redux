package org.codehaus.plexus.interpolation.os;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Os {
   public static final String FAMILY_DOS = "dos";
   public static final String FAMILY_MAC = "mac";
   public static final String FAMILY_NETWARE = "netware";
   public static final String FAMILY_OS2 = "os/2";
   public static final String FAMILY_TANDEM = "tandem";
   public static final String FAMILY_UNIX = "unix";
   public static final String FAMILY_WINDOWS = "windows";
   public static final String FAMILY_WIN9X = "win9x";
   public static final String FAMILY_ZOS = "z/os";
   public static final String FAMILY_OS400 = "os/400";
   public static final String FAMILY_OPENVMS = "openvms";
   private static final Set<String> validFamilies = setValidFamilies();
   private static final String PATH_SEP = System.getProperty("path.separator");
   public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
   public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
   public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
   public static final String OS_FAMILY = getOsFamily();
   private String family;
   private String name;
   private String version;
   private String arch;

   public Os() {
   }

   public Os(String var1) {
      this.setFamily(var1);
   }

   private static Set<String> setValidFamilies() {
      HashSet var0 = new HashSet();
      var0.add("dos");
      var0.add("mac");
      var0.add("netware");
      var0.add("os/2");
      var0.add("tandem");
      var0.add("unix");
      var0.add("windows");
      var0.add("win9x");
      var0.add("z/os");
      var0.add("os/400");
      var0.add("openvms");
      return var0;
   }

   public void setFamily(String var1) {
      this.family = var1.toLowerCase(Locale.US);
   }

   public void setName(String var1) {
      this.name = var1.toLowerCase(Locale.US);
   }

   public void setArch(String var1) {
      this.arch = var1.toLowerCase(Locale.US);
   }

   public void setVersion(String var1) {
      this.version = var1.toLowerCase(Locale.US);
   }

   public boolean eval() throws Exception {
      return isOs(this.family, this.name, this.arch, this.version);
   }

   public static boolean isFamily(String var0) {
      return isOs(var0, null, null, null);
   }

   public static boolean isName(String var0) {
      return isOs(null, var0, null, null);
   }

   public static boolean isArch(String var0) {
      return isOs(null, null, var0, null);
   }

   public static boolean isVersion(String var0) {
      return isOs(null, null, null, var0);
   }

   public static boolean isOs(String var0, String var1, String var2, String var3) {
      boolean var4 = false;
      if (var0 != null || var1 != null || var2 != null || var3 != null) {
         boolean var5 = true;
         boolean var6 = true;
         boolean var7 = true;
         boolean var8 = true;
         if (var0 != null) {
            if (var0.equalsIgnoreCase("windows")) {
               var5 = OS_NAME.contains("windows");
            } else if (var0.equalsIgnoreCase("os/2")) {
               var5 = OS_NAME.contains("os/2");
            } else if (var0.equalsIgnoreCase("netware")) {
               var5 = OS_NAME.contains("netware");
            } else if (var0.equalsIgnoreCase("dos")) {
               var5 = PATH_SEP.equals(";") && !isFamily("netware");
            } else if (var0.equalsIgnoreCase("mac")) {
               var5 = OS_NAME.contains("mac");
            } else if (var0.equalsIgnoreCase("tandem")) {
               var5 = OS_NAME.contains("nonstop_kernel");
            } else if (var0.equalsIgnoreCase("unix")) {
               var5 = PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || OS_NAME.endsWith("x"));
            } else if (var0.equalsIgnoreCase("win9x")) {
               var5 = isFamily("windows") && (OS_NAME.contains("95") || OS_NAME.contains("98") || OS_NAME.contains("me") || OS_NAME.contains("ce"));
            } else if (!var0.equalsIgnoreCase("z/os")) {
               if (var0.equalsIgnoreCase("os/400")) {
                  var5 = OS_NAME.contains("os/400");
               } else if (var0.equalsIgnoreCase("openvms")) {
                  var5 = OS_NAME.contains("openvms");
               } else {
                  var5 = OS_NAME.contains(var0.toLowerCase(Locale.US));
               }
            } else {
               var5 = OS_NAME.contains("z/os") || OS_NAME.contains("os/390");
            }
         }

         if (var1 != null) {
            var6 = var1.toLowerCase(Locale.US).equals(OS_NAME);
         }

         if (var2 != null) {
            var7 = var2.toLowerCase(Locale.US).equals(OS_ARCH);
         }

         if (var3 != null) {
            var8 = var3.toLowerCase(Locale.US).equals(OS_VERSION);
         }

         var4 = var5 && var6 && var7 && var8;
      }

      return var4;
   }

   private static String getOsFamily() {
      Object var0 = null;
      if (!validFamilies.isEmpty()) {
         var0 = validFamilies;
      } else {
         var0 = setValidFamilies();
      }

      for (String var2 : var0) {
         if (isFamily(var2)) {
            return var2;
         }
      }

      return null;
   }

   public static boolean isValidFamily(String var0) {
      return validFamilies.contains(var0);
   }

   public static Set<String> getValidFamilies() {
      return new HashSet<>(validFamilies);
   }
}
