package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.util.Strings;

public class SslSocketManager extends TcpSocketManager {
   public static final int DEFAULT_PORT = 6514;
   private static final SslSocketManager.SslSocketManagerFactory FACTORY = new SslSocketManager.SslSocketManagerFactory();
   private final SslConfiguration sslConfig;

   @Deprecated
   public SslSocketManager(
      String var1,
      OutputStream var2,
      Socket var3,
      SslConfiguration var4,
      InetAddress var5,
      String var6,
      int var7,
      int var8,
      int var9,
      boolean var10,
      Layout<? extends Serializable> var11,
      int var12
   ) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12, null);
      this.sslConfig = var4;
   }

   public SslSocketManager(
      String var1,
      OutputStream var2,
      Socket var3,
      SslConfiguration var4,
      InetAddress var5,
      String var6,
      int var7,
      int var8,
      int var9,
      boolean var10,
      Layout<? extends Serializable> var11,
      int var12,
      SocketOptions var13
   ) {
      super(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.sslConfig = var4;
   }

   @Deprecated
   public static SslSocketManager getSocketManager(
      SslConfiguration var0, String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7
   ) {
      return getSocketManager(var0, var1, var2, var3, var4, var5, var6, var7, null);
   }

   public static SslSocketManager getSocketManager(
      SslConfiguration var0, String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7, SocketOptions var8
   ) {
      if (Strings.isEmpty(var1)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if (var2 <= 0) {
            var2 = 6514;
         }

         if (var4 == 0) {
            var4 = 30000;
         }

         String var9 = "TLS:" + var1 + ':' + var2;
         return (SslSocketManager)getManager(var9, new SslSocketManager.SslFactoryData(var0, var1, var2, var3, var4, var5, var6, var7, var8), FACTORY);
      }
   }

   @Override
   protected Socket createSocket(InetSocketAddress var1) throws IOException {
      SSLSocketFactory var2 = createSslSocketFactory(this.sslConfig);
      Socket var3 = var2.createSocket();
      var3.connect(var1, this.getConnectTimeoutMillis());
      return var3;
   }

   private static SSLSocketFactory createSslSocketFactory(SslConfiguration var0) {
      SSLSocketFactory var1;
      if (var0 != null) {
         var1 = var0.getSslSocketFactory();
      } else {
         var1 = (SSLSocketFactory)SSLSocketFactory.getDefault();
      }

      return var1;
   }

   static Socket createSocket(InetSocketAddress var0, int var1, SslConfiguration var2, SocketOptions var3) throws IOException {
      SSLSocketFactory var4 = createSslSocketFactory(var2);
      SSLSocket var5 = (SSLSocket)var4.createSocket();
      if (var3 != null) {
         var3.apply(var5);
      }

      var5.connect(var0, var1);
      if (var3 != null) {
         var3.apply(var5);
      }

      return var5;
   }

   private static class SslFactoryData extends TcpSocketManager.FactoryData {
      protected SslConfiguration sslConfiguration;

      public SslFactoryData(
         SslConfiguration var1, String var2, int var3, int var4, int var5, boolean var6, Layout<? extends Serializable> var7, int var8, SocketOptions var9
      ) {
         super(var2, var3, var4, var5, var6, var7, var8, var9);
         this.sslConfiguration = var1;
      }

      @Override
      public String toString() {
         return "SslFactoryData [sslConfiguration="
            + this.sslConfiguration
            + ", host="
            + this.host
            + ", port="
            + this.port
            + ", connectTimeoutMillis="
            + this.connectTimeoutMillis
            + ", reconnectDelayMillis="
            + this.reconnectDelayMillis
            + ", immediateFail="
            + this.immediateFail
            + ", layout="
            + this.layout
            + ", bufferSize="
            + this.bufferSize
            + ", socketOptions="
            + this.socketOptions
            + "]";
      }
   }

   private static class SslSocketManagerFactory extends TcpSocketManager.TcpSocketManagerFactory<SslSocketManager, SslSocketManager.SslFactoryData> {
      private SslSocketManagerFactory() {
      }

      SslSocketManager createManager(String var1, OutputStream var2, Socket var3, InetAddress var4, SslSocketManager.SslFactoryData var5) {
         return new SslSocketManager(
            var1,
            var2,
            var3,
            var5.sslConfiguration,
            var4,
            var5.host,
            var5.port,
            var5.connectTimeoutMillis,
            var5.reconnectDelayMillis,
            var5.immediateFail,
            var5.layout,
            var5.bufferSize,
            var5.socketOptions
         );
      }

      Socket createSocket(SslSocketManager.SslFactoryData var1) throws IOException {
         List var2 = resolver.resolveHost(var1.host, var1.port);
         IOException var3 = null;

         for (InetSocketAddress var5 : var2) {
            try {
               return SslSocketManager.createSocket(var5, var1.connectTimeoutMillis, var1.sslConfiguration, var1.socketOptions);
            } catch (IOException var7) {
               var3 = var7;
            }
         }

         throw new IOException(this.errorMessage(var1, var2), var3);
      }
   }
}
