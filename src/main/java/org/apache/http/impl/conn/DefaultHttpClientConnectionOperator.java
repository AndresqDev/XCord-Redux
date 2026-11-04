package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultHttpClientConnectionOperator implements HttpClientConnectionOperator {
   static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
   private final Log log = LogFactory.getLog(this.getClass());
   private final Lookup<ConnectionSocketFactory> socketFactoryRegistry;
   private final SchemePortResolver schemePortResolver;
   private final DnsResolver dnsResolver;

   public DefaultHttpClientConnectionOperator(Lookup<ConnectionSocketFactory> var1, SchemePortResolver var2, DnsResolver var3) {
      Args.notNull(var1, "Socket factory registry");
      this.socketFactoryRegistry = var1;
      this.schemePortResolver = (SchemePortResolver)(var2 != null ? var2 : DefaultSchemePortResolver.INSTANCE);
      this.dnsResolver = (DnsResolver)(var3 != null ? var3 : SystemDefaultDnsResolver.INSTANCE);
   }

   private Lookup<ConnectionSocketFactory> getSocketFactoryRegistry(HttpContext var1) {
      Lookup var2 = (Lookup)var1.getAttribute("http.socket-factory-registry");
      if (var2 == null) {
         var2 = this.socketFactoryRegistry;
      }

      return var2;
   }

   @Override
   public void connect(ManagedHttpClientConnection var1, HttpHost var2, InetSocketAddress var3, int var4, SocketConfig var5, HttpContext var6) throws IOException {
      Lookup var7 = this.getSocketFactoryRegistry(var6);
      ConnectionSocketFactory var8 = (ConnectionSocketFactory)var7.lookup(var2.getSchemeName());
      if (var8 == null) {
         throw new UnsupportedSchemeException(var2.getSchemeName() + " protocol is not supported");
      } else {
         InetAddress[] var9 = var2.getAddress() != null ? new InetAddress[]{var2.getAddress()} : this.dnsResolver.resolve(var2.getHostName());
         int var10 = this.schemePortResolver.resolve(var2);

         for (int var11 = 0; var11 < var9.length; var11++) {
            InetAddress var12 = var9[var11];
            boolean var13 = var11 == var9.length - 1;
            Socket var14 = var8.createSocket(var6);
            var14.setSoTimeout(var5.getSoTimeout());
            var14.setReuseAddress(var5.isSoReuseAddress());
            var14.setTcpNoDelay(var5.isTcpNoDelay());
            var14.setKeepAlive(var5.isSoKeepAlive());
            if (var5.getRcvBufSize() > 0) {
               var14.setReceiveBufferSize(var5.getRcvBufSize());
            }

            if (var5.getSndBufSize() > 0) {
               var14.setSendBufferSize(var5.getSndBufSize());
            }

            int var15 = var5.getSoLinger();
            if (var15 >= 0) {
               var14.setSoLinger(true, var15);
            }

            var1.bind(var14);
            InetSocketAddress var16 = new InetSocketAddress(var12, var10);
            if (this.log.isDebugEnabled()) {
               this.log.debug("Connecting to " + var16);
            }

            try {
               var14 = var8.connectSocket(var4, var14, var2, var16, var3, var6);
               var1.bind(var14);
               if (this.log.isDebugEnabled()) {
                  this.log.debug("Connection established " + var1);
               }

               return;
            } catch (SocketTimeoutException var19) {
               if (var13) {
                  throw new ConnectTimeoutException(var19, var2, var9);
               }
            } catch (ConnectException var20) {
               if (var13) {
                  String var18 = var20.getMessage();
                  throw "Connection timed out".equals(var18) ? new ConnectTimeoutException(var20, var2, var9) : new HttpHostConnectException(var20, var2, var9);
               }
            } catch (NoRouteToHostException var21) {
               if (var13) {
                  throw var21;
               }
            }

            if (this.log.isDebugEnabled()) {
               this.log.debug("Connect to " + var16 + " timed out. " + "Connection will be retried using another IP address");
            }
         }
      }
   }

   @Override
   public void upgrade(ManagedHttpClientConnection var1, HttpHost var2, HttpContext var3) throws IOException {
      HttpClientContext var4 = HttpClientContext.adapt(var3);
      Lookup var5 = this.getSocketFactoryRegistry(var4);
      ConnectionSocketFactory var6 = (ConnectionSocketFactory)var5.lookup(var2.getSchemeName());
      if (var6 == null) {
         throw new UnsupportedSchemeException(var2.getSchemeName() + " protocol is not supported");
      } else if (!(var6 instanceof LayeredConnectionSocketFactory)) {
         throw new UnsupportedSchemeException(var2.getSchemeName() + " protocol does not support connection upgrade");
      } else {
         LayeredConnectionSocketFactory var7 = (LayeredConnectionSocketFactory)var6;
         Socket var8 = var1.getSocket();
         int var9 = this.schemePortResolver.resolve(var2);
         var8 = var7.createLayeredSocket(var8, var2.getHostName(), var9, var3);
         var1.bind(var8);
      }
   }
}
