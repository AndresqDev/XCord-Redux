package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileRenameAction extends AbstractAction {
   private final File source;
   private final File destination;
   private final boolean renameEmptyFiles;

   public FileRenameAction(File var1, File var2, boolean var3) {
      this.source = var1;
      this.destination = var2;
      this.renameEmptyFiles = var3;
   }

   @Override
   public boolean execute() {
      return execute(this.source, this.destination, this.renameEmptyFiles);
   }

   public File getDestination() {
      return this.destination;
   }

   public File getSource() {
      return this.source;
   }

   public boolean isRenameEmptyFiles() {
      return this.renameEmptyFiles;
   }

   public static boolean execute(File var0, File var1, boolean var2) {
      if (!var2 && var0.length() <= 0L) {
         try {
            var0.delete();
         } catch (Exception var11) {
            LOGGER.error("Unable to delete empty file {}: {} {}", var0.getAbsolutePath(), var11.getClass().getName(), var11.getMessage());
         }
      } else {
         File var3 = var1.getParentFile();
         if (var3 != null && !var3.exists()) {
            var3.mkdirs();
            if (!var3.exists()) {
               LOGGER.error("Unable to create directory {}", var3.getAbsolutePath());
               return false;
            }
         }

         try {
            try {
               return moveFile(Paths.get(var0.getAbsolutePath()), Paths.get(var1.getAbsolutePath()));
            } catch (IOException var12) {
               LOGGER.debug(
                  "Unable to move file {} to {}: {} {} - will try to copy and delete",
                  var0.getAbsolutePath(),
                  var1.getAbsolutePath(),
                  var12.getClass().getName(),
                  var12.getMessage()
               );
               boolean var5 = var0.renameTo(var1);
               if (!var5) {
                  try {
                     Files.copy(Paths.get(var0.getAbsolutePath()), Paths.get(var1.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

                     try {
                        Files.delete(Paths.get(var0.getAbsolutePath()));
                        var5 = true;
                        LOGGER.trace("Renamed file {} to {} using copy and delete", var0.getAbsolutePath(), var1.getAbsolutePath());
                     } catch (IOException var9) {
                        LOGGER.error("Unable to delete file {}: {} {}", var0.getAbsolutePath(), var9.getClass().getName(), var9.getMessage());

                        try {
                           var5 = true;
                           new PrintWriter(var0.getAbsolutePath()).close();
                           LOGGER.trace("Renamed file {} to {} with copy and truncation", var0.getAbsolutePath(), var1.getAbsolutePath());
                        } catch (IOException var8) {
                           LOGGER.error("Unable to overwrite file {}: {} {}", var0.getAbsolutePath(), var8.getClass().getName(), var8.getMessage());
                        }
                     }
                  } catch (IOException var10) {
                     LOGGER.error(
                        "Unable to copy file {} to {}: {} {}", var0.getAbsolutePath(), var1.getAbsolutePath(), var10.getClass().getName(), var10.getMessage()
                     );
                  }
               } else {
                  LOGGER.trace("Renamed file {} to {} with source.renameTo", var0.getAbsolutePath(), var1.getAbsolutePath());
               }

               return var5;
            }
         } catch (RuntimeException var13) {
            LOGGER.error(
               "Unable to rename file {} to {}: {} {}", var0.getAbsolutePath(), var1.getAbsolutePath(), var13.getClass().getName(), var13.getMessage()
            );
         }
      }

      return false;
   }

   private static boolean moveFile(Path var0, Path var1) throws IOException {
      try {
         Files.move(var0, var1, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         LOGGER.trace("Renamed file {} to {} with Files.move", var0.toFile().getAbsolutePath(), var1.toFile().getAbsolutePath());
         return true;
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(var0, var1, StandardCopyOption.REPLACE_EXISTING);
         LOGGER.trace("Renamed file {} to {} with Files.move", var0.toFile().getAbsolutePath(), var1.toFile().getAbsolutePath());
         return true;
      }
   }

   @Override
   public String toString() {
      return FileRenameAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", renameEmptyFiles=" + this.renameEmptyFiles + ']';
   }
}
