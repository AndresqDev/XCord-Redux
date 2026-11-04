package org.apache.maven.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileSource implements Source {
   private final File file;

   public FileSource(File var1) {
      this.file = Objects.requireNonNull(var1, "file cannot be null").getAbsoluteFile();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return new FileInputStream(this.file);
   }

   @Override
   public String getLocation() {
      return this.file.getPath();
   }

   public File getFile() {
      return this.file;
   }

   @Override
   public String toString() {
      return this.getLocation();
   }
}
