package joptsimple.util;

import java.nio.file.Files;
import java.nio.file.Path;

public enum PathProperties {
   FILE_EXISTING("file.existing") {
      @Override
      boolean accept(Path var1) {
         return Files.isRegularFile(var1);
      }
   },
   DIRECTORY_EXISTING("directory.existing") {
      @Override
      boolean accept(Path var1) {
         return Files.isDirectory(var1);
      }
   },
   NOT_EXISTING("file.not.existing") {
      @Override
      boolean accept(Path var1) {
         return Files.notExists(var1);
      }
   },
   FILE_OVERWRITABLE("file.overwritable") {
      @Override
      boolean accept(Path var1) {
         return FILE_EXISTING.accept(var1) && WRITABLE.accept(var1);
      }
   },
   READABLE("file.readable") {
      @Override
      boolean accept(Path var1) {
         return Files.isReadable(var1);
      }
   },
   WRITABLE("file.writable") {
      @Override
      boolean accept(Path var1) {
         return Files.isWritable(var1);
      }
   };

   private final String messageKey;

   private PathProperties(String var3) {
      this.messageKey = var3;
   }

   abstract boolean accept(Path var1);

   String getMessageKey() {
      return this.messageKey;
   }
}
