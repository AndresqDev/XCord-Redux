package org.apache.maven.model;

import java.io.Serializable;

public class InputSource implements Serializable, Cloneable {
   private String modelId;
   private String location;

   public InputSource clone() {
      try {
         return (InputSource)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getLocation() {
      return this.location;
   }

   public String getModelId() {
      return this.modelId;
   }

   public void setLocation(String var1) {
      this.location = var1;
   }

   public void setModelId(String var1) {
      this.modelId = var1;
   }

   @Override
   public String toString() {
      return this.getModelId() + " " + this.getLocation();
   }
}
