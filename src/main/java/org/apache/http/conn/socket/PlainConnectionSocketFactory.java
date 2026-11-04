package org.apache.http.conn.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class PlainConnectionSocketFactory implements ConnectionSocketFactory {
   public static final PlainConnectionSocketFactory INSTANCE = new PlainConnectionSocketFactory();

   public static PlainConnectionSocketFactory getSocketFactory() {
      return INSTANCE;
   }

   @Override
   public Socket createSocket(HttpContext var1) throws IOException {
      return new Socket();
   }

   @Override
   public Socket connectSocket(int var1, Socket var2, HttpHost var3, InetSocketAddress var4, InetSocketAddress var5, HttpContext var6) throws IOException {
      Socket var7 = var2 != null ? var2 : this.createSocket(var6);
      if (var5 != null) {
         var7.bind(var5);
      }

      try {
         var7.connect(var4, var1);
         return var7;
      } catch (IOException var11) {
         try {
            var7.close();
         } catch (IOException var10) {
         }

         throw var11;
      }
   }
}
