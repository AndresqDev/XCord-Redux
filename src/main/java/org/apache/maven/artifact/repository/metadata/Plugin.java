package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

@Deprecated
public class Plugin implements Serializable, Cloneable {
   private String name;
   private String prefix;
   private String artifactId;

   public Plugin clone() {
      try {
         return (Plugin)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getName() {
      return this.name;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setPrefix(String var1) {
      this.prefix = var1;
   }
}
