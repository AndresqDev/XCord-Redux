package org.apache.http.conn.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NoopHostnameVerifier implements HostnameVerifier {
   public static final NoopHostnameVerifier INSTANCE = new NoopHostnameVerifier();

   @Override
   public boolean verify(String var1, SSLSession var2) {
      return true;
   }

   @Override
   public final String toString() {
      return "NO_OP";
   }
}
