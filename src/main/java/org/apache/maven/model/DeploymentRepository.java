package org.apache.maven.model;

import java.io.Serializable;

public class DeploymentRepository extends Repository implements Serializable, Cloneable {
   private boolean uniqueVersion = true;

   public DeploymentRepository clone() {
      try {
         return (DeploymentRepository)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public boolean isUniqueVersion() {
      return this.uniqueVersion;
   }

   public void setUniqueVersion(boolean var1) {
      this.uniqueVersion = var1;
   }
}
