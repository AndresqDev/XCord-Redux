package org.eclipse.aether.internal.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.aether.spi.io.FileProcessor;
import xcord.Named;

@Named
public class DefaultFileProcessor implements FileProcessor {
   @Override
   public boolean mkdirs(File var1) {
      if (var1 == null) {
         return false;
      } else if (var1.exists()) {
         return false;
      } else if (var1.mkdir()) {
         return true;
      } else {
         File var2;
         try {
            var2 = var1.getCanonicalFile();
         } catch (IOException var4) {
            return false;
         }

         File var3 = var2.getParentFile();
         return var3 != null && (this.mkdirs(var3) || var3.exists()) && var2.mkdir();
      }
   }

   @Override
   public void write(File var1, String var2) throws IOException {
      this.mkdirs(var1.getAbsoluteFile().getParentFile());
      FileOutputStream var3 = null;

      try {
         var3 = new FileOutputStream(var1);
         if (var2 != null) {
            var3.write(var2.getBytes(StandardCharsets.UTF_8));
         }

         var3.close();
         var3 = null;
      } finally {
         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var10) {
         }
      }
   }

   @Override
   public void write(File var1, InputStream var2) throws IOException {
      this.mkdirs(var1.getAbsoluteFile().getParentFile());
      FileOutputStream var3 = null;

      try {
         var3 = new FileOutputStream(var1);
         this.copy(var3, var2, null);
         var3.close();
         var3 = null;
      } finally {
         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var10) {
         }
      }
   }

   @Override
   public void copy(File var1, File var2) throws IOException {
      this.copy(var1, var2, null);
   }

   @Override
   public long copy(File var1, File var2, FileProcessor.ProgressListener var3) throws IOException {
      long var4 = 0L;
      FileInputStream var6 = null;
      FileOutputStream var7 = null;

      try {
         var6 = new FileInputStream(var1);
         this.mkdirs(var2.getAbsoluteFile().getParentFile());
         var7 = new FileOutputStream(var2);
         var4 = this.copy(var7, var6, var3);
         var7.close();
         var7 = null;
         var6.close();
         var6 = null;
      } finally {
         try {
            if (var7 != null) {
               var7.close();
            }
         } catch (IOException var43) {
         } finally {
            try {
               if (var6 != null) {
                  var6.close();
               }
            } catch (IOException var42) {
            }
         }
      }

      return var4;
   }

   private long copy(OutputStream var1, InputStream var2, FileProcessor.ProgressListener var3) throws IOException {
      long var4 = 0L;
      ByteBuffer var6 = ByteBuffer.allocate(32768);
      byte[] var7 = var6.array();

      while (true) {
         int var8 = var2.read(var7);
         if (var8 < 0) {
            return var4;
         }

         var1.write(var7, 0, var8);
         var4 += (long)var8;
         if (var3 != null && var8 > 0) {
            try {
               ((Buffer)var6).rewind();
               ((Buffer)var6).limit(var8);
               var3.progressed(var6);
            } catch (Exception var10) {
            }
         }
      }
   }

   @Override
   public void move(File var1, File var2) throws IOException {
      if (!var1.renameTo(var2)) {
         this.copy(var1, var2);
         var2.setLastModified(var1.lastModified());
         var1.delete();
      }
   }
}
