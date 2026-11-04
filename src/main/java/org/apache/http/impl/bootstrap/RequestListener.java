package org.apache.http.impl.bootstrap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.config.SocketConfig;
import org.apache.http.protocol.HttpService;

class RequestListener implements Runnable {
   private final SocketConfig socketConfig;
   private final ServerSocket serversocket;
   private final HttpService httpService;
   private final HttpConnectionFactory<? extends HttpServerConnection> connectionFactory;
   private final ExceptionLogger exceptionLogger;
   private final ExecutorService executorService;
   private final AtomicBoolean terminated;

   public RequestListener(
      SocketConfig var1,
      ServerSocket var2,
      HttpService var3,
      HttpConnectionFactory<? extends HttpServerConnection> var4,
      ExceptionLogger var5,
      ExecutorService var6
   ) {
      this.socketConfig = var1;
      this.serversocket = var2;
      this.connectionFactory = var4;
      this.httpService = var3;
      this.exceptionLogger = var5;
      this.executorService = var6;
      this.terminated = new AtomicBoolean(false);
   }

   @Override
   public void run() {
      try {
         while (!this.isTerminated() && !Thread.interrupted()) {
            Socket var1 = this.serversocket.accept();
            var1.setSoTimeout(this.socketConfig.getSoTimeout());
            var1.setKeepAlive(this.socketConfig.isSoKeepAlive());
            var1.setTcpNoDelay(this.socketConfig.isTcpNoDelay());
            if (this.socketConfig.getRcvBufSize() > 0) {
               var1.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
            }

            if (this.socketConfig.getSndBufSize() > 0) {
               var1.setSendBufferSize(this.socketConfig.getSndBufSize());
            }

            if (this.socketConfig.getSoLinger() >= 0) {
               var1.setSoLinger(true, this.socketConfig.getSoLinger());
            }

            HttpServerConnection var2 = this.connectionFactory.createConnection(var1);
            Worker var3 = new Worker(this.httpService, var2, this.exceptionLogger);
            this.executorService.execute(var3);
         }
      } catch (Exception var4) {
         this.exceptionLogger.log(var4);
      }
   }

   public boolean isTerminated() {
      return this.terminated.get();
   }

   public void terminate() throws IOException {
      if (this.terminated.compareAndSet(false, true)) {
         this.serversocket.close();
      }
   }
}
