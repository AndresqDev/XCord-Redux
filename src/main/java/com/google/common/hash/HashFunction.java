package com.google.common.hash;

import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@Immutable
@ElementTypesAreNonnullByDefault
public interface HashFunction {
   Hasher newHasher();

   Hasher newHasher(int var1);

   HashCode hashInt(int var1);

   HashCode hashLong(long var1);

   HashCode hashBytes(byte[] var1);

   HashCode hashBytes(byte[] var1, int var2, int var3);

   HashCode hashBytes(ByteBuffer var1);

   HashCode hashUnencodedChars(CharSequence var1);

   HashCode hashString(CharSequence var1, Charset var2);

   <T> HashCode hashObject(@ParametricNullness T var1, Funnel<? super T> var2);

   int bits();
}
