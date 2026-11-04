package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.util.NullOutputStream;
import org.apache.logging.log4j.util.Strings;

public class TcpSocketManager extends AbstractSocketManager {
   public static final int DEFAULT_RECONNECTION_DELAY_MILLIS = 30000;
   private static final int DEFAULT_PORT = 4560;
   private static final TcpSocketManager.TcpSocketManagerFactory<TcpSocketManager, TcpSocketManager.FactoryData> FACTORY = new TcpSocketManager.TcpSocketManagerFactory<>();
   private final int reconnectionDelayMillis;
   private TcpSocketManager.Reconnector reconnector;
   private Socket socket;
   private final SocketOptions socketOptions;
   private final boolean retry;
   private final boolean immediateFail;
   private final int connectTimeoutMillis;

   @Deprecated
   public TcpSocketManager(
      String var1,
      OutputStream var2,
      Socket var3,
      InetAddress var4,
      String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      Layout<? extends Serializable> var10,
      int var11
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, null);
   }

   public TcpSocketManager(
      String var1,
      OutputStream var2,
      Socket var3,
      InetAddress var4,
      String var5,
      int var6,
      int var7,
      int var8,
      boolean var9,
      Layout<? extends Serializable> var10,
      int var11,
      SocketOptions var12
   ) {
      super(var1, var2, var4, var5, var6, var10, true, var11);
      this.connectTimeoutMillis = var7;
      this.reconnectionDelayMillis = var8;
      this.socket = var3;
      this.immediateFail = var9;
      this.retry = var8 > 0;
      if (var3 == null) {
         this.reconnector = this.createReconnector();
         this.reconnector.start();
      }

      this.socketOptions = var12;
   }

   @Deprecated
   public static TcpSocketManager getSocketManager(String var0, int var1, int var2, int var3, boolean var4, Layout<? extends Serializable> var5, int var6) {
      return getSocketManager(var0, var1, var2, var3, var4, var5, var6, null);
   }

   public static TcpSocketManager getSocketManager(
      String var0, int var1, int var2, int var3, boolean var4, Layout<? extends Serializable> var5, int var6, SocketOptions var7
   ) {
      if (Strings.isEmpty(var0)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if (var1 <= 0) {
            var1 = 4560;
         }

         if (var3 == 0) {
            var3 = 30000;
         }

         return (TcpSocketManager)getManager(
            "TCP:" + var0 + ':' + var1, new TcpSocketManager.FactoryData(var0, var1, var2, var3, var4, var5, var6, var7), FACTORY
         );
      }
   }

   @Override
   protected void write(byte[] var1, int var2, int var3, boolean var4) {
      if (this.socket == null) {
         if (this.reconnector != null && !this.immediateFail) {
            this.reconnector.latch();
         }

         if (this.socket == null) {
            throw new AppenderLoggingException("Error writing to " + this.getName() + ": socket not available");
         }
      }

      synchronized (this) {
         try {
            this.writeAndFlush(var1, var2, var3, var4);
         } catch (IOException var12) {
            String var7 = this.inetAddress + ":" + this.port;
            if (this.retry && this.reconnector == null) {
               this.reconnector = this.createReconnector();

               try {
                  this.reconnector.reconnect();
               } catch (IOException var11) {
                  LOGGER.debug(
                     "Cannot reestablish socket connection to {}: {}; starting reconnector thread {}",
                     var7,
                     var11.getLocalizedMessage(),
                     this.reconnector.getName(),
                     var11
                  );
                  this.reconnector.start();
                  throw new AppenderLoggingException(String.format("Error sending to %s for %s", this.getName(), var7), var12);
               }

               try {
                  this.writeAndFlush(var1, var2, var3, var4);
               } catch (IOException var10) {
                  throw new AppenderLoggingException(String.format("Error writing to %s after reestablishing connection for %s", this.getName(), var7), var12);
               }

               return;
            }

            String var8 = String.format("Error writing to %s for connection %s", this.getName(), var7);
            throw new AppenderLoggingException(var8, var12);
         }
      }
   }

   private void writeAndFlush(byte[] var1, int var2, int var3, boolean var4) throws IOException {
      OutputStream var5 = this.getOutputStream();
      var5.write(var1, var2, var3);
      if (var4) {
         var5.flush();
      }
   }

   @Override
   protected synchronized boolean closeOutputStream() {
      boolean var1 = super.closeOutputStream();
      if (this.reconnector != null) {
         this.reconnector.shutdown();
         this.reconnector.interrupt();
         this.reconnector = null;
      }

      Socket var2 = this.socket;
      this.socket = null;
      if (var2 != null) {
         try {
            var2.close();
         } catch (IOException var4) {
            LOGGER.error("Could not close socket {}", this.socket);
            return false;
         }
      }

      return var1;
   }

   public int getConnectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   @Override
   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap<>(super.getContentFormat());
      var1.put("protocol", "tcp");
      var1.put("direction", "out");
      return var1;
   }

   private TcpSocketManager.Reconnector createReconnector() {
      TcpSocketManager.Reconnector var1 = new TcpSocketManager.Reconnector(this);
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   }

   protected Socket createSocket(InetSocketAddress var1) throws IOException {
      return createSocket(var1, this.socketOptions, this.connectTimeoutMillis);
   }

   protected static Socket createSocket(InetSocketAddress var0, SocketOptions var1, int var2) throws IOException {
      LOGGER.debug("Creating socket {}", var0.toString());
      Socket var3 = new Socket();
      if (var1 != null) {
         var1.apply(var3);
      }

      var3.connect(var0, var2);
      if (var1 != null) {
         var1.apply(var3);
      }

      return var3;
   }

   public static void setHostResolver(TcpSocketManager.HostResolver var0) {
      TcpSocketManager.TcpSocketManagerFactory.resolver = var0;
   }

   public SocketOptions getSocketOptions() {
      return this.socketOptions;
   }

   public Socket getSocket() {
      return this.socket;
   }

   public int getReconnectionDelayMillis() {
      return this.reconnectionDelayMillis;
   }

   @Override
   public String toString() {
      return "TcpSocketManager [reconnectionDelayMillis="
         + this.reconnectionDelayMillis
         + ", reconnector="
         + this.reconnector
         + ", socket="
         + this.socket
         + ", socketOptions="
         + this.socketOptions
         + ", retry="
         + this.retry
         + ", immediateFail="
         + this.immediateFail
         + ", connectTimeoutMillis="
         + this.connectTimeoutMillis
         + ", inetAddress="
         + this.inetAddress
         + ", host="
         + this.host
         + ", port="
         + this.port
         + ", layout="
         + this.layout
         + ", byteBuffer="
         + this.byteBuffer
         + ", count="
         + this.count
         + "]";
   }

   static class FactoryData {
      protected final String host;
      protected final int port;
      protected final int connectTimeoutMillis;
      protected final int reconnectDelayMillis;
      protected final boolean immediateFail;
      protected final Layout<? extends Serializable> layout;
      protected final int bufferSize;
      protected final SocketOptions socketOptions;

      public FactoryData(String var1, int var2, int var3, int var4, boolean var5, Layout<? extends Serializable> var6, int var7, SocketOptions var8) {
         this.host = var1;
         this.port = var2;
         this.connectTimeoutMillis = var3;
         this.reconnectDelayMillis = var4;
         this.immediateFail = var5;
         this.layout = var6;
         this.bufferSize = var7;
         this.socketOptions = var8;
      }

      @Override
      public String toString() {
         return "FactoryData [host="
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

   public static class HostResolver {
      public List<InetSocketAddress> resolveHost(String var1, int var2) throws UnknownHostException {
         InetAddress[] var3 = InetAddress.getAllByName(var1);
         ArrayList var4 = new ArrayList(var3.length);

         for (InetAddress var8 : var3) {
            var4.add(new InetSocketAddress(var8, var2));
         }

         return var4;
      }
   }

   private class Reconnector extends Log4jThread {
      private final CountDownLatch latch = new CountDownLatch(1);
      private boolean shutdown = false;
      private final Object owner;

      public Reconnector(OutputStreamManager var2) {
         super("TcpSocketManager-Reconnector");
         this.owner = var2;
      }

      public void latch() {
         try {
            this.latch.await();
         } catch (InterruptedException var2) {
         }
      }

      public void shutdown() {
         this.shutdown = true;
      }

      @Override
      public void run() {
         while (!this.shutdown) {
            try {
               sleep((long)TcpSocketManager.this.reconnectionDelayMillis);
               this.reconnect();
            } catch (InterruptedException var7) {
               TcpSocketManager.LOGGER.debug("Reconnection interrupted.");
            } catch (ConnectException var8) {
               TcpSocketManager.LOGGER.debug("{}:{} refused connection", TcpSocketManager.this.host, TcpSocketManager.this.port);
            } catch (IOException var9) {
               TcpSocketManager.LOGGER.debug("Unable to reconnect to {}:{}", TcpSocketManager.this.host, TcpSocketManager.this.port);
            } finally {
               this.latch.countDown();
            }
         }
      }

      void reconnect() throws IOException {
         TcpSocketManager.FACTORY;
         List var1 = TcpSocketManager.TcpSocketManagerFactory.resolver.resolveHost(TcpSocketManager.this.host, TcpSocketManager.this.port);
         if (var1.size() == 1) {
            TcpSocketManager.LOGGER.debug("Reconnecting " + var1.get(0));
            this.connect((InetSocketAddress)var1.get(0));
         } else {
            IOException var2 = null;

            for (InetSocketAddress var4 : var1) {
               try {
                  TcpSocketManager.LOGGER.debug("Reconnecting " + var4);
                  this.connect(var4);
                  return;
               } catch (IOException var6) {
                  var2 = var6;
               }
            }

            throw var2;
         }
      }

      private void connect(InetSocketAddress var1) throws IOException {
         Socket var2 = TcpSocketManager.this.createSocket(var1);
         OutputStream var3 = var2.getOutputStream();
         InetAddress var4 = TcpSocketManager.this.socket != null ? TcpSocketManager.this.socket.getInetAddress() : null;
         synchronized (this.owner) {
            Closer.closeSilently(TcpSocketManager.this.getOutputStream());
            TcpSocketManager.this.setOutputStream(var3);
            TcpSocketManager.this.socket = var2;
            TcpSocketManager.this.reconnector = null;
            this.shutdown = true;
         }

         String var8 = var4 != null && var4.getHostAddress().equals(var1.getAddress().getHostAddress()) ? "reestablished" : "established";
         TcpSocketManager.LOGGER
            .debug("Connection to {}:{} {}: {}", TcpSocketManager.this.host, TcpSocketManager.this.port, var8, TcpSocketManager.this.socket);
      }

      @Override
      public String toString() {
         return "Reconnector [latch=" + this.latch + ", shutdown=" + this.shutdown + "]";
      }
   }

   protected static class TcpSocketManagerFactory<M extends TcpSocketManager, T extends TcpSocketManager.FactoryData> implements ManagerFactory<M, T> {
      static TcpSocketManager.HostResolver resolver = new TcpSocketManager.HostResolver();

      public M createManager(String var1, T var2) {
         InetAddress var3;
         try {
            var3 = InetAddress.getByName(var2.host);
         } catch (UnknownHostException var7) {
            TcpSocketManager.LOGGER.error("Could not find address of {}: {}", var2.host, var7, var7);
            return null;
         }

         Socket var5 = null;

         try {
            var5 = this.createSocket((T)var2);
            OutputStream var9 = var5.getOutputStream();
            return this.createManager(var1, var9, var5, var3, (T)var2);
         } catch (IOException var8) {
            TcpSocketManager.LOGGER.error("TcpSocketManager ({}) caught exception and will continue:", var1, var8);
            NullOutputStream var4 = NullOutputStream.getInstance();
            if (var2.reconnectDelayMillis == 0) {
               Closer.closeSilently(var5);
               return null;
            } else {
               return this.createManager(var1, var4, null, var3, (T)var2);
            }
         }
      }

      M createManager(String var1, OutputStream var2, Socket var3, InetAddress var4, T var5) {
         return (M)(new TcpSocketManager(
            var1,
            var2,
            var3,
            var4,
            var5.host,
            var5.port,
            var5.connectTimeoutMillis,
            var5.reconnectDelayMillis,
            var5.immediateFail,
            var5.layout,
            var5.bufferSize,
            var5.socketOptions
         ));
      }

      Socket createSocket(T var1) throws IOException {
         List var2 = resolver.resolveHost(var1.host, var1.port);
         IOException var3 = null;

         for (InetSocketAddress var5 : var2) {
            try {
               return TcpSocketManager.createSocket(var5, var1.socketOptions, var1.connectTimeoutMillis);
            } catch (IOException var7) {
               var3 = var7;
            }
         }

         throw new IOException(this.errorMessage((T)var1, var2), var3);
      }

      protected String errorMessage(T var1, List<InetSocketAddress> var2) {
         StringBuilder var3 = new StringBuilder("Unable to create socket for ");
         var3.append(var1.host).append(" at port ").append(var1.port);
         if (var2.size() == 1) {
            if (!((InetSocketAddress)var2.get(0)).getAddress().getHostAddress().equals(var1.host)) {
               var3.append(" using ip address ").append(((InetSocketAddress)var2.get(0)).getAddress().getHostAddress());
               var3.append(" and port ").append(((InetSocketAddress)var2.get(0)).getPort());
            }
         } else {
            var3.append(" using ip addresses and ports ");

            for (int var4 = 0; var4 < var2.size(); var4++) {
               if (var4 > 0) {
                  var3.append(", ");
                  var3.append(((InetSocketAddress)var2.get(var4)).getAddress().getHostAddress());
                  var3.append(":").append(((InetSocketAddress)var2.get(var4)).getPort());
               }
            }
         }

         return var3.toString();
      }
   }
}
