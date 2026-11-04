package net.md_5.bungee.jni;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.zlib.XCordNativeZLib;

public final class NativeCode<T> {
   private final String name;
   private final Supplier<? extends T> javaImpl;
   private final Supplier<? extends T> nativeImpl;
   private boolean loaded;

   public NativeCode(String var1, Supplier<? extends T> var2, Supplier<? extends T> var3) {
      this(var1, var2, var3, null);
   }

   public NativeCode(String var1, Supplier<? extends T> var2, Supplier<? extends T> var3, Supplier<? extends T> var4) {
      if ("Mac OS X".equals(System.getProperty("os.name"))) {
         var1 = "osx-" + var1;
         if (var1.contains("compress")) {
            var3 = var4;
         }
      } else if (var3.get() instanceof XCordNativeZLib) {
         System.out.println("XCord is attempting to load the native compression library");
         var1 = var1 + "-xcord";
      }

      System.out.println("Attempting to load:: " + var1);
      this.name = var1;
      this.javaImpl = var2;
      this.nativeImpl = var3;
   }

   public T newInstance() {
      return (T)(this.loaded ? this.nativeImpl.get() : this.javaImpl.get());
   }

   public boolean load() {
      if (!this.loaded && isSupported()) {
         String var1 = "bungeecord-" + this.name;

         try {
            System.loadLibrary(var1);
            this.loaded = true;
         } catch (Throwable var10) {
         }

         if (!this.loaded) {
            try {
               InputStream var2 = BungeeCipher.class.getClassLoader().getResourceAsStream(this.name + ".so");

               try {
                  File var3 = File.createTempFile(var1, ".so");
                  var3.deleteOnExit();
                  FileOutputStream var4 = new FileOutputStream(var3);

                  try {
                     ByteStreams.copy(var2, var4);
                  } catch (Throwable var9) {
                     try {
                        var4.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }

                     throw var9;
                  }

                  var4.close();
                  System.load(var3.getPath());
                  this.loaded = true;
                  System.out.println("Loaded native library: " + this.name);
               } catch (Throwable var11) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var7) {
                        var11.addSuppressed(var7);
                     }
                  }

                  throw var11;
               }

               if (var2 != null) {
                  var2.close();
               }
            } catch (IOException var12) {
            } catch (Exception var13) {
               System.out.println("Could not load native library: " + var13.getMessage());
            }
         }
      }

      return this.loaded;
   }

   public static boolean isSupported() {
      return ("Linux".equals(System.getProperty("os.name")) || "Mac OS X".equals(System.getProperty("os.name")))
         && ("amd64".equals(System.getProperty("os.arch")) || "x86_64".equals(System.getProperty("os.arch")));
   }
}
