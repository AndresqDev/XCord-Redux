package org.eclipse.aether.util.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;

public final class ChainedWorkspaceReader implements WorkspaceReader {
   private List<WorkspaceReader> readers = new ArrayList<>();
   private WorkspaceRepository repository;

   public ChainedWorkspaceReader(WorkspaceReader... var1) {
      if (var1 != null) {
         Collections.addAll(this.readers, var1);
      }

      StringBuilder var2 = new StringBuilder();

      for (WorkspaceReader var4 : this.readers) {
         if (var2.length() > 0) {
            var2.append('+');
         }

         var2.append(var4.getRepository().getContentType());
      }

      this.repository = new WorkspaceRepository(var2.toString(), new ChainedWorkspaceReader.Key(this.readers));
   }

   public static WorkspaceReader newInstance(WorkspaceReader var0, WorkspaceReader var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (WorkspaceReader)(var1 == null ? var0 : new ChainedWorkspaceReader(var0, var1));
      }
   }

   @Override
   public File findArtifact(Artifact var1) {
      File var2 = null;

      for (WorkspaceReader var4 : this.readers) {
         var2 = var4.findArtifact(var1);
         if (var2 != null) {
            break;
         }
      }

      return var2;
   }

   @Override
   public List<String> findVersions(Artifact var1) {
      LinkedHashSet var2 = new LinkedHashSet();

      for (WorkspaceReader var4 : this.readers) {
         var2.addAll(var4.findVersions(var1));
      }

      return Collections.unmodifiableList(new ArrayList<>(var2));
   }

   @Override
   public WorkspaceRepository getRepository() {
      ChainedWorkspaceReader.Key var1 = new ChainedWorkspaceReader.Key(this.readers);
      if (!var1.equals(this.repository.getKey())) {
         this.repository = new WorkspaceRepository(this.repository.getContentType(), var1);
      }

      return this.repository;
   }

   private static class Key {
      private final List<Object> keys = new ArrayList<>();

      Key(List<WorkspaceReader> var1) {
         for (WorkspaceReader var3 : var1) {
            this.keys.add(var3.getRepository().getKey());
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 != null && this.getClass().equals(var1.getClass()) ? this.keys.equals(((ChainedWorkspaceReader.Key)var1).keys) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.keys.hashCode();
      }
   }
}
