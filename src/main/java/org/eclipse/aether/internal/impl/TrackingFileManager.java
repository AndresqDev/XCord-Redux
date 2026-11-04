package org.eclipse.aether.internal.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingFileManager {
   private static final Logger LOGGER = LoggerFactory.getLogger(TrackingFileManager.class);

   public Properties read(File var1) {
      FileInputStream var2 = null;

      Properties var3;
      try {
         if (var1.exists()) {
            var2 = new FileInputStream(var1);
            var3 = new Properties();
            var3.load(var2);
            return var3;
         }

         var3 = null;
      } catch (IOException var8) {
         LOGGER.warn("Failed to read tracking file {}", var1, var8);
         return null;
      } finally {
         this.close(var2, var1);
      }

      return var3;
   }

   public Properties update(File var1, Map<String, String> var2) {
      Properties var3 = new Properties();
      File var4 = var1.getParentFile();
      if (!var4.mkdirs() && !var4.exists()) {
         LOGGER.warn("Failed to create parent directories for tracking file {}", var1);
         return var3;
      } else {
         RandomAccessFile var5 = null;

         try {
            var5 = new RandomAccessFile(var1, "rw");
            if (var1.canRead()) {
               byte[] var6 = new byte[(int)var5.length()];
               var5.readFully(var6);
               ByteArrayInputStream var7 = new ByteArrayInputStream(var6);
               var3.load(var7);
            }

            for (Entry var15 : var2.entrySet()) {
               if (var15.getValue() == null) {
                  var3.remove(var15.getKey());
               } else {
                  var3.setProperty((String)var15.getKey(), (String)var15.getValue());
               }
            }

            ByteArrayOutputStream var14 = new ByteArrayOutputStream(2048);
            LOGGER.debug("Writing tracking file {}", var1);
            var3.store(var14, "NOTE: This is a Maven Resolver internal implementation file, its format can be changed without prior notice.");
            var5.seek(0L);
            var5.write(var14.toByteArray());
            var5.setLength(var5.getFilePointer());
         } catch (IOException var11) {
            LOGGER.warn("Failed to write tracking file {}", var1, var11);
         } finally {
            this.close(var5, var1);
         }

         return var3;
      }
   }

   private void close(Closeable var1, File var2) {
      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var4) {
            LOGGER.warn("Error closing tracking file {}", var2, var4);
         }
      }
   }
}
