package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Queue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class ConsumingQueueIterator<T> extends AbstractIterator<T> {
   private final Queue<T> queue;

   ConsumingQueueIterator(Queue<T> var1) {
      this.queue = Preconditions.checkNotNull(var1);
   }

   @Override
   public T computeNext() {
      return this.queue.isEmpty() ? this.endOfData() : this.queue.remove();
   }
}
