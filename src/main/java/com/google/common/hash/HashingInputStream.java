package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@ElementTypesAreNonnullByDefault
@Beta
public final class HashingInputStream extends FilterInputStream {
   private final Hasher hasher;

   public HashingInputStream(HashFunction var1, InputStream var2) {
      super(Preconditions.checkNotNull(var2));
      this.hasher = Preconditions.checkNotNull(var1.newHasher());
   }

   @CanIgnoreReturnValue
   @Override
   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 != -1) {
         this.hasher.putByte((byte)var1);
      }

      return var1;
   }

   @CanIgnoreReturnValue
   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (var4 != -1) {
         this.hasher.putBytes(var1, var2, var4);
      }

      return var4;
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public void mark(int var1) {
   }

   @Override
   public void reset() throws IOException {
      throw new IOException("reset not supported");
   }

   public HashCode hash() {
      return this.hasher.hash();
   }
}
