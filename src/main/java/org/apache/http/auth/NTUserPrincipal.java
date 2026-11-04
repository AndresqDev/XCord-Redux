package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NTUserPrincipal implements Serializable, Principal {
   private static final long serialVersionUID = -6870169797924406894L;
   private final String username;
   private final String domain;
   private final String ntname;

   public NTUserPrincipal(String var1, String var2) {
      Args.notNull(var2, "User name");
      this.username = var2;
      if (var1 != null) {
         this.domain = var1.toUpperCase(Locale.ROOT);
      } else {
         this.domain = null;
      }

      if (this.domain != null && !this.domain.isEmpty()) {
         StringBuilder var3 = new StringBuilder();
         var3.append(this.domain);
         var3.append('\\');
         var3.append(this.username);
         this.ntname = var3.toString();
      } else {
         this.ntname = this.username;
      }
   }

   @Override
   public String getName() {
      return this.ntname;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getUsername() {
      return this.username;
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = LangUtils.hashCode(var1, this.username);
      return LangUtils.hashCode(var1, this.domain);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof NTUserPrincipal) {
            NTUserPrincipal var2 = (NTUserPrincipal)var1;
            if (LangUtils.equals(this.username, var2.username) && LangUtils.equals(this.domain, var2.domain)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public String toString() {
      return this.ntname;
   }
}
