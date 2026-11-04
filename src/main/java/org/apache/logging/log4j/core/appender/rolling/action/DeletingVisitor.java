package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class DeletingVisitor extends SimpleFileVisitor<Path> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Path basePath;
   private final boolean testMode;
   private final List<? extends PathCondition> pathConditions;

   public DeletingVisitor(Path var1, List<? extends PathCondition> var2, boolean var3) {
      this.testMode = var3;
      this.basePath = Objects.requireNonNull(var1, "basePath");
      this.pathConditions = Objects.requireNonNull(var2, "pathConditions");

      for (PathCondition var5 : var2) {
         var5.beforeFileTreeWalk();
      }
   }

   public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) throws IOException {
      for (PathCondition var4 : this.pathConditions) {
         Path var5 = this.basePath.relativize(var1);
         if (!var4.accept(this.basePath, var5, var2)) {
            LOGGER.trace("Not deleting base={}, relative={}", this.basePath, var5);
            return FileVisitResult.CONTINUE;
         }
      }

      if (this.isTestMode()) {
         LOGGER.info("Deleting {} (TEST MODE: file not actually deleted)", var1);
      } else {
         this.delete(var1);
      }

      return FileVisitResult.CONTINUE;
   }

   public FileVisitResult visitFileFailed(Path var1, IOException var2) throws IOException {
      if (var2 instanceof NoSuchFileException) {
         LOGGER.info("File {} could not be accessed, it has likely already been deleted", var1, var2);
         return FileVisitResult.CONTINUE;
      } else {
         return super.visitFileFailed(var1, var2);
      }
   }

   protected void delete(Path var1) throws IOException {
      LOGGER.trace("Deleting {}", var1);
      Files.deleteIfExists(var1);
   }

   public boolean isTestMode() {
      return this.testMode;
   }
}
