package org.eclipse.aether.util.repository;

import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class StringAuthentication implements Authentication {
   private final String key;
   private final String value;

   StringAuthentication(String var1, String var2) {
      this.key = Objects.requireNonNull(var1, "authentication key cannot be null");
      if (var1.length() == 0) {
         throw new IllegalArgumentException("authentication key cannot be empty");
      } else {
         this.value = var2;
      }
   }

   @Override
   public void fill(AuthenticationContext var1, String var2, Map<String, String> var3) {
      var1.put(this.key, this.value);
   }

   @Override
   public void digest(AuthenticationDigest var1) {
      var1.update(this.key, this.value);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         StringAuthentication var2 = (StringAuthentication)var1;
         return Objects.equals(this.key, var2.key) && Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.key.hashCode();
      return var1 * 31 + (this.value != null ? this.value.hashCode() : 0);
   }

   @Override
   public String toString() {
      return this.key + "=" + this.value;
   }
}
