package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class ByteStreamLogger {
   private static final int BUFFER_SIZE = 1024;
   private final ExtendedLogger logger;
   private final Level level;
   private final Marker marker;
   private final InputStreamReader reader;
   private final char[] msgBuf = new char[1024];
   private final StringBuilder msg = new StringBuilder();
   private boolean closed;
   private final ByteBuffer buf = ByteBuffer.allocate(1024);

   public ByteStreamLogger(ExtendedLogger var1, Level var2, Marker var3, Charset var4) {
      this.logger = var1;
      this.level = var2 == null ? var1.getLevel() : var2;
      this.marker = var3;
      this.reader = new InputStreamReader(new ByteStreamLogger.ByteBufferInputStream(), var4 == null ? Charset.defaultCharset() : var4);
   }

   public void close(String var1) {
      synchronized (this.msg) {
         this.closed = true;
         this.logEnd(var1);
      }
   }

   private void extractMessages(String var1) throws IOException {
      if (!this.closed) {
         for (int var2 = this.reader.read(this.msgBuf); var2 > 0; var2 = this.reader.read(this.msgBuf)) {
            int var3 = 0;

            for (int var4 = 0; var4 < var2; var4++) {
               switch (this.msgBuf[var4]) {
                  case '\n':
                     this.msg.append(this.msgBuf, var3, var4 - var3);
                     var3 = var4 + 1;
                     this.log(var1);
                     break;
                  case '\r':
                     this.msg.append(this.msgBuf, var3, var4 - var3);
                     var3 = var4 + 1;
               }
            }

            this.msg.append(this.msgBuf, var3, var2 - var3);
         }
      }
   }

   private void log(String var1) {
      this.logger.logIfEnabled(var1, this.level, this.marker, this.msg.toString());
      this.msg.setLength(0);
   }

   private void logEnd(String var1) {
      if (this.msg.length() > 0) {
         this.log(var1);
      }
   }

   public void put(String var1, byte[] var2, int var3, int var4) throws IOException {
      int var5 = var3;
      int var6 = var4;
      if (var4 >= 0) {
         synchronized (this.msg) {
            while (var6 > this.buf.remaining()) {
               int var8 = this.buf.remaining();
               this.buf.put(var2, var5, var8);
               var6 -= var8;
               var5 += var8;
               this.extractMessages(var1);
            }

            this.buf.put(var2, var5, var6);
            this.extractMessages(var1);
         }
      } else {
         this.logEnd(var1);
      }
   }

   public void put(String var1, int var2) throws IOException {
      if (var2 >= 0) {
         synchronized (this.msg) {
            this.buf.put((byte)(var2 & 0xFF));
            this.extractMessages(var1);
         }
      } else {
         this.logEnd(var1);
      }
   }

   private class ByteBufferInputStream extends InputStream {
      private ByteBufferInputStream() {
      }

      @Override
      public int read() throws IOException {
         ((Buffer)ByteStreamLogger.this.buf).flip();
         int var1 = -1;
         if (ByteStreamLogger.this.buf.limit() > 0) {
            var1 = ByteStreamLogger.this.buf.get() & 255;
         }

         ByteStreamLogger.this.buf.compact();
         return var1;
      }

      @Override
      public int read(byte[] var1, int var2, int var3) throws IOException {
         ((Buffer)ByteStreamLogger.this.buf).flip();
         int var4 = -1;
         if (ByteStreamLogger.this.buf.limit() > 0) {
            var4 = Math.min(var3, ByteStreamLogger.this.buf.limit());
            ByteStreamLogger.this.buf.get(var1, var2, var4);
         }

         ByteStreamLogger.this.buf.compact();
         return var4;
      }
   }
}
