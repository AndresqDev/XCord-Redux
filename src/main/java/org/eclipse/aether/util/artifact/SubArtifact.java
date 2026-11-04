package org.eclipse.aether.util.artifact;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

public final class SubArtifact extends AbstractArtifact {
   private final Artifact mainArtifact;
   private final String classifier;
   private final String extension;
   private final File file;
   private final Map<String, String> properties;

   public SubArtifact(Artifact var1, String var2, String var3) {
      this(var1, var2, var3, (File)null);
   }

   public SubArtifact(Artifact var1, String var2, String var3, File var4) {
      this(var1, var2, var3, null, var4);
   }

   public SubArtifact(Artifact var1, String var2, String var3, Map<String, String> var4) {
      this(var1, var2, var3, var4, null);
   }

   public SubArtifact(Artifact var1, String var2, String var3, Map<String, String> var4, File var5) {
      this.mainArtifact = Objects.requireNonNull(var1, "main artifact cannot be null");
      this.classifier = var2;
      this.extension = var3;
      this.file = var5;
      this.properties = copyProperties(var4);
   }

   private SubArtifact(Artifact var1, String var2, String var3, File var4, Map<String, String> var5) {
      this.mainArtifact = var1;
      this.classifier = var2;
      this.extension = var3;
      this.file = var4;
      this.properties = var5;
   }

   @Override
   public String getGroupId() {
      return this.mainArtifact.getGroupId();
   }

   @Override
   public String getArtifactId() {
      return this.mainArtifact.getArtifactId();
   }

   @Override
   public String getVersion() {
      return this.mainArtifact.getVersion();
   }

   @Override
   public String getBaseVersion() {
      return this.mainArtifact.getBaseVersion();
   }

   @Override
   public boolean isSnapshot() {
      return this.mainArtifact.isSnapshot();
   }

   @Override
   public String getClassifier() {
      return expand(this.classifier, this.mainArtifact.getClassifier());
   }

   @Override
   public String getExtension() {
      return expand(this.extension, this.mainArtifact.getExtension());
   }

   @Override
   public File getFile() {
      return this.file;
   }

   @Override
   public Artifact setFile(File var1) {
      return Objects.equals(this.file, var1) ? this : new SubArtifact(this.mainArtifact, this.classifier, this.extension, var1, this.properties);
   }

   @Override
   public Map<String, String> getProperties() {
      return this.properties;
   }

   @Override
   public Artifact setProperties(Map<String, String> var1) {
      return !this.properties.equals(var1) && (var1 != null || !this.properties.isEmpty())
         ? new SubArtifact(this.mainArtifact, this.classifier, this.extension, var1, this.file)
         : this;
   }

   private static String expand(String var0, String var1) {
      String var2 = "";
      if (var0 != null) {
         var2 = var0.replace("*", var1);
         if (var1.length() <= 0) {
            if (var0.startsWith("*")) {
               int var3;
               for (var3 = 0; var3 < var2.length(); var3++) {
                  char var4 = var2.charAt(var3);
                  if (var4 != '-' && var4 != '.') {
                     break;
                  }
               }

               var2 = var2.substring(var3);
            }

            if (var0.endsWith("*")) {
               int var5;
               for (var5 = var2.length() - 1; var5 >= 0; var5--) {
                  char var6 = var2.charAt(var5);
                  if (var6 != '-' && var6 != '.') {
                     break;
                  }
               }

               var2 = var2.substring(0, var5 + 1);
            }
         }
      }

      return var2;
   }
}
