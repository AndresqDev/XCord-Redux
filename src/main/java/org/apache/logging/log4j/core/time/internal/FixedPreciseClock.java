package org.apache.logging.log4j.core.time.internal;

import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.time.PreciseClock;

public class FixedPreciseClock implements PreciseClock {
   private final long currentTimeMillis;
   private final int nanosOfMillisecond;

   public FixedPreciseClock() {
      this(0L);
   }

   public FixedPreciseClock(long var1) {
      this(var1, 0);
   }

   public FixedPreciseClock(long var1, int var3) {
      this.currentTimeMillis = var1;
      this.nanosOfMillisecond = var3;
   }

   @Override
   public void init(MutableInstant var1) {
      var1.initFromEpochMilli(this.currentTimeMillis, this.nanosOfMillisecond);
   }

   @Override
   public long currentTimeMillis() {
      return this.currentTimeMillis;
   }
}
