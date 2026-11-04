package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E> extends BaseLinkedAtomicQueuePad2<E> {
   @Override
   public final Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   protected final LinkedQueueAtomicNode<E> newNode() {
      return new LinkedQueueAtomicNode<>();
   }

   protected final LinkedQueueAtomicNode<E> newNode(E var1) {
      return new LinkedQueueAtomicNode<>((E)var1);
   }

   @Override
   public final int size() {
      LinkedQueueAtomicNode var1 = this.lvConsumerNode();
      LinkedQueueAtomicNode var2 = this.lvProducerNode();

      int var3;
      for (var3 = 0; var1 != var2 && var1 != null && var3 < Integer.MAX_VALUE; var3++) {
         LinkedQueueAtomicNode var4 = var1.lvNext();
         if (var4 == var1) {
            return var3;
         }

         var1 = var4;
      }

      return var3;
   }

   @Override
   public boolean isEmpty() {
      LinkedQueueAtomicNode var1 = this.lvConsumerNode();
      LinkedQueueAtomicNode var2 = this.lvProducerNode();
      return var1 == var2;
   }

   protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> var1, LinkedQueueAtomicNode<E> var2) {
      Object var3 = var2.getAndNullValue();
      var1.soNext(var1);
      this.spConsumerNode(var2);
      return (E)var3;
   }

   @Override
   public E poll() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode();
      LinkedQueueAtomicNode var2 = var1.lvNext();
      if (var2 != null) {
         return this.getSingleConsumerNodeValue(var1, var2);
      } else if (var1 != this.lvProducerNode()) {
         var2 = this.spinWaitForNextNode(var1);
         return this.getSingleConsumerNodeValue(var1, var2);
      } else {
         return null;
      }
   }

   @Override
   public E peek() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode();
      LinkedQueueAtomicNode var2 = var1.lvNext();
      if (var2 != null) {
         return (E)var2.lpValue();
      } else if (var1 != this.lvProducerNode()) {
         var2 = this.spinWaitForNextNode(var1);
         return (E)var2.lpValue();
      } else {
         return null;
      }
   }

   LinkedQueueAtomicNode<E> spinWaitForNextNode(LinkedQueueAtomicNode<E> var1) {
      LinkedQueueAtomicNode var2;
      while ((var2 = var1.lvNext()) == null) {
      }

      return var2;
   }

   @Override
   public E relaxedPoll() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode();
      LinkedQueueAtomicNode var2 = var1.lvNext();
      return var2 != null ? this.getSingleConsumerNodeValue(var1, var2) : null;
   }

   @Override
   public E relaxedPeek() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode().lvNext();
      return (E)(var1 != null ? var1.lpValue() : null);
   }

   @Override
   public boolean relaxedOffer(E var1) {
      return this.offer((E)var1);
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      if (null == var1) {
         throw new IllegalArgumentException("c is null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("limit is negative: " + var2);
      } else if (var2 == 0) {
         return 0;
      } else {
         LinkedQueueAtomicNode var3 = this.lpConsumerNode();

         for (int var4 = 0; var4 < var2; var4++) {
            LinkedQueueAtomicNode var5 = var3.lvNext();
            if (var5 == null) {
               return var4;
            }

            Object var6 = this.getSingleConsumerNodeValue(var3, var5);
            var3 = var5;
            var1.accept(var6);
         }

         return var2;
      }
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return MessagePassingQueueUtil.drain(this, var1);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.drain(this, var1, var2, var3);
   }

   @Override
   public int capacity() {
      return -1;
   }
}
