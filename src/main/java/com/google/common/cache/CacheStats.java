package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class CacheStats {
   private final long hitCount;
   private final long missCount;
   private final long loadSuccessCount;
   private final long loadExceptionCount;
   private final long totalLoadTime;
   private final long evictionCount;

   public CacheStats(long var1, long var3, long var5, long var7, long var9, long var11) {
      Preconditions.checkArgument(var1 >= 0L);
      Preconditions.checkArgument(var3 >= 0L);
      Preconditions.checkArgument(var5 >= 0L);
      Preconditions.checkArgument(var7 >= 0L);
      Preconditions.checkArgument(var9 >= 0L);
      Preconditions.checkArgument(var11 >= 0L);
      this.hitCount = var1;
      this.missCount = var3;
      this.loadSuccessCount = var5;
      this.loadExceptionCount = var7;
      this.totalLoadTime = var9;
      this.evictionCount = var11;
   }

   public long requestCount() {
      return LongMath.saturatedAdd(this.hitCount, this.missCount);
   }

   public long hitCount() {
      return this.hitCount;
   }

   public double hitRate() {
      long var1 = this.requestCount();
      return var1 == 0L ? 1.0 : (double)this.hitCount / (double)var1;
   }

   public long missCount() {
      return this.missCount;
   }

   public double missRate() {
      long var1 = this.requestCount();
      return var1 == 0L ? 0.0 : (double)this.missCount / (double)var1;
   }

   public long loadCount() {
      return LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
   }

   public long loadSuccessCount() {
      return this.loadSuccessCount;
   }

   public long loadExceptionCount() {
      return this.loadExceptionCount;
   }

   public double loadExceptionRate() {
      long var1 = LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
      return var1 == 0L ? 0.0 : (double)this.loadExceptionCount / (double)var1;
   }

   public long totalLoadTime() {
      return this.totalLoadTime;
   }

   public double averageLoadPenalty() {
      long var1 = LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
      return var1 == 0L ? 0.0 : (double)this.totalLoadTime / (double)var1;
   }

   public long evictionCount() {
      return this.evictionCount;
   }

   public CacheStats minus(CacheStats var1) {
      return new CacheStats(
         Math.max(0L, LongMath.saturatedSubtract(this.hitCount, var1.hitCount)),
         Math.max(0L, LongMath.saturatedSubtract(this.missCount, var1.missCount)),
         Math.max(0L, LongMath.saturatedSubtract(this.loadSuccessCount, var1.loadSuccessCount)),
         Math.max(0L, LongMath.saturatedSubtract(this.loadExceptionCount, var1.loadExceptionCount)),
         Math.max(0L, LongMath.saturatedSubtract(this.totalLoadTime, var1.totalLoadTime)),
         Math.max(0L, LongMath.saturatedSubtract(this.evictionCount, var1.evictionCount))
      );
   }

   public CacheStats plus(CacheStats var1) {
      return new CacheStats(
         LongMath.saturatedAdd(this.hitCount, var1.hitCount),
         LongMath.saturatedAdd(this.missCount, var1.missCount),
         LongMath.saturatedAdd(this.loadSuccessCount, var1.loadSuccessCount),
         LongMath.saturatedAdd(this.loadExceptionCount, var1.loadExceptionCount),
         LongMath.saturatedAdd(this.totalLoadTime, var1.totalLoadTime),
         LongMath.saturatedAdd(this.evictionCount, var1.evictionCount)
      );
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.hitCount, this.missCount, this.loadSuccessCount, this.loadExceptionCount, this.totalLoadTime, this.evictionCount);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof CacheStats)) {
         return false;
      } else {
         CacheStats var2 = (CacheStats)var1;
         return this.hitCount == var2.hitCount
            && this.missCount == var2.missCount
            && this.loadSuccessCount == var2.loadSuccessCount
            && this.loadExceptionCount == var2.loadExceptionCount
            && this.totalLoadTime == var2.totalLoadTime
            && this.evictionCount == var2.evictionCount;
      }
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("hitCount", this.hitCount)
         .add("missCount", this.missCount)
         .add("loadSuccessCount", this.loadSuccessCount)
         .add("loadExceptionCount", this.loadExceptionCount)
         .add("totalLoadTime", this.totalLoadTime)
         .add("evictionCount", this.evictionCount)
         .toString();
   }
}
