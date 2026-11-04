package org.eclipse.aether.artifact;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractArtifact implements Artifact {
   private static final String SNAPSHOT = "SNAPSHOT";
   private static final Pattern SNAPSHOT_TIMESTAMP = Pattern.compile("^(.*-)?([0-9]{8}\\.[0-9]{6}-[0-9]+)$");

   @Override
   public boolean isSnapshot() {
      return isSnapshot(this.getVersion());
   }

   private static boolean isSnapshot(String var0) {
      return var0.endsWith("SNAPSHOT") || SNAPSHOT_TIMESTAMP.matcher(var0).matches();
   }

   @Override
   public String getBaseVersion() {
      return toBaseVersion(this.getVersion());
   }

   private static String toBaseVersion(String var0) {
      String var1;
      if (var0 == null) {
         var1 = var0;
      } else if (!var0.startsWith("[") && !var0.startsWith("(")) {
         Matcher var2 = SNAPSHOT_TIMESTAMP.matcher(var0);
         if (var2.matches()) {
            if (var2.group(1) != null) {
               var1 = var2.group(1) + "SNAPSHOT";
            } else {
               var1 = "SNAPSHOT";
            }
         } else {
            var1 = var0;
         }
      } else {
         var1 = var0;
      }

      return var1;
   }

   private Artifact newInstance(String var1, Map<String, String> var2, File var3) {
      return new DefaultArtifact(this.getGroupId(), this.getArtifactId(), this.getClassifier(), this.getExtension(), var1, var3, var2);
   }

   @Override
   public Artifact setVersion(String var1) {
      String var2 = this.getVersion();
      return (Artifact)(!var2.equals(var1) && (var1 != null || var2.length() > 0) ? this.newInstance(var1, this.getProperties(), this.getFile()) : this);
   }

   @Override
   public Artifact setFile(File var1) {
      File var2 = this.getFile();
      return (Artifact)(Objects.equals(var2, var1) ? this : this.newInstance(this.getVersion(), this.getProperties(), var1));
   }

   @Override
   public Artifact setProperties(Map<String, String> var1) {
      Map var2 = this.getProperties();
      return (Artifact)(!var2.equals(var1) && (var1 != null || !var2.isEmpty())
         ? this.newInstance(this.getVersion(), copyProperties(var1), this.getFile())
         : this);
   }

   @Override
   public String getProperty(String var1, String var2) {
      String var3 = this.getProperties().get(var1);
      return var3 != null ? var3 : var2;
   }

   protected static Map<String, String> copyProperties(Map<String, String> var0) {
      return var0 != null && !var0.isEmpty() ? Collections.unmodifiableMap(new HashMap<>(var0)) : Collections.emptyMap();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(this.getGroupId());
      var1.append(':').append(this.getArtifactId());
      var1.append(':').append(this.getExtension());
      if (this.getClassifier().length() > 0) {
         var1.append(':').append(this.getClassifier());
      }

      var1.append(':').append(this.getVersion());
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Artifact)) {
         return false;
      } else {
         Artifact var2 = (Artifact)var1;
         return Objects.equals(this.getArtifactId(), var2.getArtifactId())
            && Objects.equals(this.getGroupId(), var2.getGroupId())
            && Objects.equals(this.getVersion(), var2.getVersion())
            && Objects.equals(this.getExtension(), var2.getExtension())
            && Objects.equals(this.getClassifier(), var2.getClassifier())
            && Objects.equals(this.getFile(), var2.getFile())
            && Objects.equals(this.getProperties(), var2.getProperties());
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.getGroupId().hashCode();
      var1 = var1 * 31 + this.getArtifactId().hashCode();
      var1 = var1 * 31 + this.getExtension().hashCode();
      var1 = var1 * 31 + this.getClassifier().hashCode();
      var1 = var1 * 31 + this.getVersion().hashCode();
      return var1 * 31 + hash(this.getFile());
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }
}
