package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@Immutable
@ElementTypesAreNonnullByDefault
abstract class AbstractHashFunction implements HashFunction {
   @Override
   public <T> HashCode hashObject(@ParametricNullness T var1, Funnel<? super T> var2) {
      return this.newHasher().putObject(var1, var2).hash();
   }

   @Override
   public HashCode hashUnencodedChars(CharSequence var1) {
      int var2 = var1.length();
      return this.newHasher(var2 * 2).putUnencodedChars(var1).hash();
   }

   @Override
   public HashCode hashString(CharSequence var1, Charset var2) {
      return this.newHasher().putString(var1, var2).hash();
   }

   @Override
   public HashCode hashInt(int var1) {
      return this.newHasher(4).putInt(var1).hash();
   }

   @Override
   public HashCode hashLong(long var1) {
      return this.newHasher(8).putLong(var1).hash();
   }

   @Override
   public HashCode hashBytes(byte[] var1) {
      return this.hashBytes(var1, 0, var1.length);
   }

   @Override
   public HashCode hashBytes(byte[] var1, int var2, int var3) {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      return this.newHasher(var3).putBytes(var1, var2, var3).hash();
   }

   @Override
   public HashCode hashBytes(ByteBuffer var1) {
      return this.newHasher(var1.remaining()).putBytes(var1).hash();
   }

   @Override
   public Hasher newHasher(int var1) {
      Preconditions.checkArgument(var1 >= 0, "expectedInputSize must be >= 0 but was %s", var1);
      return this.newHasher();
   }
}
