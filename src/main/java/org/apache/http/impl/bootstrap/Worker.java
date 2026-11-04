package org.apache.http.impl.bootstrap;

import java.io.IOException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpService;

class Worker implements Runnable {
   private final HttpService httpservice;
   private final HttpServerConnection conn;
   private final ExceptionLogger exceptionLogger;

   Worker(HttpService var1, HttpServerConnection var2, ExceptionLogger var3) {
      this.httpservice = var1;
      this.conn = var2;
      this.exceptionLogger = var3;
   }

   public HttpServerConnection getConnection() {
      return this.conn;
   }

   @Override
   public void run() {
      try {
         BasicHttpContext var1 = new BasicHttpContext();
         HttpCoreContext var2 = HttpCoreContext.adapt(var1);

         while (!Thread.interrupted() && this.conn.isOpen()) {
            this.httpservice.handleRequest(this.conn, var2);
            var1.clear();
         }

         this.conn.close();
      } catch (Exception var11) {
         this.exceptionLogger.log(var11);
      } finally {
         try {
            this.conn.shutdown();
         } catch (IOException var10) {
            this.exceptionLogger.log(var10);
         }
      }
   }
}
