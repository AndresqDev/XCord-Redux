package org.apache.http.impl.conn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.Proxy.Type;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class SystemDefaultRoutePlanner extends DefaultRoutePlanner {
   private final ProxySelector proxySelector;

   public SystemDefaultRoutePlanner(SchemePortResolver var1, ProxySelector var2) {
      super(var1);
      this.proxySelector = var2;
   }

   public SystemDefaultRoutePlanner(ProxySelector var1) {
      this(null, var1);
   }

   @Override
   protected HttpHost determineProxy(HttpHost var1, HttpRequest var2, HttpContext var3) throws HttpException {
      URI var4;
      try {
         var4 = new URI(var1.toURI());
      } catch (URISyntaxException var10) {
         throw new HttpException("Cannot convert host to URI: " + var1, var10);
      }

      ProxySelector var5 = this.proxySelector;
      if (var5 == null) {
         var5 = ProxySelector.getDefault();
      }

      if (var5 == null) {
         return null;
      } else {
         List var6 = var5.select(var4);
         Proxy var7 = this.chooseProxy(var6);
         HttpHost var8 = null;
         if (var7.type() == Type.HTTP) {
            if (!(var7.address() instanceof InetSocketAddress)) {
               throw new HttpException("Unable to handle non-Inet proxy address: " + var7.address());
            }

            InetSocketAddress var9 = (InetSocketAddress)var7.address();
            var8 = new HttpHost(this.getHost(var9), var9.getPort());
         }

         return var8;
      }
   }

   private String getHost(InetSocketAddress var1) {
      return var1.isUnresolved() ? var1.getHostName() : var1.getAddress().getHostAddress();
   }

   private Proxy chooseProxy(List<Proxy> var1) {
      Proxy var2 = null;

      for (int var3 = 0; var2 == null && var3 < var1.size(); var3++) {
         Proxy var4 = (Proxy)var1.get(var3);
         switch (var4.type()) {
            case DIRECT:
            case HTTP:
               var2 = var4;
               break;
            case SOCKS:
         }
      }

      if (var2 == null) {
         var2 = Proxy.NO_PROXY;
      }

      return var2;
   }
}
