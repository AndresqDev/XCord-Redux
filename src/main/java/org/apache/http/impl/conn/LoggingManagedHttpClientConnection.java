package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

class LoggingManagedHttpClientConnection extends DefaultManagedHttpClientConnection {
   private final Log log;
   private final Log headerLog;
   private final Wire wire;

   public LoggingManagedHttpClientConnection(
      String var1,
      Log var2,
      Log var3,
      Log var4,
      int var5,
      int var6,
      CharsetDecoder var7,
      CharsetEncoder var8,
      MessageConstraints var9,
      ContentLengthStrategy var10,
      ContentLengthStrategy var11,
      HttpMessageWriterFactory<HttpRequest> var12,
      HttpMessageParserFactory<HttpResponse> var13
   ) {
      super(var1, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.log = var2;
      this.headerLog = var3;
      this.wire = new Wire(var4, var1);
   }

   @Override
   public void close() throws IOException {
      if (super.isOpen()) {
         if (this.log.isDebugEnabled()) {
            this.log.debug(this.getId() + ": Close connection");
         }

         super.close();
      }
   }

   @Override
   public void setSocketTimeout(int var1) {
      if (this.log.isDebugEnabled()) {
         this.log.debug(this.getId() + ": set socket timeout to " + var1);
      }

      super.setSocketTimeout(var1);
   }

   @Override
   public void shutdown() throws IOException {
      if (this.log.isDebugEnabled()) {
         this.log.debug(this.getId() + ": Shutdown connection");
      }

      super.shutdown();
   }

   @Override
   protected InputStream getSocketInputStream(Socket var1) throws IOException {
      Object var2 = super.getSocketInputStream(var1);
      if (this.wire.enabled()) {
         var2 = new LoggingInputStream((InputStream)var2, this.wire);
      }

      return (InputStream)var2;
   }

   @Override
   protected OutputStream getSocketOutputStream(Socket var1) throws IOException {
      Object var2 = super.getSocketOutputStream(var1);
      if (this.wire.enabled()) {
         var2 = new LoggingOutputStream((OutputStream)var2, this.wire);
      }

      return (OutputStream)var2;
   }

   @Override
   protected void onResponseReceived(HttpResponse var1) {
      if (var1 != null && this.headerLog.isDebugEnabled()) {
         this.headerLog.debug(this.getId() + " << " + var1.getStatusLine().toString());
         Header[] var2 = var1.getAllHeaders();

         for (Header var6 : var2) {
            this.headerLog.debug(this.getId() + " << " + var6.toString());
         }
      }
   }

   @Override
   protected void onRequestSubmitted(HttpRequest var1) {
      if (var1 != null && this.headerLog.isDebugEnabled()) {
         this.headerLog.debug(this.getId() + " >> " + var1.getRequestLine().toString());
         Header[] var2 = var1.getAllHeaders();

         for (Header var6 : var2) {
            this.headerLog.debug(this.getId() + " >> " + var6.toString());
         }
      }
   }
}
