package org.apache.http.impl.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpClientConnectionFactory;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.ConnFactory;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class BasicConnFactory implements ConnFactory<HttpHost, HttpClientConnection> {
   private final SocketFactory plainfactory;
   private final SSLSocketFactory sslfactory;
   private final int connectTimeout;
   private final SocketConfig sconfig;
   private final HttpConnectionFactory<? extends HttpClientConnection> connFactory;

   @Deprecated
   public BasicConnFactory(SSLSocketFactory var1, HttpParams var2) {
      Args.notNull(var2, "HTTP params");
      this.plainfactory = null;
      this.sslfactory = var1;
      this.connectTimeout = var2.getIntParameter("http.connection.timeout", 0);
      this.sconfig = HttpParamConfig.getSocketConfig(var2);
      this.connFactory = new DefaultBHttpClientConnectionFactory(HttpParamConfig.getConnectionConfig(var2));
   }

   @Deprecated
   public BasicConnFactory(HttpParams var1) {
      this(null, var1);
   }

   public BasicConnFactory(SocketFactory var1, SSLSocketFactory var2, int var3, SocketConfig var4, ConnectionConfig var5) {
      this.plainfactory = var1;
      this.sslfactory = var2;
      this.connectTimeout = var3;
      this.sconfig = var4 != null ? var4 : SocketConfig.DEFAULT;
      this.connFactory = new DefaultBHttpClientConnectionFactory(var5 != null ? var5 : ConnectionConfig.DEFAULT);
   }

   public BasicConnFactory(int var1, SocketConfig var2, ConnectionConfig var3) {
      this(null, null, var1, var2, var3);
   }

   public BasicConnFactory(SocketConfig var1, ConnectionConfig var2) {
      this(null, null, 0, var1, var2);
   }

   public BasicConnFactory() {
      this(null, null, 0, SocketConfig.DEFAULT, ConnectionConfig.DEFAULT);
   }

   @Deprecated
   protected HttpClientConnection create(Socket var1, HttpParams var2) throws IOException {
      int var3 = var2.getIntParameter("http.socket.buffer-size", 8192);
      DefaultBHttpClientConnection var4 = new DefaultBHttpClientConnection(var3);
      var4.bind(var1);
      return var4;
   }

   public HttpClientConnection create(HttpHost var1) throws IOException {
      String var2 = var1.getSchemeName();
      final Socket var3;
      if ("http".equalsIgnoreCase(var2)) {
         var3 = this.plainfactory != null ? this.plainfactory.createSocket() : new Socket();
      } else {
         if (!"https".equalsIgnoreCase(var2)) {
            throw new IOException(var2 + " scheme is not supported");
         }

         var3 = (this.sslfactory != null ? this.sslfactory : SSLSocketFactory.getDefault()).createSocket();
      }

      String var4 = var1.getHostName();
      int var5 = var1.getPort();
      if (var5 == -1) {
         if (var1.getSchemeName().equalsIgnoreCase("http")) {
            var5 = 80;
         } else if (var1.getSchemeName().equalsIgnoreCase("https")) {
            var5 = 443;
         }
      }

      var3.setSoTimeout(this.sconfig.getSoTimeout());
      if (this.sconfig.getSndBufSize() > 0) {
         var3.setSendBufferSize(this.sconfig.getSndBufSize());
      }

      if (this.sconfig.getRcvBufSize() > 0) {
         var3.setReceiveBufferSize(this.sconfig.getRcvBufSize());
      }

      var3.setTcpNoDelay(this.sconfig.isTcpNoDelay());
      int var6 = this.sconfig.getSoLinger();
      if (var6 >= 0) {
         var3.setSoLinger(true, var6);
      }

      var3.setKeepAlive(this.sconfig.isSoKeepAlive());
      final InetSocketAddress var7 = new InetSocketAddress(var4, var5);

      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws IOException {
               var3.connect(var7, BasicConnFactory.this.connectTimeout);
               return null;
            }
         });
      } catch (PrivilegedActionException var9) {
         Asserts.check(var9.getCause() instanceof IOException, "method contract violation only checked exceptions are wrapped: " + var9.getCause());
         throw (IOException)var9.getCause();
      }

      return this.connFactory.createConnection(var3);
   }
}
