package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.crypto.spec.SecretKeySpec;

@ElementTypesAreNonnullByDefault
@Beta
public final class Hashing {
   static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();

   public static HashFunction goodFastHash(int var0) {
      int var1 = checkPositiveAndMakeMultipleOf32(var0);
      if (var1 == 32) {
         return Murmur3_32HashFunction.GOOD_FAST_HASH_32;
      } else if (var1 <= 128) {
         return Murmur3_128HashFunction.GOOD_FAST_HASH_128;
      } else {
         int var2 = (var1 + 127) / 128;
         HashFunction[] var3 = new HashFunction[var2];
         var3[0] = Murmur3_128HashFunction.GOOD_FAST_HASH_128;
         int var4 = GOOD_FAST_HASH_SEED;

         for (int var5 = 1; var5 < var2; var5++) {
            var4 += 1500450271;
            var3[var5] = murmur3_128(var4);
         }

         return new Hashing.ConcatenatedHashFunction(var3);
      }
   }

   @Deprecated
   public static HashFunction murmur3_32(int var0) {
      return new Murmur3_32HashFunction(var0, false);
   }

   @Deprecated
   public static HashFunction murmur3_32() {
      return Murmur3_32HashFunction.MURMUR3_32;
   }

   public static HashFunction murmur3_32_fixed(int var0) {
      return new Murmur3_32HashFunction(var0, true);
   }

   public static HashFunction murmur3_32_fixed() {
      return Murmur3_32HashFunction.MURMUR3_32_FIXED;
   }

   public static HashFunction murmur3_128(int var0) {
      return new Murmur3_128HashFunction(var0);
   }

   public static HashFunction murmur3_128() {
      return Murmur3_128HashFunction.MURMUR3_128;
   }

   public static HashFunction sipHash24() {
      return SipHashFunction.SIP_HASH_24;
   }

   public static HashFunction sipHash24(long var0, long var2) {
      return new SipHashFunction(2, 4, var0, var2);
   }

   @Deprecated
   public static HashFunction md5() {
      return Hashing.Md5Holder.MD5;
   }

   @Deprecated
   public static HashFunction sha1() {
      return Hashing.Sha1Holder.SHA_1;
   }

   public static HashFunction sha256() {
      return Hashing.Sha256Holder.SHA_256;
   }

   public static HashFunction sha384() {
      return Hashing.Sha384Holder.SHA_384;
   }

   public static HashFunction sha512() {
      return Hashing.Sha512Holder.SHA_512;
   }

   public static HashFunction hmacMd5(Key var0) {
      return new MacHashFunction("HmacMD5", var0, hmacToString("hmacMd5", var0));
   }

   public static HashFunction hmacMd5(byte[] var0) {
      return hmacMd5(new SecretKeySpec(Preconditions.checkNotNull(var0), "HmacMD5"));
   }

   public static HashFunction hmacSha1(Key var0) {
      return new MacHashFunction("HmacSHA1", var0, hmacToString("hmacSha1", var0));
   }

   public static HashFunction hmacSha1(byte[] var0) {
      return hmacSha1(new SecretKeySpec(Preconditions.checkNotNull(var0), "HmacSHA1"));
   }

   public static HashFunction hmacSha256(Key var0) {
      return new MacHashFunction("HmacSHA256", var0, hmacToString("hmacSha256", var0));
   }

   public static HashFunction hmacSha256(byte[] var0) {
      return hmacSha256(new SecretKeySpec(Preconditions.checkNotNull(var0), "HmacSHA256"));
   }

   public static HashFunction hmacSha512(Key var0) {
      return new MacHashFunction("HmacSHA512", var0, hmacToString("hmacSha512", var0));
   }

   public static HashFunction hmacSha512(byte[] var0) {
      return hmacSha512(new SecretKeySpec(Preconditions.checkNotNull(var0), "HmacSHA512"));
   }

   private static String hmacToString(String var0, Key var1) {
      return String.format("Hashing.%s(Key[algorithm=%s, format=%s])", var0, var1.getAlgorithm(), var1.getFormat());
   }

   public static HashFunction crc32c() {
      return Crc32cHashFunction.CRC_32_C;
   }

   public static HashFunction crc32() {
      return Hashing.ChecksumType.CRC_32.hashFunction;
   }

   public static HashFunction adler32() {
      return Hashing.ChecksumType.ADLER_32.hashFunction;
   }

   public static HashFunction farmHashFingerprint64() {
      return FarmHashFingerprint64.FARMHASH_FINGERPRINT_64;
   }

   public static int consistentHash(HashCode var0, int var1) {
      return consistentHash(var0.padToLong(), var1);
   }

   public static int consistentHash(long var0, int var2) {
      Preconditions.checkArgument(var2 > 0, "buckets must be positive: %s", var2);
      Hashing.LinearCongruentialGenerator var3 = new Hashing.LinearCongruentialGenerator(var0);
      int var4 = 0;

      while (true) {
         int var5 = (int)((double)(var4 + 1) / var3.nextDouble());
         if (var5 < 0 || var5 >= var2) {
            return var4;
         }

         var4 = var5;
      }
   }

   public static HashCode combineOrdered(Iterable<HashCode> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "Must be at least 1 hash code to combine.");
      int var2 = ((HashCode)var1.next()).bits();
      byte[] var3 = new byte[var2 / 8];

      for (HashCode var5 : var0) {
         byte[] var6 = var5.asBytes();
         Preconditions.checkArgument(var6.length == var3.length, "All hashcodes must have the same bit length.");

         for (int var7 = 0; var7 < var6.length; var7++) {
            var3[var7] = (byte)(var3[var7] * 37 ^ var6[var7]);
         }
      }

      return HashCode.fromBytesNoCopy(var3);
   }

   public static HashCode combineUnordered(Iterable<HashCode> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "Must be at least 1 hash code to combine.");
      byte[] var2 = new byte[((HashCode)var1.next()).bits() / 8];

      for (HashCode var4 : var0) {
         byte[] var5 = var4.asBytes();
         Preconditions.checkArgument(var5.length == var2.length, "All hashcodes must have the same bit length.");

         for (int var6 = 0; var6 < var5.length; var6++) {
            var2[var6] += var5[var6];
         }
      }

      return HashCode.fromBytesNoCopy(var2);
   }

   static int checkPositiveAndMakeMultipleOf32(int var0) {
      Preconditions.checkArgument(var0 > 0, "Number of bits must be positive");
      return var0 + 31 & -32;
   }

   public static HashFunction concatenating(HashFunction var0, HashFunction var1, HashFunction... var2) {
      ArrayList var3 = new ArrayList();
      var3.add(var0);
      var3.add(var1);
      var3.addAll(Arrays.asList(var2));
      return new Hashing.ConcatenatedHashFunction(var3.toArray(new HashFunction[0]));
   }

   public static HashFunction concatenating(Iterable<HashFunction> var0) {
      Preconditions.checkNotNull(var0);
      ArrayList var1 = new ArrayList();

      for (HashFunction var3 : var0) {
         var1.add(var3);
      }

      Preconditions.checkArgument(var1.size() > 0, "number of hash functions (%s) must be > 0", var1.size());
      return new Hashing.ConcatenatedHashFunction(var1.toArray(new HashFunction[0]));
   }

   private Hashing() {
   }

   @Immutable
   static enum ChecksumType implements ImmutableSupplier<Checksum> {
      CRC_32("Hashing.crc32()") {
         public Checksum get() {
            return new CRC32();
         }
      },
      ADLER_32("Hashing.adler32()") {
         public Checksum get() {
            return new Adler32();
         }
      };

      public final HashFunction hashFunction;

      private ChecksumType(String var3) {
         this.hashFunction = new ChecksumHashFunction(this, 32, var3);
      }
   }

   private static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {
      private ConcatenatedHashFunction(HashFunction... var1) {
         super(var1);

         for (HashFunction var5 : var1) {
            Preconditions.checkArgument(var5.bits() % 8 == 0, "the number of bits (%s) in hashFunction (%s) must be divisible by 8", var5.bits(), var5);
         }
      }

      @Override
      HashCode makeHash(Hasher[] var1) {
         byte[] var2 = new byte[this.bits() / 8];
         int var3 = 0;

         for (Hasher var7 : var1) {
            HashCode var8 = var7.hash();
            var3 += var8.writeBytesTo(var2, var3, var8.bits() / 8);
         }

         return HashCode.fromBytesNoCopy(var2);
      }

      @Override
      public int bits() {
         int var1 = 0;

         for (HashFunction var5 : this.functions) {
            var1 += var5.bits();
         }

         return var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Hashing.ConcatenatedHashFunction) {
            Hashing.ConcatenatedHashFunction var2 = (Hashing.ConcatenatedHashFunction)var1;
            return Arrays.equals((Object[])this.functions, (Object[])var2.functions);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.functions);
      }
   }

   private static final class LinearCongruentialGenerator {
      private long state;

      public LinearCongruentialGenerator(long var1) {
         this.state = var1;
      }

      public double nextDouble() {
         this.state = 2862933555777941757L * this.state + 1L;
         return (double)((int)(this.state >>> 33) + 1) / 2.1474836E9F;
      }
   }

   private static class Md5Holder {
      static final HashFunction MD5 = new MessageDigestHashFunction("MD5", "Hashing.md5()");
   }

   private static class Sha1Holder {
      static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");
   }

   private static class Sha256Holder {
      static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256", "Hashing.sha256()");
   }

   private static class Sha384Holder {
      static final HashFunction SHA_384 = new MessageDigestHashFunction("SHA-384", "Hashing.sha384()");
   }

   private static class Sha512Holder {
      static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512", "Hashing.sha512()");
   }
}
