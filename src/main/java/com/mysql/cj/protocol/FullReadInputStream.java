package com.mysql.cj.protocol;

import com.mysql.cj.Messages;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FullReadInputStream extends FilterInputStream {
   public FullReadInputStream(InputStream underlyingStream) {
      super(underlyingStream);
   }

   public InputStream getUnderlyingStream() {
      return this.in;
   }

   public int readFully(byte[] b) throws IOException {
      return this.readFully(b, 0, b.length);
   }

   public int readFully(byte[] b, int off, int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int n = 0;

         while (n < len) {
            int count = this.read(b, off + n, len - n);
            if (count < 0) {
               throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[]{len, n}));
            }

            n += count;
         }

         return n;
      }
   }

   public long skipFully(long len) throws IOException {
      if (len < 0L) {
         throw new IOException(Messages.getString("MysqlIO.105"));
      } else {
         long n = 0L;

         while (n < len) {
            long count = this.skip(len - n);
            if (count < 0L) {
               throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[]{len, n}));
            }

            n += count;
         }

         return n;
      }
   }

   public int skipLengthEncodedInteger() throws IOException {
      int sw = this.read() & 0xFF;
      switch (sw) {
         case 252:
            return (int)this.skipFully(2L) + 1;
         case 253:
            return (int)this.skipFully(3L) + 1;
         case 254:
            return (int)this.skipFully(8L) + 1;
         default:
            return 1;
      }
   }
}
