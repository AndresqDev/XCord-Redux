package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transform.FileTransformer;

public final class ArtifactUpload extends ArtifactTransfer {
   private FileTransformer fileTransformer;

   public ArtifactUpload() {
   }

   public ArtifactUpload(Artifact var1, File var2) {
      this.setArtifact(var1);
      this.setFile(var2);
   }

   public ArtifactUpload(Artifact var1, File var2, FileTransformer var3) {
      this.setArtifact(var1);
      this.setFile(var2);
      this.setFileTransformer(var3);
   }

   public ArtifactUpload setArtifact(Artifact var1) {
      super.setArtifact(var1);
      return this;
   }

   public ArtifactUpload setFile(File var1) {
      super.setFile(var1);
      return this;
   }

   public ArtifactUpload setException(ArtifactTransferException var1) {
      super.setException(var1);
      return this;
   }

   public ArtifactUpload setListener(TransferListener var1) {
      super.setListener(var1);
      return this;
   }

   public ArtifactUpload setTrace(RequestTrace var1) {
      super.setTrace(var1);
      return this;
   }

   public ArtifactUpload setFileTransformer(FileTransformer var1) {
      this.fileTransformer = var1;
      return this;
   }

   public FileTransformer getFileTransformer() {
      return this.fileTransformer;
   }

   @Override
   public String toString() {
      return this.getFileTransformer() != null
         ? this.getArtifact() + " >>> " + this.getFileTransformer().transformArtifact(this.getArtifact()) + " - " + this.getFile()
         : this.getArtifact() + " - " + this.getFile();
   }
}
