package org.apache.http.impl.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.HttpService;

public class HttpServer {
   private final int port;
   private final InetAddress ifAddress;
   private final SocketConfig socketConfig;
   private final ServerSocketFactory serverSocketFactory;
   private final HttpService httpService;
   private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
   private final SSLServerSetupHandler sslSetupHandler;
   private final ExceptionLogger exceptionLogger;
   private final ThreadPoolExecutor listenerExecutorService;
   private final ThreadGroup workerThreads;
   private final WorkerPoolExecutor workerExecutorService;
   private final AtomicReference<HttpServer.Status> status;
   private volatile ServerSocket serverSocket;
   private volatile RequestListener requestListener;

   HttpServer(
      int var1,
      InetAddress var2,
      SocketConfig var3,
      ServerSocketFactory var4,
      HttpService var5,
      HttpConnectionFactory<? extends DefaultBHttpServerConnection> var6,
      SSLServerSetupHandler var7,
      ExceptionLogger var8
   ) {
      this.port = var1;
      this.ifAddress = var2;
      this.socketConfig = var3;
      this.serverSocketFactory = var4;
      this.httpService = var5;
      this.connectionFactory = var6;
      this.sslSetupHandler = var7;
      this.exceptionLogger = var8;
      this.listenerExecutorService = new ThreadPoolExecutor(
         1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadFactoryImpl("HTTP-listener-" + this.port)
      );
      this.workerThreads = new ThreadGroup("HTTP-workers");
      this.workerExecutorService = new WorkerPoolExecutor(
         0, Integer.MAX_VALUE, 1L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryImpl("HTTP-worker", this.workerThreads)
      );
      this.status = new AtomicReference<>(HttpServer.Status.READY);
   }

   public InetAddress getInetAddress() {
      ServerSocket var1 = this.serverSocket;
      return var1 != null ? var1.getInetAddress() : null;
   }

   public int getLocalPort() {
      ServerSocket var1 = this.serverSocket;
      return var1 != null ? var1.getLocalPort() : -1;
   }

   public void start() throws IOException {
      if (this.status.compareAndSet(HttpServer.Status.READY, HttpServer.Status.ACTIVE)) {
         this.serverSocket = this.serverSocketFactory.createServerSocket(this.port, this.socketConfig.getBacklogSize(), this.ifAddress);
         this.serverSocket.setReuseAddress(this.socketConfig.isSoReuseAddress());
         if (this.socketConfig.getRcvBufSize() > 0) {
            this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
         }

         if (this.sslSetupHandler != null && this.serverSocket instanceof SSLServerSocket) {
            this.sslSetupHandler.initialize((SSLServerSocket)this.serverSocket);
         }

         this.requestListener = new RequestListener(
            this.socketConfig, this.serverSocket, this.httpService, this.connectionFactory, this.exceptionLogger, this.workerExecutorService
         );
         this.listenerExecutorService.execute(this.requestListener);
      }
   }

   public void stop() {
      if (this.status.compareAndSet(HttpServer.Status.ACTIVE, HttpServer.Status.STOPPING)) {
         this.listenerExecutorService.shutdown();
         this.workerExecutorService.shutdown();
         RequestListener var1 = this.requestListener;
         if (var1 != null) {
            try {
               var1.terminate();
            } catch (IOException var3) {
               this.exceptionLogger.log(var3);
            }
         }

         this.workerThreads.interrupt();
      }
   }

   public void awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      this.workerExecutorService.awaitTermination(var1, var3);
   }

   public void shutdown(long var1, TimeUnit var3) {
      this.stop();
      if (var1 > 0L) {
         try {
            this.awaitTermination(var1, var3);
         } catch (InterruptedException var10) {
            Thread.currentThread().interrupt();
         }
      }

      for (Worker var6 : this.workerExecutorService.getWorkers()) {
         HttpServerConnection var7 = var6.getConnection();

         try {
            var7.shutdown();
         } catch (IOException var9) {
            this.exceptionLogger.log(var9);
         }
      }
   }

   static enum Status {
      READY,
      ACTIVE,
      STOPPING;
   }
}
