package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public final class GzCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8192;
   private final File source;
   private final File destination;
   private final boolean deleteSource;
   private final int compressionLevel;

   public GzCompressAction(File var1, File var2, boolean var3, int var4) {
      Objects.requireNonNull(var1, "source");
      Objects.requireNonNull(var2, "destination");
      this.source = var1;
      this.destination = var2;
      this.deleteSource = var3;
      this.compressionLevel = var4;
   }

   @Deprecated
   public GzCompressAction(File var1, File var2, boolean var3) {
      this(var1, var2, var3, -1);
   }

   @Override
   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource, this.compressionLevel);
   }

   @Deprecated
   public static boolean execute(File var0, File var1, boolean var2) throws IOException {
      return execute(var0, var1, var2, -1);
   }

   public static boolean execute(File var0, File var1, boolean var2, int var3) throws IOException {
      if (var0.exists()) {
         try (
            FileInputStream var4 = new FileInputStream(var0);
            FileOutputStream var6 = new FileOutputStream(var1);
            GzCompressAction.ConfigurableLevelGZIPOutputStream var8 = new GzCompressAction.ConfigurableLevelGZIPOutputStream(var6, 8192, var3);
            BufferedOutputStream var10 = new BufferedOutputStream(var8, 8192);
         ) {
            byte[] var12 = new byte[8192];

            int var13;
            while ((var13 = var4.read(var12)) != -1) {
               var10.write(var12, 0, var13);
            }
         }

         if (var2 && !var0.delete()) {
            LOGGER.warn("Unable to delete {}.", var0);
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
      return GzCompressAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", deleteSource=" + this.deleteSource + ']';
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

   private static final class ConfigurableLevelGZIPOutputStream extends GZIPOutputStream {
      ConfigurableLevelGZIPOutputStream(OutputStream var1, int var2, int var3) throws IOException {
         super(var1, var2);
         this.def.setLevel(var3);
      }
   }
}
