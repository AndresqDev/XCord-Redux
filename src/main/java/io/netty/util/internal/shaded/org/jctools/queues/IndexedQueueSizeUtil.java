package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
   public static int size(IndexedQueueSizeUtil.IndexedQueue var0) {
      long var1 = var0.lvConsumerIndex();

      long var5;
      long var7;
      do {
         var5 = var1;
         var7 = var0.lvProducerIndex();
         var1 = var0.lvConsumerIndex();
      } while (var5 != var1);

      long var3 = var7 - var1;
      if (var3 > 2147483647L) {
         return Integer.MAX_VALUE;
      } else if (var3 < 0L) {
         return 0;
      } else {
         return var0.capacity() != -1 && var3 > (long)var0.capacity() ? var0.capacity() : (int)var3;
      }
   }

   public static boolean isEmpty(IndexedQueueSizeUtil.IndexedQueue var0) {
      return var0.lvConsumerIndex() >= var0.lvProducerIndex();
   }

   public interface IndexedQueue {
      long lvConsumerIndex();

      long lvProducerIndex();

      int capacity();
   }
}
