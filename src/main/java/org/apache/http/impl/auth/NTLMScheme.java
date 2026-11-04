package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class NTLMScheme extends AuthSchemeBase {
   private final NTLMEngine engine;
   private NTLMScheme.State state;
   private String challenge;

   public NTLMScheme(NTLMEngine var1) {
      Args.notNull(var1, "NTLM engine");
      this.engine = var1;
      this.state = NTLMScheme.State.UNINITIATED;
      this.challenge = null;
   }

   public NTLMScheme() {
      this(new NTLMEngineImpl());
   }

   @Override
   public String getSchemeName() {
      return "ntlm";
   }

   @Override
   public String getParameter(String var1) {
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

   @Override
   protected void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException {
      this.challenge = var1.substringTrimmed(var2, var3);
      if (this.challenge.isEmpty()) {
         if (this.state == NTLMScheme.State.UNINITIATED) {
            this.state = NTLMScheme.State.CHALLENGE_RECEIVED;
         } else {
            this.state = NTLMScheme.State.FAILED;
         }
      } else {
         if (this.state.compareTo(NTLMScheme.State.MSG_TYPE1_GENERATED) < 0) {
            this.state = NTLMScheme.State.FAILED;
            throw new MalformedChallengeException("Out of sequence NTLM response message");
         }

         if (this.state == NTLMScheme.State.MSG_TYPE1_GENERATED) {
            this.state = NTLMScheme.State.MSG_TYPE2_RECEVIED;
         }
      }
   }

   @Override
   public Header authenticate(Credentials var1, HttpRequest var2) throws AuthenticationException {
      Object var3 = null;

      try {
         var3 = (NTCredentials)var1;
      } catch (ClassCastException var6) {
         throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + var1.getClass().getName());
      }

      Object var4 = null;
      if (this.state == NTLMScheme.State.FAILED) {
         throw new AuthenticationException("NTLM authentication failed");
      } else {
         if (this.state == NTLMScheme.State.CHALLENGE_RECEIVED) {
            var4 = this.engine.generateType1Msg(((NTCredentials)var3).getDomain(), ((NTCredentials)var3).getWorkstation());
            this.state = NTLMScheme.State.MSG_TYPE1_GENERATED;
         } else {
            if (this.state != NTLMScheme.State.MSG_TYPE2_RECEVIED) {
               throw new AuthenticationException("Unexpected state: " + this.state);
            }

            var4 = this.engine
               .generateType3Msg(
                  ((NTCredentials)var3).getUserName(),
                  ((NTCredentials)var3).getPassword(),
                  ((NTCredentials)var3).getDomain(),
                  ((NTCredentials)var3).getWorkstation(),
                  this.challenge
               );
            this.state = NTLMScheme.State.MSG_TYPE3_GENERATED;
         }

         CharArrayBuffer var5 = new CharArrayBuffer(32);
         if (this.isProxy()) {
            var5.append("Proxy-Authorization");
         } else {
            var5.append("Authorization");
         }

         var5.append(": NTLM ");
         var5.append((String)var4);
         return new BufferedHeader(var5);
      }
   }

   @Override
   public boolean isComplete() {
      return this.state == NTLMScheme.State.MSG_TYPE3_GENERATED || this.state == NTLMScheme.State.FAILED;
   }

   static enum State {
      UNINITIATED,
      CHALLENGE_RECEIVED,
      MSG_TYPE1_GENERATED,
      MSG_TYPE2_RECEVIED,
      MSG_TYPE3_GENERATED,
      FAILED;
   }
}
