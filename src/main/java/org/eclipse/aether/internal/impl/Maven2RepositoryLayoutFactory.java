package org.eclipse.aether.internal.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.util.ConfigUtils;
import xcord.Named;

@Named("maven2")
public final class Maven2RepositoryLayoutFactory implements RepositoryLayoutFactory {
   static final String CONFIG_PROP_SIGNATURE_CHECKSUMS = "aether.checksums.forSignature";
   static final String CONFIG_PROP_CHECKSUMS_ALGORITHMS = "aether.checksums.algorithms";
   static final String DEFAULT_CHECKSUMS_ALGORITHMS = "SHA-1,MD5";
   private float priority;

   @Override
   public float getPriority() {
      return this.priority;
   }

   public Maven2RepositoryLayoutFactory setPriority(float var1) {
      this.priority = var1;
      return this;
   }

   @Override
   public RepositoryLayout newInstance(RepositorySystemSession var1, RemoteRepository var2) throws NoRepositoryLayoutException {
      if (!"default".equals(var2.getContentType())) {
         throw new NoRepositoryLayoutException(var2);
      } else {
         boolean var3 = ConfigUtils.getBoolean(var1, false, "aether.checksums.forSignature");
         List var4 = Arrays.asList(ConfigUtils.getString(var1, "SHA-1,MD5", "aether.checksums.algorithms").split(","));
         return (RepositoryLayout)(var3
            ? new Maven2RepositoryLayoutFactory.Maven2RepositoryLayout(var4)
            : new Maven2RepositoryLayoutFactory.Maven2RepositoryLayoutEx(var4));
      }
   }

   private static class Maven2RepositoryLayout implements RepositoryLayout {
      private final List<String> checksumsAlgorithms;

      protected Maven2RepositoryLayout(List<String> var1) {
         this.checksumsAlgorithms = var1;
      }

      private URI toUri(String var1) {
         try {
            return new URI(null, null, var1, null);
         } catch (URISyntaxException var3) {
            throw new IllegalStateException(var3);
         }
      }

      @Override
      public URI getLocation(Artifact var1, boolean var2) {
         StringBuilder var3 = new StringBuilder(128);
         var3.append(var1.getGroupId().replace('.', '/')).append('/');
         var3.append(var1.getArtifactId()).append('/');
         var3.append(var1.getBaseVersion()).append('/');
         var3.append(var1.getArtifactId()).append('-').append(var1.getVersion());
         if (var1.getClassifier().length() > 0) {
            var3.append('-').append(var1.getClassifier());
         }

         if (var1.getExtension().length() > 0) {
            var3.append('.').append(var1.getExtension());
         }

         return this.toUri(var3.toString());
      }

      @Override
      public URI getLocation(Metadata var1, boolean var2) {
         StringBuilder var3 = new StringBuilder(128);
         if (var1.getGroupId().length() > 0) {
            var3.append(var1.getGroupId().replace('.', '/')).append('/');
            if (var1.getArtifactId().length() > 0) {
               var3.append(var1.getArtifactId()).append('/');
               if (var1.getVersion().length() > 0) {
                  var3.append(var1.getVersion()).append('/');
               }
            }
         }

         var3.append(var1.getType());
         return this.toUri(var3.toString());
      }

      @Override
      public List<RepositoryLayout.Checksum> getChecksums(Artifact var1, boolean var2, URI var3) {
         return this.getChecksums(var3);
      }

      @Override
      public List<RepositoryLayout.Checksum> getChecksums(Metadata var1, boolean var2, URI var3) {
         return this.getChecksums(var3);
      }

      private List<RepositoryLayout.Checksum> getChecksums(URI var1) {
         ArrayList var2 = new ArrayList(this.checksumsAlgorithms.size());

         for (String var4 : this.checksumsAlgorithms) {
            var2.add(RepositoryLayout.Checksum.forLocation(var1, var4));
         }

         return var2;
      }
   }

   private static class Maven2RepositoryLayoutEx extends Maven2RepositoryLayoutFactory.Maven2RepositoryLayout {
      protected Maven2RepositoryLayoutEx(List<String> var1) {
         super(var1);
      }

      @Override
      public List<RepositoryLayout.Checksum> getChecksums(Artifact var1, boolean var2, URI var3) {
         return this.isSignature(var1.getExtension()) ? Collections.emptyList() : super.getChecksums(var1, var2, var3);
      }

      private boolean isSignature(String var1) {
         return var1.endsWith(".asc");
      }
   }
}
