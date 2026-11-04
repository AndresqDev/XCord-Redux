package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class DeflateInputStream extends InputStream {
   private final InputStream sourceStream;

   public DeflateInputStream(InputStream var1) throws IOException {
      PushbackInputStream var2 = new PushbackInputStream(var1, 2);
      int var3 = var2.read();
      int var4 = var2.read();
      if (var3 != -1 && var4 != -1) {
         var2.unread(var4);
         var2.unread(var3);
         boolean var5 = true;
         int var6 = var3 & 0xFF;
         int var7 = var6 & 15;
         int var8 = var6 >> 4 & 15;
         int var9 = var4 & 0xFF;
         if (var7 == 8 && var8 <= 7 && (var6 << 8 | var9) % 31 == 0) {
            var5 = false;
         }

         this.sourceStream = new DeflateInputStream.DeflateStream(var2, new Inflater(var5));
      } else {
         throw new ZipException("Unexpected end of stream");
      }
   }

   @Override
   public int read() throws IOException {
      return this.sourceStream.read();
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.sourceStream.read(var1);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.sourceStream.read(var1, var2, var3);
   }

   @Override
   public long skip(long var1) throws IOException {
      return this.sourceStream.skip(var1);
   }

   @Override
   public int available() throws IOException {
      return this.sourceStream.available();
   }

   @Override
   public void mark(int var1) {
      this.sourceStream.mark(var1);
   }

   @Override
   public void reset() throws IOException {
      this.sourceStream.reset();
   }

   @Override
   public boolean markSupported() {
      return this.sourceStream.markSupported();
   }

   @Override
   public void close() throws IOException {
      this.sourceStream.close();
   }

   static class DeflateStream extends InflaterInputStream {
      private boolean closed = false;

      public DeflateStream(InputStream var1, Inflater var2) {
         super(var1, var2);
      }

      @Override
      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            this.inf.end();
            super.close();
         }
      }
   }
}
