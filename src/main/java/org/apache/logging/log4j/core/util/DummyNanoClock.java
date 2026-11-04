package org.apache.logging.log4j.core.util;

public final class DummyNanoClock implements NanoClock {
   private final long fixedNanoTime;

   public DummyNanoClock() {
      this(0L);
   }

   public DummyNanoClock(long var1) {
      this.fixedNanoTime = var1;
   }

   @Override
   public long nanoTime() {
      return this.fixedNanoTime;
   }
}
