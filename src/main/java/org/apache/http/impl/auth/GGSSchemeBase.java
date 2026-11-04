package org.apache.http.impl.auth;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public abstract class GGSSchemeBase extends AuthSchemeBase {
   private final Log log = LogFactory.getLog(this.getClass());
   private final Base64 base64codec = new Base64(0);
   private final boolean stripPort;
   private final boolean useCanonicalHostname;
   private GGSSchemeBase.State state;
   private byte[] token;

   GGSSchemeBase(boolean var1, boolean var2) {
      this.stripPort = var1;
      this.useCanonicalHostname = var2;
      this.state = GGSSchemeBase.State.UNINITIATED;
   }

   GGSSchemeBase(boolean var1) {
      this(var1, true);
   }

   GGSSchemeBase() {
      this(true, true);
   }

   protected GSSManager getManager() {
      return GSSManager.getInstance();
   }

   protected byte[] generateGSSToken(byte[] var1, Oid var2, String var3) throws GSSException {
      return this.generateGSSToken(var1, var2, var3, null);
   }

   protected byte[] generateGSSToken(byte[] var1, Oid var2, String var3, Credentials var4) throws GSSException {
      GSSManager var5 = this.getManager();
      GSSName var6 = var5.createName("HTTP@" + var3, GSSName.NT_HOSTBASED_SERVICE);
      GSSCredential var7;
      if (var4 instanceof KerberosCredentials) {
         var7 = ((KerberosCredentials)var4).getGSSCredential();
      } else {
         var7 = null;
      }

      GSSContext var8 = this.createGSSContext(var5, var2, var6, var7);
      return var1 != null ? var8.initSecContext(var1, 0, var1.length) : var8.initSecContext(new byte[0], 0, 0);
   }

   GSSContext createGSSContext(GSSManager var1, Oid var2, GSSName var3, GSSCredential var4) throws GSSException {
      GSSContext var5 = var1.createContext(var3.canonicalize(var2), var2, var4, 0);
      var5.requestMutualAuth(true);
      return var5;
   }

   @Deprecated
   protected byte[] generateToken(byte[] var1, String var2) throws GSSException {
      return null;
   }

   protected byte[] generateToken(byte[] var1, String var2, Credentials var3) throws GSSException {
      return this.generateToken(var1, var2);
   }

   @Override
   public boolean isComplete() {
      return this.state == GGSSchemeBase.State.TOKEN_GENERATED || this.state == GGSSchemeBase.State.FAILED;
   }

   @Deprecated
   @Override
   public Header authenticate(Credentials var1, HttpRequest var2) throws AuthenticationException {
      return this.authenticate(var1, var2, null);
   }

   @Override
   public Header authenticate(Credentials var1, HttpRequest var2, HttpContext var3) throws AuthenticationException {
      Args.notNull(var2, "HTTP request");
      switch (this.state) {
         case UNINITIATED:
            throw new AuthenticationException(this.getSchemeName() + " authentication has not been initiated");
         case FAILED:
            throw new AuthenticationException(this.getSchemeName() + " authentication has failed");
         case CHALLENGE_RECEIVED:
            try {
               HttpRoute var4 = (HttpRoute)var3.getAttribute("http.route");
               if (var4 == null) {
                  throw new AuthenticationException("Connection route is not available");
               } else {
                  HttpHost var5;
                  if (this.isProxy()) {
                     var5 = var4.getProxyHost();
                     if (var5 == null) {
                        var5 = var4.getTargetHost();
                     }
                  } else {
                     var5 = var4.getTargetHost();
                  }

                  String var7 = var5.getHostName();
                  if (this.useCanonicalHostname) {
                     try {
                        var7 = this.resolveCanonicalHostname(var7);
                     } catch (UnknownHostException var9) {
                     }
                  }

                  String var6;
                  if (this.stripPort) {
                     var6 = var7;
                  } else {
                     var6 = var7 + ":" + var5.getPort();
                  }

                  if (this.log.isDebugEnabled()) {
                     this.log.debug("init " + var6);
                  }

                  this.token = this.generateToken(this.token, var6, var1);
                  this.state = GGSSchemeBase.State.TOKEN_GENERATED;
               }
            } catch (GSSException var10) {
               this.state = GGSSchemeBase.State.FAILED;
               if (var10.getMajor() != 9 && var10.getMajor() != 8) {
                  if (var10.getMajor() == 13) {
                     throw new InvalidCredentialsException(var10.getMessage(), var10);
                  }

                  if (var10.getMajor() != 10 && var10.getMajor() != 19 && var10.getMajor() != 20) {
                     throw new AuthenticationException(var10.getMessage());
                  }

                  throw new AuthenticationException(var10.getMessage(), var10);
               }

               throw new InvalidCredentialsException(var10.getMessage(), var10);
            }
         case TOKEN_GENERATED:
            String var11 = new String(this.base64codec.encode(this.token));
            if (this.log.isDebugEnabled()) {
               this.log.debug("Sending response '" + var11 + "' back to the auth server");
            }

            CharArrayBuffer var12 = new CharArrayBuffer(32);
            if (this.isProxy()) {
               var12.append("Proxy-Authorization");
            } else {
               var12.append("Authorization");
            }

            var12.append(": Negotiate ");
            var12.append(var11);
            return new BufferedHeader(var12);
         default:
            throw new IllegalStateException("Illegal state: " + this.state);
      }
   }

   @Override
   protected void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException {
      String var4 = var1.substringTrimmed(var2, var3);
      if (this.log.isDebugEnabled()) {
         this.log.debug("Received challenge '" + var4 + "' from the auth server");
      }

      if (this.state == GGSSchemeBase.State.UNINITIATED) {
         this.token = Base64.decodeBase64(var4.getBytes());
         this.state = GGSSchemeBase.State.CHALLENGE_RECEIVED;
      } else {
         this.log.debug("Authentication already attempted");
         this.state = GGSSchemeBase.State.FAILED;
      }
   }

   private String resolveCanonicalHostname(String var1) throws UnknownHostException {
      InetAddress var2 = InetAddress.getByName(var1);
      String var3 = var2.getCanonicalHostName();
      return var2.getHostAddress().contentEquals(var3) ? var1 : var3;
   }

   static enum State {
      UNINITIATED,
      CHALLENGE_RECEIVED,
      TOKEN_GENERATED,
      FAILED;
   }
}
