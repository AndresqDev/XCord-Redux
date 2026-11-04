package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.apache.http.MessageConstraintException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

public class SessionInputBufferImpl implements BufferInfo, SessionInputBuffer {
   private final HttpTransportMetricsImpl metrics;
   private final byte[] buffer;
   private final ByteArrayBuffer lineBuffer;
   private final int minChunkLimit;
   private final MessageConstraints constraints;
   private final CharsetDecoder decoder;
   private InputStream inStream;
   private int bufferPos;
   private int bufferLen;
   private CharBuffer cbuf;

   public SessionInputBufferImpl(HttpTransportMetricsImpl var1, int var2, int var3, MessageConstraints var4, CharsetDecoder var5) {
      Args.notNull(var1, "HTTP transport metrcis");
      Args.positive(var2, "Buffer size");
      this.metrics = var1;
      this.buffer = new byte[var2];
      this.bufferPos = 0;
      this.bufferLen = 0;
      this.minChunkLimit = var3 >= 0 ? var3 : 512;
      this.constraints = var4 != null ? var4 : MessageConstraints.DEFAULT;
      this.lineBuffer = new ByteArrayBuffer(var2);
      this.decoder = var5;
   }

   public SessionInputBufferImpl(HttpTransportMetricsImpl var1, int var2) {
      this(var1, var2, var2, null, null);
   }

   public void bind(InputStream var1) {
      this.inStream = var1;
   }

   public boolean isBound() {
      return this.inStream != null;
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

   private int streamRead(byte[] var1, int var2, int var3) throws IOException {
      Asserts.notNull(this.inStream, "Input stream");
      return this.inStream.read(var1, var2, var3);
   }

   public int fillBuffer() throws IOException {
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
      int var4 = this.streamRead(this.buffer, var2, var3);
      if (var4 == -1) {
         return -1;
      } else {
         this.bufferLen = var2 + var4;
         this.metrics.incrementBytesTransferred((long)var4);
         return var4;
      }
   }

   public boolean hasBufferedData() {
      return this.bufferPos < this.bufferLen;
   }

   public void clear() {
      this.bufferPos = 0;
      this.bufferLen = 0;
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
         int var6 = this.streamRead(var1, var2, var3);
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

   @Override
   public int readLine(CharArrayBuffer var1) throws IOException {
      Args.notNull(var1, "Char array buffer");
      int var2 = this.constraints.getMaxLineLength();
      int var3 = 0;
      boolean var4 = true;

      while (var4) {
         int var5 = -1;

         for (int var6 = this.bufferPos; var6 < this.bufferLen; var6++) {
            if (this.buffer[var6] == 10) {
               var5 = var6;
               break;
            }
         }

         if (var2 > 0) {
            int var7 = this.lineBuffer.length() + (var5 >= 0 ? var5 : this.bufferLen) - this.bufferPos;
            if (var7 >= var2) {
               throw new MessageConstraintException("Maximum line length limit exceeded");
            }
         }

         if (var5 != -1) {
            if (this.lineBuffer.isEmpty()) {
               return this.lineFromReadBuffer(var1, var5);
            }

            var4 = false;
            int var8 = var5 + 1 - this.bufferPos;
            this.lineBuffer.append(this.buffer, this.bufferPos, var8);
            this.bufferPos = var5 + 1;
         } else {
            if (this.hasBufferedData()) {
               int var9 = this.bufferLen - this.bufferPos;
               this.lineBuffer.append(this.buffer, this.bufferPos, var9);
               this.bufferPos = this.bufferLen;
            }

            var3 = this.fillBuffer();
            if (var3 == -1) {
               var4 = false;
            }
         }
      }

      return var3 == -1 && this.lineBuffer.isEmpty() ? -1 : this.lineFromLineBuffer(var1);
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

      if (this.decoder == null) {
         var1.append(this.lineBuffer, 0, var2);
      } else {
         ByteBuffer var3 = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, var2);
         var2 = this.appendDecoded(var1, var3);
      }

      this.lineBuffer.clear();
      return var2;
   }

   private int lineFromReadBuffer(CharArrayBuffer var1, int var2) throws IOException {
      int var3 = var2;
      int var4 = this.bufferPos;
      this.bufferPos = var2 + 1;
      if (var2 > var4 && this.buffer[var2 - 1] == 13) {
         var3 = var2 - 1;
      }

      int var5 = var3 - var4;
      if (this.decoder == null) {
         var1.append(this.buffer, var4, var5);
      } else {
         ByteBuffer var6 = ByteBuffer.wrap(this.buffer, var4, var5);
         var5 = this.appendDecoded(var1, var6);
      }

      return var5;
   }

   private int appendDecoded(CharArrayBuffer var1, ByteBuffer var2) throws IOException {
      if (!var2.hasRemaining()) {
         return 0;
      } else {
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
   public boolean isDataAvailable(int var1) throws IOException {
      return this.hasBufferedData();
   }

   @Override
   public HttpTransportMetrics getMetrics() {
      return this.metrics;
   }
}
