package com.sun.jna;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class NativeLibrary {
   private long handle;
   private final String libraryName;
   private final String libraryPath;
   private final Map<String, Function> functions = new HashMap<>();
   final int callFlags;
   private String encoding;
   final Map<String, ?> options;
   private static final Map<String, Reference<NativeLibrary>> libraries = new HashMap<>();
   private static final Map<String, List<String>> searchPaths = Collections.synchronizedMap(new HashMap<>());
   private static final List<String> librarySearchPath = new ArrayList<>();
   private static final int DEFAULT_OPEN_OPTIONS = -1;

   private static String functionKey(String var0, int var1, String var2) {
      return var0 + "|" + var1 + "|" + var2;
   }

   private NativeLibrary(String var1, String var2, long var3, Map<String, ?> var5) {
      this.libraryName = this.getLibraryName(var1);
      this.libraryPath = var2;
      this.handle = var3;
      Object var6 = var5.get("calling-convention");
      int var7 = var6 instanceof Number ? ((Number)var6).intValue() : 0;
      this.callFlags = var7;
      this.options = var5;
      this.encoding = (String)var5.get("string-encoding");
      if (this.encoding == null) {
         this.encoding = Native.getDefaultStringEncoding();
      }

      if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
         synchronized (this.functions) {
            Function var9 = new Function(this, "GetLastError", 63, this.encoding) {
               @Override
               Object invoke(Object[] var1, Class<?> var2, boolean var3, int var4) {
                  return Native.getLastError();
               }

               @Override
               Object invoke(Method var1, Class<?>[] var2, Class<?> var3, Object[] var4, Map<String, ?> var5) {
                  return Native.getLastError();
               }
            };
            this.functions.put(functionKey("GetLastError", this.callFlags, this.encoding), var9);
         }
      }
   }

   private static int openFlags(Map<String, ?> var0) {
      Object var1 = var0.get("open-flags");
      return var1 instanceof Number ? ((Number)var1).intValue() : -1;
   }

   private static NativeLibrary loadLibrary(String var0, Map<String, ?> var1) {
      if (Native.DEBUG_LOAD) {
         System.out.println("Looking for library '" + var0 + "'");
      }

      boolean var2 = new File(var0).isAbsolute();
      ArrayList var3 = new ArrayList();
      int var4 = openFlags(var1);
      String var5 = Native.getWebStartLibraryPath(var0);
      if (var5 != null) {
         if (Native.DEBUG_LOAD) {
            System.out.println("Adding web start path " + var5);
         }

         var3.add(var5);
      }

      List var6 = searchPaths.get(var0);
      if (var6 != null) {
         synchronized (var6) {
            var3.addAll(0, var6);
         }
      }

      if (Native.DEBUG_LOAD) {
         System.out.println("Adding paths from jna.library.path: " + System.getProperty("jna.library.path"));
      }

      var3.addAll(initPaths("jna.library.path"));
      String var7 = findLibraryPath(var0, var3);
      long var8 = 0L;

      try {
         if (Native.DEBUG_LOAD) {
            System.out.println("Trying " + var7);
         }

         var8 = Native.open(var7, var4);
      } catch (UnsatisfiedLinkError var30) {
         if (Native.DEBUG_LOAD) {
            System.out.println("Adding system paths: " + librarySearchPath);
         }

         var3.addAll(librarySearchPath);
      }

      try {
         if (var8 == 0L) {
            var7 = findLibraryPath(var0, var3);
            if (Native.DEBUG_LOAD) {
               System.out.println("Trying " + var7);
            }

            var8 = Native.open(var7, var4);
            if (var8 == 0L) {
               throw new UnsatisfiedLinkError("Failed to load library '" + var0 + "'");
            }
         }
      } catch (UnsatisfiedLinkError var29) {
         UnsatisfiedLinkError var10 = var29;
         if (Platform.isAndroid()) {
            try {
               if (Native.DEBUG_LOAD) {
                  System.out.println("Preload (via System.loadLibrary) " + var0);
               }

               System.loadLibrary(var0);
               var8 = Native.open(var7, var4);
            } catch (UnsatisfiedLinkError var25) {
               var10 = var25;
            }
         } else if (!Platform.isLinux() && !Platform.isFreeBSD()) {
            if (Platform.isMac() && !var0.endsWith(".dylib")) {
               if (Native.DEBUG_LOAD) {
                  System.out.println("Looking for matching frameworks");
               }

               var7 = matchFramework(var0);
               if (var7 != null) {
                  try {
                     if (Native.DEBUG_LOAD) {
                        System.out.println("Trying " + var7);
                     }

                     var8 = Native.open(var7, var4);
                  } catch (UnsatisfiedLinkError var23) {
                     var10 = var23;
                  }
               }
            } else if (Platform.isWindows() && !var2) {
               if (Native.DEBUG_LOAD) {
                  System.out.println("Looking for lib- prefix");
               }

               var7 = findLibraryPath("lib" + var0, var3);
               if (var7 != null) {
                  if (Native.DEBUG_LOAD) {
                     System.out.println("Trying " + var7);
                  }

                  try {
                     var8 = Native.open(var7, var4);
                  } catch (UnsatisfiedLinkError var22) {
                     var10 = var22;
                  }
               }
            }
         } else {
            if (Native.DEBUG_LOAD) {
               System.out.println("Looking for version variants");
            }

            var7 = matchLibrary(var0, var3);
            if (var7 != null) {
               if (Native.DEBUG_LOAD) {
                  System.out.println("Trying " + var7);
               }

               try {
                  var8 = Native.open(var7, var4);
               } catch (UnsatisfiedLinkError var24) {
                  var10 = var24;
               }
            }
         }

         if (var8 == 0L) {
            try {
               File var11 = Native.extractFromResourcePath(var0, (ClassLoader)var1.get("classloader"));

               try {
                  var8 = Native.open(var11.getAbsolutePath(), var4);
                  var7 = var11.getAbsolutePath();
               } finally {
                  if (Native.isUnpacked(var11)) {
                     Native.deleteLibrary(var11);
                  }
               }
            } catch (IOException var28) {
               var10 = new UnsatisfiedLinkError(var28.getMessage());
            }
         }

         if (var8 == 0L) {
            throw new UnsatisfiedLinkError("Unable to load library '" + var0 + "': " + var10.getMessage());
         }
      }

      if (Native.DEBUG_LOAD) {
         System.out.println("Found library '" + var0 + "' at " + var7);
      }

      return new NativeLibrary(var0, var7, var8, var1);
   }

   static String matchFramework(String var0) {
      File var1 = new File(var0);
      if (var1.isAbsolute()) {
         if (var0.indexOf(".framework") != -1 && var1.exists()) {
            return var1.getAbsolutePath();
         }

         var1 = new File(new File(var1.getParentFile(), var1.getName() + ".framework"), var1.getName());
         if (var1.exists()) {
            return var1.getAbsolutePath();
         }
      } else {
         String[] var2 = new String[]{System.getProperty("user.home"), "", "/System"};
         String var3 = var0.indexOf(".framework") == -1 ? var0 + ".framework/" + var0 : var0;

         for (int var4 = 0; var4 < var2.length; var4++) {
            String var5 = var2[var4] + "/Library/Frameworks/" + var3;
            if (new File(var5).exists()) {
               return var5;
            }
         }
      }

      return null;
   }

   private String getLibraryName(String var1) {
      String var2 = var1;
      String var3 = "---";
      String var4 = mapSharedLibraryName("---");
      int var5 = var4.indexOf("---");
      if (var5 > 0 && var1.startsWith(var4.substring(0, var5))) {
         var2 = var1.substring(var5);
      }

      String var6 = var4.substring(var5 + "---".length());
      int var7 = var2.indexOf(var6);
      if (var7 != -1) {
         var2 = var2.substring(0, var7);
      }

      return var2;
   }

   public static final NativeLibrary getInstance(String var0) {
      return getInstance(var0, Collections.emptyMap());
   }

   public static final NativeLibrary getInstance(String var0, ClassLoader var1) {
      return getInstance(var0, Collections.singletonMap("classloader", var1));
   }

   public static final NativeLibrary getInstance(String var0, Map<String, ?> var1) {
      HashMap var2 = new HashMap(var1);
      if (var2.get("calling-convention") == null) {
         var2.put("calling-convention", 0);
      }

      if ((Platform.isLinux() || Platform.isFreeBSD() || Platform.isAIX()) && Platform.C_LIBRARY_NAME.equals(var0)) {
         var0 = null;
      }

      synchronized (libraries) {
         Reference var4 = libraries.get(var0 + var2);
         NativeLibrary var5 = var4 != null ? (NativeLibrary)var4.get() : null;
         if (var5 == null) {
            if (var0 == null) {
               var5 = new NativeLibrary("<process>", null, Native.open(null, openFlags(var2)), var2);
            } else {
               var5 = loadLibrary((String)var0, var2);
            }

            WeakReference var9 = new WeakReference<>(var5);
            libraries.put(var5.getName() + var2, var9);
            File var6 = var5.getFile();
            if (var6 != null) {
               libraries.put(var6.getAbsolutePath() + var2, var9);
               libraries.put(var6.getName() + var2, var9);
            }
         }

         return var5;
      }
   }

   public static final synchronized NativeLibrary getProcess() {
      return getInstance(null);
   }

   public static final synchronized NativeLibrary getProcess(Map<String, ?> var0) {
      return getInstance(null, var0);
   }

   public static final void addSearchPath(String var0, String var1) {
      synchronized (searchPaths) {
         List var3 = searchPaths.get(var0);
         if (var3 == null) {
            var3 = Collections.synchronizedList(new ArrayList());
            searchPaths.put(var0, var3);
         }

         var3.add(var1);
      }
   }

   public Function getFunction(String var1) {
      return this.getFunction(var1, this.callFlags);
   }

   Function getFunction(String var1, Method var2) {
      FunctionMapper var3 = (FunctionMapper)this.options.get("function-mapper");
      if (var3 != null) {
         var1 = var3.getFunctionName(this, var2);
      }

      String var4 = System.getProperty("jna.profiler.prefix", "$$YJP$$");
      if (var1.startsWith(var4)) {
         var1 = var1.substring(var4.length());
      }

      int var5 = this.callFlags;
      Class[] var6 = var2.getExceptionTypes();

      for (int var7 = 0; var7 < var6.length; var7++) {
         if (LastErrorException.class.isAssignableFrom(var6[var7])) {
            var5 |= 64;
         }
      }

      return this.getFunction(var1, var5);
   }

   public Function getFunction(String var1, int var2) {
      return this.getFunction(var1, var2, this.encoding);
   }

   public Function getFunction(String var1, int var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("Function name may not be null");
      } else {
         synchronized (this.functions) {
            String var5 = functionKey(var1, var2, var3);
            Function var6 = this.functions.get(var5);
            if (var6 == null) {
               var6 = new Function(this, var1, var2, var3);
               this.functions.put(var5, var6);
            }

            return var6;
         }
      }
   }

   public Map<String, ?> getOptions() {
      return this.options;
   }

   public Pointer getGlobalVariableAddress(String var1) {
      try {
         return new Pointer(this.getSymbolAddress(var1));
      } catch (UnsatisfiedLinkError var3) {
         throw new UnsatisfiedLinkError("Error looking up '" + var1 + "': " + var3.getMessage());
      }
   }

   long getSymbolAddress(String var1) {
      if (this.handle == 0L) {
         throw new UnsatisfiedLinkError("Library has been unloaded");
      } else {
         return Native.findSymbol(this.handle, var1);
      }
   }

   @Override
   public String toString() {
      return "Native Library <" + this.libraryPath + "@" + this.handle + ">";
   }

   public String getName() {
      return this.libraryName;
   }

   public File getFile() {
      return this.libraryPath == null ? null : new File(this.libraryPath);
   }

   @Override
   protected void finalize() {
      this.dispose();
   }

   static void disposeAll() {
      LinkedHashSet var0;
      synchronized (libraries) {
         var0 = new LinkedHashSet<>(libraries.values());
      }

      for (Reference var2 : var0) {
         NativeLibrary var3 = (NativeLibrary)var2.get();
         if (var3 != null) {
            var3.dispose();
         }
      }
   }

   public void dispose() {
      HashSet var1 = new HashSet();
      synchronized (libraries) {
         for (Entry var4 : libraries.entrySet()) {
            Reference var5 = (Reference)var4.getValue();
            if (var5.get() == this) {
               var1.add(var4.getKey());
            }
         }

         for (String var11 : var1) {
            libraries.remove(var11);
         }
      }

      synchronized (this) {
         if (this.handle != 0L) {
            Native.close(this.handle);
            this.handle = 0L;
         }
      }
   }

   private static List<String> initPaths(String var0) {
      String var1 = System.getProperty(var0, "");
      if ("".equals(var1)) {
         return Collections.emptyList();
      } else {
         StringTokenizer var2 = new StringTokenizer(var1, File.pathSeparator);
         ArrayList var3 = new ArrayList();

         while (var2.hasMoreTokens()) {
            String var4 = var2.nextToken();
            if (!"".equals(var4)) {
               var3.add(var4);
            }
         }

         return var3;
      }
   }

   private static String findLibraryPath(String var0, List<String> var1) {
      if (new File(var0).isAbsolute()) {
         return var0;
      } else {
         String var2 = mapSharedLibraryName(var0);

         for (String var4 : var1) {
            File var5 = new File(var4, var2);
            if (var5.exists()) {
               return var5.getAbsolutePath();
            }

            if (Platform.isMac() && var2.endsWith(".dylib")) {
               var5 = new File(var4, var2.substring(0, var2.lastIndexOf(".dylib")) + ".jnilib");
               if (var5.exists()) {
                  return var5.getAbsolutePath();
               }
            }
         }

         return var2;
      }
   }

   static String mapSharedLibraryName(String var0) {
      if (Platform.isMac()) {
         if (!var0.startsWith("lib") || !var0.endsWith(".dylib") && !var0.endsWith(".jnilib")) {
            String var1 = System.mapLibraryName(var0);
            return var1.endsWith(".jnilib") ? var1.substring(0, var1.lastIndexOf(".jnilib")) + ".dylib" : var1;
         } else {
            return var0;
         }
      } else {
         if (!Platform.isLinux() && !Platform.isFreeBSD()) {
            if (Platform.isAIX()) {
               if (var0.startsWith("lib")) {
                  return var0;
               }
            } else if (Platform.isWindows() && (var0.endsWith(".drv") || var0.endsWith(".dll"))) {
               return var0;
            }
         } else if (isVersionedName(var0) || var0.endsWith(".so")) {
            return var0;
         }

         return System.mapLibraryName(var0);
      }
   }

   private static boolean isVersionedName(String var0) {
      if (var0.startsWith("lib")) {
         int var1 = var0.lastIndexOf(".so.");
         if (var1 != -1 && var1 + 4 < var0.length()) {
            for (int var2 = var1 + 4; var2 < var0.length(); var2++) {
               char var3 = var0.charAt(var2);
               if (!Character.isDigit(var3) && var3 != '.') {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   static String matchLibrary(final String var0, List<String> var1) {
      File var2 = new File(var0);
      if (var2.isAbsolute()) {
         var1 = Arrays.asList(var2.getParent());
      }

      FilenameFilter var3 = new FilenameFilter() {
         @Override
         public boolean accept(File var1, String var2) {
            return (var2.startsWith("lib" + var0 + ".so") || var2.startsWith(var0 + ".so") && var0.startsWith("lib")) && NativeLibrary.isVersionedName(var2);
         }
      };
      LinkedList var4 = new LinkedList();

      for (String var6 : var1) {
         File[] var7 = new File(var6).listFiles(var3);
         if (var7 != null && var7.length > 0) {
            var4.addAll(Arrays.asList(var7));
         }
      }

      double var14 = -1.0;
      String var15 = null;

      for (File var9 : var4) {
         String var10 = var9.getAbsolutePath();
         String var11 = var10.substring(var10.lastIndexOf(".so.") + 4);
         double var12 = parseVersion(var11);
         if (var12 > var14) {
            var14 = var12;
            var15 = var10;
         }
      }

      return var15;
   }

   static double parseVersion(String var0) {
      double var1 = 0.0;
      double var3 = 1.0;

      for (int var5 = var0.indexOf("."); var0 != null; var3 *= 100.0) {
         String var6;
         if (var5 != -1) {
            var6 = var0.substring(0, var5);
            var0 = var0.substring(var5 + 1);
            var5 = var0.indexOf(".");
         } else {
            var6 = var0;
            var0 = null;
         }

         try {
            var1 += (double)Integer.parseInt(var6) / var3;
         } catch (NumberFormatException var8) {
            return 0.0;
         }
      }

      return var1;
   }

   private static String getMultiArchPath() {
      String var0 = Platform.ARCH;
      String var1 = Platform.iskFreeBSD() ? "-kfreebsd" : (Platform.isGNU() ? "" : "-linux");
      String var2 = "-gnu";
      if (Platform.isIntel()) {
         var0 = Platform.is64Bit() ? "x86_64" : "i386";
      } else if (Platform.isPPC()) {
         var0 = Platform.is64Bit() ? "powerpc64" : "powerpc";
      } else if (Platform.isARM()) {
         var0 = "arm";
         var2 = "-gnueabi";
      }

      return var0 + var1 + var2;
   }

   private static ArrayList<String> getLinuxLdPaths() {
      ArrayList var0 = new ArrayList();

      try {
         Process var1 = Runtime.getRuntime().exec("/sbin/ldconfig -p");
         BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream()));
         String var3 = "";

         while ((var3 = var2.readLine()) != null) {
            int var4 = var3.indexOf(" => ");
            int var5 = var3.lastIndexOf(47);
            if (var4 != -1 && var5 != -1 && var4 < var5) {
               String var6 = var3.substring(var4 + 4, var5);
               if (!var0.contains(var6)) {
                  var0.add(var6);
               }
            }
         }

         var2.close();
      } catch (Exception var7) {
      }

      return var0;
   }

   static {
      if (Native.POINTER_SIZE == 0) {
         throw new Error("Native library not initialized");
      } else {
         String var0 = Native.getWebStartLibraryPath("jnidispatch");
         if (var0 != null) {
            librarySearchPath.add(var0);
         }

         if (System.getProperty("jna.platform.library.path") == null && !Platform.isWindows()) {
            String var1 = "";
            String var2 = "";
            String var3 = "";
            if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD() || Platform.iskFreeBSD()) {
               var3 = (Platform.isSolaris() ? "/" : "") + Pointer.SIZE * 8;
            }

            String[] var4 = new String[]{"/usr/lib" + var3, "/lib" + var3, "/usr/lib", "/lib"};
            if (Platform.isLinux() || Platform.iskFreeBSD() || Platform.isGNU()) {
               String var5 = getMultiArchPath();
               var4 = new String[]{"/usr/lib/" + var5, "/lib/" + var5, "/usr/lib" + var3, "/lib" + var3, "/usr/lib", "/lib"};
            }

            if (Platform.isLinux()) {
               ArrayList var8 = getLinuxLdPaths();

               for (int var6 = var4.length - 1; 0 <= var6; var6--) {
                  int var7 = var8.indexOf(var4[var6]);
                  if (var7 != -1) {
                     var8.remove(var7);
                  }

                  var8.add(0, var4[var6]);
               }

               var4 = var8.toArray(new String[var8.size()]);
            }

            for (int var9 = 0; var9 < var4.length; var9++) {
               File var10 = new File(var4[var9]);
               if (var10.exists() && var10.isDirectory()) {
                  var1 = var1 + var2 + var4[var9];
                  var2 = File.pathSeparator;
               }
            }

            if (!"".equals(var1)) {
               System.setProperty("jna.platform.library.path", var1);
            }
         }

         librarySearchPath.addAll(initPaths("jna.platform.library.path"));
      }
   }
}
