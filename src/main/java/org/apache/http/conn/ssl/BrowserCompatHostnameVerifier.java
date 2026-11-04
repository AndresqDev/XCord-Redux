package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BrowserCompatHostnameVerifier extends AbstractVerifier {
   public static final BrowserCompatHostnameVerifier INSTANCE = new BrowserCompatHostnameVerifier();

   @Override
   public final void verify(String var1, String[] var2, String[] var3) throws SSLException {
      this.verify(var1, var2, var3, false);
   }

   @Override
   public final String toString() {
      return "BROWSER_COMPATIBLE";
   }
}
