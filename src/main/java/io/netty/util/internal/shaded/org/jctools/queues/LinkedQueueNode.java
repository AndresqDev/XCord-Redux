package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

final class LinkedQueueNode<E> {
   private static final long NEXT_OFFSET = UnsafeAccess.fieldOffset(LinkedQueueNode.class, "next");
   private E value;
   private volatile LinkedQueueNode<E> next;

   LinkedQueueNode() {
      this(null);
   }

   LinkedQueueNode(E var1) {
      this.spValue((E)var1);
   }

   public E getAndNullValue() {
      Object var1 = this.lpValue();
      this.spValue(null);
      return (E)var1;
   }

   public E lpValue() {
      return this.value;
   }

   public void spValue(E var1) {
      this.value = (E)var1;
   }

   public void soNext(LinkedQueueNode<E> var1) {
      UnsafeAccess.UNSAFE.putOrderedObject(this, NEXT_OFFSET, var1);
   }

   public void spNext(LinkedQueueNode<E> var1) {
      UnsafeAccess.UNSAFE.putObject(this, NEXT_OFFSET, var1);
   }

   public LinkedQueueNode<E> lvNext() {
      return this.next;
   }
}
