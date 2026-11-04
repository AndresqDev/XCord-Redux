package org.apache.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpMessage;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.ContentLengthOutputStream;
import org.apache.http.impl.io.EmptyInputStream;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.NetUtils;

public class BHttpConnectionBase implements HttpInetConnection {
   private final SessionInputBufferImpl inBuffer;
   private final SessionOutputBufferImpl outbuffer;
   private final MessageConstraints messageConstraints;
   private final HttpConnectionMetricsImpl connMetrics;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;
   private final AtomicReference<Socket> socketHolder;

   protected BHttpConnectionBase(
      int var1, int var2, CharsetDecoder var3, CharsetEncoder var4, MessageConstraints var5, ContentLengthStrategy var6, ContentLengthStrategy var7
   ) {
      Args.positive(var1, "Buffer size");
      HttpTransportMetricsImpl var8 = new HttpTransportMetricsImpl();
      HttpTransportMetricsImpl var9 = new HttpTransportMetricsImpl();
      this.inBuffer = new SessionInputBufferImpl(var8, var1, -1, var5 != null ? var5 : MessageConstraints.DEFAULT, var3);
      this.outbuffer = new SessionOutputBufferImpl(var9, var1, var2, var4);
      this.messageConstraints = var5;
      this.connMetrics = new HttpConnectionMetricsImpl(var8, var9);
      this.incomingContentStrategy = (ContentLengthStrategy)(var6 != null ? var6 : LaxContentLengthStrategy.INSTANCE);
      this.outgoingContentStrategy = (ContentLengthStrategy)(var7 != null ? var7 : StrictContentLengthStrategy.INSTANCE);
      this.socketHolder = new AtomicReference<>();
   }

   protected void ensureOpen() throws IOException {
      Socket var1 = this.socketHolder.get();
      if (var1 == null) {
         throw new ConnectionClosedException();
      } else {
         if (!this.inBuffer.isBound()) {
            this.inBuffer.bind(this.getSocketInputStream(var1));
         }

         if (!this.outbuffer.isBound()) {
            this.outbuffer.bind(this.getSocketOutputStream(var1));
         }
      }
   }

   protected InputStream getSocketInputStream(Socket var1) throws IOException {
      return var1.getInputStream();
   }

   protected OutputStream getSocketOutputStream(Socket var1) throws IOException {
      return var1.getOutputStream();
   }

   protected void bind(Socket var1) throws IOException {
      Args.notNull(var1, "Socket");
      this.socketHolder.set(var1);
      this.inBuffer.bind(null);
      this.outbuffer.bind(null);
   }

   protected SessionInputBuffer getSessionInputBuffer() {
      return this.inBuffer;
   }

   protected SessionOutputBuffer getSessionOutputBuffer() {
      return this.outbuffer;
   }

   protected void doFlush() throws IOException {
      this.outbuffer.flush();
   }

   @Override
   public boolean isOpen() {
      return this.socketHolder.get() != null;
   }

   protected Socket getSocket() {
      return this.socketHolder.get();
   }

   protected OutputStream createOutputStream(long var1, SessionOutputBuffer var3) {
      if (var1 == -2L) {
         return new ChunkedOutputStream(2048, var3);
      } else {
         return (OutputStream)(var1 == -1L ? new IdentityOutputStream(var3) : new ContentLengthOutputStream(var3, var1));
      }
   }

   protected OutputStream prepareOutput(HttpMessage var1) throws HttpException {
      long var2 = this.outgoingContentStrategy.determineLength(var1);
      return this.createOutputStream(var2, this.outbuffer);
   }

   protected InputStream createInputStream(long var1, SessionInputBuffer var3) {
      if (var1 == -2L) {
         return new ChunkedInputStream(var3, this.messageConstraints);
      } else if (var1 == -1L) {
         return new IdentityInputStream(var3);
      } else {
         return (InputStream)(var1 == 0L ? EmptyInputStream.INSTANCE : new ContentLengthInputStream(var3, var1));
      }
   }

   protected HttpEntity prepareInput(HttpMessage var1) throws HttpException {
      BasicHttpEntity var2 = new BasicHttpEntity();
      long var3 = this.incomingContentStrategy.determineLength(var1);
      InputStream var5 = this.createInputStream(var3, this.inBuffer);
      if (var3 == -2L) {
         var2.setChunked(true);
         var2.setContentLength(-1L);
         var2.setContent(var5);
      } else if (var3 == -1L) {
         var2.setChunked(false);
         var2.setContentLength(-1L);
         var2.setContent(var5);
      } else {
         var2.setChunked(false);
         var2.setContentLength(var3);
         var2.setContent(var5);
      }

      Header var6 = var1.getFirstHeader("Content-Type");
      if (var6 != null) {
         var2.setContentType(var6);
      }

      Header var7 = var1.getFirstHeader("Content-Encoding");
      if (var7 != null) {
         var2.setContentEncoding(var7);
      }

      return var2;
   }

   @Override
   public InetAddress getLocalAddress() {
      Socket var1 = this.socketHolder.get();
      return var1 != null ? var1.getLocalAddress() : null;
   }

   @Override
   public int getLocalPort() {
      Socket var1 = this.socketHolder.get();
      return var1 != null ? var1.getLocalPort() : -1;
   }

   @Override
   public InetAddress getRemoteAddress() {
      Socket var1 = this.socketHolder.get();
      return var1 != null ? var1.getInetAddress() : null;
   }

   @Override
   public int getRemotePort() {
      Socket var1 = this.socketHolder.get();
      return var1 != null ? var1.getPort() : -1;
   }

   @Override
   public void setSocketTimeout(int var1) {
      Socket var2 = this.socketHolder.get();
      if (var2 != null) {
         try {
            var2.setSoTimeout(var1);
         } catch (SocketException var4) {
         }
      }
   }

   @Override
   public int getSocketTimeout() {
      Socket var1 = this.socketHolder.get();
      if (var1 != null) {
         try {
            return var1.getSoTimeout();
         } catch (SocketException var3) {
         }
      }

      return -1;
   }

   @Override
   public void shutdown() throws IOException {
      Socket var1 = this.socketHolder.getAndSet(null);
      if (var1 != null) {
         try {
            var1.setSoLinger(true, 0);
         } catch (IOException var6) {
         } finally {
            var1.close();
         }
      }
   }

   @Override
   public void close() throws IOException {
      Socket var1 = this.socketHolder.getAndSet(null);
      if (var1 != null) {
         try {
            this.inBuffer.clear();
            this.outbuffer.flush();

            try {
               try {
                  var1.shutdownOutput();
               } catch (IOException var9) {
               }

               try {
                  var1.shutdownInput();
               } catch (IOException var8) {
               }
            } catch (UnsupportedOperationException var10) {
            }
         } finally {
            var1.close();
         }
      }
   }

   private int fillInputBuffer(int var1) throws IOException {
      Socket var2 = this.socketHolder.get();
      int var3 = var2.getSoTimeout();

      int var4;
      try {
         var2.setSoTimeout(var1);
         var4 = this.inBuffer.fillBuffer();
      } finally {
         var2.setSoTimeout(var3);
      }

      return var4;
   }

   protected boolean awaitInput(int var1) throws IOException {
      if (this.inBuffer.hasBufferedData()) {
         return true;
      } else {
         this.fillInputBuffer(var1);
         return this.inBuffer.hasBufferedData();
      }
   }

   @Override
   public boolean isStale() {
      if (!this.isOpen()) {
         return true;
      } else {
         try {
            int var1 = this.fillInputBuffer(1);
            return var1 < 0;
         } catch (SocketTimeoutException var2) {
            return false;
         } catch (IOException var3) {
            return true;
         }
      }
   }

   protected void incrementRequestCount() {
      this.connMetrics.incrementRequestCount();
   }

   protected void incrementResponseCount() {
      this.connMetrics.incrementResponseCount();
   }

   @Override
   public HttpConnectionMetrics getMetrics() {
      return this.connMetrics;
   }

   @Override
   public String toString() {
      Socket var1 = this.socketHolder.get();
      if (var1 != null) {
         StringBuilder var2 = new StringBuilder();
         SocketAddress var3 = var1.getRemoteSocketAddress();
         SocketAddress var4 = var1.getLocalSocketAddress();
         if (var3 != null && var4 != null) {
            NetUtils.formatAddress(var2, var4);
            var2.append("<->");
            NetUtils.formatAddress(var2, var3);
         }

         return var2.toString();
      } else {
         return "[Not bound]";
      }
   }
}
