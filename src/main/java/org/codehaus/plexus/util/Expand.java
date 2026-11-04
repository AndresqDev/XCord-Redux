package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Expand {
   private File dest;
   private File source;
   private boolean overwrite = true;

   public void execute() throws Exception {
      this.expandFile(this.source, this.dest);
   }

   protected void expandFile(File var1, File var2) throws Exception {
      ZipInputStream var3 = null;

      try {
         var3 = new ZipInputStream(new FileInputStream(var1));

         for (ZipEntry var4 = var3.getNextEntry(); var4 != null; var4 = var3.getNextEntry()) {
            this.extractFile(var1, var2, var3, var4.getName(), new Date(var4.getTime()), var4.isDirectory());
         }

         var3.close();
         var3 = null;
      } catch (IOException var8) {
         throw new Exception("Error while expanding " + var1.getPath(), var8);
      } finally {
         IOUtil.close(var3);
      }
   }

   protected void extractFile(File var1, File var2, InputStream var3, String var4, Date var5, boolean var6) throws Exception {
      File var7 = FileUtils.resolveFile(var2, var4);
      if (!var7.getAbsolutePath().startsWith(var2.getAbsolutePath())) {
         throw new IOException("Entry '" + var4 + "' outside the target directory.");
      } else {
         try {
            if (this.overwrite || !var7.exists() || var7.lastModified() < var5.getTime()) {
               File var8 = var7.getParentFile();
               var8.mkdirs();
               if (var6) {
                  var7.mkdirs();
               } else {
                  byte[] var9 = new byte[65536];
                  FileOutputStream var10 = null;

                  try {
                     var10 = new FileOutputStream(var7);

                     for (int var11 = var3.read(var9); var11 >= 0; var11 = var3.read(var9)) {
                        var10.write(var9, 0, var11);
                     }

                     var10.close();
                     var10 = null;
                  } finally {
                     IOUtil.close(var10);
                  }
               }

               var7.setLastModified(var5.getTime());
            }
         } catch (FileNotFoundException var16) {
            throw new Exception("Can't extract file " + var1.getPath(), var16);
         }
      }
   }

   public void setDest(File var1) {
      this.dest = var1;
   }

   public void setSrc(File var1) {
      this.source = var1;
   }

   public void setOverwrite(boolean var1) {
      this.overwrite = var1;
   }
}
