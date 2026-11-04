package org.eclipse.aether.util.repository;

import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class ConservativeAuthenticationSelector implements AuthenticationSelector {
   private final AuthenticationSelector selector;

   public ConservativeAuthenticationSelector(AuthenticationSelector var1) {
      this.selector = Objects.requireNonNull(var1, "authentication selector cannot be null");
   }

   @Override
   public Authentication getAuthentication(RemoteRepository var1) {
      Authentication var2 = var1.getAuthentication();
      return var2 != null ? var2 : this.selector.getAuthentication(var1);
   }
}
