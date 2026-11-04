package org.apache.http.impl.auth;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

@Deprecated
public class NegotiateScheme extends GGSSchemeBase {
   private final Log log = LogFactory.getLog(this.getClass());
   private static final String SPNEGO_OID = "1.3.6.1.5.5.2";
   private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
   private final SpnegoTokenGenerator spengoGenerator;

   public NegotiateScheme(SpnegoTokenGenerator var1, boolean var2) {
      super(var2);
      this.spengoGenerator = var1;
   }

   public NegotiateScheme(SpnegoTokenGenerator var1) {
      this(var1, false);
   }

   public NegotiateScheme() {
      this(null, false);
   }

   @Override
   public String getSchemeName() {
      return "Negotiate";
   }

   @Override
   public Header authenticate(Credentials var1, HttpRequest var2) throws AuthenticationException {
      return this.authenticate(var1, var2, null);
   }

   @Override
   public Header authenticate(Credentials var1, HttpRequest var2, HttpContext var3) throws AuthenticationException {
      return super.authenticate(var1, var2, var3);
   }

   @Override
   protected byte[] generateToken(byte[] var1, String var2) throws GSSException {
      return super.generateToken(var1, var2);
   }

   @Override
   protected byte[] generateToken(byte[] var1, String var2, Credentials var3) throws GSSException {
      Oid var4 = new Oid("1.3.6.1.5.5.2");
      byte[] var5 = var1;
      boolean var6 = false;

      try {
         var5 = this.generateGSSToken(var5, var4, var2, var3);
      } catch (GSSException var9) {
         if (var9.getMajor() != 2) {
            throw var9;
         }

         this.log.debug("GSSException BAD_MECH, retry with Kerberos MECH");
         var6 = true;
      }

      if (var6) {
         this.log.debug("Using Kerberos MECH 1.2.840.113554.1.2.2");
         var4 = new Oid("1.2.840.113554.1.2.2");
         var5 = this.generateGSSToken(var5, var4, var2, var3);
         if (var5 != null && this.spengoGenerator != null) {
            try {
               var5 = this.spengoGenerator.generateSpnegoDERObject(var5);
            } catch (IOException var8) {
               this.log.error(var8.getMessage(), var8);
            }
         }
      }

      return var5;
   }

   @Override
   public String getParameter(String var1) {
      Args.notNull(var1, "Parameter name");
      return null;
   }

   @Override
   public String getRealm() {
      return null;
   }

   @Override
   public boolean isConnectionBased() {
      return true;
   }
}
