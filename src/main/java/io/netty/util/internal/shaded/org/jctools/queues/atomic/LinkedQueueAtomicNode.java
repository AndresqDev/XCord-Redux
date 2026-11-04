package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReference;

public final class LinkedQueueAtomicNode<E> extends AtomicReference<LinkedQueueAtomicNode<E>> {
   private static final long serialVersionUID = 2404266111789071508L;
   private E value;

   LinkedQueueAtomicNode() {
   }

   LinkedQueueAtomicNode(E var1) {
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

   public void soNext(LinkedQueueAtomicNode<E> var1) {
      this.lazySet(var1);
   }

   public void spNext(LinkedQueueAtomicNode<E> var1) {
      this.lazySet(var1);
   }

   public LinkedQueueAtomicNode<E> lvNext() {
      return this.get();
   }
}
