package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.protocol.HttpContext;

class CPoolProxy implements ManagedHttpClientConnection, HttpContext {
   private volatile CPoolEntry poolEntry;

   CPoolProxy(CPoolEntry var1) {
      this.poolEntry = var1;
   }

   CPoolEntry getPoolEntry() {
      return this.poolEntry;
   }

   CPoolEntry detach() {
      CPoolEntry var1 = this.poolEntry;
      this.poolEntry = null;
      return var1;
   }

   ManagedHttpClientConnection getConnection() {
      CPoolEntry var1 = this.poolEntry;
      return var1 == null ? null : var1.getConnection();
   }

   ManagedHttpClientConnection getValidConnection() {
      ManagedHttpClientConnection var1 = this.getConnection();
      if (var1 == null) {
         throw new ConnectionShutdownException();
      } else {
         return var1;
      }
   }

   @Override
   public void close() throws IOException {
      CPoolEntry var1 = this.poolEntry;
      if (var1 != null) {
         var1.closeConnection();
      }
   }

   @Override
   public void shutdown() throws IOException {
      CPoolEntry var1 = this.poolEntry;
      if (var1 != null) {
         var1.shutdownConnection();
      }
   }

   @Override
   public boolean isOpen() {
      CPoolEntry var1 = this.poolEntry;
      return var1 != null ? !var1.isClosed() : false;
   }

   @Override
   public boolean isStale() {
      ManagedHttpClientConnection var1 = this.getConnection();
      return var1 != null ? var1.isStale() : true;
   }

   @Override
   public void setSocketTimeout(int var1) {
      this.getValidConnection().setSocketTimeout(var1);
   }

   @Override
   public int getSocketTimeout() {
      return this.getValidConnection().getSocketTimeout();
   }

   @Override
   public String getId() {
      return this.getValidConnection().getId();
   }

   @Override
   public void bind(Socket var1) throws IOException {
      this.getValidConnection().bind(var1);
   }

   @Override
   public Socket getSocket() {
      return this.getValidConnection().getSocket();
   }

   @Override
   public SSLSession getSSLSession() {
      return this.getValidConnection().getSSLSession();
   }

   @Override
   public boolean isResponseAvailable(int var1) throws IOException {
      return this.getValidConnection().isResponseAvailable(var1);
   }

   @Override
   public void sendRequestHeader(HttpRequest var1) throws HttpException, IOException {
      this.getValidConnection().sendRequestHeader(var1);
   }

   @Override
   public void sendRequestEntity(HttpEntityEnclosingRequest var1) throws HttpException, IOException {
      this.getValidConnection().sendRequestEntity(var1);
   }

   @Override
   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
      return this.getValidConnection().receiveResponseHeader();
   }

   @Override
   public void receiveResponseEntity(HttpResponse var1) throws HttpException, IOException {
      this.getValidConnection().receiveResponseEntity(var1);
   }

   @Override
   public void flush() throws IOException {
      this.getValidConnection().flush();
   }

   @Override
   public HttpConnectionMetrics getMetrics() {
      return this.getValidConnection().getMetrics();
   }

   @Override
   public InetAddress getLocalAddress() {
      return this.getValidConnection().getLocalAddress();
   }

   @Override
   public int getLocalPort() {
      return this.getValidConnection().getLocalPort();
   }

   @Override
   public InetAddress getRemoteAddress() {
      return this.getValidConnection().getRemoteAddress();
   }

   @Override
   public int getRemotePort() {
      return this.getValidConnection().getRemotePort();
   }

   @Override
   public Object getAttribute(String var1) {
      ManagedHttpClientConnection var2 = this.getValidConnection();
      return var2 instanceof HttpContext ? ((HttpContext)var2).getAttribute(var1) : null;
   }

   @Override
   public void setAttribute(String var1, Object var2) {
      ManagedHttpClientConnection var3 = this.getValidConnection();
      if (var3 instanceof HttpContext) {
         ((HttpContext)var3).setAttribute(var1, var2);
      }
   }

   @Override
   public Object removeAttribute(String var1) {
      ManagedHttpClientConnection var2 = this.getValidConnection();
      return var2 instanceof HttpContext ? ((HttpContext)var2).removeAttribute(var1) : null;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("CPoolProxy{");
      ManagedHttpClientConnection var2 = this.getConnection();
      if (var2 != null) {
         var1.append(var2);
      } else {
         var1.append("detached");
      }

      var1.append('}');
      return var1.toString();
   }

   public static HttpClientConnection newProxy(CPoolEntry var0) {
      return new CPoolProxy(var0);
   }

   private static CPoolProxy getProxy(HttpClientConnection var0) {
      if (!CPoolProxy.class.isInstance(var0)) {
         throw new IllegalStateException("Unexpected connection proxy class: " + var0.getClass());
      } else {
         return CPoolProxy.class.cast(var0);
      }
   }

   public static CPoolEntry getPoolEntry(HttpClientConnection var0) {
      CPoolEntry var1 = getProxy(var0).getPoolEntry();
      if (var1 == null) {
         throw new ConnectionShutdownException();
      } else {
         return var1;
      }
   }

   public static CPoolEntry detach(HttpClientConnection var0) {
      return getProxy(var0).detach();
   }
}
