package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class DiscreteDomain<C extends Comparable> {
   final boolean supportsFastOffset;

   public static DiscreteDomain<Integer> integers() {
      return DiscreteDomain.IntegerDomain.INSTANCE;
   }

   public static DiscreteDomain<Long> longs() {
      return DiscreteDomain.LongDomain.INSTANCE;
   }

   public static DiscreteDomain<BigInteger> bigIntegers() {
      return DiscreteDomain.BigIntegerDomain.INSTANCE;
   }

   protected DiscreteDomain() {
      this(false);
   }

   private DiscreteDomain(boolean var1) {
      this.supportsFastOffset = var1;
   }

   C offset(C var1, long var2) {
      Comparable var4 = var1;
      CollectPreconditions.checkNonnegative(var2, "distance");

      for (long var5 = 0L; var5 < var2; var5++) {
         var4 = this.next((C)var4);
         if (var4 == null) {
            String var7 = String.valueOf(var1);
            throw new IllegalArgumentException(
               new StringBuilder(51 + String.valueOf(var7).length())
                  .append("overflowed computing offset(")
                  .append(var7)
                  .append(", ")
                  .append(var2)
                  .append(")")
                  .toString()
            );
         }
      }

      return (C)var4;
   }

   public abstract C next(C var1);

   public abstract C previous(C var1);

   public abstract long distance(C var1, C var2);

   @CanIgnoreReturnValue
   public C minValue() {
      throw new NoSuchElementException();
   }

   @CanIgnoreReturnValue
   public C maxValue() {
      throw new NoSuchElementException();
   }

   private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
      private static final DiscreteDomain.BigIntegerDomain INSTANCE = new DiscreteDomain.BigIntegerDomain();
      private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
      private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
      private static final long serialVersionUID = 0L;

      BigIntegerDomain() {
         super(true);
      }

      public BigInteger next(BigInteger var1) {
         return var1.add(BigInteger.ONE);
      }

      public BigInteger previous(BigInteger var1) {
         return var1.subtract(BigInteger.ONE);
      }

      BigInteger offset(BigInteger var1, long var2) {
         CollectPreconditions.checkNonnegative(var2, "distance");
         return var1.add(BigInteger.valueOf(var2));
      }

      public long distance(BigInteger var1, BigInteger var2) {
         return var2.subtract(var1).max(MIN_LONG).min(MAX_LONG).longValue();
      }

      private Object readResolve() {
         return INSTANCE;
      }

      @Override
      public String toString() {
         return "DiscreteDomain.bigIntegers()";
      }
   }

   private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
      private static final DiscreteDomain.IntegerDomain INSTANCE = new DiscreteDomain.IntegerDomain();
      private static final long serialVersionUID = 0L;

      IntegerDomain() {
         super(true);
      }

      public Integer next(Integer var1) {
         int var2 = var1;
         return var2 == Integer.MAX_VALUE ? null : var2 + 1;
      }

      public Integer previous(Integer var1) {
         int var2 = var1;
         return var2 == Integer.MIN_VALUE ? null : var2 - 1;
      }

      Integer offset(Integer var1, long var2) {
         CollectPreconditions.checkNonnegative(var2, "distance");
         return Ints.checkedCast(var1.longValue() + var2);
      }

      public long distance(Integer var1, Integer var2) {
         return (long)var2.intValue() - (long)var1.intValue();
      }

      public Integer minValue() {
         return Integer.MIN_VALUE;
      }

      public Integer maxValue() {
         return Integer.MAX_VALUE;
      }

      private Object readResolve() {
         return INSTANCE;
      }

      @Override
      public String toString() {
         return "DiscreteDomain.integers()";
      }
   }

   private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
      private static final DiscreteDomain.LongDomain INSTANCE = new DiscreteDomain.LongDomain();
      private static final long serialVersionUID = 0L;

      LongDomain() {
         super(true);
      }

      public Long next(Long var1) {
         long var2 = var1;
         return var2 == Long.MAX_VALUE ? null : var2 + 1L;
      }

      public Long previous(Long var1) {
         long var2 = var1;
         return var2 == Long.MIN_VALUE ? null : var2 - 1L;
      }

      Long offset(Long var1, long var2) {
         CollectPreconditions.checkNonnegative(var2, "distance");
         long var4 = var1 + var2;
         if (var4 < 0L) {
            Preconditions.checkArgument(var1 < 0L, "overflow");
         }

         return var4;
      }

      public long distance(Long var1, Long var2) {
         long var3 = var2 - var1;
         if (var2 > var1 && var3 < 0L) {
            return Long.MAX_VALUE;
         } else {
            return var2 < var1 && var3 > 0L ? Long.MIN_VALUE : var3;
         }
      }

      public Long minValue() {
         return Long.MIN_VALUE;
      }

      public Long maxValue() {
         return Long.MAX_VALUE;
      }

      private Object readResolve() {
         return INSTANCE;
      }

      @Override
      public String toString() {
         return "DiscreteDomain.longs()";
      }
   }
}
