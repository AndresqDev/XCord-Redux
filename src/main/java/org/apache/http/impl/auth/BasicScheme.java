package org.apache.http.impl.auth;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class BasicScheme extends RFC2617Scheme {
   private static final long serialVersionUID = -1931571557597830536L;
   private boolean complete;

   public BasicScheme(Charset var1) {
      super(var1);
      this.complete = false;
   }

   @Deprecated
   public BasicScheme(ChallengeState var1) {
      super(var1);
   }

   public BasicScheme() {
      this(Consts.ASCII);
   }

   @Override
   public String getSchemeName() {
      return "basic";
   }

   @Override
   public void processChallenge(Header var1) throws MalformedChallengeException {
      super.processChallenge(var1);
      this.complete = true;
   }

   @Override
   public boolean isComplete() {
      return this.complete;
   }

   @Override
   public boolean isConnectionBased() {
      return false;
   }

   @Deprecated
   @Override
   public Header authenticate(Credentials var1, HttpRequest var2) throws AuthenticationException {
      return this.authenticate(var1, var2, new BasicHttpContext());
   }

   @Override
   public Header authenticate(Credentials var1, HttpRequest var2, HttpContext var3) throws AuthenticationException {
      Args.notNull(var1, "Credentials");
      Args.notNull(var2, "HTTP request");
      StringBuilder var4 = new StringBuilder();
      var4.append(var1.getUserPrincipal().getName());
      var4.append(":");
      var4.append(var1.getPassword() == null ? "null" : var1.getPassword());
      Base64 var5 = new Base64(0);
      byte[] var6 = var5.encode(EncodingUtils.getBytes(var4.toString(), this.getCredentialsCharset(var2)));
      CharArrayBuffer var7 = new CharArrayBuffer(32);
      if (this.isProxy()) {
         var7.append("Proxy-Authorization");
      } else {
         var7.append("Authorization");
      }

      var7.append(": Basic ");
      var7.append(var6, 0, var6.length);
      return new BufferedHeader(var7);
   }

   @Deprecated
   public static Header authenticate(Credentials var0, String var1, boolean var2) {
      Args.notNull(var0, "Credentials");
      Args.notNull(var1, "charset");
      StringBuilder var3 = new StringBuilder();
      var3.append(var0.getUserPrincipal().getName());
      var3.append(":");
      var3.append(var0.getPassword() == null ? "null" : var0.getPassword());
      byte[] var4 = Base64.encodeBase64(EncodingUtils.getBytes(var3.toString(), var1), false);
      CharArrayBuffer var5 = new CharArrayBuffer(32);
      if (var2) {
         var5.append("Proxy-Authorization");
      } else {
         var5.append("Authorization");
      }

      var5.append(": Basic ");
      var5.append(var4, 0, var4.length);
      return new BufferedHeader(var5);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("BASIC [complete=").append(this.complete).append("]");
      return var1.toString();
   }
}
