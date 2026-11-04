package org.apache.http.impl.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.CharsetUtils;

public abstract class RFC2617Scheme extends AuthSchemeBase implements Serializable {
   private static final long serialVersionUID = -2845454858205884623L;
   private final Map<String, String> params = new HashMap<>();
   private transient Charset credentialsCharset;

   @Deprecated
   public RFC2617Scheme(ChallengeState var1) {
      super(var1);
      this.credentialsCharset = Consts.ASCII;
   }

   public RFC2617Scheme(Charset var1) {
      this.credentialsCharset = var1 != null ? var1 : Consts.ASCII;
   }

   public RFC2617Scheme() {
      this(Consts.ASCII);
   }

   public Charset getCredentialsCharset() {
      return this.credentialsCharset != null ? this.credentialsCharset : Consts.ASCII;
   }

   String getCredentialsCharset(HttpRequest var1) {
      String var2 = (String)var1.getParams().getParameter("http.auth.credential-charset");
      if (var2 == null) {
         var2 = this.getCredentialsCharset().name();
      }

      return var2;
   }

   @Override
   protected void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException {
      BasicHeaderValueParser var4 = BasicHeaderValueParser.INSTANCE;
      ParserCursor var5 = new ParserCursor(var2, var1.length());
      HeaderElement[] var6 = var4.parseElements(var1, var5);
      this.params.clear();

      for (HeaderElement var10 : var6) {
         this.params.put(var10.getName().toLowerCase(Locale.ROOT), var10.getValue());
      }
   }

   protected Map<String, String> getParameters() {
      return this.params;
   }

   @Override
   public String getParameter(String var1) {
      return var1 == null ? null : this.params.get(var1.toLowerCase(Locale.ROOT));
   }

   @Override
   public String getRealm() {
      return this.getParameter("realm");
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeUTF(this.credentialsCharset.name());
      var1.writeObject(this.challengeState);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.credentialsCharset = CharsetUtils.get(var1.readUTF());
      if (this.credentialsCharset == null) {
         this.credentialsCharset = Consts.ASCII;
      }

      this.challengeState = (ChallengeState)var1.readObject();
   }

   private void readObjectNoData() throws ObjectStreamException {
   }
}
