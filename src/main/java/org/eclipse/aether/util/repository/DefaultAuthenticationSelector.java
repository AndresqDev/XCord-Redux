package org.eclipse.aether.util.repository;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultAuthenticationSelector implements AuthenticationSelector {
   private final Map<String, Authentication> repos = new HashMap<>();

   public DefaultAuthenticationSelector add(String var1, Authentication var2) {
      if (var2 != null) {
         this.repos.put(var1, var2);
      } else {
         this.repos.remove(var1);
      }

      return this;
   }

   @Override
   public Authentication getAuthentication(RemoteRepository var1) {
      return this.repos.get(var1.getId());
   }
}
