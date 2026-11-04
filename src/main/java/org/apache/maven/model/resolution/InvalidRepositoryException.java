package org.apache.maven.model.resolution;

import org.apache.maven.model.Repository;

public class InvalidRepositoryException extends Exception {
   private Repository repository;

   public InvalidRepositoryException(String var1, Repository var2, Throwable var3) {
      super(var1, var3);
      this.repository = var2;
   }

   public InvalidRepositoryException(String var1, Repository var2) {
      super(var1);
      this.repository = var2;
   }

   public Repository getRepository() {
      return this.repository;
   }
}
