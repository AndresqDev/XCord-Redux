package org.eclipse.aether.spi.connector;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferListener;

public final class ArtifactDownload extends ArtifactTransfer {
   private boolean existenceCheck;
   private String checksumPolicy = "";
   private String context = "";
   private Collection<String> contexts;
   private List<RemoteRepository> repositories = Collections.emptyList();

   public ArtifactDownload() {
   }

   public ArtifactDownload(Artifact var1, String var2, File var3, String var4) {
      this.setArtifact(var1);
      this.setRequestContext(var2);
      this.setFile(var3);
      this.setChecksumPolicy(var4);
   }

   public ArtifactDownload setArtifact(Artifact var1) {
      super.setArtifact(var1);
      return this;
   }

   public ArtifactDownload setFile(File var1) {
      super.setFile(var1);
      return this;
   }

   public boolean isExistenceCheck() {
      return this.existenceCheck;
   }

   public ArtifactDownload setExistenceCheck(boolean var1) {
      this.existenceCheck = var1;
      return this;
   }

   public String getChecksumPolicy() {
      return this.checksumPolicy;
   }

   public ArtifactDownload setChecksumPolicy(String var1) {
      this.checksumPolicy = var1 != null ? var1 : "";
      return this;
   }

   public String getRequestContext() {
      return this.context;
   }

   public ArtifactDownload setRequestContext(String var1) {
      this.context = var1 != null ? var1 : "";
      return this;
   }

   public Collection<String> getSupportedContexts() {
      return (Collection<String>)(this.contexts != null ? this.contexts : Collections.singleton(this.context));
   }

   public ArtifactDownload setSupportedContexts(Collection<String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.contexts = var1;
      } else {
         this.contexts = Collections.singleton(this.context);
      }

      return this;
   }

   public List<RemoteRepository> getRepositories() {
      return this.repositories;
   }

   public ArtifactDownload setRepositories(List<RemoteRepository> var1) {
      if (var1 == null) {
         this.repositories = Collections.emptyList();
      } else {
         this.repositories = var1;
      }

      return this;
   }

   public ArtifactDownload setException(ArtifactTransferException var1) {
      super.setException(var1);
      return this;
   }

   public ArtifactDownload setListener(TransferListener var1) {
      super.setListener(var1);
      return this;
   }

   public ArtifactDownload setTrace(RequestTrace var1) {
      super.setTrace(var1);
      return this;
   }

   @Override
   public String toString() {
      return this.getArtifact() + " - " + (this.isExistenceCheck() ? "?" : "") + this.getFile();
   }
}
