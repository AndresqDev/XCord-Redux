package io.netty.util.internal.shaded.org.jctools.queues;

public class SpscLinkedQueue<E> extends BaseLinkedQueue<E> {
   public SpscLinkedQueue() {
      LinkedQueueNode var1 = this.newNode();
      this.spProducerNode(var1);
      this.spConsumerNode(var1);
      var1.soNext(null);
   }

   @Override
   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         LinkedQueueNode var2 = this.newNode((E)var1);
         LinkedQueueNode var3 = this.lpProducerNode();
         this.soProducerNode(var2);
         var3.soNext(var2);
         return true;
      }
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> var1) {
      return MessagePassingQueueUtil.fillUnbounded(this, var1);
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      if (null == var1) {
         throw new IllegalArgumentException("supplier is null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("limit is negative:" + var2);
      } else if (var2 == 0) {
         return 0;
      } else {
         LinkedQueueNode var3 = this.newNode((E)var1.get());
         LinkedQueueNode var4 = var3;

         for (int var5 = 1; var5 < var2; var5++) {
            LinkedQueueNode var6 = this.newNode((E)var1.get());
            var3.spNext(var6);
            var3 = var6;
         }

         LinkedQueueNode var7 = this.lpProducerNode();
         this.soProducerNode(var3);
         var7.soNext(var4);
         return var2;
      }
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.fill(this, var1, var2, var3);
   }
}
