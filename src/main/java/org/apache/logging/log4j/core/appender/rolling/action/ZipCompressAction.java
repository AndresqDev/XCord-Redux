package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8192;
   private final File source;
   private final File destination;
   private final boolean deleteSource;
   private final int level;

   public ZipCompressAction(File var1, File var2, boolean var3, int var4) {
      Objects.requireNonNull(var1, "source");
      Objects.requireNonNull(var2, "destination");
      this.source = var1;
      this.destination = var2;
      this.deleteSource = var3;
      this.level = var4;
   }

   @Override
   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource, this.level);
   }

   public static boolean execute(File var0, File var1, boolean var2, int var3) throws IOException {
      if (var0.exists()) {
         try (
            FileInputStream var4 = new FileInputStream(var0);
            ZipOutputStream var6 = new ZipOutputStream(new FileOutputStream(var1));
         ) {
            var6.setLevel(var3);
            ZipEntry var8 = new ZipEntry(var0.getName());
            var6.putNextEntry(var8);
            byte[] var9 = new byte[8192];

            int var10;
            while ((var10 = var4.read(var9)) != -1) {
               var6.write(var9, 0, var10);
            }
         }

         if (var2 && !var0.delete()) {
            LOGGER.warn("Unable to delete " + var0.toString() + '.');
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void reportException(Exception var1) {
      LOGGER.warn("Exception during compression of '" + this.source.toString() + "'.", (Throwable)var1);
   }

   @Override
   public String toString() {
      return ZipCompressAction.class.getSimpleName()
         + '['
         + this.source
         + " to "
         + this.destination
         + ", level="
         + this.level
         + ", deleteSource="
         + this.deleteSource
         + ']';
   }

   public File getSource() {
      return this.source;
   }

   public File getDestination() {
      return this.destination;
   }

   public boolean isDeleteSource() {
      return this.deleteSource;
   }

   public int getLevel() {
      return this.level;
   }
}
