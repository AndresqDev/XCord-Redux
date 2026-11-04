package org.apache.maven.model;

import java.io.Serializable;

public class Profile extends ModelBase implements Serializable, Cloneable {
   private String id = "default";
   private Activation activation;
   private BuildBase build;
   public static final String SOURCE_POM = "pom";
   public static final String SOURCE_SETTINGS = "settings.xml";
   private String source = "pom";

   public Profile clone() {
      try {
         Profile var1 = (Profile)super.clone();
         if (this.activation != null) {
            var1.activation = this.activation.clone();
         }

         if (this.build != null) {
            var1.build = this.build.clone();
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public Activation getActivation() {
      return this.activation;
   }

   public BuildBase getBuild() {
      return this.build;
   }

   public String getId() {
      return this.id;
   }

   public void setActivation(Activation var1) {
      this.activation = var1;
   }

   public void setBuild(BuildBase var1) {
      this.build = var1;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setSource(String var1) {
      this.source = var1;
   }

   public String getSource() {
      return this.source;
   }

   @Override
   public String toString() {
      return "Profile {id: " + this.getId() + ", source: " + this.getSource() + "}";
   }
}
