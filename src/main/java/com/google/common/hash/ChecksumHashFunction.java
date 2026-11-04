package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.zip.Checksum;

@Immutable
@ElementTypesAreNonnullByDefault
final class ChecksumHashFunction extends AbstractHashFunction implements Serializable {
   private final ImmutableSupplier<? extends Checksum> checksumSupplier;
   private final int bits;
   private final String toString;
   private static final long serialVersionUID = 0L;

   ChecksumHashFunction(ImmutableSupplier<? extends Checksum> var1, int var2, String var3) {
      this.checksumSupplier = Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(var2 == 32 || var2 == 64, "bits (%s) must be either 32 or 64", var2);
      this.bits = var2;
      this.toString = Preconditions.checkNotNull(var3);
   }

   @Override
   public int bits() {
      return this.bits;
   }

   @Override
   public Hasher newHasher() {
      return new ChecksumHashFunction.ChecksumHasher(this.checksumSupplier.get());
   }

   @Override
   public String toString() {
      return this.toString;
   }

   private final class ChecksumHasher extends AbstractByteHasher {
      private final Checksum checksum;

      private ChecksumHasher(Checksum var2) {
         this.checksum = Preconditions.checkNotNull(var2);
      }

      @Override
      protected void update(byte var1) {
         this.checksum.update(var1);
      }

      @Override
      protected void update(byte[] var1, int var2, int var3) {
         this.checksum.update(var1, var2, var3);
      }

      @Override
      public HashCode hash() {
         long var1 = this.checksum.getValue();
         return ChecksumHashFunction.this.bits == 32 ? HashCode.fromInt((int)var1) : HashCode.fromLong(var1);
      }
   }
}
