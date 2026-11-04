package org.apache.maven.model;

import java.io.Serializable;

public class Resource extends FileSet implements Serializable, Cloneable {
   private String targetPath;
   private String filtering;
   private String mergeId;
   private static int mergeIdCounter = 0;

   public Resource clone() {
      try {
         return (Resource)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getFiltering() {
      return this.filtering;
   }

   public String getMergeId() {
      return this.mergeId;
   }

   public String getTargetPath() {
      return this.targetPath;
   }

   public void setFiltering(String var1) {
      this.filtering = var1;
   }

   public void setMergeId(String var1) {
      this.mergeId = var1;
   }

   public void setTargetPath(String var1) {
      this.targetPath = var1;
   }

   public void initMergeId() {
      if (this.getMergeId() == null) {
         this.setMergeId("resource-" + mergeIdCounter++);
      }
   }

   public boolean isFiltering() {
      return this.filtering != null ? Boolean.parseBoolean(this.filtering) : false;
   }

   public void setFiltering(boolean var1) {
      this.filtering = String.valueOf(var1);
   }

   @Override
   public String toString() {
      return "Resource {targetPath: " + this.getTargetPath() + ", filtering: " + this.isFiltering() + ", " + super.toString() + "}";
   }
}
