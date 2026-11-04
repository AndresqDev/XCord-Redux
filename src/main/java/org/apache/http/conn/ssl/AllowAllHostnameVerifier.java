package org.apache.http.conn.ssl;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class AllowAllHostnameVerifier extends AbstractVerifier {
   public static final AllowAllHostnameVerifier INSTANCE = new AllowAllHostnameVerifier();

   @Override
   public final void verify(String var1, String[] var2, String[] var3) {
   }

   @Override
   public final String toString() {
      return "ALLOW_ALL";
   }
}
