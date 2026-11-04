package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class BaseLinkedAtomicQueueProducerNodeRef<E> extends BaseLinkedAtomicQueuePad0<E> {
   private static final AtomicReferenceFieldUpdater<BaseLinkedAtomicQueueProducerNodeRef, LinkedQueueAtomicNode> P_NODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      BaseLinkedAtomicQueueProducerNodeRef.class, LinkedQueueAtomicNode.class, "producerNode"
   );
   private volatile LinkedQueueAtomicNode<E> producerNode;

   final void spProducerNode(LinkedQueueAtomicNode<E> var1) {
      P_NODE_UPDATER.lazySet(this, var1);
   }

   final void soProducerNode(LinkedQueueAtomicNode<E> var1) {
      P_NODE_UPDATER.lazySet(this, var1);
   }

   final LinkedQueueAtomicNode<E> lvProducerNode() {
      return this.producerNode;
   }

   final boolean casProducerNode(LinkedQueueAtomicNode<E> var1, LinkedQueueAtomicNode<E> var2) {
      return P_NODE_UPDATER.compareAndSet(this, var1, var2);
   }

   final LinkedQueueAtomicNode<E> lpProducerNode() {
      return this.producerNode;
   }

   protected final LinkedQueueAtomicNode<E> xchgProducerNode(LinkedQueueAtomicNode<E> var1) {
      return P_NODE_UPDATER.getAndSet(this, var1);
   }
}
