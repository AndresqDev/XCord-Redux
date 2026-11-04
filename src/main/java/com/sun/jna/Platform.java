package com.sun.jna;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Platform {
   public static final int UNSPECIFIED = -1;
   public static final int MAC = 0;
   public static final int LINUX = 1;
   public static final int WINDOWS = 2;
   public static final int SOLARIS = 3;
   public static final int FREEBSD = 4;
   public static final int OPENBSD = 5;
   public static final int WINDOWSCE = 6;
   public static final int AIX = 7;
   public static final int ANDROID = 8;
   public static final int GNU = 9;
   public static final int KFREEBSD = 10;
   public static final int NETBSD = 11;
   public static final boolean RO_FIELDS;
   public static final boolean HAS_BUFFERS;
   public static final boolean HAS_AWT;
   public static final boolean HAS_JAWT;
   public static final String MATH_LIBRARY_NAME;
   public static final String C_LIBRARY_NAME;
   public static final boolean HAS_DLL_CALLBACKS;
   public static final String RESOURCE_PREFIX;
   private static final int osType;
   public static final String ARCH;

   private Platform() {
   }

   public static final int getOSType() {
      return osType;
   }

   public static final boolean isMac() {
      return osType == 0;
   }

   public static final boolean isAndroid() {
      return osType == 8;
   }

   public static final boolean isLinux() {
      return osType == 1;
   }

   public static final boolean isAIX() {
      return osType == 7;
   }

      public static final boolean isAix() {
      return isAIX();
   }

   public static final boolean isWindowsCE() {
      return osType == 6;
   }

   public static final boolean isWindows() {
      return osType == 2 || osType == 6;
   }

   public static final boolean isSolaris() {
      return osType == 3;
   }

   public static final boolean isFreeBSD() {
      return osType == 4;
   }

   public static final boolean isOpenBSD() {
      return osType == 5;
   }

   public static final boolean isNetBSD() {
      return osType == 11;
   }

   public static final boolean isGNU() {
      return osType == 9;
   }

   public static final boolean iskFreeBSD() {
      return osType == 10;
   }

   public static final boolean isX11() {
      return !isWindows() && !isMac();
   }

   public static final boolean hasRuntimeExec() {
      return !isWindowsCE() || !"J9".equals(System.getProperty("java.vm.name"));
   }

   public static final boolean is64Bit() {
      String var0 = System.getProperty("sun.arch.data.model", System.getProperty("com.ibm.vm.bitmode"));
      if (var0 != null) {
         return "64".equals(var0);
      } else {
         return !"x86-64".equals(ARCH)
               && !"ia64".equals(ARCH)
               && !"ppc64".equals(ARCH)
               && !"ppc64le".equals(ARCH)
               && !"sparcv9".equals(ARCH)
               && !"amd64".equals(ARCH)
            ? Native.POINTER_SIZE == 8
            : true;
      }
   }

   public static final boolean isIntel() {
      return ARCH.startsWith("x86");
   }

   public static final boolean isPPC() {
      return ARCH.startsWith("ppc");
   }

   public static final boolean isARM() {
      return ARCH.startsWith("arm");
   }

   public static final boolean isSPARC() {
      return ARCH.startsWith("sparc");
   }

   static String getCanonicalArchitecture(String var0, boolean var1) {
      var0 = var0.toLowerCase().trim();
      if ("powerpc".equals(var0)) {
         var0 = "ppc";
      } else if ("powerpc64".equals(var0)) {
         var0 = "ppc64";
      } else if ("i386".equals(var0) || "i686".equals(var0)) {
         var0 = "x86";
      } else if ("x86_64".equals(var0) || "amd64".equals(var0)) {
         var0 = "x86-64";
      }

      if ("ppc64".equals(var0) && "little".equals(System.getProperty("sun.cpu.endian"))) {
         var0 = "ppc64le";
      }

      if ("arm".equals(var0) && var1) {
         var0 = "armel";
      }

      return var0;
   }

   private static boolean isSoftFloat() {
      try {
         File var0 = new File("/proc/self/exe");
         ELFAnalyser var1 = ELFAnalyser.analyse(var0.getCanonicalPath());
         return var1.isArmSoftFloat();
      } catch (IOException var2) {
         Logger.getLogger(Platform.class.getName()).log(Level.FINE, null, var2);
         return false;
      }
   }

   static String getNativeLibraryResourcePrefix() {
      String var0 = System.getProperty("jna.prefix");
      return var0 != null ? var0 : getNativeLibraryResourcePrefix(getOSType(), System.getProperty("os.arch"), System.getProperty("os.name"));
   }

   static String getNativeLibraryResourcePrefix(int var0, String var1, String var2) {
      return getNativeLibraryResourcePrefix(var0, var1, var2, isSoftFloat());
   }

   static String getNativeLibraryResourcePrefix(int var0, String var1, String var2, boolean var3) {
      var1 = getCanonicalArchitecture(var1, var3);
      String var4;
      switch (var0) {
         case 0:
            var4 = "darwin";
            break;
         case 1:
            var4 = "linux-" + var1;
            break;
         case 2:
            var4 = "win32-" + var1;
            break;
         case 3:
            var4 = "sunos-" + var1;
            break;
         case 4:
            var4 = "freebsd-" + var1;
            break;
         case 5:
            var4 = "openbsd-" + var1;
            break;
         case 6:
            var4 = "w32ce-" + var1;
            break;
         case 7:
         case 9:
         default:
            var4 = var2.toLowerCase();
            int var5 = var4.indexOf(" ");
            if (var5 != -1) {
               var4 = var4.substring(0, var5);
            }

            var4 = var4 + "-" + var1;
            break;
         case 8:
            if (var1.startsWith("arm")) {
               var1 = "arm";
            }

            var4 = "android-" + var1;
            break;
         case 10:
            var4 = "kfreebsd-" + var1;
            break;
         case 11:
            var4 = "netbsd-" + var1;
      }

      return var4;
   }

   static {
      String var0 = System.getProperty("os.name");
      if (var0.startsWith("Linux")) {
         if ("dalvik".equals(System.getProperty("java.vm.name").toLowerCase())) {
            osType = 8;
            System.setProperty("jna.nounpack", "true");
         } else {
            osType = 1;
         }
      } else if (var0.startsWith("AIX")) {
         osType = 7;
      } else if (var0.startsWith("Mac") || var0.startsWith("Darwin")) {
         osType = 0;
      } else if (var0.startsWith("Windows CE")) {
         osType = 6;
      } else if (var0.startsWith("Windows")) {
         osType = 2;
      } else if (var0.startsWith("Solaris") || var0.startsWith("SunOS")) {
         osType = 3;
      } else if (var0.startsWith("FreeBSD")) {
         osType = 4;
      } else if (var0.startsWith("OpenBSD")) {
         osType = 5;
      } else if (var0.equalsIgnoreCase("gnu")) {
         osType = 9;
      } else if (var0.equalsIgnoreCase("gnu/kfreebsd")) {
         osType = 10;
      } else if (var0.equalsIgnoreCase("netbsd")) {
         osType = 11;
      } else {
         osType = -1;
      }

      boolean var1 = false;

      try {
         Class.forName("java.nio.Buffer");
         var1 = true;
      } catch (ClassNotFoundException var3) {
      }

      HAS_AWT = osType != 6 && osType != 8 && osType != 7;
      HAS_JAWT = HAS_AWT && osType != 0;
      HAS_BUFFERS = var1;
      RO_FIELDS = osType != 6;
      C_LIBRARY_NAME = osType == 2 ? "msvcrt" : (osType == 6 ? "coredll" : "c");
      MATH_LIBRARY_NAME = osType == 2 ? "msvcrt" : (osType == 6 ? "coredll" : "m");
      HAS_DLL_CALLBACKS = osType == 2;
      ARCH = getCanonicalArchitecture(System.getProperty("os.arch"), isSoftFloat());
      RESOURCE_PREFIX = getNativeLibraryResourcePrefix();
   }
}
