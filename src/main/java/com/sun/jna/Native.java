package com.sun.jna;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.Map.Entry;

public final class Native implements Version {
   public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
   public static boolean DEBUG_LOAD = Boolean.getBoolean("jna.debug_load");
   public static boolean DEBUG_JNA_LOAD = Boolean.getBoolean("jna.debug_load.jna");
   static String jnidispatchPath = null;
   private static final Map<Class<?>, Map<String, Object>> typeOptions = new WeakHashMap<>();
   private static final Map<Class<?>, Reference<?>> libraries = new WeakHashMap<>();
   private static final String _OPTION_ENCLOSING_LIBRARY = "enclosing-library";
   private static final Callback.UncaughtExceptionHandler DEFAULT_HANDLER = new Callback.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Callback var1, Throwable var2) {
         System.err.println("JNA: Callback " + var1 + " threw the following exception:");
         var2.printStackTrace();
      }
   };
   private static Callback.UncaughtExceptionHandler callbackExceptionHandler = DEFAULT_HANDLER;
   public static final int POINTER_SIZE;
   public static final int LONG_SIZE;
   public static final int WCHAR_SIZE;
   public static final int SIZE_T_SIZE;
   public static final int BOOL_SIZE;
   private static final int TYPE_VOIDP = 0;
   private static final int TYPE_LONG = 1;
   private static final int TYPE_WCHAR_T = 2;
   private static final int TYPE_SIZE_T = 3;
   private static final int TYPE_BOOL = 4;
   static final int MAX_ALIGNMENT;
   static final int MAX_PADDING;
   private static final Object finalizer;
   static final String JNA_TMPLIB_PREFIX = "jna";
   private static Map<Class<?>, long[]> registeredClasses;
   private static Map<Class<?>, NativeLibrary> registeredLibraries;
   static final int CB_HAS_INITIALIZER = 1;
   private static final int CVT_UNSUPPORTED = -1;
   private static final int CVT_DEFAULT = 0;
   private static final int CVT_POINTER = 1;
   private static final int CVT_STRING = 2;
   private static final int CVT_STRUCTURE = 3;
   private static final int CVT_STRUCTURE_BYVAL = 4;
   private static final int CVT_BUFFER = 5;
   private static final int CVT_ARRAY_BYTE = 6;
   private static final int CVT_ARRAY_SHORT = 7;
   private static final int CVT_ARRAY_CHAR = 8;
   private static final int CVT_ARRAY_INT = 9;
   private static final int CVT_ARRAY_LONG = 10;
   private static final int CVT_ARRAY_FLOAT = 11;
   private static final int CVT_ARRAY_DOUBLE = 12;
   private static final int CVT_ARRAY_BOOLEAN = 13;
   private static final int CVT_BOOLEAN = 14;
   private static final int CVT_CALLBACK = 15;
   private static final int CVT_FLOAT = 16;
   private static final int CVT_NATIVE_MAPPED = 17;
   private static final int CVT_NATIVE_MAPPED_STRING = 18;
   private static final int CVT_NATIVE_MAPPED_WSTRING = 19;
   private static final int CVT_WSTRING = 20;
   private static final int CVT_INTEGER_TYPE = 21;
   private static final int CVT_POINTER_TYPE = 22;
   private static final int CVT_TYPE_MAPPER = 23;
   private static final int CVT_TYPE_MAPPER_STRING = 24;
   private static final int CVT_TYPE_MAPPER_WSTRING = 25;
   static final int CB_OPTION_DIRECT = 1;
   static final int CB_OPTION_IN_DLL = 2;
   private static final ThreadLocal<Memory> nativeThreadTerminationFlag;
   private static final Map<Thread, Pointer> nativeThreads;

   @Deprecated
   public static float parseVersion(String var0) {
      return Float.parseFloat(var0.substring(0, var0.lastIndexOf(".")));
   }

   static boolean isCompatibleVersion(String var0, String var1) {
      String[] var2 = var0.split("\\.");
      String[] var3 = var1.split("\\.");
      if (var2.length >= 3 && var3.length >= 3) {
         int var4 = Integer.parseInt(var2[0]);
         int var5 = Integer.parseInt(var3[0]);
         int var6 = Integer.parseInt(var2[1]);
         int var7 = Integer.parseInt(var3[1]);
         return var4 != var5 ? false : var6 <= var7;
      } else {
         return false;
      }
   }

   private static void dispose() {
      CallbackReference.disposeAll();
      Memory.disposeAll();
      NativeLibrary.disposeAll();
      unregisterAll();
      jnidispatchPath = null;
      System.setProperty("jna.loaded", "false");
   }

   static boolean deleteLibrary(File var0) {
      if (var0.delete()) {
         return true;
      } else {
         markTemporaryFile(var0);
         return false;
      }
   }

   private Native() {
   }

   private static native void initIDs();

   public static synchronized native void setProtected(boolean var0);

   public static synchronized native boolean isProtected();

   @Deprecated
   public static void setPreserveLastError(boolean var0) {
   }

   @Deprecated
   public static boolean getPreserveLastError() {
      return true;
   }

   public static long getWindowID(Window var0) throws HeadlessException {
      return Native.AWT.getWindowID(var0);
   }

   public static long getComponentID(Component var0) throws HeadlessException {
      return Native.AWT.getComponentID(var0);
   }

   public static Pointer getWindowPointer(Window var0) throws HeadlessException {
      return new Pointer(Native.AWT.getWindowID(var0));
   }

   public static Pointer getComponentPointer(Component var0) throws HeadlessException {
      return new Pointer(Native.AWT.getComponentID(var0));
   }

   static native long getWindowHandle0(Component var0);

   public static Pointer getDirectBufferPointer(Buffer var0) {
      long var1 = _getDirectBufferPointer(var0);
      return var1 == 0L ? null : new Pointer(var1);
   }

   private static native long _getDirectBufferPointer(Buffer var0);

   public static String toString(byte[] var0) {
      return toString(var0, getDefaultStringEncoding());
   }

   public static String toString(byte[] var0, String var1) {
      int var2 = var0.length;

      for (int var3 = 0; var3 < var2; var3++) {
         if (var0[var3] == 0) {
            var2 = var3;
            break;
         }
      }

      if (var2 == 0) {
         return "";
      } else {
         if (var1 != null) {
            try {
               return new String(var0, 0, var2, var1);
            } catch (UnsupportedEncodingException var4) {
               System.err.println("JNA Warning: Encoding '" + var1 + "' is unsupported");
            }
         }

         System.err.println("JNA Warning: Decoding with fallback " + System.getProperty("file.encoding"));
         return new String(var0, 0, var2);
      }
   }

   public static String toString(char[] var0) {
      int var1 = var0.length;

      for (int var2 = 0; var2 < var1; var2++) {
         if (var0[var2] == 0) {
            var1 = var2;
            break;
         }
      }

      return var1 == 0 ? "" : new String(var0, 0, var1);
   }

   public static List<String> toStringList(char[] var0) {
      return toStringList(var0, 0, var0.length);
   }

   public static List<String> toStringList(char[] var0, int var1, int var2) {
      ArrayList var3 = new ArrayList();
      int var4 = var1;
      int var5 = var1 + var2;

      for (int var6 = var1; var6 < var5; var6++) {
         if (var0[var6] == 0) {
            if (var4 == var6) {
               return var3;
            }

            String var7 = new String(var0, var4, var6 - var4);
            var3.add(var7);
            var4 = var6 + 1;
         }
      }

      if (var4 < var5) {
         String var8 = new String(var0, var4, var5 - var4);
         var3.add(var8);
      }

      return var3;
   }

   public static <T> T loadLibrary(Class<T> var0) {
      return loadLibrary(null, var0);
   }

   public static <T> T loadLibrary(Class<T> var0, Map<String, ?> var1) {
      return loadLibrary(null, var0, var1);
   }

   public static <T> T loadLibrary(String var0, Class<T> var1) {
      return loadLibrary(var0, var1, Collections.emptyMap());
   }

   public static <T> T loadLibrary(String var0, Class<T> var1, Map<String, ?> var2) {
      if (!Library.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Interface (" + var1.getSimpleName() + ") of library=" + var0 + " does not extend " + Library.class.getSimpleName());
      } else {
         Library.Handler var3 = new Library.Handler(var0, var1, var2);
         ClassLoader var4 = var1.getClassLoader();
         Object var5 = Proxy.newProxyInstance(var4, new Class[]{var1}, var3);
         cacheOptions(var1, var2, var5);
         return (T)var1.cast(var5);
      }
   }

   private static void loadLibraryInstance(Class<?> var0) {
      synchronized (libraries) {
         if (var0 != null && !libraries.containsKey(var0)) {
            try {
               Field[] var2 = var0.getFields();

               for (int var3 = 0; var3 < var2.length; var3++) {
                  Field var4 = var2[var3];
                  if (var4.getType() == var0 && Modifier.isStatic(var4.getModifiers())) {
                     libraries.put(var0, new WeakReference<>(var4.get(null)));
                     break;
                  }
               }
            } catch (Exception var6) {
               throw new IllegalArgumentException("Could not access instance of " + var0 + " (" + var6 + ")");
            }
         }
      }
   }

   static Class<?> findEnclosingLibraryClass(Class<?> var0) {
      if (var0 == null) {
         return null;
      } else {
         synchronized (libraries) {
            if (typeOptions.containsKey(var0)) {
               Map var7 = typeOptions.get(var0);
               Class var3 = (Class)var7.get("enclosing-library");
               if (var3 != null) {
                  return var3;
               }

               return var0;
            }
         }

         if (Library.class.isAssignableFrom(var0)) {
            return var0;
         } else {
            if (Callback.class.isAssignableFrom(var0)) {
               var0 = CallbackReference.findCallbackClass(var0);
            }

            Class var6 = var0.getDeclaringClass();
            Class var2 = findEnclosingLibraryClass(var6);
            return var2 != null ? var2 : findEnclosingLibraryClass(var0.getSuperclass());
         }
      }
   }

   public static Map<String, Object> getLibraryOptions(Class<?> var0) {
      synchronized (libraries) {
         Map var1 = typeOptions.get(var0);
         if (var1 != null) {
            return var1;
         }
      }

      Class var14 = findEnclosingLibraryClass(var0);
      if (var14 != null) {
         loadLibraryInstance(var14);
      } else {
         var14 = var0;
      }

      synchronized (libraries) {
         Map var10 = typeOptions.get(var14);
         if (var10 != null) {
            typeOptions.put(var0, var10);
            return var10;
         } else {
            try {
               Field var4 = var14.getField("OPTIONS");
               var4.setAccessible(true);
               var10 = (Map)var4.get(null);
               if (var10 == null) {
                  throw new IllegalStateException("Null options field");
               }
            } catch (NoSuchFieldException var6) {
               var10 = Collections.emptyMap();
            } catch (Exception var7) {
               throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map (" + var7 + "): " + var14);
            }

            HashMap var12 = new HashMap(var10);
            if (!var12.containsKey("type-mapper")) {
               var12.put("type-mapper", lookupField(var14, "TYPE_MAPPER", TypeMapper.class));
            }

            if (!var12.containsKey("structure-alignment")) {
               var12.put("structure-alignment", lookupField(var14, "STRUCTURE_ALIGNMENT", Integer.class));
            }

            if (!var12.containsKey("string-encoding")) {
               var12.put("string-encoding", lookupField(var14, "STRING_ENCODING", String.class));
            }

            var10 = cacheOptions(var14, var12, null);
            if (var0 != var14) {
               typeOptions.put(var0, var10);
            }

            return var10;
         }
      }
   }

   private static Object lookupField(Class<?> var0, String var1, Class<?> var2) {
      try {
         Field var3 = var0.getField(var1);
         var3.setAccessible(true);
         return var3.get(null);
      } catch (NoSuchFieldException var4) {
         return null;
      } catch (Exception var5) {
         throw new IllegalArgumentException(var1 + " must be a public field of type " + var2.getName() + " (" + var5 + "): " + var0);
      }
   }

   public static TypeMapper getTypeMapper(Class<?> var0) {
      Map var1 = getLibraryOptions(var0);
      return (TypeMapper)var1.get("type-mapper");
   }

   public static String getStringEncoding(Class<?> var0) {
      Map var1 = getLibraryOptions(var0);
      String var2 = (String)var1.get("string-encoding");
      return var2 != null ? var2 : getDefaultStringEncoding();
   }

   public static String getDefaultStringEncoding() {
      return System.getProperty("jna.encoding", DEFAULT_ENCODING);
   }

   public static int getStructureAlignment(Class<?> var0) {
      Integer var1 = (Integer)getLibraryOptions(var0).get("structure-alignment");
      return var1 == null ? 0 : var1;
   }

   static byte[] getBytes(String var0) {
      return getBytes(var0, getDefaultStringEncoding());
   }

   static byte[] getBytes(String var0, String var1) {
      if (var1 != null) {
         try {
            return var0.getBytes(var1);
         } catch (UnsupportedEncodingException var3) {
            System.err.println("JNA Warning: Encoding '" + var1 + "' is unsupported");
         }
      }

      System.err.println("JNA Warning: Encoding with fallback " + System.getProperty("file.encoding"));
      return var0.getBytes();
   }

   public static byte[] toByteArray(String var0) {
      return toByteArray(var0, getDefaultStringEncoding());
   }

   public static byte[] toByteArray(String var0, String var1) {
      byte[] var2 = getBytes(var0, var1);
      byte[] var3 = new byte[var2.length + 1];
      System.arraycopy(var2, 0, var3, 0, var2.length);
      return var3;
   }

   public static char[] toCharArray(String var0) {
      char[] var1 = var0.toCharArray();
      char[] var2 = new char[var1.length + 1];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   private static void loadNativeDispatchLibrary() {
      if (!Boolean.getBoolean("jna.nounpack")) {
         try {
            removeTemporaryFiles();
         } catch (IOException var9) {
            System.err.println("JNA Warning: IOException removing temporary files: " + var9.getMessage());
         }
      }

      String var0 = System.getProperty("jna.boot.library.name", "jnidispatch");
      String var1 = System.getProperty("jna.boot.library.path");
      if (var1 != null) {
         StringTokenizer var2 = new StringTokenizer(var1, File.pathSeparator);

         while (var2.hasMoreTokens()) {
            String var3 = var2.nextToken();
            File var4 = new File(new File(var3), System.mapLibraryName(var0).replace(".dylib", ".jnilib"));
            String var5 = var4.getAbsolutePath();
            if (DEBUG_JNA_LOAD) {
               System.out.println("Looking in " + var5);
            }

            if (var4.exists()) {
               try {
                  if (DEBUG_JNA_LOAD) {
                     System.out.println("Trying " + var5);
                  }

                  System.setProperty("jnidispatch.path", var5);
                  System.load(var5);
                  jnidispatchPath = var5;
                  if (DEBUG_JNA_LOAD) {
                     System.out.println("Found jnidispatch at " + var5);
                  }

                  return;
               } catch (UnsatisfiedLinkError var12) {
               }
            }

            if (Platform.isMac()) {
               String var6;
               String var7;
               if (var5.endsWith("dylib")) {
                  var6 = "dylib";
                  var7 = "jnilib";
               } else {
                  var6 = "jnilib";
                  var7 = "dylib";
               }

               var5 = var5.substring(0, var5.lastIndexOf(var6)) + var7;
               if (DEBUG_JNA_LOAD) {
                  System.out.println("Looking in " + var5);
               }

               if (new File(var5).exists()) {
                  try {
                     if (DEBUG_JNA_LOAD) {
                        System.out.println("Trying " + var5);
                     }

                     System.setProperty("jnidispatch.path", var5);
                     System.load(var5);
                     jnidispatchPath = var5;
                     if (DEBUG_JNA_LOAD) {
                        System.out.println("Found jnidispatch at " + var5);
                     }

                     return;
                  } catch (UnsatisfiedLinkError var11) {
                     System.err.println("File found at " + var5 + " but not loadable: " + var11.getMessage());
                  }
               }
            }
         }
      }

      if (!Boolean.getBoolean("jna.nosys")) {
         try {
            if (DEBUG_JNA_LOAD) {
               System.out.println("Trying (via loadLibrary) " + var0);
            }

            System.loadLibrary(var0);
            if (DEBUG_JNA_LOAD) {
               System.out.println("Found jnidispatch on system path");
            }

            return;
         } catch (UnsatisfiedLinkError var10) {
         }
      }

      if (!Boolean.getBoolean("jna.noclasspath")) {
         loadNativeDispatchLibraryFromClasspath();
      } else {
         throw new UnsatisfiedLinkError("Unable to locate JNA native support library");
      }
   }

   private static void loadNativeDispatchLibraryFromClasspath() {
      try {
         String var0 = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("jnidispatch").replace(".dylib", ".jnilib");
         File var1 = extractFromResourcePath(var0, Native.class.getClassLoader());
         if (var1 == null && var1 == null) {
            throw new UnsatisfiedLinkError("Could not find JNA native support");
         } else {
            if (DEBUG_JNA_LOAD) {
               System.out.println("Trying " + var1.getAbsolutePath());
            }

            System.setProperty("jnidispatch.path", var1.getAbsolutePath());
            System.load(var1.getAbsolutePath());
            jnidispatchPath = var1.getAbsolutePath();
            if (DEBUG_JNA_LOAD) {
               System.out.println("Found jnidispatch at " + jnidispatchPath);
            }

            if (isUnpacked(var1) && !Boolean.getBoolean("jnidispatch.preserve")) {
               deleteLibrary(var1);
            }
         }
      } catch (IOException var2) {
         throw new UnsatisfiedLinkError(var2.getMessage());
      }
   }

   static boolean isUnpacked(File var0) {
      return var0.getName().startsWith("jna");
   }

   public static File extractFromResourcePath(String var0) throws IOException {
      return extractFromResourcePath(var0, null);
   }

   public static File extractFromResourcePath(String var0, ClassLoader var1) throws IOException {
      boolean var2 = DEBUG_LOAD || DEBUG_JNA_LOAD && var0.indexOf("jnidispatch") != -1;
      if (var1 == null) {
         var1 = Thread.currentThread().getContextClassLoader();
         if (var1 == null) {
            var1 = Native.class.getClassLoader();
         }
      }

      if (var2) {
         System.out.println("Looking in classpath from " + var1 + " for " + var0);
      }

      String var3 = var0.startsWith("/") ? var0 : NativeLibrary.mapSharedLibraryName(var0);
      String var4 = var0.startsWith("/") ? var0 : Platform.RESOURCE_PREFIX + "/" + var3;
      if (var4.startsWith("/")) {
         var4 = var4.substring(1);
      }

      URL var5 = var1.getResource(var4);
      if (var5 == null && var4.startsWith(Platform.RESOURCE_PREFIX)) {
         var5 = var1.getResource(var3);
      }

      if (var5 == null) {
         String var26 = System.getProperty("java.class.path");
         if (var1 instanceof URLClassLoader) {
            var26 = Arrays.asList(((URLClassLoader)var1).getURLs()).toString();
         }

         throw new IOException("Native library (" + var4 + ") not found in resource path (" + var26 + ")");
      } else {
         if (var2) {
            System.out.println("Found library resource at " + var5);
         }

         File var6 = null;
         if (var5.getProtocol().toLowerCase().equals("file")) {
            try {
               var6 = new File(new URI(var5.toString()));
            } catch (URISyntaxException var23) {
               var6 = new File(var5.getPath());
            }

            if (var2) {
               System.out.println("Looking in " + var6.getAbsolutePath());
            }

            if (!var6.exists()) {
               throw new IOException("File URL " + var5 + " could not be properly decoded");
            }
         } else if (!Boolean.getBoolean("jna.nounpack")) {
            InputStream var7 = var1.getResourceAsStream(var4);
            if (var7 == null) {
               throw new IOException("Can't obtain InputStream for " + var4);
            }

            FileOutputStream var8 = null;

            try {
               File var9 = getTempDir();
               var6 = File.createTempFile("jna", Platform.isWindows() ? ".dll" : null, var9);
               if (!Boolean.getBoolean("jnidispatch.preserve")) {
                  var6.deleteOnExit();
               }

               var8 = new FileOutputStream(var6);
               byte[] var11 = new byte[1024];

               int var10;
               while ((var10 = var7.read(var11, 0, var11.length)) > 0) {
                  var8.write(var11, 0, var10);
               }
            } catch (IOException var24) {
               throw new IOException("Failed to create temporary file for " + var0 + " library: " + var24.getMessage());
            } finally {
               try {
                  var7.close();
               } catch (IOException var22) {
               }

               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (IOException var21) {
                  }
               }
            }
         }

         return var6;
      }
   }

   private static native int sizeof(int var0);

   private static native String getNativeVersion();

   private static native String getAPIChecksum();

   public static native int getLastError();

   public static native void setLastError(int var0);

   public static Library synchronizedLibrary(final Library var0) {
      Class var1 = var0.getClass();
      if (!Proxy.isProxyClass(var1)) {
         throw new IllegalArgumentException("Library must be a proxy class");
      } else {
         InvocationHandler var2 = Proxy.getInvocationHandler(var0);
         if (!(var2 instanceof Library.Handler)) {
            throw new IllegalArgumentException("Unrecognized proxy handler: " + var2);
         } else {
            final Library.Handler var3 = (Library.Handler)var2;
            InvocationHandler var4 = new InvocationHandler() {
               @Override
               public Object invoke(Object var1, Method var2, Object[] var3x) throws Throwable {
                  synchronized (var3.getNativeLibrary()) {
                     return var3.invoke(var0, var2, var3x);
                  }
               }
            };
            return (Library)Proxy.newProxyInstance(var1.getClassLoader(), var1.getInterfaces(), var4);
         }
      }
   }

   public static String getWebStartLibraryPath(String var0) {
      if (System.getProperty("javawebstart.version") == null) {
         return null;
      } else {
         try {
            ClassLoader var1 = Native.class.getClassLoader();
            Method var2 = AccessController.doPrivileged(new PrivilegedAction<Method>() {
               public Method run() {
                  try {
                     Method var1 = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
                     var1.setAccessible(true);
                     return var1;
                  } catch (Exception var2) {
                     return null;
                  }
               }
            });
            String var3 = (String)var2.invoke(var1, var0);
            return var3 != null ? new File(var3).getParent() : null;
         } catch (Exception var4) {
            return null;
         }
      }
   }

   static void markTemporaryFile(File var0) {
      try {
         File var1 = new File(var0.getParentFile(), var0.getName() + ".x");
         var1.createNewFile();
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   static File getTempDir() throws IOException {
      String var1 = System.getProperty("jna.tmpdir");
      File var0;
      if (var1 != null) {
         var0 = new File(var1);
         var0.mkdirs();
      } else {
         File var2 = new File(System.getProperty("java.io.tmpdir"));
         var0 = new File(var2, "jna-" + System.getProperty("user.name").hashCode());
         var0.mkdirs();
         if (!var0.exists() || !var0.canWrite()) {
            var0 = var2;
         }
      }

      if (!var0.exists()) {
         throw new IOException("JNA temporary directory '" + var0 + "' does not exist");
      } else if (!var0.canWrite()) {
         throw new IOException("JNA temporary directory '" + var0 + "' is not writable");
      } else {
         return var0;
      }
   }

   static void removeTemporaryFiles() throws IOException {
      File var0 = getTempDir();
      FilenameFilter var1 = new FilenameFilter() {
         @Override
         public boolean accept(File var1, String var2) {
            return var2.endsWith(".x") && var2.startsWith("jna");
         }
      };
      File[] var2 = var0.listFiles(var1);

      for (int var3 = 0; var2 != null && var3 < var2.length; var3++) {
         File var4 = var2[var3];
         String var5 = var4.getName();
         var5 = var5.substring(0, var5.length() - 2);
         File var6 = new File(var4.getParentFile(), var5);
         if (!var6.exists() || var6.delete()) {
            var4.delete();
         }
      }
   }

   public static int getNativeSize(Class<?> var0, Object var1) {
      if (var0.isArray()) {
         int var2 = Array.getLength(var1);
         if (var2 > 0) {
            Object var3 = Array.get(var1, 0);
            return var2 * getNativeSize(var0.getComponentType(), var3);
         } else {
            throw new IllegalArgumentException("Arrays of length zero not allowed: " + var0);
         }
      } else if (Structure.class.isAssignableFrom(var0) && !Structure.ByReference.class.isAssignableFrom(var0)) {
         return Structure.size(var0, (Structure)var1);
      } else {
         try {
            return getNativeSize(var0);
         } catch (IllegalArgumentException var4) {
            throw new IllegalArgumentException("The type \"" + var0.getName() + "\" is not supported: " + var4.getMessage());
         }
      }
   }

   public static int getNativeSize(Class<?> var0) {
      if (NativeMapped.class.isAssignableFrom(var0)) {
         var0 = NativeMappedConverter.getInstance(var0).nativeType();
      }

      if (var0 == boolean.class || var0 == Boolean.class) {
         return 4;
      } else if (var0 == byte.class || var0 == Byte.class) {
         return 1;
      } else if (var0 == short.class || var0 == Short.class) {
         return 2;
      } else if (var0 == char.class || var0 == Character.class) {
         return WCHAR_SIZE;
      } else if (var0 == int.class || var0 == Integer.class) {
         return 4;
      } else if (var0 == long.class || var0 == Long.class) {
         return 8;
      } else if (var0 == float.class || var0 == Float.class) {
         return 4;
      } else if (var0 == double.class || var0 == Double.class) {
         return 8;
      } else if (Structure.class.isAssignableFrom(var0)) {
         return Structure.ByValue.class.isAssignableFrom(var0) ? Structure.size(var0) : POINTER_SIZE;
      } else if (!Pointer.class.isAssignableFrom(var0)
         && (!Platform.HAS_BUFFERS || !Native.Buffers.isBuffer(var0))
         && !Callback.class.isAssignableFrom(var0)
         && String.class != var0
         && WString.class != var0) {
         throw new IllegalArgumentException("Native size for type \"" + var0.getName() + "\" is unknown");
      } else {
         return POINTER_SIZE;
      }
   }

   public static boolean isSupportedNativeType(Class<?> var0) {
      if (Structure.class.isAssignableFrom(var0)) {
         return true;
      } else {
         try {
            return getNativeSize(var0) != 0;
         } catch (IllegalArgumentException var2) {
            return false;
         }
      }
   }

   public static void setCallbackExceptionHandler(Callback.UncaughtExceptionHandler var0) {
      callbackExceptionHandler = var0 == null ? DEFAULT_HANDLER : var0;
   }

   public static Callback.UncaughtExceptionHandler getCallbackExceptionHandler() {
      return callbackExceptionHandler;
   }

   public static void register(String var0) {
      register(findDirectMappedClass(getCallingClass()), var0);
   }

   public static void register(NativeLibrary var0) {
      register(findDirectMappedClass(getCallingClass()), var0);
   }

   static Class<?> findDirectMappedClass(Class<?> var0) {
      Method[] var1 = var0.getDeclaredMethods();

      for (Method var5 : var1) {
         if ((var5.getModifiers() & 256) != 0) {
            return var0;
         }
      }

      int var7 = var0.getName().lastIndexOf("$");
      if (var7 != -1) {
         String var8 = var0.getName().substring(0, var7);

         try {
            return findDirectMappedClass(Class.forName(var8, true, var0.getClassLoader()));
         } catch (ClassNotFoundException var6) {
         }
      }

      throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + var0 + ")");
   }

   static Class<?> getCallingClass() {
      Class[] var0 = (new SecurityManager() {
         @Override
         public Class<?>[] getClassContext() {
            return super.getClassContext();
         }
      }).getClassContext();
      if (var0 == null) {
         throw new IllegalStateException("The SecurityManager implementation on this platform is broken; you must explicitly provide the class to register");
      } else if (var0.length < 4) {
         throw new IllegalStateException("This method must be called from the static initializer of a class");
      } else {
         return var0[3];
      }
   }

   public static void setCallbackThreadInitializer(Callback var0, CallbackThreadInitializer var1) {
      CallbackReference.setCallbackThreadInitializer(var0, var1);
   }

   private static void unregisterAll() {
      synchronized (registeredClasses) {
         for (Entry var2 : registeredClasses.entrySet()) {
            unregister((Class<?>)var2.getKey(), (long[])var2.getValue());
         }

         registeredClasses.clear();
      }
   }

   public static void unregister() {
      unregister(findDirectMappedClass(getCallingClass()));
   }

   public static void unregister(Class<?> var0) {
      synchronized (registeredClasses) {
         long[] var2 = registeredClasses.get(var0);
         if (var2 != null) {
            unregister(var0, var2);
            registeredClasses.remove(var0);
            registeredLibraries.remove(var0);
         }
      }
   }

   public static boolean registered(Class<?> var0) {
      synchronized (registeredClasses) {
         return registeredClasses.containsKey(var0);
      }
   }

   private static native void unregister(Class<?> var0, long[] var1);

   static String getSignature(Class<?> var0) {
      if (var0.isArray()) {
         return "[" + getSignature(var0.getComponentType());
      } else {
         if (var0.isPrimitive()) {
            if (var0 == void.class) {
               return "V";
            }

            if (var0 == boolean.class) {
               return "Z";
            }

            if (var0 == byte.class) {
               return "B";
            }

            if (var0 == short.class) {
               return "S";
            }

            if (var0 == char.class) {
               return "C";
            }

            if (var0 == int.class) {
               return "I";
            }

            if (var0 == long.class) {
               return "J";
            }

            if (var0 == float.class) {
               return "F";
            }

            if (var0 == double.class) {
               return "D";
            }
         }

         return "L" + replace(".", "/", var0.getName()) + ";";
      }
   }

   static String replace(String var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder();

      while (true) {
         int var4 = var2.indexOf(var0);
         if (var4 == -1) {
            var3.append(var2);
            return var3.toString();
         }

         var3.append(var2.substring(0, var4));
         var3.append(var1);
         var2 = var2.substring(var4 + var0.length());
      }
   }

   private static int getConversion(Class<?> var0, TypeMapper var1) {
      if (var0 == Boolean.class) {
         var0 = boolean.class;
      } else if (var0 == Byte.class) {
         var0 = byte.class;
      } else if (var0 == Short.class) {
         var0 = short.class;
      } else if (var0 == Character.class) {
         var0 = char.class;
      } else if (var0 == Integer.class) {
         var0 = int.class;
      } else if (var0 == Long.class) {
         var0 = long.class;
      } else if (var0 == Float.class) {
         var0 = float.class;
      } else if (var0 == Double.class) {
         var0 = double.class;
      } else if (var0 == Void.class) {
         var0 = void.class;
      }

      if (var1 != null) {
         FromNativeConverter var2 = var1.getFromNativeConverter(var0);
         ToNativeConverter var3 = var1.getToNativeConverter(var0);
         if (var2 != null) {
            Class var6 = var2.nativeType();
            if (var6 == String.class) {
               return 24;
            }

            if (var6 == WString.class) {
               return 25;
            }

            return 23;
         }

         if (var3 != null) {
            Class var4 = var3.nativeType();
            if (var4 == String.class) {
               return 24;
            }

            if (var4 == WString.class) {
               return 25;
            }

            return 23;
         }
      }

      if (Pointer.class.isAssignableFrom(var0)) {
         return 1;
      } else if (String.class == var0) {
         return 2;
      } else if (WString.class.isAssignableFrom(var0)) {
         return 20;
      } else if (Platform.HAS_BUFFERS && Native.Buffers.isBuffer(var0)) {
         return 5;
      } else if (Structure.class.isAssignableFrom(var0)) {
         return Structure.ByValue.class.isAssignableFrom(var0) ? 4 : 3;
      } else {
         if (var0.isArray()) {
            switch (var0.getName().charAt(1)) {
               case 'B':
                  return 6;
               case 'C':
                  return 8;
               case 'D':
                  return 12;
               case 'E':
               case 'G':
               case 'H':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               default:
                  break;
               case 'F':
                  return 11;
               case 'I':
                  return 9;
               case 'J':
                  return 10;
               case 'S':
                  return 7;
               case 'Z':
                  return 13;
            }
         }

         if (var0.isPrimitive()) {
            return var0 == boolean.class ? 14 : 0;
         } else if (Callback.class.isAssignableFrom(var0)) {
            return 15;
         } else if (IntegerType.class.isAssignableFrom(var0)) {
            return 21;
         } else if (PointerType.class.isAssignableFrom(var0)) {
            return 22;
         } else if (NativeMapped.class.isAssignableFrom(var0)) {
            Class var5 = NativeMappedConverter.getInstance(var0).nativeType();
            if (var5 == String.class) {
               return 18;
            } else {
               return var5 == WString.class ? 19 : 17;
            }
         } else {
            return -1;
         }
      }
   }

   public static void register(Class<?> var0, String var1) {
      NativeLibrary var2 = NativeLibrary.getInstance(var1, Collections.singletonMap("classloader", var0.getClassLoader()));
      register(var0, var2);
   }

   public static void register(Class<?> var0, NativeLibrary var1) {
      Method[] var2 = var0.getDeclaredMethods();
      ArrayList var3 = new ArrayList();
      Map var4 = var1.getOptions();
      TypeMapper var5 = (TypeMapper)var4.get("type-mapper");
      var4 = cacheOptions(var0, var4, null);

      for (Method var9 : var2) {
         if ((var9.getModifiers() & 256) != 0) {
            var3.add(var9);
         }
      }

      long[] var30 = new long[var3.size()];

      for (int var31 = 0; var31 < var30.length; var31++) {
         Method var33 = (Method)var3.get(var31);
         String var34 = "(";
         Class var10 = var33.getReturnType();
         Class[] var15 = var33.getParameterTypes();
         long[] var16 = new long[var15.length];
         long[] var17 = new long[var15.length];
         int[] var18 = new int[var15.length];
         ToNativeConverter[] var19 = new ToNativeConverter[var15.length];
         FromNativeConverter var20 = null;
         int var21 = getConversion(var10, var5);
         boolean var22 = false;
         long var11;
         long var13;
         switch (var21) {
            case -1:
               throw new IllegalArgumentException(var10 + " is not a supported return type (in method " + var33.getName() + " in " + var0 + ")");
            case 0:
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 20:
            default:
               var13 = var11 = Structure.FFIType.get(var10).peer;
               break;
            case 3:
               var13 = var11 = Structure.FFIType.get(Pointer.class).peer;
               break;
            case 4:
               var13 = Structure.FFIType.get(Pointer.class).peer;
               var11 = Structure.FFIType.get(var10).peer;
               break;
            case 17:
            case 18:
            case 19:
            case 21:
            case 22:
               var13 = Structure.FFIType.get(Pointer.class).peer;
               var11 = Structure.FFIType.get(NativeMappedConverter.getInstance(var10).nativeType()).peer;
               break;
            case 23:
            case 24:
            case 25:
               var20 = var5.getFromNativeConverter(var10);
               var13 = Structure.FFIType.get(var10.isPrimitive() ? var10 : Pointer.class).peer;
               var11 = Structure.FFIType.get(var20.nativeType()).peer;
         }

         for (int var23 = 0; var23 < var15.length; var23++) {
            Class var24 = var15[var23];
            var34 = var34 + getSignature(var24);
            int var25 = getConversion(var24, var5);
            var18[var23] = var25;
            if (var25 == -1) {
               throw new IllegalArgumentException(var24 + " is not a supported argument type (in method " + var33.getName() + " in " + var0 + ")");
            }

            if (var25 == 17 || var25 == 18 || var25 == 19 || var25 == 21) {
               var24 = NativeMappedConverter.getInstance(var24).nativeType();
            } else if (var25 == 23 || var25 == 24 || var25 == 25) {
               var19[var23] = var5.getToNativeConverter(var24);
            }

            switch (var25) {
               case 0:
                  var17[var23] = var16[var23] = Structure.FFIType.get(var24).peer;
                  break;
               case 1:
               case 2:
               case 3:
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
               case 16:
               case 20:
               default:
                  var17[var23] = var16[var23] = Structure.FFIType.get(Pointer.class).peer;
                  break;
               case 4:
               case 17:
               case 18:
               case 19:
               case 21:
               case 22:
                  var16[var23] = Structure.FFIType.get(var24).peer;
                  var17[var23] = Structure.FFIType.get(Pointer.class).peer;
                  break;
               case 23:
               case 24:
               case 25:
                  var17[var23] = Structure.FFIType.get(var24.isPrimitive() ? var24 : Pointer.class).peer;
                  var16[var23] = Structure.FFIType.get(var19[var23].nativeType()).peer;
            }
         }

         var34 = var34 + ")";
         var34 = var34 + getSignature(var10);
         Class[] var37 = var33.getExceptionTypes();

         for (int var38 = 0; var38 < var37.length; var38++) {
            if (LastErrorException.class.isAssignableFrom(var37[var38])) {
               var22 = true;
               break;
            }
         }

         Function var39 = var1.getFunction(var33.getName(), var33);

         try {
            var30[var31] = registerMethod(
               var0,
               var33.getName(),
               var34,
               var18,
               var17,
               var16,
               var21,
               var13,
               var11,
               var33,
               var39.peer,
               var39.getCallingConvention(),
               var22,
               var19,
               var20,
               var39.encoding
            );
         } catch (NoSuchMethodError var28) {
            throw new UnsatisfiedLinkError("No method " + var33.getName() + " with signature " + var34 + " in " + var0);
         }
      }

      synchronized (registeredClasses) {
         registeredClasses.put(var0, var30);
         registeredLibraries.put(var0, var1);
      }
   }

   private static Map<String, Object> cacheOptions(Class<?> var0, Map<String, ?> var1, Object var2) {
      HashMap var3 = new HashMap(var1);
      var3.put("enclosing-library", var0);
      synchronized (libraries) {
         typeOptions.put(var0, var3);
         if (var2 != null) {
            libraries.put(var0, new WeakReference<>(var2));
         }

         if (!var0.isInterface() && Library.class.isAssignableFrom(var0)) {
            Class[] var5 = var0.getInterfaces();

            for (Class var9 : var5) {
               if (Library.class.isAssignableFrom(var9)) {
                  cacheOptions(var9, var3, var2);
                  break;
               }
            }
         }

         return var3;
      }
   }

   private static native long registerMethod(
      Class<?> var0,
      String var1,
      String var2,
      int[] var3,
      long[] var4,
      long[] var5,
      int var6,
      long var7,
      long var9,
      Method var11,
      long var12,
      int var14,
      boolean var15,
      ToNativeConverter[] var16,
      FromNativeConverter var17,
      String var18
   );

   private static NativeMapped fromNative(Class<?> var0, Object var1) {
      return (NativeMapped)NativeMappedConverter.getInstance(var0).fromNative(var1, new FromNativeContext(var0));
   }

   private static NativeMapped fromNative(Method var0, Object var1) {
      Class var2 = var0.getReturnType();
      return (NativeMapped)NativeMappedConverter.getInstance(var2).fromNative(var1, new MethodResultContext(var2, null, null, var0));
   }

   private static Class<?> nativeType(Class<?> var0) {
      return NativeMappedConverter.getInstance(var0).nativeType();
   }

   private static Object toNative(ToNativeConverter var0, Object var1) {
      return var0.toNative(var1, new ToNativeContext());
   }

   private static Object fromNative(FromNativeConverter var0, Object var1, Method var2) {
      return var0.fromNative(var1, new MethodResultContext(var2.getReturnType(), null, null, var2));
   }

   public static native long ffi_prep_cif(int var0, int var1, long var2, long var4);

   public static native void ffi_call(long var0, long var2, long var4, long var6);

   public static native long ffi_prep_closure(long var0, Native.ffi_callback var2);

   public static native void ffi_free_closure(long var0);

   static native int initialize_ffi_type(long var0);

   public static void main(String[] var0) {
      String var1 = "Java Native Access (JNA)";
      String var2 = "4.4.0";
      String var3 = "4.4.0 (package information missing)";
      Package var4 = Native.class.getPackage();
      String var5 = var4 != null ? var4.getSpecificationTitle() : "Java Native Access (JNA)";
      if (var5 == null) {
         var5 = "Java Native Access (JNA)";
      }

      String var6 = var4 != null ? var4.getSpecificationVersion() : "4.4.0";
      if (var6 == null) {
         var6 = "4.4.0";
      }

      var5 = var5 + " API Version " + var6;
      System.out.println(var5);
      var6 = var4 != null ? var4.getImplementationVersion() : "4.4.0 (package information missing)";
      if (var6 == null) {
         var6 = "4.4.0 (package information missing)";
      }

      System.out.println("Version: " + var6);
      System.out.println(" Native: " + getNativeVersion() + " (" + getAPIChecksum() + ")");
      System.out.println(" Prefix: " + Platform.RESOURCE_PREFIX);
   }

   static synchronized native void freeNativeCallback(long var0);

   static synchronized native long createNativeCallback(Callback var0, Method var1, Class<?>[] var2, Class<?> var3, int var4, int var5, String var6);

   static native int invokeInt(Function var0, long var1, int var3, Object[] var4);

   static native long invokeLong(Function var0, long var1, int var3, Object[] var4);

   static native void invokeVoid(Function var0, long var1, int var3, Object[] var4);

   static native float invokeFloat(Function var0, long var1, int var3, Object[] var4);

   static native double invokeDouble(Function var0, long var1, int var3, Object[] var4);

   static native long invokePointer(Function var0, long var1, int var3, Object[] var4);

   private static native void invokeStructure(Function var0, long var1, int var3, Object[] var4, long var5, long var7);

   static Structure invokeStructure(Function var0, long var1, int var3, Object[] var4, Structure var5) {
      invokeStructure(var0, var1, var3, var4, var5.getPointer().peer, var5.getTypeInfo().peer);
      return var5;
   }

   static native Object invokeObject(Function var0, long var1, int var3, Object[] var4);

   static long open(String var0) {
      return open(var0, -1);
   }

   static native long open(String var0, int var1);

   static native void close(long var0);

   static native long findSymbol(long var0, String var2);

   static native long indexOf(Pointer var0, long var1, long var3, byte var5);

   static native void read(Pointer var0, long var1, long var3, byte[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, short[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, char[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, int[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, long[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, float[] var5, int var6, int var7);

   static native void read(Pointer var0, long var1, long var3, double[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, byte[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, short[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, char[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, int[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, long[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, float[] var5, int var6, int var7);

   static native void write(Pointer var0, long var1, long var3, double[] var5, int var6, int var7);

   static native byte getByte(Pointer var0, long var1, long var3);

   static native char getChar(Pointer var0, long var1, long var3);

   static native short getShort(Pointer var0, long var1, long var3);

   static native int getInt(Pointer var0, long var1, long var3);

   static native long getLong(Pointer var0, long var1, long var3);

   static native float getFloat(Pointer var0, long var1, long var3);

   static native double getDouble(Pointer var0, long var1, long var3);

   static Pointer getPointer(long var0) {
      long var2 = _getPointer(var0);
      return var2 == 0L ? null : new Pointer(var2);
   }

   private static native long _getPointer(long var0);

   static native String getWideString(Pointer var0, long var1, long var3);

   static String getString(Pointer var0, long var1) {
      return getString(var0, var1, getDefaultStringEncoding());
   }

   static String getString(Pointer var0, long var1, String var3) {
      byte[] var4 = getStringBytes(var0, var0.peer, var1);
      if (var3 != null) {
         try {
            return new String(var4, var3);
         } catch (UnsupportedEncodingException var6) {
         }
      }

      return new String(var4);
   }

   static native byte[] getStringBytes(Pointer var0, long var1, long var3);

   static native void setMemory(Pointer var0, long var1, long var3, long var5, byte var7);

   static native void setByte(Pointer var0, long var1, long var3, byte var5);

   static native void setShort(Pointer var0, long var1, long var3, short var5);

   static native void setChar(Pointer var0, long var1, long var3, char var5);

   static native void setInt(Pointer var0, long var1, long var3, int var5);

   static native void setLong(Pointer var0, long var1, long var3, long var5);

   static native void setFloat(Pointer var0, long var1, long var3, float var5);

   static native void setDouble(Pointer var0, long var1, long var3, double var5);

   static native void setPointer(Pointer var0, long var1, long var3, long var5);

   static native void setWideString(Pointer var0, long var1, long var3, String var5);

   static native ByteBuffer getDirectByteBuffer(Pointer var0, long var1, long var3, long var5);

   public static native long malloc(long var0);

   public static native void free(long var0);

   @Deprecated
   public static native ByteBuffer getDirectByteBuffer(long var0, long var2);

   public static void detach(boolean var0) {
      Thread var1 = Thread.currentThread();
      if (var0) {
         nativeThreads.remove(var1);
         Pointer var2 = nativeThreadTerminationFlag.get();
         setDetachState(true, 0L);
      } else if (!nativeThreads.containsKey(var1)) {
         Pointer var3 = nativeThreadTerminationFlag.get();
         nativeThreads.put(var1, var3);
         setDetachState(false, var3.peer);
      }
   }

   static Pointer getTerminationFlag(Thread var0) {
      return nativeThreads.get(var0);
   }

   private static native void setDetachState(boolean var0, long var1);

   static {
      loadNativeDispatchLibrary();
      if (!isCompatibleVersion("5.1.0", getNativeVersion())) {
         String var0 = System.getProperty("line.separator");
         throw new Error(
            var0
               + var0
               + "There is an incompatible JNA native library installed on this system"
               + var0
               + "Expected: "
               + "5.1.0"
               + var0
               + "Found:    "
               + getNativeVersion()
               + var0
               + (jnidispatchPath != null ? "(at " + jnidispatchPath + ")" : System.getProperty("java.library.path"))
               + "."
               + var0
               + "To resolve this issue you may do one of the following:"
               + var0
               + " - remove or uninstall the offending library"
               + var0
               + " - set the system property jna.nosys=true"
               + var0
               + " - set jna.boot.library.path to include the path to the version of the "
               + var0
               + "   jnidispatch library included with the JNA jar file you are using"
               + var0
         );
      } else {
         POINTER_SIZE = sizeof(0);
         LONG_SIZE = sizeof(1);
         WCHAR_SIZE = sizeof(2);
         SIZE_T_SIZE = sizeof(3);
         BOOL_SIZE = sizeof(4);
         initIDs();
         if (Boolean.getBoolean("jna.protected")) {
            setProtected(true);
         }

         MAX_ALIGNMENT = !Platform.isSPARC()
               && !Platform.isWindows()
               && (!Platform.isLinux() || !Platform.isARM() && !Platform.isPPC())
               && !Platform.isAIX()
               && !Platform.isAndroid()
            ? LONG_SIZE
            : 8;
         MAX_PADDING = Platform.isMac() && Platform.isPPC() ? 8 : MAX_ALIGNMENT;
         System.setProperty("jna.loaded", "true");
         finalizer = new Object() {
            @Override
            protected void finalize() {
               Native.dispose();
            }
         };
         registeredClasses = new WeakHashMap<>();
         registeredLibraries = new WeakHashMap<>();
         nativeThreadTerminationFlag = new ThreadLocal<Memory>() {
            protected Memory initialValue() {
               Memory var1 = new Memory(4L);
               var1.clear();
               return var1;
            }
         };
         nativeThreads = Collections.synchronizedMap(new WeakHashMap<>());
      }
   }

   private static class AWT {
      static long getWindowID(Window var0) throws HeadlessException {
         return getComponentID(var0);
      }

      static long getComponentID(Object var0) throws HeadlessException {
         if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("No native windows when headless");
         } else {
            Component var1 = (Component)var0;
            if (var1.isLightweight()) {
               throw new IllegalArgumentException("Component must be heavyweight");
            } else if (!var1.isDisplayable()) {
               throw new IllegalStateException("Component must be displayable");
            } else if (Platform.isX11() && System.getProperty("java.version").startsWith("1.4") && !var1.isVisible()) {
               throw new IllegalStateException("Component must be visible");
            } else {
               return Native.getWindowHandle0(var1);
            }
         }
      }
   }

   private static class Buffers {
      static boolean isBuffer(Class<?> var0) {
         return Buffer.class.isAssignableFrom(var0);
      }
   }

   public interface ffi_callback {
      void invoke(long var1, long var3, long var5);
   }
}
