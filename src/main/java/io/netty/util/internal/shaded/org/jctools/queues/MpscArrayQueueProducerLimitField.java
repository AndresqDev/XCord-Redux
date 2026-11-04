package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscArrayQueueProducerLimitField<E> extends MpscArrayQueueMidPad<E> {
   private static final long P_LIMIT_OFFSET = UnsafeAccess.fieldOffset(MpscArrayQueueProducerLimitField.class, "producerLimit");
   private volatile long producerLimit;

   MpscArrayQueueProducerLimitField(int var1) {
      super(var1);
      this.producerLimit = (long)var1;
   }

   final long lvProducerLimit() {
      return this.producerLimit;
   }

   final void soProducerLimit(long var1) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, var1);
   }
}
