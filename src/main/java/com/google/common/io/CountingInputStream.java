package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class CountingInputStream extends FilterInputStream {
   private long count;
   private long mark = -1L;

   public CountingInputStream(InputStream var1) {
      super(Preconditions.checkNotNull(var1));
   }

   public long getCount() {
      return this.count;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 != -1) {
         this.count++;
      }

      return var1;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (var4 != -1) {
         this.count += (long)var4;
      }

      return var4;
   }

   @Override
   public long skip(long var1) throws IOException {
      long var3 = this.in.skip(var1);
      this.count += var3;
      return var3;
   }

   @Override
   public synchronized void mark(int var1) {
      this.in.mark(var1);
      this.mark = this.count;
   }

   @Override
   public synchronized void reset() throws IOException {
      if (!this.in.markSupported()) {
         throw new IOException("Mark not supported");
      } else if (this.mark == -1L) {
         throw new IOException("Mark not set");
      } else {
         this.in.reset();
         this.count = this.mark;
      }
   }
}
