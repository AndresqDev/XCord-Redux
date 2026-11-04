package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseMpscLinkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueuePad3<E> {
   private static final long P_LIMIT_OFFSET = UnsafeAccess.fieldOffset(BaseMpscLinkedArrayQueueColdProducerFields.class, "producerLimit");
   private volatile long producerLimit;
   protected long producerMask;
   protected E[] producerBuffer;

   final long lvProducerLimit() {
      return this.producerLimit;
   }

   final boolean casProducerLimit(long var1, long var3) {
      return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, var1, var3);
   }

   final void soProducerLimit(long var1) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, var1);
   }
}
