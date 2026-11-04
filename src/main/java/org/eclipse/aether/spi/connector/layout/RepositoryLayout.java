package org.eclipse.aether.spi.connector.layout;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface RepositoryLayout {
   URI getLocation(Artifact var1, boolean var2);

   URI getLocation(Metadata var1, boolean var2);

   List<RepositoryLayout.Checksum> getChecksums(Artifact var1, boolean var2, URI var3);

   List<RepositoryLayout.Checksum> getChecksums(Metadata var1, boolean var2, URI var3);

   public static final class Checksum {
      private final String algorithm;
      private final URI location;

      public Checksum(String var1, URI var2) {
         verify(var1, var2);
         this.algorithm = var1;
         this.location = var2;
      }

      public static RepositoryLayout.Checksum forLocation(URI var0, String var1) {
         verify(var1, var0);
         if (var0.getRawQuery() != null) {
            throw new IllegalArgumentException("resource location must not have query parameters: " + var0);
         } else if (var0.getRawFragment() != null) {
            throw new IllegalArgumentException("resource location must not have a fragment: " + var0);
         } else {
            String var2 = '.' + var1.replace("-", "").toLowerCase(Locale.ENGLISH);
            return new RepositoryLayout.Checksum(var1, URI.create(var0.toString() + var2));
         }
      }

      private static void verify(String var0, URI var1) {
         Objects.requireNonNull(var0, "checksum algorithm cannot be null");
         if (var0.length() == 0) {
            throw new IllegalArgumentException("checksum algorithm cannot be empty");
         } else {
            Objects.requireNonNull(var1, "checksum location cannot be null");
            if (var1.isAbsolute()) {
               throw new IllegalArgumentException("checksum location must be relative");
            }
         }
      }

      public String getAlgorithm() {
         return this.algorithm;
      }

      public URI getLocation() {
         return this.location;
      }

      @Override
      public String toString() {
         return this.location + " (" + this.algorithm + ")";
      }
   }
}
