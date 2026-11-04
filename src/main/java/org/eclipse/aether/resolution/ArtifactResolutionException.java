package org.eclipse.aether.resolution;

import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.RepositoryOfflineException;

public class ArtifactResolutionException extends RepositoryException {
   private final transient List<ArtifactResult> results;

   public ArtifactResolutionException(List<ArtifactResult> var1) {
      super(getMessage(var1), getCause(var1));
      this.results = var1 != null ? var1 : Collections.emptyList();
   }

   public ArtifactResolutionException(List<ArtifactResult> var1, String var2) {
      super(var2, getCause(var1));
      this.results = var1 != null ? var1 : Collections.emptyList();
   }

   public ArtifactResolutionException(List<ArtifactResult> var1, String var2, Throwable var3) {
      super(var2, var3);
      this.results = var1 != null ? var1 : Collections.emptyList();
   }

   public List<ArtifactResult> getResults() {
      return this.results;
   }

   public ArtifactResult getResult() {
      return this.results != null && !this.results.isEmpty() ? this.results.get(0) : null;
   }

   private static String getMessage(List<? extends ArtifactResult> var0) {
      StringBuilder var1 = new StringBuilder(256);
      var1.append("The following artifacts could not be resolved: ");
      int var2 = 0;
      String var3 = "";

      for (ArtifactResult var5 : var0) {
         if (!var5.isResolved()) {
            var2++;
            var1.append(var3);
            var1.append(var5.getRequest().getArtifact());
            var3 = ", ";
         }
      }

      Throwable var6 = getCause(var0);
      if (var6 != null) {
         if (var2 == 1) {
            var1.setLength(0);
            var1.append(var6.getMessage());
         } else {
            var1.append(": ").append(var6.getMessage());
         }
      }

      return var1.toString();
   }

   private static Throwable getCause(List<? extends ArtifactResult> var0) {
      for (ArtifactResult var2 : var0) {
         if (!var2.isResolved()) {
            Throwable var3 = null;
            Throwable var4 = null;

            for (Throwable var6 : var2.getExceptions()) {
               if (!(var6 instanceof ArtifactNotFoundException)) {
                  return var6;
               }

               if (var3 == null) {
                  var3 = var6;
               }

               if (var4 == null && var6.getCause() instanceof RepositoryOfflineException) {
                  var4 = var6;
               }
            }

            if (var4 != null) {
               return var4;
            }

            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   }
}
