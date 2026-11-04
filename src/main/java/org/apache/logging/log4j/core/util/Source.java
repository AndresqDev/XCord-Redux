package org.apache.logging.log4j.core.util;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import org.apache.logging.log4j.core.config.ConfigurationSource;

public class Source {
   private final File file;
   private final URI uri;
   private final String location;

   public Source(ConfigurationSource var1) {
      this.file = var1.getFile();
      this.uri = var1.getURI();
      this.location = var1.getLocation();
   }

   public Source(File var1) {
      this.file = Objects.requireNonNull(var1, "file is null");
      this.location = var1.getAbsolutePath();
      this.uri = null;
   }

   public Source(URI var1, long var2) {
      this.uri = Objects.requireNonNull(var1, "URI is null");
      this.location = var1.toString();
      this.file = null;
   }

   public File getFile() {
      return this.file;
   }

   public URI getURI() {
      return this.uri;
   }

   public String getLocation() {
      return this.location;
   }

   @Override
   public String toString() {
      return this.location;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Source)) {
         return false;
      } else {
         Source var2 = (Source)var1;
         return Objects.equals(this.location, var2.location);
      }
   }

   @Override
   public int hashCode() {
      return 31 + Objects.hashCode(this.location);
   }
}
