package org.apache.http.impl.client;

import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler {
   @Override
   public boolean isAuthenticationRequested(HttpResponse var1, HttpContext var2) {
      Args.notNull(var1, "HTTP response");
      int var3 = var1.getStatusLine().getStatusCode();
      return var3 == 401;
   }

   @Override
   public Map<String, Header> getChallenges(HttpResponse var1, HttpContext var2) throws MalformedChallengeException {
      Args.notNull(var1, "HTTP response");
      Header[] var3 = var1.getHeaders("WWW-Authenticate");
      return this.parseChallenges(var3);
   }

   @Override
   protected List<String> getAuthPreferences(HttpResponse var1, HttpContext var2) {
      List var3 = (List)var1.getParams().getParameter("http.auth.target-scheme-pref");
      return var3 != null ? var3 : super.getAuthPreferences(var1, var2);
   }
}
