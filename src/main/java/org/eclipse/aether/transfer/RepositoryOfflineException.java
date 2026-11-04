package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public class RepositoryOfflineException extends RepositoryException {
   private final transient RemoteRepository repository;

   private static String getMessage(RemoteRepository var0) {
      return var0 == null ? "Cannot access remote repositories in offline mode" : "Cannot access " + var0.getId() + " (" + var0.getUrl() + ") in offline mode";
   }

   public RepositoryOfflineException(RemoteRepository var1) {
      super(getMessage(var1));
      this.repository = var1;
   }

   public RepositoryOfflineException(RemoteRepository var1, String var2) {
      super(var2);
      this.repository = var1;
   }

   public RemoteRepository getRepository() {
      return this.repository;
   }
}
