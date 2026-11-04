package org.eclipse.aether.metadata;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMetadata implements Metadata {
   private Metadata newInstance(Map<String, String> var1, File var2) {
      return new DefaultMetadata(this.getGroupId(), this.getArtifactId(), this.getVersion(), this.getType(), this.getNature(), var2, var1);
   }

   @Override
   public Metadata setFile(File var1) {
      File var2 = this.getFile();
      return (Metadata)(Objects.equals(var2, var1) ? this : this.newInstance(this.getProperties(), var1));
   }

   @Override
   public Metadata setProperties(Map<String, String> var1) {
      Map var2 = this.getProperties();
      return (Metadata)(!var2.equals(var1) && (var1 != null || !var2.isEmpty()) ? this.newInstance(copyProperties(var1), this.getFile()) : this);
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
      if (this.getGroupId().length() > 0) {
         var1.append(this.getGroupId());
      }

      if (this.getArtifactId().length() > 0) {
         var1.append(':').append(this.getArtifactId());
      }

      if (this.getVersion().length() > 0) {
         var1.append(':').append(this.getVersion());
      }

      var1.append('/').append(this.getType());
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Metadata)) {
         return false;
      } else {
         Metadata var2 = (Metadata)var1;
         return Objects.equals(this.getArtifactId(), var2.getArtifactId())
            && Objects.equals(this.getGroupId(), var2.getGroupId())
            && Objects.equals(this.getVersion(), var2.getVersion())
            && Objects.equals(this.getType(), var2.getType())
            && Objects.equals(this.getNature(), var2.getNature())
            && Objects.equals(this.getFile(), var2.getFile())
            && Objects.equals(this.getProperties(), var2.getProperties());
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.getGroupId().hashCode();
      var1 = var1 * 31 + this.getArtifactId().hashCode();
      var1 = var1 * 31 + this.getType().hashCode();
      var1 = var1 * 31 + this.getNature().hashCode();
      var1 = var1 * 31 + this.getVersion().hashCode();
      return var1 * 31 + hash(this.getFile());
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }
}
