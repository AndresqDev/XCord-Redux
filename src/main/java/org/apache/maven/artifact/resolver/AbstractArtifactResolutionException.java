package org.apache.maven.artifact.resolver;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;

public class AbstractArtifactResolutionException extends Exception {
   private String groupId;
   private String artifactId;
   private String version;
   private String type;
   private String classifier;
   private Artifact artifact;
   private List<ArtifactRepository> remoteRepositories;
   private final String originalMessage;
   private final String path;
   static final String LS = System.lineSeparator();

   protected AbstractArtifactResolutionException(
      String var1, String var2, String var3, String var4, String var5, String var6, List<ArtifactRepository> var7, List<String> var8
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, null);
   }

   protected AbstractArtifactResolutionException(
      String var1, String var2, String var3, String var4, String var5, String var6, List<ArtifactRepository> var7, List<String> var8, Throwable var9
   ) {
      super(constructMessageBase(var1, var2, var3, var4, var5, var7, var8), var9);
      this.originalMessage = var1;
      this.groupId = var2;
      this.artifactId = var3;
      this.type = var5;
      this.classifier = var6;
      this.version = var4;
      this.remoteRepositories = var7;
      this.path = constructArtifactPath(var8, "");
   }

   protected AbstractArtifactResolutionException(String var1, Artifact var2) {
      this(var1, var2, null);
   }

   protected AbstractArtifactResolutionException(String var1, Artifact var2, List<ArtifactRepository> var3) {
      this(var1, var2, var3, null);
   }

   protected AbstractArtifactResolutionException(String var1, Artifact var2, List<ArtifactRepository> var3, Throwable var4) {
      this(var1, var2.getGroupId(), var2.getArtifactId(), var2.getVersion(), var2.getType(), var2.getClassifier(), var3, var2.getDependencyTrail(), var4);
      this.artifact = var2;
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getVersion() {
      return this.version;
   }

   public String getType() {
      return this.type;
   }

   public String getClassifier() {
      return this.classifier;
   }

   public String getPath() {
      return this.path;
   }

   public List<ArtifactRepository> getRemoteRepositories() {
      return this.remoteRepositories;
   }

   public String getOriginalMessage() {
      return this.originalMessage;
   }

   protected static String constructArtifactPath(List<String> var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      if (var0 != null) {
         var2.append(LS);
         var2.append(var1);
         var2.append("Path to dependency: ");
         var2.append(LS);
         int var3 = 1;

         for (Iterator var4 = var0.iterator(); var4.hasNext(); var3++) {
            var2.append(var1);
            var2.append('\t');
            var2.append(var3);
            var2.append(") ");
            var2.append((String)var4.next());
            var2.append(LS);
         }
      }

      return var2.toString();
   }

   private static String constructMessageBase(String var0, String var1, String var2, String var3, String var4, List<ArtifactRepository> var5, List<String> var6) {
      StringBuilder var7 = new StringBuilder();
      var7.append(var0);
      if (var0 == null || !var0.contains("from the specified remote repositories:")) {
         var7.append(LS);
         var7.append("  ").append(var1).append(':').append(var2).append(':').append(var4).append(':').append(var3);
         var7.append(LS);
         if (var5 != null) {
            var7.append(LS);
            var7.append("from the specified remote repositories:");
            var7.append(LS).append("  ");
            if (var5.isEmpty()) {
               var7.append("(none)");
            }

            Iterator var8 = var5.iterator();

            while (var8.hasNext()) {
               ArtifactRepository var9 = (ArtifactRepository)var8.next();
               var7.append(var9.getId());
               var7.append(" (");
               var7.append(var9.getUrl());
               ArtifactRepositoryPolicy var10 = var9.getReleases();
               if (var10 != null) {
                  var7.append(", releases=").append(var10.isEnabled());
               }

               ArtifactRepositoryPolicy var11 = var9.getSnapshots();
               if (var11 != null) {
                  var7.append(", snapshots=").append(var11.isEnabled());
               }

               var7.append(')');
               if (var8.hasNext()) {
                  var7.append(',').append(LS).append("  ");
               }
            }
         }

         var7.append(constructArtifactPath(var6, ""));
         var7.append(LS);
      }

      return var7.toString();
   }

   protected static String constructMissingArtifactMessage(
      String var0, String var1, String var2, String var3, String var4, String var5, String var6, String var7, List<String> var8
   ) {
      StringBuilder var9 = new StringBuilder(var0);
      if (!"pom".equals(var5)) {
         if (var7 != null) {
            var9.append(LS);
            var9.append(LS);
            var9.append(var1);
            var9.append("Try downloading the file manually from: ");
            var9.append(LS);
            var9.append(var1);
            var9.append("    ");
            var9.append(var7);
         } else {
            var9.append(LS);
            var9.append(LS);
            var9.append(var1);
            var9.append("Try downloading the file manually from the project website.");
         }

         var9.append(LS);
         var9.append(LS);
         var9.append(var1);
         var9.append("Then, install it using the command: ");
         var9.append(LS);
         var9.append(var1);
         var9.append("    mvn install:install-file -DgroupId=");
         var9.append(var2);
         var9.append(" -DartifactId=");
         var9.append(var3);
         var9.append(" -Dversion=");
         var9.append(var4);
         if (var6 != null && !var6.equals("")) {
            var9.append(" -Dclassifier=");
            var9.append(var6);
         }

         var9.append(" -Dpackaging=");
         var9.append(var5);
         var9.append(" -Dfile=/path/to/file");
         var9.append(LS);
         var9.append(LS);
         var9.append(var1);
         var9.append("Alternatively, if you host your own repository you can deploy the file there: ");
         var9.append(LS);
         var9.append(var1);
         var9.append("    mvn deploy:deploy-file -DgroupId=");
         var9.append(var2);
         var9.append(" -DartifactId=");
         var9.append(var3);
         var9.append(" -Dversion=");
         var9.append(var4);
         if (var6 != null && !var6.equals("")) {
            var9.append(" -Dclassifier=");
            var9.append(var6);
         }

         var9.append(" -Dpackaging=");
         var9.append(var5);
         var9.append(" -Dfile=/path/to/file");
         var9.append(" -Durl=[url] -DrepositoryId=[id]");
         var9.append(LS);
      }

      var9.append(constructArtifactPath(var8, var1));
      var9.append(LS);
      return var9.toString();
   }

   public String getArtifactPath() {
      return this.path;
   }
}
