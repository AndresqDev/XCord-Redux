package org.eclipse.aether.installation;

import org.eclipse.aether.RepositoryException;

public class InstallationException extends RepositoryException {
   public InstallationException(String var1) {
      super(var1);
   }

   public InstallationException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
