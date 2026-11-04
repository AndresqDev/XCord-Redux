package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public final class CommonsCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8192;
   private final String name;
   private final File source;
   private final File destination;
   private final boolean deleteSource;

   public CommonsCompressAction(String var1, File var2, File var3, boolean var4) {
      Objects.requireNonNull(var2, "source");
      Objects.requireNonNull(var3, "destination");
      this.name = var1;
      this.source = var2;
      this.destination = var3;
      this.deleteSource = var4;
   }

   @Override
   public boolean execute() throws IOException {
      return execute(this.name, this.source, this.destination, this.deleteSource);
   }

   public static boolean execute(String var0, File var1, File var2, boolean var3) throws IOException {
      if (!var1.exists()) {
         return false;
      } else {
         LOGGER.debug("Starting {} compression of {}", var0, var1.getPath());

         try (
            FileInputStream var4 = new FileInputStream(var1);
            BufferedOutputStream var6 = new BufferedOutputStream(new CompressorStreamFactory().createCompressorOutputStream(var0, new FileOutputStream(var2)));
         ) {
            IOUtils.copy(var4, var6, 8192);
            LOGGER.debug("Finished {} compression of {}", var0, var1.getPath());
         } catch (CompressorException var39) {
            throw new IOException(var39);
         }

         if (var3) {
            try {
               if (Files.deleteIfExists(var1.toPath())) {
                  LOGGER.debug("Deleted {}", var1.toString());
               } else {
                  LOGGER.warn("Unable to delete {} after {} compression. File did not exist", var1.toString(), var0);
               }
            } catch (Exception var34) {
               LOGGER.warn("Unable to delete {} after {} compression, {}", var1.toString(), var0, var34.getMessage());
            }
         }

         return true;
      }
   }

   @Override
   protected void reportException(Exception var1) {
      LOGGER.warn("Exception during " + this.name + " compression of '" + this.source.toString() + "'.", (Throwable)var1);
   }

   @Override
   public String toString() {
      return CommonsCompressAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", deleteSource=" + this.deleteSource + ']';
   }

   public String getName() {
      return this.name;
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
}
