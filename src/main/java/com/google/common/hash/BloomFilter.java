package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.math.DoubleMath;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

@ElementTypesAreNonnullByDefault
@Beta
public final class BloomFilter<T> implements Predicate<T>, Serializable {
   private final BloomFilterStrategies.LockFreeBitArray bits;
   private final int numHashFunctions;
   private final Funnel<? super T> funnel;
   private final BloomFilter.Strategy strategy;

   private BloomFilter(BloomFilterStrategies.LockFreeBitArray var1, int var2, Funnel<? super T> var3, BloomFilter.Strategy var4) {
      Preconditions.checkArgument(var2 > 0, "numHashFunctions (%s) must be > 0", var2);
      Preconditions.checkArgument(var2 <= 255, "numHashFunctions (%s) must be <= 255", var2);
      this.bits = Preconditions.checkNotNull(var1);
      this.numHashFunctions = var2;
      this.funnel = Preconditions.checkNotNull(var3);
      this.strategy = Preconditions.checkNotNull(var4);
   }

   public BloomFilter<T> copy() {
      return new BloomFilter<>(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
   }

   public boolean mightContain(@ParametricNullness T var1) {
      return this.strategy.mightContain(var1, this.funnel, this.numHashFunctions, this.bits);
   }

   @Deprecated
   @Override
   public boolean apply(@ParametricNullness T var1) {
      return this.mightContain((T)var1);
   }

   @CanIgnoreReturnValue
   public boolean put(@ParametricNullness T var1) {
      return this.strategy.put(var1, this.funnel, this.numHashFunctions, this.bits);
   }

   public double expectedFpp() {
      return Math.pow((double)this.bits.bitCount() / (double)this.bitSize(), (double)this.numHashFunctions);
   }

   public long approximateElementCount() {
      long var1 = this.bits.bitSize();
      long var3 = this.bits.bitCount();
      double var5 = (double)var3 / (double)var1;
      return DoubleMath.roundToLong(-Math.log1p(-var5) * (double)var1 / (double)this.numHashFunctions, RoundingMode.HALF_UP);
   }

   @VisibleForTesting
   long bitSize() {
      return this.bits.bitSize();
   }

   public boolean isCompatible(BloomFilter<T> var1) {
      Preconditions.checkNotNull(var1);
      return this != var1
         && this.numHashFunctions == var1.numHashFunctions
         && this.bitSize() == var1.bitSize()
         && this.strategy.equals(var1.strategy)
         && this.funnel.equals(var1.funnel);
   }

   public void putAll(BloomFilter<T> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this != var1, "Cannot combine a BloomFilter with itself.");
      Preconditions.checkArgument(
         this.numHashFunctions == var1.numHashFunctions,
         "BloomFilters must have the same number of hash functions (%s != %s)",
         this.numHashFunctions,
         var1.numHashFunctions
      );
      Preconditions.checkArgument(
         this.bitSize() == var1.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", this.bitSize(), var1.bitSize()
      );
      Preconditions.checkArgument(this.strategy.equals(var1.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, var1.strategy);
      Preconditions.checkArgument(this.funnel.equals(var1.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, var1.funnel);
      this.bits.putAll(var1.bits);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BloomFilter)) {
         return false;
      } else {
         BloomFilter var2 = (BloomFilter)var1;
         return this.numHashFunctions == var2.numHashFunctions
            && this.funnel.equals(var2.funnel)
            && this.bits.equals(var2.bits)
            && this.strategy.equals(var2.strategy);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.numHashFunctions, this.funnel, this.strategy, this.bits);
   }

   public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> var0, long var1) {
      return toBloomFilter(var0, var1, 0.03);
   }

   public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> var0, long var1, double var3) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0L, "Expected insertions (%s) must be >= 0", var1);
      Preconditions.checkArgument(var3 > 0.0, "False positive probability (%s) must be > 0.0", var3);
      Preconditions.checkArgument(var3 < 1.0, "False positive probability (%s) must be < 1.0", var3);
      return Collector.of(() -> create(var0, var1, var3), BloomFilter::put, (var0x, var1x) -> {
         var0x.putAll(var1x);
         return var0x;
      }, Characteristics.UNORDERED, Characteristics.CONCURRENT);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, int var1, double var2) {
      return create(var0, (long)var1, var2);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1, double var3) {
      return create(var0, var1, var3, BloomFilterStrategies.MURMUR128_MITZ_64);
   }

   @VisibleForTesting
   static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1, double var3, BloomFilter.Strategy var5) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0L, "Expected insertions (%s) must be >= 0", var1);
      Preconditions.checkArgument(var3 > 0.0, "False positive probability (%s) must be > 0.0", var3);
      Preconditions.checkArgument(var3 < 1.0, "False positive probability (%s) must be < 1.0", var3);
      Preconditions.checkNotNull(var5);
      if (var1 == 0L) {
         var1 = 1L;
      }

      long var6 = optimalNumOfBits(var1, var3);
      int var8 = optimalNumOfHashFunctions(var1, var6);

      try {
         return new BloomFilter<>(new BloomFilterStrategies.LockFreeBitArray(var6), var8, var0, var5);
      } catch (IllegalArgumentException var10) {
         throw new IllegalArgumentException(new StringBuilder(57).append("Could not create BloomFilter of ").append(var6).append(" bits").toString(), var10);
      }
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, int var1) {
      return create(var0, (long)var1);
   }

   public static <T> BloomFilter<T> create(Funnel<? super T> var0, long var1) {
      return create(var0, var1, 0.03);
   }

   @VisibleForTesting
   static int optimalNumOfHashFunctions(long var0, long var2) {
      return Math.max(1, (int)Math.round((double)var2 / (double)var0 * Math.log(2.0)));
   }

   @VisibleForTesting
   static long optimalNumOfBits(long var0, double var2) {
      if (var2 == 0.0) {
         var2 = Double.MIN_VALUE;
      }

      return (long)((double)(-var0) * Math.log(var2) / (Math.log(2.0) * Math.log(2.0)));
   }

   private Object writeReplace() {
      return new BloomFilter.SerialForm<>(this);
   }

   public void writeTo(OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(var1);
      var2.writeByte(SignedBytes.checkedCast((long)this.strategy.ordinal()));
      var2.writeByte(UnsignedBytes.checkedCast((long)this.numHashFunctions));
      var2.writeInt(this.bits.data.length());

      for (int var3 = 0; var3 < this.bits.data.length(); var3++) {
         var2.writeLong(this.bits.data.get(var3));
      }
   }

   public static <T> BloomFilter<T> readFrom(InputStream var0, Funnel<? super T> var1) throws IOException {
      Preconditions.checkNotNull(var0, "InputStream");
      Preconditions.checkNotNull(var1, "Funnel");
      byte var2 = -1;
      int var3 = -1;
      int var4 = -1;

      try {
         DataInputStream var5 = new DataInputStream(var0);
         var2 = var5.readByte();
         var3 = UnsignedBytes.toInt(var5.readByte());
         var4 = var5.readInt();
         BloomFilterStrategies var11 = BloomFilterStrategies.values()[var2];
         long[] var7 = new long[var4];

         for (int var8 = 0; var8 < var7.length; var8++) {
            var7[var8] = var5.readLong();
         }

         return new BloomFilter<>(new BloomFilterStrategies.LockFreeBitArray(var7), var3, var1, var11);
      } catch (RuntimeException var10) {
         String var6 = new StringBuilder(134)
            .append("Unable to deserialize BloomFilter from InputStream. strategyOrdinal: ")
            .append((int)var2)
            .append(" numHashFunctions: ")
            .append(var3)
            .append(" dataLength: ")
            .append(var4)
            .toString();
         throw new IOException(var6, var10);
      }
   }

   private static class SerialForm<T> implements Serializable {
      final long[] data;
      final int numHashFunctions;
      final Funnel<? super T> funnel;
      final BloomFilter.Strategy strategy;
      private static final long serialVersionUID = 1L;

      SerialForm(BloomFilter<T> var1) {
         this.data = BloomFilterStrategies.LockFreeBitArray.toPlainArray(var1.bits.data);
         this.numHashFunctions = var1.numHashFunctions;
         this.funnel = var1.funnel;
         this.strategy = var1.strategy;
      }

      Object readResolve() {
         return new BloomFilter(new BloomFilterStrategies.LockFreeBitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
      }
   }

   interface Strategy extends Serializable {
      <T> boolean put(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4);

      <T> boolean mightContain(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4);

      int ordinal();
   }
}
