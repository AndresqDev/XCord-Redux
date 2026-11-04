package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class MultiInputStream extends InputStream {
   private Iterator<? extends ByteSource> it;
   private InputStream in;

   public MultiInputStream(Iterator<? extends ByteSource> var1) throws IOException {
      this.it = Preconditions.checkNotNull(var1);
      this.advance();
   }

   @Override
   public void close() throws IOException {
      if (this.in != null) {
         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }
   }

   private void advance() throws IOException {
      this.close();
      if (this.it.hasNext()) {
         this.in = this.it.next().openStream();
      }
   }

   @Override
   public int available() throws IOException {
      return this.in == null ? 0 : this.in.available();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int read() throws IOException {
      while (this.in != null) {
         int var1 = this.in.read();
         if (var1 != -1) {
            return var1;
         }

         this.advance();
      }

      return -1;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      Preconditions.checkNotNull(var1);

      while (this.in != null) {
         int var4 = this.in.read(var1, var2, var3);
         if (var4 != -1) {
            return var4;
         }

         this.advance();
      }

      return -1;
   }

   @Override
   public long skip(long var1) throws IOException {
      if (this.in != null && var1 > 0L) {
         long var3 = this.in.skip(var1);
         if (var3 != 0L) {
            return var3;
         } else {
            return this.read() == -1 ? 0L : 1L + this.in.skip(var1 - 1L);
         }
      } else {
         return 0L;
      }
   }
}
