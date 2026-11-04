package org.apache.http.impl.auth;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class SPNegoSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {
   private final boolean stripPort;
   private final boolean useCanonicalHostname;

   public SPNegoSchemeFactory(boolean var1, boolean var2) {
      this.stripPort = var1;
      this.useCanonicalHostname = var2;
   }

   public SPNegoSchemeFactory(boolean var1) {
      this.stripPort = var1;
      this.useCanonicalHostname = true;
   }

   public SPNegoSchemeFactory() {
      this(true, true);
   }

   public boolean isStripPort() {
      return this.stripPort;
   }

   public boolean isUseCanonicalHostname() {
      return this.useCanonicalHostname;
   }

   @Override
   public AuthScheme newInstance(HttpParams var1) {
      return new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
   }

   @Override
   public AuthScheme create(HttpContext var1) {
      return new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
   }
}
