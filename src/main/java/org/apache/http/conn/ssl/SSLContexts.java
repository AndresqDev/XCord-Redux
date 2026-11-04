package org.apache.http.conn.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class SSLContexts {
   public static SSLContext createDefault() throws SSLInitializationException {
      try {
         SSLContext var0 = SSLContext.getInstance("TLS");
         var0.init(null, null, null);
         return var0;
      } catch (NoSuchAlgorithmException var1) {
         throw new SSLInitializationException(var1.getMessage(), var1);
      } catch (KeyManagementException var2) {
         throw new SSLInitializationException(var2.getMessage(), var2);
      }
   }

   public static SSLContext createSystemDefault() throws SSLInitializationException {
      try {
         return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException var1) {
         return createDefault();
      }
   }

   public static SSLContextBuilder custom() {
      return new SSLContextBuilder();
   }
}
