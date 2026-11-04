package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Build extends BuildBase implements Serializable, Cloneable {
   private String sourceDirectory;
   private String scriptSourceDirectory;
   private String testSourceDirectory;
   private String outputDirectory;
   private String testOutputDirectory;
   private List<Extension> extensions;

   public void addExtension(Extension var1) {
      this.getExtensions().add(var1);
   }

   public Build clone() {
      try {
         Build var1 = (Build)super.clone();
         if (this.extensions != null) {
            var1.extensions = new ArrayList<>();

            for (Extension var3 : this.extensions) {
               var1.extensions.add(var3.clone());
            }
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public List<Extension> getExtensions() {
      if (this.extensions == null) {
         this.extensions = new ArrayList<>();
      }

      return this.extensions;
   }

   public String getOutputDirectory() {
      return this.outputDirectory;
   }

   public String getScriptSourceDirectory() {
      return this.scriptSourceDirectory;
   }

   public String getSourceDirectory() {
      return this.sourceDirectory;
   }

   public String getTestOutputDirectory() {
      return this.testOutputDirectory;
   }

   public String getTestSourceDirectory() {
      return this.testSourceDirectory;
   }

   public void removeExtension(Extension var1) {
      this.getExtensions().remove(var1);
   }

   public void setExtensions(List<Extension> var1) {
      this.extensions = var1;
   }

   public void setOutputDirectory(String var1) {
      this.outputDirectory = var1;
   }

   public void setScriptSourceDirectory(String var1) {
      this.scriptSourceDirectory = var1;
   }

   public void setSourceDirectory(String var1) {
      this.sourceDirectory = var1;
   }

   public void setTestOutputDirectory(String var1) {
      this.testOutputDirectory = var1;
   }

   public void setTestSourceDirectory(String var1) {
      this.testSourceDirectory = var1;
   }
}
