package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;

public class ContentLengthOutputStream extends OutputStream {
   private final SessionOutputBuffer out;
   private final long contentLength;
   private long total;
   private boolean closed;

   public ContentLengthOutputStream(SessionOutputBuffer var1, long var2) {
      this.out = Args.notNull(var1, "Session output buffer");
      this.contentLength = Args.notNegative(var2, "Content length");
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         this.out.flush();
      }
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("Attempted write to closed stream.");
      } else {
         if (this.total < this.contentLength) {
            long var4 = this.contentLength - this.total;
            int var6 = var3;
            if ((long)var3 > var4) {
               var6 = (int)var4;
            }

            this.out.write(var1, var2, var6);
            this.total += (long)var6;
         }
      }
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   @Override
   public void write(int var1) throws IOException {
      if (this.closed) {
         throw new IOException("Attempted write to closed stream.");
      } else {
         if (this.total < this.contentLength) {
            this.out.write(var1);
            this.total++;
         }
      }
   }
}
