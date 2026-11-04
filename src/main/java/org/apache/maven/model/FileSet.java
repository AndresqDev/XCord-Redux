package org.apache.maven.model;

import java.io.Serializable;

public class FileSet extends PatternSet implements Serializable, Cloneable {
   private String directory;

   public FileSet clone() {
      try {
         return (FileSet)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getDirectory() {
      return this.directory;
   }

   public void setDirectory(String var1) {
      this.directory = var1;
   }

   @Override
   public String toString() {
      return "FileSet {directory: " + this.getDirectory() + ", " + super.toString() + "}";
   }
}
