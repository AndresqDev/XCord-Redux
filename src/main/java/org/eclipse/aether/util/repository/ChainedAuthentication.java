package org.eclipse.aether.util.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class ChainedAuthentication implements Authentication {
   private final Authentication[] authentications;

   ChainedAuthentication(Authentication... var1) {
      if (var1 != null && var1.length > 0) {
         this.authentications = (Authentication[])var1.clone();
      } else {
         this.authentications = new Authentication[0];
      }
   }

   ChainedAuthentication(Collection<? extends Authentication> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.authentications = var1.toArray(new Authentication[var1.size()]);
      } else {
         this.authentications = new Authentication[0];
      }
   }

   @Override
   public void fill(AuthenticationContext var1, String var2, Map<String, String> var3) {
      for (Authentication var7 : this.authentications) {
         var7.fill(var1, var2, var3);
      }
   }

   @Override
   public void digest(AuthenticationDigest var1) {
      for (Authentication var5 : this.authentications) {
         var5.digest(var1);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         ChainedAuthentication var2 = (ChainedAuthentication)var1;
         return Arrays.equals((Object[])this.authentications, (Object[])var2.authentications);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.authentications);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(256);

      for (Authentication var5 : this.authentications) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append(var5);
      }

      return var1.toString();
   }
}
