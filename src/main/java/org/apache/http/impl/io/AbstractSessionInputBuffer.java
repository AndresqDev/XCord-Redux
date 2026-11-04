package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.Consts;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
public abstract class AbstractSessionInputBuffer implements BufferInfo, SessionInputBuffer {
   private InputStream inStream;
   private byte[] buffer;
   private ByteArrayBuffer lineBuffer;
   private Charset charset;
   private boolean ascii;
   private int maxLineLen;
   private int minChunkLimit;
   private HttpTransportMetricsImpl metrics;
   private CodingErrorAction onMalformedCharAction;
   private CodingErrorAction onUnmappableCharAction;
   private int bufferPos;
   private int bufferLen;
   private CharsetDecoder decoder;
   private CharBuffer cbuf;

   protected void init(InputStream var1, int var2, HttpParams var3) {
      Args.notNull(var1, "Input stream");
      Args.notNegative(var2, "Buffer size");
      Args.notNull(var3, "HTTP parameters");
      this.inStream = var1;
      this.buffer = new byte[var2];
      this.bufferPos = 0;
      this.bufferLen = 0;
      this.lineBuffer = new ByteArrayBuffer(var2);
      String var4 = (String)var3.getParameter("http.protocol.element-charset");
      this.charset = var4 != null ? Charset.forName(var4) : Consts.ASCII;
      this.ascii = this.charset.equals(Consts.ASCII);
      this.decoder = null;
      this.maxLineLen = var3.getIntParameter("http.connection.max-line-length", -1);
      this.minChunkLimit = var3.getIntParameter("http.connection.min-chunk-limit", 512);
      this.metrics = this.createTransportMetrics();
      CodingErrorAction var5 = (CodingErrorAction)var3.getParameter("http.malformed.input.action");
      this.onMalformedCharAction = var5 != null ? var5 : CodingErrorAction.REPORT;
      CodingErrorAction var6 = (CodingErrorAction)var3.getParameter("http.unmappable.input.action");
      this.onUnmappableCharAction = var6 != null ? var6 : CodingErrorAction.REPORT;
   }

   protected HttpTransportMetricsImpl createTransportMetrics() {
      return new HttpTransportMetricsImpl();
   }

   @Override
   public int capacity() {
      return this.buffer.length;
   }

   @Override
   public int length() {
      return this.bufferLen - this.bufferPos;
   }

   @Override
   public int available() {
      return this.capacity() - this.length();
   }

   protected int fillBuffer() throws IOException {
      if (this.bufferPos > 0) {
         int var1 = this.bufferLen - this.bufferPos;
         if (var1 > 0) {
            System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, var1);
         }

         this.bufferPos = 0;
         this.bufferLen = var1;
      }

      int var2 = this.bufferLen;
      int var3 = this.buffer.length - var2;
      int var4 = this.inStream.read(this.buffer, var2, var3);
      if (var4 == -1) {
         return -1;
      } else {
         this.bufferLen = var2 + var4;
         this.metrics.incrementBytesTransferred((long)var4);
         return var4;
      }
   }

   protected boolean hasBufferedData() {
      return this.bufferPos < this.bufferLen;
   }

   @Override
   public int read() throws IOException {
      while (!this.hasBufferedData()) {
         int var1 = this.fillBuffer();
         if (var1 == -1) {
            return -1;
         }
      }

      return this.buffer[this.bufferPos++] & 0xFF;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         return 0;
      } else if (this.hasBufferedData()) {
         int var7 = Math.min(var3, this.bufferLen - this.bufferPos);
         System.arraycopy(this.buffer, this.bufferPos, var1, var2, var7);
         this.bufferPos += var7;
         return var7;
      } else if (var3 > this.minChunkLimit) {
         int var6 = this.inStream.read(var1, var2, var3);
         if (var6 > 0) {
            this.metrics.incrementBytesTransferred((long)var6);
         }

         return var6;
      } else {
         while (!this.hasBufferedData()) {
            int var4 = this.fillBuffer();
            if (var4 == -1) {
               return -1;
            }
         }

         int var5 = Math.min(var3, this.bufferLen - this.bufferPos);
         System.arraycopy(this.buffer, this.bufferPos, var1, var2, var5);
         this.bufferPos += var5;
         return var5;
      }
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return var1 == null ? 0 : this.read(var1, 0, var1.length);
   }

   private int locateLF() {
      for (int var1 = this.bufferPos; var1 < this.bufferLen; var1++) {
         if (this.buffer[var1] == 10) {
            return var1;
         }
      }

      return -1;
   }

   @Override
   public int readLine(CharArrayBuffer var1) throws IOException {
      Args.notNull(var1, "Char array buffer");
      int var2 = 0;
      boolean var3 = true;

      while (var3) {
         int var4 = this.locateLF();
         if (var4 != -1) {
            if (this.lineBuffer.isEmpty()) {
               return this.lineFromReadBuffer(var1, var4);
            }

            var3 = false;
            int var5 = var4 + 1 - this.bufferPos;
            this.lineBuffer.append(this.buffer, this.bufferPos, var5);
            this.bufferPos = var4 + 1;
         } else {
            if (this.hasBufferedData()) {
               int var6 = this.bufferLen - this.bufferPos;
               this.lineBuffer.append(this.buffer, this.bufferPos, var6);
               this.bufferPos = this.bufferLen;
            }

            var2 = this.fillBuffer();
            if (var2 == -1) {
               var3 = false;
            }
         }

         if (this.maxLineLen > 0 && this.lineBuffer.length() >= this.maxLineLen) {
            throw new IOException("Maximum line length limit exceeded");
         }
      }

      return var2 == -1 && this.lineBuffer.isEmpty() ? -1 : this.lineFromLineBuffer(var1);
   }

   private int lineFromLineBuffer(CharArrayBuffer var1) throws IOException {
      int var2 = this.lineBuffer.length();
      if (var2 > 0) {
         if (this.lineBuffer.byteAt(var2 - 1) == 10) {
            var2--;
         }

         if (var2 > 0 && this.lineBuffer.byteAt(var2 - 1) == 13) {
            var2--;
         }
      }

      if (this.ascii) {
         var1.append(this.lineBuffer, 0, var2);
      } else {
         ByteBuffer var3 = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, var2);
         var2 = this.appendDecoded(var1, var3);
      }

      this.lineBuffer.clear();
      return var2;
   }

   private int lineFromReadBuffer(CharArrayBuffer var1, int var2) throws IOException {
      int var3 = this.bufferPos;
      int var4 = var2;
      this.bufferPos = var2 + 1;
      if (var2 > var3 && this.buffer[var2 - 1] == 13) {
         var4 = var2 - 1;
      }

      int var5 = var4 - var3;
      if (this.ascii) {
         var1.append(this.buffer, var3, var5);
      } else {
         ByteBuffer var6 = ByteBuffer.wrap(this.buffer, var3, var5);
         var5 = this.appendDecoded(var1, var6);
      }

      return var5;
   }

   private int appendDecoded(CharArrayBuffer var1, ByteBuffer var2) throws IOException {
      if (!var2.hasRemaining()) {
         return 0;
      } else {
         if (this.decoder == null) {
            this.decoder = this.charset.newDecoder();
            this.decoder.onMalformedInput(this.onMalformedCharAction);
            this.decoder.onUnmappableCharacter(this.onUnmappableCharAction);
         }

         if (this.cbuf == null) {
            this.cbuf = CharBuffer.allocate(1024);
         }

         this.decoder.reset();
         int var3 = 0;

         while (var2.hasRemaining()) {
            CoderResult var4 = this.decoder.decode(var2, this.cbuf, true);
            var3 += this.handleDecodingResult(var4, var1, var2);
         }

         CoderResult var6 = this.decoder.flush(this.cbuf);
         var3 += this.handleDecodingResult(var6, var1, var2);
         ((Buffer)this.cbuf).clear();
         return var3;
      }
   }

   private int handleDecodingResult(CoderResult var1, CharArrayBuffer var2, ByteBuffer var3) throws IOException {
      if (var1.isError()) {
         var1.throwException();
      }

      ((Buffer)this.cbuf).flip();
      int var4 = this.cbuf.remaining();

      while (this.cbuf.hasRemaining()) {
         var2.append(this.cbuf.get());
      }

      this.cbuf.compact();
      return var4;
   }

   @Override
   public String readLine() throws IOException {
      CharArrayBuffer var1 = new CharArrayBuffer(64);
      int var2 = this.readLine(var1);
      return var2 != -1 ? var1.toString() : null;
   }

   @Override
   public HttpTransportMetrics getMetrics() {
      return this.metrics;
   }
}
